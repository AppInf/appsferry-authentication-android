package com.appsferry.user.google.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import com.appsferry.user.google.common.GoogleRequestCode;
import com.appsferry.user.google.handlers.GoogleAuthHandler;

public class LinkAuthGoogleFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        GoogleSignInClient signInClient = GoogleAuthHandler.getInstance().getSignInClient();
        if (signInClient != null) {
            Intent signInIntent = signInClient.getSignInIntent();
            startActivityForResult(signInIntent, GoogleRequestCode.REQUEST_CODE_SIGN_IN);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        GoogleAuthHandler.getInstance().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
