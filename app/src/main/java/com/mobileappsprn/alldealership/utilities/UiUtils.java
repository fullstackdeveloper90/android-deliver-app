package com.mobileappsprn.alldealership.utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.os.Handler;
import android.util.Log;

/**
 * @author
 * 
 * 1. Show ProgressBar
 * 2. Show ProgressBar with listener
 * 3. Hide ProgressBar
 */

public class UiUtils {

	public static Context mContext;
	private static ProgressDialog mPgDialog;
	private static Handler mPgBarHandler = new Handler();
	
	public static void showProgressBar(final Activity activity, final String message, final OnCancelListener listener) {
		try {
			mPgBarHandler.post(new Runnable() {
				@Override
				public void run() {
					if (mPgDialog != null && mPgDialog.isShowing()) {
						mPgDialog.dismiss();
					}
                    if(activity.isFinishing()){
                        return;
                    }
					mPgDialog = new ProgressDialog(activity);
					mPgDialog.setIndeterminate(true);
					mPgDialog.setMessage(message);
					mPgDialog.setCanceledOnTouchOutside(false);
					mPgDialog.setCancelable(true);
					if (null != listener) {
						mPgDialog.setOnCancelListener(listener);
					}
					mPgDialog.show();
				}
			});
		} catch (Exception e) {
			Log.v("LOG_TAG", "Error showing progress bar " + e.getMessage());
		}
	}

	public static void showProgressBar(final Activity activity, final String message) {
		try {
			mPgBarHandler.post(new Runnable() {
				@Override
				public void run() {
					if (mPgDialog != null && mPgDialog.isShowing()) {
						mPgDialog.dismiss();
					}
                    if(activity.isFinishing()){
                        return;
                    }
					mPgDialog = new ProgressDialog(activity);
					mPgDialog.setIndeterminate(true);
					mPgDialog.setMessage(message);
					mPgDialog.setCanceledOnTouchOutside(false);
					mPgDialog.setCancelable(false);
					mPgDialog.show();
				}
			});
		} catch (Exception e) {
			Log.v("LOG_TAG", "Error showing progress bar " + e.getMessage());
		}
	}

	public static void dismissProgressBar() {
		if (null != mPgDialog) {
			mPgBarHandler.post(new Runnable() {

				@Override
				public void run() {

					if (mPgDialog != null && mPgDialog.isShowing()) {
						mPgDialog.dismiss();
					}
				}
			});

		}
	}
}
