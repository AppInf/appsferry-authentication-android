package com.appsferry.user.snapchat.handlers

import android.content.Context
import android.content.pm.PackageManager
import android.text.TextUtils
import android.util.Log
import com.appsferry.core.gson.AfGson.Companion.get
import com.appsferry.login.api.Constants
import com.appsferry.login.api.UserSDK
import com.appsferry.login.entity.user.UserModel
import com.appsferry.login.entity.user.UserProfile
import com.appsferry.login.listener.base.SDKError
import com.appsferry.login.listener.third.ThirdPartBindListener
import com.appsferry.login.listener.third.ThirdPartLoginListener
import com.appsferry.login.utils.EncryptUitls
import com.appsferry.user.snapchat.SnapchatManager
import com.appsferry.user.snapchat.error.SnapChatErrorCode
import com.appsferry.user.snapchat.error.SnapChatErrorMsg
import com.appsferry.user.snapchat.listener.*
import com.appsferry.user.snapchat.util.SnapChatConstants
import com.snap.loginkit.BitmojiQuery
import com.snap.loginkit.LoginStateCallback
import com.snap.loginkit.UserDataQuery
import com.snap.loginkit.UserDataResultCallback
import com.snap.loginkit.exceptions.LoginException
import com.snap.loginkit.exceptions.UserDataException
import com.snap.loginkit.models.MeData
import com.snap.loginkit.models.UserDataResult


class SnapChatAuthHandler private constructor() {
    private lateinit var mContext: Context
    private var mSnapChatAuthListener: SnapChatAuthListener? = null
    private lateinit var onLoginStateChangedListener: LoginStateCallback
    fun init(context: Context) {
        Log.i(SnapchatManager.TAG, "snapchat -> init")
        mContext = context
        SnapChatApiHandler.getInstance().init(context)
        onLoginStateChangedListener = object : LoginStateCallback {
            override fun onStart() {
            }

            override fun onSuccess(p0: String) {
                Log.d(SnapchatManager.TAG, "snapchat -> onLoginSucceeded")
                fetchUserData(mContext)
            }

            override fun onFailure(p0: LoginException) {
                Log.e(SnapchatManager.TAG, "snapchat -> onLoginFailed")
                synchronized(this@SnapChatAuthHandler) {
                    if (mSnapChatAuthListener != null) {
                        mSnapChatAuthListener!!.onFailed(
                            SnapChatErrorCode.SNAPCHAT_AUTH_ERROR_CODE,
                            SnapChatErrorMsg.authFailedMsg()
                        )
                        mSnapChatAuthListener = null
                    }
                }
            }

            override fun onLogout() {
                Log.e(SnapchatManager.TAG, "snapchat -> onLogout")
            }
        }
        SnapChatApiHandler.getInstance()
            .addOnLoginStateChangedListener(mContext, onLoginStateChangedListener)
    }

    val isSnapUserLoggedIn: Boolean
        get() {
            Log.i(SnapchatManager.TAG, "snapchat -> isSnapUserLoggedIn")
            return SnapChatApiHandler.getInstance().isUserLoggedIn(mContext)
        }

    fun authBySnapchat(listener: SnapChatAuthListener?) {
        Log.i(SnapchatManager.TAG, "snapchat -> authBySnapchat")
        mSnapChatAuthListener = listener
        val isUserLoggedIn = SnapChatApiHandler.getInstance().isUserLoggedIn(mContext)
        if (isUserLoggedIn) {
            SnapChatApiHandler.getInstance().clearToken(mContext)
        }
        SnapChatApiHandler.getInstance().startTokenGrant(mContext)
    }

    fun <T : UserModel<out UserProfile?>?> loginBySnapchat(listener: SnapChatLoginListener<T>) {
        Log.i(SnapchatManager.TAG, "snapchat -> loginBySnapchat")
        authBySnapchat(object : SnapChatAuthListener {
            override fun onSuccess(meData: MeData) {
                Log.i(SnapchatManager.TAG, "snapchat -> auth success")
                val encryptExternalId = getClientId(mContext, meData)
                val params = HashMap<String, String>()
                meData.displayName?.let { params.put("nick_name", it) }
                meData.bitmojiData?.avatarId?.let { params.put("portrait", it) }
                UserSDK.getInstance().loginService.loginByThirdparty(
                    Constants.LOGIN_TYPE_SNAPCHAT,
                    encryptExternalId,
                    params,
                    object : ThirdPartLoginListener<T> {
                        override fun onNewData(entity: T) {
                            if (listener != null) {
                                listener.onSuccess(entity)
                            }
                        }

                        override fun onError(error: SDKError) {
                            if (listener != null) {
                                listener.onFailed(error.errorCode, error.errorMessage)
                            }
                        }
                    })
            }

            override fun onFailed(errorCode: Int, errorMsg: String) {
                Log.i(SnapchatManager.TAG, "snapchat -> auth failed: " + errorMsg)
                listener.onFailed(errorCode, errorMsg)
            }
        })
    }

    private fun fetchUserData(context: Context?) {
        Log.d(SnapchatManager.TAG, "snapchat -> fetchUserData")
        val bitmojiQuery = BitmojiQuery.newBuilder()
            .withAvatarId()
            .withTwoDAvatarUrl()
            .build()
        val userDataQuery = UserDataQuery.newBuilder()
            .withDisplayName()
            .withExternalId()
            .withIdToken()
            .withBitmoji(bitmojiQuery)
            .build()
        SnapChatApiHandler.getInstance()
            .fetchUserData(context!!, userDataQuery, object : UserDataResultCallback {
                override fun onSuccess(userDataResult: UserDataResult) {
                    Log.d(SnapchatManager.TAG, "userDataResult" + get().toJson(userDataResult))
                    val userData = userDataResult.data
                    if (userData != null) {
                        val meData = userData.meData
                        synchronized(this@SnapChatAuthHandler) {
                            if (mSnapChatAuthListener != null && meData != null) {
                                mSnapChatAuthListener!!.onSuccess(meData)
                                mSnapChatAuthListener = null
                            }
                        }
                    }
                }

                override fun onFailure(userDataResultError: UserDataException) {
                    Log.e(
                        SnapchatManager.TAG,
                        "snapchat user error " + userDataResultError.message
                    )
                    synchronized(this@SnapChatAuthHandler) {
                        if (mSnapChatAuthListener != null) {
                            mSnapChatAuthListener!!.onFailed(
                                SnapChatErrorCode.SNAPCHAT_USERDATA_ERROR_CODE,
                                SnapChatErrorMsg.getUserDataFailedMsg()
                            )
                            mSnapChatAuthListener = null
                        }
                    }
                }
            })
    }

    fun bindAccountBySnapchat(listener: SnapChatBindListener) {
        Log.i(SnapchatManager.TAG, "snapchat -> bindAccountBySnapchat")
        authBySnapchat(object : SnapChatAuthListener {
            override fun onSuccess(meData: MeData) {
                val encryptExternalId = getClientId(mContext, meData)
                UserSDK.getInstance().accountService.bindAccount(
                    Constants.LOGIN_TYPE_SNAPCHAT,
                    encryptExternalId,
                    object : ThirdPartBindListener {
                        override fun onSuccess() {
                            listener.onSuccess()
                        }

                        override fun onError(error: SDKError) {
                            listener.onFailed(error.errorCode, error.errorMessage)
                        }
                    })
            }

            override fun onFailed(errorCode: Int, errorMsg: String) {
                listener.onFailed(errorCode, errorMsg)
            }
        })
    }

    fun unBindAccountBySnapchat(listener: SnapChatUnbindListener?) {
        Log.i(SnapchatManager.TAG, "snapchat -> unBindAccountBySnapchat")
    }

    fun canUnbindSnapchat(listener: SnapchatCheckUnbindListener) {
        Log.i(SnapchatManager.TAG, "snapchat ->  checkUnbind")
    }

    private fun getClientId(context: Context?, meData: MeData): String {
        Log.i(SnapchatManager.TAG, "snapchat -> getClientId")
        var encryptExternalId = ""
        return try {
            val applicationInfo = context!!.packageManager.getApplicationInfo(
                context.packageName, PackageManager.GET_META_DATA
            )
            val bundle = applicationInfo.metaData
            val clientId = bundle.getString(SnapChatConstants.clientIdPkg)
            if (!TextUtils.isEmpty(clientId)) {
                val externalId = clientId + ":" + meData.externalId
                encryptExternalId = EncryptUitls.encrypt(externalId)
            }
            encryptExternalId
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(SnapchatManager.TAG, "not configuration snapchat clientId")
            e.printStackTrace()
            encryptExternalId
        }
    }

    companion object {

        val INSTANCE by lazy {
            SnapChatAuthHandler()
        }

        @JvmStatic
        fun getInstance(): SnapChatAuthHandler {
            return INSTANCE
        }
    }
}