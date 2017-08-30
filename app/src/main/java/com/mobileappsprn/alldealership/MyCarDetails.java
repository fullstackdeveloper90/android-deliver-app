package com.mobileappsprn.alldealership;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mobileappsprn.alldealership.entities.AppConstants;
import com.mobileappsprn.alldealership.utilities.ApplicationVariables;
import com.mobileappsprn.alldealership.utilities.BitmapResizer;
import com.mobileappsprn.alldealership.utilities.Utils;
import com.squareup.picasso.Picasso;

import android.*;
import android.Manifest;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyCarDetails extends Activity {


    //http://www.mobileappsprn.com/custom/mobileappsprn/ctl/getVINInfo.aspx?a=257&v=hdhsg
    public static final int REQ_CODE_CAR_MAKE = 1;
    public static final int REQ_CODE_CAR_MODEL = 2;
    public static final int REQ_CODE_YEAR = 3;
    public static final int ACTIVITY_SELECT_IMAGE = 4;
    public static final int CAMERA_REQUEST = 5;
    public static final int CAMERA_PERMISSION_REQUEST = 6;
    public static final int WRITE_PERMISSION_REQUEST = 7;

    private static final int ZBAR_SCANNER_REQUEST = 50;
    private static final int ZBAR_QR_SCANNER_REQUEST = 55;

    static EditText carNameEditText;
    static EditText carVinEditText;

    ImageView uploadedPhoto;
    Uri imgUri;

    static EditText carMakeEditText;
    static EditText carModelEditText;
    static EditText yearEditText;
    static EditText milesEditText;

    static EditText emailEditText;
    static EditText phoneEditText;

    static String autoCompleteResponseStatus;
    static String carMakeString;
    static String carModelString;
    static String carYearString;

    static SharedPreferences pref;
    Editor prefsEditor;

    // static int sharedPrefRank;

    StringBuilder strbuildercarNames;
    StringBuilder strbuildercarDetails;

    SharedPreferences prefKeyValues;
    Editor prefKeyValuesEditor;

    Map<String, String> carDetailsHMap;

    Bitmap yourSelectedImage;
    Bitmap imageBitmap;

    String carNameArray[];
    String carDetailsArray[];

    String carDetailsArrayToLoad[];

    String carNameToLoad;
    int rowItemClickedPosition;

    ArrayList<Bitmap> bitmapList;

    String alertMessageEmail = "";

    static String alertMessageServer = "";
    static String alertTitle = "";

    String photoFilePath = "";
    boolean isPhotoUploaded;
    boolean isGalleryImage = false;
    static String responseText = "";

    static String autoCompleteResponseText;

    boolean isEditing;

    boolean isCameraImage = false;

    String barCodeScanResult = "";

    static Context mContext;

    static Activity activity;

    static String urlWithParam;
    public static boolean isCarDetailsUrl = false;

    SharedPreferences mypref;
    public boolean isEmailAlertCancelled = false;

    static String dflId;
    private Tracker mTracker;
    public static boolean isKeyBoardHidden = false;
    public static boolean isAutoCompletionPrompt = false;

    public static ArrayList<String> carDetailsList;
    public static ArrayList<String> carNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.add_car_details);
        // Obtain the shared Tracker instance.
        ApplicationVariables application = (ApplicationVariables) getApplication();
        mTracker = application.getDefaultTracker();
        Log.v("FLOW", "CAME HERE : MyCarDetails");
        /** Hide keyboard **/
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setupActionBar();

        mContext = this;
        activity = this;

        pref = getApplicationContext().getSharedPreferences("ShockleyPref", 0);
        prefsEditor = pref.edit();

        mypref = getApplicationContext().getSharedPreferences("HomePrefs", Activity.MODE_PRIVATE);
        dflId = mypref.getString("dfl_id", "");

        boolean isPreferencePopulated = pref.getBoolean(
                "isPreferencePopulated", false);

        alertMessageEmail = "Email is required in order to automatically sync your service data. You can still use this app to track your service data manually.";

        prefKeyValues = getApplicationContext().getSharedPreferences("hashmap_shared_pref", Context.MODE_PRIVATE);
        prefKeyValuesEditor = prefKeyValues.edit();

        carDetailsHMap = new TreeMap<String, String>();

        carDetailsList = new ArrayList<String>();

        carNameList = new ArrayList<String>();

        bitmapList = new ArrayList<Bitmap>();

        strbuildercarDetails = new StringBuilder();

        strbuildercarNames = new StringBuilder();

        if (isPreferencePopulated) {
            @SuppressWarnings("unchecked")
            HashMap<String, String> map = (HashMap<String, String>) prefKeyValues
                    .getAll();
            // int prefHMapSize = map.size();
            for (String keyString : map.keySet()) {
                String value = map.get(keyString);
                carDetailsHMap.put(keyString, value);
                Log.i("CarDetails-Act-carDetailsHMap-key: ", "" + keyString);
            }
        }

        carNameEditText = (EditText) findViewById(R.id.nameEditText);
        carVinEditText = (EditText) findViewById(R.id.vinEditText);
        uploadedPhoto = (ImageView) findViewById(R.id.photoImageView);
        carMakeEditText = (EditText) findViewById(R.id.carMakeEditText);
        carModelEditText = (EditText) findViewById(R.id.carModelEditText);
        yearEditText = (EditText) findViewById(R.id.yearEditText);
        milesEditText = (EditText) findViewById(R.id.milesEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        phoneEditText = (EditText) findViewById(R.id.phoneEditText);

        //1N4AL3AP2HC117457
//http://www.mobileappsprn.com/custom/mobileappsprn/ctl/getVINInfo.aspx?a=257&v=1N4AL3AP2HC117457
        carVinEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    isKeyBoardHidden = false;
                    //Toast.makeText(mContext, "Keyboard-hide-event", Toast.LENGTH_LONG).show();
                    InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                    carVinEditText.setCursorVisible(false);
                    //carNameEditText.setFocusable(false);
                    milesEditText.setFocusable(true);
                    isCarDetailsUrl = false;

                    urlWithParam = addParamsToUrl();

                    Log.d("ok", "UrlWithParams " + urlWithParam);

                    if (Utils.checkInternetConnection(activity)) {
                        Log.i("internet-connection-", "present");
                        isAutoCompletionPrompt = true;
                        new SendToServerAsync().execute(urlWithParam);
//				AlertDialog.Builder alert = new AlertDialog.Builder(activity);
//				alert.setMessage("Are you sure you would like to exit this page without saving?");
//				alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//					activity.finish();
//					}
//				});
//				alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//
//					}
//				});
//				alert.show();

                        //BS

                    }
                    return true;
                }
                return false;
            }
        });

        Intent intent = getIntent();
        carNameToLoad = intent.getStringExtra("rowItemClicked");
        rowItemClickedPosition = intent.getIntExtra("rowItemClickedPosition",
                -1);
        isEditing = intent.getBooleanExtra("isEditing", false);

        Intent intent1 = getIntent();
        boolean isAddVehicleClickIntent = intent1.getBooleanExtra(
                "isAddVehicleClickIntent", false);

        Log.i("CarDetailsAct- carNameClicked: ", "" + carNameToLoad);

        isKeyBoardHidden = true;

        if (isPreferencePopulated && !isAddVehicleClickIntent) {
            if (carNameToLoad != null && !carNameToLoad.equals("")) {
                loadCarDetails();
            }
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mTracker != null) {
            mTracker.setScreenName(getClass().getSimpleName());
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }


    public void onClickVinEditText(View view) {
        isKeyBoardHidden = true;
        carVinEditText.setCursorVisible(true);
        carVinEditText.setFocusable(true);
    }

    public void onClickNameEditText(View view) {
        carNameEditText.setFocusable(true);
    }

    //http://www.mobileappsprn.com/custom/mobileappsprn/ctl/getVINInfo.aspx?a=SERVERID&v=<VIN>

    private void setupActionBar() {

		/*ActionBar ab = getSupportActionBar();
        // ab.setBackgroundDrawable(new ColorDrawable(0x01060000));
		ab.setDisplayShowCustomEnabled(true);
		ab.setDisplayShowTitleEnabled(false);
		LayoutInflater inflator = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflator.inflate(R.layout.car_details_titlebar, null);

		ab.setCustomView(v);
		ab.setDisplayHomeAsUpEnabled(true);*/
    }
/*
    @Override
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

//	public void launchScanner(View v) {
//		if (isCameraAvailable()) {
//			Intent intent = new Intent(this, ZBarScannerActivity.class);
//			startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
//		} else {
//			Toast.makeText(this, "Rear Facing Camera Unavailable",
//					Toast.LENGTH_SHORT).show();
//		}
//	}
//
//	public void launchQRScanner(View v) {
//		if (isCameraAvailable()) {
//			Intent intent = new Intent(this, ZBarScannerActivity.class);
//			intent.putExtra(ZBarConstants.SCAN_MODES,
//					new int[] { Symbol.QRCODE });
//			startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
//		} else {
//			Toast.makeText(this, "Rear Facing Camera Unavailable",
//					Toast.LENGTH_SHORT).show();
//		}
//	}

    public void launchScanner(View v) {
        if (isCameraAvailable()) {
            Intent intent = new Intent(this, CarScannerActivity.class);
            startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
        } else {
            Toast.makeText(this, "Rear Facing Camera Unavailable",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void launchQRScanner(View v) {
        if (isCameraAvailable()) {
            Intent intent = new Intent(this, CarScannerActivity.class);
//			intent.putExtra(ZBarConstants.SCAN_MODES,
//					new int[] { Symbol.QRCODE });
            startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
        } else {
            Toast.makeText(this, "Rear Facing Camera Unavailable",
                    Toast.LENGTH_SHORT).show();
        }
    }


    public boolean isCameraAvailable() {
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public void showAlert(String alertMsg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Note!");

        alertDialogBuilder.setMessage(alertMsg).setCancelable(true)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        return;

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void emailAlert(final View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Note!");

        alertDialogBuilder
                .setMessage(alertMessageEmail)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        saveCarDetailsHelper(view);
                        //populateSharedPrefAndSendToServer();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                isEmailAlertCancelled = true;
                                return;
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public class SendToServerAsync extends AsyncTask<String, String, Void> {

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage("Processing Request...");
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(String... urlWithParam) {
            // TODO Auto-generated method stub
            try {
                Log.i("urlWithParam: ", "urlWithParam: " + urlWithParam[0]);
                isKeyBoardHidden = false;
                sendCarDetailsToServer(urlWithParam[0]);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (isAutoCompletionPrompt) {
                isAutoCompletionPrompt = false;
                pDialog.dismiss();
                try {
                    parseAutoCompleteJson();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(MyCarDetails.this, "Unable to find vehicle details.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                try {
                    if (autoCompleteResponseStatus.equals("1")) {
                        carMakeEditText.setText(carMakeString);
                        carModelEditText.setText(carModelString);
                        yearEditText.setText(carYearString);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(MyCarDetails.this, "Unable to find vehicle details.", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            if (responseText.equalsIgnoreCase("success")) {
                alertMessageServer = "Thank you for registering your vehicle. You may now begin browsing service history from your device.";
                alertTitle = "Success";

            } else if (responseText.equals("authenticated")) {
                activity.finish();
                return;

            } else {
                alertMessageServer = "We are unable to find a record with this VIN and email address in our files. Service data will not automatically be downloaded to your device, but you may still use this app to maintain your service data manually.";
                alertTitle = "Alert";
            }

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    activity);

            // set title
            alertDialogBuilder.setTitle(alertTitle);

            // set dialog message
            alertDialogBuilder
                    .setMessage(alertMessageServer)
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (pref.getBoolean(
                                            "isPreferencePopulated", false)) {
                                        Intent intent = new Intent(
                                                activity,
                                                ManageScreenActivity.class);
                                        // intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                        intent.putExtra("tag", AppConstants.MY_CAR);
                                        pDialog.dismiss();
                                        dialog.dismiss();
                                        activity.startActivity(intent);
                                        activity.finish();
                                    }
                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    // if this button is clicked, just close
                                    // the dialog box and do nothing
                                    pDialog.dismiss();
                                    dialog.cancel();
                                }
                            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }
    }

    public void loadCarDetails() {
        isKeyBoardHidden = true;
        carNameEditText.setText(carNameToLoad.substring(1));

        Log.i("CarDetailsAtc-rowItemClickedPosition: ", "" + rowItemClickedPosition);
        Log.i("CarDetailsAtc-carNameToLoad: ", "" + carNameToLoad);

        for (String keyString : carDetailsHMap.keySet()) {
            Log.i("CarDetailsAct-loadCar()-carDetailsHMap: ", "" + keyString);
        }

        String carDetailsStringToLoad = carDetailsHMap.get(carNameToLoad);

        Log.i("carDetailsStringToLoad: ", "" + carDetailsStringToLoad);
        carDetailsArrayToLoad = new String[12];

        carDetailsArrayToLoad = carDetailsStringToLoad.split(",");

        for (int i = 0; i < carDetailsArrayToLoad.length; i++) {
            Log.i("carDetailsArrayToLoad[" + i + "]: ", ""
                    + carDetailsArrayToLoad[i]);
        }

        if (carDetailsArrayToLoad.length > 1)
            carVinEditText.setText(carDetailsArrayToLoad[1]);

        if (carDetailsArrayToLoad.length > 2) {
            File selectedFile;
            String imageUrl;

            ImageView imgView = (ImageView) findViewById(R.id.photoImageView);

            if (carDetailsArrayToLoad[2].contains("http:")) {
                imageUrl = carDetailsArrayToLoad[2];
                Picasso.with(this).load(imageUrl).into(imgView);

            } else {
                selectedFile = new File(carDetailsArrayToLoad[2]);
                Log.i("ImagePath:- ", "" + carDetailsArrayToLoad[2]);
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                imageBitmap = BitmapResizer.decodeFile(selectedFile,
                        metrics.widthPixels);
                imgView = (ImageView) findViewById(R.id.photoImageView);
                imgView.setImageBitmap(imageBitmap);
            }


        }

        if (carDetailsArrayToLoad.length > 3)
            carMakeEditText.setText(carDetailsArrayToLoad[3]);

        if (carDetailsArrayToLoad.length > 4)
            carModelEditText.setText(carDetailsArrayToLoad[4]);

        if (carDetailsArrayToLoad.length > 5)
            yearEditText.setText(carDetailsArrayToLoad[5]);

        if (carDetailsArrayToLoad.length > 6)
            milesEditText.setText(carDetailsArrayToLoad[6]);

        if (carDetailsArrayToLoad.length > 7)
            emailEditText.setText(carDetailsArrayToLoad[7]);

        if (carDetailsArrayToLoad.length > 8)
            phoneEditText.setText(carDetailsArrayToLoad[8]);
    }

    public void saveCarDetails(View view) {

        if (Utils.checkInternetConnection(this) && !emailEditText.getText().toString().equals("")) {
            saveCarDetailsHelper(view);
            populateSharedPrefAndSendToServer();

        } else if (Utils.checkInternetConnection(this) && emailEditText.getText().toString().equals("")) {
            emailAlert(view);
            if (isEmailAlertCancelled) {
                isEmailAlertCancelled = false;
                return;
            }
        } else {
            showAlert("No Internet connection. Please try again ");
        }

    }

    public void saveCarDetailsHelper(View view) {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(
                view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);


        if (!carNameEditText.getText().toString().equals("")) {
            carDetailsList.add(carNameEditText.getText().toString());

        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);

            alertDialogBuilder.setTitle("Alert!");

            alertDialogBuilder.setMessage("Please enter the name to save!")
                    .setNeutralButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {

                                    dialog.dismiss();
                                }
                            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return;
        }

        if (!carVinEditText.getText().toString().equals("")) {
            carDetailsList.add(carVinEditText.getText().toString());

        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);

            alertDialogBuilder.setTitle("Alert!");

            alertDialogBuilder.setMessage("Please enter VIN to save!")
                    .setNeutralButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {

                                    dialog.dismiss();
                                }
                            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return;
        }

        if (isEditing == false) {
            Log.i("isEditing: ", "isEditing: " + isEditing);
            if (!photoFilePath.equals("")) {
                carDetailsList.add(photoFilePath);
            } else {
                carDetailsList.add("photoNotAvailable");
            }
        } else {
            Log.i("isEditing: ", "isEditing: " + isEditing);
            if (isGalleryImage) {
                if (!photoFilePath.equals("")) {
                    carDetailsList.add(photoFilePath);
                } else {
                    carDetailsList.add("photoNotAvailable");
                }
                isGalleryImage = false;
            } else {
                if (isCameraImage) {
                    if (!photoFilePath.equals("")) {
                        carDetailsList.add(photoFilePath);
                    } else {
                        carDetailsList.add("photoNotAvailable");
                    }
                    isCameraImage = false;

                } else {
                    if (!carDetailsArrayToLoad[2].equals("")) {
                        carDetailsList.add(carDetailsArrayToLoad[2]);
                    } else {
                        carDetailsList.add("photoNotAvailable");
                    }
                }
            }
        }

        if (carMakeEditText.getText() != null) {
            carDetailsList.add(carMakeEditText.getText().toString());
        }

        if (carModelEditText.getText() != null) {
            carDetailsList.add(carModelEditText.getText().toString());
        }

        if (yearEditText.getText() != null) {
            carDetailsList.add(yearEditText.getText().toString());
        }

        if (milesEditText.getText() != null) {
            carDetailsList.add(milesEditText.getText().toString());
        }

        if (!emailEditText.getText().toString().equals("")) {
            carDetailsList.add(emailEditText.getText().toString());
        }

        if (phoneEditText.getText() != null) {
            carDetailsList.add(phoneEditText.getText().toString());
        }

        // carDetails.put("carNameKey", carNameEditText.getText().toString());

        carDetailsArray = new String[carDetailsList.size()];

        for (int i = 0; i < carDetailsList.size(); i++) {
            carDetailsArray[i] = carDetailsList.get(i);
        }

        for (int i = 0; i < carDetailsList.size(); i++) {
            strbuildercarDetails.append(carDetailsArray[i]).append(",");
        }
    }

    public void populateSharedPrefAndSendToServer() {
        if (isEditing) {
            // String carDetailsStringToLoad =
            // carDetailsHMap.get(rowItemClickedPosition+carNameToLoad);
            Log.i("isEditingHmIterator: ", "isEditing: " + isEditing);
            for (Iterator<Map.Entry<String, String>> it = carDetailsHMap
                    .entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, String> entry = it.next();
                if (entry.getKey().equals(carNameToLoad)) { // 0th index is
                    // prepended
                    // index see
                    // hmap-size in
                    // cardetails.java.
                    Log.i("entry.getKey(): ", "" + entry.getKey());
                    it.remove();
                    Log.i("in CarDetailsAct-hmap remove loop:", "yes");
                }
            }
            for (String keyString : carDetailsHMap.keySet()) {
                Log.i("CarDetailsAct-keysAfterDel: ", "" + keyString);
            }
            int carDetailsHMapSize = carDetailsHMap.size();
            carDetailsHMap.put(carDetailsHMapSize + carDetailsList.get(0),
                    strbuildercarDetails.toString()); // carDetailsList.get(0).  ==>// name of the car--as key.

            prefKeyValuesEditor.clear();
            for (String keyString : carDetailsHMap.keySet()) {
                prefKeyValuesEditor.putString(keyString,
                        carDetailsHMap.get(keyString));
            }
            isEditing = false;

        } else {
            Log.i("isEditingHmIterator: ", "isEditing: " + isEditing);
            int carDetailsHMapSize = carDetailsHMap.size();
            carDetailsHMap.put(carDetailsHMapSize + carDetailsList.get(0),
                    strbuildercarDetails.toString()); // carDetailsList.get(0) // ==>// name of the car--as key.

            for (String keyString : carDetailsHMap.keySet()) {
                prefKeyValuesEditor.putString(keyString,
                        carDetailsHMap.get(keyString));
                Log.i("CarDetailsAct- carDetailsHMap:", "" + keyString);
            }
        }

        // keyValuesEditor.putBoolean("isPreferencePopulatedHashmp", true);
        prefKeyValuesEditor.commit();

        carNameArray = new String[carDetailsHMap.size()];

        int i = 0, k = 0;
        for (String keyString : carDetailsHMap.keySet()) {
            carNameArray[i++] = keyString;
            Log.i("carNameArray:[i] ", "" + carNameArray[k++]);
        }

        for (int j = 0; j < carNameArray.length; j++) {
            strbuildercarNames.append(carNameArray[j]).append(",");
        }

        prefsEditor.clear();
        prefsEditor.putString(AppConstants.CAR_NAME_PREF,
                strbuildercarNames.toString());
        prefsEditor.putBoolean("isPreferencePopulated", true);
        prefsEditor.commit();

        isCarDetailsUrl = true;
        urlWithParam = addParamsToUrl();
        if (carNameEditText.getText().toString() != null
                && carVinEditText.getText().toString() != null) {
            if (Utils.checkInternetConnection(this)) {
                isCarDetailsUrl = false;
                new SendToServerAsync().execute(urlWithParam);
            }
        }
    }

    public static String addParamsToUrl() {
        String vin = getVin();
        String deviceId = getUniqueDeviceId();
        String deviceName = getDeviceName();
        String emailId = getEmailId();
        String phoneNo = getPhoneNo();


        if (isCarDetailsUrl) {
            urlWithParam = "http://www.mobileappsprn.com/custom/mobileappsprn/ctl/postVIN.aspx?a=SERVERID&u="
                    + deviceId
                    + "&v="
                    + vin
                    + "&d="
                    + deviceId
                    + "&e="
                    + emailId
                    + "&p=" + phoneNo + "&dn=" + deviceName;

            if (GlobalState.isDealershipGroupVersion) {
                if (urlWithParam.contains("SERVERID")) {
                    urlWithParam = urlWithParam.replaceAll("SERVERID", "SERVERID&DFLID=" + dflId);
                    if (urlWithParam.contains("SERVERID"))
                        urlWithParam = urlWithParam.replaceAll("SERVERID", GlobalState.serverId);
                }
            } else {
                if (urlWithParam.contains("SERVERID")) {
                    urlWithParam = urlWithParam.replaceAll("SERVERID", GlobalState.serverId);
                }
            }
        } else {
            String carVin = carVinEditText.getText().toString();
            urlWithParam = "http://www.mobileappsprn.com/custom/mobileappsprn/ctl/getVINInfo.aspx?a=SERVERID&v=" + carVin;
            if (GlobalState.isDealershipGroupVersion) {
                if (urlWithParam.contains("SERVERID")) {
                    urlWithParam = urlWithParam.replaceAll("SERVERID", "SERVERID&DFLID=" + dflId);
                    if (urlWithParam.contains("SERVERID"))
                        urlWithParam = urlWithParam.replaceAll("SERVERID", GlobalState.serverId);
                }
            } else {
                if (urlWithParam.contains("SERVERID")) {
                    urlWithParam = urlWithParam.replaceAll("SERVERID", GlobalState.serverId);
                }
            }
        }

        Log.i("ServerUrl with Params: ", "" + urlWithParam);
        return urlWithParam;
    }

    public void launchListView(View view) {
        int i = view.getId();
        switch (i) {
            case (R.id.makeRelLayout):
                Intent intent1 = new Intent(this, CarMakeListView.class);
                startActivityForResult(intent1, REQ_CODE_CAR_MAKE);
                break;
            case (R.id.modelRelLayout):
                Intent intent2 = new Intent(this, CarModelListView.class);
                startActivityForResult(intent2, REQ_CODE_CAR_MODEL);
                break;
            case (R.id.yearRelLayout):
                Intent intent3 = new Intent(this, YearListView.class);
                startActivityForResult(intent3, REQ_CODE_YEAR);
                break;
        }
    }

    public void onClickImageViewPhoto(View view) {
        final CharSequence[] items = {"Choose from Gallery",
                "Shoot from Camera", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Make your selection");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        openGallery();
                        break;
                    case 1:
                        askForCameraPermission();
                        break;
                    case 2:
                        break;
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void openGallery() {
        Intent i = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
    }

    public void cameraIntent() {
        URIIssue();
        if (imgUri == null)
            setImageUri();
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

private void URIIssue(){
    if(Build.VERSION.SDK_INT>=24){
        try{
            Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
            m.invoke(null);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

    private void setImageUri() {
        try {
            // Store image in dcim
            File file = new File(Environment.getExternalStorageDirectory(), (carVinEditText.getText().toString() + "_" + System.currentTimeMillis() + ".jpg"));
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                file.delete();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            imgUri = Uri.fromFile(file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void askForCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        } else {
            askForStoragePermission();
        }
    }

    private void askForStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_REQUEST);
        } else {
            cameraIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    askForStoragePermission();
                }
                return;
            }

            case WRITE_PERMISSION_REQUEST: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cameraIntent();
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (REQ_CODE_CAR_MAKE):
                Log.d("ok", "On Activity Result  " + (data != null));
                if (resultCode == RESULT_OK) {
                    Log.d("ok", "On Activity Result 1  " + (data != null));
                    carMakeEditText.setText(data.getStringExtra("SelectedCar"));
                    carMakeEditText.setTypeface(null, Typeface.BOLD);
                } else if (resultCode == RESULT_CANCELED) {
                    carMakeEditText.setText("");
                }
                break;

            case (REQ_CODE_CAR_MODEL):
                Log.d("ok", "On Activity Result  " + (data != null));
                if (resultCode == RESULT_OK) {
                    Log.d("ok", "On Activity Result  1 " + (data != null));
                    carModelEditText.setText(data.getStringExtra("SelectedModel"));
                    carModelEditText.setTypeface(null, Typeface.BOLD);
                } else if (resultCode == RESULT_CANCELED) {
                    carModelEditText.setText("");
                }
                break;

            case (REQ_CODE_YEAR):
                if (resultCode == RESULT_OK) {
                    yearEditText.setText(data.getStringExtra("SelectedYear"));
                    yearEditText.setTypeface(null, Typeface.BOLD);
                } else if (resultCode == RESULT_CANCELED) {
                    yearEditText.setText("");
                }
                break;
            case ACTIVITY_SELECT_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    photoFilePath = filePath;

                    Log.i("photoFilePath: ", "" + photoFilePath);
                    cursor.close();
                    File selectedFile = new File(filePath);
                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    yourSelectedImage = BitmapResizer.decodeFile(selectedFile,
                            metrics.widthPixels);
                    ImageView imgView = (ImageView) findViewById(R.id.photoImageView);
                    imgView.setImageBitmap(yourSelectedImage);
                    isPhotoUploaded = true;
                    isGalleryImage = true;
                    isCameraImage = false;

                }
                break;

            case CAMERA_REQUEST:
                if (resultCode == RESULT_OK) {
                    Uri imageUri = imgUri;
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    photoFilePath = imageUri.getPath();

                    Log.i("Camera-photoFilePath: ", "" + photoFilePath);
                    ImageView imgView = (ImageView) findViewById(R.id.photoImageView);
                    imgView.setImageBitmap(bitmap);

                    isGalleryImage = false;
                    isCameraImage = true;
                /*
                 * ByteArrayOutputStream baos = new ByteArrayOutputStream();
				 * photo.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm
				 * is the bitmap object byte[] b = baos.toByteArray();
				 */
                }
                break;

            case ZBAR_SCANNER_REQUEST:
            case ZBAR_QR_SCANNER_REQUEST:
                if (resultCode == RESULT_OK) {
                    carVinEditText.setText(data
                            .getStringExtra("SCAN_RESULT"));

                } else if (resultCode == RESULT_CANCELED && data != null) {
//				String error = data.getStringExtra(ZBarConstants.ERROR_INFO);
//				if (!TextUtils.isEmpty(error)) {
//					Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
//				}
                }
                break;
        }

    }

    public static void parseAutoCompleteJson() throws JSONException {

        JSONObject json = new JSONObject(autoCompleteResponseText);

        Log.i("json: ", "" + json);

        autoCompleteResponseStatus = json.getString("status");
        carMakeString = json.getString("make");
        carModelString = json.getString("model");
        carYearString = json.getString("year");

        Log.i("parseAutoCompleteJson: ", "autoCompleteResponseText: " + autoCompleteResponseText);

        Log.i("autoCompleteResponseStatus: ", "" + autoCompleteResponseStatus);
        Log.i("carMakeString: ", "" + carMakeString);
        Log.i("carModelString: ", "" + carModelString);
        Log.i("carYearString: ", "" + carYearString);

    }

    public static void sendCarDetailsToServer(String urlWithParam) {

        /**
         * HttpEntity entity = response.getEntity(); String responseText =
         * EntityUtils.toString(entity);
         **/

        // InputStream content = null;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(new HttpGet(urlWithParam));
            HttpEntity entity = response.getEntity();
            responseText = EntityUtils.toString(entity);

            Log.i("HttpGet responseMsg: ", "" + responseText);
            if (isAutoCompletionPrompt) {
                autoCompleteResponseText = responseText;
                Log.i("autoCompleteResponseText: ", "" + autoCompleteResponseText);
            }

        } catch (Exception e) {
            Log.i("[GET REQUEST]", "Network exception: " + e.getMessage());
        }

    }

    public static String getUniqueDeviceId() {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) activity
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String deviceId = telephonyManager.getDeviceId();
            if (telephonyManager.getDeviceId() == null) {
                String android_id = Secure.getString(activity.getContentResolver(),
                        Secure.ANDROID_ID);
                deviceId = android_id;
            }
            return deviceId;
        } catch (Exception e) {
            Log.v("Dealership", "exception getUniqueDeviceId " + e);
        }
        return null;
    }

    public static String getDeviceName() {
        String deviceName = android.os.Build.MODEL;
        return deviceName;
    }

    public static String getVin() {
        String vin = carVinEditText.getText().toString();
        return vin;
    }

    public static String getEmailId() {
        String emailId = emailEditText.getText().toString();
        return emailId;
    }

    public static String getPhoneNo() {
        String phoneNo = phoneEditText.getText().toString();
        return phoneNo;
    }

}
