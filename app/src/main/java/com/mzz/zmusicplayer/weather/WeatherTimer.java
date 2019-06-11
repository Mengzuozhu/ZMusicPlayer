package com.mzz.zmusicplayer.weather;

import com.mzz.zmusicplayer.setting.AppSetting;

/**
 * author : Mzz
 * date : 2019 2019/6/11 10:28
 * description :
 */
public class WeatherTimer {

    private WeatherInfo weatherInfo;

    public WeatherTimer() {
        WeatherQuery.queryForWeather(AppSetting.getWeatherCity(), weatherInfo -> this.weatherInfo = weatherInfo);
    }
}
