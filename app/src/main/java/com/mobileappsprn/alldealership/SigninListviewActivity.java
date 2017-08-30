package com.mobileappsprn.alldealership;

import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.mobileappsprn.alldealership.adapters.SigninListviewAdapter;
import com.mobileappsprn.alldealership.entities.AppConstants;
import com.mobileappsprn.alldealership.entities.Items;
import com.mobileappsprn.alldealership.utilities.Utils;

public class SigninListviewActivity extends Activity {

    ArrayList<Items> listviewItemsList;
    private ListView listView;

    public ImageView imageView, newImage;

    SharedPreferences pref, mypref;
    String dflId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        listviewItemsList = new ArrayList<Items>();
        Log.v("FLOW", "CAME HERE : SigninListviewActivity");

        // Toast.makeText(this, "Came Here", Toast.LENGTH_SHORT).show();
        
        /*ActionBar ab = getSupportActionBar();
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

    @Override
    protected void onResume() {
        super.onResume();

		/*ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);*/

        listviewItemsList.removeAll(listviewItemsList);
        initialize();
    }

    private void setupActionBar() {

//		ActionBar ab = getSupportActionBar();
//        ab.setDisplayShowCustomEnabled(true);
//        ab.setDisplayShowTitleEnabled(false);
//        LayoutInflater inflator = (LayoutInflater) this
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View v = inflator.inflate(R.layout.actionbar_signin_layout, null);
//
//        ab.setCustomView(v);
//
//		ab.setDisplayHomeAsUpEnabled(true);
    }
	
	/*
	 * Bitmap bitmap =
	 * Utils.getBitmapFromAsset(GlobalState.homeMenuItems.get(position
	 * ).getLogoURL(), this); image.setImageBitmap(bitmap);
	 */

    public void initialize() {
        setContentView(R.layout.signin_listview_activity);

        Log.v("Sign In Activity", "Came Here 111");


        imageView = (ImageView) findViewById(R.id.logo);
        newImage = (ImageView) findViewById(R.id.newlogo);

        pref = getApplicationContext().getSharedPreferences("ShockleyPref", 0);

        mypref = getApplicationContext().getSharedPreferences("HomePrefs",
                Activity.MODE_PRIVATE);
        dflId = mypref.getString("dfl_id", "");

        if (mypref.getBoolean("isItemSelected", false)) {
            int position = mypref.getInt("Itemposition", 0);
            Bitmap bitmap = Utils.getBitmapFromAsset("App-Specific-Icons/" + GlobalState.homeMenuItems.get(position).getLogoURL(), this);

            if (GlobalState.isNewSubMenu) {
                imageView.setVisibility(View.GONE);
                newImage.setVisibility(View.VISIBLE);
                newImage.setImageBitmap(bitmap);
            } else {
                imageView.setImageBitmap(bitmap);
            }
        }
        newImage.setImageDrawable(getResources().getDrawable(R.drawable.logo_hd));

        if (GlobalState.isNewSubMenu) {
            listView = (ListView) findViewById(R.id.newSigninListview);
            listView.setVisibility(View.VISIBLE);
            ((ListView) findViewById(R.id.signinListview)).setVisibility(View.GONE);

        } else {
            listView = (ListView) findViewById(R.id.signinListview);
            listView.setVisibility(View.VISIBLE);
            listView.setBackground(getResources().getDrawable(R.drawable.customshape));
        }
        setupActionBar();

        listviewItemsList = new ArrayList<Items>();

        String fileName = AppConstants.JSON_FILE_PATH + "Menu_Signin.json";
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

                listviewItemsList.add(items);

            }// for
            // GlobalState.menuItems=menuItems;

        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] titles = new String[listviewItemsList.size()];

        for (int i = 0; i < listviewItemsList.size(); i++)
            titles[i] = listviewItemsList.get(i).getTitle();

        String[] subtitles = new String[listviewItemsList.size()];

        for (int i = 0; i < listviewItemsList.size(); i++)
            subtitles[i] = listviewItemsList.get(i).getSubtitle();

        final SigninListviewAdapter adapter = new SigninListviewAdapter(this,
                titles, listviewItemsList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view,
                                                                    int position, long id) {

                                                Intent intent = null;
                                                int tag = listviewItemsList.get(position).getTag();

                                                String type = listviewItemsList.get(position).getType();


                                                if (type.equals("help") ||
                                                        type.equals("redirect")) {

                                                    Log.i("MainActivity: ", "inside 'help'-check, position: " + position);
                                                    intent = new Intent(SigninListviewActivity.this, HelpWebViewActivity.class);

                                                    String feedUrl = listviewItemsList.get(position).getUrl();
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
                                                        }

                                                        feedUrl = feedUrl + "&u=" + getUniqueDeviceId() + "&d=" + getUniqueDeviceId() + "&dn=" + getDeviceName();
                                                    }

                                                    intent.putExtra("helpUrl", feedUrl);
                                                    Log.i("webViewTitle: ", listviewItemsList.get(position).getTitle());
                                                    intent.putExtra("webViewTitle", listviewItemsList.get(position).getTitle());
                                                    intent.putExtra("Tag", String.valueOf(listviewItemsList.get(position).getTag()));
                                                    intent.putExtra("browser", listviewItemsList.get(position).isBrowser());
                                                    intent.putExtra("position", position);
                                                    Log.i("MainActivity: ", "Url is: " + listviewItemsList.get(position).getUrl());
                                                    startActivity(intent);


                                                } else {
                                                    if (tag == AppConstants.ADD_VEHICLE_MANUALLY)
                                                        intent = new Intent(SigninListviewActivity.this, MyCarDetails.class);
                                                    else
                                                        intent = new Intent(SigninListviewActivity.this, SigninActivity.class);


                                                    intent.putExtra("tag", tag);
                                                    Log.v("Signin Activity", "Here Put Tag : " + tag);

                                                    // we don't want to access any instances of SigninActivity without an internet connection.
                                                    if (tag == AppConstants.ADD_VEHICLE_MANUALLY || Utils.checkInternetConnection(SigninListviewActivity.this))

                                                    {
                                                        startActivity(intent);
                                                        //finish();
                                                    } else
                                                        Toast
                                                                .makeText(

                                                                        getApplicationContext(),

                                                                        "This feature requires an internet connection.", Toast.LENGTH_SHORT)
                                                                .

                                                                        show();
                                                }
                                            }


                                            //older code
				/*if (listviewItemsList.get(position).getTag() == AppConstants.RETRIEVE_VEHICLES_VIA_EMAIL) {
					intent = new Intent(SigninListviewActivity.this,
							SigninActivity.class);
					intent.putExtra("item_position", position);
					intent.putExtra("tag", AppConstants.RETRIEVE_VEHICLES_VIA_EMAIL);
				} else if (listviewItemsList.get(position).getTag() == AppConstants.ADD_VEHICLE_MANUALLY) {
					intent = new Intent(SigninListviewActivity.this, MyCarDetails.class);
					intent.putExtra("tag", AppConstants.ADD_VEHICLE_MANUALLY);
				} else if (listviewItemsList.get(position).getTag() == AppConstants.CHANGE_PASSWORD) {
					intent = new Intent(SigninListviewActivity.this,
							SigninActivity.class);

								intent.putExtra("tag", AppConstants.CHANGE_PASSWORD);
				} else {
					intent = new Intent(SigninListviewActivity.this,
							SigninActivity.class);
					intent.putExtra("tag", AppConstants.FORGOT_PASSWORD);
				}
				
				if (Utils.checkInternetConnection(SigninListviewActivity.this) || 
						listviewItemsList.get(position).getTag() == AppConstants.ADD_VEHICLE_MANUALLY)
        			startActivity(intent);
        		else
        			noInternetDialog();*/

                                        }

        );

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


}
