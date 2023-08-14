package com.appsferry.app.user.core;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.appsferry.app.R;
import com.appsferry.login.api.UserSDK;
import com.appsferry.login.listener.base.SDKError;
import com.appsferry.login.listener.user.FetchUserInfoListener;
import com.appsferry.login.listener.user.UpdateUserProfileListener;
import com.google.gson.Gson;

import java.util.HashMap;

public class UserUpdateActivity extends AppCompatActivity {
    EditText editNickName, editGender;
    private Gson gson = new Gson();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_update);
        editNickName = findViewById(R.id.edit_user_name);
        editGender = findViewById(R.id.gender);
        findViewById(R.id.button_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> params = new HashMap<>();
                String newNick = editNickName.getText().toString();
                if (!TextUtils.isEmpty(newNick)) {
                    params.put("nick", newNick);
                }
                String newGender = editGender.getText().toString();
                if (!TextUtils.isEmpty(newGender)) {
                    params.put("gender", Integer.valueOf(newGender));
                }
                UserSDK.getInstance().getAccountService().updateUserInfo(params, new UpdateUserProfileListener() {
                    @Override
                    public void onSuccess() {
                        editNickName.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UserUpdateActivity.this, "UserInfo Update Success", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onError(SDKError error) {
                        Toast.makeText(UserUpdateActivity.this, "UserInfo Update Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        findViewById(R.id.btn_fetch_user_model).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserSDK.getInstance().getAccountService().fetchUserModel(String.valueOf(UserSDK.getInstance().getAccountService().getCacheUserModel().uid), new FetchUserInfoListener() {
                    @Override
                    public void onNewData(Object entity) {
                        editNickName.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UserUpdateActivity.this, "Fetch UserInfo Success", Toast.LENGTH_SHORT).show();
                                TextView tv = findViewById(R.id.txt_total_info);
                                tv.setText(gson.toJson(entity));
                            }
                        });
                    }

                    @Override
                    public void onError(SDKError error) {
                        Toast.makeText(UserUpdateActivity.this, "Fetch UserInfo Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
