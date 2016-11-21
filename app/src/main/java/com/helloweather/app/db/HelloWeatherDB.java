package com.helloweather.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.helloweather.app.model.City;
import com.helloweather.app.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HuaZhu
 *         created at 2016-11-10 18:04
 * @brief 将一些常用的数据库操作封装起来
 * 本类是一个单例类
 */
public class HelloWeatherDB {

    /**
     * 数据库名
     */
    public static final String DB_NAME = "hello_weather";

    /**
     * 数据库版本
     */
    public static final int VERSION = 1;

    private static HelloWeatherDB helloWeathDB;

    private SQLiteDatabase db;

    /**
     * 将构造方法私有化 （阻止外部实例化新对象，从头到尾只有一个对象的实例，保证全局只有一个实例）
     */
    private HelloWeatherDB(Context context) {
        HelloWeatherOpenHelper dbHelper = new HelloWeatherOpenHelper(context, DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 获取HelloWeatherDB实例(保证全局只有一个实例,单例设计）
     */
    public synchronized static HelloWeatherDB getInstance(Context context) {
        if (helloWeathDB == null) {
            helloWeathDB = new HelloWeatherDB(context);
        }
        return helloWeathDB;
    }

    /**
     *  
     *
     * @brief 将City实例存储到数据库（简述）
     *  @param    city（City实例）
     */
    public void saveCity(City city) {

        ContentValues values = new ContentValues();
        values.put("city_id", city.getCityId());
        values.put("city_name", city.getCityName());
        values.put("city_path", city.getCityPath());
        LogUtil.d("ceshi", "saveCity" + city.getCityName());
        db.insert("City", null, values);
//        db.execSQL("insert City (city_id,city_name,city_path) values(?,?,?)", new String[]{city.getCityId(), city.getCityName(), city.getCityPath()});
    }

    /**
     *  
     *
     * @brief 从数据库读取所有的城市信息（简述）
     */
    public List<City> loadCities() {
        List<City> list = new ArrayList<City>();
        Cursor cursor = db.query("City", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setCityId(cursor.getString(cursor.getColumnIndex("city_id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityPath(cursor.getString(cursor.getColumnIndex("city_path")));
                list.add(city);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    /**
     *  
     *
     * @brief 从数据库删除所有的城市信息（简述）
     */
    public void deleteCities() {
        db.delete("City", null, null);
    }

    /**
     *  
     *
     * @brief 将HistoryCity实例存储到数据库（简述）
     *  @param    historyCity（City实例）
     */
    public void saveHistoryCity(City city) {
        // 查询是否存在，不存在则加入数据库
        String cityId = null;
//        Cursor cursor = db.query("HistoryCity", null, "city_id = ?",new String[] {city.getCityId()}, null, null, null);
        Cursor cursor = db.rawQuery("select * from HistoryCity where city_id = ?", new String[]{city.getCityId()});
        if (cursor.moveToFirst()) {
            do {
                cityId = cursor.getString(cursor.getColumnIndex("city_id"));
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        if (TextUtils.isEmpty(cityId)) {
            ContentValues values = new ContentValues();
            values.put("city_id", city.getCityId());
            values.put("city_name", city.getCityName());
            values.put("city_path", city.getCityPath());
            LogUtil.d("ceshi", "saveCity" + city.getCityName());
            db.insert("HistoryCity", null, values);
//          db.execSQL("insert City (city_id,city_name,city_path) values(?,?,?)", new String[]{city.getCityId(), city.getCityName(), city.getCityPath()});
        }
    }

    /**
     *  
     *
     * @brief 从数据库读取所有的城市信息（简述）
     */
    public List<City> loadHisoryCities() {
        List<City> list = new ArrayList<City>();
        Cursor cursor = db.query("HistoryCity", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setCityId(cursor.getString(cursor.getColumnIndex("city_id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityPath(cursor.getString(cursor.getColumnIndex("city_path")));
                list.add(city);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    /**
     *  
     *
     * @brief 从数据库删除所有的城市信息（简述）
     */
    public void deleteHistoryCities() {
        db.delete("HistoryCity", null, null);
    }
}
