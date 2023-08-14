package com.appsferry.login.listener.base;

public class SDKError {

    public SDKError(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int errorCode;
    public String errorMessage;
}
