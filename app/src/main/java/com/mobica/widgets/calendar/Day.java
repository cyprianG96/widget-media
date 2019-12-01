package com.mobica.widgets.calendar;

public class Day {
    public int number = 0;
    public DayType type = DayType.Normal;
    public int eventCount = 0;
    public boolean otherMonth = true;

    enum DayType {
        Normal,
        Today,
        Holiday,
    }
}
