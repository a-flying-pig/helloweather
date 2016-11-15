package com.helloweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author HuaZhu
 *         created at 2016-11-11 9:41
 * @brief 服务器交互类，从服务器请求数据
 */
public class HttpUtil {
    public static void sendHttpRequest(final String address, final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    LogUtil.d("ceshi", "url " + url);
                    connection = (HttpURLConnection) url.openConnection();
                    LogUtil.d("ceshi", "connection " + connection);
                    connection.setRequestMethod("GET");
                    LogUtil.d("ceshi", "request ");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    LogUtil.d("ceshi", "read ");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    InputStream in = connection.getInputStream();
                    LogUtil.d("ceshi", "in " + in.toString());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    if (listener != null) {
                        // 回调onFinish（）方法
//                        int getId = id;
                        LogUtil.d("ceshi", response.toString());
                        listener.onFinish(response.toString());
                    }
                    LogUtil.d("ceshi", "response.toString()" + response.toString());
                } catch (Exception e) {
                    if (listener != null) {
                        // 回调onError（）方法
                        listener.onError(e);
                    }
                    LogUtil.d("ceshi", "e.getMessage" + e.getMessage());
                }
            }
        }).start();
    }
}
