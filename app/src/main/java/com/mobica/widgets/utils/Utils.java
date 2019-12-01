package com.mobica.widgets.utils;

public class Utils {
    public static String dayNumberSuffix(int dayNumber) {
        switch (dayNumber % 10) {
            case 1: return "st";
            case 2: return "nd";
            case 3: return "rd";
            default: return "th";
        }
    }
}
