package com.mobica.widgets.calendar;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.mobica.widgets.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private int count = 42;
    private List<Day> days = new ArrayList<>();
    private Context context;

    CalendarRemoteViewsFactory(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        int dow = calendar.get(Calendar.DAY_OF_WEEK);
        dow = (dow + 5) % 7;
        dow = dow == 0 ? 7 : dow;

        calendar.add(Calendar.DAY_OF_YEAR, -dow);

        for (int i = 1; i <= count; ++i) {
            Day day = new Day();
            day.otherMonth = calendar.get(Calendar.MONTH) != currentMonth;
            day.number = calendar.get(Calendar.DAY_OF_MONTH);

            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (day.number == currentDay) {
                day.type = Day.DayType.Today;
            } else if (dayOfWeek == Calendar.SUNDAY || (day.number == 20 && currentMonth == 5)) {
                day.type = Day.DayType.Holiday;
            } else {
                day.type = Day.DayType.Normal;
            }
            if (!day.otherMonth) {
                switch (day.number) {
                    case 12: day.eventCount = 1; break;
                    case 13: day.eventCount = 2; break;
                    case 20: day.eventCount = 1; break;
                    case 27: day.eventCount = 2; break;
                }
                if (day.number == currentDay + 1) {
                    day.eventCount = 1;
                } else if (day.number == currentDay) {
                    day.eventCount = 3;
                }
            }
            days.add(day);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Day day = days.get(position);
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.calendar_item);

        switch (day.type) {
            case Normal:
                rv.setViewVisibility(R.id.bg, View.VISIBLE);
                rv.setViewVisibility(R.id.bg_today, View.GONE);
                rv.setViewVisibility(R.id.bg_holiday, View.GONE);
                break;
            case Today:
                rv.setViewVisibility(R.id.bg, View.GONE);
                rv.setViewVisibility(R.id.bg_today, View.VISIBLE);
                rv.setViewVisibility(R.id.bg_holiday, View.GONE);
                break;
            case Holiday:
                rv.setViewVisibility(R.id.bg, View.GONE);
                rv.setViewVisibility(R.id.bg_today, View.GONE);
                rv.setViewVisibility(R.id.bg_holiday, View.VISIBLE);
                break;
        }

        rv.setTextViewText(R.id.day, String.valueOf(day.number));
        if (day.otherMonth) {
            rv.setTextColor(R.id.day, 0xff06041a);
        } else {
            rv.setTextColor(R.id.day, Color.WHITE);
        }
        switch (day.eventCount) {
            case 1: {
                rv.setViewVisibility(R.id.dot1, View.VISIBLE);
                rv.setViewVisibility(R.id.dot2, View.GONE);
                rv.setViewVisibility(R.id.dot3, View.GONE);
                break;
            }
            case 2: {
                rv.setViewVisibility(R.id.dot1, View.VISIBLE);
                rv.setViewVisibility(R.id.dot2, View.VISIBLE);
                rv.setViewVisibility(R.id.dot3, View.GONE);
                break;
            }
            case 3: {
                rv.setViewVisibility(R.id.dot1, View.VISIBLE);
                rv.setViewVisibility(R.id.dot2, View.VISIBLE);
                rv.setViewVisibility(R.id.dot3, View.VISIBLE);
                break;
            }
            default: {
                rv.setViewVisibility(R.id.dot1, View.GONE);
                rv.setViewVisibility(R.id.dot2, View.GONE);
                rv.setViewVisibility(R.id.dot3, View.GONE);
                break;
            }

        }
        return rv;
    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
