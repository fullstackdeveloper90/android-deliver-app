package com.mobileappsprn.alldealership.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mobileappsprn.alldealership.R;
import com.mobileappsprn.alldealership.entities.Items;


public class QRFavAdapter extends ArrayAdapter<Items>{
	private Context context;
	
	private ArrayList<Items> items;
 
	public QRFavAdapter(Context context, int layoutResource, ArrayList<Items> items) {
		super(context, layoutResource);
		this.context = context;
		this.items = items;
	}
	
	@Override
	public int getCount() {
		return items.size();
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		
		View rowView = inflater.inflate(R.layout.fav_list_item, parent, false);
		TextView textViewTitle = (TextView) rowView.findViewById(R.id.title);	
		TextView textViewSubtitle=(TextView) rowView.findViewById(R.id.subtitle);	
		
		textViewTitle.setText(items.get(position).getTitle());
		textViewSubtitle.setText(items.get(position).getUrl());	
		return rowView;
	}
 }