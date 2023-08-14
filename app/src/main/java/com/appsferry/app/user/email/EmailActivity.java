package com.appsferry.app.user.email;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.appsferry.app.R;
import com.appsferry.login.api.UserSDK;
import com.appsferry.login.listener.email.EmailLoginCodeListener;
import com.appsferry.login.listener.email.EmailLoginListener;
import com.appsferry.login.listener.base.SDKError;
import com.appsferry.login.listener.email.EmailPwdLoginListener;

public class EmailActivity extends AppCompatActivity {

    private EditText email;
    private EditText emailNum;
    private EditText emailPwd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        email = findViewById(R.id.email);
        emailNum = findViewById(R.id.emailNum);
        emailPwd = findViewById(R.id.emailPwd);

        findViewById(R.id.getEmailCode).setOnClickListener(v -> getEmailCode());
        findViewById(R.id.loginWithEmail).setOnClickListener(v -> loginWithEmail());
        findViewById(R.id.loginPwdWithEmail).setOnClickListener(v -> loginPwdWithEmail());
    }

    private void getEmailCode() {
        String emailNum = email.getText().toString();
        if (TextUtils.isEmpty(emailNum)) {
            Toast.makeText(EmailActivity.this, "please input your email", Toast.LENGTH_SHORT).show();
            return;
        }
        UserSDK.getInstance().getLoginService().sendEmailLoginCode(emailNum, new EmailLoginCodeListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(EmailActivity.this, "Email Code Send Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(SDKError error) {
                Toast.makeText(EmailActivity.this, "Email Code Send Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginWithEmail() {
        String emailNum = email.getText().toString();
        EditText editText = findViewById(R.id.emailCode);
        String code = editText.getText().toString();

        if (TextUtils.isEmpty(emailNum) || TextUtils.isEmpty(code)) {
            Toast.makeText(EmailActivity.this, "Please Input Your Email And Code", Toast.LENGTH_LONG).show();
            return;
        }

        UserSDK.getInstance().getLoginService().loginByEmailCode(emailNum, code, new EmailLoginListener() {
            @Override
            public void onNewData(Object entity) {
                editText.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(EmailActivity.this, "Email Code Login Success", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }

            @Override
            public void onError(SDKError error) {
                Toast.makeText(EmailActivity.this, "Email Code Login Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginPwdWithEmail() {
        String email = emailNum.getText().toString();
        String emailPwdStr = emailPwd.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(emailPwdStr)) {
            Toast.makeText(EmailActivity.this, "Please Input Your Email And Password", Toast.LENGTH_LONG).show();
            return;
        }

        UserSDK.getInstance().getLoginService().loginByEmailPassword(
                email, emailPwdStr, new EmailPwdLoginListener() {
                    @Override
                    public void onNewData(Object entity) {
                        Toast.makeText(EmailActivity.this, "Email Password Login Success", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError(SDKError error) {
                        Toast.makeText(EmailActivity.this, "Email Password Login Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
