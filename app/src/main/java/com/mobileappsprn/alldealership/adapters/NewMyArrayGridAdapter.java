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
import com.mobileappsprn.alldealership.utilities.Utils;


    public class NewMyArrayGridAdapter extends ArrayAdapter<String>
    {
        private Context context;
        String[] images;

        public NewMyArrayGridAdapter(Context context, String[] images)
        {
            super(context, R.layout.new_activity_main_gridview);
            this.context = context;
            this.images = images;
        }

        @Override
        public int getCount()
        {
            // TODO Auto-generated method stub
            return images.length;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.new_activity_main_gridview, parent, false);
            ImageView imageViewIcons = (ImageView) rowView.findViewById(R.id.grid_image);

            //rowView.setBackgroundColor(Color.parseColor(GlobalState.rowBGColor2));
            Bitmap bitmap = Utils.getBitmapFromAsset(images[position], context);
            imageViewIcons.setImageBitmap(bitmap);

            return rowView;
        }
    }