package com.helloweather.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.helloweather.app.service.AutoUpdateService;

/**
 * @author HuaZhu
 *         created at 2016-11-15 12:14
 * @brief 辅助实现定时服务（定时更新天气）的广播
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, AutoUpdateService.class);
        context.startService(intent1);
    }
}
