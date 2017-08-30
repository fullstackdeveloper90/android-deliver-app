package com.mobileappsprn.alldealership.contactus;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mobileappsprn.alldealership.FetchJson;
import com.mobileappsprn.alldealership.GlobalState;
import com.mobileappsprn.alldealership.R;
import com.mobileappsprn.alldealership.entities.AppConstants;
import com.mobileappsprn.alldealership.utilities.ApplicationVariables;
import com.mobileappsprn.alldealership.utilities.LocationTracker;
import com.mobileappsprn.alldealership.utilities.Utils;


public class DirectionsActivity extends FragmentActivity implements OnClickListener, OnMapReadyCallback {
	
	// the labels for our tabs.
	private final String [] TAB_OPTIONS = {
			"Basic Info",
			"Show Map",
			"Directions"
	};
	
	// class objects
	//private ActionBar ab;
	private Geocoder gc;
	private GoogleMap map;
	private LatLng myLocation, dealershipLocation;
    private LocationTracker locationTracker;
    private Marker myLocationMarker, dealershipMarker;
    //not needed because location is pinged immediately, or at least gotten from beginning of app
    //private ProgressDialog progresser;
    private String myAddress;
    private String addr,phn;
    String lat="",lon="";
	private Tracker mTracker;
	Button map_btn;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        //set up UI
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        //get a location tracker and ping location early
        locationTracker = LocationTracker.getLocationTracker(this);
        locationTracker.pingLocation(this);
        gc = new Geocoder(this);
		Log.v("FLOW", "CAME HERE : DirectionsActivity");
        addr= GlobalState.dealershipAddress;
        phn=GlobalState.dealershipPhone;
		lat= GlobalState.dealershipLat;
		lon= GlobalState.dealershipLon;
        System.out.println("addr........."+addr+phn);
		System.out.println("lat........."+lat);
		System.out.println("lon........."+lon);

		setupActionBar(getIntent().getStringExtra("title"));

    }
    
    //helper UI methods
    /*
     * sets up the basic info tab UI
     */
    private void basic(){
    	setContentView(R.layout.basic_directions_layout);
		// Obtain the shared Tracker instance.
		ApplicationVariables application = (ApplicationVariables) getApplication();
		mTracker    =   application.getDefaultTracker();
    	
    	//setting up UI elements
    	TextView address = (TextView) findViewById(R.id.directions_address_textview);
    	TextView dealershipName = (TextView) findViewById(R.id.dealership_name_textview);
    	dealershipName.setText(getResources().getString(R.string.app_name));
    	address.setText(GlobalState.dealershipAddress);


    	TextView phone = (TextView) findViewById(R.id.directions_phone_textview);
    	phone.setText(GlobalState.dealershipPhone);
    	 System.out.println("addrbasic........." + addr + phn);

		TextView titleLabel = (TextView) findViewById(R.id.help_web_view_title);
		titleLabel.setText(getIntent().getStringExtra("title"));

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
     * sets up the map tab UI.
     */
    private void map(){
    	setContentView(R.layout.map_directions_layout);
    	TextView textViewDrillDownRootTitle = (TextView)findViewById(R.id.textViewDrillDownRootTitle);
		textViewDrillDownRootTitle.setText("Location");
    	//set up the view switching button for the map
    	Button viewSwitcher = (Button) findViewById(R.id.view_switcher_button);
    	viewSwitcher.setOnClickListener(this);
    	//viewSwitcher.setOnClickListener(new MyLocationButtonListener());
    	
    	try{
    		setupGoogleMap();
			Log.v("dflt", dealershipLocation + "-");
//    		map.moveCamera(CameraUpdateFactory.newLatLngZoom(dealershipLocation, 15.f));
    	}
    	catch(IOException e) {
    		Toast.makeText(DirectionsActivity.this, "There was an error when setting up the Google Map. Please check your internet connection and try again later.", Toast.LENGTH_SHORT)
       	 		 .show();
    		//viewSwitcher.setEnabled(false);
    		e.printStackTrace();
    	}
    }
   
    /* 
     * sets up the direction tab UI.
     */
    private void directions(){
    	setContentView(R.layout.basic_directions_layout);
    
    	//first we need to find our location, we'll use the GPS for better accuracy
    	
    	findLocation();
    	
    }
    
    //helper logic methods
    /*
     * finds and returns a list of directions
     */
    private void findDirections() throws NullPointerException, JSONException {
    	if(myLocation != null && GlobalState.dealershipAddress != null) {
    		String t1 = "104 Sheffield St, Silver Spring, MD 20910";
    		String t2 = "1401 Blair Mill Rd, Silver Spring, MD 20910";
    		
    		String directionsRequest = "https://maps.googleapis.com/maps/api/directions/json?" + 
						"origin=" + t1 +
						"&destination=" + t2 + 
						"&key=AIzaSyBtb7wGuY-GbsiT7ix3j_kXQ0RuO9Sh4VM";
    		directionsRequest = directionsRequest.replaceAll(" ", "+");
    		
     		/*String directionsRequest = "https://maps.googleapis.com/maps/api/directions/json?" + 
     											"origin=" + myAddress +
     											"&destination=" + GlobalState.dealershipAddress + 
     											"&key=AIzaSyBtb7wGuY-GbsiT7ix3j_kXQ0RuO9Sh4VM";
    		directionsRequest = directionsRequest.replaceAll(" ", "+"); */	

    		if( FetchJson.getJSONfromURL(directionsRequest) == null )
    			throw new NullPointerException();
    		JSONObject response = FetchJson.getJSONfromURL(directionsRequest);
    		JSONArray routes = response.getJSONArray("routes");
    		String [] directions = new String[routes.length()];
    		for(int i = 0; i < routes.length(); i++){
    			directions[i] = routes.getJSONObject(i).getString("summary");
    			Log.i("DIRECTIONS TAG", directions[i]);
    		}
    	}
    	 System.out.println("addr..finddir......."+addr+phn);
    }
    
    /*
     * finds the current location of the user, and will display a progress dialog until
     * the user's location is found.
     */
    private void findLocation(){
        //set the location to myLocation
        //myLocation = new LatLng(locationTracker.getLatitude(), locationTracker.getLongitude());
        //now that we have location we want to update the map, if possible
//        updateMyLocationMarker();
    }

    /*
     * Sets up the actionbar.
     */
	private void setupActionBar(String title) {
		/*ab = getSupportActionBar();
		ab.setDisplayShowCustomEnabled(true);
		ab.setDisplayShowTitleEnabled(false);*/
		 System.out.println("addr.actn........"+addr+phn);
		//only setup tab environment with an internet connection
		if(Utils.checkInternetConnection(this)) {
			//basic();
			Address a = null;
			try {
				if(GlobalState.dealershipAddress!=null){
					List<Address> addressList = gc.getFromLocationName(GlobalState.dealershipAddress, 1);
					if(addressList!=null && addressList.size()>0) {
						a = gc.getFromLocationName(GlobalState.dealershipAddress, 1).get(0);
						lat=String.valueOf(a.getLatitude());
						lon=String.valueOf(a.getLongitude());
						dealershipLocation = new LatLng(a.getLatitude(), a.getLongitude());

						Log.v("dflt","Map GeoCoded Data");
					}else{

						lat= GlobalState.dealershipLat;
						lon= GlobalState.dealershipLon;
						Log.v("dflt","addr : "+addr+" Getting from lat & lon :"+lat+" "+lon);
						dealershipLocation = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));

					}
					Log.v("Map","Map : "+map);
//					map.animateCamera(CameraUpdateFactory.newLatLngZoom(dealershipLocation, 15));
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NullPointerException e){
				e.printStackTrace();
			}

			//dealershipLocation = new LatLng(42.1035, -71.2570);
			if(a!=null){
				lat=String.valueOf(a.getLatitude());
				lon=String.valueOf(a.getLongitude());
			}
			map();
		    //setup tabs
			/*Tab basicInfoTab = ab.newTab();
			basicInfoTab.setText(TAB_OPTIONS[0]);
			basicInfoTab.setTabListener(new ActionBar.TabListener(){
	
				@Override
				public void onTabSelected(Tab tab, FragmentTransaction ft) {
					basic();
				}
				
				@Override
				public void onTabUnselected(Tab tab, FragmentTransaction ft) { }
	
				@Override
				public void onTabReselected(Tab tab, FragmentTransaction ft) { }
				
			});*/

	
			/*Tab mapTab = ab.newTab();
			mapTab.setText(TAB_OPTIONS[1]);
			mapTab.setTabListener(new ActionBar.TabListener(){
	
				@Override
				public void onTabSelected(Tab tab, FragmentTransaction ft) {
					map();
				}
				
				@Override
				public void onTabUnselected(Tab tab, FragmentTransaction ft) {
					// we want to remove the fragment so its id is taken out of the viewgroup and can be re-added later
					SupportMapFragment map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.directions_map);
					ft.remove(map);

				}
	
				@Override
				public void onTabReselected(Tab tab, FragmentTransaction ft) { }
				
			});*/
			
			//ab.addTab(basicInfoTab);
			//ab.addTab(mapTab);
			//ab.addTab(directionTab);
			
			//ab.selectTab(basicInfoTab);
			//ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		}
		else {
			//ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			//basic();

			map();
			Toast.makeText(getApplicationContext(), "Limited Features without an internet connection.", Toast.LENGTH_LONG)
			 .show();
		}
		
		//setting up action bar layout
		/*LayoutInflater inflator = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View v = inflator.inflate(R.layout.help_web_view_titlebar, null);
		TextView titleLabel = (TextView)v.findViewById(R.id.help_web_view_title);
		titleLabel.setText(title);
		
		ab.setCustomView(v);
		ab.setDisplayHomeAsUpEnabled(true);*/
	}
    
    /*
     * Instantiates the GoogleMap object and adds the dealership location.
     */
    private void setupGoogleMap() throws IOException {
        // We want to make sure we have an internet connection before attempting to load the map.
        if (Utils.checkInternetConnection(this)) {


			MapFragment mapFragment = (MapFragment) getFragmentManager()
					.findFragmentById(R.id.directions_map);

			mapFragment.getMapAsync(this);

            // Try to obtain the map from the SupportMapFragment.
           /*BS map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.directions_map))
            			.getMap();*/

			/*BS try {
				MapsInitializer.initialize(this);
			}catch (Exception e){
				e.printStackTrace();
			}
            // Check if we were successful in obtaining the map, and add the dealership marker which shows by default
            if (map != null) {
            	// find the dealership's location.
            	if(dealershipLocation!=null && GlobalState.dealershipAddress!=null){
            		 dealershipMarker = map.addMarker( new MarkerOptions()
						.position( dealershipLocation )
						//.title( "Dealership Location" )
						//.title(getResources().getString(R.string.app_name))
                            .title(GlobalState.locationName)
						.snippet( GlobalState.dealershipAddress ) );
            		 	dealershipMarker.showInfoWindow();
					map.animateCamera(CameraUpdateFactory.newLatLngZoom(dealershipLocation, 15.f));
            	}
            }*/
        }
        else
        	throw new IOException();
    }
 	
    /*
     * updates the user's address string
     */
    private void updateMyAddress() throws IOException {
    	//first lets reinitialize myAddress
    	myAddress = "";
    	
    	//now get the address from known location
    	Address address = gc.getFromLocation(myLocation.latitude, myLocation.longitude, 1).get(0);
    	myAddress = address.getAddressLine(0);
    	/*for(int i = 0; i < address.getMaxAddressLineIndex() && i < 2; i++)
    		myAddress += address.getAddressLine(i) + "\n";*/
    }
    
    /*
     * Updates the users current location, first checks to see if its already set.
     */
    private void updateMyLocationMarker() {
    	
    	try {
			updateMyAddress();
		} catch (IOException e) {
			Toast.makeText(this, "Immediate address could not be found.", Toast.LENGTH_SHORT).show();
		}
    	
    	//dont want to create a new marker if we dont need to.
    	if(myLocationMarker == null)
    		myLocationMarker = map.addMarker( new MarkerOptions()
        						.position( myLocation )
        						.title( "My Current Location" )
        						.snippet( myAddress ) );
    	
    	myLocationMarker.setVisible(true);
		myLocationMarker.showInfoWindow();
		
		Builder llb = LatLngBounds.builder();
		llb.include(myLocation);
		llb.include(dealershipLocation);
		
		map.animateCamera( CameraUpdateFactory.newLatLngBounds(llb.build(), 50) );
		
    }

	@Override
	public void onMapReady(GoogleMap googleMap) {
		map = googleMap;
		setUpMap();
	}

	private void setUpMap(){
		map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		//map.setMyLocationEnabled(true);
		map.setTrafficEnabled(true);
		//map.setIndoorEnabled(true);
		map.setBuildingsEnabled(true);
		map.getUiSettings().setZoomControlsEnabled(true);

		try {
			MapsInitializer.initialize(this);
		}catch (Exception e){
			e.printStackTrace();
		}
		// Check if we were successful in obtaining the map, and add the dealership marker which shows by default
		if (map != null) {
			// find the dealership's location.
			if(dealershipLocation!=null && GlobalState.dealershipAddress!=null){
				dealershipMarker = map.addMarker( new MarkerOptions()
						.position( dealershipLocation )
						//.title( "Dealership Location" )
						//.title(getResources().getString(R.string.app_name))
						.title(GlobalState.locationName)
						.snippet( GlobalState.dealershipAddress ) );
				dealershipMarker.showInfoWindow();
				map.animateCamera(CameraUpdateFactory.newLatLngZoom(dealershipLocation, 15.f));
			}
		}



	}


	//public methods
 /*   @Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	
	    case android.R.id.home:
	         finish();
	         break;
	
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	    return false;
	}
*/
    //nested classes, mainly event listeners
    private class MyLocationButtonListener implements OnClickListener {

		@Override
		public void onClick(View button) {
			//center camera on user's current location, we use the network b/c we dont need a ton of accuracy.
			if(myLocation == null)
				findLocation();
			else
				updateMyLocationMarker();
			
			//now we reconfigure the button
			((Button) button).setText("Go to Dealership Location");
			((Button) button).setOnClickListener(new DealershipLocationButtonListener());
		}
    	
    }

    private class DealershipLocationButtonListener implements OnClickListener {

		@Override
		public void onClick(View button) {
			//center camera on dealership's location
			map.animateCamera(CameraUpdateFactory.newLatLngZoom( dealershipLocation, 15.f ), 2000, null);
			
			//setting marker properties
			dealershipMarker.showInfoWindow();
			
			//now we reconfigure the button
			((Button) button).setText("Go to My Location");
			((Button) button).setOnClickListener(new MyLocationButtonListener());
		}
    	
    }

	@Override
	public void onClick(View v) {
	switch (v.getId()) {

	case R.id.view_switcher_button:
		try {
			Geocoder geocoder;
			List<Address> addresses;
			geocoder = new Geocoder(this, Locale.getDefault());

			addresses = geocoder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lon), 1);
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
}