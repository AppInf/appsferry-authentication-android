package com.appsferry.login.entity.third;

import com.appsferry.login.entity.user.BindInfo;
import com.google.gson.annotations.SerializedName;

public class ThirdPartUnBindModel {

    @SerializedName("bind_info")
    public BindInfo[] bindInfo;
}
