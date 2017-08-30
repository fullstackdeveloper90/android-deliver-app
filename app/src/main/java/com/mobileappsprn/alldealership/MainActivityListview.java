package com.mobileappsprn.alldealership;

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
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.flurry.android.FlurryAgent;
import com.mobileappsprn.alldealership.adapters.NewMyArrayListAdapter;
import com.mobileappsprn.alldealership.contactus.ContactUsListView;
import com.mobileappsprn.alldealership.entities.AppConstants;
import com.mobileappsprn.alldealership.entities.Items;
import com.mobileappsprn.alldealership.utilities.GPSTracker;
import com.mobileappsprn.alldealership.utilities.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class MainActivityListview extends Activity{

    private final String SERVICE_PROVIDER = "Approved Service Providers";
    private final String FUEL_TIPS = "Fuel Saving Tips";
    private final String MORE_OPTIONS = "More Options";
    private final String PREFERRED_DEALER = "Preferred Dealer";


        ListView listView;
        ArrayList<Items> menuItems = new ArrayList<Items>();
        int tagValue;
        SharedPreferences pref, mypref;
        TextView tv_title;

        String[] vinArray;
        String[] carDetailsArrayToLoad;
        Map<String, String> carDetailsHMap;

        int selectedCarIndex;

        SharedPreferences prefKeyValues;
        SharedPreferences selectedCarSharedPref;
        private GPSTracker mGPS;
        double currentLatitude, currentLongitude;
        String lat, lon;

        boolean isSelectedCarPrefPopulated;
        boolean isPreferencePopulated;

        String dflId;

        private static final int ZBAR_SCANNER_REQUEST = 0;
        private static final int ZBAR_QR_SCANNER_REQUEST = 1;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.v("FLOW", "CAME HERE : MainActivityListview");
        }




//        public void launchScanner(View v) {
//            if (isCameraAvailable()) {
//                Intent intent = new Intent(this, ZBarScannerActivity.class);
//                startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
//            } else {
//                Toast.makeText(this, "Rear Facing Camera Unavailable", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//        public void launchQRScanner() {
//            if (isCameraAvailable()) {
//                Intent intent = new Intent(this, ZBarScannerActivity.class);
//                intent.putExtra(ZBarConstants.SCAN_MODES, new int[]{Symbol.QRCODE});
//                startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
//            } else {
//                Toast.makeText(this, "Rear Facing Camera Unavailable", Toast.LENGTH_SHORT).show();
//            }
//        }

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
//                intent.putExtra(ZBarConstants.SCAN_MODES, new int[]{Symbol.QRCODE});
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
                                + "&DFLID=" + dflId + "&u=&type=&d=" + getUniqueDeviceId()  + "&k=" + Utils.getRegistrationId(MainActivityListview.this)  + "&dn=" + getDeviceName();

                        Log.i("QR SCANNER SERVER URL ", url);

//                        Intent intent = new Intent(NewMainActivityListview.this, QRScannerWebViewActivity.class);
//                        intent.putExtra("qrScanUrl", data.getStringExtra(ZBarConstants.SCAN_RESULT));
//                        intent.putExtra("serverUrl", url);
//                        startActivity(intent);

                    } else if (resultCode == RESULT_CANCELED && data != null) {
//                        String error = data.getStringExtra(ZBarConstants.ERROR_INFO);
//                        if (!TextUtils.isEmpty(error)) {
//                            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
//                        }
                    }
                    break;
            }
        }

        @Override
        public void onResume() {
            super.onResume();

            Log.v("New Main Activity LV "," Here Onresume");

            menuItems.removeAll(menuItems);
            initialise();

        }

        public String getUniqueDeviceId() {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) this
                        .getSystemService(Context.TELEPHONY_SERVICE);
                String deviceId = telephonyManager.getDeviceId();
                if (telephonyManager.getDeviceId() == null) {
                    String android_id = Settings.Secure.getString(this.getContentResolver(),
                            Settings.Secure.ANDROID_ID);
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
            setContentView(R.layout.new_activity_main_listview);





            pref = getApplicationContext().getSharedPreferences("ShockleyPref", 0);

            mypref = getApplicationContext().getSharedPreferences("HomePrefs", Activity.MODE_PRIVATE);
            dflId = mypref.getString("dfl_id", "");

            int position = mypref.getInt("Itemposition", 0);

            ImageView image = (ImageView) findViewById(R.id.logo);
            tv_title = (TextView) findViewById(R.id.titleHeader);
            String img_name = GlobalState.homeMenuItems.get(position).getLogoURL();
            Log.v("Image Name","Name : "+img_name);


           AssetManager assetManager = getAssets();
            try {
                Bitmap bitmap = null;
                if (Arrays.asList(assetManager.list("MFG-Icons")).contains(img_name))
                    bitmap = Utils.getBitmapFromAsset(AppConstants.MFG_ICONS_FILE_PATH + img_name, this);

                else if (Arrays.asList(assetManager.list("App-Specific-Icons")).contains(img_name)) {
                    bitmap = Utils.getBitmapFromAsset(AppConstants.APP_SPECIFIC_ICONS_FILE_PATH + img_name, this);
                }
                else if (Arrays.asList(assetManager.list("MenuIcons2")).contains(img_name)) {
                    bitmap = Utils.getBitmapFromAsset(AppConstants.MENU_ICONS2_FILE_PATH + img_name, this);
                }
                image.setImageBitmap(bitmap);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            if(GlobalState.isVariantTcc){
                image.setImageDrawable(getResources().getDrawable(R.drawable.logo_hd));
            }


            Intent intent = getIntent();
            tagValue = intent.getIntExtra("tag", 0);


            mGPS = new GPSTracker(MainActivityListview.this);
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

            Log.v("FLOW", "CAME HERE : MainActivityListview  TagValue : "+tagValue);

            if (tagValue == AppConstants.MORE_OPTIONS) {
                JSONfileName += "Menu_More.json";
                tv_title.setText(MORE_OPTIONS);
            }

            else if (tagValue == AppConstants.MY_CAR)
                JSONfileName += "Menu_MyCar.json";

            else if (tagValue == AppConstants.SERVICE_DEPARTMENT) {
                JSONfileName += "Menu_ServiceDepartment.json";

                if(GlobalState.isVariantTcc){
                    tv_title.setText(SERVICE_PROVIDER);
                } else {
                    tv_title.setText(PREFERRED_DEALER);
                }



            }
            else if (tagValue == AppConstants.Menu_FuelSavingTips) {
                JSONfileName += "Menu_FuelSavingTips.json";
                tv_title.setText(FUEL_TIPS);
            }
            else
                JSONfileName += "Menu_Main.json";

            try {

                inpStream = this.getAssets().open(JSONfileName);

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
                        items.setShowIconType(AppConstants.MENU_ICONS2_FILE_PATH + c.getString("ShowIconType"));

                    } else {
                        items.setShowIconType(" ");
                    }

                   menuItems.add(items);

                }//for
                GlobalState.menuItems = menuItems;

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
                    Log.i("carDetailsHMap-key-MainActivity: ", "" + keyString);
                }

                int p = 0;

                for (String keyString : carDetailsHMap.keySet()) {
                    String carDetailsStringToLoad = carDetailsHMap.get(keyString);
                    carDetailsArrayToLoad = new String[12];
                    carDetailsArrayToLoad = carDetailsStringToLoad.split(",");

                    //Log.i("uploadedImage: ", "uploaded: "+carDetailsArrayToLoad[2]);

                    try {
                        vinArray[p] = carDetailsArrayToLoad[1];
                        Log.i("carDetailsArrayToLoad.length: ", "" + carDetailsArrayToLoad.length);

                        Log.i("vinArray[i]: ", "" + vinArray[p]);
                        p++;

                    } catch (Exception e) {
                        Log.i("ArrayOutOfBound Exception", "" + e.getMessage());
                    }
                }

                selectedCarIndex = selectedCarSharedPref.getInt("selectedCar", 0);
                Log.i("MainActivity: ", "selectedCarIndex: " + selectedCarIndex);
                Log.i("MainActivity: ", "vinSelected: " + vinArray[selectedCarIndex]);

            }

        /*String[] titles = new String[menuItems.size()];

        for(int i = 0; i< menuItems.size(); i++)
            titles[i] = menuItems.get(i).getTitle();

        String[] subtitles = new String[menuItems.size()];

        for(int i = 0; i< menuItems.size(); i++)
            subtitles[i] = menuItems.get(i).getSubtitle();*/


            listView = (ListView) findViewById(R.id.list);

          //  menuItems=GlobalState.homeMenuItems;
            String[] titles = new String[menuItems.size()];

            for(int i = 0; i< menuItems.size(); i++)
                titles[i] = menuItems.get(i).getTitle();

            final NewMyArrayListAdapter adapter = new NewMyArrayListAdapter(this, titles);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    if (menuItems.get(position).getType().equals("help") ||
                            menuItems.get(position).getType().equals("redirect")) {

                        Log.i("MainActivity: ", "inside 'help'-check, position: " + position);
                        Intent intent = new Intent(MainActivityListview.this, HelpWebViewActivity.class);

                        String feedUrl = menuItems.get(position).getUrl();
                        if (feedUrl.contains("www.mobileappscm.com")) {
                            if (GlobalState.isDealershipGroupVersion)
                            {
                                if (feedUrl.contains("SERVERID"))
                                {
                                    feedUrl = feedUrl.replaceAll("SERVERID", "SERVERID&DFLID=" + dflId);
                                    if (feedUrl.contains("SERVERID"))
                                        feedUrl = feedUrl.replaceAll("SERVERID", GlobalState.serverId);
                                }
                                else if (feedUrl.contains("?"))
                                {
                                    feedUrl = feedUrl + "&a=" + GlobalState.serverId + "&DFLID=" + dflId;
                                }
                                else
                                {
                                    feedUrl = feedUrl + "?a=" + GlobalState.serverId + "&DFLID=" + dflId;
                                }

                            } else {

                                    if (feedUrl.contains("SERVERID"))
                                    {
                                        feedUrl = feedUrl.replaceAll("SERVERID", "SERVERID&DFLID=" + GlobalState.dealershipId);
                                        if (feedUrl.contains("SERVERID"))
                                            feedUrl = feedUrl.replaceAll("SERVERID", GlobalState.serverId);
                                    }
                                    else if (feedUrl.contains("?"))
                                    {
                                        feedUrl = feedUrl + "&a=" + GlobalState.serverId + "&DFLID=" + GlobalState.dealershipId;
                                    }
                                    else
                                    {
                                        feedUrl = feedUrl + "?a=" + GlobalState.serverId + "&DFLID=" + GlobalState.dealershipId;
                                    }


                                /*
                                if (feedUrl.contains("SERVERID")) {
                                    feedUrl = feedUrl.replaceAll("SERVERID", GlobalState.serverId);
                                } else if (feedUrl.contains("?")) {
                                    feedUrl = feedUrl + "&a=" + GlobalState.serverId;
                                } else {
                                    feedUrl = feedUrl + "?a=" + GlobalState.serverId;
                                }*/

                            }

                            feedUrl = feedUrl + "&u=" + getUniqueDeviceId() + "&d=" + getUniqueDeviceId() + "&k=" + Utils.getRegistrationId(MainActivityListview.this)+ "&dn=" + getDeviceName();
                        }

                        intent.putExtra("helpUrl", feedUrl);
                        Log.i("webViewTitle: ", menuItems.get(position).getTitle());
                        intent.putExtra("webViewTitle", menuItems.get(position).getTitle());
                        intent.putExtra("Tag", String.valueOf(menuItems.get(position).getTag()));
                        intent.putExtra("browser", menuItems.get(position).isBrowser());
                        intent.putExtra("position", position);
                        Log.i("MainActivity: ", "Url is: " + menuItems.get(position).getUrl());
                        startActivity(intent);


                    } else if (menuItems.get(position).getType().equals("callservice")) {
                        makeCall();
                    } else if (menuItems.get(position).getType().equals("call")) {
                        new Utils().makeCall(MainActivityListview.this);
                    } else if (menuItems.get(position).getType().equals("newweb")) {

                        Log.i("MainActivity: ", "inside 'help'-check, position: " + position);
                        Intent intent = new Intent(MainActivityListview.this, HelpWebViewActivity.class);

                        String feedUrl = menuItems.get(position).getUrl();
                        if (!feedUrl.contains("www.mobileappscm.com")) {
                            if (GlobalState.isDealershipGroupVersion)
                            {
                                if (feedUrl.contains("SERVERID"))
                                {
                                    feedUrl = feedUrl.replaceAll("SERVERID", "SERVERID&DFLID=" + dflId);
                                    if (feedUrl.contains("SERVERID"))
                                        feedUrl = feedUrl.replaceAll("SERVERID", GlobalState.serverId);
                                }
                                else if (feedUrl.contains("?"))
                                {
                                    feedUrl = feedUrl + "&a=" + GlobalState.serverId + "&DFLID=" + dflId;
                                }
                                else
                                {
                                    feedUrl = feedUrl + "?a=" + GlobalState.serverId + "&DFLID=" + dflId;
                                }

                            }
                            else
                            {
                                /*if (feedUrl.contains("SERVERID"))
                                {
                                    feedUrl = feedUrl.replaceAll("SERVERID", GlobalState.serverId);
                                }
                                else if (feedUrl.contains("?"))

                                    feedUrl = feedUrl + "&a=" + GlobalState.serverId;
                                else
                                    feedUrl = feedUrl + "?a=" + GlobalState.serverId;*/
                                if (feedUrl.contains("SERVERID"))
                                {
                                    feedUrl = feedUrl.replaceAll("SERVERID", "SERVERID&DFLID=" + GlobalState.dealershipId);
                                    if (feedUrl.contains("SERVERID"))
                                        feedUrl = feedUrl.replaceAll("SERVERID", GlobalState.serverId);
                                }
                                else if (feedUrl.contains("?"))
                                {
                                    feedUrl = feedUrl + "&a=" + GlobalState.serverId + "&DFLID=" + GlobalState.dealershipId;
                                }
                                else
                                {
                                    feedUrl = feedUrl + "?a=" + GlobalState.serverId + "&DFLID=" + GlobalState.dealershipId;
                                }
                            }

                            feedUrl = feedUrl + "&u=" + getUniqueDeviceId() + "&d=" + getUniqueDeviceId()+ "&k=" + Utils.getRegistrationId(MainActivityListview.this) + "&dn=" + getDeviceName();
                        }
                        intent.putExtra("helpUrl", feedUrl);
                        Log.i("webViewTitle: ", menuItems.get(position).getTitle());
                        intent.putExtra("webViewTitle", menuItems.get(position).getTitle());
                        intent.putExtra("Tag", String.valueOf(menuItems.get(position).getTag()));
                        intent.putExtra("browser", true);
                        intent.putExtra("position", position);
                        Log.i("MainActivity: ", "Url is: " + menuItems.get(position).getUrl());
                        startActivity(intent);
                    } else if (menuItems.get(position).getType().equals("redirectweb")) {
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
                            /*if (feedUrl.contains("SERVERID"))
                             {
                                feedUrl = feedUrl.replaceAll("SERVERID", GlobalState.serverId);
                            }
                            else if (feedUrl.contains("?"))
                                feedUrl = feedUrl + "&a=" + GlobalState.serverId;
                            else
                                feedUrl = feedUrl + "?a=" + GlobalState.serverId;*/
                            if (feedUrl.contains("SERVERID"))
                            {
                                feedUrl = feedUrl.replaceAll("SERVERID", "SERVERID&DFLID=" + GlobalState.dealershipId);
                                if (feedUrl.contains("SERVERID"))
                                    feedUrl = feedUrl.replaceAll("SERVERID", GlobalState.serverId);
                            }
                            else if (feedUrl.contains("?"))
                            {
                                feedUrl = feedUrl + "&a=" + GlobalState.serverId + "&DFLID=" + GlobalState.dealershipId;
                            }
                            else
                            {
                                feedUrl = feedUrl + "?a=" + GlobalState.serverId + "&DFLID=" + GlobalState.dealershipId;
                            }
                        }
                        if (isPreferencePopulated) {
                            feedUrl = feedUrl + "&v=" + vinArray[selectedCarIndex];
                        } else
                            feedUrl = feedUrl + "&v=" + "";

                        feedUrl = feedUrl + "&u=" + getUniqueDeviceId() + "&d=" + getUniqueDeviceId() + "&k=" + Utils.getRegistrationId(MainActivityListview.this)+ "&dn=" + getDeviceName();

                        Log.i("MainActivity: ", "inside 'help'-check, position: " + position);
                        Intent intent = new Intent(MainActivityListview.this, HelpWebViewActivity.class);
                        intent.putExtra("helpUrl", feedUrl);
                        Log.i("webViewTitle: ", menuItems.get(position).getTitle());
                        intent.putExtra("webViewTitle", menuItems.get(position).getTitle());
                        intent.putExtra("Tag", String.valueOf(menuItems.get(position).getTag()));
                        intent.putExtra("browser", true);
                        intent.putExtra("position", position);
                        Log.i("MainActivity: ", "Url is: " + menuItems.get(position).getUrl());
                        startActivity(intent);
                    } else if (menuItems.get(position).getType().equals("station"))
                    {
                        String title = menuItems.get(position).getTitle();
                        Intent intent = new Intent(MainActivityListview.this, GasStationActivity.class);
                        String feedUrl = menuItems.get(position).getUrl();

                        feedUrl = feedUrl.replace("&d=DEVICEID&dn=DEVICENAME", "");

                        if (feedUrl.contains("LATITUDE")) {
                            feedUrl = feedUrl.replace("LATITUDE", lat);
                            Log.i("lat", "lat:: " + lat);
                        }
                        if (feedUrl.contains("LONGITUDE")) {
                            feedUrl = feedUrl.replaceAll("LONGITUDE", lon);
                            Log.i("lon", "lon: " + lon);
                        }
                        if (feedUrl.contains("?")) {
                            feedUrl = feedUrl + "&a=" + GlobalState.serverId + "&u=" + getUniqueDeviceId() + "&d=" + getUniqueDeviceId() + "&k=" + Utils.getRegistrationId(MainActivityListview.this)+ "&dn=" + getDeviceName();
                        } else {
                            feedUrl = feedUrl + "?a=" + GlobalState.serverId + "&u=" + getUniqueDeviceId() + "&d=" + getUniqueDeviceId()+ "&k=" + Utils.getRegistrationId(MainActivityListview.this) + "&dn=" + getDeviceName();
                        }

                        intent.putExtra("feedUrl", feedUrl);
                        Log.i("Main Activity: ", "feedUrl: " + feedUrl);
                        intent.putExtra("positionFeed", position);
                        intent.putExtra("Lat", lat);
                        intent.putExtra("Lon", lon);
                        intent.putExtra("TitleName", title);

                        if (Utils.checkInternetConnection(MainActivityListview.this))
                            startActivity(intent);
                        else
                            showAlert("Internet not present! Please try again later.");

                    } else if (menuItems.get(position).getType().equals("feed") ||
                            menuItems.get(position).getType().equals("rootfeed")) {

                        String title = menuItems.get(position).getTitle();
                        //Intent intent = new Intent(NewMainActivityListview.this, SplashFeeds.class);
                        Intent intent = new Intent(MainActivityListview.this, DrillDownRoot.class);
                        String feedUrl = menuItems.get(position).getUrl();

                        if(GlobalState.isVariantTcc){
                            dflId ="TCC"; //Remove for others
                            GlobalState.dealershipId = "TCC"; //Remove for others
                        }

                        if (GlobalState.isDealershipGroupVersion) {
                            if (feedUrl.contains("SERVERID"))
                            {
                                feedUrl = feedUrl.replaceAll("SERVERID", "SERVERID&DFLID=" + dflId);
                                if (feedUrl.contains("SERVERID"))
                                    feedUrl = feedUrl.replaceAll("SERVERID", GlobalState.serverId);
                            }
                            else if (feedUrl.contains("?"))
                                feedUrl = feedUrl + "&a=" + GlobalState.serverId + "&DFLID=" + dflId;
                            else
                                feedUrl = feedUrl + "?a=" + GlobalState.serverId + "&DFLID=" + dflId;

                        } else {
                            /*if (feedUrl.contains("SERVERID"))
                            {
                                feedUrl = feedUrl.replaceAll("SERVERID", GlobalState.serverId);
                            }
                            else if (feedUrl.contains("?"))
                                feedUrl = feedUrl + "&a=" + GlobalState.serverId;
                            else
                                feedUrl = feedUrl + "?a=" + GlobalState.serverId;*/
                            if (feedUrl.contains("SERVERID"))
                            {
                                feedUrl = feedUrl.replaceAll("SERVERID", "SERVERID&DFLID=" + GlobalState.dealershipId);
                                if (feedUrl.contains("SERVERID"))
                                    feedUrl = feedUrl.replaceAll("SERVERID", GlobalState.serverId);
                            }
                            else if (feedUrl.contains("?"))
                            {
                                feedUrl = feedUrl + "&a=" + GlobalState.serverId + "&DFLID=" + GlobalState.dealershipId;
                            }
                            else
                            {
                                feedUrl = feedUrl + "?a=" + GlobalState.serverId + "&DFLID=" + GlobalState.dealershipId;
                            }
                        }
                        if (isPreferencePopulated) {
                            feedUrl = feedUrl + "&v=" + vinArray[selectedCarIndex];
                        } else
                            feedUrl = feedUrl + "&v=" + "";

                        feedUrl = feedUrl + "&u=" + getUniqueDeviceId() + "&d=" + getUniqueDeviceId()+ "&k=" + Utils.getRegistrationId(MainActivityListview.this) + "&dn=" + getDeviceName();
                        Log.i("deviceId", "deviceId: " + getUniqueDeviceId());
                        Log.i("deviceName: ", "deviceName: " + getDeviceName());

                        feedUrl = feedUrl.replaceAll(" ", "");
                        intent.putExtra("feedUrl1", feedUrl);
                        Log.i("Main Activity: ", "feedUrl: " + feedUrl);
                        intent.putExtra("positionFeed1", position);
                        intent.putExtra("TitleName", title);

                        // The below 3 fields added as removed "SplashFeeds class"

                        intent.putExtra("Title", title);
                        intent.putExtra("tagValue1", intent.getIntExtra("tagValue", -1));
                        intent.putExtra("vin1", intent.getStringExtra("vin"));

                        if (Utils.checkInternetConnection(MainActivityListview.this))
                            startActivity(intent);
                        else
                            showAlert("Internet not present! Please try again later.");

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

                        vehInfoUrl = vehInfoUrl + "&u=" + getUniqueDeviceId() + "&d=" + getUniqueDeviceId()+ "&k=" + Utils.getRegistrationId(MainActivityListview.this) + "&dn=" + getDeviceName();

                        Intent intent = new Intent(MainActivityListview.this, HelpWebViewActivity.class);
                        intent.putExtra("webViewTitle", menuItems.get(position).getTitle());
                        intent.putExtra("helpUrl", vehInfoUrl);
                        intent.putExtra("position", position);
                        Log.i("MainActivity: ", "Url-vehinfo-Loyalty is: " + vehInfoUrl);
                        if (Utils.checkInternetConnection(MainActivityListview.this))
                            startActivity(intent);
                        else
                            showAlert("Internet not present! Please try again later.");
                    } else if (menuItems.get(position).getTag() == AppConstants.MORE_OPTIONS) {
                        Intent intent = new Intent(MainActivityListview.this, MainActivityListview.class);
                        intent.putExtra("tag", AppConstants.MORE_OPTIONS);
                        startActivity(intent);
                    } else if (menuItems.get(position).getTag() == AppConstants.CONTACTS) {
                        Intent intent = new Intent(MainActivityListview.this, ContactUsListView.class);
                        intent.putExtra("tag", AppConstants.CONTACTS);
                        intent.putExtra("title", menuItems.get(position).getDisplay());
                        startActivity(intent);
                    } else if (menuItems.get(position).getTag() == AppConstants.MY_CAR) {
                        if (pref.getBoolean("isPreferencePopulated", false)) {
                            Intent intent1 = new Intent(MainActivityListview.this, MyCarActivity.class);
                            intent1.putExtra("tag", AppConstants.MY_CAR);
                            startActivity(intent1);
                        } else {
                            Intent intent2 = new Intent(MainActivityListview.this, SigninListviewActivity.class);
                            startActivity(intent2);
                        }
                    } else if (menuItems.get(position).getTag() == AppConstants.SERVICE_DEPARTMENT) {
                        Intent intent = new Intent(MainActivityListview.this, MainActivity.class);
                        intent.putExtra("tag", AppConstants.SERVICE_DEPARTMENT);
                        startActivity(intent);
                    } else if (menuItems.get(position).getTag() == AppConstants.MPG_CALCULATOR) {
                        Intent intent = new Intent(MainActivityListview.this, MPGCalculator.class);
                        intent.putExtra("tag", AppConstants.MPG_CALCULATOR);
                        startActivity(intent);
                    } else if (menuItems.get(position).getTag() == AppConstants.NEW_MPG_CALCULATOR) {
                        Intent intent = new Intent(MainActivityListview.this, NewMPGCalculator.class);
                        intent.putExtra("tag", AppConstants.NEW_MPG_CALCULATOR);
                        startActivity(intent);
                    } else if (menuItems.get(position).getTag() == AppConstants.CARPARK_TAG) {
                        Intent intent = new Intent(MainActivityListview.this, ParkingMapActivity.class);
                        intent.putExtra("tag", AppConstants.CARPARK_TAG);
                        startActivity(intent);
                    } else if (menuItems.get(position).getTag() == AppConstants.QR_SCANNER_TAG) {
                        launchQRScanner();
                    } else if (menuItems.get(position).getTag() == AppConstants.QR_SCANNER_FAVORITE_TAG) {
                        startActivity(new Intent(MainActivityListview.this, QRFavoritesListActivity.class));
                    } else if (menuItems.get(position).getTag() == AppConstants.SETTINGS) {
                        Intent intent = new Intent(MainActivityListview.this, HelperActivity.class);
                        intent.putExtra("isCalledFrmMainActivity", true);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(MainActivityListview.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            });
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
    }