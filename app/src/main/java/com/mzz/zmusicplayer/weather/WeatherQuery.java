package com.mzz.zmusicplayer.weather;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.mzz.zandroidcommon.common.StringHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * author : Mzz
 * date : 2019 2019/6/10 19:40
 * description :
 */
public class WeatherQuery {
    private static final String TAG = "WeatherQuery";
    private static final String EMPTY = "";
    private static final String API_URL = "https://www.tianqiapi.com/api/?version=v1&city=";

    /**
     * Can query city weather.
     *
     * @param city              the city
     * @param queryCityCallback the query callback
     */
    public static void canQueryCityWeather(String city, QueryCityCallback queryCityCallback) {
        String urlByCity = getUrlByCity(city);
        requestByOkHttp(urlByCity, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                queryCityCallback.canQueryCityWeather(EMPTY);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                String cityRes = EMPTY;
                if (response.isSuccessful()) {
                    cityRes = parseJsonForCity(response);
                    //查询城市和返回的结果不一致，说明不支持
                    if (!cityRes.equals(city)) {
                        cityRes = EMPTY;
                    }
                }
                queryCityCallback.canQueryCityWeather(cityRes);
            }
        });
    }

    public static void queryForWeather(String city, QueryWeatherCallback queryWeatherCallback) {
        String urlByCity = getUrlByCity(city);
        requestByOkHttp(urlByCity, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                //ignore
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful()) {
                    WeatherInfo weatherInfo = parseJsonForWeather(response);
                    queryWeatherCallback.queryForWeather(weatherInfo);
                }
            }
        });
    }

    private static String getUrlByCity(String city) {
        return StringHelper.getLocalFormat("%s%s", API_URL, city);
    }

    private static String parseJsonForCity(Response response) {
        ResponseBody body = response.body();
        if (body == null) {
            return "";
        }
        try {
            String responseData = body.string();
            JSONObject jsonObject = new JSONObject(responseData);
            String city = jsonObject.getString("city");
            Log.d(TAG, city);
            return city;
        } catch (IOException | JSONException e) {
            Log.d(TAG, "e:" + e);
        }
        return "";
    }

    private static WeatherInfo parseJsonForWeather(Response response) {
        ResponseBody body = response.body();
        if (body == null) {
            return null;
        }
        try {
            String responseData = body.string();
            Gson gson = new Gson();
            WeatherInfo weatherInfo = gson.fromJson(responseData, WeatherInfo.class);
            Log.d(TAG, weatherInfo.toString());
            return weatherInfo;
        } catch (IOException e) {
            Log.d("WeatherQuery.parseJsonForWeather", "e:" + e);
        }
        return null;
    }

    private static void requestByOkHttp(String url, Callback responseCallback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(responseCallback);
    }

    public interface QueryCityCallback {
        void canQueryCityWeather(String cityRes);
    }

    public interface QueryWeatherCallback {
        void queryForWeather(WeatherInfo weatherInfo);
    }

}
