package com.helloweather.app.gson;

import java.util.ArrayList;

/**
 * @author HuaZhu
 *         created at 2016-11-17 17:34
 * @brief JAVAbean类，用于处理返回的城市信息JSON数据
 */
public class CityInfo {
    public ArrayList<CityResultsInfo> results;

    public static class CityResultsInfo {
        public String id;
        public String name;
        public String country;
        public String path;
        public String timezone;
        public String timezone_offset;
    }
}
