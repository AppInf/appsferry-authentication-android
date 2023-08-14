package com.appsferry.login.entity.third;

import com.google.gson.annotations.SerializedName;

public class CheckUnbindModel {

    @SerializedName("result")
    public boolean canUnbind;

    public String msg;

}
