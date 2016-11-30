package com.helloweather.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.helloweather.app.R;
import com.helloweather.app.model.City;

import java.util.List;

/**
 * Created by Administrator on 2016-11-16.
 */
public class CityAdapter extends ArrayAdapter<City> {

    private int resourceId;

    public CityAdapter(Context context, int textViewResourceId, List<City> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override

    public View getView(int position, View convertView, ViewGroup parent) {
        City city = getItem(position);
        // 布局加载是比较费性能的，因此可以减少重复加载布局。这里的convertView就是用于缓存之前
        // 加载好的布局的，以便之后进行重用。因此，在下列代码中增加判断语句，减少加载布局次数，
        // 提高效率
//        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.cityName = (TextView) view.findViewById(R.id.city_name);
            viewHolder.cityPath = (TextView) view.findViewById(R.id.city_path);
            view.setTag(viewHolder); // 将ViewHolder存储在View中
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag(); // 重新获取ViewHolder对象
        }
        /*TextView cityName = (TextView) view.findViewById(R.id.city_name);
        TextView cityPath = (TextView) view.findViewById(R.id.city_path);
        cityName.setText(city.getCityName());
        cityPath.setText(city.getCityPath());*/
        viewHolder.cityName.setText(city.getCityName());
        viewHolder.cityPath.setText(city.getCityPath());
        return view;

    }

    private class ViewHolder {
        TextView cityName;
        TextView cityPath;
    }
}
