package com.appsferry.user.google.common;

import static com.appsferry.user.google.common.SignInType.DEFAULT_GAMES_SIGN_IN;
import static com.appsferry.user.google.common.SignInType.DEFAULT_SIGN_IN;

import androidx.annotation.StringDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@StringDef({DEFAULT_SIGN_IN, DEFAULT_GAMES_SIGN_IN})
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.METHOD})
public @interface SignInType {

    String DEFAULT_SIGN_IN = "SignInOption.standard";

    String DEFAULT_GAMES_SIGN_IN = "SignInOption.games";
}
