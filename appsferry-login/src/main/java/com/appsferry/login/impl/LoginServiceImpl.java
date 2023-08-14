package com.appsferry.login.impl;

import static com.appsferry.login.api.Constants.LOGIN_APP_TYPE;
import static com.appsferry.login.api.Constants.LOGIN_TYPE_FACEBOOK;
import static com.appsferry.login.api.Constants.LOGIN_TYPE_GOOGLE;
import static com.appsferry.login.api.Constants.LOGIN_TYPE_SNAPCHAT;
import static com.appsferry.login.api.Constants.LOGIN_TYPE_TIKTOK;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.appsferry.core.gson.AfGson;
import com.appsferry.core.network.AfResponse;
import com.appsferry.core.network.RetrofitAgent;
import com.appsferry.core.parameters.Parameters;
import com.appsferry.core.parameters.observe.ParameterObserver;
import com.appsferry.login.api.Constants;
import com.appsferry.login.api.ErrorCode;
import com.appsferry.login.api.ErrorMsg;
import com.appsferry.login.api.LoginService;
import com.appsferry.login.entity.email.EmailCodeLoginSendCodeModel;
import com.appsferry.login.entity.email.EmailCodeLoginSendCodeParam;
import com.appsferry.login.entity.email.EmailCodeLoginParam;
import com.appsferry.login.entity.email.EmailPwdLoginParam;
import com.appsferry.login.entity.email.EmailPwdLoginSendCodeModel;
import com.appsferry.login.entity.email.EmailPwdLoginSendCodeParam;
import com.appsferry.login.entity.email.EmailPwdLoginSetPasswordParam;
import com.appsferry.login.entity.email.EmailPwdLoginVerifyCodeModel;
import com.appsferry.login.entity.email.EmailPwdLoginVerifyCodeParam;
import com.appsferry.login.entity.phone.PhonePwdLoginParam;
import com.appsferry.login.entity.phone.PhonePwdLoginSendCodeModel;
import com.appsferry.login.entity.phone.PhonePwdLoginSetPasswordParam;
import com.appsferry.login.entity.phone.PhonePwdLoginVerifyCodeModel;
import com.appsferry.login.entity.phone.PhonePwdLoginVerifyCodeParam;
import com.appsferry.login.entity.third.ThirdPartLoginParam;
import com.appsferry.login.entity.user.UserModel;
import com.appsferry.login.entity.user.UserProfile;
import com.appsferry.login.api.UserSDK;
import com.appsferry.login.entity.phone.PhoneCodeLoginSendCodeModel;
import com.appsferry.login.entity.phone.PhoneCodeLoginSendCodeParam;
import com.appsferry.login.entity.phone.PhoneCodeLoginParam;
import com.appsferry.login.entity.session.SessionCheckModel;
import com.appsferry.login.entity.phone.PhonePwdLoginSendCodeParam;
import com.appsferry.login.entity.visitor.VisitorLoginParam;
import com.appsferry.login.listener.email.EmailCodeVerifyListener;
import com.appsferry.login.listener.email.EmailLoginCodeListener;
import com.appsferry.login.listener.email.EmailLoginListener;
import com.appsferry.login.listener.email.EmailPwdListener;
import com.appsferry.login.listener.email.EmailPwdLoginCodeListener;
import com.appsferry.login.listener.email.EmailPwdLoginListener;
import com.appsferry.login.listener.phone.PhoneCodeVerifyListener;
import com.appsferry.login.listener.phone.PhonePwdListener;
import com.appsferry.login.listener.phone.PhonePwdLoginCodeListener;
import com.appsferry.login.listener.phone.PhonePwdLoginListener;
import com.appsferry.login.listener.session.SessionCheckListener;
import com.appsferry.login.listener.third.ThirdPartLoginListener;
import com.appsferry.login.listener.user.LoginStatusListener;
import com.appsferry.login.listener.user.LogoffListener;
import com.appsferry.login.listener.user.LogoutListener;
import com.appsferry.login.listener.phone.PhoneLoginCodeListener;
import com.appsferry.login.listener.phone.PhoneLoginListener;
import com.appsferry.login.listener.base.SDKError;
import com.appsferry.login.listener.visitor.VisitorLoginListener;
import com.appsferry.login.utils.EncryptUitls;
import com.appsferry.login.utils.LocationUtils;
import com.appsferry.login.utils.UserInfoUtils;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public class LoginServiceImpl<T extends UserModel<? extends UserProfile>> implements LoginService<T> {

    private T mLoginInfo;

    private Class<T> userModelClass;

    private static final String CODE_SOURCE_PHONE_LOGIN = "code_source_phone_login";

    private static final String CODE_SOURCE_PHONE_VERIFY = "code_source_phone_verify";

    private static final String CODE_SOURCE_EMAIL_LOGIN = "code_source_email_login";

    public static final String CODE_SOURCE_EMAIL_VERIFY = "code_source_email_verify";

    private static final String PHONE_ACCOUNT_TOKEN = "phone_account_token";

    private static final String EMAIL_ACCOUNT_TOKEN = "email_account_token";

    private final Map<String, String> mRequestIds = new HashMap<>();

    @Override
    public void init(Context context, Class<T> clazz) {
        this.userModelClass = clazz;
        String curUser = UserInfoUtils.getCurrentUidFromFile();
        if (TextUtils.isEmpty(curUser)) return;
        String cacheUserInfo = UserInfoUtils.getUserInfoFromFile(curUser);
        if (TextUtils.isEmpty(cacheUserInfo)) return;
        mLoginInfo = AfGson.Companion.get().fromJson(cacheUserInfo, clazz);
        if (mLoginInfo != null) {
            UserInfoUtils.setUidSid(String.valueOf(mLoginInfo.uid), mLoginInfo.sid);
            if (isLogin()) checkSession(null);
        }
    }

    @Override
    public long getUserId() {
        if (mLoginInfo != null) {
            return mLoginInfo.uid;
        }
        return 0;
    }

    @Override
    public String getSession() {
        if (mLoginInfo != null) {
            return mLoginInfo.sid;
        }
        return "";
    }

    @Override
    public void checkSession(SessionCheckListener listener) {
        RetrofitAgent.getINSTANCE().bizService(ILoginService.class).checkSession(UserInfoUtils.getAppid(), UserInfoUtils.getCv(), String.valueOf(getUserId()), getSession()).enqueue(new Callback<AfResponse<SessionCheckModel>>() {
            @Override
            public void onResponse(Call<AfResponse<SessionCheckModel>> call, retrofit2.Response<AfResponse<SessionCheckModel>> response) {
                if (response == null) {
                    if (listener != null) {
                        listener.onError(new SDKError(ErrorCode.NETWORK_ERROR, ErrorMsg.NETWORK_ERROR()));
                    }
                    return;
                }
                AfResponse<SessionCheckModel> afResponse = response.body();
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
            public void onFailure(Call<AfResponse<SessionCheckModel>> call, Throwable t) {
                if (listener != null) {
                    listener.onError(new SDKError(ErrorCode.SDK_ERROR, ErrorMsg.SDK_ERROR()));
                }
            }
        });
    }

    @Override
    public boolean isLogin() {
        if (mLoginInfo != null && mLoginInfo.uid != 0 && !TextUtils.isEmpty(mLoginInfo.sid)) {
            return true;
        }
        return false;
    }

    @Override
    public void logoff(LogoffListener listener) {
        if (!isLogin()) {
            if (listener != null) {
                listener.onSuccess();
            }
            return;
        }
        HashMap<String, String> param = new HashMap<>();
        param.put("app_type", LOGIN_APP_TYPE);
        RetrofitAgent.getINSTANCE().bizService(ILoginService.class).logoff(UserInfoUtils.getAppid(), UserInfoUtils.getCv(), String.valueOf(mLoginInfo.uid), mLoginInfo.sid, param).enqueue(new Callback<AfResponse>() {
            @Override
            public void onResponse(Call<AfResponse> call, Response<AfResponse> response) {
                if (response == null) {
                    if (listener != null) {
                        listener.onError(new SDKError(ErrorCode.NETWORK_ERROR, ErrorMsg.NETWORK_ERROR()));
                    }
                    return;
                }
                AfResponse afResponse = response.body();
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
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onFailure(Call<AfResponse> call, Throwable t) {
                if (listener != null) {
                    listener.onError(new SDKError(ErrorCode.SDK_ERROR, ErrorMsg.SDK_ERROR()));
                }
            }
        });

        if (mLoginInfo != null) {
            UserInfoUtils.setUidSid("", "");
            UserInfoUtils.setUserInfoToFile(String.valueOf(mLoginInfo.uid), "");
            UserInfoUtils.setCurrentUidFromFile("");
            UserSDK.getInstance().getAccountService().clearUserModel();
            mLoginInfo = null;
        }
    }

    @Override
    public void logout(LogoutListener listener) {
        if (!isLogin()) {
            if (listener != null) {
                listener.onSuccess();
            }
            return;
        }
        LoginStatusDispatcher.getInstance().dispatchBeforeLogout();
        RetrofitAgent.getINSTANCE().bizService(ILoginService.class).logout(UserInfoUtils.getAppid(), UserInfoUtils.getCv(), String.valueOf(mLoginInfo.uid), mLoginInfo.sid).enqueue(new Callback<AfResponse>() {
            @Override
            public void onResponse(Call<AfResponse> call, Response<AfResponse> response) {

            }

            @Override
            public void onFailure(Call<AfResponse> call, Throwable t) {

            }
        });
        if (mLoginInfo != null) {
            UserInfoUtils.setUidSid("", "");
            UserInfoUtils.setUserInfoToFile(String.valueOf(mLoginInfo.uid), "");
            UserInfoUtils.setCurrentUidFromFile("");
            UserSDK.getInstance().getAccountService().clearUserModel();
            mLoginInfo = null;
            if (listener != null) {
                listener.onSuccess();
            }
        }
        LoginStatusDispatcher.getInstance().dispatchAfterLogout();
    }

    @Override
    public void localLogout(LogoutListener listener) {
        LoginStatusDispatcher.getInstance().dispatchBeforeLogout();
        if (mLoginInfo != null) {
            UserInfoUtils.setUidSid("", "");
            UserInfoUtils.setCurrentUidFromFile("");
            UserSDK.getInstance().getAccountService().clearUserModel();
            mLoginInfo = null;
        }
        if (!isLogin()) {
            if (listener != null) {
                listener.onSuccess();
            }
        }
        LoginStatusDispatcher.getInstance().dispatchAfterLogout();
    }

    @Override
    public void loginByPhoneCode(String areaCode, String onlyPhoneNum, String code, PhoneLoginListener<T> listener) {
        if (TextUtils.isEmpty(areaCode)) {
            listener.onError(new SDKError(ErrorCode.AREA_CODE_INVALID, ErrorMsg.AREA_CODE_INVALID()));
            return;
        }
        if (TextUtils.isEmpty(onlyPhoneNum)) {
            listener.onError(new SDKError(ErrorCode.PHONE_NUM_INVALID, ErrorMsg.PHONE_NUM_INVALID()));
            return;
        }
        if (TextUtils.isEmpty(code)) {
            listener.onError(new SDKError(ErrorCode.PHONE_CODE_INVALID, ErrorMsg.PHONE_CODE_INVALID()));
            return;
        }
        String encryptPhone = EncryptUitls.encryptPhone(areaCode, onlyPhoneNum);
        PhoneCodeLoginParam param = new PhoneCodeLoginParam();
        param.phone = encryptPhone;
        param.code = code;
        param.request_id = mRequestIds.get(CODE_SOURCE_PHONE_LOGIN);
        param.vcode = Constants.LOGIN_VCD;
        LoginStatusDispatcher.getInstance().dispatchBeforeLogin();
        RetrofitAgent.getINSTANCE().bizService(ILoginService.class).loginByPhoneCode(UserInfoUtils.getAppid(), UserInfoUtils.getCv(), param).enqueue(new Callback<AfResponse<JsonElement>>() {
            @Override
            public void onResponse(Call<AfResponse<JsonElement>> call, retrofit2.Response<AfResponse<JsonElement>> response) {
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
                T loginInfo = processLogin(afResponse.getData());
                if (listener != null) {
                    listener.onNewData(loginInfo);
                }
                LoginStatusDispatcher.getInstance().dispatchAfterLogin();
            }

            @Override
            public void onFailure(Call<AfResponse<JsonElement>> call, Throwable t) {
                if (listener != null) {
                    listener.onError(new SDKError(ErrorCode.SDK_ERROR, ErrorMsg.SDK_ERROR()));
                }
            }
        });
    }

    private T processLogin(JsonElement jsonElement) {
        String bizData = AfGson.Companion.get().toJson(jsonElement);
        UserModel<UserProfile> userModel = AfGson.Companion.get().fromJson(bizData, new TypeToken<UserModel<UserProfile>>() {
        }.getType());
        UserInfoUtils.setUserInfoToFile(String.valueOf(userModel.uid), bizData);
        UserInfoUtils.setCurrentUidFromFile(String.valueOf(userModel.uid));
        UserInfoUtils.setUidSid(String.valueOf(userModel.uid), userModel.sid);
        mLoginInfo = AfGson.Companion.get().fromJson(bizData, userModelClass);
        UserSDK.getInstance().getAccountService().setCacheUserModel(mLoginInfo);
        return mLoginInfo;
    }

    @Override
    public void sendPhoneLoginCode(String areaCode, String onlyPhoneNum, PhoneLoginCodeListener listener) {
        if (TextUtils.isEmpty(areaCode)) {
            listener.onError(new SDKError(ErrorCode.AREA_CODE_INVALID, ErrorMsg.AREA_CODE_INVALID()));
            return;
        }
        if (TextUtils.isEmpty(onlyPhoneNum)) {
            listener.onError(new SDKError(ErrorCode.PHONE_NUM_INVALID, ErrorMsg.PHONE_NUM_INVALID()));
            return;
        }
        String codeRevised = LocationUtils.filterAreaCode(areaCode);
        String encryptPhone = EncryptUitls.encryptPhone(codeRevised, onlyPhoneNum);

        PhoneCodeLoginSendCodeParam param = new PhoneCodeLoginSendCodeParam();
        param.vcode = Constants.LOGIN_VCD;
        param.phone = encryptPhone;
        param.area_code = codeRevised;

        RetrofitAgent.getINSTANCE().bizService(ILoginService.class).sendPhoneLoginCode(UserInfoUtils.getAppid(), UserInfoUtils.getCv(), param).enqueue(new Callback<AfResponse<PhoneCodeLoginSendCodeModel>>() {
            @Override
            public void onResponse(Call<AfResponse<PhoneCodeLoginSendCodeModel>> call, retrofit2.Response<AfResponse<PhoneCodeLoginSendCodeModel>> response) {
                if (response == null) {
                    if (listener != null) {
                        listener.onError(new SDKError(ErrorCode.NETWORK_ERROR, ErrorMsg.NETWORK_ERROR()));
                    }
                    return;
                }
                AfResponse<PhoneCodeLoginSendCodeModel> afResponse = response.body();
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
                String requestId = afResponse.getData().request_id;
                mRequestIds.put(CODE_SOURCE_PHONE_LOGIN, requestId);
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onFailure(Call<AfResponse<PhoneCodeLoginSendCodeModel>> call, Throwable t) {
                if (listener != null) {
                    listener.onError(new SDKError(ErrorCode.SDK_ERROR, ErrorMsg.SDK_ERROR()));
                }
            }
        });

    }

    @Override
    public void visitorLogin(VisitorLoginListener<T> listener) {
        String smid = Parameters.getInstance().get("smid");
        if (TextUtils.isEmpty(smid)) {
            Parameters.getInstance().subscribe("smid", new ParameterObserver() {
                @Override
                public void onParameterUpdated(boolean isExisted, @Nullable String newValue, @Nullable String preValue) {
                    if (!TextUtils.isEmpty(newValue)) {
                        realVisitorLogin(newValue, listener);
                    }
                }
            });
        } else {
            realVisitorLogin(smid, listener);
        }
    }

    private void realVisitorLogin(String smid, VisitorLoginListener listener) {
        VisitorLoginParam param = new VisitorLoginParam();
        param.visitor = Constants.VISITOR_TYPE;
        param.app_type = "android";
        LoginStatusDispatcher.getInstance().dispatchBeforeLogin();
        RetrofitAgent.getINSTANCE().bizService(ILoginService.class).visitorLogin(UserInfoUtils.getAppid(), UserInfoUtils.getCv(), smid, param).enqueue(new Callback<AfResponse<JsonElement>>() {
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
                T loginInfo = processLogin(afResponse.getData());
                if (listener != null) {
                    listener.onNewData(loginInfo);
                }
                LoginStatusDispatcher.getInstance().dispatchAfterLogin();
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
    public void loginByThirdparty(String type, String token, HashMap<String, String> extras, ThirdPartLoginListener listener) {
        ThirdPartLoginParam param = new ThirdPartLoginParam();
        param.appType = LOGIN_APP_TYPE;
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
                if (extras != null) {
                    param.nickName = String.valueOf(extras.get("nick_name"));
                    param.portrait = String.valueOf(extras.get("portrait"));
                }
                param.platform = LOGIN_TYPE_SNAPCHAT;
                break;
        }
        LoginStatusDispatcher.getInstance().dispatchBeforeLogin();
        RetrofitAgent.getINSTANCE().bizService(ILoginService.class).loginByThirdPart(UserInfoUtils.getAppid(), UserInfoUtils.getCv(), param).enqueue(new Callback<AfResponse<JsonElement>>() {
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
                T loginInfo = processLogin(afResponse.getData());
                if (listener != null) {
                    listener.onNewData(loginInfo);
                }
                LoginStatusDispatcher.getInstance().dispatchAfterLogin();
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
    public void sendEmailLoginCode(String email, EmailLoginCodeListener listener) {
        if (TextUtils.isEmpty(email)) {
            listener.onError(new SDKError(ErrorCode.EMAIL_INVALID, ErrorMsg.EMAIL_INVALID()));
            return;
        }
        String encryptEmail = EncryptUitls.encryptEmail(email);
        EmailCodeLoginSendCodeParam param = new EmailCodeLoginSendCodeParam();
        param.vcode = Constants.LOGIN_VCD;
        param.email = encryptEmail;

        RetrofitAgent.getINSTANCE().bizService(ILoginService.class).sendEmailLoginCode(UserInfoUtils.getAppid(), UserInfoUtils.getCv(), param).enqueue(new Callback<AfResponse<EmailCodeLoginSendCodeModel>>() {
            @Override
            public void onResponse(Call<AfResponse<EmailCodeLoginSendCodeModel>> call, Response<AfResponse<EmailCodeLoginSendCodeModel>> response) {
                if (response == null) {
                    if (listener != null) {
                        listener.onError(new SDKError(ErrorCode.NETWORK_ERROR, ErrorMsg.NETWORK_ERROR()));
                    }
                    return;
                }
                AfResponse<EmailCodeLoginSendCodeModel> afResponse = response.body();
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
                String requestId = afResponse.getData().request_id;
                mRequestIds.put(CODE_SOURCE_EMAIL_LOGIN, requestId);
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onFailure(Call<AfResponse<EmailCodeLoginSendCodeModel>> call, Throwable t) {
                if (listener != null) {
                    listener.onError(new SDKError(ErrorCode.SDK_ERROR, ErrorMsg.SDK_ERROR()));
                }
            }
        });
    }

    @Override
    public void loginByEmailCode(String email, String code, EmailLoginListener<T> listener) {
        if (TextUtils.isEmpty(email)) {
            listener.onError(new SDKError(ErrorCode.EMAIL_INVALID, ErrorMsg.EMAIL_INVALID()));
            return;
        }
        if (TextUtils.isEmpty(code)) {
            listener.onError(new SDKError(ErrorCode.EMAIL_CODE_INVALID, ErrorMsg.EMAIL_CODE_INVALID()));
            return;
        }
        String encryptEmail = EncryptUitls.encryptEmail(email);
        String requestId = mRequestIds.get(CODE_SOURCE_EMAIL_LOGIN);
        EmailCodeLoginParam param = new EmailCodeLoginParam();
        param.email = encryptEmail;
        param.code = code;
        param.request_id = requestId;
        param.vcode = Constants.LOGIN_VCD;
        LoginStatusDispatcher.getInstance().dispatchBeforeLogin();
        RetrofitAgent.getINSTANCE().bizService(ILoginService.class).loginByEmailCode(UserInfoUtils.getAppid(), UserInfoUtils.getCv(), param).enqueue(new Callback<AfResponse<JsonElement>>() {
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
                T loginInfo = processLogin(afResponse.getData());
                if (listener != null) {
                    listener.onNewData(loginInfo);
                }
                LoginStatusDispatcher.getInstance().dispatchAfterLogin();
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
    public void loginByEmailPassword(String email, String password, EmailPwdLoginListener<T> listener) {
        if (TextUtils.isEmpty(email)) {
            listener.onError(new SDKError(ErrorCode.EMAIL_INVALID, ErrorMsg.EMAIL_INVALID()));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            listener.onError(new SDKError(ErrorCode.ACCOUNT_PASSWORD_INVALID, ErrorMsg.ACCOUNT_PASSWORD_INVALID()));
            return;
        }
        String encryptEmail = EncryptUitls.encryptEmail(email);
        String encryptPwd = EncryptUitls.encryptPassword(password);

        EmailPwdLoginParam param = new EmailPwdLoginParam();
        param.type = Constants.ACCOUNT_TYPE_EMAIL;
        param.account = encryptEmail;
        param.password = encryptPwd;
        param.vcode = Constants.LOGIN_VCD;
        LoginStatusDispatcher.getInstance().dispatchBeforeLogin();
        RetrofitAgent.getINSTANCE().bizService(ILoginService.class).loginByEmailPassword(UserInfoUtils.getAppid(), UserInfoUtils.getCv(), param).enqueue(new Callback<AfResponse<JsonElement>>() {
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
                T loginInfo = processLogin(afResponse.getData());
                if (listener != null) {
                    listener.onNewData(loginInfo);
                }
                LoginStatusDispatcher.getInstance().dispatchAfterLogin();
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
    public void sendEmailPwdLoginVerifyCode(String email, EmailPwdLoginCodeListener listener) {
        if (TextUtils.isEmpty(email)) {
            listener.onError(new SDKError(ErrorCode.EMAIL_INVALID, ErrorMsg.EMAIL_INVALID()));
            return;
        }
        String encryptEmail = EncryptUitls.encryptEmail(email);
        EmailPwdLoginSendCodeParam param = new EmailPwdLoginSendCodeParam();
        param.type = Constants.ACCOUNT_TYPE_EMAIL;
        param.account = encryptEmail;
        param.vcode = Constants.LOGIN_VCD;

        RetrofitAgent.getINSTANCE().bizService(ILoginService.class).sendEmailPwdLoginVerifyCode(UserInfoUtils.getAppid(), UserInfoUtils.getCv(), String.valueOf(getUserId()), getSession(), param).enqueue(new Callback<AfResponse<EmailPwdLoginSendCodeModel>>() {
            @Override
            public void onResponse(Call<AfResponse<EmailPwdLoginSendCodeModel>> call, Response<AfResponse<EmailPwdLoginSendCodeModel>> response) {
                if (response == null) {
                    if (listener != null) {
                        listener.onError(new SDKError(ErrorCode.NETWORK_ERROR, ErrorMsg.NETWORK_ERROR()));
                    }
                    return;
                }
                AfResponse<EmailPwdLoginSendCodeModel> afResponse = response.body();
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
                String requestId = afResponse.getData().request_id;
                mRequestIds.put(CODE_SOURCE_EMAIL_VERIFY, requestId);
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onFailure(Call<AfResponse<EmailPwdLoginSendCodeModel>> call, Throwable t) {
                if (listener != null) {
                    listener.onError(new SDKError(ErrorCode.SDK_ERROR, ErrorMsg.SDK_ERROR()));
                }
            }
        });
    }


    @Override
    public void verifyEmailPwdLoginCode(String email, String code, EmailCodeVerifyListener listener) {
        if (TextUtils.isEmpty(email)) {
            listener.onError(new SDKError(ErrorCode.EMAIL_INVALID, ErrorMsg.EMAIL_INVALID()));
            return;
        }
        if (TextUtils.isEmpty(code)) {
            listener.onError(new SDKError(ErrorCode.EMAIL_CODE_INVALID, ErrorMsg.EMAIL_CODE_INVALID()));
            return;
        }
        String encryptEmail = EncryptUitls.encryptEmail(email);
        String requestId = mRequestIds.get(CODE_SOURCE_EMAIL_VERIFY);
        EmailPwdLoginVerifyCodeParam param = new EmailPwdLoginVerifyCodeParam();
        param.type = Constants.ACCOUNT_TYPE_EMAIL;
        param.account = encryptEmail;
        param.code = code;
        param.request_id = requestId;

        RetrofitAgent.getINSTANCE().bizService(ILoginService.class).verifyEmailPwdLoginCode(UserInfoUtils.getAppid(), UserInfoUtils.getCv(), String.valueOf(getUserId()), getSession(), param).enqueue(new Callback<AfResponse<EmailPwdLoginVerifyCodeModel>>() {
            @Override
            public void onResponse(Call<AfResponse<EmailPwdLoginVerifyCodeModel>> call, Response<AfResponse<EmailPwdLoginVerifyCodeModel>> response) {
                if (response == null) {
                    if (listener != null) {
                        listener.onError(new SDKError(ErrorCode.NETWORK_ERROR, ErrorMsg.NETWORK_ERROR()));
                    }
                    return;
                }
                AfResponse<EmailPwdLoginVerifyCodeModel> afResponse = response.body();
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
                String token = afResponse.getData().token;
                mRequestIds.put(EMAIL_ACCOUNT_TOKEN, token);
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onFailure(Call<AfResponse<EmailPwdLoginVerifyCodeModel>> call, Throwable t) {
                if (listener != null) {
                    listener.onError(new SDKError(ErrorCode.SDK_ERROR, ErrorMsg.SDK_ERROR()));
                }
            }
        });
    }

    @Override
    public void setPasswordWithEmail(String email, String password, EmailPwdListener listener) {
        if (TextUtils.isEmpty(email)) {
            listener.onError(new SDKError(ErrorCode.EMAIL_INVALID, ErrorMsg.EMAIL_INVALID()));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            listener.onError(new SDKError(ErrorCode.ACCOUNT_PASSWORD_INVALID, ErrorMsg.ACCOUNT_PASSWORD_INVALID()));
            return;
        }
        String token = mRequestIds.get(EMAIL_ACCOUNT_TOKEN);
        String encryptEmail = EncryptUitls.encryptEmail(email);
        String encryptPwd = EncryptUitls.encryptPassword(password);
        EmailPwdLoginSetPasswordParam param = new EmailPwdLoginSetPasswordParam();
        param.type = Constants.ACCOUNT_TYPE_EMAIL;
        param.account = encryptEmail;
        param.token = token;
        param.password = encryptPwd;

        RetrofitAgent.getINSTANCE().bizService(ILoginService.class).setPasswordWithEmail(UserInfoUtils.getAppid(), UserInfoUtils.getCv(), String.valueOf(getUserId()), getSession(), param).enqueue(new Callback<AfResponse>() {
            @Override
            public void onResponse(Call<AfResponse> call, Response<AfResponse> response) {
                if (response == null) {
                    if (listener != null) {
                        listener.onError(new SDKError(ErrorCode.NETWORK_ERROR, ErrorMsg.NETWORK_ERROR()));
                    }
                    return;
                }
                AfResponse afResponse = response.body();
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
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onFailure(Call<AfResponse> call, Throwable t) {
                Log.e("UserTest", "email password login set password failed");
                if (listener != null) {
                    listener.onError(new SDKError(ErrorCode.SDK_ERROR, ErrorMsg.SDK_ERROR()));
                }
            }
        });
    }


    @Override
    public void loginByPhonePassword(String areaCode, String account, String password, PhonePwdLoginListener listener) {
        if (TextUtils.isEmpty(areaCode)) {
            listener.onError(new SDKError(ErrorCode.AREA_CODE_INVALID, ErrorMsg.AREA_CODE_INVALID()));
            return;
        }
        if (TextUtils.isEmpty(account)) {
            listener.onError(new SDKError(ErrorCode.PHONE_NUM_INVALID, ErrorMsg.PHONE_NUM_INVALID()));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            listener.onError(new SDKError(ErrorCode.ACCOUNT_PASSWORD_INVALID, ErrorMsg.ACCOUNT_PASSWORD_INVALID()));
            return;
        }

        String codeRevised = LocationUtils.filterAreaCode(areaCode);
        String encryptPhone = EncryptUitls.encryptPhone(codeRevised, account);
        String encryptPwd = EncryptUitls.encryptPassword(password);

        PhonePwdLoginParam param = new PhonePwdLoginParam();
        param.type = Constants.ACCOUNT_TYPE_PHONE;
        param.account = encryptPhone;
        param.password = encryptPwd;
        param.area_code = areaCode;
        param.vcode = Constants.LOGIN_VCD;
        LoginStatusDispatcher.getInstance().dispatchBeforeLogin();
        RetrofitAgent.getINSTANCE().bizService(ILoginService.class).loginByPhonePassword(UserInfoUtils.getAppid(), UserInfoUtils.getCv(), param).enqueue(new Callback<AfResponse<JsonElement>>() {
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
                T loginInfo = processLogin(afResponse.getData());
                if (listener != null) {
                    listener.onNewData(loginInfo);
                }
                LoginStatusDispatcher.getInstance().dispatchAfterLogin();
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
    public void sendPhonePwdLoginVerifyCode(String areaCode, String onlyPhoneNum, PhonePwdLoginCodeListener listener) {
        if (TextUtils.isEmpty(areaCode)) {
            listener.onError(new SDKError(ErrorCode.AREA_CODE_INVALID, ErrorMsg.AREA_CODE_INVALID()));
            return;
        }
        if (TextUtils.isEmpty(onlyPhoneNum)) {
            listener.onError(new SDKError(ErrorCode.PHONE_NUM_INVALID, ErrorMsg.PHONE_NUM_INVALID()));
            return;
        }
        String codeRevised = LocationUtils.filterAreaCode(areaCode);
        String encryptPhone = EncryptUitls.encryptPhone(codeRevised, onlyPhoneNum);
        PhonePwdLoginSendCodeParam param = new PhonePwdLoginSendCodeParam();
        param.type = Constants.ACCOUNT_TYPE_PHONE;
        param.account = encryptPhone;
        param.area_code = codeRevised;
        param.vcode = Constants.LOGIN_VCD;
        RetrofitAgent.getINSTANCE().bizService(ILoginService.class).sendPhonePasswordCode(UserInfoUtils.getAppid(), UserInfoUtils.getCv(), String.valueOf(getUserId()), getSession(), param).enqueue(new Callback<AfResponse<PhonePwdLoginSendCodeModel>>() {
            @Override
            public void onResponse(Call<AfResponse<PhonePwdLoginSendCodeModel>> call, Response<AfResponse<PhonePwdLoginSendCodeModel>> response) {
                if (response == null) {
                    if (listener != null) {
                        listener.onError(new SDKError(ErrorCode.NETWORK_ERROR, ErrorMsg.NETWORK_ERROR()));
                    }
                    return;
                }
                AfResponse<PhonePwdLoginSendCodeModel> afResponse = response.body();
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
                String requestId = afResponse.getData().request_id;
                mRequestIds.put(CODE_SOURCE_PHONE_VERIFY, requestId);
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onFailure(Call<AfResponse<PhonePwdLoginSendCodeModel>> call, Throwable t) {
                if (listener != null) {
                    listener.onError(new SDKError(ErrorCode.SDK_ERROR, ErrorMsg.SDK_ERROR()));
                }
            }
        });
    }

    @Override
    public void verifyPhonePwdLoginCode(String areaCode, String phone, String code, PhoneCodeVerifyListener listener) {
        if (TextUtils.isEmpty(areaCode)) {
            listener.onError(new SDKError(ErrorCode.AREA_CODE_INVALID, ErrorMsg.AREA_CODE_INVALID()));
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            listener.onError(new SDKError(ErrorCode.PHONE_NUM_INVALID, ErrorMsg.PHONE_NUM_INVALID()));
            return;
        }
        if (TextUtils.isEmpty(code)) {
            listener.onError(new SDKError(ErrorCode.PHONE_CODE_INVALID, ErrorMsg.ACCOUNT_PASSWORD_INVALID()));
            return;
        }
        String codeRevised = LocationUtils.filterAreaCode(areaCode);
        String encryptPhone = EncryptUitls.encryptPhone(codeRevised, phone);
        String requestId = mRequestIds.get(CODE_SOURCE_PHONE_VERIFY);
        PhonePwdLoginVerifyCodeParam param = new PhonePwdLoginVerifyCodeParam();
        param.type = Constants.ACCOUNT_TYPE_PHONE;
        param.account = encryptPhone;
        param.code = code;
        param.request_id = requestId;

        RetrofitAgent.getINSTANCE().bizService(ILoginService.class).verifyPhonePwdLoginCode(UserInfoUtils.getAppid(), UserInfoUtils.getCv(), String.valueOf(getUserId()), getSession(), param).enqueue(new Callback<AfResponse<PhonePwdLoginVerifyCodeModel>>() {
            @Override
            public void onResponse(Call<AfResponse<PhonePwdLoginVerifyCodeModel>> call, Response<AfResponse<PhonePwdLoginVerifyCodeModel>> response) {
                if (response == null) {
                    if (listener != null) {
                        listener.onError(new SDKError(ErrorCode.NETWORK_ERROR, ErrorMsg.NETWORK_ERROR()));
                    }
                    return;
                }
                AfResponse<PhonePwdLoginVerifyCodeModel> afResponse = response.body();
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
                String token = afResponse.getData().token;
                mRequestIds.put(PHONE_ACCOUNT_TOKEN, token);
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onFailure(Call<AfResponse<PhonePwdLoginVerifyCodeModel>> call, Throwable t) {
                Log.e("UserTest", "phone password login verify code failed");
                if (listener != null) {
                    listener.onError(new SDKError(ErrorCode.SDK_ERROR, ErrorMsg.SDK_ERROR()));
                }
            }
        });
    }

    @Override
    public void setPasswordWithPhone(String areaCode, String phone, String password, PhonePwdListener listener) {
        if (TextUtils.isEmpty(areaCode)) {
            listener.onError(new SDKError(ErrorCode.AREA_CODE_INVALID, ErrorMsg.AREA_CODE_INVALID()));
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            listener.onError(new SDKError(ErrorCode.PHONE_NUM_INVALID, ErrorMsg.PHONE_NUM_INVALID()));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            listener.onError(new SDKError(ErrorCode.ACCOUNT_PASSWORD_INVALID, ErrorMsg.ACCOUNT_PASSWORD_INVALID()));
            return;
        }
        String token = mRequestIds.get(PHONE_ACCOUNT_TOKEN);
        String codeRevised = LocationUtils.filterAreaCode(areaCode);
        String encryptPhone = EncryptUitls.encryptPhone(codeRevised, phone);
        String encryptPwd = EncryptUitls.encryptPassword(password);
        PhonePwdLoginSetPasswordParam param = new PhonePwdLoginSetPasswordParam();
        param.type = Constants.ACCOUNT_TYPE_PHONE;
        param.account = encryptPhone;
        param.token = token;
        param.password = encryptPwd;

        RetrofitAgent.getINSTANCE().bizService(ILoginService.class).setPasswordWithPhone(UserInfoUtils.getAppid(), UserInfoUtils.getCv(), String.valueOf(getUserId()), getSession(), param).enqueue(new Callback<AfResponse>() {
            @Override
            public void onResponse(Call<AfResponse> call, Response<AfResponse> response) {
                if (response == null) {
                    if (listener != null) {
                        listener.onError(new SDKError(ErrorCode.NETWORK_ERROR, ErrorMsg.NETWORK_ERROR()));
                    }
                    return;
                }
                AfResponse afResponse = response.body();
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
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onFailure(Call<AfResponse> call, Throwable t) {
                Log.e("UserTest", "phone password login set password failed");
                if (listener != null) {
                    listener.onError(new SDKError(ErrorCode.SDK_ERROR, ErrorMsg.SDK_ERROR()));
                }
            }
        });
    }

    @Override
    public void registerLoginStatusListener(LoginStatusListener listener) {
        LoginStatusDispatcher.getInstance().registerLoginStatusListener(listener);
    }

    @Override
    public void unRegisterLoginStatusListener(LoginStatusListener listener) {
        LoginStatusDispatcher.getInstance().unRegisterLoginStatusListener(listener);
    }

    interface ILoginService {
        @POST("api/user/account/phone/send_code")
        Call<AfResponse<PhoneCodeLoginSendCodeModel>> sendPhoneLoginCode(@Query("ik_appid") String appid, @Query("cv") String cv, @Body PhoneCodeLoginSendCodeParam body);

        @POST("api/v1/user/account/email/send_code")
        Call<AfResponse<EmailCodeLoginSendCodeModel>> sendEmailLoginCode(@Query("ik_appid") String appid, @Query("cv") String cv, @Body EmailCodeLoginSendCodeParam body);

        @POST("api/user/account/phone/login")
        Call<AfResponse<JsonElement>> loginByPhoneCode(@Query("ik_appid") String appid, @Query("cv") String cv, @Body PhoneCodeLoginParam body);

        @POST("api/v1/user/account/email/login")
        Call<AfResponse<JsonElement>> loginByEmailCode(@Query("ik_appid") String appid, @Query("cv") String cv, @Body EmailCodeLoginParam body);

        @POST("api/user/account/thirdpart/login")
        Call<AfResponse<JsonElement>> loginByThirdPart(@Query("ik_appid") String appid, @Query("cv") String cv, @Body ThirdPartLoginParam body);

        @POST("api/v1/user/account/password/login")
        Call<AfResponse<JsonElement>> loginByPhonePassword(@Query("ik_appid") String appid, @Query("cv") String cv, @Body PhonePwdLoginParam param);

        @POST("api/v1/user/account/password/login")
        Call<AfResponse<JsonElement>> loginByEmailPassword(@Query("ik_appid") String appid, @Query("cv") String cv, @Body EmailPwdLoginParam param);

        @GET("api/v1/user/session/renewal")
        Call<AfResponse<SessionCheckModel>> checkSession(@Query("ik_appid") String appid, @Query("cv") String cv, @Query("uid") String uid, @Query("sid") String sid);

        @POST("api/v1/user/session/logout")
        Call<AfResponse> logout(@Query("ik_appid") String appid, @Query("cv") String cv, @Query("uid") String uid, @Query("sid") String sid);

        @GET("api/user/account/delete")
        Call<AfResponse> logoff(@Query("ik_appid") String appid, @Query("cv") String cv, @Query("uid") String uid, @Query("sid") String sid, @QueryMap Map<String, String> param);

        @POST("api/v2/user/account/visitor/login")
        Call<AfResponse<JsonElement>> visitorLogin(@Query("ik_appid") String appid, @Query("cv") String cv, @Query("smid") String smid, @Body VisitorLoginParam body);

        @POST("api/v1/user/account/password/set/send_code")
        Call<AfResponse<PhonePwdLoginSendCodeModel>> sendPhonePasswordCode(@Query("ik_appid") String appid, @Query("cv") String cv, @Query("uid") String uid, @Query("sid") String sid, @Body PhonePwdLoginSendCodeParam body);

        @POST("api/v1/user/account/password/set/send_code")
        Call<AfResponse<EmailPwdLoginSendCodeModel>> sendEmailPwdLoginVerifyCode(@Query("ik_appid") String appid, @Query("cv") String cv, @Query("uid") String uid, @Query("sid") String sid, @Body EmailPwdLoginSendCodeParam body);

        @POST("api/v1/user/account/password/set/check_code")
        Call<AfResponse<PhonePwdLoginVerifyCodeModel>> verifyPhonePwdLoginCode(@Query("ik_appid") String appid, @Query("cv") String cv, @Query("uid") String uid, @Query("sid") String sid, @Body PhonePwdLoginVerifyCodeParam param);

        @POST("api/v1/user/account/password/set/check_code")
        Call<AfResponse<EmailPwdLoginVerifyCodeModel>> verifyEmailPwdLoginCode(@Query("ik_appid") String appid, @Query("cv") String cv, @Query("uid") String uid, @Query("sid") String sid, @Body EmailPwdLoginVerifyCodeParam param);

        @POST("api/v1/user/account/password/set")
        Call<AfResponse> setPasswordWithPhone(@Query("ik_appid") String appid, @Query("cv") String cv, @Query("uid") String uid, @Query("sid") String sid, @Body PhonePwdLoginSetPasswordParam param);

        @POST("api/v1/user/account/password/set")
        Call<AfResponse> setPasswordWithEmail(@Query("ik_appid") String appid, @Query("cv") String cv, @Query("uid") String uid, @Query("sid") String sid, @Body EmailPwdLoginSetPasswordParam param);
    }
}
