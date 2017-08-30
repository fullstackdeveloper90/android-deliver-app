package com.mobileappsprn.alldealership;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mobileappsprn.alldealership.adapters.MyArrayAdapter;
import com.mobileappsprn.alldealership.contactus.ContactUsListView;
import com.mobileappsprn.alldealership.entities.AppConstants;
import com.mobileappsprn.alldealership.entities.Items;
import com.mobileappsprn.alldealership.utilities.ApplicationVariables;


public class MoreLocations extends Activity implements OnItemClickListener {
    TextView textView;
    ListView listView;
    ArrayList<Items> menuItems = new ArrayList<Items>();
    public static String flag;
    SharedPreferences myPrefs;
    private Tracker mTracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen_main);
        // Obtain the shared Tracker instance.
        ApplicationVariables application = (ApplicationVariables) getApplication();
        mTracker    =   application.getDefaultTracker();
        myPrefs = this.getSharedPreferences("HomePrefs", Activity.MODE_PRIVATE);
        ImageView image = (ImageView) findViewById(R.id.logo);
        image.setImageDrawable(getResources().getDrawable(R.drawable.logo_hd));
        Log.v("FLOW", "CAME HERE : MoreLocations");
        textView = (TextView) findViewById(R.id.textView1);
        listView = (ListView) findViewById(android.R.id.list);
        listView.setOnItemClickListener(this);
        getParseData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mTracker != null) {
            mTracker.setScreenName("Image~" + getClass().getSimpleName());
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    private void getParseData() {
        try {

            menuItems.removeAll(menuItems);
            InputStream inpStream = null;
            String fileName = AppConstants.JSON_FILE_PATH + "MenuPreferredDealer.json";

            inpStream = this.getAssets().open(fileName);

            int size = inpStream.available();
            byte[] buffer = new byte[size];
            inpStream.read(buffer);
            inpStream.close();
            String bufferString = new String(buffer);

            JSONObject json = new JSONObject(bufferString);

            JSONArray result = json.getJSONArray("Items");

            for (int i = 0; i < result.length(); i++) {
                JSONObject c = result.getJSONObject(i);
                Items items = new Items();
                if (c.has("Title"))
                    items.setTitle(c.getString("Title"));

                if (c.has("Subtitle"))
                    items.setSubtitle(c.getString("Subtitle"));


                //getting lat,lon
                if (c.has("lat")) {
                    items.setLat(c.optString("lat"));
                }
                if (c.has("lon")) {
                    items.setLon(c.optString("lon"));
                }

                if (c.has("Tag"))
                    items.setTag(c.getInt("Tag"));
                else
                    items.setTag(-1);

                if (c.has("Type"))
                    items.setType(c.getString("Type"));
                else
                    items.setType("");

                if (c.has("Display"))
                    items.setDisplay(c.getString("Display"));
                else
                    items.setDisplay("");

                if (c.has("Url"))
                    items.setUrl(c.getString("Url"));
                else
                    items.setUrl("");

                if (c.has("LogoURL"))
                    items.setLogoURL(c.getString("LogoURL"));
                else
                    items.setUrl("");
                if (c.has("DFLID"))
                    items.setDFLID(c.getString("DFLID"));
                else
                    items.setUrl("");

                //this is all data to be used for get directions
                if (c.has("StreetAddress"))
                    items.setAddress(c.getString("StreetAddress"));
                else
                    items.setAddress("");
                if (c.has("Email"))
                    items.setEmail(c.getString("Email"));
                else
                    items.setEmail("");
                if (c.has("PhoneNo"))
                    items.setPhoneno(c.getString("PhoneNo"));


                else
                    items.setPhoneno("");
                if (c.has("PhoneNoService")) {
                    items.setPhoneNoService(c.getString("PhoneNoService"));
                    GlobalState.dealershipServicePhone = c.getString("PhoneNoService");
                } else
                    items.setPhoneNoService("");
                if (c.has("Website"))
                    items.setWebsite(c.getString("Website"));
                else
                    items.setWebsite("");

                String img_name = c.getString("ShowIconType");
                AssetManager assetManager = getAssets();
                String path = "";
                try {

                    if (Arrays.asList(assetManager.list("MFG-Icons")).contains(img_name))
                        path = AppConstants.MFG_ICONS_FILE_PATH;
                    else if (Arrays.asList(assetManager.list("App-Specific-Icons")).contains(img_name)) {
                        path = AppConstants.APP_SPECIFIC_ICONS_FILE_PATH;
                    }

                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                items.setShowIconType(path + c.getString("ShowIconType"));
                //items.setShowIconType(AppConstants.MFG_ICONS_FILE_PATH+c.getString("ShowIconType"));

                menuItems.add(items);

            }

            GlobalState.homeMenuItems = menuItems;
            GlobalState.menuItems = menuItems;
            String[] titles = new String[GlobalState.homeMenuItems.size()];

            for (int i = 0; i < GlobalState.homeMenuItems.size(); i++)
                titles[i] = GlobalState.homeMenuItems.get(i).getTitle();

            String[] subtitles = new String[GlobalState.homeMenuItems.size()];

            for (int i = 0; i < GlobalState.homeMenuItems.size(); i++)
                subtitles[i] = GlobalState.homeMenuItems.get(i).getSubtitle();
            final MyArrayAdapter adapter = new MyArrayAdapter(this, titles, subtitles);
            listView.setAdapter(adapter);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
        SharedPreferences.Editor prefsEditor = myPrefs.edit();

        prefsEditor.putBoolean("isItemSelected", true);
        prefsEditor.putInt("Itemposition", position);
        prefsEditor.putString("dfl_id", GlobalState.homeMenuItems.get(position).getDFLID());
        prefsEditor.commit();
        Intent intent = new Intent(MoreLocations.this, ContactUsListView.class);
        flag = "temp";
        intent.putExtra("tag", AppConstants.CONTACTS);
        intent.putExtra("title", menuItems.get(position).getDisplay());
        Toast.makeText(MoreLocations.this,"1"+menuItems.get(position).getDisplay(),Toast.LENGTH_SHORT).show();
        String phn = GlobalState.homeMenuItems.get(position).getPhoneno();
        GlobalState.tempdealershipPhone = phn;
        GlobalState.locationName = GlobalState.homeMenuItems.get(position).getTitle();
        System.out.println("checkphone...." + phn);
        GlobalState.tempdealershipServicePhone = GlobalState.homeMenuItems.get(position).getPhoneNoService();
        GlobalState.tempdealershipAddress = GlobalState.homeMenuItems.get(position).getAddress();
        GlobalState.tempdealershipLat = GlobalState.homeMenuItems.get(position).getLat();
        GlobalState.tempdealershipLon = GlobalState.homeMenuItems.get(position).getLon();
        GlobalState.tempdealershipEmail = GlobalState.homeMenuItems.get(position).getEmail();
        GlobalState.tempdealerWebsite = GlobalState.homeMenuItems.get(position).getWebsite();



        //GlobalState.logoUrl = GlobalState.homeMenuItems.get(position).getLogoURL();



        startActivity(intent);
        finish();

    }

}
