package com.mobileappsprn.alldealership;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mobileappsprn.alldealership.adapters.NewMyArrayListAdapter;
import com.mobileappsprn.alldealership.entities.AppConstants;
import com.mobileappsprn.alldealership.entities.Items;
import com.mobileappsprn.alldealership.utilities.ApplicationVariables;
import com.mobileappsprn.alldealership.utilities.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by T on 13-Apr-15.
 */
public class MyCarActivity extends FragmentActivity implements View.OnClickListener {

    ListView listView;
    ArrayList<Items> menuItems = new ArrayList<Items>();
    int tagValue;

    public LinearLayout linearLayout;

    ViewPager mViewPager;
    SectionsPagerAdapter mSectionsPagerAdapter;
    static int currentPage;
    Bundle global;

    String[] carDetailsArrayToLoad;

    static int imageCount = 0;

    SharedPreferences pref;

    Map<String, String> carDetailsHMap;

    SharedPreferences prefKeyValues;
    SharedPreferences mypref;

    SharedPreferences selectedCarSharedPref; // for Pager-selection
    static SharedPreferences.Editor selectedCarPrefEditor;

    boolean isSelectedCarPrefPopulated;

    String urlWithParams;

    static String[] carNameArray;
    String[] vinArray;
    String[] carMakeArray;
    String[] carModelArray;
    String[] carYearArray;

    String dflId;

    static TextView textViewPager;

    TextView addNewVehicle;
    Button manage;
    ImageView act_logo;
    private Tracker mTracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Log.v("FLOW", "CAME HERE : MyCarActivity");
    }

    private void initialiseHeader() {
        addNewVehicle = (TextView) findViewById(R.id.addNewVehicle);
        addNewVehicle.setText("My Car");
        manage = (Button) findViewById(R.id.manage);
        manage.setOnClickListener(this);
        act_logo = (ImageView) findViewById(R.id.act_logo);
        act_logo.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.act_logo:
                finish();
                break;
            case R.id.manage:
                launchManageScreen(view);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mTracker != null) {
            mTracker.setScreenName(getClass().getSimpleName());
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
        menuItems.removeAll(menuItems);
        initialise();
    }

    public void launchManageScreen(View view) {
        Intent intent = new Intent(MyCarActivity.this, ManageScreenActivity.class);
        int selectedCarIndex = selectedCarSharedPref.getInt("selectedCar", 0);
        //Log.i("MyCarActivity-launchManage: ", "" + carNameArray[selectedCarIndex]);
        //Log.i("MyCarActivity-selectedCarIndex: ", "" + selectedCarIndex);
        startActivityForResult(intent, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {

            if (resultCode == RESULT_OK) {
                menuItems.removeAll(menuItems);
                initialise();
            }
            if (resultCode == RESULT_CANCELED) {
                menuItems.removeAll(menuItems);
                initialise();
            }
        }
    }//onActivityResult

    private void setupActionBar() {

        /*ActionBar ab = getSupportActionBar();
        //ab.setBackgroundDrawable(new ColorDrawable(0x01060000));
        ab.setDisplayShowCustomEnabled(true);
        ab.setDisplayShowTitleEnabled(false);
        LayoutInflater inflator = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.customactionbar, null);

        Button manage = (Button) v.findViewById(R.id.manage);
        TextView titleTV = (TextView) v.findViewById(R.id.addNewVehicle);
        ab.setCustomView(v);
        ab.setDisplayHomeAsUpEnabled(true);*/
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }*/
    public void initialise() {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.my_car_activity);
        // Obtain the shared Tracker instance.
        ApplicationVariables application = (ApplicationVariables) getApplication();
        mTracker = application.getDefaultTracker();
        initialiseHeader();
        Log.i("MyCarActivity: ", "init");

        //setupActionBar();

        pref = getApplicationContext().getSharedPreferences("ShockleyPref", 0);

        boolean isPreferencePopulated = pref.getBoolean("isPreferencePopulated", false);

        selectedCarSharedPref = getApplicationContext().getSharedPreferences("selected_car_shared_pref", Context.MODE_PRIVATE);
        selectedCarPrefEditor = selectedCarSharedPref.edit();


        isSelectedCarPrefPopulated = selectedCarSharedPref.getBoolean("isSelectedCarPrefPopulated", false);

        mypref = getApplicationContext().getSharedPreferences("HomePrefs", Activity.MODE_PRIVATE);
        dflId = mypref.getString("dfl_id", "");

        Intent intent = getIntent();
        tagValue = intent.getIntExtra("tag", 0);
        InputStream inpStream = null;

        GlobalState.uploadedImagePath = new ArrayList<String>();

        textViewPager = (TextView) findViewById(R.id.pagerName);

        String fileName = "";

        if (tagValue == AppConstants.MORE_OPTIONS)
            fileName = "Json/Menu_More.json";

        else if (tagValue == AppConstants.MY_CAR)
            fileName = "Json/Menu_MyCar.json";

        else if (tagValue == AppConstants.SERVICE_DEPARTMENT)
            fileName = "Json/Menu_ServiceDepartment.json";

        else
            fileName = "Json/Menu_Main.json";
        try {

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


        String[] titles = new String[menuItems.size()];

        for (int i = 0; i < menuItems.size(); i++)
            titles[i] = menuItems.get(i).getTitle();

        String[] subtitles = new String[menuItems.size()];

        for (int i = 0; i < menuItems.size(); i++)
            subtitles[i] = menuItems.get(i).getSubtitle();

        listView = (ListView) findViewById(R.id.newList);
        listView.setVisibility(View.VISIBLE);
        final NewMyArrayListAdapter adapter = new NewMyArrayListAdapter(this, titles);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                if (menuItems.get(position).getType().equals("help")) {

                    Log.i("MainActivity: ", "inside 'help'-check, position: " + position);
                    Intent intent = new Intent(MyCarActivity.this, HelpWebViewActivity.class);

                    String feedUrl = menuItems.get(position).getUrl();
                    if (!feedUrl.contains("www.mobileappscm.com")) {
                        if (feedUrl.contains("?"))
                            feedUrl = feedUrl + "&a=" + GlobalState.serverId + "&u=" + getUniqueDeviceId() + "&d=" + getUniqueDeviceId() + "&dn=" + getDeviceName();
                        else
                            feedUrl = feedUrl + "?a=" + GlobalState.serverId + "&u=" + getUniqueDeviceId() + "&d=" + getUniqueDeviceId() + "&dn=" + getDeviceName();
                    }

                    intent.putExtra("helpUrl", feedUrl);

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
                        if (feedUrl.contains("SERVERID")) {
                            feedUrl = feedUrl.replaceAll("SERVERID", GlobalState.serverId);
                        } else if (feedUrl.contains("?"))
                            feedUrl = feedUrl + "&a=" + GlobalState.serverId;
                        else
                            feedUrl = feedUrl + "?a=" + GlobalState.serverId;
                    }
                    feedUrl = feedUrl + "&u=" + getUniqueDeviceId() + "&d=" + getUniqueDeviceId() + "&dn=" + getDeviceName();

                    Log.i("MainActivity: ", "inside 'help'-check, position: " + position);
                    Intent intent = new Intent(MyCarActivity.this, HelpWebViewActivity.class);
                    intent.putExtra("helpUrl", feedUrl);
                    Log.i("webViewTitle: ", menuItems.get(position).getTitle());
                    intent.putExtra("webViewTitle", menuItems.get(position).getTitle());
                    intent.putExtra("Tag", String.valueOf(menuItems.get(position).getTag()));
                    intent.putExtra("browser", true);
                    intent.putExtra("position", position);
                    Log.i("MainActivity: ", "Url is: " + menuItems.get(position).getUrl());
                    startActivity(intent);
                } else if (menuItems.get(position).getType().equals("feed") || menuItems.get(position).getType().equals("rootfeed")) {
                    Intent intent = new Intent(MyCarActivity.this, SplashFeeds.class);
                    String feedUrl = menuItems.get(position).getUrl();
                    int tagValue = menuItems.get(position).getTag();

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
                    String carMakeNonEncoded = carMakeArray[currentPage];
                    String carMakeEncoded = "";
                    try {
                        carMakeEncoded = URLEncoder.encode(carMakeNonEncoded, "utf-8");
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    String carModelNonEncoded = carModelArray[currentPage];
                    String carModelEncoded = "";
                    try {
                        carModelEncoded = URLEncoder.encode(carModelNonEncoded, "utf-8");
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    feedUrl = feedUrl
                            + "&v=" + vinArray[currentPage]
                            + "&u=" + getUniqueDeviceId()
                            + "&d=" + getUniqueDeviceId()
                            + "&dn=" + getDeviceName()
                            + "&m=" + carMakeEncoded
                            + "&mo=" + carModelEncoded
                            + "&y=" + carYearArray[currentPage];
                    feedUrl = feedUrl.replaceAll(" ", "");
                    String temp = carDetailsArrayToLoad[1];
                    Log.i("vin +++++++++++++++++++++: ", temp + "feedUrl:" + feedUrl);
                    intent.putExtra("feedUrl", feedUrl);
                    intent.putExtra("positionFeed", position);
                    intent.putExtra("vin", temp);
                    intent.putExtra("tagValue", tagValue);

                    if (Utils.checkInternetConnection(MyCarActivity.this))
                        startActivity(intent);
                    else
                        showAlert("Internet not present! Please try again later.");
                } else if (menuItems.get(position).getType().equals("vehinfo")) {
                    Intent intent = new Intent(MyCarActivity.this, HelpWebViewActivity.class);
                    String vehInfoUrl = menuItems.get(position).getUrl();

                    int tagValue = menuItems.get(position).getTag();
                    if (GlobalState.isDealershipGroupVersion) {

                        if (vehInfoUrl.contains("SERVERID")) {
                            vehInfoUrl = vehInfoUrl.replaceAll("SERVERID", "SERVERID&DFLID=" + dflId);
                            if (vehInfoUrl.contains("SERVERID"))
                                vehInfoUrl = vehInfoUrl.replaceAll("SERVERID", GlobalState.serverId);
                        } else if (vehInfoUrl.contains("?"))
                            vehInfoUrl = vehInfoUrl + "&a=" + GlobalState.serverId + "&DFLID=" + dflId;
                        else
                            vehInfoUrl = vehInfoUrl + "&?=" + GlobalState.serverId + "&DFLID=" + dflId;
                    } else {
                        if (vehInfoUrl.contains("SERVERID")) {
                            vehInfoUrl = vehInfoUrl.replaceAll("SERVERID", GlobalState.serverId);
                        } else if (vehInfoUrl.contains("?"))
                            vehInfoUrl = vehInfoUrl + "&a=" + GlobalState.serverId;
                        else
                            vehInfoUrl = vehInfoUrl + "?a=" + GlobalState.serverId;
                    }
                    String carMakeNonEncoded = carMakeArray[currentPage];
                    String carMakeEncoded = "";
                    try {
                        carMakeEncoded = URLEncoder.encode(carMakeNonEncoded, "utf-8");
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    String carModelNonEncoded = carModelArray[currentPage];
                    String carModelEncoded = "";
                    try {
                        carModelEncoded = URLEncoder.encode(carModelNonEncoded, "utf-8");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    vehInfoUrl = vehInfoUrl
                            + "&v=" + vinArray[currentPage]
                            + "&u=" + getUniqueDeviceId()
                            + "&d=" + getUniqueDeviceId()
                            + "&dn=" + getDeviceName()
                            + "&m=" + carMakeEncoded
                            + "&mo=" + carModelEncoded
                            + "&y=" + carYearArray[currentPage];

                    intent.putExtra("helpUrl", vehInfoUrl);
                    intent.putExtra("position", position);
                    intent.putExtra("tagValue", tagValue);
                    intent.putExtra("webViewTitle", menuItems.get(position).getTitle());

                    Log.i("MyCarActivity: ", "vehinfo url is: " + vehInfoUrl);
                    if (Utils.checkInternetConnection(MyCarActivity.this))
                        startActivity(intent);
                    else
                        showAlert("Internet not present! Please try again later.");
                } else if (menuItems.get(position).getTag() == AppConstants.MORE_OPTIONS) {
                    Intent intent = new Intent(MyCarActivity.this, MainActivityListview.class);
                    intent.putExtra("tag", AppConstants.MORE_OPTIONS);
                    startActivity(intent);
                } else if (menuItems.get(position).getTag() == AppConstants.MY_CAR) {
                    Intent intent = new Intent(MyCarActivity.this, MyCarDetails.class);
                    intent.putExtra("tag", AppConstants.MY_CAR);
                    startActivity(intent);
                    finish();
                } else if (menuItems.get(position).getTag() == AppConstants.SERVICE_DEPARTMENT) {
                    Intent intent = new Intent(MyCarActivity.this, MainActivity.class);
                    intent.putExtra("tag", AppConstants.SERVICE_DEPARTMENT);
                    startActivity(intent);
                } else if (menuItems.get(position).getTag() == AppConstants.MPG_CALCULATOR) {
                    Intent intent = new Intent(MyCarActivity.this, MPGCalculator.class);
                    intent.putExtra("tag", AppConstants.MPG_CALCULATOR);
                    startActivity(intent);
                } else if (menuItems.get(position).getTag() == AppConstants.CARPARK_TAG) {

                    Intent intent = new Intent(MyCarActivity.this, ParkingMapActivity.class);
                    intent.putExtra("tag", AppConstants.CARPARK_TAG);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MyCarActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager_new);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(1);
        //mViewPager.setCurrentItem(3);
        PageListener pageListener = new PageListener();
        mViewPager.setOnPageChangeListener(pageListener);

        prefKeyValues = getApplicationContext().getSharedPreferences("hashmap_shared_pref", Context.MODE_PRIVATE);
        carDetailsHMap = new TreeMap<String, String>();


        carNameArray = new String[40];
        vinArray = new String[40];
        carMakeArray = new String[40];
        carModelArray = new String[40];
        carYearArray = new String[40];

        @SuppressWarnings("unchecked")
        HashMap<String, String> map = (HashMap<String, String>) prefKeyValues.getAll();
        for (String keyString : map.keySet()) {
            String value = map.get(keyString);
            carDetailsHMap.put(keyString, value);
            Log.i("carDetailsHMap-key-MyCarActivity: ", "" + keyString);
        }

        int i = 0;

        for (String keyString : carDetailsHMap.keySet()) {
            String carDetailsStringToLoad = carDetailsHMap.get(keyString);
            carDetailsArrayToLoad = new String[15];
            carDetailsArrayToLoad = carDetailsStringToLoad.split(",");

            Log.i("uploadedImage: ", "uploaded: " + carDetailsArrayToLoad[2]);

            try {
                GlobalState.uploadedImagePath.add(carDetailsArrayToLoad[2]);

                vinArray[i] = carDetailsArrayToLoad[1];
                carNameArray[i] = carDetailsArrayToLoad[0];

                Log.i("carDetailsArrayToLoad.length: ", "" + carDetailsArrayToLoad.length);

                if (carDetailsArrayToLoad.length > 3)
                    carMakeArray[i] = carDetailsArrayToLoad[3];
                else
                    carMakeArray[i] = "";

                if (carDetailsArrayToLoad.length > 4)
                    carModelArray[i] = carDetailsArrayToLoad[4];
                else
                    carModelArray[i] = "";

                if (carDetailsArrayToLoad.length > 5)
                    carYearArray[i] = carDetailsArrayToLoad[5];
                else
                    carYearArray[i] = "";

                Log.i("vinArray[i]: ", "" + vinArray[i]);
                i++;

            } catch (Exception e) {
                Log.i("ArrayOutOfBound Exception", "" + e.getMessage());
            }

        }

        mSectionsPagerAdapter.notifyDataSetChanged();

        currentPage = mViewPager.getCurrentItem();
        if (isSelectedCarPrefPopulated) {
            currentPage = selectedCarSharedPref.getInt("selectedCar", 0);
            mViewPager.setCurrentItem(currentPage);
        }
        Log.i("selectedCar", "" + carNameArray[currentPage]);
        Log.i("GlobalState.uploadedImagePath.size: ", "" + GlobalState.uploadedImagePath.size());
        textViewPager.setText(carNameArray[currentPage]);

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

    public void sendVinToServer(String vinParam, String feedUrl) {
        String deviceId = getUniqueDeviceId();
        urlWithParams = feedUrl + "&v=" + vinParam + "&u=" + deviceId;

        Log.i("vinParam: ", "" + vinParam);
        Log.i("urlWithParams: ", "" + urlWithParams);

        //InputStream content = null;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(new HttpGet(urlWithParams));
            HttpEntity entity = response.getEntity();
            String responseText = EntityUtils.toString(entity);

            Log.i("HttpGet responseMsg: ", "" + responseText);
        } catch (Exception e) {
            Log.i("[GET REQUEST]", "Network exception: " + e.getMessage());
        }
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
                Log.i("DeviceID ", "" + deviceId);
            }
            return deviceId;
        } catch (Exception e) {
            Log.v("Dealership", "exception getUniqueDeviceId " + e);
        }
        return null;
    }

    public String getDeviceName() {
        String deviceName = android.os.Build.MODEL;
        return deviceName;
    }

    private static class PageListener extends ViewPager.SimpleOnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
            currentPage = position;
            textViewPager.setText(carNameArray[currentPage]);
            selectedCarPrefEditor.putInt("selectedCar", currentPage);
            selectedCarPrefEditor.putBoolean("isSelectedCarPrefPopulated", true);
            selectedCarPrefEditor.commit();
            Log.i("selectedCarCommitted: ", "" + carNameArray[currentPage]);
            Log.i("selectedCarIndexCommitted: ", "" + currentPage);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            // TODO Auto-generated constructor stub
        }

        @Override
        public Fragment getItem(int position) {
            // TODO Auto-generated method stub
            Fragment fragment = new MyCarPagerFragment();
            Bundle args = new Bundle();
            args.putInt("page", (position));
            global = new Bundle();
            global.putInt("page", (position));
            //args.putString("file",direct.getAbsolutePath());
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return GlobalState.uploadedImagePath.size();
        }
    }

    //@Override
    //  public boolean onCreateOptionsMenu(Menu menu) {
    //   MenuInflater inflater = getSupportMenuInflater();
    //   inflater.inflate(R.menu.activity_itemlist, menu);
    //   return true;
    //}
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
