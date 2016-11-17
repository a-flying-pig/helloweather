package com.helloweather.app.model;

/**
 * @author HuaZhu
 *         created at 2016-11-10 17:45
 * @brief City类
 */
public class City {

    private String cityId;

    private String cityName;

    private String cityPath;

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityPath(String cityPath) {
        this.cityPath = cityPath;
    }

    public String getCityPath() {
        return cityPath;
    }
}
