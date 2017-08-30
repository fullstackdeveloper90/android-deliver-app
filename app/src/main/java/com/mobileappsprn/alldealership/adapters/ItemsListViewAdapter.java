package com.mobileappsprn.alldealership.adapters;

import java.util.ArrayList;

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
import com.mobileappsprn.alldealership.entities.Items;
import com.mobileappsprn.alldealership.utilities.Utils;


public class ItemsListViewAdapter extends ArrayAdapter<Items>{
	private Context context;
    Bitmap bitmap =null;
	private ArrayList<Items> items;
 
	public ItemsListViewAdapter(Context context, int layoutResource, ArrayList<Items> items) {
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
 
		Items current = items.get(position);
		
		View rowView = inflater.inflate(R.layout.custom_list, parent, false);

		TextView textViewTitle = (TextView) rowView.findViewById(R.id.title);
		TextView textViewSubTitle = (TextView) rowView.findViewById(R.id.subtitle);
        TextView newTextView = (TextView) rowView.findViewById(R.id.newTitle);
		ImageView imageViewIcons = (ImageView) rowView.findViewById(R.id.list_image);
        ImageView arrow_image=(ImageView)rowView.findViewById(R.id.arrow_image);

        if (GlobalState.isNewSubMenu)
        {
            textViewTitle = (TextView) rowView.findViewById(R.id.newTitle);
            textViewTitle.setVisibility(View.VISIBLE);
            textViewTitle.setText(items.get(position).getTitle());
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

            bitmap = Utils.getBitmapFromAsset(AppConstants.MENU_ICONS2_FILE_PATH + items.get(position).getShowIconType(), context);
        }
        else
        {

            textViewTitle.setText((current.getTitle() == null) ? "Title Missing" : current.getTitle());
            textViewSubTitle.setText((current.getSubtitle() == null) ? "" : current.getSubtitle());
            bitmap = Utils.getBitmapFromAsset((current.getShowIconType() == null) ? AppConstants.GENERAL_ICON_FILE_PATH + "btn-QuestionMark" : AppConstants.GENERAL_ICON_FILE_PATH+current.getShowIconType(), context);

        }
        imageViewIcons.setImageBitmap(bitmap);
		return rowView;
	}

}