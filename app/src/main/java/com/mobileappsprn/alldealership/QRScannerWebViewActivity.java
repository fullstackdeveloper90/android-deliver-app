package com.mobileappsprn.alldealership;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mobileappsprn.alldealership.utilities.ApplicationVariables;
import com.mobileappsprn.alldealership.utilities.Utils;

public class QRScannerWebViewActivity extends Activity {

	Activity     		 activity;
	WebView 	 		 qrScanWebView;
	ImageView 	 		 imageview;
	Utils        		 utils;
	public String 		 UrlTitle="",isUrlPresent,type="",qrServerUrl,url;
	boolean     		 isFav; 
	Activity    		 act=QRScannerWebViewActivity.this;
	private Tracker mTracker;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		/*ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
*/
		setContentView(R.layout.qrscanner_webview);
		// Obtain the shared Tracker instance.
		ApplicationVariables application = (ApplicationVariables) getApplication();
		mTracker    =   application.getDefaultTracker();
		Log.v("FLOW", "CAME HERE : QRScannerWebViewActivity");
		activity=QRScannerWebViewActivity.this;
		
		Intent intent = getIntent();
		final String qrScanUrl = intent.getStringExtra("qrScanUrl");
		UrlTitle= intent.getStringExtra("title");
		type=intent.getStringExtra("type");
		qrServerUrl=intent.getStringExtra("serverUrl");
		
		setupActionBar();

		imageview=(ImageView)findViewById(R.id.imageview);
		TextView  textview=(TextView)findViewById(R.id.titleBarText);
		if(type!=null){
			if(type.equalsIgnoreCase("Fav"))
				textview.setText(UrlTitle);
			else
				textview.setText("Scanner");
		}
		
		utils=new Utils();		
		checkForUrl();		
	
		qrScanWebView = (WebView) findViewById(R.id.qrScannerWebView);

		if(Utils.checkInternetConnection(this)){
			//qrScanWebView.setWebViewClient(new WebViewClient());
			qrScanWebView.setWebViewClient(new WebBrowser());
			qrScanWebView.getSettings().setJavaScriptEnabled(true);
			qrScanWebView.getSettings().setPluginState(WebSettings.PluginState.ON);

			qrScanWebView.loadUrl(qrScanUrl);	
			//checkForUrl();	
		}

		else {
			Toast.makeText(getApplicationContext(), "Internet Not Present.Please Try Later.", Toast.LENGTH_SHORT).show();
			finish();
		}
		if(qrServerUrl!=null && !TextUtils.isEmpty(qrServerUrl))
		{
			if(qrServerUrl.contains("&u="))
			{
				url=qrServerUrl;
				url=url.replaceAll("&u=", "&u="+qrScanUrl);
				url=url.replaceAll("&type=", "&type=view");
			}
			new ScannerUrlServerUpdate().execute(url);
		}
		
		
		imageview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(isFav)
				{
					imageview.setImageResource(R.drawable.unfav);
					if(UrlTitle!=null)
						utils.removeQRurl(act, UrlTitle);

					isFav=false;
				}					
				else{
					
					isFav=true;
					imageview.setImageResource(R.drawable.fav);	
					
					if(UrlTitle!=null)
						utils.saveFavQRCodes(act, UrlTitle,qrScanUrl);	
					if(qrServerUrl!=null && !TextUtils.isEmpty(qrServerUrl))
					{
						if(qrServerUrl.contains("&u="))
						{
							url=qrServerUrl;
							url=url.replaceAll("&u=", "&u="+qrScanUrl);
							url=url.replaceAll("&type=", "&type=fav");
						}
						new ScannerUrlServerUpdate().execute(url);
					}
					
				}

			}
		});
	}


	private void checkForUrl()
	{
		isUrlPresent=utils.checkQRurl(act, UrlTitle);

		if(isUrlPresent!=null){
			//imageview.setVisibility(View.VISIBLE);
			imageview.setImageResource(R.drawable.fav);
			isFav=true;
		}
		else{
			imageview.setImageResource(R.drawable.unfav);
			isFav=false;
		}			
	}
	private class WebBrowser extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);
			
			
			UrlTitle=view.getTitle();
			checkForUrl();
			//Log.i("QRScannerWebViewActivity :", UrlTitle);
		}

	}
	private void setupActionBar()
	{
		/*ActionBar ab = getSupportActionBar();
		ab.setDisplayShowCustomEnabled(true);
		ab.setDisplayShowTitleEnabled(false);
		LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View	v = inflator.inflate(R.layout.actionbar_scanner, null);
		imageview=(ImageView)v.findViewById(R.id.imageview);
		TextView  textview=(TextView)v.findViewById(R.id.titleBarText);
		if(type!=null){
			if(type.equalsIgnoreCase("Fav"))		
				textview.setText(UrlTitle);	
			else
				textview.setText("Scanner");			
		}
		
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
	@Override
	protected void onResume() {	
		super.onResume();
		if(mTracker != null) {
			mTracker.setScreenName(getClass().getSimpleName());
			mTracker.send(new HitBuilders.ScreenViewBuilder().build());
		}
		/*ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);*/
	}
	private class ScannerUrlServerUpdate extends AsyncTask<String, String, String>{

		@Override
		protected String doInBackground(String... params)
		{
			Log.i("QRScannerWebViewActivity :","server qrScanUrl :"+params[0]);
			if (Utils.checkInternetConnection(activity)){
			
				 String scanUrl=params[0];
				
			    try {			    	
			    	HttpClient httpclient = new DefaultHttpClient();
					HttpResponse response = httpclient.execute(new HttpGet(scanUrl));
					HttpEntity entity = response.getEntity();
					
					
					 Log.v("server response : ", EntityUtils.toString(entity));					
			    } catch (Exception exception) {
			        Log.v("Exception", exception.getMessage());
			    }
			}
				
			return null;
		}
		
	}
  }
