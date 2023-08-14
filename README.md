# AppsFerry Android Open Source Development

This repository contains a subset of the AppsFerry Android SDK source. It
currently includes the following libraries, and some of their dependencies:

* `appsferry-adapter`
* `appsferry-analytics`
* `appsferry-login`
* `appsferry-login-facebook`
* `appsferry-login-google`
* `appsferry-login-snapchat`
* `appsferry-login-tiktok`

AppsFerry is an app development platform with tools to help you build, grow and
monetize your app. Learn more about the provided samples, documentation, 
integrating the SDK into your app, accessing source code, and more at 
https://developers.appinfra.io. Fill out issues to tell us what’s most important 
to you and how we can improve.

## Getting Started
1. Check-out the tutorials available online at https://developers.appinfra.io/docs/android/getting-started
2. Start coding! Visit https://developers.appinfra.io/docs/android/ for tutorials and reference documentation.
3. You must create an account on the cloud platform https://cloud.appinfra.io, then enable the corresponding module capabilities

## Features
* [Authentication](https://github.com/AppInf/appsferry-authentication-android)
* [Analytics](https://github.com/AppInf/appsferry-analytyics-android)

## Project Setup

AppsFerry SDKs are published to Maven as independent modules. To utilize a feature listed above
include the appropriate dependency (or dependencies) listed below in your `app/build.gradle` file.
```gradle
dependencies {
    // Appsferry Authentication 
    implementation 'io.appinfra:appsferry-login:latest.release'
    
    // Appsferry Login for Google
    implementation 'io.appinfra:appsferry-login-google:latest.release'
    
    // Appsferry Login for Facebook
    implementation 'io.appinfra:appsferry-login-facebook:latest.release'
    
    // Appsferry Login for Snapchat
    implementation 'io.appinfra:appsferry-login-snapchat:latest.release'
    
    // Appsferry Login for Tiktok
    implementation 'io.appinfra:appsferry-login-tiktok:latest.release'

    // Appsferry Analytics
    implementation 'io.appinfra:appsferry-analytics:latest.release'
}
```

You may also need to add the following to your project/build.gradle file.
```gradle
buildscript {
    repositories {
        mavenCentral()
    }
}
```

## Integration

### Authentication Kit
The Login Kit is an application scenario of the AppsFerrySDK. It includes user login, 
through mobile phone number, email, tourist mode and other thirdparty platforms 
such as Google, Facebook, Snapchat and Tiktok. Among them, the thirdparty platform login 
needs to integrate sub-modules separately. It also provides management, 
expansion of user information, account binding and unbinding. For more information, 
please refer to the specific access document [Login](https://developers.appinfra.io/docs/appsferry-login)


### Analytics Kit
The Data Kit mainly includes two key capabilities: autotrack data statistics and 
custom data statistics. We will display autotrack data statistics in the form of a data dashboard 
and custom data statistics need deal with separately before they can be seen. For more information,
please refer to the specific access document [Analytics](https://developers.appinfra.io/docs/appsferry-analytics)

## License
We use the Apache License 2.0, see the [LICENSE](LICENSE) for more info.

## Developer Terms
- By enabling AppsFerrySDK integrations, including through this SDK, 
  you can share information with us, including information about people’s use of your app. 
  AppsFerrySDK will use information received in accordance with our Data Use Policy
  (https://developers.appinfra.io/about/privacy/), provide you with the display of indicators 
  such as user addition, retention and conversion rate.

- You may limit your sharing of information with us by updating the Insights control in the 
  developer tool (https://developers.appinfra.io/apps/[app_id]/settings/advanced).

- If you use a AppsFerrySDK integration, including to share information with us, 
  you agree and confirm that you have provided appropriate and sufficiently prominent notice 
  to and obtained the appropriate consent from your users regarding such collection, use, 
  and disclosure (including, at a minimum, through your privacy policy). 
  You further agree that you will not share information with us about children under the age of 13.

- You agree to comply with all applicable laws and regulations and also agree to 
  our Terms (https://developers.appinfra.io/policies/), including 
  our Platform Policies (https://developers.appinfra.io/policy/).

By using the AppsFerrySDK for Android you agree to these terms.