package com.mobileappsprn.alldealership.utilities;

import android.location.Location;

/**
 * Created by jeremy on 4/3/15.
 *interface that a class must implement if it calls updateLocationUntilStop
 *contains name of the callback funciton when location is changed
 */
public interface LocationUpdateReceiver {
    public void onLocationChangedCallback();
}
