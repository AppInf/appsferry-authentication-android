package com.appsferry.user.snapchat;

import android.content.Context;

import androidx.annotation.NonNull;

import com.appsferry.login.entity.user.UserModel;
import com.appsferry.login.entity.user.UserProfile;
import com.appsferry.user.snapchat.handlers.SnapChatAuthHandler;
import com.appsferry.user.snapchat.listener.SnapChatAuthListener;
import com.appsferry.user.snapchat.listener.SnapChatBindListener;
import com.appsferry.user.snapchat.listener.SnapChatLoginListener;
import com.appsferry.user.snapchat.listener.SnapChatUnbindListener;
import com.appsferry.user.snapchat.listener.SnapchatCheckUnbindListener;

import org.jetbrains.annotations.NotNull;

public class SnapchatManager {
    private static final SnapchatManager INSTANCE = new SnapchatManager();
    public static final String TAG = "SnapchatManager";

    private SnapchatManager() {
    }

    public static SnapchatManager getInstance() {
        return INSTANCE;
    }

    public void init(@NotNull Context context) {
        SnapChatAuthHandler.getInstance().init(context);
    }

    public boolean isSnapUserLoggedIn() {
        return SnapChatAuthHandler.getInstance().isSnapUserLoggedIn();
    }

    public void authBySnapchat(@NotNull SnapChatAuthListener listener) {
        SnapChatAuthHandler.getInstance().authBySnapchat(listener);
    }

    public <T extends UserModel<? extends UserProfile>> void loginBySnapchat(@NotNull SnapChatLoginListener<T> listener) {
        SnapChatAuthHandler.getInstance().loginBySnapchat(listener);
    }

    public void bindAccountBySnapchat(@NotNull SnapChatBindListener listener) {
        SnapChatAuthHandler.getInstance().bindAccountBySnapchat(listener);
    }

    public void unBindAccountBySnapchat(@NotNull SnapChatUnbindListener listener) {
        SnapChatAuthHandler.getInstance().unBindAccountBySnapchat(listener);
    }

    public void canUnbindSnapchat(@NonNull SnapchatCheckUnbindListener listener) {
        SnapChatAuthHandler.getInstance().canUnbindSnapchat(listener);
    }


}
