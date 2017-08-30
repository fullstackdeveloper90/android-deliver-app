package com.mobileappsprn.alldealership.adapters;

import java.util.ArrayList;

import com.mobileappsprn.alldealership.R;
import com.mobileappsprn.alldealership.entities.GasItems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GasStationAdapter extends BaseAdapter{
	private Context context;
	private LayoutInflater l_inflate;
	private ArrayList<GasItems> stationItems;
	public GasStationAdapter(Context context,
			ArrayList<GasItems> stationlist) {
		this.context=context;
		this.stationItems=stationlist;
       l_inflate = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return stationItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return stationItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = l_inflate.inflate(R.layout.snippet_station_listrow, parent, false);
		TextView textView_station = (TextView) rowView.findViewById(R.id.text_station);
		textView_station.setText(stationItems.get(position).getStation());
		
		TextView textView_address = (TextView) rowView.findViewById(R.id.text_address);
		textView_address.setText(stationItems.get(position).getAddress());
		
		TextView textView_price = (TextView) rowView.findViewById(R.id.text_price);
		textView_price.setText("$"+stationItems.get(position).getReg_price());
		
		TextView textView_distance = (TextView) rowView.findViewById(R.id.text_distance);
		textView_distance.setText(stationItems.get(position).getDistance());
		

		
		return rowView;
	}

}
