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
                    connection.setDoOutput(false);
                    int status = connection.getResponseCode();
                    LogUtil.d("ceshi", "status " + status);
                    InputStream in = null;
                    if (status == 200) { // 如果是200(访问成功)，获取正常的输入流
                        in = connection.getInputStream();
                        LogUtil.d("ceshi", "in " + in.toString());
                    } else { // 如果不是200，比如说经常出现的403（拒绝访问），则获取错误流
                        in = connection.getErrorStream();
                        LogUtil.d("ceshi", "in " + in.toString());
                    }
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    if (listener != null) {
                        // 回调onFinish（）方法
//                        int getId = id;
                        LogUtil.d("ceshi", "response" + response.toString());
                        listener.onFinish(response.toString());
                    }
                    LogUtil.d("ceshi", "response.toString()" + response.toString());
                } catch (Exception e) {
                    if (listener != null) {
                        // 回调onError（）方法
                        listener.onError(e);
                    }
                    LogUtil.d("ceshi", "e.getMessage");
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
