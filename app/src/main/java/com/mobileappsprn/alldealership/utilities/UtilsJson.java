package com.mobileappsprn.alldealership.utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UtilsJson {
public static final String PREFERENCES_FILE_NAME="SPLASH_SHARED";

    public static JSONObject getJsonData(String urlString, String... prams) {
        try {
            URL url;
            url = new URL(String.format(urlString, new Object[]{prams}));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout((int) 30e3);
            int resCode = conn.getResponseCode();
            if (resCode == 200) {
                InputStream ios = conn.getInputStream();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                byte[] buf = new byte[4096];
                int n;
                while ((n = ios.read(buf)) >= 0)
                    os.write(buf, 0, n);
                os.close();
                ios.close();
                byte[] data = os.toByteArray();
                if (data != null) {
                    return new JSONObject(new String(data));
                }
            }
        } catch (Exception e) {
            // do nothing
        }
        return null;
    }

    public static JSONArray getJsonArray(String urlString, String... prams) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(String.format(urlString, prams));
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout((int) 30e3);
            int resCode = conn.getResponseCode();
            if (resCode == 200) {
                InputStream ios = conn.getInputStream();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                byte[] buf = new byte[4096];
                int count;
                while ((count = ios.read(buf)) >= 0) {
                    os.write(buf, 0, count);
                }
                closeOutputStream(os);
                closeInputStream(ios);
                byte[] data = os.toByteArray();
                if (data != null) {
                    return new JSONArray(new String(data));
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return null;
    }
@SuppressWarnings("unused")
protected static boolean postJsonData(String urlString, String... prams) {
    	
    	  Authenticator.setDefault(new Authenticator() {
              protected PasswordAuthentication getPasswordAuthentication(){

              return new PasswordAuthentication("lmg", "lmg".toCharArray());   
              }

          });

        try {

            URL url = new URL(String.format(urlString, prams));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout((int) 30e3);
            int resCode = conn.getResponseCode();
            if (resCode == 200) {
                InputStream ios = conn.getInputStream();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                byte[] buf = new byte[4096];
                int count;
                while ((count = ios.read(buf)) >= 0) {
                    os.write(buf, 0, count);
                }
                closeOutputStream(os);
                closeInputStream(ios);
                byte[] data = os.toByteArray();
                if (data != null) {
                    return new Boolean(new String(data));
                }
            }
        } catch (Exception e) {
            // do nothing
        }
        return false;
    }

    private static void closeOutputStream(OutputStream outputStream) {
        try {
            outputStream.close();
        } catch (IOException e) {
            // do nothing
        }
    }

    private static void closeInputStream(InputStream inputStream) {
        try {
            inputStream.close();
        } catch (IOException e) {
            // do nothing
        }
    }


    public static int getJsonIntValue(JSONObject jsonObject, String key) {
        try {
            return jsonObject.getInt(key);
        } catch (JSONException e) {
            // do nothing
        }
        return 0;
    }

    public static long getJsonLongValue(JSONObject jsonObject, String key) {
        try {
            return jsonObject.getLong(key);
        } catch (JSONException e) {
            // do nothing
        }
        return 0;
    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            while (true) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1) {
                    break;
                }
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
            //do nothing
        }
    }


    public static boolean isPropertyEmpty(String property) {
        if (property == null) {
            return true;
        } else {
            if (property.trim().equals("")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPropertyNotEmpty(String property) {
        return !isPropertyEmpty(property);
    }

    public static String encodeURL(String url) {
        int index = url.lastIndexOf("/") + 1;

        return url.substring(0, index) + URLEncoder.encode(url.substring(index));
    }


    public static void main(String[] arg) {
        System.out.println(encodeURL("/sdf/sfsdf/sdfsdfs/sfs sfds.png"));
    }

    public static String getCurrentDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public static String getCurrentTime() {
		Calendar cal = Calendar.getInstance();
		cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return sdf.format(cal.getTime());
	}
	
	public static String getCurrentDateAndTime(){
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		Date date = new Date();
		return dateFormat.format(date);
	}


}