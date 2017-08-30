package com.mobileappsprn.alldealership;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mobileappsprn.alldealership.utilities.ApplicationVariables;

/**
 * Created by FuGenX-03 on 20-09-2016.
 */
public class MPGHistory extends Activity {

    private ListView mpgList;
    SharedPreferences preferences;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mpg_history);
        // Obtain the shared Tracker instance.
        ApplicationVariables application = (ApplicationVariables) getApplication();
        mTracker    =   application.getDefaultTracker();
        Log.v("FLOW", "CAME HERE : MPGHistory");
        mpgList = (ListView) findViewById(R.id.mpgListView);
        preferences = PreferenceManager.getDefaultSharedPreferences(MPGHistory.this);

        String[] records = preferences.getString("mpgHistoryData","").replace(",","\n").split("\\*");

        ArrayAdapter<String> recordsAdapter = new ArrayAdapter<String>(this, R.layout.history_item, records);

        mpgList.setAdapter(recordsAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mTracker != null) {
            mTracker.setScreenName(getClass().getSimpleName());
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }
}
