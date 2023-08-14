package com.appsferry.user.tiktok.handlers;

import static com.appsferry.login.api.Constants.LOGIN_TYPE_TIKTOK;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.appsferry.login.api.Constants;
import com.appsferry.login.api.UserSDK;
import com.appsferry.login.entity.user.UserModel;
import com.appsferry.login.entity.user.UserProfile;
import com.appsferry.login.listener.base.SDKError;
import com.appsferry.login.listener.third.ThirdPartBindListener;
import com.appsferry.login.listener.third.ThirdPartLoginListener;
import com.appsferry.login.listener.third.ThirdPartUnBindListener;
import com.appsferry.user.tiktok.TikTokManager;
import com.appsferry.user.tiktok.error.TikTokErrorCode;
import com.appsferry.user.tiktok.error.TikTokErrorMsg;
import com.appsferry.user.tiktok.listener.TikTokAuthListener;
import com.appsferry.user.tiktok.listener.TikTokBindListener;
import com.appsferry.user.tiktok.listener.TikTokCheckUnbindListener;
import com.appsferry.user.tiktok.listener.TikTokLoginListener;
import com.appsferry.user.tiktok.listener.TikTokUnBindListener;
import com.appsferry.user.tiktok.util.TikTokAssert;
import com.tiktok.open.sdk.auth.AuthResponse;

public class TiktokAuthHandler {
    private static final TiktokAuthHandler INSTANCE = new TiktokAuthHandler();

    private TiktokAuthHandler() {
    }

    public static TiktokAuthHandler getInstance() {
        return INSTANCE;
    }

    private TiktokApiHandler tiktokApiHandler;
    private TikTokAuthListener mTokAuthListener;

    public TiktokApiHandler getTiktokApiHandler() {
        return tiktokApiHandler;
    }

    public void init(@NonNull String clientKey, String redirectUri) {
        Log.i(TikTokManager.TAG, "tiktok -> init");
        if (tiktokApiHandler == null) {
            tiktokApiHandler = new TiktokApiHandler();
        }
        tiktokApiHandler.init(clientKey, redirectUri);
    }

    public <T extends UserModel<? extends UserProfile>> void loginByTikTok(@NonNull Activity activity, @NonNull String scope, String state, @NonNull TikTokLoginListener<T> listener) {
        Log.i(TikTokManager.TAG, "tiktok -> loginByTikTok");
        TikTokAssert.assertion(tiktokApiHandler);
        authorize(activity, scope, state, new TikTokAuthListener() {
            @Override
            public void onSuccess(String authCode) {
                loginByTiktokToService(authCode, listener);
            }

            @Override
            public void onFailed(int errorCode, String errorMsg) {
                listener.onFailed(errorCode, errorMsg);
            }
        });


    }

    public void bindWithTikTok(@NonNull Activity activity, @NonNull String scope, String state, @NonNull TikTokBindListener listener) {
        Log.i(TikTokManager.TAG, "tiktok -> bindWithTikTok");
        TikTokAssert.assertion(tiktokApiHandler);
        authorize(activity, scope, state, new TikTokAuthListener() {
            @Override
            public void onSuccess(String authCode) {
                bindByThirdParty(authCode, listener);
            }

            @Override
            public void onFailed(int errorCode, String errorMsg) {
                listener.onFailed(errorCode, errorMsg);
            }
        });
    }

    public void unbindWithTikTok(@NonNull TikTokUnBindListener listener) {
        Log.i(TikTokManager.TAG, "tiktok -> unBindWithQQ");
        UserSDK.getInstance().getAccountService().unBindAccount(LOGIN_TYPE_TIKTOK, new ThirdPartUnBindListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onError(SDKError error) {
                listener.onFailed(error.errorCode, error.errorMessage);
            }
        });
    }

    public void canUnbindTiktok(@NonNull TikTokCheckUnbindListener listener) {
        Log.i(TikTokManager.TAG, "tiktok -> checkUnbind");
    }

    private <T extends UserModel<? extends UserProfile>> void loginByTiktokToService(String authCode, TikTokLoginListener<T> listener) {
        Log.i(TikTokManager.TAG, "tiktok -> loginByTiktok");
        UserSDK.getInstance().getLoginService().loginByThirdparty(Constants.LOGIN_TYPE_TIKTOK, authCode, null, new ThirdPartLoginListener<T>() {
            @Override
            public void onNewData(T entity) {
                if (listener != null) {
                    listener.onSuccess(entity);
                }
            }

            @Override
            public void onError(SDKError error) {
                if (listener != null) {
                    listener.onFailed(error.errorCode, error.errorMessage);
                }
            }
        });
    }

    private void bindByThirdParty(String authCode, TikTokBindListener listener) {
        Log.i(TikTokManager.TAG, "tiktok -> bindByThirdParty");
        UserSDK.getInstance().getAccountService().bindAccount(LOGIN_TYPE_TIKTOK, authCode, new ThirdPartBindListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onError(SDKError error) {
                listener.onFailed(error.errorCode, error.errorMessage);
            }
        });

    }

    public boolean authorize(@NonNull Activity activity, @NonNull String scope, String state, @NonNull TikTokAuthListener listener) {
        Log.i(TikTokManager.TAG, "tiktok -> authorize");
        TikTokAssert.assertion(tiktokApiHandler);
        mTokAuthListener = listener;
        if (tiktokApiHandler != null) {
            return tiktokApiHandler.authorize(activity, scope, state);
        }
        return false;
    }

    public boolean isAppInstalled(@NonNull Activity activity) {
        Log.i(TikTokManager.TAG, "tiktok -> isAppInstalled");
        TikTokAssert.assertion(tiktokApiHandler);
        if (tiktokApiHandler != null) {
            return tiktokApiHandler.isAppInstalled(activity);
        }
        return false;
    }

    public void dispatchResponse(AuthResponse response) {
        if (mTokAuthListener != null) {
            if (response.getErrorCode() == 0) {
                mTokAuthListener.onSuccess(response.getAuthCode());
            } else {
                mTokAuthListener.onFailed(TikTokErrorCode.TIKTOK_AUTH_ERROR_CODE, TikTokErrorMsg.authFailedMsg());
            }
        }
    }

}
