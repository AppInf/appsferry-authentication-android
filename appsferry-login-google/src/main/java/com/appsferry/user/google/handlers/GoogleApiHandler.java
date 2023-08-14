package com.appsferry.user.google.handlers;

import android.accounts.Account;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import com.appsferry.user.google.GoogleManager;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.appsferry.user.google.common.GoogleConstants;
import com.appsferry.user.google.common.GoogleRequestCode;
import com.appsferry.user.google.common.SignInType;
import com.appsferry.user.google.error.GoogleErrorCode;
import com.appsferry.user.google.error.GoogleErrorMsg;
import com.appsferry.user.google.fragment.LinkAuthGoogleFragment;
import com.appsferry.user.google.listener.GoogleClearCacheListener;
import com.appsferry.user.google.listener.GoogleDisconnectListener;
import com.appsferry.user.google.listener.GoogleGetTokenListener;
import com.appsferry.user.google.listener.GoogleRequestScopeListener;
import com.appsferry.user.google.listener.GoogleSignInListener;
import com.appsferry.user.google.listener.GoogleSignOutListener;
import com.appsferry.user.google.param.GoogleSignInOptions;
import com.appsferry.user.google.util.ListUtil;
import com.appsferry.user.google.util.ThreadUtil;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class GoogleApiHandler {
    private GoogleSignInClient signInClient;
    private GoogleSignInListener signInListener;
    private GoogleRequestScopeListener scopeListener;
    private List<String> requestedScopes;
    private FragmentManager mFragmentManager;
    private LinkAuthGoogleFragment mLinkGoogleFragment;

    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public void init(@NonNull Context context, @NonNull GoogleSignInOptions options) {
        com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder optionsBuilder;
        switch (options.signInOption) {
            case SignInType
                    .DEFAULT_GAMES_SIGN_IN:
                optionsBuilder =
                        new com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
                break;
            case SignInType.DEFAULT_SIGN_IN:
                optionsBuilder =
                        new com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail();
                break;
            default:
                throw new IllegalStateException("Unknown signInOption");
        }
        int clientIdIdentifier = context.getResources()
                .getIdentifier(GoogleConstants.DEFAULT_WEB_CLIENT_ID, GoogleConstants.DEF_TYPE, context.getPackageName());
        if (!TextUtils.isEmpty(options.clientId)) {
            optionsBuilder.requestIdToken(options.clientId);
            optionsBuilder.requestServerAuthCode(options.clientId);
        } else if (clientIdIdentifier != 0) {
            optionsBuilder.requestIdToken(context.getString(clientIdIdentifier));
            optionsBuilder.requestServerAuthCode(context.getString(clientIdIdentifier));
        }
        if (options.requestedScopes != null && !options.requestedScopes.isEmpty()) {
            this.requestedScopes = options.requestedScopes;
        } else {
            this.requestedScopes = ListUtil.getRequestedScopes();
        }
        for (String scope : requestedScopes) {
            optionsBuilder.requestScopes(new Scope(scope));
        }
        if (!TextUtils.isEmpty(options.hostedDomain)) {
            optionsBuilder.setHostedDomain(options.hostedDomain);
        }
        signInClient = GoogleSignIn.getClient(context, optionsBuilder.build());
    }

    public GoogleSignInClient getSignInClient() {
        return signInClient;
    }

    public void signInSilently(@NonNull GoogleSignInListener listener) {
        if (signInClient != null) {
            Task<GoogleSignInAccount> task = signInClient.silentSignIn();
            if (task.isSuccessful()) {
                listener.onSuccess(task.getResult());
            } else {
                task.addOnCompleteListener(task1 -> onSignInResult(task1, listener));
            }
        }
    }

    public void signIn(@NonNull Activity activity, GoogleSignInListener listener) {
        this.signInListener = listener;
        if (signInClient != null) {
            mFragmentManager = activity.getFragmentManager();
            mLinkGoogleFragment = new LinkAuthGoogleFragment();
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.add(mLinkGoogleFragment, GoogleConstants.FRAGMENT_AUTH_TAG);
            fragmentTransaction.commit();
        }
    }

    public void signOut(@NonNull GoogleSignOutListener listener) {
        if (signInClient != null) {
            signInClient.signOut().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    listener.onSuccess();
                } else {
                    listener.onFailed(GoogleErrorCode.GOOGLE_SIGN_OUT_ERROR_CODE, GoogleErrorMsg.signOutFailed());
                }
            });
        }

    }

    public boolean isSignedIn(@NonNull Context context) {
        return GoogleSignIn.getLastSignedInAccount(context) != null;
    }

    public void requestScopes(@NonNull Activity activity, @NonNull List<String> scopes, @NonNull GoogleRequestScopeListener listener) {
        this.scopeListener = listener;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(activity.getApplicationContext());
        if (account != null) {
            List<Scope> wrappedScopes = new ArrayList<>();
            for (String scope : scopes) {
                Scope wrappedScope = new Scope(scope);
                boolean hasPermissions = GoogleSignIn.hasPermissions(account, wrappedScope);
                if (!hasPermissions) {
                    wrappedScopes.add(wrappedScope);
                }
            }
            if (wrappedScopes.isEmpty()) {
                listener.onSuccess();
                return;
            }
            GoogleSignIn.requestPermissions(activity, GoogleRequestCode.REQUEST_CODE_REQUEST_SCOPE, account, wrappedScopes.toArray(new Scope[0]));
        }
    }

    public void getToken(@NonNull Activity activity, GoogleGetTokenListener listener) {
        signInSilently(new GoogleSignInListener() {
            @Override
            public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                String email = googleSignInAccount.getEmail();
                if (TextUtils.isEmpty(email)) {
                    Log.e(GoogleManager.TAG, "email is null");
                    return;
                }
                if (scheduledExecutorService == null) {
                    Log.e(GoogleManager.TAG, "Can't get thread pool");
                    listener.onFailed(GoogleErrorCode.GOOGLE_GET_TOKEN_ERROR_CODE, GoogleErrorMsg.getTokenError());
                    return;
                }
                scheduledExecutorService.submit(() -> {
                    Account account = new Account(email, "com.google");
                    StringBuilder appendable = new StringBuilder();
                    for (String part : requestedScopes) {
                        appendable.append(' ').append(part);
                    }
                    String scopesStr = "oauth2:" + appendable.toString();
                    try {
                        String token = GoogleAuthUtil.getToken(activity.getApplicationContext(), account, scopesStr);
                        ThreadUtil.runOnUiThread(() -> listener.onSuccess(token));
                    } catch (IOException e) {
                        ThreadUtil.runOnUiThread(() -> listener.onFailed(GoogleErrorCode.GOOGLE_GET_TOKEN_ERROR_CODE, GoogleErrorMsg.getTokenError()));
                        Log.e(GoogleManager.TAG, "auth error msg" + e.getMessage());
                        e.printStackTrace();
                    } catch (GoogleAuthException e) {
                        ThreadUtil.runOnUiThread(() -> listener.onFailed(GoogleErrorCode.GOOGLE_GET_TOKEN_ERROR_CODE, GoogleErrorMsg.getTokenError()));
                        Log.e(GoogleManager.TAG, "auth error msg" + e.getMessage());
                        e.printStackTrace();
                    }

                });
            }

            @Override
            public void onFailed(int errorCode, String errorMsg) {
                listener.onFailed(errorCode, errorMsg);
            }
        });


    }

    public void clearAuthCache(@NonNull Context context, @NonNull GoogleClearCacheListener listener) {
        signInSilently(new GoogleSignInListener() {
            @Override
            public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                String token = googleSignInAccount.getIdToken();
                scheduledExecutorService.submit(() -> {
                    try {
                        if (token != null) {
                            GoogleAuthUtil.clearToken(context, token);
                            ThreadUtil.runOnUiThread(listener::onSuccess);
                        }
                    } catch (GoogleAuthException e) {
                        ThreadUtil.runOnUiThread(() -> listener.onFailed(GoogleErrorCode.GOOGLE_CLEAR_TOKEN_ERROR_CODE, GoogleErrorMsg.clearTokenError()));
                        Log.e(GoogleManager.TAG, "clear token error msg" + e.getMessage());
                        e.printStackTrace();
                    } catch (IOException e) {
                        ThreadUtil.runOnUiThread(() -> listener.onFailed(GoogleErrorCode.GOOGLE_CLEAR_TOKEN_ERROR_CODE, GoogleErrorMsg.clearTokenError()));
                        Log.e(GoogleManager.TAG, "clear token error msg" + e.getMessage());
                    }
                });
            }

            @Override
            public void onFailed(int errorCode, String errorMsg) {
                listener.onFailed(errorCode, errorMsg);
            }
        });
    }

    public void disconnect(@NonNull GoogleDisconnectListener listener) {
        if (signInClient != null) {
            signInClient.revokeAccess()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            listener.onSuccess();
                        } else {
                            listener.onFailed(GoogleErrorCode.GOOGLE_DISCONNECT_ERROR_CODE, GoogleErrorMsg.disconnectFailed());
                        }
                    });
        }

    }

    public synchronized void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        switch (requestCode) {
            case GoogleRequestCode.REQUEST_CODE_SIGN_IN:
                if (signInListener != null) {
                    onSignInResult(GoogleSignIn.getSignedInAccountFromIntent(data), signInListener);
                }
                if (mFragmentManager != null && mLinkGoogleFragment != null) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.remove(mLinkGoogleFragment);
                    fragmentTransaction.commit();
                }
                signInListener = null;
                break;
            case GoogleRequestCode.REQUEST_CODE_REQUEST_SCOPE:
                if (scopeListener != null) {
                    if (resultCode == Activity.RESULT_OK) {
                        scopeListener.onSuccess();
                    } else {
                        scopeListener.onFailed(GoogleErrorCode.GOOGLE_REQUEST_SCOPE_ERROR_CODE, GoogleErrorMsg.requestScopeError());
                    }
                }
                break;
            default:
                break;
        }
    }

    private void onSignInResult(@NonNull Task<GoogleSignInAccount> task, @NonNull GoogleSignInListener listener) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            listener.onSuccess(account);
            Log.i(GoogleManager.TAG, "google sign in success");
        } catch (ApiException e) {
            int statusCode = e.getStatusCode();
            Log.e(GoogleManager.TAG, "google sign in errorCode = " + statusCode);
            if (statusCode == GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                listener.onFailed(GoogleErrorCode.GOOGLE_SIGN_IN_CANCEL_ERROR_CODE, GoogleErrorMsg.signInCancel());
            } else if (statusCode == GoogleSignInStatusCodes.SIGN_IN_CURRENTLY_IN_PROGRESS) {
                Log.i(GoogleManager.TAG, "statusCode = " + statusCode);
            } else if (statusCode == CommonStatusCodes.SIGN_IN_REQUIRED) {
                listener.onFailed(GoogleErrorCode.GOOGLE_SIGN_IN_REQUIRED_ERROR_CODE, GoogleErrorMsg.signInError());
            } else if (statusCode == CommonStatusCodes.NETWORK_ERROR) {
                listener.onFailed(GoogleErrorCode.GOOGLE_SIGN_IN_NETWORK_ERROR_CODE, GoogleErrorMsg.networkError());
            } else {
                listener.onFailed(GoogleErrorCode.GOOGLE_SIGN_IN_ERROR_CODE, GoogleErrorMsg.signInFailed());
            }
            e.printStackTrace();
        } catch (RuntimeExecutionException e) {
            listener.onFailed(GoogleErrorCode.GOOGLE_SIGN_IN_ERROR_CODE, GoogleErrorMsg.signInFailed());
            e.printStackTrace();
        }
    }

}
