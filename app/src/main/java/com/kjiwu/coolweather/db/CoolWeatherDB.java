package com.kjiwu.coolweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by wulei on 16/6/7.
 */
public class CoolWeatherDB {

    public static final String DB_NAME = "cool_weather";

    public static final int VERSION = 1;

    private static CoolWeatherDB sCoolWeatherDB;

    private SQLiteDatabase db;

    public CoolWeatherDB(Context context) {
        CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context, DB_NAME,
                null,
                VERSION);
        this.db = dbHelper.getWritableDatabase();
    }

    public synchronized static CoolWeatherDB getInstance(Context context) {
        if(null == sCoolWeatherDB) {
            sCoolWeatherDB = new CoolWeatherDB(context);
        }

        return sCoolWeatherDB;
    }
}
