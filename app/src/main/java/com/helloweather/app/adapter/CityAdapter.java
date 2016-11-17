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
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        TextView cityName = (TextView) view.findViewById(R.id.city_name);
        TextView cityPath = (TextView) view.findViewById(R.id.city_path);
        cityName.setText(city.getCityName());
        cityPath.setText(city.getCityPath());
        return view;

    }
}
