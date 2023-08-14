package com.appsferry.login.entity.third;

import com.google.gson.annotations.SerializedName;

public class ThirdPartLoginParam {

    @SerializedName("plat")
    public String platform;

    @SerializedName("idtoken")
    public String idToken;

    @SerializedName("access_token")
    public String accessToken;

    @SerializedName("app_type")
    public String appType;

    @SerializedName("code")
    public String code;

    @SerializedName("external_id")
    public String externalId;

    @SerializedName("nick_name")
    public String nickName;

    @SerializedName("portrait")
    public String portrait;
}
