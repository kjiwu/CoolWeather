package com.kjiwu.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kjiwu.coolweather.R;
import com.kjiwu.coolweather.db.CoolWeatherDB;
import com.kjiwu.coolweather.model.City;
import com.kjiwu.coolweather.model.County;
import com.kjiwu.coolweather.model.Province;
import com.kjiwu.coolweather.util.HttpCallbackListener;
import com.kjiwu.coolweather.util.HttpUtil;
import com.kjiwu.coolweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kjiwu on 16/6/13.
 */
public class ChooseAreaActivity extends Activity {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog mProgressDialog;
    private TextView mTitleText;
    private ListView mListView;
    private ArrayAdapter<String> mAdapter;
    private CoolWeatherDB mCoolWeatherDB;
    private List<String> dataList = new ArrayList<String>();

    private List<Province> mProvinceList;
    private List<City> mCityList;
    private List<County> mCountyList;

    private Province selectedProvince;
    private City selectedCity;

    private int currentLevel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getBoolean("city_selected", false)) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        mListView = (ListView) findViewById(R.id.list_view);
        mTitleText = (TextView) findViewById(R.id.title_text);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                dataList);
        mListView.setAdapter(mAdapter);

        mCoolWeatherDB =CoolWeatherDB.getInstance(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = mProvinceList.get(position);
                    queryCities();
                }
                else if(currentLevel == LEVEL_CITY) {
                    selectedCity = mCityList.get(position);
                    queryCounties();
                }
                else if(currentLevel == LEVEL_COUNTY) {
                    String countyCode = mCountyList.get(position).getCountyCode();
                    Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                    intent.putExtra("county_code", countyCode);
                    startActivity(intent);
                    finish();
                }
            }
        });

        queryProvinces();
    }

    private void queryProvinces() {
        mProvinceList = mCoolWeatherDB.loadProvinces();
        if(mProvinceList.size() > 0) {
            dataList.clear();
            for(Province p : mProvinceList) {
                dataList.add(p.getProvinceName());
            }

            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mTitleText.setText(R.string.default_countryName);
            currentLevel = LEVEL_PROVINCE;
        }
        else {
            queryFromServer(null, "province");
        }
    }

    private void queryCities() {
        mCityList = mCoolWeatherDB.loadCities(selectedProvince.getId());
        if(mCityList.size() > 0) {
            dataList.clear();
            for(City c : mCityList) {
                dataList.add(c.getCityName());
            }

            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mTitleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }
        else {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }

    private void queryCounties() {
        mCountyList = mCoolWeatherDB.loadCounties(selectedCity.getId());
        if(mCountyList.size() > 0) {
            dataList.clear();
            for(County c : mCountyList) {
                dataList.add(c.getCountyName());
            }

            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mTitleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        }
        else {
            queryFromServer(selectedCity.getCityCode(), "county");
        }
    }

    private void queryFromServer(final String code, final String type) {
        String address;
        if(!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        }
        else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }

        showProgressDialog();

        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if("province".equals(type)) {
                    result = Utility.handleProvincesResponse(mCoolWeatherDB, response);
                }
                else if("city".equals(type)) {
                    result = Utility.handleCitiesResponse(mCoolWeatherDB, response,
                            selectedProvince.getId());
                }
                else if("county".equals(type)) {
                    result = Utility.handleCountiesResponse(mCoolWeatherDB, response,
                            selectedCity.getId());
                }

                if(result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)) {
                                queryProvinces();
                            }
                            else if ("city".equals(type)) {
                                queryCities();
                            }
                            else if("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,
                                "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void showProgressDialog() {
        if(null == mProgressDialog) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("正在加载中……");
            mProgressDialog.setCanceledOnTouchOutside(false);
        }

        mProgressDialog.show();
    }

    private void closeProgressDialog() {
        if(null != mProgressDialog) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if(currentLevel == LEVEL_COUNTY) {
            queryCities();
        }
        else if(currentLevel == LEVEL_CITY) {
            queryProvinces();
        }
        else {
            finish();
        }
    }
}
