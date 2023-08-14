package com.appsferry.app.user.phone;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.appsferry.app.R;
import com.appsferry.login.api.UserSDK;
import com.appsferry.login.listener.base.SDKError;
import com.appsferry.login.listener.phone.PhoneCodeVerifyListener;
import com.appsferry.login.listener.phone.PhonePwdListener;
import com.appsferry.login.listener.phone.PhonePwdLoginCodeListener;

public class PhonePwdActivity extends AppCompatActivity {
    private EditText phone;
    private EditText phonePassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_pwd);
        phone = findViewById(R.id.phone);
        phonePassword = findViewById(R.id.phonePassword);
        findViewById(R.id.getPhoneCode).setOnClickListener(v -> getPhoneCode());
        findViewById(R.id.verifierPhoneCode).setOnClickListener(v -> verifierPhoneCode());
        findViewById(R.id.setPhonePassword).setOnClickListener(v -> setPhonePassword());
        findViewById(R.id.checkPhoneInfo).setOnClickListener(v -> checkPhoneInfo());
    }

    private void getPhoneCode() {
        String phoneNum = phone.getText().toString();
        if (TextUtils.isEmpty(phoneNum)) {
            Toast.makeText(PhonePwdActivity.this, "please input your phone number", Toast.LENGTH_SHORT).show();
            return;
        }
        UserSDK.getInstance().getLoginService().sendPhonePwdLoginVerifyCode("86", phoneNum, new PhonePwdLoginCodeListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(PhonePwdActivity.this, "Phone Code Send Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(SDKError error) {
                Toast.makeText(PhonePwdActivity.this, "Phone Code Send Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verifierPhoneCode() {
        EditText editText = findViewById(R.id.phoneCode);
        String code = editText.getText().toString();
        String phoneStr = phone.getText().toString();
        if (TextUtils.isEmpty(phoneStr) || TextUtils.isEmpty(code)) {
            Toast.makeText(PhonePwdActivity.this, "Please Input Phone And Code", Toast.LENGTH_LONG).show();
            return;
        }
        UserSDK.getInstance().getLoginService().verifyPhonePwdLoginCode("86", phoneStr, code, new PhoneCodeVerifyListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(PhonePwdActivity.this, "Verify Code Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(SDKError error) {
                Toast.makeText(PhonePwdActivity.this, "Verify Code Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setPhonePassword() {
        String phoneNum = phone.getText().toString();
        String pwd = phonePassword.getText().toString();
        if (TextUtils.isEmpty(phoneNum) || TextUtils.isEmpty(pwd)) {
            Toast.makeText(PhonePwdActivity.this, "Please Input Phone And Password", Toast.LENGTH_LONG).show();
            return;
        }
        UserSDK.getInstance().getLoginService().setPasswordWithPhone("86", phoneNum, pwd, new PhonePwdListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(PhonePwdActivity.this, "Phone Password Set Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(SDKError error) {
                Toast.makeText(PhonePwdActivity.this, "Phone Password Set Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkPhoneInfo() {
        String phoneNum = phone.getText().toString();

        UserSDK.getInstance().getAccountService().checkAccountInfo("phone", phoneNum, "86", "password");
    }
}
