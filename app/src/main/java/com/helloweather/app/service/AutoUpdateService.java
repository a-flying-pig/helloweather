package com.helloweather.app.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.helloweather.app.receiver.AlarmReceiver;
import com.helloweather.app.util.HttpCallbackListener;
import com.helloweather.app.util.HttpUtil;
import com.helloweather.app.util.LogUtil;
import com.helloweather.app.util.MyApplication;
import com.helloweather.app.util.Utility;

/**
 * @author HuaZhu
 *         created at 2016-11-15 11:58
 * @brief 实现后台自动更新天气
 */
public class AutoUpdateService extends Service {

    private static final int UPDATE_UI = 0;

    /**
     * 处理服务发出的消息更新UI
     */
    /*public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_UI:
                    Intent intent = new Intent("com.app.helloweather.MY_UI_BROADCAST");
                    sendBroadcast(intent);
                    LogUtil.d("weatherRefresh", "get message and sendBroadcast");
                    break;
                default:
                    break;
            }
        }
    };*/

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                refreshWeather();
            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int aMinute = 10*1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + aMinute;
        Intent intent1 = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent1, 0);
//        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     *  
     *
     * @brief   更新天气信息（简述）
     */
    private void refreshWeather() {
//        publishTimeText.setText(R.string.synchronizing);
        LogUtil.d("weatherRefresh", "refreshWeather");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String cityId = prefs.getString("city_id", "");
        String address = "https://api.thinkpage.cn/v3/weather/daily.json?key=" + MyApplication.getMyKey() + "&location=" + cityId + "&language=zh-Hans&unit=c&start=0&days=3";
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(AutoUpdateService.this, response);
                Intent intent = new Intent("com.app.helloweather.MY_UI_BROADCAST");
                sendBroadcast(intent);
                LogUtil.d("weatherRefresh", "get message and sendBroadcast");
               /* LogUtil.d("weatherRefresh", "onFinish executed");
                Message message = new Message();
                LogUtil.d("weatherRefresh", "message executed");
                message.what= UPDATE_UI;
                LogUtil.d("weatherRefresh", "message.what executed");
                mHandler.sendMessage(message);
                LogUtil.d("weatherRefresh", "sendMessage executed");*/
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
