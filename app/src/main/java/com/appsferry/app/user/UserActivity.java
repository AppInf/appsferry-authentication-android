package com.appsferry.app.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.appsferry.app.R;
import com.appsferry.app.user.core.UserCoreActivity;
import com.appsferry.app.user.email.EmailActivity;
import com.appsferry.app.user.facebook.FacebookActivity;
import com.appsferry.app.user.google.GoogleActivity;
import com.appsferry.app.user.phone.PhoneActivity;
import com.appsferry.app.user.snapchat.SnapchatActivity;
import com.appsferry.app.user.tiktok.TikTokActivity;
import com.appsferry.login.api.AccountService;
import com.appsferry.login.api.UserSDK;
import com.appsferry.login.listener.base.SDKError;
import com.appsferry.login.listener.user.LoginStatusListener;
import com.appsferry.login.listener.user.LogoffListener;
import com.appsferry.login.listener.user.LogoutListener;
import com.appsferry.login.listener.visitor.VisitorLoginListener;
import com.google.gson.Gson;

public class UserActivity extends AppCompatActivity {
    private TextView loginStatus, curUid, curSid, userInfo;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);
        findViewById(R.id.login_phone).setOnClickListener(v -> loginByPhoneCodeOrPassword());
        findViewById(R.id.login_email).setOnClickListener(v -> loginByEmailCodeOrPassword());
        findViewById(R.id.login_visitor).setOnClickListener(v -> loginWithVisitor());
        findViewById(R.id.tiktok).setOnClickListener(v -> startActivity(TikTokActivity.class));
        findViewById(R.id.snapchat).setOnClickListener(v -> startActivity(SnapchatActivity.class));
        findViewById(R.id.facebook).setOnClickListener(v -> startActivity(FacebookActivity.class));
        findViewById(R.id.google).setOnClickListener(v -> loginByGoogle());
        findViewById(R.id.userCore).setOnClickListener(v -> openUserCenter());
        findViewById(R.id.logout).setOnClickListener(v -> logout());
        findViewById(R.id.local_logout).setOnClickListener(v -> localLogout());
        findViewById(R.id.local_logoff).setOnClickListener(v -> logoff());
        userInfo = findViewById(R.id.user_info);
        loginStatus = findViewById(R.id.login_status);
        curUid = findViewById(R.id.cur_uid);
        curSid = findViewById(R.id.cur_sid);
        UserSDK.getInstance().setPrivacyAgree(true);
        UserSDK.getInstance().init(getApplicationContext(), MyUserModel.class);
        UserSDK.getInstance().getLoginService().registerLoginStatusListener(new LoginStatusListener() {
            @Override
            public void beforeLogin() {
                Toast.makeText(UserActivity.this, "beforeLogin", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void afterLogin() {
                Toast.makeText(UserActivity.this, "afterLogin", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void beforeLogout() {
                Toast.makeText(UserActivity.this, "beforeLogout", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void afterLogout() {
                Toast.makeText(UserActivity.this, "afterLogout", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginByGoogle() {
        startActivity(GoogleActivity.class);
    }

    private void logoff() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
        builder.setTitle("Logoff?");
        builder.setMessage("Unrecoverable after this operation");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                UserSDK.getInstance().getLoginService().logoff(new LogoffListener() {
                    @Override
                    public void onSuccess() {
                        userInfo.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UserActivity.this, "Logoff Success", Toast.LENGTH_SHORT).show();
                                loginStatus.setText("Status:logout");
                                curUid.setText("current uid:" + UserSDK.getInstance().getLoginService().getUserId());
                                curSid.setText("current sid:" + UserSDK.getInstance().getLoginService().getSession());
                                userInfo.setText("UserInfo is Empty");
                            }
                        });
                    }

                    @Override
                    public void onError(SDKError error) {
                        Toast.makeText(UserActivity.this, "Logoff Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openUserCenter() {
        if (UserSDK.getInstance().getLoginService().isLogin()) {
            startActivity(UserCoreActivity.class);
        } else {
            Toast.makeText(UserActivity.this, "Please Login", Toast.LENGTH_SHORT).show();
        }
    }

    private void loginByEmailCodeOrPassword() {
        if (UserSDK.getInstance().getLoginService().isLogin()) {
            Toast.makeText(UserActivity.this, "Please Logout", Toast.LENGTH_SHORT).show();
        } else {
            startActivity(EmailActivity.class);
        }
    }

    private void loginByPhoneCodeOrPassword() {
        if (UserSDK.getInstance().getLoginService().isLogin()) {
            Toast.makeText(UserActivity.this, "Please Logout", Toast.LENGTH_SHORT).show();
        } else {
            startActivity(PhoneActivity.class);
        }
    }

    private void localLogout() {
        UserSDK.getInstance().getLoginService().localLogout(new LogoutListener() {
            @Override
            public void onSuccess() {
                userInfo.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UserActivity.this, "Local Logout Success", Toast.LENGTH_SHORT).show();
                        loginStatus.setText("Status:logout");
                        curUid.setText("current uid:" + UserSDK.getInstance().getLoginService().getUserId());
                        curSid.setText("current sid:" + UserSDK.getInstance().getLoginService().getSession());
                        userInfo.setText("UserInfo is Empty");
                    }
                });
            }

            @Override
            public void onError(SDKError error) {

            }
        });
    }


    private void logout(){
        UserSDK.getInstance().getLoginService().logout(new LogoutListener() {
            @Override
            public void onSuccess() {
                userInfo.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UserActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                        loginStatus.setText("Status:logout");
                        curUid.setText("current uid:" + UserSDK.getInstance().getLoginService().getUserId());
                        curSid.setText("current sid:" + UserSDK.getInstance().getLoginService().getSession());
                        userInfo.setText("UserInfo is Empty");
                    }
                });
            }

            @Override
            public void onError(SDKError error) {

            }
        });
    }

    private void startActivity(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    private void loginWithVisitor(){
        if (UserSDK.getInstance().getLoginService().isLogin()) {
            Toast.makeText(UserActivity.this, "Please Logout", Toast.LENGTH_SHORT).show();
        } else {
            UserSDK.getInstance().getLoginService().visitorLogin(new VisitorLoginListener() {
                @Override
                public void onNewData(Object entity) {
                    userInfo.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UserActivity.this, "visitor login success", Toast.LENGTH_SHORT).show();
                            loginStatus.setText("Status:login");
                            AccountService<MyUserModel> accountService = UserSDK.getInstance().getAccountService();
                            MyUserModel userModel = accountService.getCacheUserModel();
                            userInfo.setText("UserInfo：" + gson.toJson(userModel));
                            curUid.setText("current uid：" + UserSDK.getInstance().getLoginService().getUserId());
                            curSid.setText("current sid：" + UserSDK.getInstance().getLoginService().getSession());
                        }
                    });
                }

                @Override
                public void onError(SDKError error) {
                    Toast.makeText(UserActivity.this, "visitor login failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UserSDK.getInstance().getLoginService().isLogin()) {
            loginStatus.setText("Status:login");
            AccountService<MyUserModel> accountService = UserSDK.getInstance().getAccountService();
            MyUserModel userModel = accountService.getCacheUserModel();
            userInfo.setText("UserInfo：" + gson.toJson(userModel));
            curUid.setText("current uid：" + UserSDK.getInstance().getLoginService().getUserId());
            curSid.setText("current sid：" + UserSDK.getInstance().getLoginService().getSession());
        } else {
            loginStatus.setText("Status:logout");
            curUid.setText("current uid:" + UserSDK.getInstance().getLoginService().getUserId());
            curSid.setText("current sid:" + UserSDK.getInstance().getLoginService().getSession());
            userInfo.setText("UserInfo is Empty");
        }
    }
}
