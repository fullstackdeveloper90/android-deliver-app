package com.mobileappsprn.alldealership.entities;

import java.util.ArrayList;

public class RootItems {
	private String title;
	private String url;
	private String showicon;
	private String customURL;
	public String getCustomURL() {
		return customURL;
	}
	public void setCustomURL(String customURL) {
		this.customURL = customURL;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public ArrayList<Details> getDetails() {
		return details;
	}
	public void setDetails(ArrayList<Details> details) {
		this.details = details;
	}

	public String getShowicon() {
		return showicon;
	}
	public void setShowicon(String showicon) {
		this.showicon = showicon;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	private ArrayList<Details> details;
	
	
}
