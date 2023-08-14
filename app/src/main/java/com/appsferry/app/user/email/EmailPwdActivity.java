package com.appsferry.app.user.email;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.appsferry.app.R;
import com.appsferry.login.api.UserSDK;
import com.appsferry.login.listener.base.SDKError;
import com.appsferry.login.listener.email.EmailCodeVerifyListener;
import com.appsferry.login.listener.email.EmailPwdListener;
import com.appsferry.login.listener.email.EmailPwdLoginCodeListener;

public class EmailPwdActivity extends AppCompatActivity {

    private EditText email;
    private EditText code;
    private EditText emailPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_pwd);
        email = findViewById(R.id.email);
        emailPassword = findViewById(R.id.emailPassword);
        code = findViewById(R.id.emailCode);
        findViewById(R.id.getEmailCode).setOnClickListener(v -> getEmailCode());
        findViewById(R.id.verifierEmailCode).setOnClickListener(v -> verifierEmailCodeWithEmal());
        findViewById(R.id.setEmailPassword).setOnClickListener(v -> setEmailPassword());
        findViewById(R.id.checkEmailInfo).setOnClickListener(v -> checkEmailInfo());
    }

    private void getEmailCode() {
        String emailNum = email.getText().toString();
        if (TextUtils.isEmpty(emailNum)) {
            Toast.makeText(EmailPwdActivity.this, "please input your email", Toast.LENGTH_SHORT).show();
            return;
        }
        UserSDK.getInstance().getLoginService().sendEmailPwdLoginVerifyCode(emailNum, new EmailPwdLoginCodeListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(EmailPwdActivity.this, "Email Code Send Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(SDKError error) {
                Toast.makeText(EmailPwdActivity.this, "Email Code Send Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verifierEmailCodeWithEmal() {
        String emailNum = email.getText().toString();
        String codeStr = code.getText().toString();

        if (TextUtils.isEmpty(emailNum) || TextUtils.isEmpty(codeStr)) {
            Toast.makeText(EmailPwdActivity.this, "Please Input Your Email And Code", Toast.LENGTH_LONG).show();
            return;
        }

        UserSDK.getInstance().getLoginService().verifyEmailPwdLoginCode(emailNum, codeStr, new EmailCodeVerifyListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(EmailPwdActivity.this, "Verify Email Code Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(SDKError error) {
                Toast.makeText(EmailPwdActivity.this, "Verify Email Code Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setEmailPassword() {
        String emailNum = email.getText().toString();
        String emailPwd = emailPassword.getText().toString();

        if (TextUtils.isEmpty(emailNum) || TextUtils.isEmpty(emailPwd)) {
            Toast.makeText(EmailPwdActivity.this, "Please Input Your Email And Password", Toast.LENGTH_LONG).show();
        }
        UserSDK.getInstance().getLoginService().setPasswordWithEmail(emailNum, emailPwd, new EmailPwdListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(EmailPwdActivity.this, "Set Email password Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(SDKError error) {
                Toast.makeText(EmailPwdActivity.this, "Set Email password Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkEmailInfo() {
        String emailNum = email.getText().toString();
    }
}
