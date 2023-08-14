package com.appsferry.app.user.core;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.appsferry.app.R;
import com.appsferry.app.user.email.EmailPwdActivity;
import com.appsferry.app.user.phone.PhonePwdActivity;

public class UserCoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);
        findViewById(R.id.user_update).setOnClickListener(v -> startActivity(UserUpdateActivity.class));
        findViewById(R.id.setPhonePwd).setOnClickListener(v -> startActivity(PhonePwdActivity.class));
        findViewById(R.id.setEmailPwd).setOnClickListener(v -> startActivity(EmailPwdActivity.class));
    }

    private void startActivity(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

}
