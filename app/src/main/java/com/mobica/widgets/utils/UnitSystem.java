package com.mobica.widgets.utils;

import java.util.Locale;

public enum UnitSystem {
    Imperial,
    Metric;

    public static UnitSystem byLocale(Locale locale) {
        String countryCode = locale.getCountry();
        if ("UK".equals(countryCode)) return Imperial; // UK
        if ("US".equals(countryCode)) return Imperial; // USA
        if ("LR".equals(countryCode)) return Imperial; // Liberia
        if ("MM".equals(countryCode)) return Imperial; // Myanmar
        return Metric;
    }
}
