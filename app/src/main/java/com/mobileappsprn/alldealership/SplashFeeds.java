package com.mobileappsprn.alldealership;


import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mobileappsprn.alldealership.utilities.ApplicationVariables;
import com.mobileappsprn.alldealership.utilities.Utils;

public class SplashFeeds extends Activity {

	public String feedUrl;
	public int positionFeed;
	public int tagValue;
	public String vin,title;
	Context context = this;
	Activity activity;
	JSONObject json = null;
	private Tracker mTracker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_feeds);
		// Obtain the shared Tracker instance.
		ApplicationVariables application = (ApplicationVariables) getApplication();
		mTracker    =   application.getDefaultTracker();
		Log.v("FLOW", "CAME HERE : SplashFeeds");
		activity = this;
		Intent intent = getIntent();
		feedUrl = intent.getStringExtra("feedUrl");
		positionFeed = intent.getIntExtra("positionFeed", -1);
		tagValue = intent.getIntExtra("tagValue", -1);
		title = intent.getStringExtra("TitleName");
		
		vin = intent.getStringExtra("vin");
		
		if(Utils.checkInternetConnection(this)) {
			getServerResponse();
		} else {
			showDialog("Internet not present! Please try later.");
			finish();
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
	
	public void showDialog(String msg){
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setMessage(msg);
        adb.setTitle("Alert!");
        adb.setCancelable(false);
//        adb.setIcon(android.R.drawable.ic_dialog_alert);
         adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		        	dialog.dismiss();
                  finish();
		      } });
         adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		        	dialog.dismiss();
		            finish();
		      } });
		    adb.show();
	}
	
	public void getServerResponse(){
		new HandleBgTasks().execute();
	}
	
	public class HandleBgTasks extends AsyncTask<Integer, String, Void>{
    	String type;
    	boolean postStatus = false;
		private ProgressDialog dialog;
    	
    	@Override
    	protected void onPreExecute() {
    		// TODO Auto-generated method stub
    		super.onPreExecute();
    		dialog = new ProgressDialog(context);
    		dialog.setMessage("Loading ...");
    		dialog.setCancelable(true);
            dialog.show();
    	}

		@Override
		protected Void doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			if (Utils.checkInternetConnection(activity))
				json=FetchJson.getJSONfromURL(feedUrl);

			Log.v("Splash Feeds", "JSON RES: "+json.toString());

			return null;
		}
		
		@Override
	    protected void onPostExecute(Void result) {	
			
			 if (dialog.isShowing()) {
		            dialog.dismiss();
		     }

			 finish();
             Intent intent = new Intent(SplashFeeds.this,DrillDownRoot.class);
             intent.putExtra("feedUrl1", feedUrl);
             intent.putExtra("positionFeed1", positionFeed);
             intent.putExtra("tagValue1", tagValue);
             intent.putExtra("vin1", vin);
             intent.putExtra("Title", title);
            // intent.putExtra("json", json.toString());
             startActivity(intent);
		}	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash_feeds, menu);
		return true;
	}

}
