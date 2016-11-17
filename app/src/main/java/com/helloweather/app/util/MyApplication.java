package com.helloweather.app.util;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2016-11-16.
 */
public class MyApplication extends Application {

    private static Context context;

    private static String myKey = "yw7jedk529ilgdfk";

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

    public static String getMyKey() {
        return myKey;
    }
}
