package com.mobileappsprn.alldealership;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mobileappsprn.alldealership.utilities.ApplicationVariables;

public class YearListView extends Activity {
	
	ArrayList<String> year; 
	ListView listView;
	ItemsAdapter adapter;
	String yearStr;
	String[] carYear;
	private Tracker mTracker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.year_listview);
		// Obtain the shared Tracker instance.
		ApplicationVariables application = (ApplicationVariables) getApplication();
		mTracker    =   application.getDefaultTracker();
		Log.v("FLOW", "CAME HERE : YearListView");
		year = new ArrayList<String>();
		
		carYear = new String[26];
		
		Calendar dater = Calendar.getInstance(Locale.US);
		int yearCurrent = dater.get(Calendar.YEAR) + ( (dater.get(Calendar.MONTH) >= 9) ? 1 : 0 );
	
		
		for(int i = 0; i <= 25 ; i++)
			carYear[i] = "" + yearCurrent-- ;

		listView = (ListView) findViewById(R.id.yearListView);
		adapter = new ItemsAdapter(this, carYear);
		
		listView.setAdapter(adapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		this.setTitle("Select Year");
	    
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				CheckedTextView ctv = (CheckedTextView)view;
				ctv.setChecked(!ctv.isChecked());
				ctv.toggle();
				
				yearStr = carYear[position];
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
	
	@Override
	public void finish() {
		// Prepare data intent 
		Intent returnIntent = new Intent();
	    returnIntent.putExtra("SelectedYear", yearStr);
	    setResult(RESULT_OK,returnIntent);   
		super.finish();
	} 
	
	private class ItemsAdapter extends BaseAdapter {
		String[] items;

		public ItemsAdapter(Context context, String[] item) {
			this.items = item;
		}

		// @Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.items_year_checklist, null);
			}
			CheckedTextView post = (CheckedTextView) v.findViewById(R.id.checkList);
			post.setText(items[position]);
			return v;
		}

		public int getCount() {
			return items.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.year_list_view, menu);
		return true;
	}

}
