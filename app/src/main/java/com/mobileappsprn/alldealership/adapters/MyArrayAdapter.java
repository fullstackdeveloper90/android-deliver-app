package com.mobileappsprn.alldealership.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobileappsprn.alldealership.GlobalState;
import com.mobileappsprn.alldealership.R;
import com.mobileappsprn.alldealership.entities.AppConstants;
import com.mobileappsprn.alldealership.utilities.Utils;


public class MyArrayAdapter extends ArrayAdapter<String> {
	private Context context;
	private String[] titles;
	private String[] substitles;

	public MyArrayAdapter(Context context, String[] titles, String[] substitles)
    {
		super(context, R.layout.custom_list, titles);
		this.context = context;
		this.titles = titles;
		this.substitles = substitles;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return titles.length;
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.custom_list, parent, false);
        TextView textViewTitle = (TextView) rowView.findViewById(R.id.title);
        TextView textViewSubTitle = (TextView) rowView.findViewById(R.id.subtitle);
        TextView newTextView = (TextView) rowView.findViewById(R.id.newTitle);
        ImageView imageViewIcons = (ImageView) rowView.findViewById(R.id.list_image);

        textViewTitle.setText(titles[position]);
        textViewSubTitle.setText(substitles[position]);

        if(GlobalState.isNewSubMenu )
        {
            if (position % 2 == 0)
            {
                if ((GlobalState.rowBGColor1!=null) && (GlobalState.rowFGColor1!=null))
                {
                    rowView.setBackgroundColor(Color.parseColor(GlobalState.rowBGColor1));
                    newTextView.setTextColor(Color.parseColor(GlobalState.rowFGColor1));
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
                    newTextView.setTextColor(Color.parseColor(GlobalState.rowFGColor2));
                }
            }
         }

        imageViewIcons.setImageBitmap(Utils.getBitmapFromAsset(GlobalState.menuItems.get(position).getShowIconType(), context));
        return rowView;
        }
	}