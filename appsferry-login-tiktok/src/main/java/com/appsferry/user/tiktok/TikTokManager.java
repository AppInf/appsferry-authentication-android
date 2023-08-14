package com.appsferry.user.tiktok;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.appsferry.login.entity.user.UserModel;
import com.appsferry.login.entity.user.UserProfile;
import com.appsferry.user.tiktok.handlers.TiktokAuthHandler;
import com.appsferry.user.tiktok.listener.TikTokAuthListener;
import com.appsferry.user.tiktok.listener.TikTokBindListener;
import com.appsferry.user.tiktok.listener.TikTokCheckUnbindListener;
import com.appsferry.user.tiktok.listener.TikTokLoginListener;
import com.appsferry.user.tiktok.listener.TikTokUnBindListener;

public class TikTokManager {
    public static final TikTokManager INSTANCE = new TikTokManager();
    public static final String TAG = "TikTokManager";

    private TikTokManager() {
    }

    public static TikTokManager getInstance() {
        return INSTANCE;
    }

    public void init(@NonNull String clientKey, String redirectUri) {
        TiktokAuthHandler.getInstance().init(clientKey, redirectUri);
    }

    public boolean isAppInstalled(@NonNull Activity activity) {
        return TiktokAuthHandler.getInstance().isAppInstalled(activity);
    }

    public boolean authorize(@NonNull Activity activity, @NonNull String scope, String state, @NonNull TikTokAuthListener listener) {
        return TiktokAuthHandler.getInstance().authorize(activity, scope, state, listener);
    }

    public <T extends UserModel<? extends UserProfile>> void loginByTikTok(@NonNull Activity activity, @NonNull String scope, String state, @NonNull TikTokLoginListener<T> listener) {
        TiktokAuthHandler.getInstance().loginByTikTok(activity, scope, state, listener);
    }

    public void bindWithTikTok(@NonNull Activity activity, @NonNull String scope, String state, @NonNull TikTokBindListener listener) {
        TiktokAuthHandler.getInstance().bindWithTikTok(activity, scope, state, listener);
    }

    public void unbindWithTikTok(@NonNull TikTokUnBindListener listener) {
        TiktokAuthHandler.getInstance().unbindWithTikTok(listener);
    }

    public void canUnbindTiktok(@NonNull TikTokCheckUnbindListener listener) {
        TiktokAuthHandler.getInstance().canUnbindTiktok(listener);
    }


}
