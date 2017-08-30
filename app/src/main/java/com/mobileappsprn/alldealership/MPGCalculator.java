package com.mobileappsprn.alldealership;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mobileappsprn.alldealership.utilities.ApplicationVariables;


public class MPGCalculator extends Activity {
	
	EditText dateEditText;
	EditText costEditText; //mandatory field
	EditText gallonsEditText; //mandatory field
	
	EditText vehMilesEditText; 
	EditText tripMilesEditText;
	
	TextView lastFilledUpAtValue;
	TextView mpgResultTextView;
	
	private static final int DATE_DIALOG_ID = 1;	
	private int year;
	private int month;
	private int day;
	public  int year1,month1,day1,hour1,minute1;  // declare  the variables
	private String currentDate;
	//private Context mContext = this;
	//private String TAG = "MPGCalculator";
	
	SharedPreferences pref;
	SharedPreferences selectedCarSharedPref;
	SharedPreferences prefKeyValues;
	
	SharedPreferences mpgCalcPref;
	SharedPreferences.Editor mpgCalcPrefEditor;
	
	int selectedCarIndex;
	boolean isSelectedCarPrefPopulated;
	
	Map <String, String> carDetailsHMap;
	
	ArrayList<String> milesPerTank;
	public int mptValue;
	
	double prevVehMiles;
	double mpgResult;
	double tripMilesCalculated;
	
	String carDetailsArrayToLoad[];
	private Tracker mTracker;
	
	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.mpg_calculator);
		// Obtain the shared Tracker instance.
		ApplicationVariables application = (ApplicationVariables) getApplication();
		mTracker    =   application.getDefaultTracker();
		Log.v("FLOW", "CAME HERE : MPGCalculator");
		/*ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);*/
			
		Calendar c = Calendar.getInstance();
		System.out.println("Current time => " + c.getTime());

		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		String formattedDate = df.format(c.getTime());
		
		carDetailsHMap = new TreeMap<String, String>();
		milesPerTank = new ArrayList<String>(); 
		
		pref = getApplicationContext().getSharedPreferences("ShockleyPref", 0); 
		//boolean isPreferencePopulated = pref.getBoolean("isPreferencePopulated", false);
		
		prefKeyValues = getApplicationContext().getSharedPreferences("hashmap_shared_pref", Context.MODE_PRIVATE);
		
		selectedCarSharedPref = getApplicationContext().getSharedPreferences("selected_car_shared_pref", 
				Context.MODE_PRIVATE);
		isSelectedCarPrefPopulated = selectedCarSharedPref.getBoolean("isSelectedCarPrefPopulated", false);
		
		mpgCalcPref = getApplicationContext().getSharedPreferences("mpg_calc_pref", 
				Context.MODE_PRIVATE);
		mpgCalcPrefEditor = mpgCalcPref.edit();
		
		selectedCarIndex = selectedCarSharedPref.getInt("selectedCar", 0);
		
		prevVehMiles = Double.parseDouble(mpgCalcPref.getString("prevVehMiles", "0"));
		
		
		
		dateEditText = (EditText) findViewById(R.id.dateEditText);
		dateEditText.setText(formattedDate);
		
		costEditText = (EditText) findViewById(R.id.costEditText);
		gallonsEditText = (EditText) findViewById(R.id.gallonsEditText);
		vehMilesEditText = (EditText) findViewById(R.id.milesEditText);
		tripMilesEditText = (EditText) findViewById(R.id.tripMilesEditText);
		mpgResultTextView = (TextView) findViewById(R.id.mpgResultTextView);
		lastFilledUpAtValue = (TextView) findViewById(R.id.lastFilledUpAtValue);
		
		lastFilledUpAtValue.setText(String.valueOf(prevVehMiles)+" miles");
		
		@SuppressWarnings("unchecked")
		HashMap<String, String> map= (HashMap<String, String>) prefKeyValues.getAll();
		for (String keyString : map.keySet()) {
			String value=map.get(keyString);
			carDetailsHMap.put(keyString, value);
			//Log.i("carDetailsHMap-key-MyCarActivity: ", ""+keyString);

		}

		for (String keyString : carDetailsHMap.keySet()) {
			String carDetailsStringToLoad = carDetailsHMap.get(keyString);
			carDetailsArrayToLoad = new String[12];
			carDetailsArrayToLoad = carDetailsStringToLoad.split(",");
			
			if (carDetailsArrayToLoad.length > 6)
				milesPerTank.add(carDetailsArrayToLoad[6]);
			else
				milesPerTank.add("");
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
	
	@SuppressWarnings("deprecation")
	public void onClickDatePicker (View view) {
		
		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		showDialog(DATE_DIALOG_ID);
	}
	
	public void onClickCalculate (View view) {
		
		if (costEditText.getText().toString().equals("") ) {
			showAlert ("Please enter cost!");	
			return;
		}
		
		else if (gallonsEditText.getText().toString().equals("")) {
			showAlert ("Please enter gallons!");
			return;
		}
		
		if (!vehMilesEditText.getText().toString().equals("")) {
			mpgCalcPrefEditor.putString("prevVehMiles", vehMilesEditText.getText().toString());
			mpgCalcPrefEditor.commit();			
		}
		
		try {
			if (milesPerTank.get(selectedCarIndex).toString().equals(""))
				mptValue = 500;
			else
				mptValue = Integer.parseInt(milesPerTank.get(selectedCarIndex).toString());
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		calculateMPG();
		
	}
	
	public void calculateMPG() {
		
		if (!tripMilesEditText.getText().toString().equals(""))
			mpgResult = Double.parseDouble(tripMilesEditText.getText().toString())
					/Double.parseDouble(gallonsEditText.getText().toString());
		else if ( prevVehMiles != 0 && !vehMilesEditText.getText().toString().equals("") ){
			tripMilesCalculated = Double.parseDouble(vehMilesEditText.getText().toString()) - prevVehMiles;
			mpgResult = tripMilesCalculated/Double.parseDouble(gallonsEditText.getText().toString());
		}
		
		mpgResultTextView.setText(String.valueOf(mpgResult));
		
	}
	
	public void showAlert (String alertMsg) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);
		
		alertDialogBuilder.setTitle("Alert!");
		
		alertDialogBuilder
				.setMessage(alertMsg)
				.setNeutralButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
								
								dialog.dismiss();
							}
						});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			DatePickerDialog dialog = new DatePickerDialog(this, myDateSetListener, year, month, day);
		    dialog.getDatePicker().setMaxDate(new Date().getTime());
		    return dialog;
		}
		return null;
		
	}
	
	OnDateSetListener myDateSetListener = new OnDateSetListener() {
		@SuppressLint("SimpleDateFormat")
		@Override
		public void onDateSet(DatePicker datePicker, int i, int j, int k) {
			try{
				year = i;
				month = j;
				day = k;
				
				updateDisplay();
				dateEditText.setText(currentDate);
			}catch(Exception e){

			}
		}
	};

	public void updateDisplay() {
		currentDate = new StringBuilder().append(day).append("/")
				.append(month + 1).append("/").append(year).toString();
		/* Current date is the date selected from DatePickerDialog */
		Log.i("DATE", "Current Date"+currentDate);
	}
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.mpgcalculator, menu);
//		return true;
//	}

}
