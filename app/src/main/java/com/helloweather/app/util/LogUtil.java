package com.helloweather.app.util;

import android.util.Log;

/**
 * @author HuaZhu
 *         created at 2016-11-14 18:20
 * @brief 日志打印工具
 * 本类是一个单例类
 */
public class LogUtil {

    public static final int VERBOSE = 1;

    public static final int DEBUG = 2;

    public static final int NOTHING = 6;

    public static final int LEVEL = VERBOSE;

    public static void d(String tag, String msg) {
        if (LEVEL <= DEBUG) {
            Log.d(tag, msg);
        }
    }
}
