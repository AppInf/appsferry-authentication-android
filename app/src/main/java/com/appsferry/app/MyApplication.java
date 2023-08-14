package com.appsferry.app;

import android.app.Application;
import android.content.Context;

import com.appsferry.adapter.AdapterSDK;
import com.appsferry.adapter.AhParams;
import com.appsferry.user.google.GoogleManager;
import com.appsferry.user.google.common.SignInType;
import com.appsferry.user.google.param.GoogleSignInOptions;
import com.appsferry.user.snapchat.SnapchatManager;
import com.appsferry.user.tiktok.TikTokManager;

/**
 * description:
 *
 * @author Floyd
 * @since 2023/5/31
 */
public class MyApplication extends Application {
    private final static String APPID = "bnZ3YWRlbW86TFVCQU4";
    private final static String CV = "LUBAN1.0.01_Android";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AdapterSDK.initSdk(
            new AhParams()
                .withAppid(APPID)
                .withCv(CV)
                .withSmid( "Dukjhjgfghjkljhgfjkjhgfhjkhgfhjkjhfghj")
                .withHttpParams("https://your.bizhost.com", "https://your.upload_host.com", "your.report_url.com")
        );

        GoogleSignInOptions options = new GoogleSignInOptions.Builder()
            .setSignInOption(SignInType.DEFAULT_SIGN_IN)
            .build();
        GoogleManager.getInstance().init(this, options);
        TikTokManager.getInstance().init("aw4fytdiai60bq0s", "appsferry://com.inke.inf.kae/tiktok");
        SnapchatManager.getInstance().init(this);
    }
}
