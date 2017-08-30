package com.mobileappsprn.alldealership;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mobileappsprn.alldealership.adapters.NewMyArrayListAdapter;
import com.mobileappsprn.alldealership.entities.Items;
import com.mobileappsprn.alldealership.utilities.ApplicationVariables;
import com.mobileappsprn.alldealership.utilities.Utils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class HomeActivity extends Activity {

    ListView listView;
    ArrayList<Items> menuItems = new ArrayList<Items>();

    TextView textView,text_settings,text_version;
    boolean isCalledFrmSettingsActivity;

    private Tracker mTracker;
    SharedPreferences myPrefs;
    TextView headerTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menuItems.removeAll(menuItems);
        //GlobalState.homeMenuItems.removeAll(GlobalState.homeMenuItems);
        Log.v("FLOW", "CAME HERE : HomeActivity");
        // Obtain the shared Tracker instance.
        ApplicationVariables application = (ApplicationVariables) getApplication();
        mTracker    =   application.getDefaultTracker();

        initialise();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(mTracker != null) {
            mTracker.setScreenName("Image~" + getClass().getSimpleName());
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }


    public void initialise() {

        myPrefs = this.getSharedPreferences("HomePrefs",  Activity.MODE_PRIVATE);
        setContentView(R.layout.new_home_screen_main);
        listView = (ListView) findViewById(R.id.list);
        textView=(TextView)findViewById(R.id.textView1);
        headerTitle = (TextView) findViewById(R.id.addNewVehicle);

        if(! GlobalState.isVariantTcc){
            headerTitle.setText("Preferred Dealer");
        }

        isCalledFrmSettingsActivity = getIntent().getBooleanExtra("isCalledFrmSettingsActivity", false);
        if (isCalledFrmSettingsActivity)
        {
            text_settings=(TextView)findViewById(R.id.settings_title);
            text_version=(TextView)findViewById(R.id.settings_version);
            text_settings.setVisibility(View.VISIBLE);
            text_version.setVisibility(View.VISIBLE);
            text_version.setText("Version: 1.8");
            text_settings.setText("For information about this app, please contact "+GlobalState.dealershipEmail);
            Linkify.addLinks(text_settings, Linkify.EMAIL_ADDRESSES);
            if (GlobalState.homeMenuItems.size()==1)
            {
                listView.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
            }
        }
        if (myPrefs.getBoolean("isItemSelected", false))
        {
            int position=myPrefs.getInt("Itemposition",0);
            ImageView image=(ImageView)findViewById(R.id.logo);
            Bitmap bitmap = Utils.getBitmapFromAsset("App-Specific-Icons/" + GlobalState.homeMenuItems.get(position).getLogoURL(), this);
            image.setImageBitmap(bitmap);
        }

        menuItems=GlobalState.homeMenuItems;
        String[] titles = new String[menuItems.size()];

        for(int i = 0; i< menuItems.size(); i++)
            titles[i] = menuItems.get(i).getTitle();

        final NewMyArrayListAdapter adapter = new NewMyArrayListAdapter(this, titles);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

            //if(GlobalState.isFromMoreOptions) {
                SharedPreferences.Editor prefsEditor = myPrefs.edit();
                prefsEditor.putBoolean("isItemSelected", true);
                prefsEditor.putInt("Itemposition", position);
                Log.i("dfl_id: ", "CAAAMEEEE Here HOME Pos" + GlobalState.homeMenuItems.get(position).getDFLID());
                Log.v("MainActivity: ", "CAAAMEEEE Here HOME Pos : " + position + "  DFL-ID : " + GlobalState.homeMenuItems.get(position).getDFLID());
                prefsEditor.putString("dfl_id", GlobalState.homeMenuItems.get(position).getDFLID());
                prefsEditor.commit();
            //}

                String phn=GlobalState.homeMenuItems.get(position).getPhoneno();
                GlobalState.dealershipPhone=phn;
                System.out.println("checkphone...." + phn);
                GlobalState.dealershipServicePhone=GlobalState.homeMenuItems.get(position).getPhoneNoService();
                GlobalState.locationName =GlobalState.homeMenuItems.get(position).getTitle();
                GlobalState.dealershipAddress=GlobalState.homeMenuItems.get(position).getAddress();
                GlobalState.dealershipLat=GlobalState.homeMenuItems.get(position).getLat();
                GlobalState.dealershipLon=GlobalState.homeMenuItems.get(position).getLon();
                GlobalState.dealershipEmail=GlobalState.homeMenuItems.get(position).getEmail();
                GlobalState.dealerWebsite= GlobalState.homeMenuItems.get(position).getWebsite();
                GlobalState.logoUrl=  GlobalState.homeMenuItems.get(position).getLogoURL();
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
                                        }
        );
    }
    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        FlurryAgent.onStartSession(this, getResources().getString(R.string.flurry_key));
    }
    @Override
    protected void onStop()
    {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }
}
