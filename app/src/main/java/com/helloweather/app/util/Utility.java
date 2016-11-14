package com.helloweather.app.util;

import android.text.TextUtils;

import com.helloweather.app.db.HelloWeatherDB;
import com.helloweather.app.model.City;
import com.helloweather.app.model.Country;
import com.helloweather.app.model.Province;

/**
 *@brief  工具类，用于解析和处理服务器返回的省市县数据（其格式为"代号|城市，代号|城市"）
 *@author HuaZhu
 *created at 2016-11-11 9:57
 */
public class Utility {

   /** 
    * @brief    解析和处理服务器返回的省级数据（简述）
    * @param    helloWeatherDB（数据库实例）
    * @param    response（服务器返回的数据）
    * @return   获取到数据返回值为true，未获取数据返回值为false（return描述返回值）
    */
   public synchronized static boolean handleProvincesRespnse(HelloWeatherDB helloWeatherDB, String response) {
       if (!TextUtils.isEmpty(response)) {
           String[] allProvinces = response.split(",");
           if (allProvinces != null && allProvinces.length > 0) {
               for (String p : allProvinces) {
                   String[] array = p.split("\\|");
                   Province province = new Province();
                   province.setProvinceCode(array[0]);
                   province.setProvinceName(array[1]);
                   helloWeatherDB.saveProvince(province);
               }
           }
           return true;
       }
       return false;
   }

    /** 
     * @brief    解析和处理服务器返回的市级数据（简述）
     * @param    helloWeatherDB（数据库实例）
     * @param    response（服务器返回的数据）
     * @return   获取到数据返回值为true，未获取数据返回值为false（return描述返回值）
     */
    public synchronized static boolean handleCityResponse(HelloWeatherDB helloWeatherDB, String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            LogUtil.d("ceshi", "handleCityResponse" + response);
            if (allCities != null && allCities.length > 0) {
                for (String c : allCities) {
                    String[] array = c.split("\\|");
                    City city = new City();
                    LogUtil.d("ceshi", "handleCityResponse" + array[1]);
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    helloWeatherDB.saveCity(city);
                }
            }
            return true;
        }
        return false;
    }

    /** 
     * @brief   解析和处理服务器返回的县级数据（简述）
     * @param   helloWeatherDB（数据库实例）
     * @param   response（服务器返回的数据）
     * @return  获取到数据返回值为true，未获取数据返回值为false（return描述返回值）
     */
    public static boolean handleCountryResponse(HelloWeatherDB helloWearherDB, String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCountries = response.split(",");
            if (allCountries != null && allCountries.length > 0) {
                for (String c : allCountries) {
                    String[] array = c.split("\\|");
                    Country country = new Country();
                    country.setCountryCode(array[0]);
                    country.setCountryName(array[1]);
                    country.setCityId(cityId);
                    helloWearherDB.saveCountry(country);
                    LogUtil.d("ceshi", "saveCountry" );
                }
                return true;
            }
        }
        return false;
    }
}
