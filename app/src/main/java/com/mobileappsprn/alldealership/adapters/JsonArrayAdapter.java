package com.mobileappsprn.alldealership.adapters;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobileappsprn.alldealership.GlobalState;
import com.mobileappsprn.alldealership.R;
import com.mobileappsprn.alldealership.entities.AppConstants;
import com.mobileappsprn.alldealership.entities.RootItems;


public class JsonArrayAdapter extends BaseAdapter {
	private Context context;
	//private String[] titles;
	private LayoutInflater l_inflate;
	private ArrayList<RootItems> root_item;

	public JsonArrayAdapter(Context context,
			ArrayList<RootItems> rootMenuItems) {
		this.context=context;
		this.root_item=rootMenuItems;
       l_inflate = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		 return root_item.size();
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View rowView = inflater.inflate(R.layout.custom_drilldown_root, parent, false);
		TextView textViewTitle = (TextView) rowView.findViewById(R.id.titleJson);
		ImageView img=(ImageView)rowView.findViewById(R.id.icon_tit);
		textViewTitle.setText(root_item.get(position).getTitle());

		if (position % 2 == 0)
		{
			if ((GlobalState.rowBGColor1!=null) && (GlobalState.rowFGColor1!=null))
			{
				rowView.setBackgroundColor(Color.parseColor(GlobalState.rowBGColor1));
				textViewTitle.setTextColor(Color.parseColor(GlobalState.rowFGColor1));
			}
			else
			{
				rowView.setBackgroundColor(context.getResources().getColor(R.color.grey));
			}
		}
		else
		{
			if ((GlobalState.rowBGColor2!=null) && (GlobalState.rowFGColor2!=null))
			{
				rowView.setBackgroundColor(Color.parseColor(GlobalState.rowBGColor2));
				textViewTitle.setTextColor(Color.parseColor(GlobalState.rowFGColor2));
			}
		}



		System.out.println("root_item.get(position).getShowicon() = " + root_item.get(position).getShowicon());
		if (root_item.get(position).getShowicon()!= null) {
			img.setImageBitmap(getTitleImage(root_item.get(position).getShowicon()));
		}
		
		return rowView;
	}

	public Bitmap getTitleImage(String img_name) {
		AssetManager assetManager = context.getAssets();
		InputStream istr = null;
		try {
			System.out.println("img_name :"+img_name);
			
			if(Arrays.asList(assetManager.list("General-Icons")).contains(img_name))
				istr = assetManager.open("General-Icons/" + img_name);
			else if(Arrays.asList(assetManager.list("Social-Media-Icons")).contains(img_name)){
				istr = assetManager.open(AppConstants.SOCIAL_MEDIA_ICON_FILE_PATH + img_name);
			}else
				istr = assetManager.open(AppConstants.APP_SPECIFIC_ICONS_FILE_PATH + "ic_launcherpng");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Bitmap bm = BitmapFactory.decodeStream(istr);

		try {
			istr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NullPointerException e){
			e.printStackTrace();
		}
		istr = null;
		return bm;
	}



	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return root_item.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
}
