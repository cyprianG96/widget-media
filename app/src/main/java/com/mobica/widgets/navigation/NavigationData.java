package com.mobica.widgets.navigation;

import androidx.annotation.Nullable;

public class NavigationData {
    public String distance = "";
    public String estimatedArrival = "";
    public String distanceToManeuver = "";
    public int currentManeuverId = -1;
    public int nextManeuverId = -1;
    public String currentPlace = "";

    public void setFields(NavigationData navData) {
        distance = navData.distance;
        estimatedArrival = navData.estimatedArrival;
        distanceToManeuver = navData.distanceToManeuver;
        currentManeuverId = navData.currentManeuverId;
        nextManeuverId = navData.nextManeuverId;
        currentPlace = navData.currentPlace;
    }

    public boolean isEmpty() {
        return distance.isEmpty() &&
                estimatedArrival.isEmpty() &&
                distanceToManeuver.isEmpty() &&
                currentPlace.isEmpty() &&
                currentManeuverId == -1 &&
                nextManeuverId == -1;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof NavigationData) {
            NavigationData navData = (NavigationData) obj;
            return distance.equals(navData.distance) &&
                    estimatedArrival.equals(navData.estimatedArrival) &&
                    distanceToManeuver.equals(navData.distanceToManeuver) &&
                    currentManeuverId == navData.currentManeuverId &&
                    nextManeuverId == navData.nextManeuverId &&
                    currentPlace.equals(navData.currentPlace);
        } else {
            return false;
        }
    }
}
