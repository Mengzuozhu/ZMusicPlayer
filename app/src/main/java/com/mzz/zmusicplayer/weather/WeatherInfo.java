package com.mzz.zmusicplayer.weather;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;
import lombok.Getter;

/**
 * author : Mzz
 * date : 2019 2019/6/10 20:49
 * description :
 */
@Data
public class WeatherInfo {
    @Getter
    private String city;

    @Getter
    @SerializedName("update_time")
    private String updateTime;

    @SerializedName("data")
    private List <DayWeather> dayWeathers;

    @Data
    private class DayWeather {
        private String date;
        @SerializedName("wea")
        private String weather;
        @SerializedName("tem")
        private String temperature;
        @SerializedName("tem1")
        private String maxTemperature;
        @SerializedName("tem2")
        private String minTemperature;
    }

}
