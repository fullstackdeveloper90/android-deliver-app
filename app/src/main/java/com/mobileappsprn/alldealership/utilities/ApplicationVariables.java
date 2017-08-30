package com.mobileappsprn.alldealership.utilities;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.mobileappsprn.alldealership.R;

/**
 * Created by sri on 18/04/16.
 */
public class ApplicationVariables extends MultiDexApplication {
    private Tracker mTracker;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
             mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }
}
