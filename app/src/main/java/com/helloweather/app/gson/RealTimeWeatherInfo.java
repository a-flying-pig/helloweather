package com.helloweather.app.gson;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HuaZhu
 *         created at 2016-11-21 10:21
 * @brief 实时天气JSON数据对应的javabean类，用于取出服务器返回的实时天气数据
 */
public class RealTimeWeatherInfo {

    public List<ResultInfo> results = new ArrayList<>();

    public class ResultInfo {
        public Location location;
        public Now now;
        public String last_update;

    }

    public class Location {
        public String id;
        public String name;
        public String country;
        public String timezone;
        public String timezone_offset;
    }

    public class Now {
        public String text;
        public String code;
        public String temperature;
    }
}
