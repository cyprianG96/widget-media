package com.mobica.widgets.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import androidx.core.content.ContextCompat;

import com.mobica.widgets.StartActivity;

import java.util.List;
import java.util.Locale;

public class LocationUtils {

    public static String getCurrentLocation(Context context, PendingIntent locationResuestResult) {
        String result = "";
        String locationProvider = getLocationProvider(context);
        if (!locationProvider.isEmpty()) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(context);
            } else {
                result = getCurrentAddress(context, locationResuestResult, locationProvider);
            }
        }
        return result;
    }

    private static void requestPermissions(Context context) {
        Intent intent = new Intent(context, StartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    private static String getLocationProvider(Context context) {
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = "";

        try {
            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationProvider = LocationManager.GPS_PROVIDER;
            }
        } catch (Exception ignored) {}

        try {
            if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationProvider = LocationManager.NETWORK_PROVIDER;
            }
        } catch (Exception ignored) {}

        return locationProvider;
    }

    @SuppressLint("MissingPermission")
    private static String getCurrentAddress(Context context, PendingIntent locationResuestResult, String locationProvider) {
        String result = "";

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            Location location = locationManager.getLastKnownLocation(locationProvider);

            if (location == null) {
                locationManager.requestSingleUpdate(locationProvider, locationResuestResult);
            } else {
                Geocoder gcd = new Geocoder(context, Locale.getDefault());
                List<Address> addresses;
                try {
                    addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (!addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        String locality = address.getLocality();
                        if (locality != null) {
                            String subLocality = address.getSubLocality();
                            if (subLocality != null) {
                                result = locality + "," + subLocality;
                            } else {
                                result = locality;
                            }
                        } else {
                            result = address.getSubAdminArea();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }
}
