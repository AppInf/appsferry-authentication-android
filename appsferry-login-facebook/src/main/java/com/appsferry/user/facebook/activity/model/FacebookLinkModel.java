package com.appsferry.user.facebook.activity.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class FacebookLinkModel implements Parcelable {
    private List<String> permissions;

    public FacebookLinkModel(List<String> permissions) {
        this.permissions = permissions != null ? permissions : new ArrayList<>();
    }

    private FacebookLinkModel(Parcel parcel) {
        ArrayList<String> permissionsList = new ArrayList<>();
        parcel.readStringList(permissionsList);
        this.permissions = new ArrayList<>(permissionsList);
    }

    public static final Creator<FacebookLinkModel> CREATOR = new Creator<FacebookLinkModel>() {
        @Override
        public FacebookLinkModel createFromParcel(Parcel in) {
            return new FacebookLinkModel(in);
        }

        @Override
        public FacebookLinkModel[] newArray(int size) {
            return new FacebookLinkModel[size];
        }
    };

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(new ArrayList<>(permissions));
    }
}
