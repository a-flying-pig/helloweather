package com.helloweather.app.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.helloweather.app.R;
import com.helloweather.app.util.HttpCallbackListener;
import com.helloweather.app.util.HttpUtil;
import com.helloweather.app.util.LogUtil;
import com.helloweather.app.util.MyApplication;
import com.helloweather.app.util.NoDoubleClickUtil;
import com.helloweather.app.util.Utility;

public class WeatherActivity extends AppCompatActivity implements View.OnClickListener{

    private RelativeLayout weatherInfoLayout;

    private static final int GET_INFO_FAIL = 0;

    private static final int QUERY_NOW_SUCCEED = 1;

    private static final int QUERY_DAILY_SUCCEED = 2;

    private static final int REAL_TIME_WEATHER = 0;

    private static final int DAILY_WEATHER = 1;

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
     * 用于显示实时天气变量
     */
    private ImageView nowImagine;
    private TextView nowDesp;
    private TextView nowTemp;

    /**
     * 用于显示第一天天气变量
     */
    private TextView firstDate;
    private TextView firstDayDesp;
    private ImageView firstDayImagine;
    private TextView firstNightDesp;
    private ImageView firstNightImagine;
    private TextView firstTemp1;
    private TextView firstTemp2;

    /**
     * 用于显示第二天天气变量
     */
    private TextView secondDate;
    private TextView secondDayDesp;
    private ImageView secondDayImagine;
    private TextView secondNightDesp;
    private ImageView secondNightImagine;
    private TextView secondTemp1;
    private TextView secondTemp2;

    /**
     * 用于显示第三天天气变量
     */
    private TextView thirdDate;
    private TextView thirdDayDesp;
    private ImageView thirdDayImagine;
    private TextView thirdNightDesp;
    private ImageView thirdNightImagine;
    private TextView thirdTemp1;
    private TextView thirdTemp2;

    /**
     * 是否完成实时天气或者几天天气的信息查询
     */
    private boolean isQueryNowSucceed = false;
    private boolean isQueryDailySucceed = false;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == GET_INFO_FAIL) {
                publishTimeText.setText(R.string.sync_failure);
                Toast.makeText(WeatherActivity.this, R.string.get_infor_failed, Toast.LENGTH_SHORT).show();
            }
            if (msg.what == QUERY_NOW_SUCCEED) {
                isQueryNowSucceed =true;
                LogUtil.d("handlerr", "if QUERY_NOW_SUCCEED " + isQueryNowSucceed + "if QUERY_DAILY_SUCCEED " + isQueryDailySucceed);
                if (isQueryNowSucceed && isQueryDailySucceed) {
                    showWeather();
                    LogUtil.d("handlerr", "showWeather executed ");
                    isQueryNowSucceed = false;
                    isQueryDailySucceed = false;
                }
            }
            if (msg.what == QUERY_DAILY_SUCCEED) {
                isQueryDailySucceed = true;
                LogUtil.d("handlerr", "if QUERY_NOW_SUCCEED " + isQueryNowSucceed + "if QUERY_DAILY_SUCCEED " + isQueryDailySucceed);
                if (isQueryNowSucceed && isQueryDailySucceed) {
                    showWeather();
                    LogUtil.d("handlerr", "showWeather executed ");
                    isQueryNowSucceed = false;
                    isQueryDailySucceed = false;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);
        // 初始化标题栏及发布日期控件
        weatherInfoLayout = (RelativeLayout) findViewById(R.id.weather_info_layout);
        cityNameText = (TextView) findViewById(R.id.city_name);
        publishTimeText = (TextView) findViewById(R.id.publish_time_text);
        Button switchCity = (Button) findViewById(R.id.switch_city);
        Button refreshWeather = (Button) findViewById(R.id.refresh_weather);
        // 初始化实时天气变量
        nowImagine = (ImageView) findViewById(R.id.real_time_picture);
        nowDesp = (TextView) findViewById(R.id.real_time_weather_desp);
        nowTemp = (TextView) findViewById(R.id.real_time_tmp);
        // 初始化第一天的天气变量
        firstDate = (TextView) findViewById(R.id.first_date);
        firstDayDesp = (TextView) findViewById(R.id.first_day_desp);
        firstDayImagine = (ImageView) findViewById(R.id.first_day_imagine);
        firstNightDesp = (TextView) findViewById(R.id.first_night_desp);
        firstNightImagine = (ImageView) findViewById(R.id.first_night_imagine);
        firstTemp1 = (TextView) findViewById(R.id.first_temp1);
        firstTemp2 = (TextView) findViewById(R.id.first_temp2);
        // 初始化第二天的天气变量
        secondDate = (TextView) findViewById(R.id.second_date);
        secondDayDesp = (TextView) findViewById(R.id.second_day_desp);
        secondDayImagine = (ImageView) findViewById(R.id.second_day_imagine);
        secondNightDesp = (TextView) findViewById(R.id.second_night_desp);
        secondNightImagine = (ImageView) findViewById(R.id.second_night_imagine);
        secondTemp1 = (TextView) findViewById(R.id.second_temp1);
        secondTemp2 = (TextView) findViewById(R.id.second_temp2);
        // 初始化第三天的天气变量
        thirdDate = (TextView) findViewById(R.id.third_date);
        thirdDayDesp = (TextView) findViewById(R.id.third_day_desp);
        thirdDayImagine = (ImageView) findViewById(R.id.third_day_imagine);
        thirdNightDesp = (TextView) findViewById(R.id.third_night_desp);
        thirdNightImagine = (ImageView) findViewById(R.id.third_night_imagine);
        thirdTemp1 = (TextView) findViewById(R.id.third_temp1);
        thirdTemp2 = (TextView) findViewById(R.id.third_temp2);
        // 从ChooseAreaActivity活动跳转过来时执行的逻辑
        String cityId = getIntent().getStringExtra("cityId");
        if (!TextUtils.isEmpty(cityId)) {
            // 有城市代号时就去查询天气
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            // 将存储的城市Id改为从ChooseActivity传过来的Id，
            // 避免在服务自动更新的时候将前面的城市Id用于更新
            SharedPreferences.Editor editor =  PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putString("city_id", cityId);
            editor.apply();
            queryWeatherInfo(cityId, mHandler);
        /*    // 启动定时更新服务
            Intent serviceIntent = new Intent(WeatherActivity.this, AutoUpdateService.class);
            startService(serviceIntent);*/
            LogUtil.d("updateService", "weatherActivity updateService start");
        } else {
            // 没有城市Id时就直接显示本地天气
            showWeather();
         /*   //启动定时更新服务
            Intent serviceIntent = new Intent(this, AutoUpdateService.class);
            startService(serviceIntent);
            LogUtil.d("updateService", "weatherActivity updateService start");*/
        }

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
                Intent intent = new Intent(this, ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather: // 更新天气
                if (!NoDoubleClickUtil.isDoubleClick()) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    String cityId = prefs.getString("city_id", "");
                    if (!TextUtils.isEmpty(cityId)) {
                        queryWeatherInfo(cityId, mHandler);
                    }
                    break;
                }
        }
    }

    /**
     *  
     *
     * @brief 查询天气代号所对应的天气（简述）
     *  @param   weatherCode（天气代号）
     */

    public void queryWeatherInfo(String cityId, Handler mHandler) {
        publishTimeText.setText(R.string.synchronizing);
        String address = "https://api.thinkpage.cn/v3/weather/daily.json?key=" + MyApplication.getMyKey() + "&location=" + cityId + "&language=zh-Hans&unit=c&start=0&days=3";
        LogUtil.d("weatherTest", "queryWeatherInfo address" + address);
        String address1 = "https://api.thinkpage.cn/v3/weather/now.json?key=" + MyApplication.getMyKey() + "&location=" + cityId + "&language=zh-Hans&unit=c";
        LogUtil.d("weatherTest", "queryWeatherInfo address1" + address1);
        queryFromServer(address1, REAL_TIME_WEATHER, mHandler);
        queryFromServer(address, DAILY_WEATHER, mHandler);
    }

    /**
     *  
     *
     * @brief 根据传入的地址和类型去向服务器查询天气代号或者天气信息（简述）
     *  @param   address（传入的地址）
     *  @param   type（查询类型）
     */
    private void queryFromServer(final String address, final int type, final Handler mHandler) {

        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                if (response.contains("AP")) { // 如果返回的数据包含“AP”则说明没有正常访问，弹出获取信息失败提示
                    LogUtil.d("queryWeather", "queryFromServer AP " + response);
                    Message message = new Message();
                    message.what = GET_INFO_FAIL;
                    mHandler.sendMessage(message);
                } else {
                    // 处理服务器返回的天气信息
                    LogUtil.d("weatherTest", "queryFromServer  handleWeatherResponse START");
                    Utility.handleWeatherResponse(WeatherActivity.this, response, type, mHandler);
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
     * @brief 从SharedPreferences文件中读取存储的天气信息，并显示到界面上（简述）。
     */

    public void showWeather() {
        SharedPreferences prfs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
        // 显示标题栏及发布日期
//        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
        cityNameText.setText(prfs.getString("city_name", ""));
        LogUtil.d("weatherTest", "showWeather  publishTime " + prfs.getString("city_name", ""));
        String editedDate = prfs.getString("publish_time", "").substring(0, 10);
        String editedTime = prfs.getString("publish_time", "").substring(11, 16);
        String publishTime = this.getString(R.string.publish_time) + editedDate + " " + editedTime;
        publishTimeText.setText(publishTime);
        // 显示实时天气
        nowImagine.setImageResource(Utility.parsePictureId(prfs.getString("now_weather_code", "99"))); // 99 表示没有获得数据
        nowDesp.setText(prfs.getString("now_weather_desp", ""));
        String temperature = prfs.getString("now_temp", "") + getString(R.string.degree);
        nowTemp.setText(temperature);
        // 显示第一天的天气
        firstDate.setText(prfs.getString("first_date", ""));
        firstDayDesp.setText(prfs.getString("first_day_desp", ""));
        firstDayImagine.setImageResource(Utility.parsePictureId(prfs.getString("first_day_code", "99")));
        firstNightDesp.setText(prfs.getString("first_night_desp", ""));
        firstNightImagine.setImageResource(Utility.parsePictureId(prfs.getString("first_night_code", "99")));
        String firstT1 = prfs.getString("first_temp1", "");
        firstTemp1.setText(firstT1);
        String firstT2 = prfs.getString("first_temp2", "") + getString(R.string.degree);
        firstTemp2.setText(firstT2);
        // 显示第二天的天气
        secondDate.setText(prfs.getString("second_date", ""));
        secondDayDesp.setText(prfs.getString("second_day_desp", ""));
        secondDayImagine.setImageResource(Utility.parsePictureId(prfs.getString("second_day_code", "99")));
        secondNightDesp.setText(prfs.getString("second_night_desp", ""));
        secondNightImagine.setImageResource(Utility.parsePictureId(prfs.getString("second_night_code", "99")));
        String secondT1 = prfs.getString("second_temp1", "");
        secondTemp1.setText(secondT1);
        String secondT2 = prfs.getString("second_temp2", "") + getString(R.string.degree);
        secondTemp2.setText(secondT2);
        // 显示第三天的天气
        thirdDate.setText(prfs.getString("third_date", ""));
        thirdDayDesp.setText(prfs.getString("third_day_desp", ""));
        thirdDayImagine.setImageResource(Utility.parsePictureId(prfs.getString("third_day_code", "99")));
        thirdNightDesp.setText(prfs.getString("third_night_desp", ""));
        thirdNightImagine.setImageResource(Utility.parsePictureId(prfs.getString("third_night_code", "99")));
        String thirdT1 = prfs.getString("third_temp1", "");
        thirdTemp1.setText(thirdT1);
        String thirdT2 = prfs.getString("third_temp2", "") + getString(R.string.degree);
        thirdTemp2.setText(thirdT2);
        Toast.makeText(WeatherActivity.this, R.string.synchronizing_succeed, Toast.LENGTH_SHORT).show();
    }

    /**
     *  
     *
     * @brief 接收数据更新的广播，收到从服务发送的广播，然后更新UI（天气信息）（简述）
     */
    public class MyUiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.d("weatherRefresh", "MyUiBroadcast start");
            SharedPreferences prfs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
            String cityId = prfs.getString("city_id", "");
            if (!TextUtils.isEmpty(cityId)) {
                queryWeatherInfo(cityId, mHandler);
            }
        }
    }
}
