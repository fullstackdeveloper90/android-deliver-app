package com.mobileappsprn.alldealership.entities;

public class Details {
	private String title;
	private String detailText;
	private String type;
	private int sortOrder;
	private String eventName;
	private String eventDetailText;
	private String length;
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
	public String getDetailText() {
		return detailText;
	}
	public void setDetailText(String detailText) {
		this.detailText = detailText;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public String getEventDetailText() {
		return eventDetailText;
	}
	public void setEventDetailText(String eventDetailText) {
		this.eventDetailText = eventDetailText;
	}
	public String getLength() {
		return length;
	}
	public void setLength(String length) {
		this.length = length;
	}
	public String getShowIconType() {
		return showIconType;
	}
	public void setShowIconType(String showIconType) {
		this.showIconType = showIconType;
	}
	String showIconType;
}
