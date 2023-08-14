package com.appsferry.user.facebook.handlers;

import android.app.Activity;
import android.app.Fragment;
import android.util.Log;

import com.appsferry.user.facebook.FacebookManager;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.List;

public class FacebookApiHandler {
    private static final FacebookApiHandler INSTANCE = new FacebookApiHandler();
    private LoginBehavior loginBehavior = LoginBehavior.NATIVE_WITH_FALLBACK;

    private FacebookApiHandler() {
    }

    public static FacebookApiHandler getInstance() {
        return INSTANCE;
    }

    public void logout() {
        Log.i(FacebookManager.TAG, "logout");
        LoginManager.getInstance().logOut();
    }

    public void login(Activity activity, List<String> permissions) {
        Log.i(FacebookManager.TAG, "login");
        LoginManager.getInstance().logIn(activity, permissions);
    }

    public void login(Fragment fragment, List<String> permissions) {
        Log.i(FacebookManager.TAG, "logInFragment");
        LoginManager.getInstance().logIn(fragment, permissions);
    }

    public void setLoginBehavior() {
        Log.i(FacebookManager.TAG, "setLoginBehavior");
        LoginManager.getInstance().setLoginBehavior(loginBehavior);
    }

    public void setCustomLoginBehavior(LoginBehavior loginBehavior) {
        Log.i(FacebookManager.TAG, "setLoginBehavior");
        this.loginBehavior = loginBehavior;
    }

    public void registerCallback(CallbackManager callbackManager, FacebookCallback<LoginResult> callback) {
        Log.i(FacebookManager.TAG, "registerCallback");
        LoginManager.getInstance().registerCallback(callbackManager, callback);
    }

    public void unregisterCallback(CallbackManager callbackManager) {
        Log.i(FacebookManager.TAG, "unregisterCallback");
        LoginManager.getInstance().unregisterCallback(callbackManager);
    }

    public AccessToken getCurrentAccessToken() {
        Log.i(FacebookManager.TAG, "getCurrentAccessToken");
        return AccessToken.getCurrentAccessToken();
    }

    public void expireCurrentAccessToken() {
        Log.i(FacebookManager.TAG, "expireCurrentAccessToken");
        AccessToken.expireCurrentAccessToken();
    }
}
