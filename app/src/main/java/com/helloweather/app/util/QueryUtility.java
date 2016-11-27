package com.helloweather.app.util;

import android.os.Handler;
import android.os.Message;

/**
 * Created by Administrator on 2016-11-25.
 * 用于从服务器查询数据的工具类
 */
public class QueryUtility {

    private static final int SYNCHRONIZING = 3;

    private static final int GET_INFO_FAIL = 0;

    private static final int REAL_TIME_WEATHER = 0;

    private static final int DAILY_WEATHER = 1;

    /**
     *  
     *
     * @brief 查询天气代号所对应的天气（简述）
     *  @param   weatherCode（天气代号）
     */

    public static void queryWeatherInfo(String cityId, Handler mHandler) {
        Message message = new Message();
        message.what = SYNCHRONIZING;
        mHandler.sendMessage(message);
        String address = "https://api.thinkpage.cn/v3/weather/daily.json?key=" + MyApplication.getMyKey() + "&location=" + cityId + "&language=zh-Hans&unit=c&start=0&days=3";
        LogUtil.d("weatherTest", "queryWeatherInfo address" + address);
        String address1 = "https://api.thinkpage.cn/v3/weather/now.json?key=" + MyApplication.getMyKey() + "&location=" + cityId + "&language=zh-Hans&unit=c";
        LogUtil.d("weatherTest", "queryWeatherInfo address1" + address1);
        queryFromServer(address, DAILY_WEATHER, mHandler);
        LogUtil.d("handlerr", "queryFromServer(address, DAILY_WEATHER executed ");
        queryFromServer(address1, REAL_TIME_WEATHER, mHandler);
        LogUtil.d("handlerr", "queryFromServer(address1, REAL_TIME_WEATHER executed ");
    }

    /**
     *  
     *
     * @brief 根据传入的地址和类型去向服务器查询天气代号或者天气信息（简述）
     *  @param   address（传入的地址）
     *  @param   type（查询类型）
     */
    private static void queryFromServer(final String address, final int type, final Handler mHandler) {

        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                if (response.contains("AP")) { // 如果返回的数据包含“AP”则说明没有正常访问，弹出获取信息失败提示
                    LogUtil.d("queryWeather", "queryFromServer AP " + response);
                    Message message = new Message();
                    message.what = GET_INFO_FAIL;
                    mHandler.sendMessage(message);
                } else {
                    // 处理服务器返回的天气信息
                    LogUtil.d("weatherTest", "queryFromServer  handleWeatherResponse START");
                    Utility.handleWeatherResponse(MyApplication.getContext(), response, type, mHandler);
                }
            }

            @Override
            public void onError(Exception e) {
                Message message = new Message();
                message.what = GET_INFO_FAIL;
                mHandler.sendMessage(message);
            }
        });
    }
}
