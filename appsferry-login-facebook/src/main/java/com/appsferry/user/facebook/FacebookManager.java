package com.appsferry.user.facebook;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.appsferry.login.entity.user.UserModel;
import com.appsferry.login.entity.user.UserProfile;
import com.appsferry.user.facebook.handlers.FacebookAuthHandler;
import com.appsferry.user.facebook.listener.FacebookBindListener;
import com.appsferry.user.facebook.listener.FacebookCheckUnbindListener;
import com.appsferry.user.facebook.listener.FacebookLoginListener;
import com.appsferry.user.facebook.listener.FacebookUnBindListener;

import java.util.List;

public class FacebookManager {
    private static final FacebookManager INSTANCE = new FacebookManager();
    public static final String TAG = "FacebookManager";

    private FacebookManager() {
    }

    public static FacebookManager getInstance() {
        return INSTANCE;
    }

    public <T extends UserModel<? extends UserProfile>> void loginWithFacebook(@NonNull Activity activity, List<String> permissions, @NonNull FacebookLoginListener<T> listener) {
        FacebookAuthHandler.getInstance().loginWithFacebook(activity, permissions, listener);

    }

    public void bindWithFacebook(@NonNull Activity activity, List<String> permissions, @NonNull FacebookBindListener listener) {
        FacebookAuthHandler.getInstance().bindWithFacebook(activity, permissions, listener);
    }

    public void unBindWithFacebook(@NonNull FacebookUnBindListener listener) {
        FacebookAuthHandler.getInstance().unBindWithFacebook(listener);
    }

    public void checkUnbind(@NonNull FacebookCheckUnbindListener listener) {
        FacebookAuthHandler.getInstance().checkUnbind(listener);
    }
}
