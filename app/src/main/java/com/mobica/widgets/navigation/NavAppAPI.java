package com.mobica.widgets.navigation;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;

import com.tomtom.navapp.ErrorCallback;
import com.tomtom.navapp.Instruction;
import com.tomtom.navapp.NavAppClient;
import com.tomtom.navapp.NavAppError;
import com.tomtom.navapp.RouteableInfo;
import com.tomtom.navapp.Trip;
import com.tomtom.navui.api.util.Log;

import java.util.List;
import java.util.Locale;

public class NavAppAPI implements Trip.Listener, Trip.InfoListener,
        Trip.ProgressListener, Instruction.Listener {

    private Context context;
    private NavAppClient navappClient;
    private NavigationData lastUpdateNavigationData;
    private NavigationData navigationData;

    public NavAppAPI(Context context) {
        this.context = context;
        ErrorCallback errorCallback = new ErrorCallback() {
            @Override
            public void onError(final NavAppError error) {
                Log.e("ZXC", "onError(" + error.getErrorMessage() + ")\n" +
                        error.getStackTraceString());
                navappClient = null;
            }
        };
        navappClient = NavAppClient.Factory.make(context, errorCallback);
        lastUpdateNavigationData = new NavigationData();
        clearNavigationData();
        registerListeners();
    }

    private String getFormattedETA(long etaInSecs) {
        Calendar utcCalendar = Calendar.getInstance(TimeZone.getDefault());
        utcCalendar.setTimeInMillis(etaInSecs * 1000L);
        int hours = utcCalendar.get(Calendar.HOUR_OF_DAY);
        int minutes = utcCalendar.get(Calendar.MINUTE);
        return String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
    }

    private String getFormattedDistance(long distance) {
        StringBuilder str = new StringBuilder();
        if (distance >= 1000) {
            str.append(distance / 1000L).append(" km");
        } else {
            str.append(distance).append(" m");
        }
        return str.toString();
    }

    private void registerListeners() {
        navappClient.getTripManager().registerTripListener(this);
        navappClient.getTripManager().registerTripProgressListener(this);
        navappClient.getTripManager().registerWaypointsArrivalListener(this);
        navappClient.getGuidanceManager().registerInstructionListener(this);
        updateNavigationData();
    }

    private void unregisterListeners() {
        navappClient.getTripManager().unregisterTripListener(this);
        navappClient.getTripManager().unregisterTripProgressListener(this);
        navappClient.getTripManager().unregisterWaypointsArrivalListener(this);
        navappClient.getGuidanceManager().unregisterInstructionListener(this);
    }

    public void close() {
        if (navappClient != null) {
            unregisterListeners();
            navappClient.close();
        }
    }

    @Override
    public void onTripActive(Trip trip) {
        if (trip == null) clearNavigationData();
    }

    @Override
    public void onInfo(Trip trip, List<RouteableInfo> list) {
    }

    @Override
    public void onTripProgress(Trip trip, long eta, int distanceRemaining) {
        if (trip != null) {
            navigationData.estimatedArrival = getFormattedETA(eta);
            navigationData.distance = getFormattedDistance(distanceRemaining);
        } else {
            navigationData.estimatedArrival = "";
            navigationData.distance = "";
        }
        updateNavigationData();
    }

    @Override
    public void onTripArrival(Trip trip) {
        if (trip == null) clearNavigationData();
    }

    @Override
    public void onError(Trip.RequestError requestError) {
    }

    @Override
    public void onInstructionList(List<Instruction> list) {
        if (list.size() > 0) {
            navigationData.currentManeuverId = getManeuverId(list.get(0));
            navigationData.currentPlace = list.get(0).getAttributeString(Instruction.Attributes.INSTRUCTION_ROAD_NAME);
            navigationData.distanceToManeuver = getFormattedDistance((long) list.get(0).getAttributeDouble(Instruction.Attributes.INSTRUCTION_DISTANCE_TO_INSTRUCTION_METERS));
            if (list.size() > 1) {
                navigationData.nextManeuverId = getManeuverId(list.get(1));
            } else {
                navigationData.nextManeuverId = -1;
            }
        } else {
            navigationData.currentManeuverId = -1;
            navigationData.currentPlace = "";
            navigationData.distanceToManeuver = "";
            navigationData.nextManeuverId = -1;
        }
        updateNavigationData();
    }

    private void updateNavigationData() {
        if (!navigationData.equals(lastUpdateNavigationData) || navigationData.isEmpty()) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("NavigationData", Context.MODE_PRIVATE);
            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.putString("distance", navigationData.distance);
            sharedPreferencesEditor.putString("estimatedArrival", navigationData.estimatedArrival);
            sharedPreferencesEditor.putString("distanceToManeuver", navigationData.distanceToManeuver);
            sharedPreferencesEditor.putInt("currentManeuverId", navigationData.currentManeuverId);
            sharedPreferencesEditor.putInt("nextManeuverId", navigationData.nextManeuverId);
            sharedPreferencesEditor.putString("currentPlace", navigationData.currentPlace);
            sharedPreferencesEditor.apply();
            NavigationProvider.updateWidgets(context);
            lastUpdateNavigationData.setFields(navigationData);
        }
    }

    private void clearNavigationData() {
        navigationData = new NavigationData();
        updateNavigationData();
    }

    private static int getManeuverId(Instruction instruction) {
        boolean isRoundabout = instruction.getAttributeString(Instruction.Attributes.INSTRUCTION_JUNCTION_TYPE).equalsIgnoreCase("roundabout");
        double angle = instruction.getAttributeDouble(Instruction.Attributes.INSTRUCTION_ANGLE);
        if (isRoundabout) {
            if (angle == 0.0) return 2;
            else if (angle < 0.0) return 0;
            else return 1;
        } else {
            if (angle == 0.0) return 3;
            else if (angle == -45.0) return 4;
            else if (angle == -90.0) return 5;
            else if (angle == -135.0) return 6;
            else if (angle == -180.0) return 7;
            else if (angle == 45.0) return 8;
            else if (angle == 90.0) return 9;
            else if (angle == 135.0) return 10;
            else if (angle == 180.0) return 11;
            else return -1;
        }
    }
}
