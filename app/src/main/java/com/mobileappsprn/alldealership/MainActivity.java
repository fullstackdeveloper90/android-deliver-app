package com.mobileappsprn.alldealership;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mobileappsprn.alldealership.adapters.NewMyArrayGridAdapter;
//import com.mobileappsprn.alldealership.analytics.AnalyticsApplication;
import com.mobileappsprn.alldealership.autoscrollviewpager.AutoScrollViewPager;
import com.mobileappsprn.alldealership.autoscrollviewpager.BannerFragment;
import com.mobileappsprn.alldealership.contactus.ContactUsListView;
import com.mobileappsprn.alldealership.entities.AppConstants;
import com.mobileappsprn.alldealership.entities.Items;
import com.mobileappsprn.alldealership.tutorial.ViewPagerBottomTickerAdapter;
import com.mobileappsprn.alldealership.utilities.ApplicationVariables;
import com.mobileappsprn.alldealership.utilities.GPSTracker;
import com.mobileappsprn.alldealership.utilities.PermissionUtil;
import com.mobileappsprn.alldealership.utilities.Utils;


public class MainActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {
    public static final int REQUEST_PERMISSION_SETTING = 1;
    public static int SLIDER_DURATION = 1000;
    public static String DEFAULT_BG_COLOR = "#000000";
    public static String DEFAULT_FG_COLOR = "#ffffff";

    GridView gridView, gridViewSlider;
    LinearLayout linearLayout;
    ArrayList<Items> menuItems = new ArrayList<Items>();
    public static ArrayList<Items> sliderItems = new ArrayList<Items>();
    ArrayList<Items> bottomTickerItems = new ArrayList<Items>();
    ArrayList<Items> leftMenuItems = new ArrayList<Items>();

    int tagValue;
    SharedPreferences pref, mypref;

    String[] vinArray;
    String[] carDetailsArrayToLoad;
    Map<String, String> carDetailsHMap;

    int selectedCarIndex;

    SharedPreferences myPrefs;
    SharedPreferences prefKeyValues;
    SharedPreferences selectedCarSharedPref;
    SharedPreferences.Editor editor;
    private GPSTracker mGPS;
    double currentLatitude, currentLongitude;
    String lat, lon;

    boolean isSelectedCarPrefPopulated;
    boolean isPreferencePopulated;
    boolean isMainHeaderAvailable;

    String mainHeaderEmail;
    String dflId;

    private static final int ZBAR_SCANNER_REQUEST = 0;
    private static final int ZBAR_QR_SCANNER_REQUEST = 1;
    Items centreItem;
    TextView callUsText;
    ImageView iv_callEmail;
    TextView tv_callEmail;

    AutoScrollViewPager scrollBanner;
    BannerAdapter bannerAdapter;

    /*Bottom Ticker */
    private ViewPager vpItem;
    private ViewPagerBottomTickerAdapter vbtAdapter;
    private FrameLayout frameLay;
    private LinearLayout leftSliderLay;
    private ImageView hamburger;
    private Tracker mTracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("FLOW", "CAME HERE : MainActivity");
        checkPermissions();

    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.MAPS_RECEIVE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.MAPS_RECEIVE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                PermissionUtil.askForLocationPermission(this);


            }
        }
    }


//    public void launchScanner(View v) {
//        if (isCameraAvailable()) {
//            Intent intent = new Intent(this, ZBarScannerActivity.class);
//            startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
//        } else {
//            Toast.makeText(this, "Rear Facing Camera Unavailable", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    public void launchQRScanner() {
//        if (isCameraAvailable()) {
//            Intent intent = new Intent(this, ZBarScannerActivity.class);
//            intent.putExtra(ZBarConstants.SCAN_MODES, new int[]{Symbol.QRCODE});
//            startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
//        } else {
//            Toast.makeText(this, "Rear Facing Camera Unavailable", Toast.LENGTH_SHORT).show();
//        }
//    }

    public void launchScanner(View v) {
        if (isCameraAvailable()) {
            Intent intent = new Intent(this, ScannerActivity.class);
            startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
        } else {
            Toast.makeText(this, "Rear Facing Camera Unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    public void launchQRScanner() {
        if (isCameraAvailable()) {
            Intent intent = new Intent(this, ScannerActivity.class);
//            intent.putExtra(ZBarConstants.SCAN_MODES, new int[]{Symbol.QRCODE});
            startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
        } else {
            Toast.makeText(this, "Rear Facing Camera Unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isCameraAvailable() {
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ZBAR_SCANNER_REQUEST:
            case ZBAR_QR_SCANNER_REQUEST:
                if (resultCode == RESULT_OK) {
                    String url = AppConstants.QRSCANNER_SERVER_URL + "a=" + GlobalState.serverId
                            + "&DFLID=" + dflId + "&u=&type=&d=" + getUniqueDeviceId() + "&k=" + Utils.getRegistrationId(MainActivity.this)  + "&dn=" + getDeviceName();

                    Log.i("QR SCANNER SERVER URL ", url);

//                    Intent intent = new Intent(NewMainActivity.this, QRScannerWebViewActivity.class);
//                    intent.putExtra("qrScanUrl", data.getStringExtra(ZBarConstants.SCAN_RESULT));
//                    intent.putExtra("serverUrl", url);
//                    startActivity(intent);

                } else if (resultCode == RESULT_CANCELED && data != null) {
//                    String error = data.getStringExtra(ZBarConstants.ERROR_INFO);
//                    if (!TextUtils.isEmpty(error)) {
//                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
//                    }
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        menuItems.removeAll(menuItems);
        sliderItems.clear();
        bottomTickerItems.clear();
        leftMenuItems.clear();

        initialise();
        vehiclSpecials();

        if (GlobalState.isRegisterRemainderEnable) {
            registrationRemainder();  // -disable if not needed
        }
        //Google analytics
        analytics();

        new RetrieveFeedTask().execute();

    }

    public String getUniqueDeviceId() {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) this
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String deviceId = telephonyManager.getDeviceId();
            if (telephonyManager.getDeviceId() == null) {
                String android_id = Secure.getString(this.getContentResolver(),
                        Secure.ANDROID_ID);
                deviceId = android_id;
            }
            return deviceId;
        } catch (Exception e) {
            Log.i("Dealership", "exception getUniqueDeviceId " + e);
        }
        return null;
    }

    public String getDeviceName() {
        String deviceName = android.os.Build.MODEL;
        return deviceName;
    }

    public void showAlert(String alertMsg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        alertDialogBuilder.setTitle("Alert!");

        alertDialogBuilder
                .setMessage(alertMsg)
                .setNeutralButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.dismiss();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void initialise() {

        Utils.registerWithGcm(MainActivity.this.getApplicationContext());

        Bitmap bitmap = null;
        setContentView(R.layout.new_activity_main);

        iv_callEmail = (ImageView) findViewById(R.id.imagecall);
        tv_callEmail = (TextView) findViewById(R.id.callUsText);
        leftSliderLay = (LinearLayout) findViewById(R.id.leftSliderLay);
        hamburger = (ImageView) findViewById(R.id.hamburger);
        frameLay = (FrameLayout) findViewById(R.id.bottomTickerLay);
        callUsText = (TextView) findViewById(R.id.callUsText);
        callUsText.setText("CALL US " + GlobalState.dealershipPhone);

        callUsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMainHeaderAvailable)
                    new Utils().sendEmail(MainActivity.this, mainHeaderEmail);
                else
                    new Utils().makeCall(MainActivity.this);
            }
        });

        centreItem = new Items();
        pref = getApplicationContext().getSharedPreferences("ShockleyPref", 0);
        mypref = getApplicationContext().getSharedPreferences("HomePrefs", Activity.MODE_PRIVATE);
        dflId = mypref.getString("dfl_id", ""); //Here not coming any DFL-ID

        if(dflId == null || dflId.equals("")){
            dflId = GlobalState.dealershipId;
        }

        Log.v("MainActivity: ", "CAAAMEEEE Here MAin "+dflId);

       /* if (myPrefs.getBoolean("isItemSelected", false))
        {
            int position=myPrefs.getInt("Itemposition",0);
            ImageView image=(ImageView)findViewById(R.id.logo);
            bitmap = Utils.getBitmapFromAsset("App-Specific-Icons/" + GlobalState.homeMenuItems.get(position).getLogoURL(), this);
            image.setImageBitmap(bitmap);
        }*/

        int position = mypref.getInt("Itemposition", 0);
        ImageView image = (ImageView) findViewById(R.id.imagelogo);


        try {

            String img_name = GlobalState.homeMenuItems.get(position).getLogoURL();
            Log.v("ImageName", "ImageName : " + img_name);
            AssetManager assetManager = getAssets();

            if (Arrays.asList(assetManager.list("MFG-Icons")).contains(img_name)) {
                bitmap = Utils.getBitmapFromAsset(AppConstants.MFG_ICONS_FILE_PATH + img_name, this);
                image.setImageBitmap(bitmap);
            } else if (Arrays.asList(assetManager.list("App-Specific-Icons")).contains(img_name)) {
                bitmap = Utils.getBitmapFromAsset(AppConstants.APP_SPECIFIC_ICONS_FILE_PATH + img_name, this);
                image.setImageBitmap(bitmap);
            }

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        if (GlobalState.isVariantTcc) {
            image.setImageDrawable(getResources().getDrawable(R.drawable.logo_hd));
        }

        Intent intent = getIntent();
        tagValue = intent.getIntExtra("tag", 0);
        mGPS = new GPSTracker(MainActivity.this);
        if (mGPS.canGetLocation()) {
            currentLatitude = mGPS.getLatitude();
            currentLongitude = mGPS.getLongitude();
            lat = String.valueOf(currentLatitude);
            lon = String.valueOf(currentLongitude);

            System.out.println("current lat" + currentLatitude + "current longi" + currentLongitude);

        } else {
            mGPS.showSettingsAlert();
        }

        InputStream inpStream = null;

        String JSONfileName = AppConstants.JSON_FILE_PATH;

        if (tagValue == AppConstants.MORE_OPTIONS)
            JSONfileName += "Menu_More.json";

        else if (tagValue == AppConstants.MY_CAR)
            JSONfileName += "Menu_MyCar.json";

        else if (tagValue == AppConstants.SERVICE_DEPARTMENT)
            JSONfileName += "Menu_ServiceDepartment.json";

        else if (tagValue == AppConstants.Menu_FuelSavingTips)
            JSONfileName += "Menu_FuelSavingTips.json";

        else
            JSONfileName += "Menu_Main.json";

        try {
            Log.v("JsonFileName", "FileName : " + JSONfileName);
            String imag_name = "";
            inpStream = this.getAssets().open(JSONfileName);

            int size = inpStream.available();
            byte[] buffer = new byte[size];
            inpStream.read(buffer);
            inpStream.close();
            String bufferString = new String(buffer);

            JSONObject json = new JSONObject(bufferString);

            JSONArray result = json.getJSONArray("Items");


            if (json.has("Header")) {

                JSONObject headerObj = json.getJSONObject("Header");
                mainHeaderEmail = headerObj.getString("Title");
                iv_callEmail.setImageResource(android.R.drawable.sym_action_email);
                tv_callEmail.setText(mainHeaderEmail);
                isMainHeaderAvailable = true;


            }

            if (json.has("CenterItems")) {

                JSONArray resultJ = json.getJSONArray("CenterItems");

                if (resultJ.length() == 0) {
                    Items centreItem = new Items();

                    JSONObject obj = json.getJSONArray("CenterItems").getJSONObject(0);

                    if (obj.has("Title"))
                        centreItem.setTitle(obj.getString("Title"));
                    else
                        centreItem.setTitle("");

                    if (obj.has("Subtitle"))
                        centreItem.setSubtitle(obj.getString("Subtitle"));
                    else
                        centreItem.setSubtitle("");

                    if (obj.has("Display"))
                        centreItem.setDisplay(obj.getString("Display"));
                    else
                        centreItem.setDisplay("");

                    if (obj.has("Type"))
                        centreItem.setType(obj.getString("Type"));
                    else
                        centreItem.setType("");

                    if (obj.has("Url"))
                        centreItem.setUrl(obj.getString("Url"));
                    else
                        centreItem.setUrl("");

                    if (obj.has("isBrowser"))
                        centreItem.setBrowser(obj.getBoolean("isBrowser"));
                    else
                        centreItem.setBrowser(false);

                    if (obj.has("Tag"))
                        centreItem.setTag(obj.getInt("Tag"));
                    else
                        centreItem.setTag(obj.getInt("Tag"));

                    if (obj.has("ShowIconType"))
                        imag_name = obj.getString("ShowIconType");
                    else
                        imag_name = "";


                    AssetManager assetManagr = getAssets();
                    String path = "";

                    try {
                        if (Arrays.asList(assetManagr.list("MFG-Icons")).contains(imag_name))
                            path = AppConstants.MFG_ICONS_FILE_PATH;
                        else if (Arrays.asList(assetManagr.list("App-Specific-Icons")).contains(imag_name)) {
                            path = AppConstants.APP_SPECIFIC_ICONS_FILE_PATH;
                        }

                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    Log.v("ShowIconType", "ShowIconType : " + obj.has("ShowIconType") + ",Path : " + path);
                    if (obj.has("ShowIconType"))
                        centreItem.setShowIconType(path + obj.getString("ShowIconType"));
                    else
                        centreItem.setShowIconType("");
                } else {

                    scrollBanner = (AutoScrollViewPager) findViewById(R.id.bannerPager);
                    //int height = (int)(0 *(0.65));
                    //RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
                    //scrollBanner.setLayoutParams(params);
                    scrollBanner.setBorderAnimation(false);
                    //scrollBanner.setInterval(SLIDER_DURATION);
                    scrollBanner.startAutoScroll();
                    bannerAdapter = new BannerAdapter(getSupportFragmentManager());
                    scrollBanner.setAdapter(bannerAdapter);

                    scrollBanner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        }

                        @Override
                        public void onPageSelected(int position) {

                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });

                    for (int i = 0; i < resultJ.length(); i++) {

                        Items centreItem = new Items();

                        JSONObject obj = json.getJSONArray("CenterItems").getJSONObject(i);

                        if (obj.has("Title"))
                            centreItem.setTitle(obj.getString("Title"));
                        else
                            centreItem.setTitle("");

                        if (obj.has("Subtitle"))
                            centreItem.setSubtitle(obj.getString("Subtitle"));
                        else
                            centreItem.setSubtitle("");

                        if (obj.has("Display"))
                            centreItem.setDisplay(obj.getString("Display"));
                        else
                            centreItem.setDisplay("");

                        if (obj.has("Type"))
                            centreItem.setType(obj.getString("Type"));
                        else
                            centreItem.setType("");

                        if (obj.has("Url"))
                            centreItem.setUrl(obj.getString("Url"));
                        else
                            centreItem.setUrl("");

                        if (obj.has("isBrowser"))
                            centreItem.setBrowser(obj.getBoolean("isBrowser"));
                        else
                            centreItem.setBrowser(false);

                        if (obj.has("Tag"))
                            centreItem.setTag(obj.getInt("Tag"));
                        else
                            centreItem.setTag(obj.getInt("Tag"));

                        if (obj.has("ShowIconType"))
                            imag_name = obj.getString("ShowIconType");
                        else
                            imag_name = "";

                        if (i == 0 && obj.has("SliderDuration"))
                            SLIDER_DURATION = Integer.valueOf(obj.getString("SliderDuration")) * 1000;


                        AssetManager assetManagr = getAssets();
                        String path = "";

                        try {
                            if (Arrays.asList(assetManagr.list("MFG-Icons")).contains(imag_name))
                                path = AppConstants.MFG_ICONS_FILE_PATH;
                            else if (Arrays.asList(assetManagr.list("App-Specific-Icons")).contains(imag_name)) {
                                path = AppConstants.APP_SPECIFIC_ICONS_FILE_PATH;
                            }

                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        Log.v("ShowIconType", "ShowIconType : " + obj.has("ShowIconType") + ",Path : " + path);
                        if (obj.has("ShowIconType"))
                            centreItem.setShowIconType(path + obj.getString("ShowIconType"));
                        else
                            centreItem.setShowIconType("");

                        scrollBanner.setInterval(SLIDER_DURATION);
                        sliderItems.add(centreItem);

                    }
                }

            }

            for (int i = 0; i < result.length(); i++) {
                JSONObject c = result.getJSONObject(i);
                Items items = new Items();
                if (c.has("Title"))
                    items.setTitle(c.getString("Title"));

                if (c.has("Subtitle"))
                    items.setSubtitle(c.getString("Subtitle"));

                if (c.has("Tag"))
                    items.setTag(c.getInt("Tag"));
                else
                    items.setTag(-1);

                if (c.has("isBrowser"))
                    items.setBrowser(c.getBoolean("isBrowser"));
                else
                    items.setBrowser(false);

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

                if (c.has("ShowIconType")) {
                    items.setShowIconType(AppConstants.NEW_MENU_FILE_PATH + c.getString("ShowIconType"));
                    Log.v("FileNames", "File " + ":" + AppConstants.NEW_MENU_FILE_PATH + c.getString("ShowIconType"));

                } else {
                    items.setShowIconType(" ");
                }

                menuItems.add(items);


            }//for


            if (json.has("BottomItems")) {

                JSONArray bottomTickerItemsArray = json.getJSONArray("BottomItems");

                for (int i = 0; i < bottomTickerItemsArray.length(); i++) {
                    JSONObject c = bottomTickerItemsArray.getJSONObject(i);
                    Items items = new Items();
                    if (c.has("Title"))
                        items.setTitle(c.getString("Title"));

                    if (c.has("Subtitle"))
                        items.setSubtitle(c.getString("Subtitle"));

                    if (c.has("Tag"))
                        items.setTag(c.getInt("Tag"));
                    else
                        items.setTag(-1);

                    if (c.has("isBrowser"))
                        items.setBrowser(c.getBoolean("isBrowser"));
                    else
                        items.setBrowser(false);

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

                    if (c.has("ShowIconType")) {
                        items.setShowIconType(AppConstants.NEW_MENU_FILE_PATH + c.getString("ShowIconType"));
                        Log.v("FileNames", "File " + ":" + AppConstants.NEW_MENU_FILE_PATH + c.getString("ShowIconType"));

                    } else {
                        items.setShowIconType(" ");
                    }

                    if (c.has("SliderDuration"))
                        items.setSliderDuration(c.getString("SliderDuration"));
                    else
                        items.setSliderDuration("");

                    bottomTickerItems.add(items);

                }//for loop ends

                bottomTicker(); //Load Bottom Ticker if Bottom Ticker items available.


            } else {
                frameLay.getLayoutParams().height = 0;
            }

            GlobalState.menuItems = menuItems;

            bannerAdapter.updateList(sliderItems);

        } catch (Exception e) {
            e.printStackTrace();
        }


        selectedCarSharedPref = getApplicationContext().getSharedPreferences("selected_car_shared_pref", Context.MODE_PRIVATE);
        isSelectedCarPrefPopulated = selectedCarSharedPref.getBoolean("isSelectedCarPrefPopulated", false);
        isPreferencePopulated = pref.getBoolean("isPreferencePopulated", false);

        prefKeyValues = getApplicationContext().getSharedPreferences("hashmap_shared_pref", Context.MODE_PRIVATE);

        vinArray = new String[40];
        carDetailsHMap = new TreeMap<String, String>();

        Log.i("MainActivity: ", "isPreferencePopulated: " + isPreferencePopulated);

        if (isPreferencePopulated) {
            @SuppressWarnings("unchecked")
            HashMap<String, String> map = (HashMap<String, String>) prefKeyValues.getAll();
            for (String keyString : map.keySet()) {
                String value = map.get(keyString);
                carDetailsHMap.put(keyString, value);
                //Log.i("carDetailsHMap-key-MainActivity: ", "" + keyString);
                Log.i("carDetHM-key-MActivity:", "" + keyString);
            }

            int p = 0;

            for (String keyString : carDetailsHMap.keySet()) {
                String carDetailsStringToLoad = carDetailsHMap.get(keyString);
                carDetailsArrayToLoad = new String[12];
                carDetailsArrayToLoad = carDetailsStringToLoad.split(",");

                //Log.i("uploadedImage: ", "uploaded: "+carDetailsArrayToLoad[2]);

                try {
                    vinArray[p] = carDetailsArrayToLoad[1];
                    //Log.i("carDetailsArrayToLoad.length: ", "" + carDetailsArrayToLoad.length);
                    Log.i("carDetArrToLoad.len:", "" + carDetailsArrayToLoad.length);

                    Log.i("vinArray[i]: ", "" + vinArray[p]);
                    p++;

                } catch (Exception e) {
                    //Log.i("ArrayOutOfBound Exception", "" + e.getMessage());
                    Log.i("ArrayOutOfBound Ex", "" + e.getMessage());
                }
            }

            selectedCarIndex = selectedCarSharedPref.getInt("selectedCar", 0);
            Log.i("MainActivity: ", "selectedCarIndex: " + selectedCarIndex);
            Log.i("MainActivity: ", "vinSelected: " + vinArray[selectedCarIndex]);

        }

        String[] imagesList = new String[menuItems.size()];

        for (int i = 0; i < menuItems.size(); i++)
            imagesList[i] = menuItems.get(i).getShowIconType();

        /*String[] subtitles = new String[menuItems.size()];

        for(int i = 0; i< menuItems.size(); i++)
            subtitles[i] = menuItems.get(i).getSubtitle();*/


        gridView = (GridView) findViewById(R.id.grid);


        final NewMyArrayGridAdapter adapter = new NewMyArrayGridAdapter(this, imagesList);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                GlobalState.isFromMoreOptions = false;

                if (menuItems.get(position).getType().equals("help") ||
                        menuItems.get(position).getType().equals("redirect")) {

                    Log.i("MainActivity: ", "inside 'help'-check, position: " + position);
                    Intent intent = new Intent(MainActivity.this, HelpWebViewActivity.class);

                    String feedUrl = menuItems.get(position).getUrl();
                    if (feedUrl.contains("www.mobileappscm.com")) {
                        Log.i("MainActivity: ", "inside Here 1");
                        if (GlobalState.isDealershipGroupVersion) {


                            if (feedUrl.contains("SERVERID")) {


                                feedUrl = feedUrl.replaceAll("SERVERID", "SERVERID&DFLID=" + dflId);
                                if (feedUrl.contains("SERVERID"))
                                    feedUrl = feedUrl.replaceAll("SERVERID", GlobalState.serverId);

                                Log.i("MainActivity: ", "inside Here 1 DFLId: "+dflId+"  Server Id : "+GlobalState.serverId+"   feed: "+feedUrl);
                            } else if (feedUrl.contains("?")) {
                                feedUrl = feedUrl + "&a=" + GlobalState.serverId + "&DFLID=" + dflId;
                            } else {
                                feedUrl = feedUrl + "?a=" + GlobalState.serverId + "&DFLID=" + dflId;
                            }
                        } else {
                            if (feedUrl.contains("SERVERID")) {
                                feedUrl = feedUrl.replaceAll("SERVERID", "SERVERID&DFLID=" + GlobalState.dealershipId);
                                if (feedUrl.contains("SERVERID"))
                                    feedUrl = feedUrl.replaceAll("SERVERID", GlobalState.serverId);
                            } else if (feedUrl.contains("?")) {
                                feedUrl = feedUrl + "&a=" + GlobalState.serverId + "&DFLID=" + GlobalState.dealershipId;
                            } else {
                                feedUrl = feedUrl + "?a=" + GlobalState.serverId + "&DFLID=" + GlobalState.dealershipId;
                            }
/*
                            if (feedUrl.contains("SERVERID"))
                            {
                                feedUrl = feedUrl.replaceAll("SERVERID", GlobalState.serverId);
                            }
                            else if (feedUrl.contains("?"))
                            {
                                feedUrl = feedUrl + "&a=" + GlobalState.serverId;
                            }
                            else
                            {
                                feedUrl = feedUrl + "?a=" + GlobalState.serverId;
                            }
*/
                        }

                        feedUrl = feedUrl + "&u=" + getUniqueDeviceId() + "&k=" + Utils.getRegistrationId(MainActivity.this) + "&dn=" + getDeviceName();
                    }

                    intent.putExtra("helpUrl", feedUrl);
                    Log.i("webViewTitle: ", menuItems.get(position).getTitle());
                    intent.putExtra("webViewTitle", menuItems.get(position).getTitle());
                    intent.putExtra("Tag", String.valueOf(menuItems.get(position).getTag()));
                    intent.putExtra("browser", menuItems.get(position).isBrowser());
                    intent.putExtra("position", position);
                    Log.i("MainActivity: ", "Url is 1 : " + menuItems.get(position).getUrl());
                    startActivity(intent);

                } else if (menuItems.get(position).getType().equals("callservice")) {
                    makeCall();
                } else if (menuItems.get(position).getType().equals("call")) {
                    new Utils().makeCall(MainActivity.this);
                } else if (menuItems.get(position).getType().equals("newweb")) {
                    Log.i("MainActivity: ", "inside Here 2");
                    Log.i("MainActivity: ", "inside 'help'-check, position: " + position);
                    Intent intent = new Intent(MainActivity.this, HelpWebViewActivity.class);

                    String feedUrl = menuItems.get(position).getUrl();
                    if (!feedUrl.contains("www.mobileappscm.com")) {
                        if (GlobalState.isDealershipGroupVersion) {
                            if (feedUrl.contains("SERVERID")) {
                                feedUrl = feedUrl.replaceAll("SERVERID", "SERVERID&DFLID=" + dflId);
                                if (feedUrl.contains("SERVERID"))
                                    feedUrl = feedUrl.replaceAll("SERVERID", GlobalState.serverId);
                            } else if (feedUrl.contains("?"))
                                feedUrl = feedUrl + "&a=" + GlobalState.serverId + "&DFLID=" + dflId;
                            else
                                feedUrl = feedUrl + "?a=" + GlobalState.serverId + "&DFLID=" + dflId;

                        } else {
                            if (feedUrl.contains("SERVERID")) {
                                feedUrl = feedUrl.replaceAll("SERVERID", GlobalState.serverId);
                            } else if (feedUrl.contains("?"))
                                feedUrl = feedUrl + "&a=" + GlobalState.serverId;
                            else
                                feedUrl = feedUrl + "?a=" + GlobalState.serverId;
                        }

                        feedUrl = feedUrl + "&u=" + getUniqueDeviceId() + "&k=" + Utils.getRegistrationId(MainActivity.this) + "&dn=" + getDeviceName();
                    }
                    intent.putExtra("helpUrl", feedUrl);
                    Log.i("webViewTitle: ", menuItems.get(position).getTitle());
                    intent.putExtra("webViewTitle", menuItems.get(position).getTitle());
                    intent.putExtra("Tag", String.valueOf(menuItems.get(position).getTag()));
                    intent.putExtra("browser", true);
                    intent.putExtra("position", position);
                    Log.i("MainActivity: ", "Url is 2 : " + menuItems.get(position).getUrl());
                    startActivity(intent);
                } else if (menuItems.get(position).getType().equals("redirectweb")) {
                    Log.i("MainActivity: ", "inside Here 3");
                    String feedUrl = menuItems.get(position).getUrl();
                    if (GlobalState.isDealershipGroupVersion) {
                        if (feedUrl.contains("SERVERID")) {
                            feedUrl = feedUrl.replaceAll("SERVERID", "SERVERID&DFLID=" + dflId);
                            if (feedUrl.contains("SERVERID"))
                                feedUrl = feedUrl.replaceAll("SERVERID", GlobalState.serverId);
                        } else if (feedUrl.contains("?")) {
                            feedUrl = feedUrl + "&a=" + GlobalState.serverId + "&DFLID=" + dflId;
                        } else {
                            feedUrl = feedUrl + "?a=" + GlobalState.serverId + "&DFLID=" + dflId;
                        }
                    } else {
                        if (feedUrl.contains("SERVERID")) {
                            feedUrl = feedUrl.replaceAll("SERVERID", GlobalState.serverId);
                        } else if (feedUrl.contains("?"))
                            feedUrl = feedUrl + "&a=" + GlobalState.serverId;
                        else
                            feedUrl = feedUrl + "?a=" + GlobalState.serverId;
                    }
                    if (isPreferencePopulated) {
                        feedUrl = feedUrl + "&v=" + vinArray[selectedCarIndex];
                    } else
                        feedUrl = feedUrl + "&v=" + "";

                    feedUrl = feedUrl + "&u=" + getUniqueDeviceId() + "&k=" + Utils.getRegistrationId(MainActivity.this) + "&dn=" + getDeviceName();

                    Log.i("MainActivity: ", "inside 'help'-check, position: " + position);
                    Intent intent = new Intent(MainActivity.this, HelpWebViewActivity.class);
                    intent.putExtra("helpUrl", feedUrl);
                    Log.i("webViewTitle: ", menuItems.get(position).getTitle());
                    intent.putExtra("webViewTitle", menuItems.get(position).getTitle());
                    intent.putExtra("Tag", String.valueOf(menuItems.get(position).getTag()));
                    intent.putExtra("browser", true);
                    intent.putExtra("position", position);
                    Log.i("MainActivity: ", "Url is 3 : " + menuItems.get(position).getUrl());
                    startActivity(intent);
                } else if (menuItems.get(position).getType().equals("station")) {
                    Log.i("MainActivity: ", "inside Here 4");
                    String title = menuItems.get(position).getTitle();
                    Intent intent = new Intent(MainActivity.this, GasStationActivity.class);
                    String feedUrl = menuItems.get(position).getUrl();

                    if (feedUrl.contains("LATITUDE")) {
                        feedUrl = feedUrl.replace("LATITUDE", lat);
                        Log.i("lat", "lat:: " + lat);
                    }
                    if (feedUrl.contains("LONGITUDE")) {
                        feedUrl = feedUrl.replaceAll("LONGITUDE", lon);
                        Log.i("lon", "lon: " + lon);
                    }
                    if (feedUrl.contains("?")) {
                        feedUrl = feedUrl + "&a=" + GlobalState.serverId + "&u=" + getUniqueDeviceId() + "&k=" + Utils.getRegistrationId(MainActivity.this) + "&dn=" + getDeviceName();
                    } else {
                        feedUrl = feedUrl + "?a=" + GlobalState.serverId + "&u=" + getUniqueDeviceId() + "&k=" + Utils.getRegistrationId(MainActivity.this) + "&dn=" + getDeviceName();
                    }

                    intent.putExtra("feedUrl", feedUrl);
                    Log.i("Main Activity: ", "feedUrl: " + feedUrl);
                    intent.putExtra("positionFeed", position);
                    intent.putExtra("Lat", lat);
                    intent.putExtra("Lon", lon);
                    intent.putExtra("TitleName", title);

                    if (Utils.checkInternetConnection(MainActivity.this))
                        startActivity(intent);
                    else
                        showAlert("Internet not present! Please try again later.");

                } else if (menuItems.get(position).getType().equals("feed") ||
                        menuItems.get(position).getType().equals("rootfeed")) {
                    Log.i("MainActivity: ", "inside Here 5");

                    if (!pref.getBoolean("isPreferencePopulated", false)) {
                        Log.v("NewMainActivity", "Login New 1");
                        Intent intentCar = new Intent(MainActivity.this, SigninListviewActivity.class);
                        noCarRegisteredAlert(intentCar);
                        //BS startActivity(intentCar);

                    } else {

                        String title = menuItems.get(position).getTitle();
                        Intent intent = new Intent(MainActivity.this, SplashFeeds.class);
                        String feedUrl = menuItems.get(position).getUrl();

                        if (GlobalState.isDealershipGroupVersion) {
                            if (feedUrl.contains("SERVERID")) {
                                feedUrl = feedUrl.replaceAll("SERVERID", "SERVERID&DFLID=" + dflId);
                                if (feedUrl.contains("SERVERID"))
                                    feedUrl = feedUrl.replaceAll("SERVERID", GlobalState.serverId);
                            } else if (feedUrl.contains("?"))
                                feedUrl = feedUrl + "&a=" + GlobalState.serverId + "&DFLID=" + dflId;
                            else
                                feedUrl = feedUrl + "?a=" + GlobalState.serverId + "&DFLID=" + dflId;

                        } else {
                            if (feedUrl.contains("SERVERID")) {
                                feedUrl = feedUrl.replaceAll("SERVERID", GlobalState.serverId);
                            } else if (feedUrl.contains("?"))
                                feedUrl = feedUrl + "&a=" + GlobalState.serverId;
                            else
                                feedUrl = feedUrl + "?a=" + GlobalState.serverId;
                        }
                        if (isPreferencePopulated) {
                            feedUrl = feedUrl + "&v=" + vinArray[selectedCarIndex];
                        } else
                            feedUrl = feedUrl + "&v=" + "";

                        feedUrl = feedUrl + "&u=" + getUniqueDeviceId() + "&k=" + Utils.getRegistrationId(MainActivity.this) + "&dn=" + getDeviceName();
                        Log.i("deviceId", "deviceId: " + getUniqueDeviceId());
                        Log.i("deviceName: ", "deviceName: " + getDeviceName());

                        feedUrl = feedUrl.replaceAll(" ", "");
                        intent.putExtra("feedUrl", feedUrl);
                        Log.i("Main Activity: ", "feedUrl: " + feedUrl);
                        intent.putExtra("positionFeed", position);
                        intent.putExtra("TitleName", title);

                        if (Utils.checkInternetConnection(MainActivity.this))
                            startActivity(intent);
                        else
                            showAlert("Internet not present! Please try again later.");

                    }

                } else if (menuItems.get(position).getType().equalsIgnoreCase("morelocations")) {
                    Intent i;
                    if (GlobalState.isNewMenu) {
                        i = new Intent(MainActivity.this, NewMoreLocations.class);
                    } else {
                        i = new Intent(MainActivity.this, MoreLocations.class);
                    }

                    startActivity(i);
                } else if (menuItems.get(position).getType().equals("vehinfo")) {
                    String vehInfoUrl = menuItems.get(position).getUrl();

                    if (GlobalState.isDealershipGroupVersion) {

                        if (vehInfoUrl.contains("SERVERID")) {
                            vehInfoUrl = vehInfoUrl.replaceAll("SERVERID", "SERVERID&DFLID=" + dflId);
                            if (vehInfoUrl.contains("SERVERID"))
                                vehInfoUrl = vehInfoUrl.replaceAll("SERVERID", GlobalState.serverId);
                        } else if (vehInfoUrl.contains("?"))
                            vehInfoUrl = vehInfoUrl + "&a=" + GlobalState.serverId + "&DFLID=" + dflId;
                        else
                            vehInfoUrl = vehInfoUrl + "?a=" + GlobalState.serverId + "&DFLID=" + dflId;
                    } else {
                        if (vehInfoUrl.contains("SERVERID")) {
                            vehInfoUrl = vehInfoUrl.replaceAll("SERVERID", GlobalState.serverId);
                        } else if (vehInfoUrl.contains("?"))
                            vehInfoUrl = vehInfoUrl + "&a=" + GlobalState.serverId;
                        else
                            vehInfoUrl = vehInfoUrl + "?a=" + GlobalState.serverId;
                    }
                    if (isPreferencePopulated) {
                        vehInfoUrl = vehInfoUrl + "&v=" + vinArray[selectedCarIndex];

                    } else
                        vehInfoUrl = vehInfoUrl + "&v=" + "";

                    vehInfoUrl = vehInfoUrl + "&u=" + getUniqueDeviceId() + "&k=" + Utils.getRegistrationId(MainActivity.this) + "&dn=" + getDeviceName();

                    Intent intent = new Intent(MainActivity.this, HelpWebViewActivity.class);
                    intent.putExtra("webViewTitle", menuItems.get(position).getTitle());
                    intent.putExtra("helpUrl", vehInfoUrl);
                    intent.putExtra("position", position);
                    Log.i("MainActivity: ", "Url-vehinfo-Loyalty is: " + vehInfoUrl);
                    if (Utils.checkInternetConnection(MainActivity.this))
                        startActivity(intent);
                    else
                        showAlert("Internet not present! Please try again later.");
                } else if (menuItems.get(position).getTag() == AppConstants.MORE_OPTIONS) {
                    GlobalState.isFromMoreOptions = true;
                    Intent intent = new Intent(MainActivity.this, MainActivityListview.class);
                    intent.putExtra("tag", AppConstants.MORE_OPTIONS);
                    startActivity(intent);
                } else if (menuItems.get(position).getTag() == AppConstants.Menu_FuelSavingTips) {
                    Intent intent = new Intent(MainActivity.this, MainActivityListview.class);
                    intent.putExtra("tag", AppConstants.Menu_FuelSavingTips);
                    startActivity(intent);
                } else if (menuItems.get(position).getTag() == AppConstants.CONTACTS) {
                    Intent intent = new Intent(MainActivity.this, ContactUsListView.class);
                    intent.putExtra("tag", AppConstants.CONTACTS);
                    intent.putExtra("title", menuItems.get(position).getDisplay());


                    startActivity(intent);
                } else if (menuItems.get(position).getTag() == AppConstants.MY_CAR) {
                    if (pref.getBoolean("isPreferencePopulated", false)) {
                        Intent intent1 = new Intent(MainActivity.this, MyCarActivity.class);
                        intent1.putExtra("tag", AppConstants.MY_CAR);
                        startActivity(intent1);
                    } else {
                        Intent intent2 = new Intent(MainActivity.this, SigninListviewActivity.class);
                        noCarRegisteredAlert(intent2);
                    }


                } else if (menuItems.get(position).getTag() == AppConstants.SERVICE_DEPARTMENT) {
                    Intent intent = new Intent(MainActivity.this, MainActivityListview.class);
                    intent.putExtra("tag", AppConstants.SERVICE_DEPARTMENT);
                    startActivity(intent);
                } else if (menuItems.get(position).getTag() == AppConstants.MPG_CALCULATOR) {
                    Intent intent = new Intent(MainActivity.this, MPGCalculator.class);
                    intent.putExtra("tag", AppConstants.MPG_CALCULATOR);
                    startActivity(intent);
                } else if (menuItems.get(position).getTag() == AppConstants.NEW_MPG_CALCULATOR) {
                    Intent intent = new Intent(MainActivity.this, NewMPGCalculator.class);
                    intent.putExtra("tag", AppConstants.NEW_MPG_CALCULATOR);
                    startActivity(intent);
                } else if (menuItems.get(position).getTag() == AppConstants.CARPARK_TAG) {
                    Intent intent = new Intent(MainActivity.this, ParkingMapActivity.class);
                    intent.putExtra("tag", AppConstants.CARPARK_TAG);
                    startActivity(intent);
                } else if (menuItems.get(position).getTag() == AppConstants.QR_SCANNER_TAG) {
                    launchQRScanner();
                } else if (menuItems.get(position).getTag() == AppConstants.QR_SCANNER_FAVORITE_TAG) {
                    startActivity(new Intent(MainActivity.this, QRFavoritesListActivity.class));
                } else if (menuItems.get(position).getTag() == AppConstants.SETTINGS) {
                    Intent intent = new Intent(MainActivity.this, HelperActivity.class);
                    intent.putExtra("isCalledFrmMainActivity", true);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        // Adding Slider Items
        loadSliderData();

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

    private void makeCall() {
        // we need to check to see if we have a phone number on record.
        if (MoreLocations.flag != null) {
            if (MoreLocations.flag.equalsIgnoreCase("temp")) {
                Intent call = new Intent(Intent.ACTION_DIAL);
                call.setData(Uri.parse("tel:" + GlobalState.tempdealershipServicePhone));
                startActivity(call);
            }
        } else if (GlobalState.dealershipPhone != null) {
            Intent call = new Intent(Intent.ACTION_DIAL);
            call.setData(Uri.parse("tel:" + GlobalState.dealershipServicePhone));
            startActivity(call);

        } else
            Toast.makeText(this, "No telephone number was provided, press visit our website for more contact options.",
                    Toast.LENGTH_LONG).show();
    }

    public void gotoWeb() {
        Intent intent = new Intent(MainActivity.this, HelpWebViewActivity.class);

        String feedUrl = centreItem.getUrl();
        if (feedUrl.contains("www.mobileappscm.com")) {
            if (GlobalState.isDealershipGroupVersion) {
                if (feedUrl.contains("SERVERID")) {
                    feedUrl = feedUrl.replaceAll("SERVERID", "SERVERID&DFLID=" + dflId);
                    if (feedUrl.contains("SERVERID"))
                        feedUrl = feedUrl.replaceAll("SERVERID", GlobalState.serverId);
                } else if (feedUrl.contains("?")) {
                    feedUrl = feedUrl + "&a=" + GlobalState.serverId + "&DFLID=" + dflId;
                } else {
                    feedUrl = feedUrl + "?a=" + GlobalState.serverId + "&DFLID=" + dflId;
                }

            } else {

                if (feedUrl.contains("SERVERID")) {
                    feedUrl = feedUrl.replaceAll("SERVERID", "SERVERID&DFLID=" + GlobalState.dealershipId);
                    if (feedUrl.contains("SERVERID"))
                        feedUrl = feedUrl.replaceAll("SERVERID", GlobalState.serverId);
                } else if (feedUrl.contains("?")) {
                    feedUrl = feedUrl + "&a=" + GlobalState.serverId + "&DFLID=" + GlobalState.dealershipId;
                } else {
                    feedUrl = feedUrl + "?a=" + GlobalState.serverId + "&DFLID=" + GlobalState.dealershipId;
                }


               /* if (feedUrl.contains("SERVERID")) {
                    feedUrl = feedUrl.replaceAll("SERVERID", GlobalState.serverId);
                } else if (feedUrl.contains("?")) {
                    feedUrl = feedUrl + "&a=" + GlobalState.serverId;
                } else {
                    feedUrl = feedUrl + "?a=" + GlobalState.serverId;
                }*/

            }

            feedUrl = feedUrl + "&u=" + getUniqueDeviceId() + "&k=" + Utils.getRegistrationId(MainActivity.this) + "&dn=" + getDeviceName();
        }

        intent.putExtra("helpUrl", feedUrl);
        intent.putExtra("webViewTitle", centreItem.getTitle());
        intent.putExtra("Tag", String.valueOf(centreItem.getTag()));
        intent.putExtra("browser", centreItem.isBrowser());
        intent.putExtra("position", 0);
        Log.i("MainActivity: ", "Url is 4 : " + centreItem.getUrl());
        startActivity(intent);
    }

    private void vehiclSpecials() {
       /*BS ImageView imageView = (ImageView) findViewById(R.id.imagevehicle);
        Log.v("Image Name","Name : "+centreItem.getShowIconType());
        imageView.setImageBitmap(Utils.getBitmapFromAsset(centreItem.getShowIconType(),this));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                gotoWeb();
            }
        });*/
    }

    private void showEmail() {
        //JSONObject headerObj =
    }


    class BannerAdapter extends FragmentStatePagerAdapter {

        List<Items> list;

        public BannerAdapter(FragmentManager fm) {
            super(fm);

            list = new ArrayList<Items>();
        }

        public void updateList(List<Items> list_) {
            this.list.clear();
            this.list.addAll(list_);
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int arg0) {
            BannerFragment frag = new BannerFragment();
            Bundle b = new Bundle();
            b.putSerializable("banner", list.get(arg0));
            frag.setArguments(b);
            return frag;
        }

        @Override
        public int getCount() {
            return list.size();
        }

    }

    private void noCarRegisteredAlert(final Intent intent) {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setMessage("No Car Registered. Do you want to continue?");
        alert.setPositiveButton("Yes. Proceed", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                startActivity(intent);

            }
        });

        alert.setNegativeButton("No. Stay Here", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.show();
    }


    private void bottomTicker() {


        //InputStream inpStream = null;
        String foreG_color = DEFAULT_FG_COLOR;
        String backG_color = DEFAULT_BG_COLOR;

        try {
            JSONObject jsonBT = readAssetsJson("appspecific.json");
            foreG_color = jsonBT.getString("BOTTOM_TICKER_FG");
            backG_color = jsonBT.getString("BOTTOM_TICKER_BG");
            vpItem = (ViewPager) findViewById(R.id.vgView);
            vpItem.setCurrentItem(0);
            vpItem.setOnPageChangeListener(this);

            vbtAdapter = new ViewPagerBottomTickerAdapter(MainActivity.this, bottomTickerItems, foreG_color, backG_color);
            vpItem.setOffscreenPageLimit(bottomTickerItems.size() - 1);
            vpItem.setAdapter(vbtAdapter);


            /*inpStream = this.getAssets().open("appspecific.json");
            int size = inpStream.available();
            byte[] buffer = new byte[size];
            inpStream.read(buffer);
            inpStream.close();
            String bufferString = new String(buffer);

            JSONObject json = new JSONObject(bufferString);

            foreG_color = json.getString("BOTTOM_TICKER_FG");
            backG_color = json.getString("BOTTOM_TICKER_FG");*/
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    private void loadSliderData() {

        try {

            JSONObject appSpecJson = readAssetsJson("appspecific.json");
            if (!appSpecJson.getString("SHOW_SIDE_MENU").equalsIgnoreCase("true")) {
                hamburger.setVisibility(View.GONE);
                return;
            }


            JSONObject resJson = readAssetsJson(AppConstants.JSON_FILE_PATH + "Menu_LeftNav.json");
            JSONArray result = resJson.getJSONArray("Items");

            for (int i = 0; i < result.length(); i++) {
                JSONObject c = result.getJSONObject(i);
                Items items = new Items();
                if (c.has("Title"))
                    items.setTitle(c.getString("Title"));

                if (c.has("Subtitle"))
                    items.setSubtitle(c.getString("Subtitle"));

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

                if (c.has("ShowIconType")) {
                    items.setShowIconType(AppConstants.NEW_MENU_FILE_PATH + c.getString("ShowIconType"));
                    Log.v("FileNames", "File " + ":" + AppConstants.NEW_MENU_FILE_PATH + c.getString("ShowIconType"));

                } else {
                    items.setShowIconType(" ");
                }

                leftMenuItems.add(items);

            }//for


            String[] imagesListSlider = new String[leftMenuItems.size()];

            for (int i = 0; i < leftMenuItems.size(); i++)
                imagesListSlider[i] = leftMenuItems.get(i).getShowIconType();

            gridViewSlider = (GridView) findViewById(R.id.gridSlider);
            final NewMyArrayGridAdapter adapterSlider = new NewMyArrayGridAdapter(this, imagesListSlider);
            gridViewSlider.setAdapter(adapterSlider);


            hamburger.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    leftSliderLay.setVisibility(View.VISIBLE);
                }
            });

            leftSliderLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    leftSliderLay.setVisibility(View.GONE);
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


    }


    private JSONObject readAssetsJson(String jsonFileName) {
        InputStream inpStream = null;
        JSONObject json = null;
        try {
            inpStream = this.getAssets().open(jsonFileName);
            int size = inpStream.available();
            byte[] buffer = new byte[size];
            inpStream.read(buffer);
            inpStream.close();
            String bufferString = new String(buffer);

            json = new JSONObject(bufferString);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void analytics() {
        ApplicationVariables application = (ApplicationVariables) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(getClass().getSimpleName());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        //AnalyticsApplication application = (AnalyticsApplication) getApplication();
        //mTracker = application.getDefaultTracker();
    }

    private void registrationRemainder() {
        if (!pref.getBoolean("isPreferencePopulated", false)) {
            if (!(mypref.getLong("app_install_time", 0) == 0)) {
                long savedMillis = mypref.getLong("app_install_time", 0);
                long currentMillis = System.currentTimeMillis();
                if ((currentMillis - savedMillis) > GlobalState.REGISTRATION_REMAINDER_TIMELAP) {
                    final Intent intent = new Intent(MainActivity.this, SigninListviewActivity.class);
                    noCarRegisteredAlert(intent);
                    setCurrentRegisterRamainderTime();
                }
            } else {
                setCurrentRegisterRamainderTime();
            }
        }
    }

    private void setCurrentRegisterRamainderTime() {
        editor = mypref.edit();
        editor.putLong("app_install_time", System.currentTimeMillis());
        editor.commit();
    }




    private class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
        }

        protected String doInBackground(Void... urls) {
            // Do some validation here

            String postUrl = "http://www.mobileappscm.com/custom/mobileappscm/ctl/postDeviceToken.aspx?a=SERVERID";
            postUrl = postUrl.replaceAll("SERVERID", GlobalState.serverId)+"&DFLID="+dflId + "&u=" + getUniqueDeviceId() + "&k=" + Utils.getRegistrationId(MainActivity.this) + "&dn=" + getDeviceName() + "&v=" + vinArray[selectedCarIndex];

            Log.v("Main Activity", "FFFFFFFFFFFFF : "+postUrl);

            try {
                URL url = new URL(postUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            Log.i("INFO FFFFFFFFFFFFF ", response);
        }
    }







}