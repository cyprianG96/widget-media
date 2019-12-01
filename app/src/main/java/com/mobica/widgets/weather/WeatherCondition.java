package com.mobica.widgets.weather;

import com.mobica.widgets.utils.UnitSystem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class WeatherCondition {

    public UnitSystem unitSystem;
    public String location = "";
    public int degrees = 0;
    public int feelsLike = 0;
    public String conditionName = "";
    public int humidity = 0;
    public int pressure = 0;
    public int wind = 0;
    public String windDir = "";
    public WeatherImage image;

    public WeatherCondition(String json) {
        unitSystem = UnitSystem.byLocale(Locale.getDefault());
        try {
            JSONObject res = new JSONObject(json);
            JSONObject location = res.getJSONObject("location");
            this.location = String.format("%s, %s", location.getString("name"),
                    location.getString("country"));
            JSONObject current = res.getJSONObject("current");
            if (unitSystem == UnitSystem.Metric) {
                degrees = (int) current.getDouble("temp_c");
                feelsLike = (int) current.getDouble("feelslike_c");
            } else {
                degrees = (int) current.getDouble("temp_f");
                feelsLike = (int) current.getDouble("feelslike_f");
            }
            JSONObject condition = current.getJSONObject("condition");
            conditionName = condition.getString("text");
            humidity = (int) current.getDouble("humidity");
            if (unitSystem == UnitSystem.Metric) {
                pressure = (int) current.getDouble("pressure_mb");
                wind = (int) current.getDouble("wind_kph");
            } else {
                pressure = (int) current.getDouble("pressure_in");
                wind = (int) current.getDouble("wind_mph");
            }
            windDir = current.getString("wind_dir");
            int code = condition.getInt("code");
            if (code <= 1003) {
                image = WeatherImage.Sun;
            } else if (code == 1006 || code == 1009 || code == 1030 || code == 1135) {
                image = WeatherImage.Cloud;
            } else {
                image = WeatherImage.Rain;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public enum WeatherImage {
        Sun,
        Cloud,
        Rain
    }
}
