package com.appsferry.user.google;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.appsferry.login.entity.user.UserModel;
import com.appsferry.login.entity.user.UserProfile;
import com.appsferry.user.google.handlers.GoogleAuthHandler;
import com.appsferry.user.google.listener.GoogleBindListener;
import com.appsferry.user.google.listener.GoogleCheckUnbindListener;
import com.appsferry.user.google.listener.GoogleLoginListener;
import com.appsferry.user.google.listener.GoogleSignInListener;
import com.appsferry.user.google.listener.GoogleUnBindListener;
import com.appsferry.user.google.param.GoogleSignInOptions;

public class GoogleManager {
    private static final GoogleManager INSTANCE = new GoogleManager();

    public static final String TAG = "GoogleManager";

    private GoogleManager() {
    }

    public static GoogleManager getInstance() {
        return INSTANCE;
    }

    public void init(@NonNull Context context, @NonNull GoogleSignInOptions options) {
        GoogleAuthHandler.getInstance().init(context, options);
    }

    public void signInSilently(@NonNull GoogleSignInListener listener) {
        GoogleAuthHandler.getInstance().signInSilently(listener);
    }

    public <T extends UserModel<? extends UserProfile>> void loginWithGoogle(@NonNull Activity activity, boolean isSilently, @NonNull GoogleLoginListener<T> listener) {
        GoogleAuthHandler.getInstance().loginWithGoogle(activity, isSilently, listener);
    }

    public void bindWithGoogle(@NonNull Activity activity, boolean isSilently, @NonNull GoogleBindListener listener) {
        GoogleAuthHandler.getInstance().bindWithGoogle(activity, isSilently, listener);
    }

    public void unBindWithGoogle(@NonNull GoogleUnBindListener listener) {
        GoogleAuthHandler.getInstance().unBindWithGoogle(listener);
    }

    public void checkUnbind(@NonNull GoogleCheckUnbindListener listener) {
        GoogleAuthHandler.getInstance().checkUnbind(listener);
    }

}
