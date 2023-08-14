package com.appsferry.login.listener.user;

public interface LoginStatusListener {
    void beforeLogin();

    void afterLogin();

    void beforeLogout();

    void afterLogout();
}
