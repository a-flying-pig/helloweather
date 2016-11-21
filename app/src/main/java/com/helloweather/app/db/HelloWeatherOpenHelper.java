package com.helloweather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author HuaZhu
 *         created at 2016-11-10 17:28
 * @brief 建表辅助类
 */
public class HelloWeatherOpenHelper extends SQLiteOpenHelper {

    /**
     * @brief City表建表语句
     */
    public static final String CREATE_CITY = "create table City ("
            + "city_id text, "
            + "city_name text, "
            + "city_path text)";

    /**
     * @brief HistoryCity表建表语句
     */
    public static final String CREATE_HISTORYCITY = "create table HistoryCity ("
            + "city_id text, "
            + "city_name text, "
            + "city_path text)";

    /**
     * 构造函数
     *
     * @author HuaZhu
     * created at 2016-11-10 17:30
     */
    public HelloWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_HISTORYCITY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
