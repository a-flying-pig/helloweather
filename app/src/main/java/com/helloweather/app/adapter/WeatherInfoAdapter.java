package com.helloweather.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.helloweather.app.R;
import com.helloweather.app.model.WeatherInfo;

import java.util.List;

/**
 * Created by Administrator on 2016-11-28.
 * 天气信息适配器
 */
public class WeatherInfoAdapter extends ArrayAdapter<WeatherInfo> {

    private int resourceId;

    public WeatherInfoAdapter (Context context, int textViewResourceId, List<WeatherInfo> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WeatherInfo weatherInfo = getItem(position);
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        } else {
            view = convertView;
        }

        // 初始化发布时间
        TextView publishTimeText = (TextView) view.findViewById(R.id.publish_time_text);
        publishTimeText.setText(weatherInfo.getPublishTimeText());
        // 初始化实时天气变量
        ImageView nowImagine = (ImageView) view.findViewById(R.id.real_time_picture);
        TextView nowDesp = (TextView) view.findViewById(R.id.real_time_weather_desp);
        TextView nowTemp = (TextView) view.findViewById(R.id.real_time_tmp);
        nowImagine.setImageResource(weatherInfo.getNowImage());
        nowDesp.setText(weatherInfo.getNowDesp());
        nowTemp.setText(weatherInfo.getNowTemp());
        // 初始化第一天的天气变量
        TextView firstDate = (TextView) view.findViewById(R.id.first_date);
        TextView firstDayDesp = (TextView) view.findViewById(R.id.first_day_desp);
        ImageView firstDayImagine = (ImageView) view.findViewById(R.id.first_day_imagine);
        TextView firstNightDesp = (TextView) view.findViewById(R.id.first_night_desp);
        ImageView firstNightImagine = (ImageView) view.findViewById(R.id.first_night_imagine);
        TextView firstTemp1 = (TextView) view.findViewById(R.id.first_temp1);
        TextView firstTemp2 = (TextView) view.findViewById(R.id.first_temp2);
        firstDate.setText(weatherInfo.getFirstDate());
        firstDayDesp.setText(weatherInfo.getFirstDayDesp());
        firstDayImagine.setImageResource(weatherInfo.getFirstDayImage());
        firstNightDesp.setText(weatherInfo.getFirstNightDesp());
        firstNightImagine.setImageResource(weatherInfo.getFirstNightImage());
        firstTemp1.setText(weatherInfo.getFirstTemp1());
        firstTemp2.setText(weatherInfo.getFirstTemp2());
        // 初始化第二天的天气变量
        TextView secondDate = (TextView) view.findViewById(R.id.second_date);
        TextView secondDayDesp = (TextView) view.findViewById(R.id.second_day_desp);
        ImageView secondDayImagine = (ImageView) view.findViewById(R.id.second_day_imagine);
        TextView secondNightDesp = (TextView) view.findViewById(R.id.second_night_desp);
        ImageView secondNightImagine = (ImageView) view.findViewById(R.id.second_night_imagine);
        TextView secondTemp1 = (TextView) view.findViewById(R.id.second_temp1);
        TextView secondTemp2 = (TextView) view.findViewById(R.id.second_temp2);
        secondDate.setText(weatherInfo.getSecondDate());
        secondDayDesp.setText(weatherInfo.getSecondDayDesp());
        secondDayImagine.setImageResource(weatherInfo.getSecondDayImage());
        secondNightDesp.setText(weatherInfo.getSecondNightDesp());
        secondNightImagine.setImageResource(weatherInfo.getSecondNightImage());
        secondTemp1.setText(weatherInfo.getSecondTemp1());
        secondTemp2.setText(weatherInfo.getSecondTemp2());
        // 初始化第三天的天气变量
        TextView thirdDate = (TextView) view.findViewById(R.id.third_date);
        TextView thirdDayDesp = (TextView) view.findViewById(R.id.third_day_desp);
        ImageView thirdDayImagine = (ImageView) view.findViewById(R.id.third_day_imagine);
        TextView thirdNightDesp = (TextView) view.findViewById(R.id.third_night_desp);
        ImageView thirdNightImagine = (ImageView) view.findViewById(R.id.third_night_imagine);
        TextView thirdTemp1 = (TextView) view.findViewById(R.id.third_temp1);
        TextView thirdTemp2 = (TextView) view.findViewById(R.id.third_temp2);
        thirdDate.setText(weatherInfo.getThirdDate());
        thirdDayDesp.setText(weatherInfo.getThirdDayDesp());
        thirdDayImagine.setImageResource(weatherInfo.getThirdDayImage());
        thirdNightDesp.setText(weatherInfo.getThirdNightDesp());
        thirdNightImagine.setImageResource(weatherInfo.getThirdNightImage());
        thirdTemp1.setText(weatherInfo.getThirdTemp1());
        thirdTemp2.setText(weatherInfo.getThirdTemp2());
        return view;
    }
}
