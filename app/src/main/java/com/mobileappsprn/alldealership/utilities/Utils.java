package com.mobileappsprn.alldealership.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mobileappsprn.alldealership.GlobalState;
import com.mobileappsprn.alldealership.MoreLocations;
import com.mobileappsprn.alldealership.entities.AppConstants;
import com.mobileappsprn.alldealership.entities.Items;

public class Utils {

	public static Bitmap getBitmapFromAsset(String strName, Context context) {
		AssetManager assetManager = context.getAssets();
		InputStream istr = null;
		try {
			istr = assetManager.open(strName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Bitmap bitmap = BitmapFactory.decodeStream(istr);
		return bitmap;
	}

	public static boolean checkInternetConnection(Activity activity){
		ConnectivityManager connMgr = (ConnectivityManager) activity.getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
	}
	public void makeCall(Activity act) {
		// we need to check to see if we have a phone number on record.
		if (MoreLocations.flag!= null) {
			if (MoreLocations.flag.equalsIgnoreCase("temp")) {
				Intent call = new Intent(Intent.ACTION_DIAL);
				call.setData(Uri.parse("tel:" + GlobalState.tempdealershipPhone));
				act.startActivity(call);
			}
		}
		else if(GlobalState.dealershipPhone != null){ 
			Intent call = new Intent(Intent.ACTION_DIAL);
			call.setData(Uri.parse("tel:" + GlobalState.dealershipPhone));
			act.startActivity(call);	
		}
		else 
			Toast.makeText(act, "No telephone number was provided, press visit our website for more contact options.", 
					Toast.LENGTH_LONG).show();

	}
	public void makeCallService(Activity act) {

		if (MoreLocations.flag!= null) {
			if (MoreLocations.flag.equalsIgnoreCase("temp")) {
				Intent call = new Intent(Intent.ACTION_DIAL);
				call.setData(Uri.parse("tel:" + GlobalState.tempdealershipServicePhone));
				act.startActivity(call);
			}
		}
		else if(GlobalState.dealershipPhone != null){ 
			Intent call = new Intent(Intent.ACTION_DIAL);
			call.setData(Uri.parse("tel:" + GlobalState.dealershipServicePhone));
			act.startActivity(call);

		}
		else 
			Toast.makeText(act, "No telephone number was provided, press visit our website for more contact options.", 
					Toast.LENGTH_LONG).show();

	}
	public ArrayList<Items> getFavQRurls(Activity act){
		ArrayList<Items> items=new ArrayList<Items>();
		SharedPreferences pref=act.getSharedPreferences(AppConstants.QR_SCANNER_FAV_PREFERENCE, Context.MODE_PRIVATE);
		List<String> keys = new ArrayList<String>();
		HashMap<String, ?> map = new HashMap<String, Object>(pref.getAll());	

		for (Map.Entry<String, ?> entry : map.entrySet()) {
			keys.add(entry.getKey());		    
		}
		for (int i = 0; i < keys.size(); i++)
		{
			Items item=new Items();
			if(pref.getString(keys.get(i), null)!=null)
			{
				item.setTitle(keys.get(i));
				item.setUrl(pref.getString(keys.get(i), null));
				items.add(item);
			}

		}

		return items;
	}
	public void removeQRurl(Activity act,String title){
		SharedPreferences pref=act.getSharedPreferences(AppConstants.QR_SCANNER_FAV_PREFERENCE, Context.MODE_PRIVATE);
		pref.edit().remove(title).commit();
	}
	public void saveFavQRCodes(Activity act,String title,String url)
	{
		SharedPreferences pref=act.getSharedPreferences(AppConstants.QR_SCANNER_FAV_PREFERENCE, Context.MODE_PRIVATE);
		pref.edit().putString(title, url).commit();	

	}
	public String checkQRurl(Activity act,String title)
	{
		SharedPreferences pref=act.getSharedPreferences(AppConstants.QR_SCANNER_FAV_PREFERENCE, Context.MODE_PRIVATE);
		return pref.getString(title, null);
	}

	public void sendEmail(Activity act, String email){
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/html");
		intent.putExtra(Intent.EXTRA_EMAIL, email);
		intent.putExtra(Intent.EXTRA_SUBJECT, "");
		intent.putExtra(Intent.EXTRA_TEXT, "");

		act.startActivity(Intent.createChooser(intent, "Send Email"));
	}

	public static void registerWithGcm(Context mContext) {
		//if(checkPlayServices(mContext)) {
			final GoogleCloudMessaging[] gcm = new GoogleCloudMessaging[1];
			final Context context = mContext;
			final String[] regId = new String[1];

			gcm[0] = GoogleCloudMessaging.getInstance(context);
			regId[0] = getRegistrationId(context);

		String android_id = Settings.Secure.getString(mContext.getContentResolver(),
				Settings.Secure.ANDROID_ID);

		Log.v("Utils", "DeviceToken "+android_id);

			new AsyncTask<Void, Void, String>() {
				@Override
				protected String doInBackground(Void... params) {
					String msg = "";
					try {
						if (TextUtils.isEmpty(regId[0])) {
							if (gcm[0] == null) {
								gcm[0] = GoogleCloudMessaging.getInstance(context);
							}
							regId[0] = gcm[0].register(AppConstants.GOOGLE_PROJECT_NUMBER);
						}
						Log.v("RegisterActivity", "registerInBackground - regId: "
								+ regId[0]);
						msg = "Device registered, registration ID=" + regId[0];
						storeRegistrationId(context, regId[0]);
						//new ShareExternalServer().shareRegIdWithAppServer(context, regId[0], GlobalConstants.LOGGED_IN_USER_ID);
						//BS updateRegIdBackend(context, regId[0]);
					} catch (IOException ex) {
						msg = "Error :" + ex.getMessage();
						Log.v("RegisterActivity", msg);
					}
					Log.v("RegisterActivity", "AsyncTask completed: " + msg);
					return msg;
				}

				@Override
				protected void onPostExecute(String msg) {

				}
			}.execute(null, null, null);
		//}
	}

	public static String getRegistrationId(Context context){
		SharedPreferences mpref = context.getApplicationContext().getSharedPreferences(AppConstants.pref_NAME, Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		String sessId = mpref.getString(AppConstants.pref_REG, null);
		return sessId;
	}

	public static void storeRegistrationId(Context context, String reg){
		SharedPreferences mPref = context.getApplicationContext().getSharedPreferences(AppConstants.pref_NAME, Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		SharedPreferences.Editor editor = mPref.edit();
		editor.putString(AppConstants.pref_REG, reg);
		editor.commit();
	}

	//Parveen



}
