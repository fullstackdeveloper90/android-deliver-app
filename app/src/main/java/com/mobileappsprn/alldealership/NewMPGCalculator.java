package com.mobileappsprn.alldealership;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mobileappsprn.alldealership.utilities.ApplicationVariables;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by sri on 25/07/16.
 */
public class NewMPGCalculator extends Activity {

    EditText dateEditText;
    EditText preOdoEditText; //mandatory field
    EditText gallonsEditText; //mandatory field

    EditText currentOdoEditText;
    EditText tripMilesEditText;

    TextView lastFilledUpAtValue;
    TextView mpgResultTextView;

    Button saveHistory, showHistory;

    private static final int DATE_DIALOG_ID = 1;
    private int year;
    private int month;
    private int day;
    public int year1, month1, day1, hour1, minute1;  // declare  the variables
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

    Map<String, String> carDetailsHMap;

    ArrayList<String> milesPerTank;
    public int mptValue;

    double prevVehMiles;
    double mpgResult;
    double tripMilesCalculated;

    String carDetailsArrayToLoad[];
    SharedPreferences historyPrefs;
    SharedPreferences.Editor editor;
    String savedTime;
    private Tracker mTracker;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.new_mpg_calculator);
        // Obtain the shared Tracker instance.
        ApplicationVariables application = (ApplicationVariables) getApplication();
        mTracker    =   application.getDefaultTracker();
        Log.v("FLOW", "CAME HERE : NewMPGCalculator");
		/*ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);*/

        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat dfSaved = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");

        String formattedDate = df.format(c.getTime());
        savedTime = dfSaved.format(c.getTime());


        carDetailsHMap = new TreeMap<String, String>();
        milesPerTank = new ArrayList<String>();

        pref = getApplicationContext().getSharedPreferences("ShockleyPref", 0);
        //boolean isPreferencePopulated = pref.getBoolean("isPreferencePopulated", false);

        prefKeyValues = getApplicationContext().getSharedPreferences("hashmap_shared_pref", Context.MODE_PRIVATE);

        selectedCarSharedPref = getApplicationContext().getSharedPreferences("selected_car_shared_pref",
                Context.MODE_PRIVATE);
        isSelectedCarPrefPopulated = selectedCarSharedPref.getBoolean("isSelectedCarPrefPopulated", false);

        historyPrefs = PreferenceManager.getDefaultSharedPreferences(NewMPGCalculator.this);

        mpgCalcPref = getApplicationContext().getSharedPreferences("mpg_calc_pref",
                Context.MODE_PRIVATE);
        mpgCalcPrefEditor = mpgCalcPref.edit();

        selectedCarIndex = selectedCarSharedPref.getInt("selectedCar", 0);

        prevVehMiles = Double.parseDouble(mpgCalcPref.getString("prevVehMiles", "0"));


        dateEditText = (EditText) findViewById(R.id.dateEditText);
        dateEditText.setText(formattedDate);

        preOdoEditText = (EditText) findViewById(R.id.preOdometerEditText);
        gallonsEditText = (EditText) findViewById(R.id.gallonsEditText);
        currentOdoEditText = (EditText) findViewById(R.id.currentOdometerEditText);
        tripMilesEditText = (EditText) findViewById(R.id.tripMilesEditText);
        mpgResultTextView = (TextView) findViewById(R.id.mpgResultTextView);
        lastFilledUpAtValue = (TextView) findViewById(R.id.lastFilledUpAtValue);

        saveHistory = (Button) findViewById(R.id.saveHistoryButton);
        showHistory = (Button) findViewById(R.id.showHistoryButton);

       // lastFilledUpAtValue.setText(String.valueOf(prevVehMiles) + " miles");

        lastFilledUpAtValue.setText(NumberFormat.getNumberInstance(Locale.US).format(prevVehMiles) + " miles");

        fillOdoMeterifAvail();

        @SuppressWarnings("unchecked")
        HashMap<String, String> map = (HashMap<String, String>) prefKeyValues.getAll();
        for (String keyString : map.keySet()) {
            String value = map.get(keyString);
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

        currentOdoEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if (!currentOdoEditText.getText().toString().trim().equals("")) {
                    tripMilesCalculated = Double.parseDouble(currentOdoEditText.getText().toString()) - Double.parseDouble(preOdoEditText.getText().toString());
                    //tripMilesEditText.setText(String.valueOf(tripMilesCalculated));
                    tripMilesEditText.setText(String.format("%.2f", tripMilesCalculated));
                } else {
                    tripMilesEditText.setText("");
                }

                return false;
            }
        });

        saveHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveHistory.setEnabled(false);
                saveHistory.setClickable(false);
                saveHistory.setBackgroundColor(Color.parseColor("#9fb9f7"));
                saveHistory();

            }
        });

        showHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(NewMPGCalculator.this, MPGHistory.class));

            }
        });

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
    public void onClickDatePicker(View view) {

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        showDialog(DATE_DIALOG_ID);
    }

    public void onClickCalculate(View view) {


        if (gallonsEditText.getText().toString().equals("")) {
            showAlert("Please enter gallons!");
            return;
        } else if (preOdoEditText.getText().toString().equals("")) {
            showAlert("Please enter previous odometer!");
            return;
        }

        if (!currentOdoEditText.getText().toString().equals("")) {
            mpgCalcPrefEditor.putString("prevVehMiles", currentOdoEditText.getText().toString());
            mpgCalcPrefEditor.commit();
        } else {
            showAlert("Please enter current odometer!");
            return;
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
                    / Double.parseDouble(gallonsEditText.getText().toString());
        else if (!currentOdoEditText.getText().toString().equals("") && !preOdoEditText.getText().toString().equals("")) {
            tripMilesCalculated = Double.parseDouble(currentOdoEditText.getText().toString()) - Double.parseDouble(preOdoEditText.getText().toString());
            mpgResult = tripMilesCalculated / Double.parseDouble(gallonsEditText.getText().toString());
            //tripMilesEditText.setText(String.valueOf(tripMilesCalculated));
            tripMilesEditText.setText(String.format("%.2f", tripMilesCalculated));

        }

        mpgResultTextView.setText(String.format("%.2f", mpgResult));
        saveHistory.setEnabled(true);
        saveHistory.setClickable(true);
        saveHistory.setBackgroundColor(Color.parseColor("#306EFF"));

    }

    public void showAlert(String alertMsg) {
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

    DatePickerDialog.OnDateSetListener myDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @SuppressLint("SimpleDateFormat")
        @Override
        public void onDateSet(DatePicker datePicker, int i, int j, int k) {
            try {
                year = i;
                month = j;
                day = k;

                updateDisplay();
                dateEditText.setText(currentDate);
            } catch (Exception e) {

            }
        }
    };

    public void updateDisplay() {
        currentDate = new StringBuilder().append(day).append("/")
                .append(month + 1).append("/").append(year).toString();
		/* Current date is the date selected from DatePickerDialog */
        Log.i("DATE", "Current Date" + currentDate);
    }


    private void saveHistory() {

        String latestRecord = "\t\t\t\t" + savedTime + "," + "\nDate: " + dateEditText.getText().toString() + "," + "Total Gallons: " + gallonsEditText.getText().toString() + "," + "Previous Odometer: " + preOdoEditText.getText().toString()
                + "," + "Current Odometer: " + currentOdoEditText.getText().toString() + "," + "Trip Miles: " + tripMilesEditText.getText().toString() + "," + "Calculated MPG: " + mpgResultTextView.getText().toString();

        String previousRecords = historyPrefs.getString("mpgHistoryData", "");
        String toSave = latestRecord + "*" + previousRecords;

        editor = historyPrefs.edit();
        editor.remove("mpgHistoryData");
        editor.putString("mpgHistoryData", toSave);
        editor.commit();

        Toast.makeText(NewMPGCalculator.this, "Record saved", Toast.LENGTH_SHORT).show();
    }

    private void fillOdoMeterifAvail() {
        try {
            String[] records = historyPrefs.getString("mpgHistoryData", "").split("\\*");
            if (records.length > 0) {
                String[] record = records[0].split("\\,");
                preOdoEditText.setText(record[4].replace("Current Odometer: ", ""));
            }
        } catch(ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.mpgcalculator, menu);
//		return true;
//	}

}
