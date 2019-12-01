package com.mobica.widgets.weather;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;

import com.mobica.widgets.HttpRequestThread;
import com.mobica.widgets.R;
import com.mobica.widgets.utils.LocationUtils;
import com.mobica.widgets.utils.SharedPrefsUtils;
import com.mobica.widgets.utils.UnitSystem;

public class Weather extends AppWidgetProvider {

    static void updateAppWidget(final Context context, final int appWidgetId) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        updateAppWidget(context, appWidgetManager, appWidgetId, appWidgetManager.getAppWidgetOptions(appWidgetId));
    }

    static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager, final int appWidgetId) {
        updateAppWidget(context, appWidgetManager, appWidgetId, appWidgetManager.getAppWidgetOptions(appWidgetId));
    }

    static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager, final int appWidgetId, Bundle options) {
        int maxWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        int maxHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);
        final boolean isLong = maxWidth > maxHeight;

        String location = SharedPrefsUtils.loadLocationForWidget(context, appWidgetId);
        if (location.isEmpty()) {
            Intent intent = new Intent(context, Weather.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            location = LocationUtils.getCurrentLocation(context, pendingIntent);
            if (!location.isEmpty()) {
                SharedPrefsUtils.saveLocationForWidget(context, appWidgetId, location);
            }
        }
        appWidgetManager.updateAppWidget(appWidgetId, createRemoteViews(context, isLong, appWidgetId));
        if (!location.isEmpty()) requestCurrentCondition(context, appWidgetId, isLong, location);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        updateAppWidget(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private static RemoteViews createRemoteViews(Context context, boolean isLong, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), isLong ? R.layout.weather_long : R.layout.weather);
        Intent intent = new Intent(context, WeatherConfigureActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.root, pendingIntent);
        return views;
    }

    private static void requestCurrentCondition(final Context context, final int appWidgetId, final boolean isLong, String location) {
        new HttpRequestThread("http://api.apixu.com/v1/current.json?key=851f9a23ff23485d9b383147192406&q=" + location).start(new HttpRequestThread.OnHttpRequestListener() {
            @Override
            public void onHttpResponseOk(String response) {
                WeatherCondition condition = new WeatherCondition(response);
                RemoteViews remoteViews = createRemoteViews(context, isLong, appWidgetId);
                fillRemoteViews(context, remoteViews, condition, isLong);
                AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, remoteViews);
            }
            @Override
            public void onHttpResponseError(int errorCode, String response) { }
        });
    }

    private static void fillRemoteViews(Context context, RemoteViews views, WeatherCondition condition, boolean isLong) {
        if (condition != null) {
            views.setTextViewText(R.id.weatherLocation, condition.location);
            views.setTextViewText(R.id.weatherDegreesNumber, String.valueOf(condition.degrees));
            views.setTextViewText(R.id.weatherFeelsLike,
                    context.getString(R.string.weather_feels_like,
                            condition.feelsLike));
            views.setTextViewText(R.id.weatherType, condition.conditionName);
            if (isLong) {
                views.setTextViewText(R.id.weatherHumidity, context.getString(R.string.weather_humidity, condition.humidity));
                if (condition.unitSystem == UnitSystem.Metric) {
                    views.setTextViewText(R.id.weatherPressure,
                            context.getString(R.string.weather_pressure_metrics,
                                    condition.pressure));
                    views.setTextViewText(R.id.weatherWind,
                            context.getString(R.string.weather_wind_metrics,
                                    condition.wind, condition.windDir));
                } else {
                    views.setTextViewText(R.id.weatherPressure,
                            context.getString(R.string.weather_pressure_imperial,
                                    condition.pressure));
                    views.setTextViewText(R.id.weatherWind,
                            context.getString(R.string.weather_wind_imperial,
                                    condition.wind, condition.windDir));
                }
            }
            switch (condition.image) {
                case Sun: {
                    views.setViewVisibility(R.id.sun_background, View.VISIBLE);
                    views.setViewVisibility(R.id.cloud_background, View.GONE);
                    views.setViewVisibility(R.id.rain_background, View.GONE);
                    break;
                }
                case Cloud: {
                    views.setViewVisibility(R.id.sun_background, View.GONE);
                    views.setViewVisibility(R.id.cloud_background, View.VISIBLE);
                    views.setViewVisibility(R.id.rain_background, View.GONE);
                    break;
                }
                case Rain: {
                    views.setViewVisibility(R.id.sun_background, View.GONE);
                    views.setViewVisibility(R.id.cloud_background, View.GONE);
                    views.setViewVisibility(R.id.rain_background, View.VISIBLE);
                    break;
                }
            }
        }
    }
}
