package com.helloweather.app.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.helloweather.app.R;
import com.helloweather.app.adapter.CityAdapter;
import com.helloweather.app.db.HelloWeatherDB;
import com.helloweather.app.model.City;
import com.helloweather.app.util.HttpCallbackListener;
import com.helloweather.app.util.HttpUtil;
import com.helloweather.app.util.LogUtil;
import com.helloweather.app.util.MyApplication;
import com.helloweather.app.util.Utility;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author HuaZhu
 *         created at 2016-11-11 11:31
 * @brief 用于遍历省市县数据
 */
public class ChooseAreaActivity extends AppCompatActivity implements View.OnClickListener{

    private ProgressDialog progressDialog;
    private EditText queryEdit;
    private Button queryButton;
    private Button deleteHistory;
    private String queryCity;
    private ListView listView;
    private CityAdapter adapter;
    private HelloWeatherDB helloWeatherDB;
    private List<City> dataList = new ArrayList<City>();


    /**
     * 市刘表
     */
    private List<City> cityList;

    /**
     * 是否从WeatherActivity中跳转过来
     */
    private boolean isFromWeatherActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // 已经选择了城市且不是从WeatherActivity跳转过来，才会直接跳转到WeatherActivity
        if (prefs.getBoolean("city_selected", false) && !isFromWeatherActivity) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.choose_area);
        listView = (ListView) findViewById(R.id.list_view);
        queryEdit = (EditText) findViewById(R.id.query_edit);
        queryButton = (Button) findViewById(R.id.query_button);
        deleteHistory = (Button) findViewById(R.id.delete_history_record);
        adapter = new CityAdapter(this, R.layout.city_item, dataList);
        listView.setAdapter(adapter);
        helloWeatherDB = HelloWeatherDB.getInstance(this);
        queryButton.setOnClickListener(this);
        deleteHistory.setOnClickListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 将选中的城市加入到历史纪录
                helloWeatherDB.saveHistoryCity(cityList.get(position));
                String cityId = cityList.get(position).getCityId();
                LogUtil.d("getWeather", "countryCode" + cityList.get(position).getCityId());
                Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                intent.putExtra("cityId", cityId); // 将获得的城市id传入WeatherActivity
                startActivity(intent);
                finish();
            }
        });
        queryHistoryCities();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.query_button:
                queryCity = queryEdit.getText().toString().replaceAll("\\s*", "");
                if (TextUtils.isEmpty(queryCity)) {
                    Toast.makeText(this, R.string.no_input_city,Toast.LENGTH_SHORT).show();
                } else {
                    try { // 将输入的数据转换成"UTF-8"类型，为了是输入的是城市时也能搜索
//                        queryCity = "%E6%88%90%E9%83%BD";
                        queryCity = URLEncoder.encode(queryCity,"UTF-8");
//                        queryCity = URLDecoder.decode(queryCity, "UTF-8");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    queryFromServer(queryCity);
                }
                break;
            case R.id.delete_history_record:
                helloWeatherDB.deleteHistoryCities();
                // 重新查询历史城市，刷新ListView
                queryHistoryCities();
        }
    }

    /**
     *  
     *
     * @brief   加载历史纪录城市，没有则提示没有历史纪录城市（简述）
     */
    private void queryHistoryCities() {
        cityList = helloWeatherDB.loadHisoryCities();
        if (cityList.size() > 0) {
            dataList.clear();
            for (City historyCity : cityList) {
                dataList.add(historyCity);
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
        } else {
            dataList.clear();
            adapter.notifyDataSetChanged();
//            Toast.makeText(this, R.string.no_history_city, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *  
     *
     * @brief 加载查询到的城市，没有查询到则提示搜索城市（简述）
     */
    private void loadSearchCities() {
        cityList = helloWeatherDB.loadCities();
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city);
                LogUtil.d("ceshi", city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            //删除City数据库数据
            helloWeatherDB.deleteCities();
        } else {
            Toast.makeText(this, R.string.no_search_city, Toast.LENGTH_SHORT).show();
            queryHistoryCities();
        }
    }

    /**
     *  
     *
     * @brief 根据输入的数据（拼音，中文，等）从服务器上查询城市数据（简述）
     *  @param   code（省或市或县代号）
     *  @param   type（省或市或县）
     */
    private void queryFromServer(final String queryCity) {
        String address;
        address = "https://api.thinkpage.cn/v3/location/search.json?key=" + MyApplication.getMyKey() + "&q=" + queryCity + "&limit=10";
        LogUtil.d("ceshi", "showProgressDialog" + address);
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                result = Utility.handleCityResponse(helloWeatherDB, response);
                if (result) {
                    // 通过runOnUiThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            loadSearchCities();
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
                        Toast.makeText(ChooseAreaActivity.this, R.string.load_failure, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     *  
     *
     * @brief 显示进度对话框（简述）
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     *  
     *
     * @brief 关闭进度对话框（简述）
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     *  
     *
     * @brief 捕获Back按键，根据当前的级别来判断，此时应返天气界面还是直接退出（简述）
     */
    @Override
    public void onBackPressed() {
        if (isFromWeatherActivity) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
        }
        finish();
    }
}
