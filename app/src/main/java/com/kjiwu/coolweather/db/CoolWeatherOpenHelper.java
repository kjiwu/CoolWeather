package com.kjiwu.coolweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kjiwu on 16/6/7.
 */
public class CoolWeatherOpenHelper extends SQLiteOpenHelper {

    public static final String IdColumnName = "id";

    public static final String ProvinceTableName = "Province";
    public static final String ProvinceNameColumnName = "province_name";
    public static final String ProvinceCodeColumnName = "province_code";

    public static final String CityTableName = "City";
    public static final String CityNameColumnName = "city_name";
    public static final String CityCodeColumnName = "city_code";
    public static final String ProvinceIdColumnName = "province_id";

    public static final String CountyTableName = "County";
    public static final String CountyNameColumnName = "county_name";
    public static final String CountyCodeColumnName = "county_code";
    public static final String CityIdColumnName = "city_id";


    public static final String CREATE_PROVINCE = "create table Province ("
            + "id integer primary key autoincrement, "
            + "province_name text, "
            + "province_code text)";

    public static final String CREATE_CITY = "create table City ("
            + "id integer primary key autoincrement, "
            + "city_name text, "
            + "city_code text, "
            + "province_id integer)";

    public static final String CREATE_COUNTY = "create table County ("
            + "id integer primary key autoincrement, "
            + "county_name text, "
            + "county_code text, "
            + "city_id integer)";

    public CoolWeatherOpenHelper(Context context, String name,
                                 SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_COUNTY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
