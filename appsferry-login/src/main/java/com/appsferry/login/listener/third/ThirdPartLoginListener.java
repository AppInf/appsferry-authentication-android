package com.appsferry.login.listener.third;

import com.appsferry.login.entity.user.UserModel;
import com.appsferry.login.entity.user.UserProfile;
import com.appsferry.login.listener.base.BaseDataListener;

public interface ThirdPartLoginListener<E extends UserModel<? extends UserProfile>> extends BaseDataListener<E> {
}
