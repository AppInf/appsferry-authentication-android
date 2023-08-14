package com.appsferry.login.api;

import android.content.Context;

import com.appsferry.login.entity.third.ThirdPartBindParam;
import com.appsferry.login.entity.user.UserModel;
import com.appsferry.login.entity.user.UserProfile;
import com.appsferry.login.listener.third.CheckUnbindListener;
import com.appsferry.login.listener.third.ThirdPartBindListener;
import com.appsferry.login.listener.third.ThirdPartUnBindListener;
import com.appsferry.login.listener.user.FetchUserInfoListener;
import com.appsferry.login.listener.user.UpdateUserProfileListener;

import java.util.HashMap;

public interface AccountService<T extends UserModel<? extends UserProfile>> {

    /**
     * init account component
     *
     * @param context
     */
    void init(Context context, Class<T> clazz);

    /**
     * get userinfo from memory cache
     *
     * @return
     */
    T getCacheUserModel();

    /**
     * set userinfo into memory cache
     *
     * @param t
     */
    void setCacheUserModel(T t);

    /**
     * update parcial userinfo in remote server
     *
     * @param params
     * @param listener
     */
    void updateUserInfo(HashMap<String, String> params, UpdateUserProfileListener listener);

    /**
     * get userinfo from remote server
     */
    void fetchUserModel(String targetId, FetchUserInfoListener listener);

    /**
     * clear userinfo from memory
     */
    void clearUserModel();

    /**
     * check user account
     *
     * @param type
     * @param account
     * @param areaCode
     * @param targetType
     */
    void checkAccountInfo(String type, String account, String areaCode, String targetType);

    /**
     * bind thirdparty account
     *
     * @param type
     * @param token
     * @param listener
     */
    void bindAccount(String type, String token, ThirdPartBindListener listener);

    /**
     * unbind thirdparty account
     *
     * @param type
     * @param listener
     */
    void unBindAccount(String type, ThirdPartUnBindListener listener);

    /**
     * check account status
     *
     * @param type
     * @param listener
     */
    void checkUnBind(String type, CheckUnbindListener listener);

}
