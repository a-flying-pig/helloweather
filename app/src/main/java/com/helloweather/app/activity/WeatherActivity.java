package com.helloweather.app.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.helloweather.app.R;
import com.helloweather.app.adapter.WeatherInfoAdapter;
import com.helloweather.app.model.WeatherInfo;
import com.helloweather.app.service.AutoUpdateService;
import com.helloweather.app.util.CustomProgressDialog;
import com.helloweather.app.util.LogUtil;
import com.helloweather.app.util.NoDoubleClickUtil;
import com.helloweather.app.util.QueryUtility;
import com.helloweather.app.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class WeatherActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener{

    private RelativeLayout weatherInfoLayout;

    private RelativeLayout getInfoFailedLayout;

    private static final int GET_INFO_FAIL = 0;

    private static final int QUERY_NOW_SUCCEED = 1;

    private static final int QUERY_DAILY_SUCCEED = 2;

    private static final int NETWORK_UNAVAILABLE = 3;

    /**
     * 用于显示城市名称
     */
    private TextView cityNameText;

    /**
     * 是否完成实时天气或者几天天气的信息查询
     */
    private boolean isQueryNowSucceed = false;
    private boolean isQueryDailySucceed = false;

    private WeatherInfo weatherInfo = new WeatherInfo();
    private ArrayList<WeatherInfo> weatherInfos = new ArrayList<>();
    private WeatherInfoAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Dialog mDialog;
    /**
     * 刷新失败时的背景
     */
    private ImageView getInfoFailedBg;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case NETWORK_UNAVAILABLE:
                    swipeRefreshLayout.setRefreshing(false); // 非正在刷新
                    getInfoFailedBg.setImageResource(R.drawable.fun_background_1_400x530);
                    closeProgressDialog();
                    weatherInfoLayout.setVisibility(View.INVISIBLE);
                    getInfoFailedLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(WeatherActivity.this, R.string.connect_network, Toast.LENGTH_SHORT).show();
                    break;
                case GET_INFO_FAIL:
                    swipeRefreshLayout.setRefreshing(false); // 非正在刷新
                    getInfoFailedBg.setImageResource(R.drawable.fun_background_1_400x530);
                    closeProgressDialog();
                    weatherInfoLayout.setVisibility(View.INVISIBLE);
                    getInfoFailedLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(WeatherActivity.this, R.string.get_info_failed, Toast.LENGTH_SHORT).show();
                    break;
                case QUERY_NOW_SUCCEED: // 如果NOW 和DAILY都查询成功，则展示信息
                    isQueryNowSucceed = true;
                    LogUtil.d("handlerr", "if QUERY_NOW_SUCCEED " + isQueryNowSucceed + "if QUERY_DAILY_SUCCEED " + isQueryDailySucceed);
                    if (isQueryNowSucceed && isQueryDailySucceed) {
                        swipeRefreshLayout.setRefreshing(false); // 非正在刷新
                        getInfoFailedBg.setImageResource(R.drawable.fun_background_1_400x530);
                        closeProgressDialog();
                        showWeather();
                        LogUtil.d("handlerr", "showWeather executed ");
                        isQueryNowSucceed = false;
                        isQueryDailySucceed = false;
                    }
                    break;
                case QUERY_DAILY_SUCCEED: // 如果NOW 和DAILY都查询成功，则展示信息
                    isQueryDailySucceed = true;
                    LogUtil.d("handlerr", "if QUERY_NOW_SUCCEED " + isQueryNowSucceed + "if QUERY_DAILY_SUCCEED " + isQueryDailySucceed);
                    if (isQueryNowSucceed && isQueryDailySucceed) {
                        swipeRefreshLayout.setRefreshing(false); // 非正在刷新
                        getInfoFailedBg.setImageResource(R.drawable.fun_background_1_400x530);
                        closeProgressDialog();
                        showWeather();
                        LogUtil.d("handlerr", "showWeather executed ");
                        isQueryNowSucceed = false;
                        isQueryDailySucceed = false;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);
        adapter = new WeatherInfoAdapter(WeatherActivity.this, R.layout.weather_info_layout, weatherInfos);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_green_light,android.R.color.holo_red_light);
        swipeRefreshLayout.setEnabled(true); // 是否可以刷新
        swipeRefreshLayout.setOnRefreshListener(this);
        // 初始化标题栏及发布日期控件
        weatherInfoLayout = (RelativeLayout) findViewById(R.id.weather_info_layout);
        getInfoFailedLayout = (RelativeLayout) findViewById(R.id.get_info_failed_ly);
        cityNameText = (TextView) findViewById(R.id.city_name);
        ImageButton switchCity = (ImageButton) findViewById(R.id.switch_city);
        ImageButton locationWeather = (ImageButton) findViewById(R.id.location_weather);
        Button tryAgain = (Button) findViewById(R.id.try_again);
        getInfoFailedBg = (ImageView) findViewById(R.id.get_info_failed_background);
        getInfoFailedBg.setImageResource(R.drawable.fun_background_1_400x530);
        switchCity.setOnClickListener(this);
        locationWeather.setOnClickListener(this);
        tryAgain.setOnClickListener(this);

        // 从ChooseAreaActivity活动跳转过来时执行的逻辑
        String cityId = getIntent().getStringExtra("cityId");
        if (!TextUtils.isEmpty(cityId)) {
            // 有城市代号时就去查询天气
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            getInfoFailedLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            // 将存储的城市Id改为从ChooseActivity传过来的Id，
            // 避免在服务自动更新的时候将前面的城市Id用于更新
            SharedPreferences.Editor editor =  PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putString("city_id", cityId);
            editor.apply();
            QueryUtility.queryWeatherInfo(cityId, mHandler);
            // 启动定时更新服务
            Intent serviceIntent = new Intent(WeatherActivity.this, AutoUpdateService.class);
            startService(serviceIntent);
            LogUtil.d("updateService", "weatherActivity updateService start");
        } else {
            // 没有城市Id时就直接显示本地天气
            showWeather();
            //启动定时更新服务
            Intent serviceIntent = new Intent(this, AutoUpdateService.class);
            startService(serviceIntent);
            LogUtil.d("updateService", "weatherActivity updateService start");
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String cityId = prefs.getString("city_id", "");
        if (!TextUtils.isEmpty(cityId)) {
            QueryUtility.queryWeatherInfo(cityId, mHandler);
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        showWeather();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_city: // 切换城市
                Intent intent = new Intent(this, ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            case R.id.location_weather: // 获得自动定位位置天气
                if (!NoDoubleClickUtil.isDoubleClick()) {
                    showProgressDialog();
                    new Handler().postDelayed(new Runnable() { // 延迟半秒执行，方便看到图片的效果
                        @Override
                        public void run() {
                            String longitudeLatitude = getLocation();
                            if (!TextUtils.isEmpty(longitudeLatitude)) {
                                QueryUtility.queryWeatherInfo(longitudeLatitude, mHandler);
                            }
                        }
                    }, 500);
                }
                break;
            case R.id.try_again: // 更新失败时，重新刷新
                if (!NoDoubleClickUtil.isDoubleClick()) {
                    showProgressDialog();
                    getInfoFailedBg.setImageResource(R.drawable.fun_background_2_400x530);
                    new Handler().postDelayed(new Runnable() { // 延迟半秒执行，方便看到图片的效果
                        @Override
                        public void run() {
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                            String cityId = prefs.getString("city_id", "");
                            if (!TextUtils.isEmpty(cityId)) {
                                QueryUtility.queryWeatherInfo(cityId, mHandler);
                            }
                        }
                    }, 500);
                }
                break;
            default:
                break;
        }
    }

    /**
     *  
     *
     * @brief 从SharedPreferences文件中读取存储的天气信息，并显示到界面上（简述）。
     */

    public void showWeather() {
        SharedPreferences prfs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
        // 显示标题栏及发布日期
        getInfoFailedLayout.setVisibility(View.INVISIBLE);
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
        cityNameText.setText(prfs.getString("city_name", ""));
        LogUtil.d("weatherTest", "showWeather  publishTime " + prfs.getString("city_name", ""));
        // 清除之前的天气信息
        weatherInfos.clear();
        String editedDate = prfs.getString("publish_time", "").substring(0, 10);
        String editedTime = prfs.getString("publish_time", "").substring(11, 16);
        String publishTime = this.getString(R.string.publish_time) + editedDate + " " + editedTime;
        weatherInfo.setPublishTimeText(publishTime);
        // 添加实时天气
        weatherInfo.setNowImage(Utility.parsePictureId(prfs.getString("now_weather_code", "99")));// 99 表示没有获得数据
        weatherInfo.setNowDesp(prfs.getString("now_weather_desp", ""));
        String temperature = prfs.getString("now_temp", "") + getString(R.string.degree);
        weatherInfo.setNowTemp(temperature);
        // 添加第一天的天气
        weatherInfo.setFirstDate(prfs.getString("first_date", ""));
        String dayDesp = this.getString(R.string.day)  + " " + prfs.getString("first_day_desp", "");
        weatherInfo.setFirstDayDesp(dayDesp);
        weatherInfo.setFirstDayImage(Utility.parsePictureId(prfs.getString("first_day_code", "99")));
        String nightDesp = this.getString(R.string.night) + " " + prfs.getString("first_night_desp", "");
        weatherInfo.setFirstNightDesp(nightDesp);
        weatherInfo.setFirstNightImage(Utility.parsePictureId(prfs.getString("first_night_code", "99")));
        weatherInfo.setFirstTemp1(prfs.getString("first_temp1", ""));
        weatherInfo.setFirstTemp2(prfs.getString("first_temp2", "") + getString(R.string.degree));
        // 添加第二天的天气
        weatherInfo.setSecondDate(prfs.getString("second_date", ""));
        dayDesp = this.getString(R.string.day)  + " " + prfs.getString("second_day_desp", "");
        weatherInfo.setSecondDayDesp(dayDesp);
        weatherInfo.setSecondDayImage(Utility.parsePictureId(prfs.getString("second_day_code", "99")));
        nightDesp = this.getString(R.string.night) + " " + prfs.getString("second_night_desp", "");
        weatherInfo.setSecondNightDesp(nightDesp);
        weatherInfo.setSecondNightImage(Utility.parsePictureId(prfs.getString("second_night_code", "99")));
        weatherInfo.setSecondTemp1(prfs.getString("second_temp1", ""));
        weatherInfo.setSecondTemp2(prfs.getString("second_temp2", "") + getString(R.string.degree));
        // 添加第三天的天气
        weatherInfo.setThirdDate(prfs.getString("third_date", ""));
        dayDesp = this.getString(R.string.day)  + " " + prfs.getString("third_day_desp", "");
        weatherInfo.setThirdDayDesp(dayDesp);
        weatherInfo.setThirdDayImage(Utility.parsePictureId(prfs.getString("third_day_code", "99")));
        nightDesp = this.getString(R.string.night) + " " + prfs.getString("third_night_desp", "");
        weatherInfo.setThirdNightDesp(nightDesp);
        weatherInfo.setThirdNightImage(Utility.parsePictureId(prfs.getString("third_night_code", "99")));
        weatherInfo.setThirdTemp1(prfs.getString("third_temp1", ""));
        weatherInfo.setThirdTemp2(prfs.getString("third_temp2", "") + getString(R.string.degree));
        // 将weatherInfo对象添加到weatherInfos
        weatherInfos.add(weatherInfo);
        adapter.notifyDataSetChanged();
        Toast.makeText(WeatherActivity.this, R.string.synchronizing_succeed, Toast.LENGTH_SHORT).show();
    }

    /**
     * 获取手机位置，返回经纬度信息
     */
    private String getLocation() {
        String locationInfo = null;
        String provider = null;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // 获取可用位置信息
        List<String> providerList = locationManager.getProviders(true);
        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            // 当没有可用的位置提供器时，弹出Toast提示用户
            Toast.makeText(this, this.getText(R.string.no_location_provider), Toast.LENGTH_SHORT).show();
        }
        if (Build.VERSION.SDK_INT >= 23) {
            if (WeatherActivity.this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    locationInfo = location.getLatitude() + ":" + location.getLongitude();

                }
            }
        } else {
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                locationInfo = location.getLatitude() + ":" + location.getLongitude();
            }
        }
        return locationInfo;
    }

    /**
     *  
     *
     * @brief 显示进度对话框（简述）
     */
    private void showProgressDialog() {
        mDialog = CustomProgressDialog.getCustomProgressDialog(this, getString(R.string.loading));
        mDialog.show();
    }

    /**
     *  
     *
     * @brief 关闭进度对话框（简述）
     */
    private void closeProgressDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }
}
