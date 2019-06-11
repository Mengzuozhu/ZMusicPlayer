package com.mzz.zmusicplayer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mzz.zandroidcommon.view.BaseActivity;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.setting.AppSetting;
import com.mzz.zmusicplayer.weather.WeatherQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CityPickerActivity extends BaseActivity {

    public static final int CITY_PICKER_CODE = 2;
    @BindView(R.id.rv_city_picker)
    RecyclerView rvCityPicker;
    @BindView(R.id.sv_city)
    SearchView searchView;
    @BindView(R.id.tv_city_select)
    TextView tvCitySelect;
    @BindView(R.id.tv_popular_city)
    TextView tvPopularCity;
    private List <String> cities;
    private BaseQuickAdapter <String, BaseViewHolder> baseAdapter;
    private String selectCity;

    public static void start(FragmentActivity activity) {
        Intent starter = new Intent(activity, CityPickerActivity.class);
        activity.startActivityForResult(starter, CITY_PICKER_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_picker);
        ButterKnife.bind(this);

        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_save) {
            AppSetting.setWeatherCity(selectCity);
            setResult(CITY_PICKER_CODE, getIntent());
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        initPopularCities();
        updateSelectCity(AppSetting.getWeatherCity());
        baseAdapter =
                new BaseQuickAdapter <String, BaseViewHolder>(R.layout.item_city, cities) {
                    @Override
                    protected void convert(BaseViewHolder helper, String item) {
                        helper.setText(R.id.tv_city, item);
                    }
                };
        rvCityPicker.setLayoutManager(new GridLayoutManager(this, 4));
        rvCityPicker.setAdapter(baseAdapter);
        baseAdapter.setOnItemClickListener((adapter, view, position) -> {
            TextView tvCity = view.findViewById(R.id.tv_city);
            updateSelectCity(tvCity.getText().toString());
        });
        setQueryTextListener(searchView);
    }

    private void updateSelectCity(String selectCity) {
        this.selectCity = selectCity;
        String text = String.format("当前选中城市：%s", this.selectCity);
        tvCitySelect.setText(text);
    }

    private void initPopularCities() {
        cities = new ArrayList <>();
        cities.add("北京");
        cities.add("成都");
        cities.add("上海");
        cities.add("广州");
        cities.add("杭州");
    }

    public void setQueryTextListener(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)) {
                    WeatherQuery.canQueryCityWeather(query, cityRes -> showSearchResult(cityRes));
                }
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    resetAdapter();
                }
                return false;
            }
        });
    }

    private void resetAdapter() {
        tvPopularCity.setVisibility(View.VISIBLE);
        tvPopularCity.setText("热门城市");
        baseAdapter.setNewData(cities);
    }

    private void showSearchResult(String cityRes) {
        CityPickerActivity.this.runOnUiThread(() -> {
            ArrayList <String> data = new ArrayList <>();
            String label = "搜索结果";
            if ("".equals(cityRes)) {
                label = "不支持查询该城市的天气";
            } else {
                data.add(cityRes);
            }
            tvPopularCity.setText(label);
            baseAdapter.setNewData(data);
        });
    }

}
