package com.helloweather.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.helloweather.app.db.HelloWeatherDB;
import com.helloweather.app.model.City;
import com.helloweather.app.model.Country;
import com.helloweather.app.model.Province;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author HuaZhu
 *         created at 2016-11-11 9:57
 * @brief 工具类，用于解析和处理服务器返回的省市县数据（其格式为"代号|城市，代号|城市"）
 */
public class Utility {

    /**
     *  
     *
     * @brief 解析和处理服务器返回的省级数据（简述）
     *  @param    helloWeatherDB（数据库实例）
     *  @param    response（服务器返回的数据）
     *  @return   获取到数据返回值为true，未获取数据返回值为false（return描述返回值）
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
     *  
     *
     * @brief 解析和处理服务器返回的市级数据（简述）
     *  @param    helloWeatherDB（数据库实例）
     *  @param    response（服务器返回的数据）
     *  @return   获取到数据返回值为true，未获取数据返回值为false（return描述返回值）
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
     *  
     *
     * @brief 解析和处理服务器返回的县级数据（简述）
     *  @param   helloWeatherDB（数据库实例）
     *  @param   response（服务器返回的数据）
     *  @return  获取到数据返回值为true，未获取数据返回值为false（return描述返回值）
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
                    LogUtil.d("ceshi", "saveCountry");
                }
                return true;
            }
        }
        return false;
    }

    /**
     *  
     *
     * @brief 解析服务器返回的JSON数据，并将解析出的数据存储到本地。（简述）
     *  @param   context（上下文，环境）
     *  @param   response（服务器返回的JSON数据）
     */
    public static void handleWeatherResponse(Context context, String response) {
        try {
            JSONObject jsonObject1 = new JSONObject(response);
            LogUtil.d("weatherTest", "handleWeatherResponse " + response);
            JSONObject jsonObject = jsonObject1.getJSONObject("weatherinfo");
            String cityName = jsonObject.getString("city");
            LogUtil.d("weatherTest", "handleWeatherResponse  cityName " + cityName);
            String weatherCode = jsonObject.getString("cityid");
            LogUtil.d("weatherTest", "handleWeatherResponse  cityId " + weatherCode);
            String temp1 = jsonObject.getString("temp1");
            String temp2 = jsonObject.getString("temp2");
            String weatherDesp = jsonObject.getString("weather");
            LogUtil.d("weatherTest", "handleWeatherResponse  weatherDesp " + weatherDesp);
            String publishTime = jsonObject.getString("ptime");
            saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, publishTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  
     *
     * @brief 将服务器返回的数据经解析后的所有天气信息储存到SharePreferences文件中（简述）
     *  @param   （param描述参数）
     *  @param   （）
     */
    public static void saveWeatherInfo(Context context, String cityName, String weatherCode, String temp1, String temp2, String weatherDesp, String publishTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
        editor.putString("current_date", sdf.format(new Date()));
        editor.commit();
    }
}
