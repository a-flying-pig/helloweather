package com.helloweather.app.gson;

import java.util.ArrayList;

/**
 * @author HuaZhu
 *         created at 2016-11-17 14:56
 * @brief JAVAbean类，用于处理返回的天气信息JSON数据
 */
public class WeatherInfo {

    public ArrayList<ResultsInfo> results;

    public static class ResultsInfo{
        public Location location;
        public ArrayList<DailyInfo> daily;
        public String last_update;
    }
    public static class Location {
        public String id;
        public String name;
    }
    public static class DailyInfo {
        public String date;
        public String text_day;
        public String high;
        public String low;
    }
}
