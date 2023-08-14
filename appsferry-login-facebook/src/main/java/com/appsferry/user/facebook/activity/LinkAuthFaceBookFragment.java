package com.appsferry.user.facebook.activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.appsferry.user.facebook.FacebookManager;
import com.appsferry.user.facebook.activity.common.FacebookConstants;
import com.appsferry.user.facebook.activity.model.FacebookAuthResult;
import com.appsferry.user.facebook.activity.model.FacebookLinkModel;
import com.appsferry.user.facebook.error.FacebookErrorCode;
import com.appsferry.user.facebook.error.FacebookErrorMsg;
import com.appsferry.user.facebook.handlers.FacebookApiHandler;
import com.appsferry.user.facebook.handlers.FacebookAuthHandler;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;

import java.util.ArrayList;
import java.util.List;

public class LinkAuthFaceBookFragment extends Fragment implements FacebookCallback<LoginResult> {
    private CallbackManager callbackManager = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        FacebookApiHandler.getInstance().registerCallback(callbackManager, this);
        Bundle bundle = getArguments();
        List<String> permissions;
        if (bundle != null) {
            FacebookLinkModel request = bundle.getParcelable(FacebookConstants.EXTRA_REQUEST);
            permissions = request.getPermissions();
        } else {
            permissions = new ArrayList<>();
            permissions.add("public_profile");
            permissions.add("email");
            permissions.add("user_location");
            permissions.add("user_birthday");
            permissions.add("user_hometown");
            permissions.add("user_gender");
            permissions.add("user_photos");
            permissions.add("user_link");
            permissions.add("user_videos");
        }
        authWithFacebook(this, permissions);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        if (loginResult != null) {
            Log.i(FacebookManager.TAG, "onSuccess loginResult");
            String token = loginResult.getAccessToken().getToken();
            FacebookAuthResult facebookResult = new FacebookAuthResult();
            facebookResult.setCode(FacebookErrorCode.FACEBOOK_AUTH_SUCCESS_CODE);
            facebookResult.setToken(token);
            FacebookAuthHandler.getInstance().registerFbResp(facebookResult);
        }
    }

    @Override
    public void onCancel() {
        Log.i(FacebookManager.TAG, "onCancel");
        FacebookAuthResult facebookResult = new FacebookAuthResult();
        facebookResult.setCode(FacebookErrorCode.FACEBOOK_CANCEL_ERROR_CODE);
        facebookResult.setMsg(FacebookErrorMsg.authCancelMsg());
        facebookResult.setFbMsg("cancel auth");
        FacebookAuthHandler.getInstance().registerFbResp(facebookResult);
    }

    @Override
    public void onError(@NonNull FacebookException error) {
        Log.i(FacebookManager.TAG, "onError = " + error);
        FacebookAuthResult facebookResult = new FacebookAuthResult();
        facebookResult.setCode(FacebookErrorCode.FACEBOOK_AUTH_ERROR_CODE);
        facebookResult.setMsg(FacebookErrorMsg.authFailedMsg());
        facebookResult.setFbMsg(error.getMessage());
        FacebookAuthHandler.getInstance().registerFbResp(facebookResult);
    }

    private void authWithFacebook(Fragment fragment, List<String> permissions) {
        Log.i(FacebookManager.TAG, "authWithFacebook");
        AccessToken accessToken = FacebookApiHandler.getInstance().getCurrentAccessToken();
        if (accessToken != null && !accessToken.isExpired()) {
            FacebookApiHandler.getInstance().logout();
        }
        FacebookApiHandler.getInstance().setLoginBehavior();
        FacebookApiHandler.getInstance().login(fragment, permissions);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (callbackManager != null) {
            FacebookApiHandler.getInstance().unregisterCallback(callbackManager);
        }
    }
}
