package com.helloweather.app.util;

/**
 * Created by Administrator on 2016-11-22.
 * 固定几秒内不能重复刷新的工具类
 */
public class NoDoubleClickUtil {

    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static  long lastClickTime;
    public static boolean isDoubleClick() {
        boolean isClick;
        long currentTime = System.currentTimeMillis();
        if ((currentTime - lastClickTime) > MIN_CLICK_DELAY_TIME ) {
            LogUtil.d("doubleClick", "isDoubleClick currentTime" + " " + currentTime);
            LogUtil.d("doubleClick", "isDoubleClick lastClickTime" + " " +lastClickTime);
            lastClickTime = currentTime;
            isClick = false;
        } else {
            isClick = true;
        }
        return isClick;
    }
}
