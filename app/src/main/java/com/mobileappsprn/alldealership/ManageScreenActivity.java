package com.mobileappsprn.alldealership;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mobileappsprn.alldealership.entities.AppConstants;
import com.mobileappsprn.alldealership.utilities.ApplicationVariables;

import android.app.Activity;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

//import com.actionbarsherlock.view.MenuItem;

public class ManageScreenActivity extends Activity {

	ArrayList<String> carName;
	ListView listView;
	ItemsAdapter adapter;
	String carNameStr;
	String[] carNameArray;
	String[] carNameArrayNoSubs;
	String[] resultantCarNameArray;
	AlertDialog.Builder alert;
	SharedPreferences pref;
	Editor prefsEditor;
	List<String> carNameArrayList;

	SharedPreferences selectedCarSharedPref;
	Editor selectedCarPrefEditor;
	boolean isSelectedCarPrefPopulated;
	Editor prefKeyValuesEditor;
	// int selectedCar;

	int clickedItem;
	Map<String, String> carDetailsHMap;
	static int indexOfCarPageSelected;

	SharedPreferences prefKeyValues;
	private Tracker mTracker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Log.v("FLOW", "CAME HERE : ManageScreenActivity");
		/*ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);*/
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
			if(mTracker != null) {
				mTracker.setScreenName(getClass().getSimpleName());
				mTracker.send(new HitBuilders.ScreenViewBuilder().build());
			}
		/*ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);*/
		
		initialise();
	}
	private void setupActionBar() {
		
		/*ActionBar ab = getSupportActionBar();
	    //ab.setBackgroundDrawable(new ColorDrawable(0x01060000));
        ab.setDisplayShowCustomEnabled(true);
        ab.setDisplayShowTitleEnabled(false);
        LayoutInflater inflator = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.managescreentitlebar, null);

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
	public void initialise() {
		
		  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		   		    
		setContentView(R.layout.manage_screen_listview);
		// Obtain the shared Tracker instance.
		ApplicationVariables application = (ApplicationVariables) getApplication();
		mTracker    =   application.getDefaultTracker();
	    setupActionBar();
		alert = new AlertDialog.Builder(this);

		pref = getApplicationContext().getSharedPreferences("ShockleyPref", 0); 

		prefsEditor = pref.edit();

		selectedCarSharedPref = getApplicationContext().getSharedPreferences(
				"selected_car_shared_pref", Context.MODE_PRIVATE);
		selectedCarPrefEditor = selectedCarSharedPref.edit();

		isSelectedCarPrefPopulated = selectedCarSharedPref.getBoolean(
				"isSelectedCarPrefPopulated", false);

		indexOfCarPageSelected = selectedCarSharedPref.getInt("selectedCar", 0);

		String prefStringCarName = pref.getString(AppConstants.CAR_NAME_PREF,
				"");
		carNameArray = prefStringCarName.split(",");
		carNameArrayNoSubs = prefStringCarName.split(",");
		
		if (carNameArray[0]!="")
			for (int i = 0; i < carNameArray.length; i++) {
				carNameArray[i] = carNameArray[i].substring(1);
				carNameArrayNoSubs[i] = carNameArrayNoSubs[i];
				Log.i("MngScreen-carNameArray[i]", "" + carNameArray[i]);
			}
		else {
			carNameArray[0] = "";
			return;
		}
		// convert from array to ArrayList
		carNameArrayList = new ArrayList<String>(Arrays.asList(carNameArray));

		prefKeyValues = getApplicationContext().getSharedPreferences(
				"hashmap_shared_pref", Context.MODE_PRIVATE);

		prefKeyValuesEditor = prefKeyValues.edit();

		carDetailsHMap = new TreeMap<String, String>();

		@SuppressWarnings("unchecked")
		HashMap<String, String> map = (HashMap<String, String>) prefKeyValues
		.getAll();
		for (String keyString : map.keySet()) {
			String value = map.get(keyString);
			carDetailsHMap.put(keyString, value);
			Log.i("carDetailsHMap-key-ManageCarAct: ", "" + keyString);
		}
	
		listView = (ListView) findViewById(R.id.manageListView);
		adapter = new ItemsAdapter(this, carNameArray);

		listView.setAdapter(adapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				clickedItem = position;
				carNameStr = carNameArray[position];
				adapter.setSelectedIndex(position);
				adapter.notifyDataSetChanged();
				selectedCarPrefEditor.putInt("selectedCar", position);
				selectedCarPrefEditor.putBoolean("isSelectedCarPrefPopulated",
						true);
				selectedCarPrefEditor.commit();
			}
		});
	 }
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Intent returnIntent = new Intent();
		setResult(RESULT_CANCELED, returnIntent);        
		finish();
	}

	public void addVehicle(View view) {
//		Intent intent = new Intent(ManageScreenActivity.this,
//				MyCarDetails.class);
//		intent.putExtra("isAddVehicleClickIntent", true);
//		startActivity(intent);
//		finish();
		
		Intent intent2 = new Intent(ManageScreenActivity.this, SigninListviewActivity.class);
		startActivity(intent2);
		finish();
		
	}
	@SuppressWarnings("unused")
	private class ItemsAdapter extends ArrayAdapter<String> {	
		String[] items;
		String[] carName;
		boolean[] checkBoxState;
		TextView carNameText;
		TextView invisibleTextView;
		CheckBox checkBox;
		List<String> itemsList;
		int checkBoxTickPos;
		int selectedIndex = indexOfCarPageSelected; // Vital (keeps 0th checkbox
		// true by default)

		public ItemsAdapter(Context context, String[] items) {
			super(context, R.layout.custom_manage_checklist, items);
			this.items = items;
			checkBoxState = new boolean[items.length];
			itemsList = new ArrayList<String>(Arrays.asList(items));
		}

		public void setSelectedIndex(int index) {
			selectedIndex = index;
		}

		// @Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			View view = convertView;
			if (view == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.custom_manage_checklist, null);
			}

			carNameText = (TextView) view.findViewById(R.id.carNametextView);
			invisibleTextView = (TextView) view
					.findViewById(R.id.invisibleTextView);
			checkBox = (CheckBox) view.findViewById(R.id.checkboxManage);
			carNameText.setText(itemsList.get(position));

			if (selectedIndex == position) {
				checkBox.setChecked(true);
				checkBoxTickPos = position;
			} else {
				checkBox.setChecked(false);
			}

			/** selectedIndex is the selected checkBox, so no-need of listener. */

			invisibleTextView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
					Intent intent = new Intent(ManageScreenActivity.this,
							MyCarDetails.class);
					Log.i("clickedItem: ", "" + clickedItem);
					intent.putExtra("rowItemClicked", carNameArrayNoSubs[position]);
					Log.i("MngCar- carNameArrayNoSubs[position]: ", ""+carNameArrayNoSubs[position]);
					intent.putExtra("rowItemClickedPosition", position);
					Log.i("MngCar- rowItemClickedPosition", ""+position);
					intent.putExtra("isEditing", true);
					startActivity(intent);
					ManageScreenActivity.this.finish();
				}

			});
			invisibleTextView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View arg0) {
					// TODO Auto-generated method stub
					
					alert.setTitle("Alert");
					alert.setMessage("Do you want to delete?");
					// set dialog message
					alert.setCancelable(false).setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int id) {
							try {
								
								if (position < checkBoxTickPos) {
									Log.i("position: ", ""+position);
									Log.i("checkBoxTickPos: ", ""+checkBoxTickPos);
						        	for (int c = 0; c < getCount(); c++) {
						        		if (c == checkBoxTickPos - 1)
						        			checkBox.setChecked(true);
						        		else
						        			checkBox.setChecked(false);
						        	}
						        }
								
								String carname = itemsList
										.get(position);
								
								for (Iterator<Map.Entry<String, String>> it = carDetailsHMap.entrySet().iterator(); it.hasNext();) {
									Map.Entry<String, String> entry = it.next();
									if (entry.getKey().substring(1).equals(carname)) { // 0th index is prepended index see hmap-size in cardetails.java.
										Log.i("entry.getKey(): ", ""+entry.getKey());
										it.remove();
										Log.i("in hmap remove loop:", "yes");
									}
								}

								for (String keyString : carDetailsHMap.keySet()) {
									Log.i("keysAfterDel: ", ""+keyString);
								}

								prefKeyValuesEditor.clear();
								for (String keyString : carDetailsHMap.keySet()) {
									prefKeyValuesEditor.putString(keyString,carDetailsHMap.get(keyString));
								}

								prefKeyValuesEditor.commit();

								for (Map.Entry<String, String> entry1 : carDetailsHMap.entrySet()) {
									System.out.println("Key = " + entry1.getKey() + ", Value = " + entry1.getValue());
								}

								itemsList.remove(position);
								
								resultantCarNameArray = new String[carDetailsHMap.size()];
								StringBuilder strbuildercarNames = new StringBuilder();
								int i = 0, k = 0;
								for (String keyString : carDetailsHMap.keySet()) {
									resultantCarNameArray[i++] = keyString;
									Log.i("resultantCarNameArray:[i] ", "" + resultantCarNameArray[k++]);
								}

								for (int j = 0; j < resultantCarNameArray.length; j++) {
									strbuildercarNames.append(resultantCarNameArray[j]).append(",");
								}

								
						        
							   
								//prefsEditor.clear();
								prefsEditor = pref.edit();
								prefsEditor.putString(AppConstants.CAR_NAME_PREF,
										strbuildercarNames.toString());
								prefsEditor.putBoolean("isPreferencePopulated", false); //true - false set by BS
								prefsEditor.commit();



								adapter.notifyDataSetChanged();


							} catch (Exception e) {
								// TODO: handle exception
							}
						}
					});
					alert.setCancelable(false).setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int id) {
							dialog.dismiss();
						}
					});

					AlertDialog alertDialog = alert.create();
					alertDialog.show();

					return false;
				}
			});
			return view;
		}

		public int getCount() {
			return itemsList.size();
		}

		public long getItemId(int position) {
			return position;
		}
	}

}