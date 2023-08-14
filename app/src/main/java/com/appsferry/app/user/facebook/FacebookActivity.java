package com.appsferry.app.user.facebook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.appsferry.app.R;
import com.appsferry.app.user.google.GoogleActivity;
import com.appsferry.login.api.UserSDK;
import com.appsferry.login.entity.user.UserModel;
import com.appsferry.login.entity.user.UserProfile;
import com.appsferry.user.facebook.FacebookManager;
import com.appsferry.user.facebook.listener.FacebookBindListener;
import com.appsferry.user.facebook.listener.FacebookLoginListener;
import com.appsferry.user.facebook.listener.FacebookUnBindListener;
import com.appsferry.user.google.GoogleManager;
import com.appsferry.user.google.listener.GoogleBindListener;
import com.appsferry.user.google.listener.GoogleUnBindListener;

import java.util.ArrayList;
import java.util.List;

public class FacebookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook);
        findViewById(R.id.loginWithFacebook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserSDK.getInstance().getLoginService().isLogin()) {
                    Toast.makeText(FacebookActivity.this, "Attempt After Logout", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<String> permissions = new ArrayList<>();
                permissions.add("public_profile");
                permissions.add("email");
                FacebookManager.getInstance().loginWithFacebook(FacebookActivity.this, permissions, new FacebookLoginListener<UserModel<? extends UserProfile>>() {
                    @Override
                    public void onSuccess(UserModel<? extends UserProfile> userModel) {
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(FacebookActivity.this, "Facebook Login Success", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMsg) {
                        Toast.makeText(FacebookActivity.this, "Facebook Login Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        findViewById(R.id.bindWithFacebook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> permissions = new ArrayList<>();
                permissions.add("public_profile");
                permissions.add("email");
                FacebookManager.getInstance().bindWithFacebook(FacebookActivity.this, permissions, new FacebookBindListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(FacebookActivity.this, "Facebook Bind Success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMsg) {
                        Toast.makeText(FacebookActivity.this, "Facebook Bind Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        findViewById(R.id.unbindWithFacebook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FacebookManager.getInstance().unBindWithFacebook(new FacebookUnBindListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(FacebookActivity.this, "Facebook Unbind Success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMsg) {
                        Toast.makeText(FacebookActivity.this, "Facebook Unbind Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}