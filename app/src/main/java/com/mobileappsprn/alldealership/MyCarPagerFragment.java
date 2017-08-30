package com.mobileappsprn.alldealership;


import java.io.File;

import com.mobileappsprn.alldealership.utilities.BitmapResizer;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MyCarPagerFragment extends Fragment {
    View view;
    int pageNum;

    Bitmap imageBitmap;

    int reqWidthPx = 0;
    int reqHeightPx = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LayoutInflater inflater1 = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater1.inflate(R.layout.my_car_activity_fragment, null);
        Log.v("FLOW", "CAME HERE : MyCarPagerFragment");
        Bundle bundle = getArguments();
        pageNum = bundle.getInt("page");


        ImageView imgView = (ImageView) view.findViewById(R.id.logo);

        try {
            if (!GlobalState.uploadedImagePath.get(pageNum).equals("photoNotAvailable") && !GlobalState.uploadedImagePath.get(pageNum).contains("http:")) {
                File selectedFile = new File(GlobalState.uploadedImagePath.get(pageNum));
                DisplayMetrics metrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
                imageBitmap = BitmapResizer.decodeFile(selectedFile, metrics.widthPixels);
                imgView.setImageBitmap(imageBitmap);

            } else if (!GlobalState.uploadedImagePath.get(pageNum).equals("photoNotAvailable") && GlobalState.uploadedImagePath.get(pageNum).contains("http:")) {
                Picasso.with(getActivity()).load(GlobalState.uploadedImagePath.get(pageNum)).into(imgView);
            } else {
                imgView.setImageResource(R.drawable.photo_not_available);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return view;
    }
}
