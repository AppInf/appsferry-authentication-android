package com.appsferry.login.impl;

import com.appsferry.login.listener.user.LoginStatusListener;

import java.util.LinkedHashSet;

public class LoginStatusDispatcher {
    private static volatile LoginStatusDispatcher sINSTANCE;

    private LoginStatusDispatcher() {
    }

    public static LoginStatusDispatcher getInstance() {
        if (sINSTANCE == null) {
            synchronized (LoginStatusDispatcher.class) {
                if (sINSTANCE == null) {
                    sINSTANCE = new LoginStatusDispatcher();
                }
            }
        }
        return sINSTANCE;
    }

    private LinkedHashSet<LoginStatusListener> listeners = new LinkedHashSet<>();

    public void registerLoginStatusListener(LoginStatusListener listener) {
        listeners.add(listener);
    }

    public void unRegisterLoginStatusListener(LoginStatusListener listener) {
        listeners.remove(listener);
    }

    public void dispatchBeforeLogin() {
        for (LoginStatusListener listener : listeners) {
            if (listener != null) {
                listener.beforeLogin();
            }
        }
    }

    public void dispatchAfterLogin() {
        for (LoginStatusListener listener : listeners) {
            if (listener != null) {
                listener.afterLogin();
            }
        }
    }

    public void dispatchBeforeLogout() {
        for (LoginStatusListener listener : listeners) {
            if (listener != null) {
                listener.beforeLogout();
            }
        }
    }

    public void dispatchAfterLogout() {
        for (LoginStatusListener listener : listeners) {
            if (listener != null) {
                listener.afterLogout();
            }
        }
    }
}
