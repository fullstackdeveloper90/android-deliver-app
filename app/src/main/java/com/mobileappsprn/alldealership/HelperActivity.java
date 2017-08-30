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
import android.widget.ListView;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mobileappsprn.alldealership.entities.AppConstants;
import com.mobileappsprn.alldealership.entities.Items;
import com.mobileappsprn.alldealership.utilities.ApplicationVariables;

public class HelperActivity extends Activity {

    ListView listView;
    ArrayList<Items> menuItems = new ArrayList<Items>();
    String serverId;
    String dealershipGroupVersion;
    String uiTitle;
    String footerText;
    String dealerWebsite;

    boolean isCalledFrmMainActivity;
    boolean isCalledFrmcontacts;

    boolean isDealershipGroupVersion;
    private Tracker mTracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menuItems.removeAll(menuItems);
        isCalledFrmMainActivity = getIntent().getBooleanExtra("isCalledFrmMainActivity", false);
        isCalledFrmcontacts = true;
        Log.v("FLOW", "CAME HERE : HelperActivity");

        // Obtain the shared Tracker instance.
        ApplicationVariables application = (ApplicationVariables) getApplication();
        mTracker    =   application.getDefaultTracker();

        getMenuItems();

        AssetManager mg = getResources().getAssets();

        try {
            mg.open("appspecific.json");
            Log.v("Helper: ", "CAAAMEEEE   000 ");

        } catch (IOException ex) {
            Log.v("Helper: ", "CAAAMEEEE   111 ");
            ex.printStackTrace();
        }


        try {

            Log.v("Helper: ", "CAAAMEEEE   222 ");

            InputStream inpStream = null;
            String fileName = "appspecific.json";

            inpStream = getResources().getAssets().open(fileName);

            int size = inpStream.available();
            byte[] buffer = new byte[size];
            inpStream.read(buffer);
            inpStream.close();
            String bufferString = new String(buffer);

            JSONObject jsonObj = new JSONObject(bufferString);


            serverId = jsonObj.getString("a");
            GlobalState.serverId = serverId;

            Log.i("serverId: ", "" + serverId);

            dealershipGroupVersion = jsonObj.getString("DEALERSHIP_GROUP_VERSION");
            GlobalState.dealershipGroupVersion = dealershipGroupVersion;

            Log.i("dealershipGrpVer: ", "" + dealershipGroupVersion);

            if (dealershipGroupVersion.equalsIgnoreCase("true")) {
                isDealershipGroupVersion = true;
                GlobalState.isDealershipGroupVersion = isDealershipGroupVersion;
            } else {
                isDealershipGroupVersion = false;
                GlobalState.isDealershipGroupVersion = isDealershipGroupVersion;
            }

            GlobalState.menuType = jsonObj.getString("MENU_TYPE");

            if (GlobalState.menuType.equals("0")) {
                GlobalState.isNewMenu = false;
                GlobalState.isNewSubMenu = false;
            } else if (GlobalState.menuType.equals("1")) {
                GlobalState.isNewMenu = true;
                GlobalState.isNewSubMenu = false;
            } else {
                GlobalState.isNewMenu = true;
                GlobalState.isNewSubMenu = true;
            }

            uiTitle = jsonObj.getString("UI_TITLE");
            GlobalState.uiTitle = uiTitle;

            footerText = jsonObj.getString("FOOTER_TEXT");
            GlobalState.footerText = footerText;

            dealerWebsite = jsonObj.getString("DEALER_WEBSITE");
            //GlobalState.dealerWebsite = dealerWebsite;

            GlobalState.rowBGColor1 = jsonObj.getString("ROW_1_BG_COLOR");
            GlobalState.rowFGColor1 = jsonObj.getString("ROW_1_FG_COLOR");
            GlobalState.rowBGColor2 = jsonObj.getString("ROW_2_BG_COLOR");
            GlobalState.rowFGColor2 = jsonObj.getString("ROW_2_FG_COLOR");

        } catch (Exception e) {
            Log.v("Helper: ", "CAAAMEEEE   333 ");
            e.printStackTrace();
        }

        SharedPreferences myPrefs = getApplicationContext().getSharedPreferences("HomePrefs", MODE_PRIVATE);



        if (isCalledFrmMainActivity) {

            if (GlobalState.isNewMenu && GlobalState.isNewSubMenu) {
                Log.v("MainActivity: ", "CAAAMEEEE Here 7");
                Intent intent1 = new Intent(HelperActivity.this, HomeActivity.class);
                intent1.putExtra("isCalledFrmSettingsActivity", true);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
                finish();
            } else {
                Log.v("MainActivity: ", "CAAAMEEEE Here 8");
                Intent intent = new Intent(HelperActivity.this, HomeActivity.class);
                intent.putExtra("isCalledFrmSettingsActivity", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

        } else if (GlobalState.isDealershipGroupVersion) {

            Log.v("MainActivity: ", "CAAAMEEEE Here IS ITEM SELECTED : "+ myPrefs.getBoolean("isItemSelected", false));


            if (myPrefs.getBoolean("isItemSelected", false)) {

                if (GlobalState.isNewMenu && GlobalState.isNewSubMenu) {
                    Log.v("MainActivity: ", "CAAAMEEEE Here 1");
                    Intent intent1 = new Intent(HelperActivity.this, MainActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent1);
                    finish();
                } else {
                    Log.v("MainActivity: ", "CAAAMEEEE Here 2");
                    Intent intent = new Intent(HelperActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }

            } else {
                if (GlobalState.isNewMenu) {
                    Log.v("MainActivity: ", "CAAAMEEEE Here 5");
                    Intent intent1 = new Intent(HelperActivity.this, HomeActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent1);
                    finish();
                } else {
                    Log.v("MainActivity: ", "CAAAMEEEE Here 6");
                    Intent intent = new Intent(HelperActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }

            }
        } else {
            if (GlobalState.isNewMenu && GlobalState.isNewSubMenu) {
                Log.v("MainActivity: ", "CAAAMEEEE Here 3");
                Intent intent1 = new Intent(HelperActivity.this, MainActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
                finish();
            } else {
                Log.v("MainActivity: ", "CAAAMEEEE Here 4");
                Intent intent = new Intent(HelperActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mTracker != null) {
            mTracker.setScreenName(getClass().getSimpleName());
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    /**
     * To read Menu Items from Assets folder
     */
    private void getMenuItems() {
        try {
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

            Log.v("Helper Activity", "MENU ITEMSSS : 1 " + result.length());

            for (int i = 0; i < result.length(); i++) {
                JSONObject c = result.getJSONObject(i);
                Items items = new Items();
                if (c.has("Title")) {
                    items.setTitle(c.getString("Title"));
                    GlobalState.locationName = c.getString("Title");
                }

                if (c.has("Subtitle"))
                    items.setSubtitle(c.getString("Subtitle"));

                //getting lat,lon
                if (c.has("lat")) {
                    items.setLat(c.optString("lat"));
                    AppConstants.MENU_PREFFERED_LAT = items.getLat();
                    GlobalState.dealershipLat = items.getLat();
                }
                if (c.has("lon")) {
                    items.setLon(c.optString("lon"));
                    AppConstants.MENU_PREFFERED_LON = items.getLon();
                    GlobalState.dealershipLon = items.getLon();
                }

                Log.v("Location", "Loc : " + GlobalState.dealershipLat + "," + GlobalState.dealershipLon);


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
                if (c.has("DFLID")) {
                    items.setDFLID(c.getString("DFLID"));
                    GlobalState.dealershipId = c.getString("DFLID");
                } else
                    items.setDFLID("");

                //this is all data to be used for get directions
                if (c.has("StreetAddress")) {
                    items.setAddress(c.getString("StreetAddress"));
                    GlobalState.dealershipAddress = c.getString("StreetAddress");
                } else
                    items.setAddress("");

                if (c.has("Email")) {
                    items.setEmail(c.getString("Email"));
                    GlobalState.dealershipEmail = c.getString("Email");
                } else
                    items.setEmail("");

                if (c.has("PhoneNo")) {
                    items.setPhoneno(c.getString("PhoneNo"));
                    GlobalState.dealershipPhone = c.getString("PhoneNo");
                } else
                    items.setPhoneno("");

                if (c.has("PhoneNoService")) {
                    items.setPhoneNoService(c.getString("PhoneNoService"));
                    GlobalState.dealershipServicePhone = c.getString("PhoneNoService");
                } else
                    items.setPhoneNoService("");

                if (c.has("Website")) {
                    items.setWebsite(c.getString("Website"));
                    GlobalState.dealerWebsite = c.getString("Website");
                } else
                    items.setWebsite("");

                String img_name = c.getString("ShowIconType");
                AssetManager assetManager = getAssets();
                String path = "";
                try {

                    if (Arrays.asList(assetManager.list("MFG-Icons")).contains(img_name))
                        path = AppConstants.MFG_ICONS_FILE_PATH;
                    else if (Arrays.asList(assetManager.list("App-Specific-Icons")).contains(img_name)) {
                        path = AppConstants.APP_SPECIFIC_ICONS_FILE_PATH;
                    } else if (Arrays.asList(assetManager.list("MenuIcons2")).contains(img_name)) {
                        path = AppConstants.MENU_ICONS2_FILE_PATH;
                    } else if (Arrays.asList(assetManager.list("NewMenu")).contains(img_name)) {
                        path = AppConstants.NEW_MENU_FILE_PATH;
                    } else if (Arrays.asList(assetManager.list("General-Icons")).contains(img_name)) {
                        path = AppConstants.GENERAL_ICON_FILE_PATH;
                    }

                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                items.setShowIconType(path + c.getString("ShowIconType"));

                menuItems.add(items);
                Log.v("Helper Activity", "MENU ITEMSSS : " + menuItems.size());

            }


            GlobalState.menuItems = menuItems;
            GlobalState.homeMenuItems = menuItems;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        FlurryAgent.onStartSession(this, getResources().getString(R.string.flurry_key));
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }
}
