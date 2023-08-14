package com.appsferry.user.google.listener;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public interface GoogleSignInListener {

    void onSuccess(GoogleSignInAccount googleSignInAccount);

    void onFailed(int errorCode, String errorMsg);
}
