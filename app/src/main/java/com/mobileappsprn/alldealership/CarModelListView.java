package com.mobileappsprn.alldealership;

import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

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
import com.mobileappsprn.alldealership.entities.AppConstants;
import com.mobileappsprn.alldealership.utilities.ApplicationVariables;

public class CarModelListView extends Activity {
	
	ArrayList<String> carModel; 
	ListView listView;
	ItemsAdapter adapter;
	String carModelStr;
	String[] carModelTitles;
	private Tracker mTracker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.car_model_listview);
		// Obtain the shared Tracker instance.
		ApplicationVariables application = (ApplicationVariables) getApplication();
		mTracker    =   application.getDefaultTracker();
		Log.v("FLOW", "CAME HERE : CarModelListView");
		carModel = new ArrayList<String>();
		
		try {
			InputStream istr = this.getAssets().open(AppConstants.JSON_FILE_PATH + "VehMakesModels.json");


			int size = istr.available();
			byte[] buffer = new byte[size];
			istr.read(buffer);
			istr.close();
			String bufferString = new String(buffer);

			JSONObject json = new JSONObject(bufferString);

			JSONArray jsonArray = json.getJSONArray("Items");
			
			JSONObject jsonObj = jsonArray.getJSONObject(GlobalState.selectedCarIndex);
        	
        	JSONArray jsonArray1 = jsonObj.getJSONArray("Details");
        	
    		for(int i = 0; i< jsonArray1.length(); i++) {
    			JSONObject jsonObj1 = jsonArray1.getJSONObject(i);
    			carModel.add(jsonObj1.getString("Title"));
    		}

			GlobalState.carModel = carModel;

		} catch (Exception e) {
			e.printStackTrace();
		}

		carModelTitles = new String[carModel.size()];

		for(int i = 0; i< carModel.size(); i++)
			carModelTitles[i] = carModel.get(i);

		listView = (ListView) findViewById(R.id.carModelListView);
		adapter = new ItemsAdapter(this,carModelTitles);
		
		listView.setAdapter(adapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		this.setTitle("Select Model");
		
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				CheckedTextView ctv = (CheckedTextView)view;
				ctv.setChecked(!ctv.isChecked());
				ctv.toggle();
				
				carModelStr = carModelTitles[position];
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
	    returnIntent.putExtra("SelectedModel", carModelStr);
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
				v = vi.inflate(R.layout.items_carmodel_checklist, null);
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
		getMenuInflater().inflate(R.menu.car_model_list_view, menu);
		return true;
	}

}
