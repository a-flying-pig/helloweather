package com.helloweather.app.model;

/**
 *@brief  Country类
 *@author HuaZhu
 *created at 2016-11-10 18:02
 */
public class Country {
    
    private int id;
    
    private String countryName;
    
    private String countryCode;
    
    private int cityId;
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getId() {
        return id;
    }
    
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
    
    public String getCountryName() {
        return countryName;
    }
    
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
    
    public String getCountryCode() {
        return countryCode;
    }
    
    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
    
    public int getCityId() {
        return cityId;
    }
}
