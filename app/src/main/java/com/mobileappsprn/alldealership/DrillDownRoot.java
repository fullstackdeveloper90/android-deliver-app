package com.mobileappsprn.alldealership;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobileappsprn.alldealership.adapters.JsonArrayAdapter;
import com.mobileappsprn.alldealership.entities.AppConstants;
import com.mobileappsprn.alldealership.entities.Details;
import com.mobileappsprn.alldealership.entities.RootItems;
import com.mobileappsprn.alldealership.utilities.Utils;

public class DrillDownRoot extends FragmentActivity {

	ListView listView;
	ArrayList<RootItems> rootMenuItems = new ArrayList<RootItems>();
	Context context;
	String vin,title;

	TextView textViewTitleBar;
	Button btnAddService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initialise();
		Log.v("FLOW", "CAME HERE : DrillDownRoot");

	}

	public void showAlert(String msg) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		alertDialogBuilder.setTitle("Alert!");

		alertDialogBuilder
		.setMessage(msg)
		.setNeutralButton("Ok",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
					int id) {

				dialog.dismiss();
				finish();
			}
		});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	public void initialise() {

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);


		setContentView(R.layout.drilldown_root);
		//findViewById(R.id.header).setVisibility(View.GONE);
		//setupActionBar();
		textViewTitleBar = (TextView)findViewById(R.id.textViewDrillDownRootTitle);
		//ActionBar ab = getSupportActionBar();
		//ab.setDisplayHomeAsUpEnabled(true);

		btnAddService = (Button) findViewById(R.id.btnAddService);

		Intent intent = getIntent();
		String feedUrl = intent.getStringExtra("feedUrl1"); //feedUrl1

		int positionFeed1 = intent.getIntExtra("positionFeed1", -1); //BS positionFeed1

		int tagValue = intent.getIntExtra("tagValue1", -1); //BS tagValue1
		Log.i("tagValue", ""+tagValue);

		if (tagValue == AppConstants.SERVICE_HISTORY_TAG) {
			textViewTitleBar.setText("Service");
			btnAddService.setVisibility(View.VISIBLE);

		}
		vin = intent.getStringExtra("vin1"); //BS vin1
		title = intent.getStringExtra("Title");

		Log.v("DrillDown","Values Drill : "+feedUrl+"    "+positionFeed1+"    "+tagValue+"     "+vin+"     "+title);

		setupActionBar();

		/*if (tagValue == AppConstants.VEHICLE_INFO_TAG) {
			textViewTitleBar.setText("Vehicle Information");

		} 
		else if (tagValue == AppConstants.SERVICE_HISTORY_TAG) {
			textViewTitleBar.setText("Service");
			btnAddService.setVisibility(View.VISIBLE);

		} else if (tagValue == AppConstants.LOYALTY_POINTS_TAG) {
			textViewTitleBar.setText("Dealership for Life Account Information");

		} else if (tagValue == AppConstants.OWNER_MANUALS_TAG) {
			textViewTitleBar.setText("Owners Manuals");

		} else if (tagValue == AppConstants.MAINTENANCE_TAG) {
			textViewTitleBar.setText("Suggested Maintenance");

		} else if (tagValue == AppConstants.ACCESSORIES_TAG) {
			textViewTitleBar.setText("Accessories");
		} else if (title.length() < 20){
			String titlename = title.substring(0, title.length());
			textViewTitleBar.setText(titlename);
		} else {
			String titleName = title.substring(0, 20);
			textViewTitleBar.setText(titleName + "..");
		}*/


		Log.i("Feed-url: ", "" + feedUrl);

		try {
			JSONObject json = null;
			if (Utils.checkInternetConnection(this)) {
				//json = new JSONObject(intent.getStringExtra("json"));
				json = FetchJson.getJSONfromURL(feedUrl);

				// Get the element that holds the items ( JSONArray )
				JSONArray result = json.getJSONArray("Items");

				for (int i = 0; i < result.length(); i++) { // 3-times ,
					// itemsArray.length
					// == 3
					// (result.length
					// ==3)
					JSONObject c = result.getJSONObject(i);
					Log.i("json string", " parsing data " + c.toString());
					RootItems rootItems = new RootItems();

					if (c.has("Title"))
						rootItems.setTitle(c.getString("Title"));
					if (c.has("URL"))
						rootItems.setUrl(c.getString("URL"));
					if (c.has("customURL"))
						rootItems.setCustomURL(c.getString("customURL"));
					if (c.has("ShowIconType"))
						rootItems.setShowicon(c.getString("ShowIconType"));
					if (c.has("Details")) {
						JSONArray detailsResult1 = c.getJSONArray("Details");
					
						

						ArrayList<Details> menuItemDetails = new ArrayList<Details>();


						// k < 4 --detailsArray.length == 4
						for (int k = 0; k < detailsResult1.length(); k++) {// 4-times
							JSONObject detailObj = detailsResult1
									.getJSONObject(k);

							Details details = new Details();

							if (detailObj.has("Title"))
								details.setTitle(detailObj.getString("Title"));
							else
								details.setTitle("");

							if (detailObj.has("DetailText"))
								details.setDetailText(detailObj
										.getString("DetailText"));
							else
								details.setDetailText("");

							if (detailObj.has("Type"))
								details.setType(detailObj.getString("Type"));
							else
								details.setType("");

							if (detailObj.has("SortOrder"))
								details.setSortOrder(detailObj
										.getInt("SortOrder"));
							else
								details.setSortOrder(-1);

							if (detailObj.has("EventName"))
								details.setEventName(detailObj
										.getString("EventName"));
							else
								details.setEventName("");

							if (detailObj.has("EventDetailText"))
								details.setEventDetailText(detailObj
										.getString("EventDetailText"));
							else
								details.setEventDetailText("");

							if (detailObj.has("Length"))
								details.setLength(detailObj.getString("Length"));
							else
								details.setLength(null);

							

							menuItemDetails.add(details);

						}
						rootItems.setDetails(menuItemDetails);
					}

					// JSONArray detailsResult = json.getJSONArray("Details");

					rootMenuItems.add(rootItems);

				}// for

				GlobalState.rootMenuItems = rootMenuItems;
			} else {
				showAlert("Internet not present! Please try later.");
			}

		} catch (Exception e) {
			showAlert("Internet not present! Please try later.");
			Log.i("log_tag", "Error parsing data " + e.toString());
		}

		String[] titles = new String[rootMenuItems.size()];

		for (int i = 0; i < rootMenuItems.size(); i++)
			titles[i] = rootMenuItems.get(i).getTitle();

		listView = (ListView) findViewById(R.id.listviewjson);
		final JsonArrayAdapter adapter = new JsonArrayAdapter(this, rootMenuItems);
		listView.setAdapter(adapter);

		this.setTitle(GlobalState.menuItems.get(positionFeed1).getDisplay());

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (rootMenuItems.get(position).getUrl()!= null
						|| rootMenuItems.get(position).getCustomURL()!=null) {
					if (rootMenuItems.get(position).getUrl().startsWith("fb")
							|| rootMenuItems.get(position).getCustomURL().startsWith("fb")) 
					{
						try {
							getApplicationContext().getPackageManager().getPackageInfo("com.facebook.katana", 0);
							String url="";
							if(rootMenuItems.get(position).getCustomURL()!=null && !rootMenuItems.get(position).getCustomURL().isEmpty()){
								url=rootMenuItems.get(position).getCustomURL();
							}else{
								url=rootMenuItems.get(position).getUrl();
							}
							System.out.println("custom url"+url);
							
							/*Intent i =new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/nielsendodge"));
							startActivity(i);*/
							
							
							    int versionCode = getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
							    if (versionCode >= 3002850) {
							        Uri uri = Uri.parse("fb://facewebmodal/f?href=" + url);
							        startActivity(new Intent(Intent.ACTION_VIEW, uri));;
							    } else {
							        // open the Facebook app using the old method (fb://profile/id or fb://page/id)
							        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(rootMenuItems.get(position).getUrl())));
							    }
							
							    
						} catch (Exception e)
						{
							String fburl=rootMenuItems.get(position).getUrl();
							fburl=fburl.replace("fb://profile/", "https://www.facebook.com/");
							System.out.println("fburl"+fburl);
							Intent i=  new Intent(Intent.ACTION_VIEW, Uri.parse(rootMenuItems.get(position).getCustomURL()));
							startActivity(i);
						}
					}
					else {
						String url="";
						if(rootMenuItems.get(position).getCustomURL()!=null && !rootMenuItems.get(position).getCustomURL().isEmpty()){
							url=rootMenuItems.get(position).getCustomURL();
						}else{
							url=rootMenuItems.get(position).getUrl();
						}
						Intent intent = new Intent(DrillDownRoot.this, HelpWebViewActivity.class);
						intent.putExtra("helpUrl", url);
						//Log.i("webViewTitle: ", menuItems.get(position).getTitle());
						intent.putExtra("webViewTitle", rootMenuItems.get(position).getTitle());
						intent.putExtra("Tag", "");
						intent.putExtra("browser", true);
						intent.putExtra("position", position);
						startActivity(intent);
					}

				}
				else {
					Intent intent = new Intent(DrillDownRoot.this,
							DrillDownDetails.class);
					intent.putExtra("itemClickedPostion", position);
					Log.i("toolbar-title: ", ""+rootMenuItems.get(position).getTitle());
					intent.putExtra("toolbarTitle", rootMenuItems.get(position).getTitle());
					//intent.putExtra("socialurl", rootMenuItems.get(position).getUrl());
					startActivity(intent);
				}

			}
		});
	}

	public void onClickAddServiceItem(View view) {
		Intent intent = new Intent(DrillDownRoot.this,
				AddServiceItemActivity.class);
		intent.putExtra("vin2", vin);
		startActivityForResult(intent, 1);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1) {

			if(resultCode == RESULT_OK){  
				rootMenuItems.removeAll(rootMenuItems);
				initialise();      
			}
			if (resultCode == RESULT_CANCELED) {    
				rootMenuItems.removeAll(rootMenuItems);
				initialise();
			}
		}
	}//onActivityResult


	private void setupActionBar() {


		TextView help_web_view_title = (TextView) findViewById(R.id.textViewDrillDownRootTitle);
		help_web_view_title.setText(title);

		/*ActionBar ab = getSupportActionBar();
		//ab.setBackgroundDrawable(new ColorDrawable(0x01060000));
		ab.setDisplayShowCustomEnabled(true);
		ab.setDisplayShowTitleEnabled(false);
		LayoutInflater inflator = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflator.inflate(R.layout.drilldown_title_bar, null);  

		ab.setCustomView(v);        
		ab.setDisplayHomeAsUpEnabled(true);*/
	}

	/*@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			finish();
			break;

		default:
			return super.onOptionsItemSelected(item);
		}
		return false;
	}*/

}
