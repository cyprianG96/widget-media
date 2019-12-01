package com.mobica.widgets.calendar;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.icu.text.DateFormatSymbols;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.mobica.widgets.R;
import com.mobica.widgets.utils.Utils;

import java.text.SimpleDateFormat;

public class CalendarProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle options) {
        int maxWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        int maxHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);

        RemoteViews rv;

        if (maxWidth > maxHeight) {
            Intent intent = new Intent(context, CalendarWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            rv = new RemoteViews(context.getPackageName(), R.layout.calendar_long);
            rv.setRemoteAdapter(R.id.gridView, intent);
            rv.setEmptyView(R.id.gridView, R.id.emptyView);
        } else {
            rv = new RemoteViews(context.getPackageName(), R.layout.calendar);
        }

        Calendar calendar = Calendar.getInstance();
        String dayNumberSuffix = Utils.dayNumberSuffix(calendar.get(java.util.Calendar.DAY_OF_MONTH));
        rv.setTextViewText(R.id.todayDate, new SimpleDateFormat("MMMM d'" + dayNumberSuffix + "'").format(calendar.getTime()));
        rv.setTextViewText(R.id.todayDateYear, new SimpleDateFormat(" yyyy").format(calendar.getTime()));

        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] weekdays = dfs.getWeekdays();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        rv.setTextViewText(R.id.todayDayName, weekdays[day]);

        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        updateAppWidget(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, appWidgetManager.getAppWidgetOptions(appWidgetId));
        }
    }
}
