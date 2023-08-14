package com.appsferry.user.facebook.handlers;

import static com.appsferry.login.api.Constants.LOGIN_TYPE_FACEBOOK;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
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
import com.appsferry.user.facebook.FacebookManager;
import com.appsferry.user.facebook.activity.LinkAuthFaceBookFragment;
import com.appsferry.user.facebook.activity.common.FacebookConstants;
import com.appsferry.user.facebook.activity.model.FacebookAuthResult;
import com.appsferry.user.facebook.activity.model.FacebookLinkModel;
import com.appsferry.user.facebook.error.FacebookErrorCode;
import com.appsferry.user.facebook.listener.FacebookAuthListener;
import com.appsferry.user.facebook.listener.FacebookBindListener;
import com.appsferry.user.facebook.listener.FacebookCheckUnbindListener;
import com.appsferry.user.facebook.listener.FacebookLoginListener;
import com.appsferry.user.facebook.listener.FacebookUnBindListener;
import com.appsferry.user.facebook.utils.MainThreadUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FacebookAuthHandler {

    private static final FacebookAuthHandler INSTANCE = new FacebookAuthHandler();
    private FacebookAuthListener mFacebookAuthListener;
    private LinkAuthFaceBookFragment mLinkAuthFaceBookFragment;
    private FragmentManager mFragmentManager;
    private final Map<String, Object> mRequestIdMap = new HashMap<>();

    private FacebookAuthHandler() {

    }

    public static FacebookAuthHandler getInstance() {
        return INSTANCE;
    }

    public <T extends UserModel<? extends UserProfile>> void loginWithFacebook(@NonNull Activity activity, List<String> permissions, @NonNull FacebookLoginListener<T> listener) {
        Log.i(FacebookManager.TAG, "facebook -> loginWithFacebook");
        MainThreadUtils.runOnUiThread(() -> {
            authLinkFacebook(activity, permissions, new FacebookAuthListener() {
                @Override
                public void onSuccess(String token) {
                    loginByThirdParty(token, listener);
                }

                @Override
                public void onFailed(int errorCode, String errorMsg, String fbMsg) {
                    listener.onFailed(errorCode, errorMsg);
                }
            });
        });

    }

    public void bindWithFacebook(@NonNull Activity activity, List<String> permissions, @NonNull FacebookBindListener listener) {
        Log.i(FacebookManager.TAG, "facebook -> bindWithFacebook");
        authLinkFacebook(activity, permissions, new FacebookAuthListener() {
            @Override
            public void onSuccess(String token) {
                bindByThirdParty(token, listener);
            }

            @Override
            public void onFailed(int errorCode, String errorMsg, String fbMsg) {
                listener.onFailed(errorCode, errorMsg);
            }
        });
    }

    public void unBindWithFacebook(@NonNull FacebookUnBindListener listener) {
        Log.i(FacebookManager.TAG, "facebook -> unBindWithFacebook");
        UserSDK.getInstance().getAccountService().unBindAccount(LOGIN_TYPE_FACEBOOK, new ThirdPartUnBindListener() {
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

    public void checkUnbind(@NonNull FacebookCheckUnbindListener listener) {
        Log.i(FacebookManager.TAG, "facebook -> checkUnbind");
    }

    private void authLinkFacebook(@NonNull Activity activity, List<String> permissions, FacebookAuthListener listener) {
        this.mFacebookAuthListener = listener;
        mFragmentManager = activity.getFragmentManager();
        mLinkAuthFaceBookFragment = new LinkAuthFaceBookFragment();
        if (permissions != null && permissions.size() > 0) {
            FacebookLinkModel linkModel = new FacebookLinkModel(permissions);
            Bundle bundle = new Bundle();
            bundle.putParcelable(FacebookConstants.EXTRA_REQUEST, linkModel);
            mLinkAuthFaceBookFragment.setArguments(bundle);
        }
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.add(mLinkAuthFaceBookFragment, FacebookConstants.FRAGMENT_TAG);
        fragmentTransaction.commit();

    }

    private <T extends UserModel<? extends UserProfile>> void loginByThirdParty(String accessToken, FacebookLoginListener<T> listener) {
        Log.i(FacebookManager.TAG, "facebook -> loginByThirdParty");
        UserSDK.getInstance().getLoginService().loginByThirdparty(Constants.LOGIN_TYPE_FACEBOOK, accessToken, null, new ThirdPartLoginListener<T>() {
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

    private void bindByThirdParty(String accessToken, FacebookBindListener listener) {
        Log.i(FacebookManager.TAG, "facebook -> loginByThirdParty");
        UserSDK.getInstance().getAccountService().bindAccount(LOGIN_TYPE_FACEBOOK, accessToken, new ThirdPartBindListener() {
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

    public synchronized void registerFbResp(@NonNull FacebookAuthResult result) {
        if (result.getCode() == FacebookErrorCode.FACEBOOK_AUTH_SUCCESS_CODE) {
            if (mFacebookAuthListener != null) {
                mFacebookAuthListener.onSuccess(result.getToken());
            }
        } else {
            if (mFacebookAuthListener != null) {
                mFacebookAuthListener.onFailed(result.getCode(), result.getMsg(), result.getFbMsg());
            }
        }
        if (mFragmentManager != null && mLinkAuthFaceBookFragment != null) {
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.remove(mLinkAuthFaceBookFragment);
            fragmentTransaction.commit();

        }
        mFacebookAuthListener = null;
    }

}
