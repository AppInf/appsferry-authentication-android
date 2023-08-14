package com.appsferry.login.entity.third;

import com.google.gson.annotations.SerializedName;

public class ThirdPartBindParam {

    public String platform;

    /**
     * google
     */
    public String idToken;

    @SerializedName("access_token")
    public String accessToken;

    @SerializedName("code")
    public String code;

    @SerializedName("external_id")
    public String externalId;

}
