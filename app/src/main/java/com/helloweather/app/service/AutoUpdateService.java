package com.helloweather.app.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

import com.helloweather.app.receiver.AlarmReceiver;
import com.helloweather.app.util.LogUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author HuaZhu
 *         created at 2016-11-15 11:58
 * @brief 实现后台自动更新天气
 */
public class AutoUpdateService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d("updateService", "onStartCommand execute");
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 发送广播，更新天气
                Intent intent1 = new Intent("com.app.helloweather.MY_UI_BROADCAST");
                sendBroadcast(intent1);
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                LogUtil.d("updateService", "sendBroadcast(MyUiBroadcast) executed" + df.format(new Date()));
            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int aMinute = 30*1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + aMinute;
        Intent intent1 = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent1, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }
}
