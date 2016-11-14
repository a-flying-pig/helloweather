package com.helloweather.app.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.helloweather.app.R;
import com.helloweather.app.db.HelloWeatherDB;
import com.helloweather.app.model.City;
import com.helloweather.app.model.Country;
import com.helloweather.app.model.Province;
import com.helloweather.app.util.HttpCallbackListener;
import com.helloweather.app.util.HttpUtil;
import com.helloweather.app.util.LogUtil;
import com.helloweather.app.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 *@brief  用于遍历省市县数据
 *@author HuaZhu
 *created at 2016-11-11 11:31
 */
public class ChooseAreaActivity extends AppCompatActivity {

    public static final int LEVEL_PROVINCE = 0;

    public static final int LEVEL_CITY = 1;

    public static final int LEVEL_COUNTRY = 2;
    
    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private HelloWeatherDB helloWeatherDB;
    private List<String> dataList = new ArrayList<String>();
    
    /** 省列表 */
    private List<Province> provinceList;
    /** 市刘表 */
    private List<City> cityList;
    /** 县列表 */
    private List<Country> countryList;

    /** 选中的省份 */
    private Province selectedProvince;
    /** 选中的城市 */
    private City selectedCity;
    /** 当前选中的级别 */
    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_area);
        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        helloWeatherDB = HelloWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCountries();
                }
            }
        });
        queryProvinces(); // 加载省级数据
    }
    
    /** 
     * @brief   查询所有的省，优先从数据库查，如果没有查询到再去服务器上查询（简述）
     */
    private void queryProvinces() {
        provinceList = helloWeatherDB.loadProvinces();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(R.string.chinese);
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer(null, "province");
        }
    }

    /** 
     * @brief   查询选中的所有的市，优先从数据库查，没有查询到再去服务器查询（简述）
     */
    private void queryCities() {
        cityList = helloWeatherDB.loadCities(selectedProvince.getId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        } else {
            LogUtil.d("ceshi", "queryCities" + cityList.size());
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }

    /** 
     * @brief   查询选中的所有的县，优先从数据库查，没有查询到再去服务器查询（简述）
     */
    private void queryCountries() {
        countryList = helloWeatherDB.loadCountries(selectedCity.getId());
        if (countryList.size() > 0) {
            dataList.clear();
            for (Country country : countryList) {
                dataList.add(country.getCountryName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTRY;
        } else {
            LogUtil.d("ceshi", "queryCountries" + countryList.size());
            queryFromServer(selectedCity.getCityCode(), "country");
        }
    }

    /** 
     * @brief   根据传入的代号和类型从服务器上查询省市县数据（简述）
     * @param   code（省或市或县代号）
     * @param   type（省或市或县）
     */
    private void queryFromServer(final String code, final String type) {
        String address;
        if (!TextUtils.isEmpty(code)) {
            address = "http://10.0.2.2:8080/city" + code + ".xml" /*"http://www.weather.com.cn/data/list3/city" + code + ".xml"*/;
        } else address = "http://10.0.2.2:8080/city.xml"; /*address = "http://www.weather.com.cn/data/list3/city.xml";*/
        LogUtil.d("ceshi" , "showProgressDialog" + address);
        showProgressDialog();
//        final int getId = id;


        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvincesRespnse(helloWeatherDB, response);
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(helloWeatherDB, response, selectedProvince.getId());
                } else if ("country".equals(type)) {
                    result = Utility.handleCountryResponse(helloWeatherDB, response, selectedCity.getId());
                }
                if (result) {
                    // 通过runOnUiThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("country".equals(type)) {
                                queryCountries();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                // 通过runOnUiThread（）方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /** 
     * @brief   显示进度对话框（简述）
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /** 
     * @brief   关闭进度对话框（简述）
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /** 
     * @brief   捕获Back按键，根据当前的级别来判断，此时应返回市、省列表还是直接退出（简述）
     */
    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_COUNTRY) {
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        } else {
            finish();
        }
    }
}
