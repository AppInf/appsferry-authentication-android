package com.appsferry.login.entity.user;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class UserModel<E extends UserProfile> {
    @Nullable
    @SerializedName("profile")
    public E profile;

    @SerializedName("bind_info")
    @Nullable
    public BindInfo[] bindInfo;

    @Nullable
    public AuditInfo audit;

    public long uid = 0;

    public String sid = "";

    public String token;

    public int visitor;
}
