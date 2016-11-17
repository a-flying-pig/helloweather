package com.helloweather.app.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.helloweather.app.R;
import com.helloweather.app.service.AutoUpdateService;
import com.helloweather.app.util.HttpCallbackListener;
import com.helloweather.app.util.HttpUtil;
import com.helloweather.app.util.LogUtil;
import com.helloweather.app.util.MyApplication;
import com.helloweather.app.util.Utility;

public class WeatherActivity extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout weatherInfoLayout;

    private MyUiReceiver myUiReceiver;

    /**
     * 用于显示城市名称
     */
    private TextView cityNameText;

    /**
     * 用于显示发布时间
     */
    private TextView publishTimeText;

    /**
     * 用于显示天气信息描述
     */
    private TextView weatherDespText;

    /**
     * 用于显示气温1
     */
    private TextView temp1Text;

    /**
     * 用于显示气温2
     */
    private TextView temp2Text;

    /**
     * 用于显示当前日期
     */
    private TextView currentDataText;

    /**
     * 切换城市按钮
     */
    private Button switchCity;

    /**
     * 更新天气按钮
     */
    private Button refreshWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);
        // 初始化各控件
        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText = (TextView) findViewById(R.id.city_name);
        publishTimeText = (TextView) findViewById(R.id.publish_time_text);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);
        currentDataText = (TextView) findViewById(R.id.current_date);
        String cityId = getIntent().getStringExtra("cityId");
        if (!TextUtils.isEmpty(cityId)) {
            // 有城市代号时就去查询天气
            publishTimeText.setText(R.string.synchronizing);
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherInfo(cityId);
        } else {
            // 没有县级代号时就直接显示本地天气
            showWeather();
            //启动定时服务
            Intent serviceIntent = new Intent(this, AutoUpdateService.class);
            startService(serviceIntent);
        }
        switchCity = (Button) findViewById(R.id.switch_city);
        refreshWeather = (Button) findViewById(R.id.refresh_weather);
        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
        // 注册动态广播（更新UI（天气信息））
        IntentFilter intentFiler = new IntentFilter();
        intentFiler.addAction("com.app.helloweather.MY_UI_BROADCAST");
        myUiReceiver = new MyUiReceiver();
        registerReceiver(myUiReceiver, intentFiler);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myUiReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_city: // 切换城市
                Intent intent = new Intent(this,ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather: // 更新天气
                publishTimeText.setText(R.string.synchronizing);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String cityId = prefs.getString("city_id", "");
                if (!TextUtils.isEmpty(cityId)) {
                    queryWeatherInfo(cityId);
                }
                break;
            default:
                break;
        }
    }

    /**
     *  
     *
     * @brief 查询天气代号所对应的天气（简述）
     *  @param   weatherCode（天气代号）
     */
    private void queryWeatherInfo(String cityId) {
        String address = "https://api.thinkpage.cn/v3/weather/daily.json?key=" + MyApplication.getMyKey() + "&location=" +cityId + "&language=zh-Hans&unit=c&start=0&days=3";
        LogUtil.d("weatherTest", "queryWeatherInfo" + address);
        queryFromServer(address, "cityId");
    }

    /**
     *  
     *
     * @brief 根据传入的地址和类型去向服务器查询天气代号或者天气信息（简述）
     *  @param   address（传入的地址）
     *  @param   type（查询类型）
     */
    private void queryFromServer(final String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                if (type.equals("weatherCodeNoUse")) {
                    if (!TextUtils.isEmpty(response)) {
                        // 从服务器中返回的数据中解析出天气代号
                        String[] array = response.split("\\|");
                        if (array !=null && array.length == 2) {
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                            LogUtil.d("weatherTest", "queryFromServer  weatherCode " + weatherCode);
                        }
                    }
                } else if ("cityId".equals(type)) {
                    // 处理服务器返回的天气信息
                    LogUtil.d("weatherTest", "queryFromServer  handleWeatherResponse START");
                    Utility.handleWeatherResponse(WeatherActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                            // 启动定时服务
                            Intent serviceIntent = new Intent(WeatherActivity.this, AutoUpdateService.class);
                            startService(serviceIntent);
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishTimeText.setText(R.string.sync_failure);
                    }
                });
            }
        });
    }

    /**
     *  
     *
     * @brief   从SharedPreferences文件中读取存储的天气信息，并显示到界面上（简述）。
     */

    public void showWeather() {
        SharedPreferences prfs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
        cityNameText.setText(prfs.getString("city_name", ""));
        temp1Text.setText(prfs.getString("temp1", ""));
        String temperature = prfs.getString("temp2", "") + getString(R.string.degree);
        temp2Text.setText(temperature);
        LogUtil.d("weatherTest", "showWeather  temp2 " + prfs.getString("temp2", ""));
        weatherDespText.setText(prfs.getString("weather_desp", ""));
        String publishTime = this.getString(R.string.publish_time) + prfs.getString("publish_time", "");
        LogUtil.d("weatherTest", "showWeather  publishTime " + publishTime);
        publishTimeText.setText(publishTime);
        currentDataText.setText(prfs.getString("current_date", ""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
    }

    /**
     *  
     *
     * @brief   接收数据更新的广播，收到从服务发送的广播，然后更新UI（天气信息）（简述）
     */
    public class MyUiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.d("weatherRefresh", "MyUiBroadcast start");
            showWeather();
        }
    }
}
