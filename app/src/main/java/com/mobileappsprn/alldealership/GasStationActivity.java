package com.mobileappsprn.alldealership;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mobileappsprn.alldealership.adapters.GasStationAdapter;
import com.mobileappsprn.alldealership.entities.GasItems;
import com.mobileappsprn.alldealership.utilities.ApplicationVariables;
import com.mobileappsprn.alldealership.utilities.UiUtils;
import com.mobileappsprn.alldealership.utilities.Utils;
import com.mobileappsprn.alldealership.utilities.UtilsJson;

public class GasStationActivity extends FragmentActivity implements OnItemClickListener{
	ListView listView;
	ArrayList<GasItems> stationItems = new ArrayList<GasItems>();
    Context context;
	String vin,title,lat_str,lon_str;
	TextView textViewTitleBar/*,lat_lon,url,response*/;
	String feedUrl;
	LinearLayout debug_layout;
	JSONObject jObj;
	private Tracker mTracker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initialise();
		Log.v("FLOW", "CAME HERE : GasStationActivity");
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(mTracker != null) {
			mTracker.setScreenName(getClass().getSimpleName());
			mTracker.send(new HitBuilders.ScreenViewBuilder().build());
		}
	}

	private void initialise() {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.drilldown_root);
		// Obtain the shared Tracker instance.
		ApplicationVariables application = (ApplicationVariables) getApplication();
		mTracker    =   application.getDefaultTracker();
		setupActionBar();
		textViewTitleBar = (TextView)findViewById(R.id.textViewDrillDownRootTitle);
		//ActionBar ab = getSupportActionBar();
		//ab.setDisplayHomeAsUpEnabled(true);

		Intent intent = getIntent();
		feedUrl = intent.getStringExtra("feedUrl");

		int positionFeed1 = intent.getIntExtra("positionFeed1", -1); 

		int tagValue = intent.getIntExtra("tagValue1", -1);
		Log.i("tagValue", ""+tagValue);
		/*debug_layout=(LinearLayout)findViewById(R.id.debug_layout);
		debug_layout.setVisibility(View.VISIBLE);
		lat_lon=(TextView)findViewById(R.id.lat);
		url=(TextView)findViewById(R.id.url);
		response=(TextView)findViewById(R.id.response);*/
		title = intent.getStringExtra("TitleName");
		lat_str = intent.getStringExtra("Lat");
		lon_str = intent.getStringExtra("Lon");
		textViewTitleBar.setText(title);
		Log.i("Feed-url: ", "" + feedUrl);
		if (Utils.checkInternetConnection(this)) {
		downloadStationData();
		
		}
		else {
			showAlert("Internet not present! Please try later.");
		}
		
	

		//this.setTitle(GlobalState.menuItems.get(positionFeed1).getTitle());
		
	}
private void downloadStationData() {
	new AsyncTask<String, String, String>() {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		
				UiUtils.showProgressBar(GasStationActivity.this,"Working...");
		}

		@Override
		protected String doInBackground(String... params) {
			try {

				 jObj = UtilsJson.getJsonData(feedUrl);
				  System.out.println("staresult"+jObj);
					// Get the element that holds the items ( JSONArray )
	                if (jObj!=null) {
	                	JSONArray result = jObj.getJSONArray("stations");
	                	if (result!= null) {
							
						

	    				for (int i = 0; i < result.length(); i++) { // 3-times ,
	    															// itemsArray.length
	    															// == 3
	    															// (result.length
	    															// ==3)
	    					JSONObject c = result.getJSONObject(i);
	    					GasItems rootItems = new GasItems();
	    					if (c.has("reg_price"))
	    						rootItems.setReg_price(c.getString("reg_price"));
	    					if (c.has("station"))
	    					rootItems.setStation(c.getString("station"));
	    					if (c.has("distance"))
	    					rootItems.setDistance(c.getString("distance"));
	    					if (c.has("address"))
	    						rootItems.setAddress(c.getString("address"));
	    					if (c.has("lat"))
	    						rootItems.setLat(c.getString("lat"));	
	    					if (c.has("lng"))
	    						rootItems.setLng(c.getString("lng"));	
	    					

	    					stationItems.add(rootItems);
	    				}
			}
	           else {
	      					//Toast.makeText(getApplicationContext(), "No Station Present.Station is null", Toast.LENGTH_LONG).show();
	      				}
	                }
	                	else {
	    					//Toast.makeText(getApplicationContext(), "Json is null", Toast.LENGTH_LONG).show();
	    				}
			}
	                	catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			UiUtils.dismissProgressBar();
			//lat_lon.setText("LAT:"+lat_str+"::LNG:"+lon_str);
			//url.setText("URL:"+feedUrl);
			//response.setText("RESPONSE:"+jObj);
			//Toast.makeText(getApplicationContext(), "LAT:"+lat_str+"::LNG:"+lon_str+"URL:"+feedUrl, Toast.LENGTH_LONG).show();
			listView = (ListView) findViewById(R.id.listviewjson);
			final GasStationAdapter adapter = new GasStationAdapter(getApplicationContext(), stationItems);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					
					Intent intent = new Intent(GasStationActivity.this, StationMapActivity.class);
					intent.putExtra("lat", stationItems.get(position).getLat());
					intent.putExtra("lng", stationItems.get(position).getLng());
					intent.putExtra("add", stationItems.get(position).getAddress());
					
					startActivity(intent);
					
				}
				
			});
		}
			}.execute("");
			
			
		
	}
private void setupActionBar() {
		
		/*ActionBar ab = getSupportActionBar();
		//ab.setBackgroundDrawable(new ColorDrawable(0x01060000));
        ab.setDisplayShowCustomEnabled(true);
        ab.setDisplayShowTitleEnabled(false);
        LayoutInflater inflator = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.drilldown_title_bar, null);  
       
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
public void showAlert(String msg) {
	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
			this);
	
	alertDialogBuilder.setTitle("Alert!");
	
	alertDialogBuilder
			.setMessage(msg)
			.setNeutralButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int id) {
							
							dialog.dismiss();
							finish();
						}
					});

	AlertDialog alertDialog = alertDialogBuilder.create();
	alertDialog.show();
}
@Override
public void onItemClick(AdapterView<?> parent, View view,
		int position, long id) {
	
		Intent intent = new Intent(GasStationActivity.this, StationMapActivity.class);
		intent.putExtra("lat", stationItems.get(position).getLat());
		intent.putExtra("lng", stationItems.get(position).getLng());
		intent.putExtra("add", stationItems.get(position).getStation()+"-"+stationItems.get(position).getAddress());
		
		startActivity(intent);
	
}

}
