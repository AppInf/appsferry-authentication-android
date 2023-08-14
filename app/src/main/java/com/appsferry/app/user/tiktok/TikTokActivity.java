package com.appsferry.app.user.tiktok;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.appsferry.app.R;
import com.appsferry.login.entity.user.UserModel;
import com.appsferry.login.entity.user.UserProfile;
import com.appsferry.user.tiktok.TikTokManager;
import com.appsferry.user.tiktok.listener.TikTokBindListener;
import com.appsferry.user.tiktok.listener.TikTokLoginListener;
import com.appsferry.user.tiktok.listener.TikTokUnBindListener;

public class TikTokActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiktok);
        findViewById(R.id.loginWithTiktok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TikTokManager.getInstance().loginByTikTok(TikTokActivity.this, "user.info.basic", null, new TikTokLoginListener<UserModel<? extends UserProfile>>() {
                    @Override
                    public void onSuccess(UserModel<? extends UserProfile> userModel) {
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(TikTokActivity.this, "Tiktok Login Success", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMsg) {
                        Toast.makeText(TikTokActivity.this, "Tiktok Login Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        findViewById(R.id.bindWithTiktok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TikTokManager.getInstance().bindWithTikTok(TikTokActivity.this, "user.info.basic", "", new TikTokBindListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(TikTokActivity.this, "Tiktok Bind Success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMsg) {
                        Toast.makeText(TikTokActivity.this, "Tiktok Bind Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        findViewById(R.id.unbindWithTiktok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TikTokManager.getInstance().unbindWithTikTok(new TikTokUnBindListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(TikTokActivity.this, "Tiktok Unbind Success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMsg) {
                        Toast.makeText(TikTokActivity.this, "Tiktok Unbind Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}