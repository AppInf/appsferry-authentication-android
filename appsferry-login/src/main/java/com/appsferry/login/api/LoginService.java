package com.appsferry.login.api;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.appsferry.login.entity.user.UserModel;
import com.appsferry.login.entity.user.UserProfile;
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
import com.appsferry.login.listener.visitor.VisitorLoginListener;

import java.util.HashMap;

@SuppressWarnings({"AlibabaClassMustHaveAuthor", "AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc"})
public interface LoginService<T extends UserModel<? extends UserProfile>> {
    /**
     * init login commponent
     *
     * @param context Application Context
     * @param clazz   custom UserModel
     */
    void init(Context context, Class<T> clazz);

    /**
     * get current userid, default 0
     *
     * @return uid
     */
    long getUserId();

    /**
     * get current session, default ''
     *
     * @return session
     */
    String getSession();

    /**
     * check session state
     *
     * @param listener SessionState listener
     */
    void checkSession(SessionCheckListener listener);

    /**
     * current login status
     *
     * @return login status
     */
    boolean isLogin();

    /**
     * logoff user, unrecoverable after this operation
     */
    void logoff(LogoffListener listener);

    /**
     * logout user, can login again after this operation
     *
     * @param listener LoginStatus listener
     */
    void logout(LogoutListener listener);

    /**
     * local logout, will not affect remote server
     *
     * @param listener LogoutStatus listener
     */
    void localLogout(LogoutListener listener);

    /**
     * login by phone code
     *
     * @param areaCode
     * @param phoneNum
     * @param code
     * @param listener
     */
    void loginByPhoneCode(String areaCode, String phoneNum, String code, PhoneLoginListener<T> listener);

    /**
     * send phone code for login
     *
     * @param areaCode
     * @param phoneNum
     * @param listener
     */
    void sendPhoneLoginCode(String areaCode, String phoneNum, PhoneLoginCodeListener listener);

    /**
     * visitor login, force dependence on a parameter: 'smid'

     */
    void visitorLogin(VisitorLoginListener<T> listener);

    /**
     * login from thirdparty channel
     *
     * @param type
     * @param token
     * @param extras
     * @param listener
     */
    void loginByThirdparty(String type, String token, HashMap<String, String> extras, ThirdPartLoginListener listener);

    /**
     * send email code for login
     *
     * @param email
     * @param listener
     */
    void sendEmailLoginCode(String email, EmailLoginCodeListener listener);

    /**
     * login by email code
     *
     * @param email
     * @param code
     * @param listener
     */
    void loginByEmailCode(String email, String code, EmailLoginListener<T> listener);

    /**
     * login by email password
     *
     * @param email
     * @param password
     * @param listener
     */
    void loginByEmailPassword(String email, String password, EmailPwdLoginListener<T> listener);

    /**
     * send email code for password login
     *
     * @param email
     * @param listener
     */
    void sendEmailPwdLoginVerifyCode(String email, EmailPwdLoginCodeListener listener);

    /**
     * verify code for email password login
     *
     * @param email
     * @param code
     * @param listener
     */
    void verifyEmailPwdLoginCode(String email, String code, EmailCodeVerifyListener listener);

    /**
     * set email password
     *
     * @param email
     * @param password
     * @param listener
     */
    void setPasswordWithEmail(String email, String password, EmailPwdListener listener);


    /**
     * phone password login
     *
     * @param areaCode
     * @param phone
     * @param password
     * @param listener
     */
    void loginByPhonePassword(String areaCode, String phone, String password, PhonePwdLoginListener<T> listener);

    /**
     * send verify code for phone password login
     */
    void sendPhonePwdLoginVerifyCode(String areaCode, String phone, PhonePwdLoginCodeListener listener);

    /**
     * verify code for phone password login
     *
     * @param areaCode
     * @param phone
     * @param code
     * @param listener
     */
    void verifyPhonePwdLoginCode(String areaCode, String phone, String code, PhoneCodeVerifyListener listener);

    /**
     * set password for phone password login
     *
     * @param areaCode
     * @param phone
     * @param password
     * @param listener
     */
    void setPasswordWithPhone(String areaCode, String phone, String password, PhonePwdListener listener);

    void registerLoginStatusListener(LoginStatusListener listener);

    void unRegisterLoginStatusListener(LoginStatusListener listener);
}
