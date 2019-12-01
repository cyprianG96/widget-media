package com.mobica.widgets.navigation;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.content.ContextCompat;

import com.mobica.widgets.R;

import java.util.Iterator;
import java.util.List;

public class NavigationProvider extends AppWidgetProvider {

    private static NavigationData loadNavigationData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("NavigationData", Context.MODE_PRIVATE);
        NavigationData navigationData = new NavigationData();
        navigationData.distance = sharedPreferences.getString("distance", "");
        navigationData.estimatedArrival = sharedPreferences.getString("estimatedArrival", "");
        navigationData.distanceToManeuver = sharedPreferences.getString("distanceToManeuver", "");
        navigationData.currentManeuverId = sharedPreferences.getInt("currentManeuverId", -1);
        navigationData.nextManeuverId = sharedPreferences.getInt("nextManeuverId", -1);
        navigationData.currentPlace = sharedPreferences.getString("currentPlace", "");
        return navigationData;
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle options, NavigationData navigationData) {
        int maxWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        int maxHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);

        RemoteViews rv;

        if (maxWidth > maxHeight) {
            rv = new RemoteViews(context.getPackageName(), R.layout.navigation_long);
        } else {
            rv = new RemoteViews(context.getPackageName(), R.layout.navigation);
        }

        Intent planRoadIntent = new Intent();
        planRoadIntent.setComponent(new ComponentName("com.tomtom.navui.stocknavapp",
                "com.tomtom.navui.stocknavapp.StockNavAppActivity"));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                planRoadIntent, 0);

        if (navigationData != null && !navigationData.isEmpty()) {
            rv.setViewVisibility(R.id.trip, View.VISIBLE);
            rv.setViewVisibility(R.id.noTrip, View.GONE);
            rv.setOnClickPendingIntent(R.id.trip, pendingIntent);
            rv.setTextViewText(R.id.navDistanceValue, navigationData.distance);
            rv.setTextViewText(R.id.navEstArrivalValue, navigationData.estimatedArrival);
            rv.setTextViewText(R.id.navManeuverDistance, navigationData.distanceToManeuver);
            hideAllManeuvers(rv);
            if (navigationData.currentManeuverId >= 0) {
                rv.setViewVisibility(Maneuver.values()[navigationData.currentManeuverId].viewId, View.VISIBLE);
            }
            if (navigationData.nextManeuverId >= 0) {
                rv.setViewVisibility(R.id.navThenValue, View.VISIBLE);
                rv.setViewVisibility(Maneuver.values()[navigationData.nextManeuverId].smallViewId, View.VISIBLE);
            } else {
                rv.setViewVisibility(R.id.navThenValue, View.INVISIBLE);
            }

            if (maxWidth > maxHeight) rv.setTextViewText(R.id.navManeuverPlace, navigationData.currentPlace);
        } else {
            rv.setViewVisibility(R.id.trip, View.GONE);
            rv.setViewVisibility(R.id.noTrip, View.VISIBLE);
            rv.setOnClickPendingIntent(R.id.noTrip, pendingIntent);
        }

        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        NavigationData navigationData = loadNavigationData(context);
        updateAppWidget(context, appWidgetManager, appWidgetId,
                newOptions, navigationData);
    }

    @Override
    public void onUpdate(final Context context, AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        NavigationData navigationData = loadNavigationData(context);
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId,
                    appWidgetManager.getAppWidgetOptions(appWidgetId), navigationData);
        }
        if (!isForegroundServiceRunning(context)) {
            startForegroundService(context);
        }
    }

    private static void hideAllManeuvers(RemoteViews rv) {
        for (Maneuver maneuver : Maneuver.values()) {
            rv.setViewVisibility(maneuver.viewId, View.GONE);
            rv.setViewVisibility(maneuver.smallViewId, View.GONE);
        }
    }

    public static void updateWidgets(Context context) {
        Intent intent = new Intent(context, NavigationProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(new ComponentName(context, NavigationProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        startForegroundService(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        stopForegroundService(context);
    }

    private static void startForegroundService(Context context) {
        Intent service = new Intent(context, NavClientService.class);
        service.setAction(NavClientService.ACTION.START_FOREGROUND);
        ContextCompat.startForegroundService(context, service);
    }

    private static void stopForegroundService(Context context) {
        Intent service = new Intent(context, NavClientService.class);
        service.setAction(NavClientService.ACTION.STOP_FOREGROUND);
        ContextCompat.startForegroundService(context, service);
    }

    private static boolean isForegroundServiceRunning(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> l = am.getRunningServices(50);
        Iterator<ActivityManager.RunningServiceInfo> i = l.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningServiceInfo runningServiceInfo = i.next();
            if (runningServiceInfo.service.getClassName().equals(NavClientService.class.getName()) &&
                    runningServiceInfo.foreground) {
                return true;
            }
        }
        return false;
    }
}
