package com.appsferry.login.impl;

import static com.appsferry.login.api.Constants.LOGIN_TYPE_FACEBOOK;
import static com.appsferry.login.api.Constants.LOGIN_TYPE_GOOGLE;
import static com.appsferry.login.api.Constants.LOGIN_TYPE_SNAPCHAT;
import static com.appsferry.login.api.Constants.LOGIN_TYPE_TIKTOK;

import android.content.Context;
import android.util.Log;

import com.appsferry.core.gson.AfGson;
import com.appsferry.core.network.AfResponse;
import com.appsferry.core.network.RetrofitAgent;
import com.appsferry.core.parameters.Parameters;
import com.appsferry.core.storage.FileStorage;
import com.appsferry.login.api.AccountService;
import com.appsferry.login.api.Constants;
import com.appsferry.login.api.ErrorCode;
import com.appsferry.login.api.ErrorMsg;
import com.appsferry.login.api.LoginService;
import com.appsferry.login.entity.third.CheckUnbindModel;
import com.appsferry.login.entity.third.ThirdPartBindModel;
import com.appsferry.login.entity.third.ThirdPartBindParam;
import com.appsferry.login.entity.third.ThirdPartUnBindModel;
import com.appsferry.login.entity.third.ThirdPartUnBindParam;
import com.appsferry.login.entity.user.AccountCheckModel;
import com.appsferry.login.entity.user.AccountCheckParam;
import com.appsferry.login.entity.user.UserModel;
import com.appsferry.login.entity.user.UserProfile;
import com.appsferry.login.api.UserSDK;
import com.appsferry.login.listener.third.CheckUnbindListener;
import com.appsferry.login.listener.third.ThirdPartBindListener;
import com.appsferry.login.listener.third.ThirdPartUnBindListener;
import com.appsferry.login.listener.user.FetchUserInfoListener;
import com.appsferry.login.listener.base.SDKError;
import com.appsferry.login.listener.user.UpdateUserProfileListener;
import com.appsferry.login.utils.EncryptUitls;
import com.appsferry.login.utils.LocationUtils;
import com.appsferry.login.utils.MD5UtilsCompat;
import com.appsferry.login.utils.UserInfoUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class AccountServiceImpl<T extends UserModel<? extends UserProfile>> implements AccountService<T> {

    private T userModel;

    private Class<T> userModelClass;

    @Override
    public void init(Context context, Class<T> clazz) {
        this.userModelClass = clazz;
        LoginService loginService = UserSDK.getInstance().getLoginService();
        ;
        String userInfoKey = "user-" + loginService.getUserId();
        String cacheUserInfo = FileStorage.Companion.getENCRYPT_DEFAULT().getString(MD5UtilsCompat.encode(userInfoKey), "");
        if (loginService.isLogin()) {
            userModel = AfGson.Companion.get().fromJson(cacheUserInfo, clazz);
        }
    }

    @Override
    public T getCacheUserModel() {
        return userModel;
    }

    @Override
    public void setCacheUserModel(T t) {
        this.userModel = t;
    }

    @Override
    public void updateUserInfo(HashMap<String, String> params, UpdateUserProfileListener listener) {
        if (params != null) {
            if (params.containsKey("uid")) {
                params.remove("uid");
            }
            if (params.containsKey("age")) {
                params.remove("age");
            }
            RetrofitAgent.getINSTANCE().bizService(IAccountService.class)
                    .updateUserInfo(UserInfoUtils.getAppid(), UserInfoUtils.getCv(),
                            Parameters.getInstance().get("uid"), Parameters.getInstance().get("sid"), params)
                    .enqueue(new Callback<AfResponse<JsonElement>>() {
                        @Override
                        public void onResponse(Call<AfResponse<JsonElement>> call, Response<AfResponse<JsonElement>> response) {
                            if (response == null) {
                                if (listener != null) {
                                    listener.onError(new SDKError(ErrorCode.NETWORK_ERROR, ErrorMsg.NETWORK_ERROR()));
                                }
                                return;
                            }
                            AfResponse<JsonElement> afResponse = response.body();
                            if (afResponse == null) {
                                if (listener != null) {
                                    listener.onError(new SDKError(ErrorCode.SERVER_DATA_ERROR, ErrorMsg.SERVER_DATA_ERROR()));
                                }
                                return;
                            }
                            if (!afResponse.getSuccess()) {
                                if (listener != null) {
                                    listener.onError(new SDKError(afResponse.getCode(), afResponse.getErrorMsg()));
                                }
                                return;
                            }
                            if (afResponse.getData() == null) {
                                if (listener != null) {
                                    listener.onError(new SDKError(ErrorCode.SERVER_DATA_ERROR, ErrorMsg.SERVER_DATA_ERROR()));
                                }
                                return;
                            }
                            JsonElement newJsonElement = afResponse.getData();
                            String localUserInfo = AfGson.Companion.get().toJson(userModel);
                            JsonElement oldJsonElement = JsonParser.parseString(localUserInfo);
                            mergeUpdate(oldJsonElement, newJsonElement);
                            String newData = AfGson.Companion.get().toJson(oldJsonElement);
                            userModel = AfGson.Companion.get().fromJson(newData, userModelClass);
                            UserInfoUtils.setUserInfoToFile(String.valueOf(userModel.uid), newData);
                            if (listener != null) {
                                listener.onSuccess();
                            }
                        }

                        @Override
                        public void onFailure(Call<AfResponse<JsonElement>> call, Throwable t) {
                            if (listener != null) {
                                listener.onError(new SDKError(ErrorCode.SDK_ERROR, ErrorMsg.SDK_ERROR()));
                            }
                        }
                    });
        }
    }

    private void mergeUpdate(JsonElement oldData, JsonElement newData) {
        if (oldData != null && newData != null) {
            try {
                //update profile
                JsonElement profile = newData.getAsJsonObject().get("profile");
                if (profile != null && !profile.isJsonNull()) {
                    oldData.getAsJsonObject().add("profile", profile);
                }
                //update audit
                JsonElement audit = newData.getAsJsonObject().get("audit");
                if (audit != null && !audit.isJsonNull()) {
                    oldData.getAsJsonObject().add("audit", audit);
                }
                //update bind_info
                JsonElement bindInfo = newData.getAsJsonObject().get("bind_info");
                if (bindInfo != null && !bindInfo.isJsonNull()) {
                    oldData.getAsJsonObject().add("bind_info", bindInfo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void fetchUserModel(String targetId, FetchUserInfoListener listener) {
        RetrofitAgent.getINSTANCE().bizService(IAccountService.class).fetchUserModel(
                        UserInfoUtils.getAppid(), UserInfoUtils.getCv(),
                        Parameters.getInstance().get("uid"), Parameters.getInstance().get("sid"), targetId)
                .enqueue(new Callback<AfResponse<JsonElement>>() {
                    @Override
                    public void onResponse(Call<AfResponse<JsonElement>> call, Response<AfResponse<JsonElement>> response) {
                        if (response == null) {
                            if (listener != null) {
                                listener.onError(new SDKError(ErrorCode.NETWORK_ERROR, ErrorMsg.NETWORK_ERROR()));
                            }
                            return;
                        }
                        AfResponse<JsonElement> afResponse = response.body();
                        if (afResponse == null) {
                            if (listener != null) {
                                listener.onError(new SDKError(ErrorCode.SERVER_DATA_ERROR, ErrorMsg.SERVER_DATA_ERROR()));
                            }
                            return;
                        }
                        if (!afResponse.getSuccess()) {
                            if (listener != null) {
                                listener.onError(new SDKError(afResponse.getCode(), afResponse.getErrorMsg()));
                            }
                            return;
                        }
                        if (afResponse.getData() == null) {
                            if (listener != null) {
                                listener.onError(new SDKError(ErrorCode.SERVER_DATA_ERROR, ErrorMsg.SERVER_DATA_ERROR()));
                            }
                            return;
                        }
                        JsonElement newJsonElement = afResponse.getData();
                        String newData = AfGson.Companion.get().toJson(newJsonElement);
                        userModel = AfGson.Companion.get().fromJson(newData, userModelClass);
                        if (listener != null) {
                            listener.onNewData(userModel);
                        }
                    }

                    @Override
                    public void onFailure(Call<AfResponse<JsonElement>> call, Throwable t) {
                        if (listener != null) {
                            listener.onError(new SDKError(ErrorCode.SDK_ERROR, ErrorMsg.SDK_ERROR()));
                        }
                    }
                });
    }

    @Override
    public void clearUserModel() {
        this.userModel = null;
    }

    @Override
    public void checkAccountInfo(String type, String account, String areaCode, String targetType) {
        String codeRevised = LocationUtils.filterAreaCode(areaCode);
        String encryptPhone = EncryptUitls.encryptPhone(codeRevised, account);

        AccountCheckParam param = new AccountCheckParam();
        param.type = type;
        param.account = encryptPhone;
        param.area_code = areaCode;
        param.target_type = targetType;
        param.vcode = Constants.LOGIN_VCD;

        RetrofitAgent.getINSTANCE().bizService(IAccountService.class)
                .checkAccountInfo(UserInfoUtils.getAppid(), UserInfoUtils.getCv(),
                        Parameters.getInstance().get("uid"), Parameters.getInstance().get("sid"), param)
                .enqueue(new Callback<AfResponse<AccountCheckModel>>() {
                    @Override
                    public void onResponse(Call<AfResponse<AccountCheckModel>> call, Response<AfResponse<AccountCheckModel>> response) {
                    }

                    @Override
                    public void onFailure(Call<AfResponse<AccountCheckModel>> call, Throwable t) {
                    }
                });
    }

    @Override
    public void bindAccount(String type, String token, ThirdPartBindListener listener) {
        ThirdPartBindParam param = new ThirdPartBindParam();
        switch (type) {
            case LOGIN_TYPE_GOOGLE:
                param.idToken = token;
                param.platform = LOGIN_TYPE_GOOGLE;
                break;
            case LOGIN_TYPE_FACEBOOK:
                param.accessToken = token;
                param.platform = LOGIN_TYPE_FACEBOOK;
                break;
            case LOGIN_TYPE_TIKTOK:
                param.code = token;
                param.platform = LOGIN_TYPE_TIKTOK;
                break;
            case LOGIN_TYPE_SNAPCHAT:
                param.externalId = token;
                param.platform = LOGIN_TYPE_SNAPCHAT;
                break;
        }
        RetrofitAgent.getINSTANCE().bizService(IAccountService.class).bindAccount(
                UserInfoUtils.getAppid(), UserInfoUtils.getCv()
                , String.valueOf(userModel.uid), userModel.sid, param).enqueue(new Callback<AfResponse<ThirdPartBindModel>>() {
            @Override
            public void onResponse(Call<AfResponse<ThirdPartBindModel>> call, Response<AfResponse<ThirdPartBindModel>> response) {
                if (response == null) {
                    if (listener != null) {
                        listener.onError(new SDKError(ErrorCode.NETWORK_ERROR, ErrorMsg.NETWORK_ERROR()));
                    }
                    return;
                }
                AfResponse<ThirdPartBindModel> afResponse = response.body();
                if (afResponse == null) {
                    if (listener != null) {
                        listener.onError(new SDKError(ErrorCode.SERVER_DATA_ERROR, ErrorMsg.SERVER_DATA_ERROR()));
                    }
                    return;
                }
                if (!afResponse.getSuccess()) {
                    if (listener != null) {
                        listener.onError(new SDKError(afResponse.getCode(), afResponse.getErrorMsg()));
                    }
                    return;
                }
                if (afResponse.getData() == null) {
                    if (listener != null) {
                        listener.onError(new SDKError(ErrorCode.SERVER_DATA_ERROR, ErrorMsg.SERVER_DATA_ERROR()));
                    }
                    return;
                }
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onFailure(Call<AfResponse<ThirdPartBindModel>> call, Throwable t) {
                if (listener != null) {
                    listener.onError(new SDKError(ErrorCode.SDK_ERROR, ErrorMsg.SDK_ERROR()));
                }
            }
        });
    }

    @Override
    public void unBindAccount(String type, ThirdPartUnBindListener listener) {
        ThirdPartUnBindParam param = new ThirdPartUnBindParam();
        switch (type) {
            case LOGIN_TYPE_GOOGLE:
                param.plat = LOGIN_TYPE_GOOGLE;
                break;
            case LOGIN_TYPE_FACEBOOK:
                param.plat = LOGIN_TYPE_FACEBOOK;
                break;
            case LOGIN_TYPE_TIKTOK:
                param.plat = LOGIN_TYPE_TIKTOK;
                break;
            case LOGIN_TYPE_SNAPCHAT:
                param.plat = LOGIN_TYPE_SNAPCHAT;
                break;
        }
        RetrofitAgent.getINSTANCE().bizService(IAccountService.class).unBindAccount(
                        UserInfoUtils.getAppid(), UserInfoUtils.getCv()
                        , String.valueOf(userModel.uid), userModel.sid, param)
                .enqueue(new Callback<AfResponse<ThirdPartUnBindModel>>() {
                    @Override
                    public void onResponse(Call<AfResponse<ThirdPartUnBindModel>> call, Response<AfResponse<ThirdPartUnBindModel>> response) {
                        if (response == null) {
                            if (listener != null) {
                                listener.onError(new SDKError(ErrorCode.NETWORK_ERROR, ErrorMsg.NETWORK_ERROR()));
                            }
                            return;
                        }
                        AfResponse<ThirdPartUnBindModel> afResponse = response.body();
                        if (afResponse == null) {
                            if (listener != null) {
                                listener.onError(new SDKError(ErrorCode.SERVER_DATA_ERROR, ErrorMsg.SERVER_DATA_ERROR()));
                            }
                            return;
                        }
                        if (!afResponse.getSuccess()) {
                            if (listener != null) {
                                listener.onError(new SDKError(afResponse.getCode(), afResponse.getErrorMsg()));
                            }
                            return;
                        }
                        if (afResponse.getData() == null) {
                            if (listener != null) {
                                listener.onError(new SDKError(ErrorCode.SERVER_DATA_ERROR, ErrorMsg.SERVER_DATA_ERROR()));
                            }
                            return;
                        }
                        if (listener != null) {
                            listener.onSuccess();
                        }
                    }

                    @Override
                    public void onFailure(Call<AfResponse<ThirdPartUnBindModel>> call, Throwable t) {
                        if (listener != null) {
                            listener.onError(new SDKError(ErrorCode.SDK_ERROR, ErrorMsg.SDK_ERROR()));
                        }
                    }
                });
    }

    @Override
    public void checkUnBind(String type, CheckUnbindListener listener) {
        switch (type) {
            case LOGIN_TYPE_GOOGLE:
                RetrofitAgent.getINSTANCE().bizService(IAccountService.class)
                        .checkUnBindAccount(UserInfoUtils.getAppid(), UserInfoUtils.getCv()
                                , String.valueOf(userModel.uid), userModel.sid, LOGIN_TYPE_GOOGLE)
                        .enqueue(new Callback<AfResponse<CheckUnbindModel>>() {
                            @Override
                            public void onResponse(Call<AfResponse<CheckUnbindModel>> call, Response<AfResponse<CheckUnbindModel>> response) {
                                if (response == null) {
                                    if (listener != null) {
                                        listener.onError(new SDKError(ErrorCode.NETWORK_ERROR, ErrorMsg.NETWORK_ERROR()));
                                    }
                                    return;
                                }
                                AfResponse<CheckUnbindModel> afResponse = response.body();
                                if (afResponse == null) {
                                    if (listener != null) {
                                        listener.onError(new SDKError(ErrorCode.SERVER_DATA_ERROR, ErrorMsg.SERVER_DATA_ERROR()));
                                    }
                                    return;
                                }
                                if (!afResponse.getSuccess()) {
                                    if (listener != null) {
                                        listener.onError(new SDKError(afResponse.getCode(), afResponse.getErrorMsg()));
                                    }
                                    return;
                                }
                                if (afResponse.getData() == null) {
                                    if (listener != null) {
                                        listener.onError(new SDKError(ErrorCode.SERVER_DATA_ERROR, ErrorMsg.SERVER_DATA_ERROR()));
                                    }
                                    return;
                                }
                                if (listener != null) {
                                    listener.onNewData(afResponse.getData());
                                }
                            }

                            @Override
                            public void onFailure(Call<AfResponse<CheckUnbindModel>> call, Throwable t) {
                                if (listener != null) {
                                    listener.onError(new SDKError(ErrorCode.SDK_ERROR, ErrorMsg.SDK_ERROR()));
                                }
                            }
                        });
                break;
        }
    }

    interface IAccountService {
        @POST("api/v2/users/profile/update")
        Call<AfResponse<JsonElement>>
        updateUserInfo(@Query("ik_appid") String appid, @Query("cv") String cv, @Query("uid") String uid, @Query("sid") String sid, @Body HashMap<String, String> body);

        @GET("api/v1/users/profile/get")
        Call<AfResponse<JsonElement>> fetchUserModel(@Query("ik_appid") String appid, @Query("cv") String cv, @Query("uid") String uid, @Query("sid") String sid, @Query("id") String id);

        @POST("api/v1/user/account/status/check")
        Call<AfResponse<AccountCheckModel>> checkAccountInfo(@Query("ik_appid") String appid, @Query("cv") String cv, @Query("uid") String uid, @Query("sid") String sid, @Body AccountCheckParam param);

        @POST("api/user/account/thirdpart/bind")
        Call<AfResponse<ThirdPartBindModel>> bindAccount(@Query("ik_appid") String appid, @Query("cv") String cv, @Query("uid") String uid, @Query("sid") String sid, @Body ThirdPartBindParam param);

        @POST("api/v1/user/account/unbind")
        Call<AfResponse<ThirdPartUnBindModel>> unBindAccount(@Query("ik_appid") String appid, @Query("cv") String cv, @Query("uid") String uid, @Query("sid") String sid, @Body ThirdPartUnBindParam param);

        @GET("api/user/account/check_unbind")
        Call<AfResponse<CheckUnbindModel>> checkUnBindAccount(@Query("ik_appid") String appid, @Query("cv") String cv, @Query("uid") String uid, @Query("sid") String sid, @Query("plat") String platform);
    }
}
