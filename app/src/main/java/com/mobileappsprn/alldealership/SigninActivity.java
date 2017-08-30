package com.mobileappsprn.alldealership;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobileappsprn.alldealership.entities.AppConstants;
import com.mobileappsprn.alldealership.utilities.PasswordValidator;
import com.mobileappsprn.alldealership.utilities.Utils;

public class SigninActivity extends Activity {

	public static final String TAG = "SigninActivity";
	EditText emailEditText;
	EditText pwdEditText;
	EditText oldPwdEditText;
	EditText newPwdEditText;
	EditText verifyPwdEditText;
	
	Button submitBtn;
	Button forgotPwdBtn;

    LinearLayout linearLayout;
    //ImageView imageView;
    RelativeLayout relativeLayout;

	int intExtraTag;
	SharedPreferences mypref;
	String dflId;
	SharedPreferences prefKeyValues;
	Editor prefKeyValuesEditor;
	
	SharedPreferences pref;
	Editor prefsEditor;
	
	Map<String, String> carDetailsHMap;
	public static ArrayList<String> carNameList;
	ArrayList<Bitmap> bitmapList;

	TextView titleBarText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		Log.v("FLOW", "CAME HERE : SigninActivity");
		/*ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);*/
		
		intExtraTag = getIntent().getIntExtra("tag", -1);

		Log.v("Signin Activity", "Here Extra Tag : "+intExtraTag);

		switch (intExtraTag) {
			case AppConstants.RETRIEVE_VEHICLES_VIA_EMAIL:
                if (GlobalState.isNewMenu)
                {
					Log.v("Sign In Activity","Here 1");
					setContentView(R.layout.new_signin_retrieve_veh_activity);
                }
                else
                {
					Log.v("Sign In Activity","Here 2");
                    setContentView(R.layout.signin_retrieve_veh_activity);
                }
				setupActionBar();
				break;
			case AppConstants.CHANGE_PASSWORD:

                if (GlobalState.isNewMenu)
                {
					Log.v("Sign In Activity","Here 3");
                    setContentView(R.layout.new_change_password_layout);
                }
                else
                {
					Log.v("Sign In Activity","Here 4");
                    setContentView(R.layout.change_password_layout);
                }
				setupActionBar();
				break;
			case AppConstants.FORGOT_PASSWORD:

                if (GlobalState.isNewMenu)
                {
					Log.v("Sign In Activity","Here 5");
                    setContentView(R.layout.new_forgot_pwd_layout);
                }
                else
                {
					Log.v("Sign In Activity","Here 6");
                    setContentView(R.layout.forgot_pwd_layout);
                }
				setupActionBar();
				break;
			
		}
		
		initializeInstanceFields();
		initializeViews();

	}
   /* private void setBackground()
    {
        linearLayout = (LinearLayout) findViewById(R.id.linearlay);

        if (GlobalState.isNewSubMenu)
        {
            // imageView.setBackground(getResources().getDrawable(R.drawable.logobg));
            //todo:add dealership_bg image!
            //linearLayout.setBackground(getResources().getDrawable(R.drawable.dealership_bg));
        }
        else
        {
            linearLayout.setBackground(getResources().getDrawable(R.drawable.background));
        }
    }*/

	
	private void setupActionBar() {
		titleBarText = (TextView) findViewById(R.id.titleBarText);
		/*ActionBar ab = getSupportActionBar();
        ab.setDisplayShowCustomEnabled(true);
        ab.setDisplayShowTitleEnabled(false);
        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);*/
        
        View v = null;
        
        switch (intExtraTag) {
		case AppConstants.RETRIEVE_VEHICLES_VIA_EMAIL:
			titleBarText.setText("Sign In");
			break;

		case AppConstants.CHANGE_PASSWORD:
			titleBarText.setText("Change Password");
			break;

		case AppConstants.FORGOT_PASSWORD:
			titleBarText.setText("Forgot Password");
        	break;
		
        }
        
       // ab.setCustomView(v);
       
		//ab.setDisplayHomeAsUpEnabled(true);
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
//		/*ActionBar ab = getSupportActionBar();
//		ab.setDisplayHomeAsUpEnabled(true);*/
	}

	public void initializeInstanceFields() {
		
		mypref = getApplicationContext().getSharedPreferences("HomePrefs", Activity.MODE_PRIVATE);
		dflId = mypref.getString("dfl_id", "");
		
		prefKeyValues = getApplicationContext().getSharedPreferences("hashmap_shared_pref", Context.MODE_PRIVATE);
		prefKeyValuesEditor = prefKeyValues.edit();
		
		pref = getApplicationContext().getSharedPreferences("ShockleyPref", 0);
		prefsEditor = pref.edit();

		if (mypref.getBoolean("isItemSelected", false)) {
            ImageView image = (ImageView) findViewById(R.id.logo);
			int position = mypref.getInt("Itemposition", 0);
			Bitmap bitmap = Utils.getBitmapFromAsset("App-Specific-Icons/"+GlobalState.homeMenuItems.get(position).getLogoURL(), this);

			Log.v("SignIn Activity", "IV BM : "+image+"    "+bitmap);

			image.setImageBitmap(bitmap);
		}

        ImageView image = (ImageView) findViewById(R.id.logo);
		image.setImageDrawable(getResources().getDrawable(R.drawable.logo_hd));
		
		carDetailsHMap = new TreeMap<String, String>();
		carNameList = new ArrayList<String>();
		bitmapList = new ArrayList<Bitmap>();
		
		boolean isPreferencePopulated = pref.getBoolean("isPreferencePopulated", false);
		
		if (isPreferencePopulated) {
			@SuppressWarnings("unchecked")
			HashMap<String, String> map = (HashMap<String, String>) prefKeyValues.getAll();
			// int prefHMapSize = map.size();
			for (String keyString : map.keySet()) {
				String value = map.get(keyString);
				carDetailsHMap.put(keyString, value);
				Log.i("CarDetails-Act-carDetailsHMap-key: ", "" + keyString);
			}
		}
	}
	
	public void initializeViews() {
		switch (intExtraTag) {
			case AppConstants.RETRIEVE_VEHICLES_VIA_EMAIL:
				emailEditText = (EditText) findViewById(R.id.emailEditText);
				pwdEditText = (EditText) findViewById(R.id.pwdEditText);
				submitBtn = (Button) findViewById(R.id.submitBtn);
				forgotPwdBtn = (Button) findViewById(R.id.forgotPwdBtn);
				break;
			case AppConstants.CHANGE_PASSWORD:
				emailEditText = (EditText) findViewById(R.id.emailEditText);
				oldPwdEditText = (EditText) findViewById(R.id.oldPassword);
				newPwdEditText = (EditText) findViewById(R.id.newPassword);
				verifyPwdEditText = (EditText) findViewById(R.id.verifyPassword);
				submitBtn = (Button) findViewById(R.id.submitBtn);
				break;
			case AppConstants.FORGOT_PASSWORD:
				emailEditText = (EditText) findViewById(R.id.emailEditText);
				submitBtn = (Button) findViewById(R.id.submitBtn);
				break;
				
		}
	}
	
	public void onClickSubmitBtn(View view) {
		Log.i(TAG, "intExtraTag: " +intExtraTag);
		
		switch (intExtraTag) {
			
			case AppConstants.RETRIEVE_VEHICLES_VIA_EMAIL:
				retrieveVehViaEmail();
				break;
				
			case AppConstants.CHANGE_PASSWORD:
				changePassword();				
				break;
				
			case AppConstants.FORGOT_PASSWORD:
				forgotPassword();
				break;
		}

	}

	public void onClickForgotPwd(View view) {
		Intent intent = new Intent(SigninActivity.this, SigninActivity.class);
		intent.putExtra("tag", AppConstants.FORGOT_PASSWORD);
		startActivity(intent);
		finish();
	}
	
	boolean isEmailValid(CharSequence email) {
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}
	
	boolean isPwdValid(String pwd) {
		
		PasswordValidator pwdValidator = new PasswordValidator();
		return pwdValidator.validate(pwd);
		
	}

	class FetchJSONAsync extends AsyncTask<String, String, Void> {

		private ProgressDialog pDialog;
		String responseJSONString;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(SigninActivity.this);
			pDialog.setMessage("Processing Request...");
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(String... urlWithParam) {
			// TODO Auto-generated method stub
			try {

				urlWithParam[0] = urlWithParam[0] + "&a=" + GlobalState.serverId + "&DFLID=" + GlobalState.dealershipId;
				urlWithParam[0] = urlWithParam[0] + "&u=" + getUniqueDeviceId() + "&k=" + Utils.getRegistrationId(SigninActivity.this) + "&dn=" + getDeviceName();

				Log.i("urlWithParam: ", "urlWithParam: " + urlWithParam[0]);

				try {
					HttpClient httpclient = new DefaultHttpClient();
					HttpResponse response = httpclient.execute(new HttpGet(urlWithParam[0].replace(" ","%20")));
					HttpEntity entity = response.getEntity();
					responseJSONString = EntityUtils.toString(entity);

					Log.i("HttpGet responseMsg: ", "" + responseJSONString);

				} catch (Exception e) {
					Log.i("[GET REQUEST]", "Network exception: " + e.getMessage());
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (pDialog != null && pDialog.isShowing())
				pDialog.dismiss();
			
			try {
				parseResponseJSON(responseJSONString);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			/*Intent intent = new Intent(SigninActivity.this, MyCarActivity.class);
			intent.putExtra("tag", AppConstants.MY_CAR);
			startActivity(intent);
			finish();*/
		}
	}
	
	public String prepareCarDetailString(ArrayList<String> carDetailsList) {
		String[] carDetailsArray = new String[carDetailsList.size()];
		StringBuilder strbuildercarDetails = new StringBuilder();

		for (int i = 0; i < carDetailsList.size(); i++) {
			carDetailsArray[i] = carDetailsList.get(i);
		}

		for (int i = 0; i < carDetailsList.size(); i++) {
			strbuildercarDetails.append(carDetailsArray[i]).append(",");
		}
		
		return strbuildercarDetails.toString();
		//populateCarDetailsHashMap(carDetailsList, strbuildercarDetails);
	}
	
	public void populateCarDetailsHashMap(ArrayList<String> carNameList, ArrayList<String> carDetailsStringList) {
		
		for (int i = 0; i < carNameList.size(); i++) {
			carDetailsHMap.put(i + carNameList.get(i), carDetailsStringList.get(i)); // carDetailsList.get(0) // ==>// name of the car--as key.
		}
	}
	
	public void saveCarsNamesSharedPref() {
		String[] carNameArray = new String[carDetailsHMap.size()];

		int i = 0, k = 0;
		for (String keyString : carDetailsHMap.keySet()) {
			carNameArray[i++] = keyString;
			Log.i("carNameArray:[i] ", "" + carNameArray[k++]);
		}
		
		StringBuilder strbuildercarNames = new StringBuilder();
		
		for (int j = 0; j < carNameArray.length; j++) {
			strbuildercarNames.append(carNameArray[j]).append(",");
		}

		prefsEditor.clear();
		prefsEditor.putString(AppConstants.CAR_NAME_PREF,
				strbuildercarNames.toString());
		prefsEditor.putBoolean("isPreferencePopulated", true);
		prefsEditor.commit();
	}
	
	public void saveCarHMapSharedPref() {
		for (String keyString : carDetailsHMap.keySet()) {
			prefKeyValuesEditor.putString(keyString,
					carDetailsHMap.get(keyString));
			Log.i("CarDetailsAct- carDetailsHMap:", "" + keyString);
		}
		
		prefKeyValuesEditor.commit();
	}

	public void parseResponseJSON(String jsonString) throws JSONException {

		try {

			Log.i("ERROR TAG", jsonString + "");
			JSONObject jsonObject = new JSONObject(jsonString);
			String response = jsonObject.getString("response");
			Log.i(TAG, "response: " + response);

			//temporary fix
			if (response.equals("passwordreset")) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						this);

				alertDialogBuilder.setTitle("Password Information");

				alertDialogBuilder
						.setMessage(jsonObject.getString("message"))
						.setNeutralButton("Ok",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
														int id) {

										dialog.dismiss();
									}
								});

				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			} else if (response.equals("success")) {
				JSONArray jsonItemsArray = jsonObject.getJSONArray("Items");
				ArrayList<String> carDetailsStringList = new ArrayList<String>();
				ArrayList<String> carNameList = new ArrayList<String>();

				for (int i = 0; i < jsonItemsArray.length(); i++) {
					ArrayList<String> retrievedCarDetailsList = new ArrayList<String>();
					JSONObject jsonObj = jsonItemsArray.getJSONObject(i);
					retrievedCarDetailsList.add(jsonObj.getString("Name"));
					retrievedCarDetailsList.add(jsonObj.getString("VIN"));
					retrievedCarDetailsList.add(jsonObj.getString("Photo"));
					retrievedCarDetailsList.add(jsonObj.getString("Make"));
					retrievedCarDetailsList.add(jsonObj.getString("Model"));
					retrievedCarDetailsList.add(jsonObj.getString("Year"));
					retrievedCarDetailsList.add(jsonObj.getString("MilesPerTank"));
					retrievedCarDetailsList.add(jsonObj.getString("Email"));
					retrievedCarDetailsList.add(jsonObj.getString("Phone"));
					//retrievedCarDetailsList.add(jsonObj.getString("SortOrder"));
					//retrievedCarDetailsList.add(jsonObj.getString("EdmundsStyleID"));
					String carDetailsString = prepareCarDetailString(retrievedCarDetailsList);
					carNameList.add(jsonObj.getString("Name"));
					carDetailsStringList.add(carDetailsString);
				}

				populateCarDetailsHashMap(carNameList, carDetailsStringList);
				saveCarHMapSharedPref();
				saveCarsNamesSharedPref();
				Intent intent;

				if (GlobalState.isNewMenu) {
					intent = new Intent(SigninActivity.this, MyCarActivity.class);
				} else {
					intent = new Intent(SigninActivity.this, MyCarActivity.class);
				}

				intent.putExtra("tag", AppConstants.MY_CAR);
				//reset the back button stack, we don't want the user to go backwards following a log in.
				//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				//finish();

			} else if (response.equals("failure")) {

				if (intExtraTag == AppConstants.RETRIEVE_VEHICLES_VIA_EMAIL) {
					pwdEditText.setText("");
					pwdEditText.setError("Invalid email id or password.");
					pwdEditText.requestFocus();
				} else {
					emailEditText.setText("");
					emailEditText.setError("Email not found, please re-enter.");
				}

			}

		}catch (Exception e){
			Toast.makeText(SigninActivity.this, "Network Error. Please try again.", Toast.LENGTH_SHORT).show();
		}
        
	}
	
	public void retrieveVehViaEmail() {
		String emailString = emailEditText.getText().toString();
		String pwdString = pwdEditText.getText().toString();
		String serviceApiUrl = AppConstants.RETRIEVE_VEH_DETAILS_URL1;
		String urlWithParams;
		
		Log.i(TAG, "emailString: "+emailString);
		Log.i(TAG, "pwdString: "+pwdString);
		
		Log.i(TAG, "case 1: ");
		
		if (!emailString.equals("") && !pwdString.equals("")) {
			if (isEmailValid(emailString) ) { //&& isPwdValid(pwdString)
				urlWithParams = serviceApiUrl 
						+ "&e=" 
						+ emailString
						+ "&pw=" 
						+ pwdString;
				
				Log.i(TAG, "urlWithParams: "+urlWithParams);
				new FetchJSONAsync().execute(urlWithParams);
				
			} else {
				
				Log.i(TAG, "case 1-inner-else: ");
				
				if (!isEmailValid(emailString)) {
					Log.i(TAG, "emailNotValid: ");
					emailEditText.requestFocus();
					emailEditText.setError("Please enter a valid email address.");
					return;
				}
				
				/*if (!isPwdValid(pwdString))
					pwdEditText.setError("Password should be ")*/
			}
			
		} else {
			
			Log.i(TAG, "outer-else");
			if (emailString.equals("")) {
				emailEditText.setError("Email field is empty.");
				
				if (pwdString.equals("")) {
					pwdEditText.requestFocus();
					pwdEditText.setError("Password is empty.");
				}
				
				return;
			}
			
			if (!emailString.equals("") && pwdString.equals("")) {
				pwdEditText.setError("Password is empty.");
				return;
			}
		}
	}
	
	public void changePassword() {
		String emailStr = emailEditText.getText().toString();
		String oldPwdStr = oldPwdEditText.getText().toString();
		String newPwdStr = newPwdEditText.getText().toString();
		String verifyPwdStr = verifyPwdEditText.getText().toString();
		String urlWithParams1 = AppConstants.PASSWORD_CHANGE_URL + "&e="
								+ emailStr 
								+ "&pw="
								+ verifyPwdStr
								+ "&DFLID="
								+ dflId
								+ "&n1="
								+ newPwdStr
								+ "&n2="
								+ newPwdStr;
		
		if (!emailStr.equals("") && !oldPwdStr.equals("") && !newPwdStr.equals("") && !verifyPwdStr.equals("")) {
			if(isEmailValid(emailStr) && isPwdValid(newPwdStr) && newPwdStr.equals(verifyPwdStr)) {
				new FetchJSONAsync().execute(urlWithParams1);
			} else {
				if (!isEmailValid(emailStr)) {
					emailEditText.setError("Please enter a valid email address.");
					return;
				}
				if (!isPwdValid(newPwdStr)) {
					newPwdEditText.setError("Password not valid acc to rule.");
					return;
				}
				
				if (!newPwdStr.equals(verifyPwdStr)) {
					Toast.makeText(this, "Password do not match", Toast.LENGTH_LONG).show();
					return;
				}
			}
			
		} else {
			if(emailStr.equals("")) {
				emailEditText.setError("Email field is empty."); //setError
				return;
			}
			if (oldPwdStr.equals("")) {
				oldPwdEditText.setError("Password field is empty.");
				return;
			}
			if (newPwdStr.equals("")) { 
				newPwdEditText.setError("New password field is empty");
				return;
			}
			if(verifyPwdStr.equals("")) {
				verifyPwdEditText.setError("Verify password is empty");
				return;
			}
		}
	}
	
	public void forgotPassword() {
		String emailString = emailEditText.getText().toString();  ///serviceCall - forgot pwd. //&e=briankross@gmail.com&DFLID=DFL72
		String urlWithParams = AppConstants.PASSWORD_RESET + "&e="
							+ emailString 
							+ "&DFLID="
							+ dflId;
				
		if (!emailString.equals("")) {
			if(isEmailValid(emailString)) {
				new FetchJSONAsync().execute(urlWithParams);
				
			} else {
				Log.i(TAG, "emailNotValid: ");
				emailEditText.setError("Please enter a valid email address.");
				return;
			}
		} else {
			emailEditText.setError("Email field is empty."); //setError
			return;
		}
			
	}

	public String getUniqueDeviceId() {
		try {
			TelephonyManager telephonyManager = (TelephonyManager) this
					.getSystemService(Context.TELEPHONY_SERVICE);
			String deviceId = telephonyManager.getDeviceId();
			if (telephonyManager.getDeviceId() == null) {
				String android_id = Settings.Secure.getString(this.getContentResolver(),
						Settings.Secure.ANDROID_ID);
				deviceId = android_id;
			}
			return deviceId;
		} catch (Exception e) {
			Log.i("Dealership", "exception getUniqueDeviceId " + e);
		}
		return null;
	}

	public String getDeviceName() {
		String deviceName = android.os.Build.MODEL;
		return deviceName;
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
