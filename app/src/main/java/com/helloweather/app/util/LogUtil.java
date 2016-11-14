package com.helloweather.app.util;

import android.util.Log;

/**
 * Created by Administrator on 2016-11-11.
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
