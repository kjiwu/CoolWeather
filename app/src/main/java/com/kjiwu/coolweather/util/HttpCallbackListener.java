package com.kjiwu.coolweather.util;

/**
 * Created by kjiwu on 16/6/13.
 */
public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
