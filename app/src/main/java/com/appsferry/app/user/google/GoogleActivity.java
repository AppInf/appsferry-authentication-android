package com.appsferry.app.user.google;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.appsferry.app.R;
import com.appsferry.login.api.UserSDK;
import com.appsferry.login.entity.user.UserModel;
import com.appsferry.login.entity.user.UserProfile;
import com.appsferry.user.google.GoogleManager;
import com.appsferry.user.google.listener.GoogleBindListener;
import com.appsferry.user.google.listener.GoogleLoginListener;
import com.appsferry.user.google.listener.GoogleSignInListener;
import com.appsferry.user.google.listener.GoogleUnBindListener;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class GoogleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google);
        findViewById(R.id.loginWithGoogle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserSDK.getInstance().getLoginService().isLogin()) {
                    Toast.makeText(GoogleActivity.this, "Attempt After Logout", Toast.LENGTH_SHORT).show();
                    return;
                }
                GoogleManager.getInstance().signInSilently(new GoogleSignInListener() {
                    @Override
                    public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                        GoogleManager.getInstance().loginWithGoogle(GoogleActivity.this, true, new GoogleLoginListener<UserModel<? extends UserProfile>>() {
                            @Override
                            public void onSuccess(UserModel<? extends UserProfile> userModel) {
                                view.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GoogleActivity.this, "Google Login Success", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            }

                            @Override
                            public void onFailed(int errorCode, String errorMsg) {
                                Toast.makeText(GoogleActivity.this, "Google Login Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMsg) {
                        GoogleManager.getInstance().loginWithGoogle(GoogleActivity.this, false, new GoogleLoginListener<UserModel<? extends UserProfile>>() {
                            @Override
                            public void onSuccess(UserModel<? extends UserProfile> userModel) {
                                view.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GoogleActivity.this, "Google Login Success", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            }

                            @Override
                            public void onFailed(int errorCode, String errorMsg) {
                                Toast.makeText(GoogleActivity.this, "Google Login Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });

        findViewById(R.id.bindWithGoogle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoogleManager.getInstance().bindWithGoogle(GoogleActivity.this, true, new GoogleBindListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(GoogleActivity.this, "Google Bind Success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMsg) {
                        Toast.makeText(GoogleActivity.this, "Google Bind Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        findViewById(R.id.unbindWithGoogle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoogleManager.getInstance().unBindWithGoogle(new GoogleUnBindListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(GoogleActivity.this, "Google Unbind Success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMsg) {
                        Toast.makeText(GoogleActivity.this, "Google Unbind Failed", Toast.LENGTH_SHORT).show();
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