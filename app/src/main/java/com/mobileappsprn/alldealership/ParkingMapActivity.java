package com.mobileappsprn.alldealership;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mobileappsprn.alldealership.utilities.ApplicationVariables;
import com.mobileappsprn.alldealership.utilities.GPSTracker;
import com.mobileappsprn.alldealership.utilities.LocationTracker;
import com.mobileappsprn.alldealership.utilities.LocationUpdateReceiver;

public class ParkingMapActivity extends FragmentActivity implements OnClickListener, OnMarkerDragListener, LocationUpdateReceiver, OnMapReadyCallback {
    //TextView textViewTitleBar;
    Button btn;
    //Button btn_set;
    GoogleMap mMap;
    String lat, lng, add;
    double parkLat, parkLng, curLat, curLng;
    Button btn_hybrid, btn_standard, btn_satelite;
    LocationTracker locationTracker;
    float pixelMin, dpMin, closeViewDist, distance;
    Marker marker;
    SharedPreferences parkPrefs;
    String date;
    LinearLayout layout_mapType;
    SimpleDateFormat dfDate;
    boolean parkInstance;
    Calendar cal;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_directions_layout);
        // Obtain the shared Tracker instance.
        ApplicationVariables application = (ApplicationVariables) getApplication();
        mTracker    =   application.getDefaultTracker();
        setupActionBar();
        Log.v("FLOW", "CAME HERE : ParkingMapActivity");
        //textViewTitleBar = (TextView)findViewById(R.id.textViewDrillDownRootTitle);
        //textViewTitleBar.setText("Find My Car");
        //btn_set=(Button)findViewById(R.id.park_set);
        layout_mapType = (LinearLayout) findViewById(R.id.layout_maptype);
        //btn_set.setText("Set");
        btn = (Button) findViewById(R.id.view_switcher_button);
        btn.setVisibility(View.GONE);
        layout_mapType.setVisibility(View.VISIBLE);
        btn_satelite = (Button) findViewById(R.id.button_satelite);
        btn_hybrid = (Button) findViewById(R.id.button_hybrid);
        btn_standard = (Button) findViewById(R.id.button_standerd);

        btn_hybrid.setOnClickListener(this);
        btn_standard.setOnClickListener(this);
        btn_satelite.setOnClickListener(this);

        dfDate = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
        cal = Calendar.getInstance();
        date = dfDate.format(Calendar.getInstance().getTime());
        System.out.println("date" + date);
        /*try {
			Date currentDate = dfDate.parse(dfDate.format(cal.getTime()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/


        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.directions_map);
        mapFragment.getMapAsync(this);

		/*BS SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.directions_map);
	    mMap = fm.getMap();*/


       //BS mMap.setMyLocationEnabled(true);

        parkPrefs = this.getSharedPreferences("CARPARK_PREFS", Activity.MODE_PRIVATE);

        screenSizeDiagnostics();
        initializeLocationAndDistance();

        //btn_set.setOnClickListener(this);

        // Getting Google Play availability status
      //BS  int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        // Showing status
        /*BS if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        } else {
            startParkingInfo();
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mTracker != null) {
            mTracker.setScreenName(getClass().getSimpleName());
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
        //BS setUpMapBlueDot();
    }

    protected void onPause() {
        super.onPause();
        locationTracker.stopUpdates();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpMap();
    }

    private void setUpMap() {
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //map.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(true);
        //mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.setOnMarkerDragListener(this);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        } else {
            startParkingInfo();
        }

        setUpMapBlueDot();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.park_set:
                /*if (btn_set.getText().toString().equalsIgnoreCase("set")) {
                    btn_set.setText("Clear");
                    locationTracker.stopUpdates();
                    date= dfDate.format(Calendar.getInstance().getTime());
                    updateParkingLocation();
                    placeParkingMarker();
                    saveParkingLocation();
                }
                else {
                    Log.i("ParkingMapActivity", "STARTING LOCATION UPDATES");
                    locationTracker.updateLocationUntilStop(this);
                    marker.remove();
                    maxZoomMap();
                    btn_set.setText("Set");
                    date= dfDate.format(Calendar.getInstance().getTime());
                    SharedPreferences.Editor editor = parkPrefs.edit();
                    editor.clear();
                    editor.commit();
                }*/
                break;
            case R.id.button_satelite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.button_hybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.button_standerd:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;

            default:
                break;
        }
    }

    //sets pixelMin and dp min, the dimensions of the screen's smaller side
    private void screenSizeDiagnostics() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        if (dpWidth < dpHeight) {
            pixelMin = displayMetrics.widthPixels;
            dpMin = dpWidth;
        } else {
            pixelMin = displayMetrics.heightPixels;
            dpMin = dpHeight;
        }
        //the maximum distance between car and device in which closeZoom is applicable
        closeViewDist = (float) (dpMin / 360 * 15);
    }

    private void initializeLocationAndDistance() {
        locationTracker = LocationTracker.getLocationTracker(this);
        if (locationTracker.canGetLocation()) {
            locationTracker.pingLocation(this);
            curLat = locationTracker.getLatitude();
            curLng = locationTracker.getLongitude();
            parkLat = curLat;
            parkLng = curLng;
        } else {
            locationTracker.showSettingsAlert();
        }
        distance = 0;
    }

    private void startParkingInfo() {
        // Google Play Services are available
        parkInstance = parkPrefs.getBoolean("parkingSaved", false);
        setUpMapBlueDot();
        //if parking location has been saved
        if (parkInstance) {
            lat = parkPrefs.getString("lat_str", null);
            lng = parkPrefs.getString("lng_str", null);
            parkLat = Double.parseDouble(lat);
            parkLng = Double.parseDouble(lng);
            date = parkPrefs.getString("parkDate", "");
            //btn_set.setText("Clear");
            placeParkingMarker();
            //if parking location has not been saved, update location
        } else {
            Log.i("ParkingMapActivity", "STARTING LOCATION UPDATES");
            locationTracker.updateLocationUntilStop(this);
        }
    }


    //sets up the map's display of the user's current location
    private void setUpMapBlueDot() {
        // Enabling MyLocation Layer of Google Map
        // this is the blue dot that you see on the parking map
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //initializes current location
        checkDistance();
    }

    //updates current location
	@Override
    public void onLocationChangedCallback() {
        curLat = locationTracker.getLatitude();
        curLng = locationTracker.getLongitude();
        checkDistance();
    }

    private void updateParkingLocation() {
        parkLat = curLat;
        parkLng = curLng;
    }

	private void placeParkingMarker() {
		MarkerOptions markerOpt = new MarkerOptions().position(new LatLng(parkLat, parkLng)).title("My Car"+"\n"+date).visible(true).draggable(true);
//                                                     .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher));
        Toast.makeText(this, "Press and hold red marker to drag to exact desired location", Toast.LENGTH_LONG).show();
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		// Check if we were successful in obtaining the map.
		
		//mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).draggable(true));
		marker = mMap.addMarker(markerOpt);
		marker.showInfoWindow();
		//marker.isVisible();

        checkDistance();
	}

    //checks if distance is such that the map should be resized
    private void checkDistance() {
        /*if (btn_set.getText().toString().equalsIgnoreCase("set")) {
            maxZoomMap();
        } else {*/
            distance = distanceBetween(curLat, curLng, parkLat, parkLng);

        Log.i("dist betn ", "ZZZZZZZZZZZ  DISTANCE : "+distance +"           "+closeViewDist );

            if (distance < closeViewDist) {
                maxZoomMap();
            } else {
                dynamicMapResize();
            }
        //}
    }

    private float distanceBetween(double lat1, double lng1, double lat2, double lng2) {
        double R = 6371; // km
        double dLat = (lat2-lat1)*Math.PI/180;
        double dLon = (lng1-lng2)*Math.PI/180;
        lat1 = lat1*Math.PI/180;
        lat2 = lat2*Math.PI/180;

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c * 1000;

        Log.i("dist betn ", "" + d);

        return (float) d;
    }

    private void maxZoomMap() {
        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(curLat, curLng);
        // Showing the current location in Google Map

        Log.i("dist betn ", "ZZZZZZZZZZZ     MAX ZOOM MAP : " );

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        // Zoom in the Google Map
        mMap.animateCamera(CameraUpdateFactory.zoomTo(20));
    }

    private void dynamicMapResize() {
        float earthCircumference = 26367426.56f; //40075000;
        //the number of DPs the earth takes up when zoomLevel == 0
        float dpConstant = 256;
        //formula to find exact zoom level

        Log.i("dist betn ", "ZZZZZZZZZZZ     DYNAMIC ZOOM MAP : " );


        double zoomLevel = Math.log(earthCircumference/(distance*2) * dpMin/dpConstant)/Math.log(2);
        //add margins to that level?

        Log.i("dynamic resize:", "zoomlevel: " + zoomLevel);

        mMap.animateCamera(CameraUpdateFactory.zoomTo((float)zoomLevel));
    }

    //todo: delete this function
//    private void zoomTests() {
//        float kLat = 39.0997f, kLng = -94.5783f;
//        Location kcity = new Location("point B");
//        kcity.setLatitude(kLat);
//        kcity.setLongitude(kLng);
//        distance = currentLocation.distanceTo(kcity);
//        Log.i("parking map:", "distance from boston to Kansas City: " + distance);
//
//        MarkerOptions markerOpt2 = new MarkerOptions().position(new LatLng(kLat, kLng));
//        mMap.addMarker(markerOpt2);
//
//
//        float earthCircumference = 26367426.56f; //40075000;
//        //the number of DPs the earth takes up when zoomLevel == 0
//        float dpConstant = 256;
//        //formula to find exact zoom level
//        double zoomLevel = Math.log(earthCircumference/(distance*2) * dpMin/dpConstant)/Math.log(2);
//        //add margins to that level?
//
//        mMap.animateCamera(CameraUpdateFactory.zoomTo((float)zoomLevel));
//        Log.i("parking map:", "EC: " + earthCircumference + " distance: " + distance + " dpMin: " + dpMin);
//        Log.i("parking map:", "kmRatio: " + earthCircumference/distance + " dpRatio: " + dpMin/dpConstant);
//        Log.i("parking map:", "log test: " + Math.log(13)/Math.log(2));
//        Log.i("parking map:", "zoomLevel: " + zoomLevel);
//    }

	private void setupActionBar() {
		/*ActionBar ab = getSupportActionBar();
		//ab.setBackgroundDrawable(new ColorDrawable(0x01060000));
        ab.setDisplayShowCustomEnabled(true);
        ab.setDisplayShowTitleEnabled(false);
        LayoutInflater inflator = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.park_where_titlebar, null);  
       
        ab.setCustomView(v);        
		ab.setDisplayHomeAsUpEnabled(true);*/
		
	}
	/*@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			if (btn_set.getText().toString().equalsIgnoreCase("clear")) {
				saveParkingLocation();
				finish();
			}
			else {
				finish();
			}
			
			break;

		default:
			return super.onOptionsItemSelected(item);
		}
		return false;
	}*/




	@Override
	public void onMarkerDragStart(Marker arg0) {
		// TODO Auto-generated method stub
		arg0.setTitle("");
	}
	
	@Override
	public void onMarkerDragEnd(Marker arg0) {
		LatLng position = arg0.getPosition();
		double lat = position.latitude;
		double lan= position.longitude;
		parkLat = position.latitude;
		parkLng = position.longitude;
		Toast.makeText(getApplicationContext(), "dragged"+lat+lan, Toast.LENGTH_SHORT).show();
	//	date1= dfDate.format(Calendar.getInstance().getTime());
		//parkInstance=false;
		//arg0.setTitle("My Car"+"\n"+date1);
		//date=date1;
	}
	@Override
	public void onMarkerDrag(Marker arg0) {
		LatLng pos = arg0.getPosition();
		 
        // Updating the infowindow contents with the new marker coordinates
		date= dfDate.format(Calendar.getInstance().getTime());
		//parkInstance=false;
		arg0.setTitle("My Car"+"\n"+date);
		//date=date1;

        // Updating the infowindow for the user
		arg0.showInfoWindow();

	}
	
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		/*if (btn_set.getText().toString().equalsIgnoreCase("clear")) {
            saveParkingLocation();
		}*/
		
	}
	private void saveParkingLocation() {
		 SharedPreferences.Editor editor = parkPrefs.edit();
		 String lat_str=String.valueOf(parkLat);
		 String lng_str=String.valueOf(parkLng);
		 editor.putBoolean("parkingSaved", true);
		 editor.putString("lat_str", lat_str);
		 editor.putString("lng_str", lng_str);
		 editor.putString("parkDate", date);
		 editor.commit();
		 
	}

}
