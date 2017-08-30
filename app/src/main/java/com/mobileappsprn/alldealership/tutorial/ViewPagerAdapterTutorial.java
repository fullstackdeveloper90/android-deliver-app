package com.mobileappsprn.alldealership.tutorial;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mobileappsprn.alldealership.R;


public class ViewPagerAdapterTutorial extends PagerAdapter {
    Context context;

    LayoutInflater inflater;
    int[] mImageResources;
    public ViewPagerAdapterTutorial(Context context, int[] mImageResources) {
        this.context = context;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mImageResources =mImageResources;
    }

    @Override
    public int getCount() {
        return mImageResources.length;
    }



    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        // Declare Variables

        ImageView imgflag;

        View itemView = inflater.inflate(R.layout.item_tutorial, container,
                false);
        // Locate the ImageView in viewpager_item.global_trackers
        imgflag = (ImageView) itemView.findViewById(R.id.imageView);
        // Capture position and set to the ImageView
       imgflag.setImageResource(mImageResources[position]);

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
