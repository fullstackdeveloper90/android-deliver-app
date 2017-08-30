package com.mobileappsprn.alldealership;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mobileappsprn.alldealership.adapters.DrillDetailsArrayAdapter;
import com.mobileappsprn.alldealership.utilities.ApplicationVariables;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

public class DrillDownDetails extends Activity {
	int position = 0;
	private Tracker mTracker;
	ListView listView;
	String toolbarTitle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.drilldown_details);
		// Obtain the shared Tracker instance.
		ApplicationVariables application = (ApplicationVariables) getApplication();
		mTracker    =   application.getDefaultTracker();
		Log.v("FLOW", "CAME HERE : DrillDownDetails");
		
		/*ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);*/
		
		Intent intent = getIntent();
		position = intent.getIntExtra("itemClickedPostion", -1);
		
		toolbarTitle = intent.getStringExtra("toolbarTitle");
		
		int detailsArraySize = GlobalState.rootMenuItems.get(position).getDetails().size();
		String[] titles = new String[detailsArraySize];
		Log.i("detailsArraySize: ", ""+detailsArraySize);
		
		for(int i = 0; i< detailsArraySize; i++) {
			titles[i] = GlobalState.rootMenuItems.get(position).getDetails().get(i).getTitle();
			Log.i("titles[i]: ", ""+titles[i]);
		}
		
		String[] subtitles = new String[detailsArraySize];
		
		for(int i = 0; i< detailsArraySize; i++) {
			subtitles[i] = GlobalState.rootMenuItems.get(position).getDetails().get(i).getDetailText();
			Log.i("subtitles[i]: ", ""+subtitles[i]);
		}
		
		listView = (ListView) findViewById(R.id.drilldownDetails);
		final DrillDetailsArrayAdapter adapter = new DrillDetailsArrayAdapter(this, titles, subtitles, "Title");
		listView.setAdapter(adapter);
		this.setTitle(toolbarTitle);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(mTracker != null) {
			mTracker.setScreenName(getClass().getSimpleName());
			mTracker.send(new HitBuilders.ScreenViewBuilder().build());
		}
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