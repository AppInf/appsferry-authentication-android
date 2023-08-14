package com.appsferry.login.listener.base;

public interface BaseDataListener<T> {

    void onNewData(T entity);

    void onError(SDKError error);
}
