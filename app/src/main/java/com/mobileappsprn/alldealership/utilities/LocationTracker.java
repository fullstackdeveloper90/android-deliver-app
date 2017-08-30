package com.mobileappsprn.alldealership.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.mobileappsprn.alldealership.Manifest;
import com.mobileappsprn.alldealership.ParkingMapActivity;

/**
 * Created by jeremy on 4/2/15.
 */
public class LocationTracker extends Service implements LocationListener {
    public static LocationTracker locationTracker;
    Context context;
    LocationManager locationManager;
    Boolean isGPSEnabled;
    Boolean isNetworkEnabled;
    String pingProvider, updatesProvider;
    Location location;
    Boolean singlePing;
    Activity activity;

    //initializes the singleton and pings the location on the app's opening
    //things to think about: do I need to get a different context for every activity that calls the singleton?
    protected LocationTracker(Context context ) {
        this.context = context;
        this.activity = activity;
        locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        setProviders();
        setLocation();
        pingLocation(context);
    }

    public static LocationTracker getLocationTracker(Context context) {
        if (locationTracker == null) {
            locationTracker = new LocationTracker(context);
        }

        return locationTracker;
    }

    public Boolean canGetLocation() {
        return (isGPSEnabled || isNetworkEnabled);
    }

    //pings location once using the pingProvider. onlocationchanged is called when location is recieved
    public void pingLocation(Context context) {
        this.context = context;
        singlePing = true;
        locationManager.requestLocationUpdates(pingProvider, 0, 0, this);
    }

    //updates location until stopUpdates is called.
    //this function should not run for very long, it is battery-intensive.
    public void updateLocationUntilStop(Context context) {
        this.context = context;
        singlePing = false;
        //updates every two seconds
        locationManager.requestLocationUpdates(updatesProvider, 2000, 0, this);
    }

    public void stopUpdates() {
        locationManager.removeUpdates(this);
    }


    public double getLatitude() {
        if (location != null) {
//            return 39.0997;
            return location.getLatitude();
        } else {
            return 0;
        }
    }

    public double getLongitude() {
        if (location != null) {
//            return -94.5783;
            return location.getLongitude();
        } else {
            return 0;
        }
    }

    public double getRecentLatitude() {
        getRecentLocation();
        return getLatitude();
    }

    public double getRecentLongitude() {
        getRecentLocation();
        return getLongitude();
    }

    private void getRecentLocation() {
        Location networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Location gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (networkLocation.getTime() < gpsLocation.getTime()) {
            location = networkLocation;
        } else {
            location = gpsLocation;
        }
    }

    private void setProviders() {
        if (isGPSEnabled && isNetworkEnabled) {
            pingProvider = locationManager.NETWORK_PROVIDER;
            updatesProvider = locationManager.GPS_PROVIDER;
        } else if (!isGPSEnabled && isNetworkEnabled) {
            pingProvider = locationManager.NETWORK_PROVIDER;
            updatesProvider = locationManager.NETWORK_PROVIDER;
        } else if (isGPSEnabled && !isNetworkEnabled) {
            pingProvider = locationManager.GPS_PROVIDER;
            updatesProvider = locationManager.GPS_PROVIDER;
        } else {
            pingProvider = locationManager.NETWORK_PROVIDER;
            updatesProvider = locationManager.NETWORK_PROVIDER;
        }
    }

    private void setLocation() {


        this.location = locationManager.getLastKnownLocation(pingProvider);
        if (location == null) {
            this.location = locationManager.getLastKnownLocation(updatesProvider);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("locationTracker", "onlocationchangedcalled");
        this.location = location;
//        debug: alert on screen when location has been pinged
//        Toast.makeText(context, "location pinged at time " + location.getTime(), Toast.LENGTH_SHORT).show();
        if (singlePing) {
            locationManager.removeUpdates(this);
        } else {
            LocationUpdateReceiver l = (LocationUpdateReceiver) context;
            l.onLocationChangedCallback();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        if (provider.equalsIgnoreCase(locationManager.GPS_PROVIDER)) {
            isGPSEnabled = false;
            setProviders();
        } else if (provider.equalsIgnoreCase(locationManager.NETWORK_PROVIDER)) {
            isNetworkEnabled = false;
            setProviders();
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        if (provider.equalsIgnoreCase(locationManager.GPS_PROVIDER)) {
            isGPSEnabled = true;
            setProviders();
        } else if (provider.equalsIgnoreCase(locationManager.NETWORK_PROVIDER)) {
            isNetworkEnabled = true;
            setProviders();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
}


