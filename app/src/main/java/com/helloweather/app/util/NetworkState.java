package com.helloweather.app.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Administrator on 2016-11-30.
 * 网络是否可用
 */
public class NetworkState {

    public static boolean IsNetworkAvailable () {
        boolean isNetworkAvailable = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable()) {
            isNetworkAvailable = true;
        }
        return isNetworkAvailable;
    }
}
