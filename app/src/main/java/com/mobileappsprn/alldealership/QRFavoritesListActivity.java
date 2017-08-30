package com.mobileappsprn.alldealership;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mobileappsprn.alldealership.adapters.QRFavAdapter;
import com.mobileappsprn.alldealership.entities.Items;
import com.mobileappsprn.alldealership.utilities.ApplicationVariables;
import com.mobileappsprn.alldealership.utilities.Utils;



public class QRFavoritesListActivity extends Activity {
	
	ListView listview;
	QRFavAdapter adapter;
	private ArrayList<Items> items=new ArrayList<Items>();
	private Tracker mTracker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	/*	ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);*/
		setContentView(R.layout.drilldown_root);
		// Obtain the shared Tracker instance.
		ApplicationVariables application = (ApplicationVariables) getApplication();
		mTracker    =   application.getDefaultTracker();
		Log.v("FLOW", "CAME HERE : QRFavoritesListActivity");
		setupActionBar();
		init();
	}
	private void init()	{		

		items=new Utils().getFavQRurls(this);		
		listview=(ListView)findViewById(R.id.listviewjson);
		adapter=new QRFavAdapter(this,R.layout.fav_list_item,items);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				/*Intent intent = new Intent(QRFavoritesListActivity.this, HelpWebViewActivity.class);

				intent.putExtra("helpUrl", items.get(position).getUrl());        		
				intent.putExtra("webViewTitle", items.get(position).getTitle());
				intent.putExtra("Tag", "");
				intent.putExtra("browser", false);
				intent.putExtra("position", position);        		
				startActivity(intent);    */
				Intent intent = new Intent(QRFavoritesListActivity.this, QRScannerWebViewActivity.class);
				intent.putExtra("qrScanUrl", items.get(position).getUrl());
				intent.putExtra("title", items.get(position).getTitle());
				intent.putExtra("type", "Fav");
				startActivity(intent);

			}
		});
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View view,
					int position, long parent)
			{				
				showAlert(position);
				return false;
			}
		});
	}
	private void setupActionBar() {

//	/*	ActionBar ab = getSupportActionBar();
//		ab.setDisplayShowCustomEnabled(true);
//		ab.setDisplayShowTitleEnabled(false);
//		LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		View	v = inflator.inflate(R.layout.actionbar_qrfavorites, null);
//
//		ab.setCustomView(v);
//		ab.setDisplayHomeAsUpEnabled(true);*/

	}
/*	@Override
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
	@Override
	protected void onResume() {	
		super.onResume();
		if(mTracker != null) {
			mTracker.setScreenName(getClass().getSimpleName());
			mTracker.send(new HitBuilders.ScreenViewBuilder().build());
		}
		/*ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);*/
	}
	public void showAlert (final int position) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);
		
		alertDialogBuilder.setTitle("Alert!");
		
		alertDialogBuilder
				.setMessage("Do you want to delete?")
				.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id)
							{								
								dialog.dismiss();
								new Utils().removeQRurl(QRFavoritesListActivity.this, items.get(position).getTitle());
								items.remove(items.get(position));
								adapter.notifyDataSetChanged();
								
							}
						})
						.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id)
							{								
								dialog.dismiss();							
								
							}
						});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialogBuilder.setCancelable(true);
		alertDialog.show();
	}
}
