package com.mobileappsprn.alldealership;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.mobileappsprn.alldealership.tutorial.TutorialActivity;
import com.mobileappsprn.alldealership.utilities.Utils;

import io.fabric.sdk.android.Fabric;

/**
 * Created by sri on 16/08/16.
 */
public class SplashScreen extends Activity {

    public static final int SLEEP_TIME = 3000;
    private ImageView splashIV;
    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics()); //Fabric Analytics
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash_screen);
        Log.v("FLOW", "CAME HERE : SplashScreen");

        //loading splash screen dynamically from assets app-specific folder
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Bitmap bitmap = Utils.getBitmapFromAsset("App-Specific-Icons/default2x.png", this);
        splashIV = (ImageView) findViewById(R.id.splashScreenIV);
        splashIV.setImageBitmap(bitmap);

        new SplashWait().execute();

    }

    private class SplashWait extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... params) {

            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Checking weather the tutorial screen is shown or not initially.
            if(preferences.contains("isTutorialShown") && preferences.getBoolean("isTutorialShown", false)){
                startActivity(new Intent(SplashScreen.this, HelperActivity.class));
            } else {
                startActivity(new Intent(SplashScreen.this, TutorialActivity.class));
            }

            finish();

        }
    }

}
