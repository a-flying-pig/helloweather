package com.helloweather.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.helloweather.app.R;
import com.helloweather.app.db.HelloWeatherDB;
import com.helloweather.app.gson.CityInfo;
import com.helloweather.app.gson.DailyWeatherInfo;
import com.helloweather.app.gson.RealTimeWeatherInfo;
import com.helloweather.app.model.City;

/**
 * @author HuaZhu
 *         created at 2016-11-11 9:57
 * @brief 工具类，用于解析和处理服务器返回的省市县数据（其格式为JSON数据）
 */
public class Utility {

    private static final int REAL_TIME_WEATHER = 0;

    private static final int DAILY_WEATHER = 1;

    private static final int QUERY_NOW_SUCCEED = 1;

    private static final int QUERY_DAILY_SUCCEED = 2;

    private static int i = 0;

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
    public static void handleWeatherResponse(Context context, String response, int type, Handler mHandler) {
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
        if (type == REAL_TIME_WEATHER) { // 实时的天气信息处理
            RealTimeWeatherInfo realTimeWeatherInfo = gson.fromJson(response, RealTimeWeatherInfo.class);
            if (realTimeWeatherInfo != null) {
                RealTimeWeatherInfo.ResultInfo resultInfo = realTimeWeatherInfo.results.get(0);
                String cityId = resultInfo.location.id;
                String cityName = resultInfo.location.name;
                String publishTime = resultInfo.last_update;
                String nowWeatherCode = resultInfo.now.code;
                String nowWeatherDesp = resultInfo.now.text;
                String nowTemp = resultInfo.now.temperature;
                // 存储实时信息
                saveNowWeatherInfo(context, cityId, cityName, publishTime, nowWeatherCode, nowWeatherDesp, nowTemp);
                Message message = new Message();
                message.what = QUERY_NOW_SUCCEED;
                mHandler.sendMessage(message);
                LogUtil.d("handlertest", "QUERY_NOW_SUCCEED send msg executed");
            }
        } else if (type == DAILY_WEATHER) { // 几天的天气信息数据处理
            DailyWeatherInfo dailyWeatherInfo = gson.fromJson(response, DailyWeatherInfo.class);
            if (dailyWeatherInfo != null) {
                for (int i = 0; i < dailyWeatherInfo.results.size(); i++) {
                    DailyWeatherInfo.ResultsInfo resultsInfo = dailyWeatherInfo.results.get(i);
                    // 第一天的天气信息
                    String firstDate = resultsInfo.daily.get(0).date;
                    String firstDayDesp = resultsInfo.daily.get(0).text_day;
                    String firstDayCode = resultsInfo.daily.get(0).code_day;
                    String firstNightDesp = resultsInfo.daily.get(0).text_night;
                    String firstNightCode = resultsInfo.daily.get(0).code_night;
                    String firstTemp1 = resultsInfo.daily.get(0).low;
                    String firstTemp2 = resultsInfo.daily.get(0).high;
                    String order = "first";
                    // 存储第一天的天气信息
                    saveDailyWeatherInfo(context, firstDate, firstDayDesp, firstDayCode, firstNightDesp, firstNightCode, firstTemp1, firstTemp2, order);
                    // 第二天的天气信息
                    String secondDate = resultsInfo.daily.get(1).date;
                    String secondDayDesp = resultsInfo.daily.get(1).text_day;
                    String secondDayCode = resultsInfo.daily.get(1).code_day;
                    String secondNightDesp = resultsInfo.daily.get(1).text_night;
                    String secondNightCode = resultsInfo.daily.get(1).code_night;
                    String secondTemp1 = resultsInfo.daily.get(1).low;
                    String secondTemp2 = resultsInfo.daily.get(1).high;
                    order = "second";
                    // 存储第二天的天气信息
                    saveDailyWeatherInfo(context, secondDate, secondDayDesp, secondDayCode, secondNightDesp, secondNightCode, secondTemp1, secondTemp2, order);
                    // 第三天的天气信息
                    String thirdDate = resultsInfo.daily.get(2).date;
                    String thirdDayDesp = resultsInfo.daily.get(2).text_day;
                    String thirdDayCode = resultsInfo.daily.get(2).code_day;
                    String thirdNightDesp = resultsInfo.daily.get(2).text_night;
                    String thirdNightCode = resultsInfo.daily.get(2).code_night;
                    String thirdTemp1 = resultsInfo.daily.get(2).low;
                    String thirdTemp2 = resultsInfo.daily.get(2).high;
                    order = "third";
                    // 存储第三天的天气信息
                    saveDailyWeatherInfo(context, thirdDate, thirdDayDesp, thirdDayCode, thirdNightDesp, thirdNightCode, thirdTemp1, thirdTemp2, order);
                    Message message = new Message();
                    message.what = QUERY_DAILY_SUCCEED;
                    mHandler.sendMessage(message);
                    LogUtil.d("handlertest", "QUERY_DAILY_SUCCEED send msg executed");
                }
            }
        }
    }

    /**
     *  
     *
     * @brief 将服务器返回的实时天气数据经解析后的所有天气信息储存到SharePreferences文件中（简述）
     */
    public static void saveNowWeatherInfo(Context context, String cityId, String cityName, String publishTime, String nowWeatherCode, String nowWeatherDesp, String nowTemp) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_id", cityId);
        editor.putString("city_name", cityName);
        editor.putString("publish_time", publishTime);
        editor.putString("now_weather_code", nowWeatherCode);
        editor.putString("now_weather_desp", nowWeatherDesp);
        editor.putString("now_temp", nowTemp);
        editor.apply();
    }

    /**
     *  
     *
     * @brief 将服务器返回的几天的天气数据经解析后的所有天气信息储存到SharePreferences文件中（简述）
     */
    public static void saveDailyWeatherInfo(Context context, String date, String dayDesp, String dayCode, String nightDesp, String nightCode, String temp1, String temp2, String order) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(order + "_date", date);
        editor.putString(order + "_day_desp", dayDesp);
        editor.putString(order + "_day_code", dayCode);
        editor.putString(order + "_night_desp", nightDesp);
        editor.putString(order + "_night_code", nightCode);
        editor.putString(order + "_temp1", temp1);
        editor.putString(order + "_temp2", temp2);
        editor.apply();
    }

    /**
     *  
     *
     * @brief 将获取的天气现象图片代码转换为图片的ID地址（简述）
     *  @param   pictureCode（获取的天气现象图片代码）
     */
    public static int parsePictureId(String pictureCode) {
        int pictureId = R.drawable.nity_nine;
        switch (pictureCode) {
            case "0":
                pictureId = R.drawable.zero;
                break;
            case "1":
                pictureId = R.drawable.one;
                break;
            case "2":
                pictureId = R.drawable.two;
                break;
            case "3":
                pictureId = R.drawable.three;
                break;
            case "4":
                pictureId = R.drawable.four;
                break;
            case "5":
                pictureId = R.drawable.five;
                break;
            case "6":
                pictureId = R.drawable.six;
                break;
            case "7":
                pictureId = R.drawable.seven;
                break;
            case "8":
                pictureId = R.drawable.eight;
                break;
            case "9":
                pictureId = R.drawable.nine;
                break;
            case "10":
                pictureId = R.drawable.ten;
                break;
            case "11":
                pictureId = R.drawable.eleven;
                break;
            case "12":
                pictureId = R.drawable.twelve;
                break;
            case "13":
                pictureId = R.drawable.thirteen;
                break;
            case "14":
                pictureId = R.drawable.fourteen;
                break;
            case "15":
                pictureId = R.drawable.fifteen;
                break;
            case "16":
                pictureId = R.drawable.sixteen;
                break;
            case "17":
                pictureId = R.drawable.seventeen;
                break;
            case "18":
                pictureId = R.drawable.eighteen;
                break;
            case "19":
                pictureId = R.drawable.nineteen;
                break;
            case "20":
                pictureId = R.drawable.twenty;
                break;
            case "21":
                pictureId = R.drawable.twenty_one;
                break;
            case "22":
                pictureId = R.drawable.thirty_two;
                break;
            case "23":
                pictureId = R.drawable.twenty_three;
                break;
            case "24":
                pictureId = R.drawable.twenty_four;
                break;
            case "25":
                pictureId = R.drawable.twenty_five;
                break;
            case "26":
                pictureId = R.drawable.twenty_six;
                break;
            case "27":
                pictureId = R.drawable.twenty_seven;
                break;
            case "28":
                pictureId = R.drawable.twenty_eight;
                break;
            case "29":
                pictureId = R.drawable.twenty_nine;
                break;
            case "30":
                pictureId = R.drawable.thirty;
                break;
            case "31":
                pictureId = R.drawable.thirty_one;
                break;
            case "32":
                pictureId = R.drawable.thirty_two;
                break;
            case "33":
                pictureId = R.drawable.thirty_three;
                break;
            case "34":
                pictureId = R.drawable.thirty_four;
                break;
            case "35":
                pictureId = R.drawable.thirty_five;
                break;
            case "36":
                pictureId = R.drawable.thirty_six;
                break;
            case "37":
                pictureId = R.drawable.thirty_seven;
                break;
            case "38":
                pictureId = R.drawable.thirty_eight;
                break;
            case "99":
                pictureId = R.drawable.nity_nine;
                break;
            default:
                break;
        }
        return pictureId;
    }
}
