package com.mobileappsprn.alldealership.contactus;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.android.gms.internal.nu;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mobileappsprn.alldealership.DrillDownRoot;
import com.mobileappsprn.alldealership.GlobalState;
import com.mobileappsprn.alldealership.HelpWebViewActivity;
import com.mobileappsprn.alldealership.MoreLocations;
import com.mobileappsprn.alldealership.NewMoreLocations;
import com.mobileappsprn.alldealership.R;
import com.mobileappsprn.alldealership.SplashFeeds;
import com.mobileappsprn.alldealership.adapters.ItemsListViewAdapter;
import com.mobileappsprn.alldealership.entities.AppConstants;
import com.mobileappsprn.alldealership.entities.Items;
import com.mobileappsprn.alldealership.utilities.ApplicationVariables;
import com.mobileappsprn.alldealership.utilities.Utils;

public class ContactUsListView extends Activity {

    private ArrayList<Items> itemsList;
    private ListView listView;
    public ImageView imageView, newImage;
    public static String flag;
    SharedPreferences pref, mypref;
    String dflId;
    ArrayList<Items> menuItems = new ArrayList<Items>();
    boolean isPreferencePopulated;
    String[] vinArray;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        itemsList = new ArrayList<Items>();
        Log.v("FLOW", "CAME HERE : ContactUsListView");
        Intent calling = getIntent();
        if (GlobalState.homeMenuItems == null) {
//            getMenuItems();
            finish();
        }
        setupActionBar(calling.getStringExtra("title"));




    }

    private void setupActionBar(String title) {


        /*ActionBar ab = getSupportActionBar();
        ab.setDisplayShowCustomEnabled(true);
        ab.setDisplayShowTitleEnabled(false);
        LayoutInflater inflator = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflator.inflate(R.layout.help_web_view_titlebar, null);
        TextView titleLabel = (TextView) v.findViewById(R.id.help_web_view_title);
        titleLabel.setText(title);

        ab.setCustomView(v);
        ab.setDisplayHomeAsUpEnabled(true);*/
    }

   /* @Override
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

    @Override
    protected void onResume() {
        super.onResume();
        itemsList.clear();
        if(mTracker != null) {
            mTracker.setScreenName(getClass().getSimpleName());
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
        initialize();

    }

    private void setupActionBar() {

      /*  ActionBar ab = getSupportActionBar();
        ab.setDisplayShowCustomEnabled(true);
        ab.setDisplayShowTitleEnabled(false);
        LayoutInflater inflator = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.actionbar_directions_layout, null);

        ab.setCustomView(v);*/
    }

	/*
     * Bitmap bitmap =
	 * Utils.getBitmapFromAsset(GlobalState.homeMenuItems.get(position
	 * ).getLogoURL(), this); image.setImageBitmap(bitmap);
	 */

    public void initialize() {
        setContentView(R.layout.dealershipinfo_listview_activity);
        // Obtain the shared Tracker instance.
        ApplicationVariables application = (ApplicationVariables) getApplication();
        mTracker    =   application.getDefaultTracker();

        imageView = (ImageView) findViewById(R.id.logo);
        newImage = (ImageView) findViewById(R.id.newlogo);
        Intent calling = getIntent();
        TextView titleLabel = (TextView) findViewById(R.id.help_web_view_title);
        titleLabel.setText(calling.getStringExtra("title"));
        pref = getApplicationContext().getSharedPreferences("ShockleyPref", 0);
        isPreferencePopulated = pref.getBoolean("isPreferencePopulated", false);
        vinArray = new String[40];

        mypref = getApplicationContext().getSharedPreferences("HomePrefs",
                Activity.MODE_PRIVATE);
        dflId = mypref.getString("dfl_id", ""); //BS uncommented

        Log.v("Contact Us LV ","DFL Res 0: "+dflId);




        if (mypref.getBoolean("isItemSelected", false)) {
            int position = mypref.getInt("Itemposition", 0);
            Log.v("GlobalState", "GS : " + GlobalState.homeMenuItems);
            Bitmap bitmap = Utils.getBitmapFromAsset("App-Specific-Icons/" + GlobalState.homeMenuItems.get(position).getLogoURL(), this);

            if (GlobalState.isNewSubMenu) {
                newImage.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                newImage.setImageBitmap(bitmap);
            } else {
                imageView.setImageBitmap(bitmap);
            }
        }





        if (GlobalState.isNewMenu) {
            listView = (ListView) findViewById(R.id.newdealershipinfo_listview);
            listView.setVisibility(View.VISIBLE);
            ((ListView) findViewById(R.id.dealershipinfo_listview)).setVisibility(View.GONE);

        } else {
            listView = (ListView) findViewById(R.id.dealershipinfo_listview);
            listView.setVisibility(View.VISIBLE);
            listView.setBackground(getResources().getDrawable(R.drawable.customshape));

        }
        setupActionBar();

        itemsList = new ArrayList<Items>();

        String fileName = AppConstants.JSON_FILE_PATH + "Menu_Contact.json";
        InputStream inpStream = null;

        try {
            inpStream = this.getAssets().open(fileName);

            int size = inpStream.available();
            byte[] buffer = new byte[size];
            inpStream.read(buffer);
            inpStream.close();
            String bufferString = new String(buffer);

            JSONObject jsonRootObj = new JSONObject(bufferString);

            JSONArray jsonArrayItems = jsonRootObj.getJSONArray("Items");

            for (int i = 0; i < jsonArrayItems.length(); i++) {
                JSONObject jsonItemObj = jsonArrayItems.getJSONObject(i);
                Items items = new Items();
                if (jsonItemObj.has("Title"))
                    items.setTitle(jsonItemObj.getString("Title"));

                if (jsonItemObj.has("Subtitle"))
                    items.setSubtitle(jsonItemObj.getString("Subtitle"));

                if (jsonItemObj.has("Tag"))
                    items.setTag(jsonItemObj.getInt("Tag"));
                else
                    items.setTag(-1);

                if (jsonItemObj.has("Type"))
                    items.setType(jsonItemObj.getString("Type"));
                else
                    items.setType("");

                if (jsonItemObj.has("Display"))
                    items.setDisplay(jsonItemObj.getString("Display"));
                else
                    items.setDisplay("");

                if (jsonItemObj.has("Url"))
                    items.setUrl(jsonItemObj.getString("Url"));
                else
                    items.setUrl("");

                if (jsonItemObj.has("ShowIconType")) {
                    items.setShowIconType(jsonItemObj.getString("ShowIconType"));
                } else {
                    items.setShowIconType(" ");
                }

                itemsList.add(items);

            }// for
            // GlobalState.menuItems=menuItems;

        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] titles = new String[itemsList.size()];

        for (int i = 0; i < itemsList.size(); i++)
            titles[i] = itemsList.get(i).getTitle();

        String[] subtitles = new String[itemsList.size()];

        for (int i = 0; i < itemsList.size(); i++)
            subtitles[i] = itemsList.get(i).getSubtitle();

        //listView = (ListView) findViewById(R.id.dealershipinfo_listview);
        ItemsListViewAdapter adapter = new ItemsListViewAdapter(this, R.layout.custom_list, itemsList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent intent = null;
                Items selected = itemsList.get(position);
                int tag = selected.getTag();
                if (selected.getType().equals("callservice")) {
                    new Utils().makeCallService(ContactUsListView.this);
                } else if (selected.getType().equalsIgnoreCase("website")) {
                    //else if (tag == AppConstants.WEBVIEW_TAG) {
                    if (MoreLocations.flag != null) {

                        Log.v("FLOW", "CAME HERE inside : Temp Dearler");

                        intent = new Intent(ContactUsListView.this, HelpWebViewActivity.class);
                        intent.putExtra("helpUrl", GlobalState.tempdealerWebsite);
                        intent.putExtra("webViewTitle", selected.getTitle());
                        intent.putExtra("Tag", String.valueOf(selected.getTag()));
                        intent.putExtra("browser", selected.isBrowser());
                        intent.putExtra("position", position);
                    } else {

                        Log.v("FLOW", "CAME HERE inside : Dearler");

                        intent = new Intent(ContactUsListView.this, HelpWebViewActivity.class);
                        intent.putExtra("helpUrl", GlobalState.dealerWebsite);
                        intent.putExtra("webViewTitle", selected.getTitle());
                        intent.putExtra("Tag", String.valueOf(selected.getTag()));
                        intent.putExtra("browser", selected.isBrowser());
                        intent.putExtra("position", position);
                    }


                } else if (selected.getType().equals("feed") ||
                        selected.getType().equals("rootfeed")) {

                    String title = selected.getTitle();
                   //intent = new Intent(ContactUsListView.this, SplashFeeds.class);
                    intent = new Intent(ContactUsListView.this, DrillDownRoot.class);
                    String feedUrl = selected.getUrl();


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
                            feedUrl = feedUrl.replaceAll("SERVERID", "SERVERID&DFLID=" + GlobalState.homeMenuItems.get(mypref.getInt("Itemposition", 0)).getDFLID() /*GlobalState.dealershipId*/);
                            if (feedUrl.contains("SERVERID"))
                                feedUrl = feedUrl.replaceAll("SERVERID", GlobalState.serverId);
                        }
                        else if (feedUrl.contains("?"))
                        {
                            feedUrl = feedUrl + "&a=" + GlobalState.serverId + "&DFLID=" + GlobalState.homeMenuItems.get(mypref.getInt("Itemposition", 0)).getDFLID() /*GlobalState.dealershipId*/;
                        }
                        else
                        {
                            feedUrl = feedUrl + "?a=" + GlobalState.serverId + "&DFLID=" + GlobalState.homeMenuItems.get(mypref.getInt("Itemposition", 0)).getDFLID() /*GlobalState.dealershipId*/;
                        }

                    }
                    /*if (isPreferencePopulated) {
                        feedUrl = feedUrl + "&v=" + vinArray[selectedCarIndex];
                    } else
                        feedUrl = feedUrl + "&v=" + "";

                    feedUrl = feedUrl + "&u=" + getUniqueDeviceId() + "&d=" + getUniqueDeviceId() + "&dn=" + getDeviceName();
                   */

                    Log.v("Contact Us LV ","DFL Res: "+feedUrl);

                    feedUrl = feedUrl.replaceAll(" ", "");
                    intent.putExtra("feedUrl1", feedUrl);
                    Log.i("Main Activity: ", "feedUrl: " + feedUrl);
                    intent.putExtra("positionFeed1", position);
                    intent.putExtra("TitleName", title);

                    // The below 3 fields added as removed "SplashFeeds class"

                    intent.putExtra("Title", title);
                    intent.putExtra("tagValue1", intent.getIntExtra("tagValue", -1));
                    intent.putExtra("vin1", intent.getStringExtra("vin"));

                    if (Utils.checkInternetConnection(ContactUsListView.this))
                        startActivity(intent);
                   // else
                        //showAlert("Internet not present! Please try again later.");





                } else if (selected.getType().equalsIgnoreCase("directions"))

                    //else if(tag == AppConstants.DIRECTIONS_CONTACT_TAG)
                    if (MoreLocations.flag != null) {
                        intent = new Intent(ContactUsListView.this, TempDirectionsActivity.class);
                    } else {
                        intent = new Intent(ContactUsListView.this, DirectionsActivity.class);
                    }

                else if (selected.getType().equalsIgnoreCase("call"))

                    // else if(tag == AppConstants.PHONE_CONTACT_TAG)
                    makeCall();
                else if (selected.getType().equalsIgnoreCase("email"))

                    //else if(tag == AppConstants.EMAIL_CONTACT_TAG)
                    makeEmail();
                else if (selected.getType().equalsIgnoreCase("morelocations")) {
                    Intent i;
                    if (GlobalState.isNewMenu) {
                        i = new Intent(ContactUsListView.this, NewMoreLocations.class);
                    } else {
                        i = new Intent(ContactUsListView.this, MoreLocations.class);
                    }

                    startActivity(i);
                }

                if (intent != null) {
                    intent.putExtra("title", selected.getTitle());
                    intent.putExtra("tag", tag);
                    startActivity(intent);
                }
            }
        });

    }

    /*
     * creates a phone call intent and makes a call
     */
    private void makeCall() {
        // we need to check to see if we have a phone number on record.

        if (MoreLocations.flag != null) {
            if (MoreLocations.flag.equalsIgnoreCase("temp")) {
                Intent call = new Intent(Intent.ACTION_DIAL);
                call.setData(Uri.parse("tel:" + GlobalState.tempdealershipPhone));
                startActivity(call);
            }
        } else if (GlobalState.dealershipPhone != null) {
            Intent call = new Intent(Intent.ACTION_DIAL);
            call.setData(Uri.parse("tel:" + GlobalState.dealershipPhone));
            startActivity(call);

            // NOTE: THIS NEEDS TO BE LOOKED AT, IT WILL MESS UP THE SHERLOCK BACK BUTTON IF USED
            //we want to return to the contacts listview when the call is ended.
            //TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            //tm.listen(new ContactUsPhoneListener(), PhoneStateListener.LISTEN_CALL_STATE);

            //finish();
        } else
            Toast.makeText(this, "No telephone number was provided, press visit our website for more contact options.",
                    Toast.LENGTH_LONG).show();

    }

    /*
     * calls email client to make an email.
     */
    private void makeEmail() {
        if (MoreLocations.flag != null) {
            if (MoreLocations.flag.equalsIgnoreCase("temp")) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{GlobalState.tempdealershipEmail});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Information Request");
                startActivity(Intent.createChooser(intent, ""));
            }
        } else if (GlobalState.dealershipEmail != null) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("plain/text");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{GlobalState.dealershipEmail});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Information Request");
            startActivity(Intent.createChooser(intent, ""));
        } else
            Toast.makeText(this, "No email address was provided, press visit our website for more contact options.",
                    Toast.LENGTH_LONG).show();
    }

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
                    GlobalState.dealershipLat = c.getString("lat");
                }
                if (c.has("lon")) {
                    items.setLon(c.optString("lon"));
                    GlobalState.dealershipLon = c.getString("lon");
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

            }


            GlobalState.menuItems = menuItems;
            GlobalState.homeMenuItems = menuItems;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //nested classes
	/*private class ContactUsPhoneListener extends PhoneStateListener {
			
		public void onCallStateChanged(int state, String incomingNumber){
			if(state == TelephonyManager.CALL_STATE_IDLE){
				Intent ret = new Intent(ContactUsListView.this, ContactUsListView.class);
				startActivity(ret);
			}
		}
			
	}*/

}
