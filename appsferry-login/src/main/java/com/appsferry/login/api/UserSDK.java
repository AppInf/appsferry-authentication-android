package com.appsferry.login.api;

import android.content.Context;

import com.appsferry.login.entity.user.UserModel;
import com.appsferry.login.entity.user.UserProfile;
import com.appsferry.login.impl.AccountServiceImpl;
import com.appsferry.login.impl.LoginServiceImpl;

public class UserSDK<T extends UserModel<? extends UserProfile>> {

    private static final UserSDK userSDK = new UserSDK();

    private boolean enable;

    private UserSDK() {

    }

    public static UserSDK getInstance() {
        return userSDK;
    }

    private LoginService<T> loginService = new LoginServiceImpl<T>();
    private AccountService<T> accountService = new AccountServiceImpl<T>();

    private Context context;

    public void setPrivacyAgree(boolean enable) {
        this.enable = enable;
    }

    public void init(Context context, Class<T> clazz) {
        this.context = context;
        if (!enable) {
            throw new IllegalStateException("you need an agreement with user");
        }
        loginService.init(context, clazz);
        accountService.init(context, clazz);
    }

    public LoginService<T> getLoginService() {
        if (!enable) {
            throw new IllegalStateException("you need an agreement with user");
        }
        return loginService;
    }

    public AccountService<T> getAccountService() {
        if (!enable) {
            throw new IllegalStateException("you need an agreement with user");
        }
        return accountService;
    }

    public Context getContext() {
        return context;
    }
}
