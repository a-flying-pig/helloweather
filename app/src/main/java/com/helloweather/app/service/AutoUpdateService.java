package com.helloweather.app.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.helloweather.app.receiver.AlarmReceiver;
import com.helloweather.app.util.LogUtil;
import com.helloweather.app.util.MyApplication;
import com.helloweather.app.util.QueryUtility;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author HuaZhu
 *         created at 2016-11-15 11:58
 * @brief 实现后台自动更新天气
 */
public class AutoUpdateService extends Service {

    private static final int QUERY_NOW_SUCCEED = 1;
    private static final int QUERY_DAILY_SUCCEED = 2;

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
                Looper.prepare();
                Handler mHandler = new Handler(Looper.myLooper());
                // 更新天气
                SharedPreferences prfs = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
                String cityId = prfs.getString("city_id", "");
                QueryUtility.queryWeatherInfo(cityId, new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == QUERY_NOW_SUCCEED) {
                            LogUtil.d("ttttt", "AutoUpdateService QUERY_NOW_SUCCEED");
                        }
                        if (msg.what == QUERY_DAILY_SUCCEED) {
                            LogUtil.d("ttttt", "AutoUpdateService QUERY_DAILY_SUCCEED");
                        }
                    }
                });
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                LogUtil.d("ttttt", "AutoUpdateService start" + df.format(new Date()));
                Looper.loop();
            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int aMinute = 10*60*1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + aMinute;
        Intent intent1 = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent1, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }
}
