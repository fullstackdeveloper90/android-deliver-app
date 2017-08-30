package com.mobileappsprn.alldealership.adapters;

import java.util.ArrayList;

import com.flurry.sdk.el;
import com.mobileappsprn.alldealership.GlobalState;
import com.mobileappsprn.alldealership.R;
import com.mobileappsprn.alldealership.entities.AppConstants;
import com.mobileappsprn.alldealership.entities.Items;
import com.mobileappsprn.alldealership.utilities.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class SigninListviewAdapter extends ArrayAdapter<String> {
	private Context context;
    String[] titles;
	ArrayList<Items> listviewItemsList;
    Bitmap bitmap =null;

    public SigninListviewAdapter(Context context, String[] titles, ArrayList<Items> listviewItemsList) {
		super(context, R.layout.signin_custom_list, titles);
		this.context = context;
		this.titles = titles;
		this.listviewItemsList = listviewItemsList;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		// return titles.length;
		return listviewItemsList.size();
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
    {
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.signin_custom_list, parent, false);

        ImageView imageViewIcons = (ImageView) rowView.findViewById(R.id.list_image);
        ImageView arrow_image=(ImageView)rowView.findViewById(R.id.arrow_image);
        TextView newTextView = (TextView) rowView.findViewById(R.id.newTitle);

        if (GlobalState.isNewSubMenu)
        {
            TextView textViewTitle = (TextView) rowView.findViewById(R.id.newTitle);
            textViewTitle.setVisibility(View.VISIBLE);
            textViewTitle.setText(listviewItemsList.get(position).getTitle());
            ((TextView) rowView.findViewById(R.id.title)).setVisibility(View.GONE);
            arrow_image.setVisibility(View.VISIBLE);

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

            bitmap = Utils.getBitmapFromAsset(AppConstants.MENU_ICONS2_FILE_PATH + listviewItemsList.get(position).getShowIconType(), context);
        }
        else
        {
            TextView  textViewTitle = (TextView) rowView.findViewById(R.id.title);
            textViewTitle.setText(listviewItemsList.get(position).getTitle());
            bitmap = Utils.getBitmapFromAsset(AppConstants.GENERAL_ICON_FILE_PATH + listviewItemsList.get(position).getShowIconType(), context);
        }
        imageViewIcons.setImageBitmap(bitmap);
        return rowView;
        }
	}
