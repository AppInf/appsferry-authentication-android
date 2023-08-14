package com.appsferry.user.google.listener;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public interface GoogleAuthListener {

    void onSuccess(GoogleSignInAccount account);

    void onFailed(int errorCode, String errorMsg);
}
