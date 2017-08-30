package com.mobileappsprn.alldealership;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mobileappsprn.alldealership.utilities.ApplicationVariables;
import com.mobileappsprn.alldealership.utilities.Utils;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class HelpWebViewActivity extends Activity {
	
	WebView helpWebView;
	String dflId;
	SharedPreferences pref,mypref;
	private Tracker mTracker;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.help_web_view);
		// Obtain the shared Tracker instance.
		ApplicationVariables application = (ApplicationVariables) getApplication();
		mTracker    =   application.getDefaultTracker();
		Log.v("FLOW", "CAME HERE : HelpWebViewActivity");
		//findViewById(R.id.header).setVisibility(View.GONE);
		
		helpWebView = (WebView) findViewById(R.id.helpWebView);
		
        
        Intent intent = getIntent();
        String helpUrl = intent.getStringExtra("helpUrl");
        int position = intent.getIntExtra("position", -1);
        boolean flag = intent.getBooleanExtra("browser", false);
        String webViewTitle = intent.getStringExtra("webViewTitle");
        Log.i("WebViewActivity -- webViewTitle: ", ""+webViewTitle);
        
        Log.i("WebView Activity: ", "helpUrl: "+helpUrl);
		mypref = getApplicationContext().getSharedPreferences("HomePrefs", Activity.MODE_PRIVATE);
		dflId = mypref.getString("dfl_id", "");

		Log.v("MainActivity: ", "CAAAMEEEE Here HElp WebView "+GlobalState.dealershipId);

		if(dflId.trim().equals("")){
			dflId = GlobalState.dealershipId;
		}
        
        if(Utils.checkInternetConnection(this))
        {
			if (helpUrl.contains("SERVERID")){
				Log.v("MainActivity: ", "CAAAMEEEE Here HElp URL 1 "+helpUrl);
				helpUrl = helpUrl.replaceAll("SERVERID", GlobalState.serverId)+"&DFLID="+dflId  + "&k=" + Utils.getRegistrationId(HelpWebViewActivity.this);
				Log.i("HelpWebView Activity: ", "helpUrl: "+helpUrl);

			}
			//else { //Added By BS
				if (flag) {
					Log.v("MainActivity: ", "CAAAMEEEE Here HElp URL 2 " + helpUrl);
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(helpUrl));
					startActivity(browserIntent);
					finish();
				} else {
					Log.v("MainActivity: ", "CAAAMEEEE Here HElp URL 3 " + helpUrl + "&k=" + Utils.getRegistrationId(HelpWebViewActivity.this));
					helpWebView.getSettings().setJavaScriptEnabled(true);
					helpWebView.setWebViewClient(new WebViewClient());
					helpWebView.loadUrl(helpUrl);
					setupActionBar(webViewTitle);
					//this.setTitle(GlobalState.menuItems.get(position).getDisplay());
				}
			//}
        }
		
        else
        {
        	Toast.makeText(getApplicationContext(), "Internet Not Present! Please Try Later.", Toast.LENGTH_SHORT).show();
        	finish();
        }
        
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(mTracker != null) {
			mTracker.setScreenName("Image~" + getClass().getSimpleName());
			mTracker.send(new HitBuilders.ScreenViewBuilder().build());
		}
	}
	
	private void setupActionBar(String title) {

		TextView help_web_view_title = (TextView) findViewById(R.id.help_web_view_title);
		help_web_view_title.setText(title);
		/*ActionBar ab = getSupportActionBar();
		ab.setDisplayShowCustomEnabled(true);
		ab.setDisplayShowTitleEnabled(false);
		LayoutInflater inflator = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
		
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
