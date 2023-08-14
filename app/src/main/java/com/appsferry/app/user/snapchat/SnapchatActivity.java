package com.appsferry.app.user.snapchat;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.appsferry.app.R;
import com.appsferry.app.user.google.GoogleActivity;
import com.appsferry.app.user.tiktok.TikTokActivity;
import com.appsferry.login.entity.user.UserModel;
import com.appsferry.login.entity.user.UserProfile;
import com.appsferry.user.snapchat.SnapchatManager;
import com.appsferry.user.snapchat.listener.SnapChatBindListener;
import com.appsferry.user.snapchat.listener.SnapChatLoginListener;
import com.appsferry.user.snapchat.listener.SnapChatUnbindListener;

public class SnapchatActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snapchat);
        findViewById(R.id.loginWithSnapchat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SnapchatManager.getInstance().loginBySnapchat(new SnapChatLoginListener<UserModel<? extends UserProfile>>() {
                    @Override
                    public void onSuccess(UserModel<? extends UserProfile> userModel) {
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SnapchatActivity.this, "Snapchat Login Success", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMsg) {
                        Toast.makeText(SnapchatActivity.this, "Snapchat Login Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        findViewById(R.id.bindWithSnapchat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SnapchatManager.getInstance().bindAccountBySnapchat(new SnapChatBindListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(SnapchatActivity.this, "Snapchat Bind Success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMsg) {
                        Toast.makeText(SnapchatActivity.this, "Snapchat Bind Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        findViewById(R.id.unbindWithSnapchat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SnapchatManager.getInstance().unBindAccountBySnapchat(new SnapChatUnbindListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(SnapchatActivity.this, "Snapchat Unbind Success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMsg) {
                        Toast.makeText(SnapchatActivity.this, "Snapchat Unbind Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}