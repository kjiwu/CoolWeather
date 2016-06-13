package com.kjiwu.coolweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kjiwu.coolweather.model.City;
import com.kjiwu.coolweather.model.County;
import com.kjiwu.coolweather.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kjiwu on 16/6/7.
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


    public void saveProvince(Province province) {
        if(null != province) {
            ContentValues values = new ContentValues();
            values.put(CoolWeatherOpenHelper.ProvinceNameColumnName, province.getProvinceName());
            values.put(CoolWeatherOpenHelper.ProvinceCodeColumnName, province.getProvinceCode());
            db.insert(CoolWeatherOpenHelper.ProvinceTableName, null, values);
        }
    }

    public List<Province> loadProvinces() {
        List<Province> list = new ArrayList<Province>();
        Cursor cursor = db.query(CoolWeatherOpenHelper.ProvinceTableName,
                                null, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex(CoolWeatherOpenHelper.IdColumnName)));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex
                        (CoolWeatherOpenHelper.ProvinceNameColumnName)));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex
                        (CoolWeatherOpenHelper.ProvinceCodeColumnName)));
                list.add(province);
            } while (cursor.moveToNext());
        }

        return list;
    }


    public void saveCity(City city) {
        if(null != city) {
            ContentValues values = new ContentValues();
            values.put(CoolWeatherOpenHelper.CityNameColumnName, city.getCityName());
            values.put(CoolWeatherOpenHelper.CityCodeColumnName, city.getCityCode());
            values.put(CoolWeatherOpenHelper.ProvinceIdColumnName, city.getProvinceId());
            db.insert(CoolWeatherOpenHelper.CityTableName, null, values);
        }
    }

    public List<City> loadCities(int provinceId) {
        List<City> cities = new ArrayList<City>();

        Cursor cursor = db.query(CoolWeatherOpenHelper.CityTableName, null,
                CoolWeatherOpenHelper.ProvinceIdColumnName + " = ?",
                new String[] { String.valueOf(provinceId) }, null, null, null);

        if(cursor.moveToFirst()) {
            City city = new City();
            city.setId(cursor.getInt(cursor.getColumnIndex(CoolWeatherOpenHelper.IdColumnName)));
            city.setCityName(cursor.getString(cursor.getColumnIndex(CoolWeatherOpenHelper
                    .CityNameColumnName)));
            city.setCityCode(cursor.getString(cursor.getColumnIndex(CoolWeatherOpenHelper
                    .CityCodeColumnName)));
            city.setProvinceId(cursor.getInt(cursor.getColumnIndex(CoolWeatherOpenHelper
                    .ProvinceIdColumnName)));
            cities.add(city);
        }

        return cities;
    }


    public void saveCounty(County county) {
        if(null != county) {
            ContentValues values = new ContentValues();
            values.put(CoolWeatherOpenHelper.CountyNameColumnName, county.getCountyName());
            values.put(CoolWeatherOpenHelper.CountyCodeColumnName, county.getCountyCode());
            values.put(CoolWeatherOpenHelper.CityIdColumnName, county.getCityId());
            db.insert(CoolWeatherOpenHelper.CountyTableName, null, values);
        }
    }

    public List<County> loadCounties(int cityId) {
        List<County> counties = new ArrayList<County>();

        Cursor cursor = db.query(CoolWeatherOpenHelper.CountyTableName, null,
                CoolWeatherOpenHelper.CityIdColumnName + " = ?",
                new String[] { String.valueOf(cityId) }, null, null, null);

        if(cursor.moveToFirst()) {
            County county = new County();
            county.setId(cursor.getInt(cursor.getColumnIndex(CoolWeatherOpenHelper.IdColumnName)));
            county.setCountyName(cursor.getString(cursor.getColumnIndex(CoolWeatherOpenHelper
                    .CountyNameColumnName)));
            county.setCountyCode(cursor.getString(cursor.getColumnIndex(CoolWeatherOpenHelper
                    .CountyCodeColumnName)));
            county.setCityId(cursor.getInt(cursor.getColumnIndex(CoolWeatherOpenHelper
                    .CityIdColumnName)));
            counties.add(county);
        }

        return counties;
    }
}
