package com.helloweather.app.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.helloweather.app.R;
import com.helloweather.app.util.HttpCallbackListener;
import com.helloweather.app.util.HttpUtil;
import com.helloweather.app.util.LogUtil;
import com.helloweather.app.util.Utility;

public class WeatherActivity extends AppCompatActivity {

    private LinearLayout weatherInfoLayout;

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
        String countryCode = getIntent().getStringExtra("country_code");
        if (!TextUtils.isEmpty(countryCode)) {
            // 有县级代号时就去查询天气
            publishTimeText.setText(R.string.synchronizing);
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countryCode);
        } else {
            // 没有县级代号时就直接显示本地天气
            showWeather();
        }
    }

    /**
     *  
     *
     * @brief 查询县级代号所对应天气代号（简述）
     *  @param   countryCode（县级天气代号）
     */
    private void queryWeatherCode(String countryCode) {
        String address = "http://10.0.2.2:8080/" + countryCode + ".xml";
        LogUtil.d("weatherTest", "queryWeatherCode " + address);
        queryFromServer(address, "countryCode");
    }

    /**
     *  
     *
     * @brief 查询天气代号所对应的天气（简述）
     *  @param   weatherCode（天气代号）
     */
    private void queryWeatherInfo(String weatherCode) {
        String address = "http://10.0.2.2:8080/" + weatherCode + ".html";
        LogUtil.d("weatherTest", "queryWeatherInfo" + address);
        queryFromServer(address, "weatherCode");
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
                if (type.equals("countryCode")) {
                    if (!TextUtils.isEmpty(response)) {
                        // 从服务器中返回的数据中解析出天气代号
                        String[] array = response.split("\\|");
                        if (array !=null && array.length == 2) {
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                            LogUtil.d("weatherTest", "queryFromServer  weatherCode " + weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {
                    // 处理服务器返回的天气信息
                    Utility.handleWeatherResponse(WeatherActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                    LogUtil.d("weatherTest", "queryFromServer showWeather " );
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

    private void showWeather() {
        SharedPreferences prfs = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prfs.getString("city_name", ""));
        temp1Text.setText(prfs.getString("temp1", ""));
        temp2Text.setText(prfs.getString("temp2", ""));
        LogUtil.d("weatherTest", "showWeather  temp2 " + prfs.getString("temp2", ""));
        weatherDespText.setText(prfs.getString("weather_desp", ""));
        String publishTime = this.getString(R.string.today) + prfs.getString("publish_time", "") + this.getString(R.string.publish);
        LogUtil.d("weatherTest", "showWeather  publishTime " + publishTime);
        publishTimeText.setText(publishTime);
        currentDataText.setText(prfs.getString("current_date", ""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);

    }
}
