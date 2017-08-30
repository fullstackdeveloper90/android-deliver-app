package com.mobileappsprn.alldealership.tutorial;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobileappsprn.alldealership.R;
import com.mobileappsprn.alldealership.entities.Items;

import java.util.ArrayList;


public class ViewPagerBottomTickerAdapter extends PagerAdapter {
    Context context;

    private LayoutInflater inflater;
    private ArrayList<Items> bottomTickerArray;
    private String foreGColor;
    private String backGColor;

    //int[] mImageResources;
    public ViewPagerBottomTickerAdapter(Context context, ArrayList<Items> bottomTickerArray, String foreGColor, String backGColor) {
        this.context = context;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.bottomTickerArray = bottomTickerArray;
        this.foreGColor = foreGColor;
        this.backGColor = backGColor;
    }

    @Override
    public int getCount() {
        return bottomTickerArray.size();
    }



    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        // Declare Variables

        TextView textFlag;

        View itemView = inflater.inflate(R.layout.item_bottom_ticker, container,
                false);
        // Locate the ImageView in viewpager_item.global_trackers
        textFlag = (TextView) itemView.findViewById(R.id.textView);
        // Capture position and set to the ImageView
        textFlag.setText(bottomTickerArray.get(position).getDisplay());
        textFlag.setBackgroundColor(Color.parseColor(backGColor));
        textFlag.setTextColor(Color.parseColor(foreGColor));

        // Add viewpager_item.global_trackers to ViewPager
        ((ViewPager) container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.global_trackers from ViewPager
        ((ViewPager) container).removeView((LinearLayout) object);

    }
}
