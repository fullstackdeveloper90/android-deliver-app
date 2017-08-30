package com.mobileappsprn.alldealership;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mobileappsprn.alldealership.utilities.ApplicationVariables;

public class StationMapActivity extends FragmentActivity implements OnClickListener, OnMapReadyCallback {
	TextView textViewTitleBar;
	Button btn;
	GoogleMap mMap;
	String lat,lng,add;
	private Tracker mTracker;
	Double cur_lat,cur_lng;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.map_directions_layout);
		// Obtain the shared Tracker instance.
		ApplicationVariables application = (ApplicationVariables) getApplication();
		mTracker    =   application.getDefaultTracker();

		setupActionBar();
		textViewTitleBar = (TextView)findViewById(R.id.textViewDrillDownRootTitle);
		textViewTitleBar.setText("Directions");
		btn=(Button)findViewById(R.id.view_switcher_button);
		btn.setOnClickListener(this);
		lat=getIntent().getStringExtra("lat");
		lng=getIntent().getStringExtra("lng");
		add=getIntent().getStringExtra("add");
		cur_lat = Double.parseDouble(lat);
		Log.v("FLOW", "CAME HERE : StationMapActivity"+lat+" "+lng);
		cur_lng = Double.parseDouble(lng);
		setUpMapIfNeeded();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(mTracker != null) {
			mTracker.setScreenName(getClass().getSimpleName());
			mTracker.send(new HitBuilders.ScreenViewBuilder().build());
		}
	}

	private void setUpMapIfNeeded() {
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.

			MapFragment mapFragment = (MapFragment) getFragmentManager()
					.findFragmentById(R.id.directions_map);

			mapFragment.getMapAsync(this);

			/*BS mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.directions_map))
        			.getMap();*/



			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				setUpMap();

			}
		}
		
	}

	public void onClickAddServiceItem(View v){

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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.view_switcher_button:
			try {
				Geocoder geocoder;
				List<Address> addresses;
				geocoder = new Geocoder(this, Locale.getDefault());
				
					addresses = geocoder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lng), 1);
					String address = addresses.get(0).getAddressLine(0);
					String city = addresses.get(0).getAddressLine(1);
					String country = addresses.get(0).getAddressLine(2);
					Intent intent = new Intent(Intent.ACTION_VIEW,
							Uri.parse("google.navigation:q="+address+","+city+","+country));
					startActivity(intent);
					/*Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
						    Uri.parse("http://maps.google.com/maps?saddr="+lat+","+lng+"&daddr="+lat+","+lng));
						startActivity(intent);*/
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			break;

		default:
			break;
		}
		
		
	}


	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
		//setUpMapIfNeeded();
		setUpMap();
	}

	/*private void setUpMap(){
		map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		//map.setMyLocationEnabled(true);
		map.setTrafficEnabled(true);
		map.setIndoorEnabled(true);
		map.setBuildingsEnabled(true);
		map.getUiSettings().setZoomControlsEnabled(true);
	}*/

	private void setUpMap() {
		mMap.addMarker(new MarkerOptions()
				.position(new LatLng(cur_lat,cur_lng))
				.title(add));
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
				cur_lat, cur_lng), 8));
		mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 200, null);

	}
}
