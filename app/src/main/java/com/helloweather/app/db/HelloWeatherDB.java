package com.helloweather.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.helloweather.app.model.City;
import com.helloweather.app.model.Country;
import com.helloweather.app.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 *@brief  将一些常用的数据库操作封装起来
 * 本类是一个单例类
 *@author HuaZhu
 *created at 2016-11-10 18:04
 */
public class HelloWeatherDB {

    /** 数据库名 */
    public static final String DB_NAME = "hello_weather";

    /** 数据库版本 */
    public static final int VERSION = 1;

    private static HelloWeatherDB helloWeathDB;

    private SQLiteDatabase db;

    /** 将构造方法私有化 （阻止外部实例化新对象，从头到尾只有一个对象的实例，保证全局只有一个实例） */
    private HelloWeatherDB(Context context) {
        HelloWeatherOpenHelper dbHelper = new HelloWeatherOpenHelper(context, DB_NAME, null,VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /** 获取HelloWeatherDB实例(保证全局只有一个实例,单例设计） */
    public synchronized static HelloWeatherDB getInstance(Context context) {
        if (helloWeathDB == null) {
            helloWeathDB = new HelloWeatherDB(context);
        }
        return helloWeathDB;
    }
    
    /** 
     * @brief   将Province实例存储到数据库（简述）
     * @param province （Province实例）
     * @return  （return描述返回值）
     * @see     （本函数参考其它的相关的函数，这里作一个链接）
     * @note     (note描述需要注意的问题)
     */
    public void saveProvince(Province province) {
        ContentValues values = new ContentValues();
        values.put("province_name", province.getProvinceName());
        values.put("province_code", province.getProvinceCode());
        db.insert("Province", null, values);
    }

    /** 
     * @brief    从数据库读取所有省份的信息（简述）
     * @param   （param描述参数）
     * @param   （）
     * @return  返回一个List<Province>（return描述返回值）
     * @see     （本函数参考其它的相关的函数，这里作一个链接）
     * @note     (note描述需要注意的问题)
     */
    public List<Province> loadProvinces() {
        List<Province> list = new ArrayList<Province>();
        Cursor cursor = db.query("Provnce", null, null, null, null, null,null);
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("provinceName")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("provinceCode")));
                list.add(province);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    /** 
     * @brief    将City实例存储到数据库（简述）
     * @param    city（City实例）
     * @param   （）
     * @return  （return描述返回值）
     * @see     （本函数参考其它的相关的函数，这里作一个链接）
     * @note     (note描述需要注意的问题)
     */
    public void saveCity(City city) {
        ContentValues values = new ContentValues();
        values.put("city_name", city.getCityName());
        values.put("city_code", city.getCityCode());
        db.insert("City", null, values);
        // db.execSQL("insert City (city_name,city_code) values(?, ?)", new String[]{city.getCityName(),city.getCityCode()});
    }

    /** 
     * @brief    从数据库读取某省所有的城市信息（简述）
     * @param    provinceId（省的id）
     * @param   （）
     * @return  （return描述返回值）
     * @see     （本函数参考其它的相关的函数，这里作一个链接）
     * @note     (note描述需要注意的问题)
     */
    public List<City> loadCities(int provinceId) {
        List<City> list = new ArrayList<City>();
        Cursor  cursor = db.query("Province", null, "provinceId = ?", new String[]{String.valueOf(provinceId)}, null, null,null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("cityName")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("cityCode")));
                list.add(city);
            } while (cursor.moveToNext());
        } if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    /** 
     * @brief    将Country实例存储到数据库（简述）
     * @param    country（Country实例）
     * @param   （）
     * @return  （return描述返回值）
     * @see     （本函数参考其它的相关的函数，这里作一个链接）
     * @note     (note描述需要注意的问题)
     */
    public void saveCountry(Country country) {
        ContentValues values = new ContentValues();
        values.put("country_name", country.getCountryName());
        values.put("country_code", country.getCountryCode());
        db.insert("Country", null, values);
//        db.execSQL("insert Country(country_name,country_code) values(?,?)",new String[]{country.getCountryName(),country.getCountryCode()});
    }
    
    /** 
     * @brief    从数据库中取出某城市下所有县的信息（简述）
     * @param    cityId（城市的id）
     * @param   （）
     * @return  （return描述返回值）
     * @see     （本函数参考其它的相关的函数，这里作一个链接）
     * @note     (note描述需要注意的问题)
     */
    public List<Country> loadCountries(int cityId) {
        List<Country> list = new ArrayList<Country>();
        Cursor cursor = db.query("Country", null,"cityId = ?", new String[]{String.valueOf(cityId)}, null, null,null);
        if (cursor.moveToFirst()) {
            do {
                Country country = new Country();
                country.setId(cursor.getInt(cursor.getColumnIndex("id")));
                country.setCountryName(cursor.getString(cursor.getColumnIndex("countryName")));
                country.setCountryCode(cursor.getString(cursor.getColumnIndex("countryCode")));
                list.add(country);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }
}
