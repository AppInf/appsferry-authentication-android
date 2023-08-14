## Getting started

### Step 1
We provide a base class for user profile 'UserModel', then you need to extend the class of UserModel 
as custom, it has a property 'UserProfile' inside, also you need to extend the class of UserProfile 
as custom.
```
public class MyUserModel extends UserModel<MyUserProfile> {

}

public class MyUserProfile extends UserProfile {

    public String profession;

    public String xxx;
}
```
### Step 2
First of all, you need to read the privacy policy of this module carefully.
Then remind the user that you need to access his personal information clearly.
After he agrees, the following operations can be carried out'
```
    UserSDK.getInstance().setPrivacyAgree(true);
```
### Step 3
Initialize the SDK like this
```
    UserSDK.getInstance().init(getApplicationContext(), MyUserModel.class);
```
### Step 4
Monitor changes in login status for your subsequent operations. It provides four states 
before login, after login, before logout, and after logout. The sample code is as follows:
```
    UserSDK.getInstance().getLoginService().registerLoginStatusListener(new LoginStatusListener() {
        @Override
        public void beforeLogin() {
        
        }

        @Override
        public void afterLogin() {
        
        }

        @Override
        public void beforeLogout() {
        
        }

        @Override
        public void afterLogout() {
        
        }
    });
```
### Step 5
Here we will list a few key APIs that you will use
#### Phone Code Login
Get phone login code from server that two necessary parameters: 'areaCode' and 'phoneNumber' 
need to pay attention to.
```
    UserSDK.getInstance().getLoginService().sendPhoneLoginCode("your areaCode", "your phoneNumber", new PhoneLoginCodeListener() {
        @Override
        public void onSuccess() {
        
        }

        @Override
        public void onError(SDKError error) {
        
        }
    });
```
Login with phone code, that three necessary parameters: 'areaCode', 'phoneNumber' and 'verifyCode'
need to pay attention to.
```
    UserSDK.getInstance().getLoginService().loginByPhoneCode("your areaCode", "your phoneNumber", 
        "your verifyCode", new PhoneLoginListener<MyUserModel>() {
        @Override
        public void onNewData(MyUserModel entity) {
        
        }

        @Override
        public void onError(SDKError error) {
        
        }
    });
```
#### Phone Password Login
Get phone password verify code from server that two necessary parameters: 'areaCode' and 'phoneNumber'
need to pay attention to.
```
    UserSDK.getInstance().getLoginService().sendPhonePwdLoginVerifyCode("your areaCode", "your phoneNumber", new PhonePwdLoginCodeListener() {
        @Override
        public void onSuccess() {
        
        }

        @Override
        public void onError(SDKError error) {
        
        }
    });
```
Verify code for phone password login, that three necessary parameters: 'areaCode', 'phoneNumber' and 'verifyCode'
need to pay attention to.
```
    UserSDK.getInstance().getLoginService().verifyPhonePwdLoginCode("your areaCode", "your phoneNumber",
        "your verifyCode", new PhoneCodeVerifyListener() {
        @Override
        public void onSuccess() {
        
        }

        @Override
        public void onError(SDKError error) {
        
        }
    });
```
Set your password, that three necessary parameters: 'areaCode', 'phoneNumber' and 'password'
need to pay attention to.
```
    UserSDK.getInstance().getLoginService().setPasswordWithPhone("your areaCode", "your phoneNumber",
        "your password", new PhonePwdListener() {
        @Override
        public void onSuccess() {
        
        }

        @Override
        public void onError(SDKError error) {
        
        }
    });
```
Login with phone password, that three necessary parameters: 'areaCode', 'phoneNumber' and 'password'
need to pay attention to.
```
    UserSDK.getInstance().getLoginService().loginByPhonePassword("your areaCode", "your phoneNumber", 
        "your password" , new PhonePwdLoginListener() {
        @Override
        public void onNewData(Object entity) {
        
        }

        @Override
        public void onError(SDKError error) {
        
        }
    });
```
#### Email Code Login
Get email login code from server that only one necessary parameters: 'email' need to pay attention to.
```
    UserSDK.getInstance().getLoginService().sendEmailLoginCode("your email", new EmailLoginCodeListener() {
        @Override
        public void onSuccess() {
        
        }

        @Override
        public void onError(SDKError error) {
        
        }
    });
```
Login with email code, that two necessary parameters: 'email' and 'verifyCode' need to pay attention to.
```
    UserSDK.getInstance().getLoginService().loginByEmailCode("your email", " your verifyCode", new EmailLoginListener() {
        @Override
        public void onNewData(Object entity) {
        
        }

        @Override
        public void onError(SDKError error) {
        
        }
    });
```
#### Email Password Login
Get email password verify code from server that only one necessary parameters: 'email'
need to pay attention to.
```
    UserSDK.getInstance().getLoginService().sendEmailPwdLoginVerifyCode("your email", new EmailPwdLoginCodeListener() {
        @Override
        public void onSuccess() {
        
        }

        @Override
        public void onError(SDKError error) {
        
        }
    });
```
Verify code for email password login, that two necessary parameters: 'email' and 'verifyCode'
need to pay attention to.
```
    UserSDK.getInstance().getLoginService().verifyEmailPwdLoginCode("your email", "your verifyCode", new EmailCodeVerifyListener() {
        @Override
        public void onSuccess() {
        
        }

        @Override
        public void onError(SDKError error) {
        
        }
    });
```
Set your password, that two necessary parameters: 'email' and 'password'
need to pay attention to.
```
    UserSDK.getInstance().getLoginService().setPasswordWithEmail("your email", "your password", new EmailPwdListener() {
        @Override
        public void onSuccess() {
        
        }

        @Override
        public void onError(SDKError error) {
        
        }
    });
```
Login with email password, that two necessary parameters: 'email' and 'password'
need to pay attention to.
```
    UserSDK.getInstance().getLoginService().loginByEmailPassword("your email", "your password", new EmailPwdLoginListener() {
        @Override
        public void onNewData(Object entity) {
        
        }

        @Override
        public void onError(SDKError error) {
        
        }
    });
```
#### Visitor Login
Login by the guest mode, it actually uses the user's device information for binding, so there is a parameter that cannot be empty "smid"
```
    UserSDK.getInstance().getLoginService().visitorLogin(new VisitorLoginListener() {
        @Override
        public void onNewData(Object entity) {
        
        }

        @Override
        public void onError(SDKError error) {
        
        }
    });
```
#### Logout
Log out user from server, the client will not save any user information locally for reuse
```
    UserSDK.getInstance().getLoginService().logout(new LogoutListener() {
        @Override
        public void onSuccess() {
        
        }

        @Override
        public void onError(SDKError error) {
        
        }
    });
```
Log out user only on client, the client will save user information locally for relogin or switch accounts quickly.
```
    UserSDK.getInstance().getLoginService().localLogout(new LogoutListener() {
        @Override
        public void onSuccess() {
            
        }

        @Override
        public void onError(SDKError error) {

        }
    });
```
#### Logoff
Delete your account from server, this is a dangerous operation and cannot be undone.
```
    UserSDK.getInstance().getLoginService().logoff(new LogoffListener() {
        @Override
        public void onSuccess() {
            
        }

        @Override
        public void onError(SDKError error) {
        
        }
    });
```
#### Get UserInfo
Get user info from client, note that this is the data in memory, not necessarily the latest data.
If you want to get the latest user data, you should call another interface to get it from the server
```
    AccountService<MyUserModel> accountService = UserSDK.getInstance().getAccountService();
    MyUserModel userModel = accountService.getCacheUserModel();
```
Get the latest user information from the server, please note that network requests will occur here, 
frequent calls may have performance problems
```
    UserSDK.getInstance().getAccountService().fetchUserModel("your uid", new FetchUserInfoListener() {
        @Override
        public void onNewData(Object entity) {
            
        }

        @Override
        public void onError(SDKError error) {
            
        }
    });
```
#### Update UserInfo
You can update the user profile through this method, either full or partial.
The parameters to be updated should be placed in a 'HashMap', but cannot conflict with some unique parameters like 'uid'.
```
    HashMap<String, Object> params = new HashMap<>();
    params.put("nick", "your nick");
    params.put("gender", 1);
    UserSDK.getInstance().getAccountService().updateUserInfo(params, new UpdateUserProfileListener() {
        @Override
        public void onSuccess() {
            
        }

        @Override
        public void onError(SDKError error) {
            
        }
    });
```
#### Thirdparty Login
We provide four login channels for third-party platforms: Google, Facebook, Snapchat and Tiktok.
Each platform requires you to register an account on the corresponding official open platform 
and apply for relevant configuration. After this operation, please set these configurations 
on the cloud platform and your local project. 
##### Google Login
Please complete some preliminary work, such as put your 'google-services.json' in your root project.
Initialize some platform configuration in your Application like this:
```
    GoogleSignInOptions options = new GoogleSignInOptions.Builder()
                .setSignInOption(SignInType.DEFAULT_SIGN_IN)
                .build();
    GoogleManager.getInstance().init(this, options);
```
Call this login interface where necessary. there is a logic to be aware of here. 
The first time you log in with Google, you will be prompted to select the account you want to log in to,
After a successful login you can then log in silently.
```
    GoogleManager.getInstance().signInSilently(new GoogleSignInListener() {
        @Override
        public void onSuccess(GoogleSignInAccount googleSignInAccount) {
            GoogleManager.getInstance().loginWithGoogle(GoogleActivity.this, true, new GoogleLoginListener<UserModel<? extends UserProfile>>() {
                @Override
                public void onSuccess(UserModel<? extends UserProfile> userModel) {
                    
                }

                @Override
                public void onFailed(int errorCode, String errorMsg) {
                
                }
            });
        }

        @Override
        public void onFailed(int errorCode, String errorMsg) {
            GoogleManager.getInstance().loginWithGoogle(GoogleActivity.this, false, new GoogleLoginListener<UserModel<? extends UserProfile>>() {
                @Override
                public void onSuccess(UserModel<? extends UserProfile> userModel) {
                    
                }

                @Override
                public void onFailed(int errorCode, String errorMsg) {
                    
                }
            });
        }
    });
```
##### Facebook Login
Configure your Facebook appid in the metadta of Manifest like this:
```
1. app build.gradle
    manifestPlaceholders = [
        facebook_app_id: 'your facebook appid',
    ]
2.Manifest metadata
    <meta-data
        android:name="facebook_app_id"
        android:value="your facebook appid" />
```
Set login parameters according to the permissions you applied for on the facebook developer platform.
```
    List<String> permissions = new ArrayList<>();
    permissions.add("public_profile");
    permissions.add("email");
    FacebookManager.getInstance().loginWithFacebook(FacebookActivity.this, permissions, new FacebookLoginListener<UserModel<? extends UserProfile>>() {
        @Override
        public void onSuccess(UserModel<? extends UserProfile> userModel) {
            
        }

        @Override
        public void onFailed(int errorCode, String errorMsg) {
            
        }
    });
```
##### Snapchat Login
Configure your Snapchat clientId and redirectUrl in the metadta of Manifest like this:
```
Manifest metadata
    <meta-data
        android:name="com.snap.kit.clientId"
        android:value="your clientId" />

    <meta-data
        android:name="com.snap.kit.redirectUrl"
        android:value="your redirectUrl" />
```
Configure the callback path with a activity in the manifest like this:
```
    <data
        android:scheme="your scheme"
        android:host="your host"
        android:path="/your path"/>
```
Initialize some platform configuration in your Application like this:
```
    SnapchatManager.getInstance().init(getApplicationContext());
```
Finally, you can call the following interface to login:
```
    SnapchatManager.getInstance().loginBySnapchat(new SnapChatLoginListener<UserModel<? extends UserProfile>>() {
        @Override
        public void onSuccess(UserModel<? extends UserProfile> userModel) {
            
        }

        @Override
        public void onFailed(int errorCode, String errorMsg) {
            
        }
    });
```
##### Tiktok Login
Configure the callback path with a custom activity in the manifest like this:
```
    <activity android:name="{your package}.TikTokEntryActivity"
        android:exported="true">
        <intent-filter>
            <action android:name="android.intent.action.VIEW" />
            <category android:name="android.intent.category.DEFAULT" />
            <category android:name="android.intent.category.BROWSABLE" />

            <data
                android:scheme="your scheme"
                android:host="your host"
                android:path="/your path"/>
        </intent-filter>
    </activity>
```
Initialize some platform configuration in your Application like this:
```
    TikTokManager.getInstance().init("your clientKey", "your redirectUrl");
```
Finally, you can call the following interface to login:
```
    TikTokManager.getInstance().loginByTikTok(TikTokActivity.this, "user.info.basic", null, 
        new TikTokLoginListener<UserModel<? extends UserProfile>>() {
        @Override
        public void onSuccess(UserModel<? extends UserProfile> userModel) {
            
        }

        @Override
        public void onFailed(int errorCode, String errorMsg) {
            
        }
    });
```
#### Bind/Unbind
We provide account binding and unbinding for all login channels: 'google', 'facebook', 'snapchat' and tiktok'.
```
    UserSDK.getInstance().getAccountService().bindAccount("Platform Type", "your accessToken", new ThirdPartBindListener() {
        @Override
        public void onSuccess() {
        
        }

        @Override
        public void onError(SDKError error) {
        
        }
    });
```
```
    UserSDK.getInstance().getAccountService().unBindAccount("Platform Type", new ThirdPartUnBindListener() {
        @Override
        public void onSuccess() {
        
        }

        @Override
        public void onError(SDKError error) {
            
        }
    });
```