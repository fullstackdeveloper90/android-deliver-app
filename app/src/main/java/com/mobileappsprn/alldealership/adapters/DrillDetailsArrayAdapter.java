package com.mobileappsprn.alldealership.adapters;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobileappsprn.alldealership.ImageLoader;
import com.mobileappsprn.alldealership.R;



public class DrillDetailsArrayAdapter extends ArrayAdapter<String> {
	private Context context;
	private String[] titles;
	private String[] subtitles;
	ImageView img_thumb;
	ImageLoader loader;
	TextView textViewSubTitle;
	String evDate,cal_title;
	int finalHeight, finalWidth;
 
	public DrillDetailsArrayAdapter(Context context, String[] titles, String[] substitles, String title_Text ) {
		super(context, R.layout.custom_drilldown_details, titles);
		this.context = context;
		this.titles = titles;
		this.subtitles = substitles;
		this.cal_title=title_Text;
		loader = new ImageLoader(context);
	}
	
	@Override
	public int getCount() {
		return subtitles.length;
	}
 
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View rowView = inflater.inflate(R.layout.custom_drilldown_details, parent, false);
		//TextView textViewTitle = (TextView) rowView.findViewById(R.id.title);
		textViewSubTitle = (TextView) rowView.findViewById(R.id.subtitle);
		img_thumb= (ImageView) rowView.findViewById(R.id.detail_thumbImg);
		//textViewTitle.setText(titles[position]);
	
		if (titles[position].equalsIgnoreCase("Title") || titles[position].equalsIgnoreCase("Posted")) {
			textViewSubTitle.setVisibility(View.GONE);
		}
		if(titles[position].equalsIgnoreCase("EventDateStart")||titles[position].equalsIgnoreCase("EventDateEnd")){
			
			textViewSubTitle.setTextColor(context.getResources().getColor(R.color.white));
			textViewSubTitle.setPadding(5, 0, 0, 0);
			rowView.setBackgroundResource(R.color.blue);
			ImageView arrw= (ImageView) rowView.findViewById(R.id.arrowIcon_dtl);
			arrw.setVisibility(View.VISIBLE);
			evDate=subtitles[position];
			textViewSubTitle.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					
					addEvent();
				}
			});
		}
		
		textViewSubTitle.setText(Html.fromHtml(subtitles[position]));
		
		if (titles[position].equalsIgnoreCase("Image")) {
			img_thumb.setVisibility(View.VISIBLE);
			textViewSubTitle.setVisibility(View.GONE);
			try {
				//img_thumb.setImageUrl(substitles[position]);
				loader.DisplayImage(subtitles[position], img_thumb);
			} catch (Exception e) {
                e.printStackTrace();
			}
			/*ViewTreeObserver viewTreeObserver = img_thumb.getViewTreeObserver();
			if (viewTreeObserver.isAlive()) {
			  viewTreeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			    @Override
			    public void onGlobalLayout() {
			    	img_thumb.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			      finalWidth = img_thumb.getWidth();
			      finalHeight = img_thumb.getHeight();
			      System.out.println("height"+finalHeight+":"+finalWidth);
			    }
			  });
			}*/
			
		}
		
		return rowView;
	}

	protected void addEvent() {
		long startTime = 0,endTime= 0;
		 try {
		        Date date = new SimpleDateFormat("MM/dd/yyyy HH:mm").parse(evDate);
		        startTime=date.getTime();
		     Date endDate= new Date(date.getTime() + 60 * 60 * 1000);
		     endTime=endDate.getTime();
		      System.out.println(startTime+"::"+endTime);
		    }
		    catch(Exception e){ }
		
		// Calendar cal = Calendar.getInstance();     
		    Intent intent = new Intent(Intent.ACTION_EDIT);
		    intent.setType("vnd.android.cursor.item/event");
		    intent.putExtra("beginTime", startTime);
		    intent.putExtra("allDay", true);
		   // intent.putExtra("rrule", "FREQ=YEARLY");
		    intent.putExtra("endTime", startTime+60*60*1000);
		    intent.putExtra("title", cal_title);
		   // intent.putExtra("description", "A Test Description from android app");
		    context.startActivity(intent);
		
	}
	
	/*public void addEventCalendar(int pos){
		// TODO Auto-generated method stub
		if(titles[pos].equalsIgnoreCase(""))
		String dtStart = substitles[pos];  
		String dtEnd = messageEntity.event_end_date;  
		
		
		String locName = messageEntity.loc;
		
		//SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");  
		SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {  
			Date date;
			Date endDate;
				
			try {
				date = format.parse(dtStart);					
				
				if(dtEnd.equalsIgnoreCase("")){
					endDate= format.parse(dtStart);		
				}else{
					endDate = format.parse(dtEnd);
				}
				
				final Intent intent = new Intent(Intent.ACTION_EDIT);
				intent.setType("vnd.android.cursor.item/event");
				intent.putExtra("beginTime", date.getTime());
				intent.putExtra("allDay", false);
				//intent.putExtra("rrule", "FREQ=DAILY");
				intent.putExtra("endTime", endDate.getTime());
				intent.putExtra("title", messageEntity.title);
				//intent.putExtra("eventLocation", locationName);
				intent.putExtra("eventLocation", locName);
				startActivity(intent);
				 
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  

		} catch (ParseException e) {  
			// TODO Auto-generated catch block  
			e.printStackTrace();  
		}
	}*/
}

