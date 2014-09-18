package com.mp.android.statusnet.statusnet;

import java.util.ArrayList;
import java.util.Date;

public class RowStatus {

	private long id;
	private long statusId;
	private long accountId;
	private long userId;
	private String screenName;
	private String source;
	private long inReplyTo;
	private String inReplyToScreenName;
	private long repeatedId;
	private String repeatedByScreenName;
	private String profileImage;
	private String profileUrl;
	private long dateTime;
	private int geolocation;
	private String lon;
	private String lat;
	private int attachment;
	private String status;
	private String status_html;
	private String name;
	private ArrayList<String> attachment_url;
	private String notice_type;
	private long reply_count;
	private int favorited;
	private Date created;
	private String location;
	private String adcode;
	
	public Date getCreated() {
		return created;
	}

	public String getadcode(){
		return adcode;
	}

	public void setadcode(String code){
		this.adcode = code;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
	public String getNotice_type() {
		return notice_type;
	}
	public void setNotice_type(String notice_type) {
		this.notice_type = notice_type;
	}
	
	public void setAttachment_url(ArrayList<String> attachment_url) {
		this.attachment_url = attachment_url;
	}
	public ArrayList<String> getAttachment_url() {
		return attachment_url;
	}
	public long getAccountId() {
		return accountId;
	}
	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getScreenName() {
		return screenName;
	}
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	public String getInReplyToScreenName() {
		return inReplyToScreenName;
	}
	public void setInReplyToScreenName(String inReplyToScreenName) {
		this.inReplyToScreenName = inReplyToScreenName;
	}
        public String getRepeatedByScreenName() {
                return repeatedByScreenName;
        }
        public void setRepeatedByScreenName(String repeatedByScreenName) {
                this.repeatedByScreenName = repeatedByScreenName;
        }
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public long getInReplyTo() {
		return inReplyTo;
	}
	public void setInReplyTo(long inReplyTo) {
		this.inReplyTo = inReplyTo;
	}
    
    public long getRepeatedId() {
        return repeatedId;
    }
    
    public void setRepeatedId(long repeatedId) {
        this.repeatedId = repeatedId;
    }
    
	public String getProfileImage() {
		return profileImage;
	}
	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}
	public String getProfileUrl() {
		return profileUrl;
	}
	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}
	public long getDateTime() {
		return dateTime;
	}
	public void setDateTime(long dateTime) {
		this.dateTime = dateTime;
	}
	public int getGeolocation() {
		return geolocation;
	}
	public void setGeolocation(int geolocation) {
		this.geolocation = geolocation;
	}
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public int getAttachment() {
		return attachment;
	}
	public void setAttachment(int attachment) {
		this.attachment = attachment;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatusHtml() {
		return status_html;
	}
	public void setStatusHtml(String status_html) {
		this.status_html = status_html;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public int getFavorited() {
		return favorited;
	}
	public void setFavorited(int favoried) {
		this.favorited = favoried;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getStatusId() {
		return statusId;
	}
	public void setStatusId(long statusId) {
		this.statusId = statusId;
	}
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public long getReplyCount() {
		return reply_count;
	}
	public void setReplyCount(long reply_count) {
		this.reply_count = reply_count;
	}
	@Override
	public String toString() {
		return "RowStatus [id=" + id + ", statusId=" + statusId
				+ ", accountId=" + accountId + ", userId=" + userId
				+ ", screenName=" + screenName + ", source=" + source
				+ ", inReplyTo=" + inReplyTo + ", inReplyToScreenName="
				+ inReplyToScreenName + ", repeatedId=" + repeatedId
				+ ", repeatedByScreenName=" + repeatedByScreenName
				+ ", profileImage=" + profileImage + ", profileUrl="
				+ profileUrl + ", dateTime=" + dateTime + ", geolocation="
				+ geolocation + ", lon=" + lon + ", lat=" + lat
				+ ", attachment=" + attachment + ", status=" + status
				+ ", status_html=" + status_html
				+ ", location=" + location
				+ ", reply_count=" + reply_count
				+ ", name=" + name + "]";
	}

    
}
