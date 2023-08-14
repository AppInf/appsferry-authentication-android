package com.appsferry.user.facebook.activity.model;

public class FacebookAuthResult {
    private int code;
    private String msg;
    private String token;
    private String fbMsg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFbMsg() {
        return fbMsg;
    }

    public void setFbMsg(String fbMsg) {
        this.fbMsg = fbMsg;
    }
}
