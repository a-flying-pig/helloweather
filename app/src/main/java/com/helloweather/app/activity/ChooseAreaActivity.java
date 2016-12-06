package com.helloweather.app.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.helloweather.app.R;
import com.helloweather.app.adapter.CityAdapter;
import com.helloweather.app.db.HelloWeatherDB;
import com.helloweather.app.model.City;
import com.helloweather.app.util.CustomProgressDialog;
import com.helloweather.app.util.HttpCallbackListener;
import com.helloweather.app.util.HttpUtil;
import com.helloweather.app.util.LogUtil;
import com.helloweather.app.util.MyApplication;
import com.helloweather.app.util.NetworkState;
import com.helloweather.app.util.NoDoubleClickUtil;
import com.helloweather.app.util.Utility;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author HuaZhu
 *         created at 2016-11-11 11:31
 * @brief 用于遍历省市县数据
 */
public class ChooseAreaActivity extends AppCompatActivity implements View.OnClickListener {

    private Dialog mDialog;
    private EditText queryEdit;
    private Button queryButton;
    private Button deleteHistory;
    private String queryCity;
    private ListView listView;
    private CityAdapter adapter;
    private HelloWeatherDB helloWeatherDB;
    private List<City> dataList = new ArrayList<City>();
    private RelativeLayout rootChooseArea;
    private RelativeLayout listViewDelete;
    private RelativeLayout searchLy;

    /**
     * listViewDelete的Y坐标
     */
    private int listViewDeleteY;

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
        rootChooseArea = (RelativeLayout) findViewById(R.id.root_choose_area);
        listViewDelete = (RelativeLayout) findViewById(R.id.list_view_delete_ly);
        searchLy = (RelativeLayout) findViewById(R.id.search_ly);
        listView.setAdapter(adapter);
        // 监控界面布局变化，更改布局
        controlKeyboardLayout(rootChooseArea, listViewDelete);
        helloWeatherDB = HelloWeatherDB.getInstance(this);
        queryButton.setOnClickListener(this);
        deleteHistory.setOnClickListener(this);
        // 点击listViewDelete时关闭键盘
        listViewDelete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                boolean isOpen = inputMethodManager.isActive();
                if (isOpen) {
                    inputMethodManager.showSoftInput(listView,InputMethodManager.SHOW_FORCED);
                    inputMethodManager.hideSoftInputFromWindow(listView.getWindowToken(), 0);
                }
                return false;
            }
        });
        // 触摸listView时关闭键盘
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                boolean isOpen = inputMethodManager.isActive();
                if (isOpen) {
                    inputMethodManager.showSoftInput(listView,InputMethodManager.SHOW_FORCED);
                    inputMethodManager.hideSoftInputFromWindow(listView.getWindowToken(), 0);
                }
                return false;
            }
        });
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
        switch (v.getId()) {
            case R.id.query_button:
                if (!NoDoubleClickUtil.isDoubleClick()) {
                    queryCity = queryEdit.getText().toString().replaceAll("\\s*", "");
                    if (TextUtils.isEmpty(queryCity)) {
                        Toast.makeText(this, R.string.no_input_city, Toast.LENGTH_SHORT).show();
                    } else {
                        try { // 将输入的数据转换成"UTF-8"类型，为了是输入的是城市时也能搜索
//                        queryCity = "%E6%88%90%E9%83%BD";
                            queryCity = URLEncoder.encode(queryCity, "UTF-8");
//                        queryCity = URLDecoder.decode(queryCity, "UTF-8");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        queryFromServer(queryCity);
                    }
                }
                break;
            case R.id.delete_history_record:
                if (!NoDoubleClickUtil.isDoubleClick()) {
                    helloWeatherDB.deleteHistoryCities();
                    // 重新查询历史城市，刷新ListView
                    queryHistoryCities();
                }
                break;
            default:
                break;
        }
    }

    /**
     *  
     *
     * @brief 加载历史纪录城市，没有则提示没有历史纪录城市（简述）
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
     *  @param   queryCity（查询的城市名）
     */
    private void queryFromServer(final String queryCity) {
        String address;
        address = "https://api.thinkpage.cn/v3/location/search.json?key=" + MyApplication.getMyKey() + "&q=" + queryCity + "&limit=10";
        LogUtil.d("ceshi", "showProgressDialog" + address);
        if (NetworkState.IsNetworkAvailable()) {
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
        } else {
            Toast.makeText(ChooseAreaActivity.this, R.string.connect_network, Toast.LENGTH_SHORT).show();
        }
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

    /**
     * @brief 调用软键盘时使相应控件上移（简述）
     * @param  view（最外层布局，需要调整的布局）
     * @param  listViewDelete（要更改高度的控件，使其高度减小，减小的值为键盘占用的高度值）
     */
    private void controlKeyboardLayout(final View view, final View listViewDelete) {
        // 获得活动启动时视图的最底部的y坐标值
        Rect rect = new Rect();
        // 获取view在窗口的可视区域
        view.getWindowVisibleDisplayFrame(rect);
        // 获得活动启动时视图的最底部的y坐标值
        final int originBottomHeight = rect.bottom;
        LogUtil.d("keyBoard", "originBottomHeight：" + originBottomHeight);
        // 注册一个回调函数，当在一个视图树中全局布局发生改变或者视图树中的某个视图的可视状态发生改变时调用这个回调函数
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 获得启动活动时listViewDelete的高度值
                int listViewDeleteHeight = originBottomHeight - listViewDeleteY;
                LogUtil.d("keyBoard", "listViewHeight：" + listViewDeleteHeight);
                Rect rect1 = new Rect();
                // 获取view在窗口的可视区域
                view.getWindowVisibleDisplayFrame(rect1);
                // 被占用的高度，就是键盘的高度值
                int viewInvisibleHeight = originBottomHeight - rect1.bottom;
                // 若viewInvisibleHeight高度大于0，说明当前视图上移了，说明软键盘弹出来了
                if (viewInvisibleHeight > 0) {
                    // 软键盘弹出来时的逻辑处理
                    // listViewDelete高度更改为listViewDeleteHeight2
                    int listViewDeleteHeight2 = listViewDeleteHeight - viewInvisibleHeight;
                    LogUtil.d("keyBoard", "listViewHeight2：" + listViewDeleteHeight2);
                    // 更改listViewDelete的高度
                    setViewHeight(listViewDelete, listViewDeleteHeight2);
                } else {
                    // 软键盘没有弹出来的时候
                    setViewHeight(listViewDelete, listViewDeleteHeight);
                }
            }
        });
    }

    /**
     * 获得listViewDelete控件在可视窗口的（即当前窗口，不包括标题栏）Y坐标值
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        int[] searchLyLocation = new int[2];
        searchLy.getLocationOnScreen(searchLyLocation); // 通过搜索栏获得（他们是接触的），它是不变化的避免listView变化时对数据有影响
        LogUtil.d("listViewLocation", "searchLyLocation[0]：" + searchLyLocation[0] + " " + "searchLyLocation[1]：" + searchLyLocation[1]);
        // 获得listView控件Y坐标
        listViewDeleteY =searchLyLocation[1] + Dp2Px(this, 50);
        LogUtil.d("listViewLocation", "listViewDeleteY: " +listViewDeleteY);
    }

    /**
     * 定义一个函数将dp转换为像素
     */
    public int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp*scale + 0.5f);
    }

    /**
     * 定义函数根据子项动态控制ListView的高度
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取listView的适配器
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View的宽高
            // 统计所有子项的总高度
            totalHeight += Dp2Px(getApplicationContext(), listItem.getMeasuredHeight() + listView.getDividerHeight());
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight;
            listView.setLayoutParams(params);
        }
    }
    // 在每次listView的adapter发生变化后，要调用setListViewHeightBasedOnChildren(listView)更新界面

    /**
     * 设置View的高度(这里设置的是listViewDeleteLy，layout控件的高度)
     */
    public void setViewHeight(View view, int mHeight) {
        if (mHeight > 0) {
            ViewGroup.LayoutParams params = view.getLayoutParams(); // 加载View
            params.height = mHeight; // 添加值
            view.setLayoutParams(params); // 设定值
        }
    }
}
