package com.helloweather.app.util;

/**
 * @author HuaZhu
 *         created at 2016-11-11 9:57
 * @brief 回调接口，用于回调服务返回的结果
 */
public interface HttpCallbackListener {

    void onFinish(String response);

    void onError(Exception e);
}
