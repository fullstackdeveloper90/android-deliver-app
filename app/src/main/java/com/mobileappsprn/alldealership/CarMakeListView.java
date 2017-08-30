package com.mobileappsprn.alldealership;

import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mobileappsprn.alldealership.entities.AppConstants;
import com.mobileappsprn.alldealership.utilities.ApplicationVariables;

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
import android.widget.Toast;

public class CarMakeListView extends Activity {

    ArrayList<String> carMake;
    ListView listView;
    ItemsAdapter adapter;
    String carMakeStr;
    String[] carMakeTitles;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_make_listview);
        // Obtain the shared Tracker instance.
        ApplicationVariables application = (ApplicationVariables) getApplication();
        mTracker = application.getDefaultTracker();
        Log.v("FLOW", "CAME HERE : CarMakeListView");
        carMake = new ArrayList<String>();

        try {
            InputStream istr = this.getAssets().open(AppConstants.JSON_FILE_PATH + "VehMakesModels.json");


            int size = istr.available();
            byte[] buffer = new byte[size];
            istr.read(buffer);
            istr.close();
            String bufferString = new String(buffer);

            JSONObject json = new JSONObject(bufferString);

            JSONArray jsonArray = json.getJSONArray("Items");

            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject jsonObj = jsonArray.getJSONObject(j);
                carMake.add(jsonObj.getString("Title"));
            }

            GlobalState.carMake = carMake;

        } catch (Exception e) {
            e.printStackTrace();
        }

        carMakeTitles = new String[carMake.size()];

        for (int i = 0; i < carMake.size(); i++)
            carMakeTitles[i] = carMake.get(i);

        listView = (ListView) findViewById(R.id.carMakeListView);
        adapter = new ItemsAdapter(this, carMakeTitles);

        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        this.setTitle("Select Make");

        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                try {
                    Log.d("ok", "OnItem Clicked " + position + "  id  " + id);
                    CheckedTextView ctv = (CheckedTextView) view;
                    ctv.setChecked(!ctv.isChecked());
                    ctv.toggle();

                    carMakeStr = carMakeTitles[position];
                    GlobalState.selectedCarIndex = position;
                } catch (Exception ex) {
                    ex.printStackTrace();

                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mTracker != null) {
            mTracker.setScreenName(getClass().getSimpleName());
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    @Override
    public void finish() {
        // Prepare data intent
        Intent returnIntent = new Intent();
        returnIntent.putExtra("SelectedCar", carMakeStr);
        GlobalState.selectedCar = carMakeStr;
        setResult(RESULT_OK, returnIntent);
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
                v = vi.inflate(R.layout.items_carmake_checklist, null);
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
        getMenuInflater().inflate(R.menu.car_make_list_view, menu);
        return true;
    }

}
