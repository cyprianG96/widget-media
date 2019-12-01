package com.mobica.widgets.weather;

import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;

import com.mobica.widgets.R;

import static com.mobica.widgets.Constants.Theme.THEME_BLUE;
import static com.mobica.widgets.Constants.Theme.THEME_GREEN;
import static com.mobica.widgets.Constants.Theme.THEME_KEY;
import static com.mobica.widgets.Constants.Theme.THEME_PURPLE;

public class WeatherConfigureActivity extends AppCompatActivity implements WeatherConfigureFragment.Callback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setCorrectMobicaTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_configure);

        WeatherConfigureFragment.newInstance(this).show(getSupportFragmentManager(), "WeatherConfigureFragment");
    }

    private void setCorrectMobicaTheme() {
        String currentTheme = Settings.Global.getString(getApplicationContext().getContentResolver(), THEME_KEY);

        if (currentTheme != null) {
            switch (currentTheme) {
                case THEME_BLUE:
                    setTheme(R.style.AppThemeBlue);
                    break;
                case THEME_GREEN:
                    setTheme(R.style.AppThemeGreen);
                    break;
                case THEME_PURPLE:
                    setTheme(R.style.AppThemePurple);
                    break;
            }
        } else {
            setTheme(R.style.AppThemeBlue);
        }
    }

    @Override
    public void onCloseWeatherConfigure() {
        finish();
    }
}
