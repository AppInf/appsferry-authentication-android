package com.appsferry.user.google.handlers;

import static com.appsferry.login.api.Constants.LOGIN_TYPE_GOOGLE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import com.appsferry.login.api.Constants;
import com.appsferry.login.api.UserSDK;
import com.appsferry.login.entity.user.UserModel;
import com.appsferry.login.entity.user.UserProfile;
import com.appsferry.login.listener.base.SDKError;
import com.appsferry.login.listener.third.ThirdPartBindListener;
import com.appsferry.login.listener.third.ThirdPartLoginListener;
import com.appsferry.login.listener.third.ThirdPartUnBindListener;
import com.appsferry.user.google.GoogleManager;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.appsferry.user.google.listener.GoogleAuthListener;
import com.appsferry.user.google.listener.GoogleBindListener;
import com.appsferry.user.google.listener.GoogleCheckUnbindListener;
import com.appsferry.user.google.listener.GoogleClearCacheListener;
import com.appsferry.user.google.listener.GoogleDisconnectListener;
import com.appsferry.user.google.listener.GoogleGetTokenListener;
import com.appsferry.user.google.listener.GoogleLoginListener;
import com.appsferry.user.google.listener.GoogleRequestScopeListener;
import com.appsferry.user.google.listener.GoogleSignInListener;
import com.appsferry.user.google.listener.GoogleSignOutListener;
import com.appsferry.user.google.listener.GoogleUnBindListener;
import com.appsferry.user.google.param.GoogleSignInOptions;
import com.appsferry.user.google.util.GoogleAssert;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class GoogleAuthHandler {
    private static final GoogleAuthHandler INSTANCE = new GoogleAuthHandler();
    private GoogleApiHandler googleApiHandler;
    private final Map<String, Object> mRequestIdMap = new HashMap<>();

    private GoogleAuthHandler() {
    }

    public static GoogleAuthHandler getInstance() {
        return INSTANCE;
    }

    public void init(@NonNull Context context, @NonNull GoogleSignInOptions options) {
        if (googleApiHandler == null) {
            googleApiHandler = new GoogleApiHandler();
        }
        googleApiHandler.init(context, options);
    }

    public GoogleSignInClient getSignInClient() {
        Log.i(GoogleManager.TAG, "google signIn");
        GoogleAssert.googleAssert(googleApiHandler);
        if (googleApiHandler != null) {
            return googleApiHandler.getSignInClient();
        }
        return null;
    }

    public <T extends UserModel<? extends UserProfile>> void loginWithGoogle(@NonNull Activity activity, boolean isSilently, @NonNull GoogleLoginListener<T> listener) {
        Log.i(GoogleManager.TAG, "loginWithGoogle");
        googleAuth(activity, isSilently, new GoogleAuthListener() {
            @Override
            public void onSuccess(GoogleSignInAccount account) {
                loginUser(account.getIdToken(), listener);

            }

            @Override
            public void onFailed(int errorCode, String errorMsg) {
                listener.onFailed(errorCode, errorMsg);
            }
        });

    }

    private <T extends UserModel<? extends UserProfile>> void loginUser(String token, GoogleLoginListener<T> listener) {
        UserSDK.getInstance().getLoginService().loginByThirdparty(Constants.LOGIN_TYPE_GOOGLE, token, null, new ThirdPartLoginListener<T>() {
            @Override
            public void onNewData(T entity) {
                if (listener != null) {
                    listener.onSuccess(entity);
                }
            }

            @Override
            public void onError(SDKError error) {
                if (listener != null) {
                    listener.onFailed(error.errorCode, error.errorMessage);
                }
            }
        });
    }

    public void bindWithGoogle(@NonNull Activity activity, boolean isSilently, @NonNull GoogleBindListener listener) {
        Log.i(GoogleManager.TAG, "bindWithGoogle");
        googleAuth(activity, isSilently, new GoogleAuthListener() {
            @Override
            public void onSuccess(GoogleSignInAccount account) {
                bindByThirdParty(account.getIdToken(), listener);
            }

            @Override
            public void onFailed(int errorCode, String errorMsg) {
                listener.onFailed(errorCode, errorMsg);
            }
        });

    }

    public void unBindWithGoogle(@NonNull GoogleUnBindListener listener) {
        Log.i(GoogleManager.TAG, "unBindWithGoogle");
        UserSDK.getInstance().getAccountService().unBindAccount(LOGIN_TYPE_GOOGLE, new ThirdPartUnBindListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onError(SDKError error) {
                listener.onFailed(error.errorCode, error.errorMessage);
            }
        });
    }

    public void checkUnbind(@NonNull GoogleCheckUnbindListener listener) {
        Log.i(GoogleManager.TAG, "google -> checkUnbind");
    }

    private void googleAuth(@NonNull Activity activity, boolean isSilently, GoogleAuthListener listener) {
        Log.i(GoogleManager.TAG, "googleAuth");
        GoogleAssert.googleAssert(googleApiHandler);
        if (isSilently) {
            signInSilently(new GoogleSignInListener() {
                @Override
                public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                    listener.onSuccess(googleSignInAccount);

                }

                @Override
                public void onFailed(int errorCode, String errorMsg) {
                    listener.onFailed(errorCode, errorMsg);

                }
            });
        } else {
            signOut(new GoogleSignOutListener() {
                @Override
                public void onSuccess() {
                    signIn(activity, new GoogleSignInListener() {
                        @Override
                        public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                            listener.onSuccess(googleSignInAccount);
                        }

                        @Override
                        public void onFailed(int errorCode, String errorMsg) {
                            listener.onFailed(errorCode, errorMsg);
                        }
                    });
                }

                @Override
                public void onFailed(int errorCode, String errorMsg) {
                    listener.onFailed(errorCode, errorMsg);
                }
            });

        }
    }

    private void bindByThirdParty(String token, GoogleBindListener listener) {
        Log.i(GoogleManager.TAG, "bindByThirdParty");
        UserSDK.getInstance().getAccountService().bindAccount(LOGIN_TYPE_GOOGLE, token, new ThirdPartBindListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onError(SDKError error) {
                listener.onFailed(error.errorCode, error.errorMessage);
            }
        });

    }

    public void signInSilently(@NonNull GoogleSignInListener listener) {
        Log.i(GoogleManager.TAG, "signInSilently");
        GoogleAssert.googleAssert(googleApiHandler);
        if (googleApiHandler != null) {
            googleApiHandler.signInSilently(listener);
        }
    }

    public void signIn(@NonNull Activity activity, GoogleSignInListener listener) {
        Log.i(GoogleManager.TAG, "signIn");
        GoogleAssert.googleAssert(googleApiHandler);
        if (googleApiHandler != null) {
            googleApiHandler.signIn(activity, listener);
        }
    }

    public void signOut(@NonNull GoogleSignOutListener listener) {
        Log.i(GoogleManager.TAG, "signOut");
        GoogleAssert.googleAssert(googleApiHandler);
        if (googleApiHandler != null) {
            googleApiHandler.signOut(listener);
        }
    }

    public boolean isSignedIn(@NonNull Context context) {
        Log.i(GoogleManager.TAG, "isSignedIn");
        GoogleAssert.googleAssert(googleApiHandler);
        if (googleApiHandler != null) {
            return googleApiHandler.isSignedIn(context);
        }
        return false;
    }

    public void requestScopes(@NonNull Activity activity, @NonNull List<String> scopes, @NonNull GoogleRequestScopeListener listener) {
        Log.i(GoogleManager.TAG, "requestScopes");
        GoogleAssert.googleAssert(googleApiHandler);
        if (googleApiHandler != null) {
            googleApiHandler.requestScopes(activity, scopes, listener);
        }
    }

    public void getToken(@NonNull Activity activity, GoogleGetTokenListener listener) {
        Log.i(GoogleManager.TAG, "getToken");
        GoogleAssert.googleAssert(googleApiHandler);
        if (googleApiHandler != null) {
            googleApiHandler.getToken(activity, listener);
        }

    }

    public void clearAuthCache(@NonNull Context context, @NonNull GoogleClearCacheListener listener) {
        Log.i(GoogleManager.TAG, "clearAuthCache");
        GoogleAssert.googleAssert(googleApiHandler);
        if (googleApiHandler != null) {
            googleApiHandler.clearAuthCache(context, listener);
        }

    }

    public void disconnect(@NonNull GoogleDisconnectListener listener) {
        Log.i(GoogleManager.TAG, "disconnect");
        GoogleAssert.googleAssert(googleApiHandler);
        if (googleApiHandler != null) {
            googleApiHandler.disconnect(listener);
        }

    }

    public void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        Log.i(GoogleManager.TAG, "onActivityResult");
        GoogleAssert.googleAssert(googleApiHandler);
        if (googleApiHandler != null) {
            googleApiHandler.onActivityResult(requestCode, resultCode, data);
        }
    }

}
