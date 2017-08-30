package com.mobileappsprn.alldealership.contactus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mobileappsprn.alldealership.R;
import com.mobileappsprn.alldealership.utilities.ApplicationVariables;

public class ContactUsEmailActivity extends FragmentActivity {

	private Tracker mTracker;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.help_web_view);
		// Obtain the shared Tracker instance.
		ApplicationVariables application = (ApplicationVariables) getApplication();
		mTracker    =   application.getDefaultTracker();
		Log.v("FLOW", "CAME HERE : ContactUsEmailActivity");
		
		Intent calling = getIntent();
		setupActionBar(calling.getStringExtra("title"));

		TextView titleLabel = (TextView) findViewById(R.id.help_web_view_title);
		titleLabel.setText(calling.getStringExtra("title"));
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(mTracker != null) {
			mTracker.setScreenName(getClass().getSimpleName());
			mTracker.send(new HitBuilders.ScreenViewBuilder().build());
		}
	}

	/*
	 * Basic setup of the action bar.
	 */
	private void setupActionBar(String title) {
		/*ActionBar ab = getSupportActionBar();
		ab.setDisplayShowCustomEnabled(true);
		ab.setDisplayShowTitleEnabled(false);
		LayoutInflater inflator = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View v = inflator.inflate(R.layout.help_web_view_titlebar, null);
		TextView titleLabel = (TextView)v.findViewById(R.id.help_web_view_title);
		titleLabel.setText(title);
		
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

}
