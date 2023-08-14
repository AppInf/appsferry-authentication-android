package com.appsferry.login.entity.third;

import com.appsferry.login.entity.user.BindInfo;
import com.google.gson.annotations.SerializedName;

public class ThirdPartBindModel {

    @SerializedName("bind_info")
    public BindInfo[] bindInfo;
}
