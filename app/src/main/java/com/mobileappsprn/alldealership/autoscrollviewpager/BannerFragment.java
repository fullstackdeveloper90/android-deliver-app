package com.mobileappsprn.alldealership.autoscrollviewpager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mobileappsprn.alldealership.GlobalState;
import com.mobileappsprn.alldealership.HelpWebViewActivity;
import com.mobileappsprn.alldealership.R;
import com.mobileappsprn.alldealership.entities.Items;
import com.mobileappsprn.alldealership.utilities.Utils;

public class BannerFragment extends Fragment{

	View rootView;
	ImageView imageView;
	String imageUrl = null;
	FragmentActivity mAct;
	Items banner;
	
//	@Override
//	public void onAttach(Context activity) {
//		super.onAttach(activity);
//		mAct = getActivity();
//
//	}
	
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.image_frag, container, false);
		imageView = (ImageView)rootView.findViewById(R.id.imageView);
		banner = (Items) getArguments().getSerializable("banner");
		
		String img = null;
		//img = GlobalConstant.uploadUrl+Uri.encode(banner.getImage(), "/");
		//Picasso.with(mAct).load(img).into(imageView);

		imageView.setImageBitmap(Utils.getBitmapFromAsset(banner.getShowIconType(),getActivity()));


		Log.v("Banner",""+banner.getShowIconType());

		/*if(NewMainActivity.sliderPosition >  NewMainActivity.sliderItems.size())
			NewMainActivity.sliderPosition = 0;*/

		/*if(rootView.getTag()==null) {

			rootView.setTag(NewMainActivity.sliderPosition++);
		}*/
		rootView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				gotoWeb(banner.getUrl(), banner.getDisplay() ,banner.getTag(),banner.isBrowser()); //banner.getTitle()

			}
		});
		
		return rootView;
	}

	public void gotoWeb(String url, String title, int tag, boolean isBrowser)
	{
		SharedPreferences mypref = getActivity().getSharedPreferences("HomePrefs", Activity.MODE_PRIVATE);
		String dflId = mypref.getString("dfl_id", "");


		Intent intent = new Intent(getActivity(), HelpWebViewActivity.class);

		String feedUrl = url;//NewMainActivity.sliderItems.get(rowId).getUrl();


		if (feedUrl.contains("www.mobileappscm.com"))
		{
			if (GlobalState.isDealershipGroupVersion) {
				if (feedUrl.contains("SERVERID")) {
					feedUrl = feedUrl.replaceAll("SERVERID", "SERVERID&DFLID=" + dflId);
					if (feedUrl.contains("SERVERID"))
						feedUrl = feedUrl.replaceAll("SERVERID", GlobalState.serverId);
				} else if (feedUrl.contains("?")) {
					feedUrl = feedUrl + "&a=" + GlobalState.serverId + "&DFLID=" + dflId;
				} else {
					feedUrl = feedUrl + "?a=" + GlobalState.serverId + "&DFLID=" + dflId;
				}

			} else {

				if (feedUrl.contains("SERVERID"))
				{
					feedUrl = feedUrl.replaceAll("SERVERID", "SERVERID&DFLID=" + GlobalState.dealershipId);
					if (feedUrl.contains("SERVERID"))
						feedUrl = feedUrl.replaceAll("SERVERID", GlobalState.serverId);
				}
				else if (feedUrl.contains("?"))
				{
					feedUrl = feedUrl + "&a=" + GlobalState.serverId + "&DFLID=" + GlobalState.dealershipId;
				}
				else
				{
					feedUrl = feedUrl + "?a=" + GlobalState.serverId + "&DFLID=" + GlobalState.dealershipId;
				}


               /* if (feedUrl.contains("SERVERID")) {
                    feedUrl = feedUrl.replaceAll("SERVERID", GlobalState.serverId);
                } else if (feedUrl.contains("?")) {
                    feedUrl = feedUrl + "&a=" + GlobalState.serverId;
                } else {
                    feedUrl = feedUrl + "?a=" + GlobalState.serverId;
                }*/

			}

			feedUrl = feedUrl + "&u=" + getUniqueDeviceId() + "&d=" + getUniqueDeviceId() + "&dn=" + getDeviceName();
		}

		intent.putExtra("helpUrl", feedUrl);
		intent.putExtra("webViewTitle", title/*NewMainActivity.sliderItems.get(rowId).getTitle()*/);
		intent.putExtra("Tag", String.valueOf(tag/*NewMainActivity.sliderItems.get(rowId).getTag()*/));
		intent.putExtra("browser", isBrowser/*NewMainActivity.sliderItems.get(rowId).isBrowser()*/);
		intent.putExtra("position", 0);
		Log.i("MainActivity: ", "Url is: " + url /*NewMainActivity.sliderItems.get(rowId).getUrl()*/);
		startActivity(intent);
	}

	public String getUniqueDeviceId() {
		try {
			TelephonyManager telephonyManager = (TelephonyManager) getActivity()
					.getSystemService(Context.TELEPHONY_SERVICE);
			String deviceId = telephonyManager.getDeviceId();
			if (telephonyManager.getDeviceId() == null) {
				String android_id = Settings.Secure.getString(getActivity().getContentResolver(),
						Settings.Secure.ANDROID_ID);
				deviceId = android_id;
			}
			return deviceId;
		} catch (Exception e) {
			Log.i("Dealership", "exception getUniqueDeviceId " + e);
		}
		return null;
	}

	public String getDeviceName() {
		String deviceName = android.os.Build.MODEL;
		return deviceName;
	}


}
