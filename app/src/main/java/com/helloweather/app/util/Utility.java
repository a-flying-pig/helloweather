package com.helloweather.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.helloweather.app.db.HelloWeatherDB;
import com.helloweather.app.gson.CityInfo;
import com.helloweather.app.gson.WeatherInfo;
import com.helloweather.app.model.City;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author HuaZhu
 *         created at 2016-11-11 9:57
 * @brief 工具类，用于解析和处理服务器返回的省市县数据（其格式为JSON数据）
 */
public class Utility {

    /**
     *  
     *
     * @brief 解析和处理服务器返回的城市数据（简述）
     *  @param    helloWeatherDB（数据库实例）
     *  @param    response（服务器返回的数据）
     *  @return   获取到数据返回值为true，未获取数据返回值为false（return描述返回值）
     */
    public synchronized static boolean handleCityResponse(HelloWeatherDB helloWeatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
           /* try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String cityId = jsonObject1.getString("id");
                    String cityName = jsonObject1.getString("name");
                    String cityPath = jsonObject1.getString("path");
                    City city = new City();
                    city.setCityId(cityId);
                    city.setCityName(cityName);
                    city.setCityPath(cityPath);
                    HelloWeatherDB.getInstance(MyApplication.getContext()).saveCity(city);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }*/

            Gson gson = new Gson();
            CityInfo cityInfo = gson.fromJson(response, CityInfo.class);
            for (CityInfo.CityResultsInfo cityResultsInfo : cityInfo.results) {
                String cityId = cityResultsInfo.id;
                String cityName = cityResultsInfo.name;
                String cityPath = cityResultsInfo.path;
                City city = new City();
                city.setCityId(cityId);
                city.setCityName(cityName);
                city.setCityPath(cityPath);
                HelloWeatherDB.getInstance(MyApplication.getContext()).saveCity(city);

            }
            LogUtil.d("ceshi", "handleCityResponse" + response);

            return true;
        }
        return false;
    }

    /**
     *  
     *
     * @brief 解析服务器返回的信息数据（JSON格式），并将解析出的数据存储到本地。（简述）
     *  @param   context（上下文，环境）
     *  @param   response（服务器返回的JSON数据）
     */
    public static void handleWeatherResponse(Context context, String response) {
     /*   try {
            JSONObject jsonObject = new JSONObject(response);
            LogUtil.d("weatherTest", "jsonObject " + jsonObject);

            JSONArray  results = jsonObject.getJSONArray("results");
            LogUtil.d("weatherTest", "result " + results);

            JSONObject jsonObject1 = results.getJSONObject(0);

            JSONObject locationJsonObject = jsonObject1.getJSONObject("location");
            LogUtil.d("weatherTest", "locationJsonObject " + locationJsonObject);

            JSONArray daily = jsonObject1.getJSONArray("daily");
            LogUtil.d("weatherTest", "daily " + daily);


            JSONObject firstDayJsonObject = daily.getJSONObject(0);
            LogUtil.d("weatherTest", "handleWeatherResponse  firstDayJsonObject " + firstDayJsonObject);

            String cityName = locationJsonObject.getString("name");
            LogUtil.d("weatherTest", "handleWeatherResponse  cityName " + cityName);

            String temp1 = firstDayJsonObject.getString("low");
            LogUtil.d("weatherTest", "handleWeatherResponse temp1" + temp1);

            String temp2 = firstDayJsonObject.getString("high");
            LogUtil.d("weatherTest", "handleWeatherResponse temp2" + temp2);

            String weatherDesp = firstDayJsonObject.getString("text_day");
            LogUtil.d("weatherTest", "handleWeatherResponse  weatherDesp " + weatherDesp);

            String publishTime = jsonObject1.getString("last_update");
            saveWeatherInfo(context, cityName, temp1, temp2, weatherDesp, publishTime);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        Gson gson = new Gson();
        WeatherInfo weatherInfo =gson.fromJson(response, WeatherInfo.class);
        for (int i = 0; i < weatherInfo.results.size(); i++) {
            WeatherInfo.ResultsInfo resultsInfo = weatherInfo.results.get(i);
            String cityId = resultsInfo.location.id;
            String cityName = resultsInfo.location.name;
            String temp1 = resultsInfo.daily.get(0).low;
            String temp2 = resultsInfo.daily.get(0).high;
            String weatherDesp = resultsInfo.daily.get(0).text_day;
            String publishTime = resultsInfo.last_update;
            saveWeatherInfo(context, cityId, cityName, temp1, temp2, weatherDesp, publishTime);
        }
    }

    /**
     *  
     *
     * @brief 将服务器返回的数据经解析后的所有天气信息储存到SharePreferences文件中（简述）
     *  @param   （param描述参数）
     *  @param   （）
     */
    public static void saveWeatherInfo(Context context, String cityId, String cityName, String temp1, String temp2, String weatherDesp, String publishTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_id", cityId);
        editor.putString("city_name", cityName);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
        editor.putString("current_date", sdf.format(new Date()));
        editor.commit();
    }
}
