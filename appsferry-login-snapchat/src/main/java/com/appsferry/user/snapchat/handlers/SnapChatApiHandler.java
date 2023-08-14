package com.appsferry.user.snapchat.handlers;

import android.content.Context;
import android.util.Log;

import com.appsferry.user.snapchat.SnapchatManager;
import com.snap.corekit.SnapKit;
import com.snap.loginkit.LoginStateCallback;
import com.snap.loginkit.SnapLogin;
import com.snap.loginkit.SnapLoginProvider;
import com.snap.loginkit.UserDataQuery;
import com.snap.loginkit.UserDataResultCallback;

import org.jetbrains.annotations.NotNull;

public class SnapChatApiHandler {
    private static final SnapChatApiHandler INSTANCE = new SnapChatApiHandler();

    private SnapChatApiHandler() {
    }

    static SnapChatApiHandler getInstance() {
        return INSTANCE;
    }

    private SnapLogin snapLogin;

    public void init(Context context) {
        SnapKit.initSDK(context);
        snapLogin = SnapLoginProvider.get(context);
    }

    public boolean isUserLoggedIn(@NotNull Context context) {
        Log.d(SnapchatManager.TAG, "isUserLoggedIn");
        return snapLogin.isUserLoggedIn();
    }

    public void startTokenGrant(@NotNull Context context) {
        Log.d(SnapchatManager.TAG, "startTokenGrant");
        snapLogin.startTokenGrant();
    }

    public void addOnLoginStateChangedListener(@NotNull Context context, @NotNull LoginStateCallback listener) {
        Log.d(SnapchatManager.TAG, "addOnLoginStateChangedListener");
        snapLogin.addLoginStateCallback(listener);
    }

    public void removeOnLoginStateChangedListener(@NotNull Context context, @NotNull LoginStateCallback listener) {
        Log.d(SnapchatManager.TAG, "removeOnLoginStateChangedListener");
        snapLogin.removeLoginStateCallback(listener);
    }

    public void fetchUserData(@NotNull Context context, @NotNull UserDataQuery query, @NotNull UserDataResultCallback callback) {
        Log.d(SnapchatManager.TAG, "fetchUserData");
        snapLogin.fetchUserData(query, callback);
    }

    public void clearToken(@NotNull Context context) {
        Log.d(SnapchatManager.TAG, "clearToken");
        snapLogin.clearToken();
    }


}
