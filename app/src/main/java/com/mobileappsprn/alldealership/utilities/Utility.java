package com.mobileappsprn.alldealership.utilities;//package com.mobileappsprn.alldealership.utilities;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Matrix;
//import android.graphics.Paint;
//import android.media.ExifInterface;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.Environment;
//import android.provider.Settings;
//import android.support.v4.content.ContextCompat;
//import android.text.TextUtils;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.util.TypedValue;
//import android.view.inputmethod.InputMethodManager;
//
//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.developer.buyit.AppController;
//import com.developer.buyit.model.UserData;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GoogleApiAvailability;
//import com.google.android.gms.gcm.GoogleCloudMessaging;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.text.NumberFormat;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.Random;
//
///**
// * Created by Khushal on 11/04/16.
// */
//public class Utility {
//
//
//
//    // Check whether internet is available or not
//    public static boolean isNetworkAvailable(Context ctx) {
//        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo netInfo = cm.getActiveNetworkInfo();
//        if (netInfo != null && netInfo.isConnectedOrConnecting()
//                && cm.getActiveNetworkInfo().isAvailable()
//                && cm.getActiveNetworkInfo().isConnected()) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    public static String getDeviceId(Context context) {
//        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
//    }
//
//
//    public static int getTotalBackgroundTime(long startBackgroundMillis) {
//        //mPrefs.getPrefs(getApplicationContext());
//        Calendar startTime = Calendar.getInstance();
//        long endBackgroundMillis = startTime.getTimeInMillis();
//        //long startBackgroundMillis = mPrefs.getLong(PrefsManager.KEY_BACKGROUND_TIME, 0L);
//
//        if (startBackgroundMillis > 0) {
//            long diffenceMillis = endBackgroundMillis - startBackgroundMillis;
//            int seconds = (int) ((diffenceMillis / 1000) % 60);
//            int minutes = (int) ((diffenceMillis / (1000 * 60)) % 60);
//            int hours = (int) ((diffenceMillis / (1000 * 60 * 60)) % 24);
//            return minutes;
//        } else {
//            return 0;
//        }
//    }
//
//    public static boolean isBuy() {
//        if (GlobalConstant.PACKAGE.contains("buyit"))
//            return true;
//        else
//            return false;
//    }
//
//    public static boolean writeData(Context context, String fileName, String data) {
//        boolean success = false;
//        try {
//            FileWriter writer = new FileWriter(context.getFilesDir().getPath() + File.separator + fileName);
//            writer.write(data);
//            writer.flush();
//            writer.close();
//            success = true;
//        } catch (IOException e) {
//            e.printStackTrace();
//            success = false;
//            Log.d(Utility.class.getName(), "Error writing file " + fileName);
//        } finally {
//            return success;
//        }
//    }
//
//    public static String readData(Context context, String filename) {
//        String data = null;
//        try {
//            File f = new File(context.getFilesDir() + File.separator + filename);
//            if (f.exists()) {
//                FileInputStream is = new FileInputStream(f);
//                int size = is.available();
//                byte[] buffer = new byte[size];
//                is.read(buffer);
//                is.close();
//                data = new String(buffer);
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            return data;
//        }
//    }
//
//    public static final int getColor(Context context, int id) {
//        final int version = Build.VERSION.SDK_INT;
//        if (version >= 23) {
//            return ContextCompat.getColor(context, id);
//        } else {
//            return context.getResources().getColor(id);
//        }
//    }
//
//    public static String compressImage(final String imageUriPath) {
//        final String filename = getFilename();
//
//        try {
//            String filePath = imageUriPath;
//            Bitmap scaledBitmap = null;
//
//            BitmapFactory.Options options = new BitmapFactory.Options();
//
//            // by setting this field as true, the actual bitmap pixels
//            // are
//            // not
//            // loaded in the memory. Just the bounds are loaded. If
//            // you try the use the bitmap here, you will get null.
//            options.inJustDecodeBounds = true;
//            Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
//
//            int actualHeight = options.outHeight;
//            int actualWidth = options.outWidth;
//
//
//            options.inSampleSize = calculateInSampleSize(options, actualWidth,
//                    actualHeight);
//
//            // inJustDecodeBounds set to false to load the actual bitmap
//            options.inJustDecodeBounds = false;
//
//            // this options allow android to claim the bitmap memory if
//            // it
//            // runs low
//            // on memory
//            options.inPurgeable = true;
//            options.inInputShareable = true;
//            options.inTempStorage = new byte[16 * 1024];
//
//            try {
//                // load the bitmap from its path
//                bmp = BitmapFactory.decodeFile(filePath, options);
//            } catch (OutOfMemoryError exception) {
//                exception.printStackTrace();
//
//            }
//            try {
//                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,
//                        Bitmap.Config.ARGB_8888);
//            } catch (OutOfMemoryError exception) {
//                exception.printStackTrace();
//            }
//
//            float ratioX = actualWidth / (float) options.outWidth;
//            float ratioY = actualHeight / (float) options.outHeight;
//            float middleX = actualWidth / 2.0f;
//            float middleY = actualHeight / 2.0f;
//
//            Matrix scaleMatrix = new Matrix();
//            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
//
//            Canvas canvas = new Canvas(scaledBitmap);
//            canvas.setMatrix(scaleMatrix);
//            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2,
//                    middleY - bmp.getHeight() / 2, new Paint(
//                            Paint.FILTER_BITMAP_FLAG));
//
//            // check the rotation of the image and display it properly
//            ExifInterface exif;
//            try {
//                exif = new ExifInterface(filePath);
//
//                int orientation = exif.getAttributeInt(
//                        ExifInterface.TAG_ORIENTATION, 0);
//
//                Matrix matrix = new Matrix();
//                if (orientation == 6) {
//                    matrix.postRotate(90);
//
//                } else if (orientation == 3) {
//                    matrix.postRotate(180);
//
//                } else if (orientation == 8) {
//                    matrix.postRotate(270);
//
//                }
//                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
//                        scaledBitmap.getWidth(), scaledBitmap.getHeight(),
//                        matrix, true);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            FileOutputStream out = null;
//
//            out = new FileOutputStream(filename);
//
//            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return filename;
//
//    }
//
//    public static String getFilename() {
//        File file = new File(Environment.getExternalStorageDirectory()
//                + "/"+ConfigValues.appName+"/" + "TempPic");
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
//                .format(new Date());
//        String uriSting = (file.getAbsolutePath() + "/" + "Img_" + timeStamp
//                + "profile.png");
//        return uriSting;
//
//    }
//
//    public static int calculateInSampleSize(BitmapFactory.Options options,
//                                            int reqWidth, int reqHeight) {
//        final int height = options.outHeight;
//        final int width = options.outWidth;
//        int inSampleSize = 1;
//
//        if (height > reqHeight || width > reqWidth) {
//            final int heightRatio = Math.round((float) height
//                    / (float) reqHeight);
//            final int widthRatio = Math.round((float) width / (float) reqWidth);
//            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
//        }
//        final float totalPixels = width * height;
//        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
//        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
//            inSampleSize++;
//        }
//
//        return inSampleSize;
//    }
//
//    public static String getRegistrationId(Context context){
//        SharedPreferences mpref = context.getApplicationContext().getSharedPreferences(GlobalConstant.pref_NAME, Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
//        String sessId = mpref.getString(GlobalConstant.pref_REG, null);
//        return sessId;
//    }
//
//    public static void storeRegistrationId(Context context, String reg){
//        SharedPreferences mPref = context.getApplicationContext().getSharedPreferences(GlobalConstant.pref_NAME, Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
//        SharedPreferences.Editor editor = mPref.edit();
//        editor.putString(GlobalConstant.pref_REG, reg);
//        editor.commit();
//    }
//
//    public static void registerWithGcm(Context mContext) {
//        if(checkPlayServices(mContext)) {
//            final GoogleCloudMessaging[] gcm = new GoogleCloudMessaging[1];
//            final Context context = mContext;
//            final String[] regId = new String[1];
//
//            gcm[0] = GoogleCloudMessaging.getInstance(context);
//            regId[0] = getRegistrationId(context);
//
//            new AsyncTask<Void, Void, String>() {
//                @Override
//                protected String doInBackground(Void... params) {
//                    String msg = "";
//                    try {
//                        if (TextUtils.isEmpty(regId[0])) {
//                            if (gcm[0] == null) {
//                                gcm[0] = GoogleCloudMessaging.getInstance(context);
//                            }
//                            regId[0] = gcm[0].register(ConfigValues.GOOGLE_PROJECT_NUMBER);
//                        }
//                        Log.v("RegisterActivity", "registerInBackground - regId: "
//                                + regId[0]);
//                        msg = "Device registered, registration ID=" + regId[0];
//                        storeRegistrationId(context, regId[0]);
//                        //new ShareExternalServer().shareRegIdWithAppServer(context, regId[0], GlobalConstants.LOGGED_IN_USER_ID);
//                        updateRegIdBackend(context, regId[0]);
//                    } catch (IOException ex) {
//                        msg = "Error :" + ex.getMessage();
//                        Log.v("RegisterActivity", msg);
//                    }
//                    Log.v("RegisterActivity", "AsyncTask completed: " + msg);
//                    return msg;
//                }
//
//                @Override
//                protected void onPostExecute(String msg) {
//
//                }
//            }.execute(null, null, null);
//        }
//    }
//
//    public static void updateRegIdBackend(final Context context, String regId){
//        if(UserData.getInstance(context)!=null && UserData.getInstance(context).get_id()!=null) {
//            final JSONObject jObj = new JSONObject();
//            try {
//                jObj.put("_id", UserData.getInstance(context).get_id());
//                jObj.put("device_id", regId);
//                jObj.put("device", "android");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            Log.d("Registration Update", "Req: " + jObj.toString());
//            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, GlobalConstant.reg_id, jObj, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    Log.d("Registration Update", "Rsp: " + response.toString());
//                    //AppDialog.showAlerDialog(context, "Request: "+jObj.toString()+"\nResponse: "+response.toString());
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Log.d("Registration Update", "Error: " + error.toString());
//                    //AppDialog.showAlerDialog(context, "Request: " + jObj.toString() + "\nError: " + error.toString());
//                }
//            });
//            AppController.getInstance().addToRequestQueue(request);
//        }
//    }
//
//    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
//    private static boolean checkPlayServices(Context context) {
//        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
//        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (apiAvailability.isUserResolvableError(resultCode)) {
//                //apiAvailability.getErrorDialog(context, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
//                Log.i("PLAY_SERVICE_CHECK", "Play services available");
//            } else {
//                Log.i("PLAY_SERVICE_CHECK", "This device is not supported.");
//                //finish();
//            }
//            return false;
//        }
//        return true;
//    }
//
//    public static String toTitleCase(String input) {
//        StringBuilder titleCase = new StringBuilder();
//        boolean nextTitleCase = true;
//
//        for (char c : input.toCharArray()) {
//            if (Character.isSpaceChar(c)) {
//                nextTitleCase = true;
//            } else if (nextTitleCase) {
//                c = Character.toTitleCase(c);
//                nextTitleCase = false;
//            }
//
//            titleCase.append(c);
//        }
//
//        return titleCase.toString();
//    }
//
//    public static String getCurrencyFormat(float amt){
//        NumberFormat formatter = NumberFormat.getNumberInstance();
//        formatter.setMinimumFractionDigits(2);
//        formatter.setMaximumFractionDigits(2);
//        String output = formatter.format(amt);
//        return output;
//    }
//}
