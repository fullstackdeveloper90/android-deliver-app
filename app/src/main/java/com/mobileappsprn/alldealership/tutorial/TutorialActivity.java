package com.mobileappsprn.alldealership.tutorial;

/*
 * Copyright 2016, t24 Client
 * This software is released under the terms and conditions of the t24 Client
 */

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mobileappsprn.alldealership.HelperActivity;
import com.mobileappsprn.alldealership.R;
import com.mobileappsprn.alldealership.utilities.ApplicationVariables;

/**
 * This class is used for intro of the app
 * It's a one time process.
 */
public class TutorialActivity extends FragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private ViewPager vpItem;

    private ImageView[] dots;
    Activity activity;

    private TextView tvSignIn, tvExplore;
    private TextView tvSkip;
    ImageView imActiveORInactive1, imActiveORInactive2, imActiveORInactive0, imActiveORInactive3/*, imActiveORInactive4*/;

    LinearLayout lLayoutBottom;

    RelativeLayout rLayoutParrent;

    ViewPagerAdapterTutorial mAdapter;
    private int[] mImageResources = {
            R.drawable.tutorialone,
            R.drawable.tutorialtwo,
            R.drawable.tutorialthree,
            R.drawable.tutorialfour
    };

    int dotsCount;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_tutorial);
        // Obtain the shared Tracker instance.
        ApplicationVariables application = (ApplicationVariables) getApplication();
        mTracker    =   application.getDefaultTracker();
        Log.v("FLOW", "CAME HERE : TutorialActivity");
        activity = this;
        initView();

    }

    private void initView() {

        preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        vpItem = (ViewPager) findViewById(R.id.vgView);
        vpItem.setCurrentItem(0);
        tvSignIn = (TextView) findViewById(R.id.tv_signin);
        tvExplore = (TextView) findViewById(R.id.tv_explore);
        //  tvSignUp=(TextView)findViewById(R.id.tv_signup);
        tvSkip = (TextView) findViewById(R.id.tv_skip);

        lLayoutBottom = (LinearLayout) findViewById(R.id.lLayout_bottom);

        rLayoutParrent = (RelativeLayout) findViewById(R.id.rlayout_parrent);

        tvSignIn.setOnClickListener(this);
        tvExplore.setOnClickListener(this);

        tvSkip.setOnClickListener(this);

        lLayoutBottom.setOnClickListener(this);

        vpItem.setOnPageChangeListener(this);

        lLayoutBottom.setVisibility(View.INVISIBLE);

        imActiveORInactive0 = (ImageView) findViewById(R.id.im_active_or_inactive1);
        imActiveORInactive1 = (ImageView) findViewById(R.id.im_active_or_inactive2);
        imActiveORInactive2 = (ImageView) findViewById(R.id.im_active_or_inactive3);
        imActiveORInactive3 = (ImageView) findViewById(R.id.im_active_or_inactive4);
        //imActiveORInactive4 = (ImageView) findViewById(R.id.im_active_or_inactive5);

        //Font
       // Utils.overrideFonts(activity, rLayoutParrent);

        //setViewController();
        loadImage();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mTracker != null) {
            mTracker.setScreenName(getClass().getSimpleName());
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_skip:

                editor = preferences.edit();
                editor.putBoolean("isTutorialShown", true);
                editor.commit();
                startActivity(new Intent(activity, HelperActivity.class));
                finish();
                break;

        }
    }


    public void setActvieInactiveState(int currentPosition) {
        switch (currentPosition) {
            case 0:
                imActiveORInactive0.setImageResource(R.drawable.ic_dot_active);
                imActiveORInactive1.setImageResource(R.drawable.ic_dot_inactive);
                imActiveORInactive2.setImageResource(R.drawable.ic_dot_inactive);
                imActiveORInactive3.setImageResource(R.drawable.ic_dot_inactive);
                //imActiveORInactive4.setBackgroundResource(R.drawable.ic_dot_inactive);
                break;
            case 1:
                imActiveORInactive0.setImageResource(R.drawable.ic_dot_inactive);
                imActiveORInactive1.setImageResource(R.drawable.ic_dot_active);
                imActiveORInactive2.setImageResource(R.drawable.ic_dot_inactive);
                imActiveORInactive3.setImageResource(R.drawable.ic_dot_inactive);
                //imActiveORInactive4.setBackgroundResource(R.drawable.ic_dot_inactive);
                break;
            case 2:
                imActiveORInactive0.setImageResource(R.drawable.ic_dot_inactive);
                imActiveORInactive1.setImageResource(R.drawable.ic_dot_inactive);
                imActiveORInactive2.setImageResource(R.drawable.ic_dot_active);
                imActiveORInactive3.setImageResource(R.drawable.ic_dot_inactive);
                //imActiveORInactive4.setBackgroundResource(R.drawable.ic_dot_inactive);
                break;
            case 3:
                imActiveORInactive0.setImageResource(R.drawable.ic_dot_inactive);
                imActiveORInactive1.setImageResource(R.drawable.ic_dot_inactive);
                imActiveORInactive2.setImageResource(R.drawable.ic_dot_inactive);
                //imActiveORInactive4.setBackgroundResource(R.drawable.ic_dot_inactive);
                imActiveORInactive3.setImageResource(R.drawable.ic_dot_active);
                break;
            /*case 4:
                imActiveORInactive0.setBackgroundResource(R.drawable.ic_dot_inactive);
                imActiveORInactive1.setBackgroundResource(R.drawable.ic_dot_inactive);
                imActiveORInactive2.setBackgroundResource(R.drawable.ic_dot_inactive);
                imActiveORInactive3.setBackgroundResource(R.drawable.ic_dot_inactive);
                imActiveORInactive4.setBackgroundResource(R.drawable.ic_dot_active);
                break;*/
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.v("Tutorial", "The Tutorial Position : "+position);
        setActvieInactiveState(position);
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void loadImage() {
        mAdapter = new ViewPagerAdapterTutorial(activity, mImageResources);
        vpItem.setOffscreenPageLimit(3);
        vpItem.setAdapter(mAdapter);
    }
}
