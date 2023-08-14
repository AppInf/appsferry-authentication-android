package com.appsferry.app.user.phone;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.appsferry.app.R;
import com.appsferry.app.user.MyUserModel;
import com.appsferry.login.api.UserSDK;
import com.appsferry.login.listener.phone.PhoneLoginCodeListener;
import com.appsferry.login.listener.phone.PhoneLoginListener;
import com.appsferry.login.listener.base.SDKError;
import com.appsferry.login.listener.phone.PhonePwdLoginListener;

public class PhoneActivity extends AppCompatActivity {
    EditText phoneCodeNum, phonePwdNum, phoneCode, phonePwd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_phone_activity);
        phoneCodeNum = findViewById(R.id.phone);
        phoneCode = findViewById(R.id.phoneCode);
        phonePwdNum = findViewById(R.id.phoneNum);
        phonePwd = findViewById(R.id.phonePwd);
        findViewById(R.id.getPhoneCode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = phoneCodeNum.getText().toString();
                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(PhoneActivity.this, "please input your phone number", Toast.LENGTH_SHORT).show();
                    return;
                }
                UserSDK.getInstance().getLoginService().sendPhoneLoginCode("86", phoneNumber, new PhoneLoginCodeListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(PhoneActivity.this, "get phone login code success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(SDKError error) {
                        Toast.makeText(PhoneActivity.this, "get phone login code failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        findViewById(R.id.loginWithPhone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = phoneCodeNum.getText().toString();
                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(PhoneActivity.this, "please input your phone number", Toast.LENGTH_SHORT).show();
                    return;
                }
                String verifyCode = phoneCode.getText().toString();
                if (TextUtils.isEmpty(verifyCode)) {
                    Toast.makeText(PhoneActivity.this, "please input your phone code", Toast.LENGTH_SHORT).show();
                    return;
                }
                UserSDK.getInstance().getLoginService().loginByPhoneCode("86", phoneNumber, verifyCode, new PhoneLoginListener<MyUserModel>() {
                    @Override
                    public void onNewData(MyUserModel entity) {
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PhoneActivity.this, "Phone Code Login Success", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onError(SDKError error) {
                        Toast.makeText(PhoneActivity.this, "Phone Code Login Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        findViewById(R.id.loginPwdWithPhone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = phonePwdNum.getText().toString();
                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(PhoneActivity.this, "please input your phone number", Toast.LENGTH_SHORT).show();
                    return;
                }
                String password = phonePwd.getText().toString();
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(PhoneActivity.this, "please input your password", Toast.LENGTH_SHORT).show();
                    return;
                }
                UserSDK.getInstance().getLoginService().loginByPhonePassword(
                        "86", phoneNumber, password
                        , new PhonePwdLoginListener() {
                    @Override
                    public void onNewData(Object entity) {
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PhoneActivity.this, "Phone Password Login Success", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onError(SDKError error) {
                        Toast.makeText(PhoneActivity.this, "Phone Password Login Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
