package com.mobileappsprn.alldealership.entities;

import java.io.Serializable;

public class Items implements Serializable
{
	private String title;
	private String subtitle;
	private int tag;
	private boolean browser;
	private String type;
	private String display;
	private String url;
	private String logoUrl;
	private String dfid;
	private String showIconType;
	private String phoneno;
	private String email;
	private String address;
	private String website;
	private String PhoneNoService;

	public String getSliderDuration() {
		return sliderDuration;
	}

	public void setSliderDuration(String sliderDuration) {
		this.sliderDuration = sliderDuration;
	}

	private String sliderDuration;

	public String getPhoneNoService() {
		return PhoneNoService;
	}

	public void setPhoneNoService(String phoneNoService) {
		PhoneNoService = phoneNoService;
	}

	public void setBrowser(boolean value){
		browser = value;
	}

	public boolean isBrowser(){
		return browser;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSubtitle() {
		return subtitle;
	}
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	public int getTag() {
		return tag;
	}
	public void setTag(int tag) {
		this.tag = tag;
	}

	//adds the path name to the icon's location
	public String getShowIconType() {
		return showIconType;
	}
	/*public String getShowIconType() {
		return AppConstants.MFG_ICONS_FILE_PATH + showIconType;
	}*/
	public void setShowIconType(String showIconType) {
		this.showIconType = showIconType;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDisplay() {
		return display;
	}
	public void setDisplay(String display) {
		this.display = display;
	}
	public String getUrl() {
		return url;
	}	
	public void setUrl(String url) {
		this.url = url;
	}
	public String getLogoURL() {
		return logoUrl;
	}
	public void setLogoURL(String logoUrl) {
		this.logoUrl = logoUrl;
	}
	public String getDFLID() {
		return dfid;
	}
	public void setDFLID(String dfid) {
		this.dfid = dfid;
	}

	public String getPhoneno() {
		return phoneno;
	}

	public void setPhoneno(String phoneno) {
		this.phoneno = phoneno;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	private String lat;
	private String lon;

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}
}
