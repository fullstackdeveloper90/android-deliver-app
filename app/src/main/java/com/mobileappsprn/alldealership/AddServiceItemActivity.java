package com.mobileappsprn.alldealership;

import java.util.Calendar;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mobileappsprn.alldealership.utilities.ApplicationVariables;


public class AddServiceItemActivity extends Activity implements View.OnClickListener{

	EditText serviceDateEditText;
	EditText mileageEditText;
	EditText serviceDescrition;

	String mileageString;
	String serviceDescString;
	String serviceDateString;

	private static final int DATE_DIALOG_ID = 1;
	private int year, month, day;

	private String currentDate;
	//private String resultDate;

	String serverUrl = "";
	
	String vin;
	private Tracker mTracker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		 getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		 
		setContentView(R.layout.add_service_item);
		// Obtain the shared Tracker instance.
		ApplicationVariables application = (ApplicationVariables) getApplication();
		mTracker    =   application.getDefaultTracker();
		Log.v("FLOW", "CAME HERE : AddServiceItemActivity");
		setupActionBar();
		 
		Intent intent = getIntent();
		vin = intent.getStringExtra("vin2");
		serverUrl = "http://www.mobileappsprn.com/custom/mobileappsprn/ctl/postService.aspx?a=4";// &sd=mDateTf&d=mDescTextView&v=vin&m=mMileageTf";

		serviceDateEditText = (EditText) findViewById(R.id.serviceDate);
		mileageEditText = (EditText) findViewById(R.id.mileage);
		serviceDescrition = (EditText) findViewById(R.id.service_description);

	}

	@Override
	protected void onResume() {
		super.onResume();
		if(mTracker != null) {
			mTracker.setScreenName(getClass().getSimpleName());
			mTracker.send(new HitBuilders.ScreenViewBuilder().build());
		}
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()){

		}
	}

	@SuppressWarnings("deprecation")
	public void onClickDatePicker(View view) {

		Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		showDialog(DATE_DIALOG_ID);
	}

	OnDateSetListener myDateSetListener = new OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker datePicker, int i, int j, int k) {
			try {
				year = i;
				month = j;
				day = k;

				updateDisplay();
				serviceDateEditText.setText(currentDate);
			} catch (Exception e) {

			}
		}

	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, myDateSetListener, year, month,
					day);
		}
		return null;
	}

	public void updateDisplay() {
		currentDate = new StringBuilder().append(month + 1).append("/")
				.append(day).append("/").append(year).toString(); // append(day)
		/* Current date is the date selected from DatePickerDialog */
	}

	public void onClickSaveServiceItems(View view) {
		if (serviceDateEditText.getText().toString().equals("")) {
			showAlert("Please add valid service date.");
			return;
			
		} else if (serviceDescrition.getText().toString().equals("")) {
			showAlert("Please add valid service description.");
			return;
		}
		
		serviceDateString = currentDate;
		// if (!mileageEditText.getTe)
		mileageString = mileageEditText.getText().toString();
		serviceDescString = serviceDescrition.getText().toString();
		new SendToServerAsync().execute();
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
	
	public class SendToServerAsync extends AsyncTask<Integer, String, Void> {

		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(AddServiceItemActivity.this);
			pDialog.setMessage("Processing Request...");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			try {
				sendCarDetailsToServer();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(Void result) {
			AlertDialog alertDialog = new AlertDialog.Builder(
					AddServiceItemActivity.this).create();

			// Setting Dialog Title
			alertDialog.setTitle("Success");

			// Setting Dialog Message
			alertDialog.setMessage("Service Item Added.");

			// Setting OK Button
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					pDialog.dismiss();
					dialog.dismiss();
					AddServiceItemActivity.this.finish();
				}
			});

			// Showing Alert Message
			alertDialog.show();
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Intent returnIntent = new Intent();
		setResult(RESULT_CANCELED, returnIntent);        
		finish();
	}
	
	private void setupActionBar() {
			
			/*ActionBar ab = getSupportActionBar();
			//ab.setBackgroundDrawable(new ColorDrawable(0x01060000));
	        ab.setDisplayShowCustomEnabled(true);
	        ab.setDisplayShowTitleEnabled(false);
	        LayoutInflater inflator = (LayoutInflater) this
	                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        View v = inflator.inflate(R.layout.add_service_titlebar, null);
	        
	        //Button save=(Button)findViewById(R.id.car_detail);
	        //TextView Title=(TextView)findViewById(R.id.addNewVehicle);	        
	      
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
	public void sendCarDetailsToServer() {

		String url = serverUrl+"&sd="+currentDate+"&d="+serviceDescString+"&v="+vin+"&m="+mileageString;
			
		
		Log.i("ServerUrl with Params: ", ""+url);
		
		/** HttpEntity entity = response.getEntity();
            String responseText = EntityUtils.toString(entity);
            **/
		
		//InputStream content = null;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = httpclient.execute(new HttpGet(url));
			HttpEntity entity = response.getEntity();
			String responseText = EntityUtils.toString(entity);
			
			Log.i("HttpGet responseMsg: ", ""+responseText);
		} catch (Exception e) {
			Log.i("[GET REQUEST]", "Network exception: "+e.getMessage());
		}

	}


//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.add_service_item, menu);
//		return true;
//	}

}
