package com.mobica.widgets;

import java.util.Arrays;
import java.util.List;

public class Constants {

    public static class Layout {
        public static final String LAYOUT_ACTION = "com.mobica.action.LAYOUT_CHANGED";
        public static final String LAYOUT_KEY = "mobica_layout";
        public static final String LAYOUT_NORMAL = "normal_layout";
        public static final String LAYOUT_MIRROR = "mirror_layout";
    }

    public static class Theme {
        public static final String THEME_ACTION = "com.mobica.action.THEME_CHANGED";
        public static final String THEME_KEY = "mobica_theme";
        public static final String THEME_PURPLE = "purple";
        public static final String THEME_BLUE = "blue";
        public static final String THEME_GREEN = "green";
    }

    public static class Cluster {
        public static final String CLUSTER_ACTION = "com.mobica.clustercomm.BIND";
        public static final String CLUSTER_PACKAGE = "com.mobica.clustercomm";
    }

    public static class RegionalSettings {
        public static final String EU = "EU";
        public static final String UK = "UK";

        public static List<String> getRegionalSettings() {
            return Arrays.asList(EU, UK);
        }
    }

    public static class Custom {
        public static final String DASHBOARD_LOGO = "DASHBOARD_LOGO";
    }

}
