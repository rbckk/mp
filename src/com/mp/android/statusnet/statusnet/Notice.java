/*
 * MUSTARD: Android's Client for StatusNet
 * 
 * Copyright (C) 2009-2010 macno.org, Michele Azzolari
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package com.mp.android.statusnet.statusnet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Notice implements Serializable{
    private static final long serialVersionUID = -4346943780351474501L;
    private long id;
	private String text;
	private boolean truncated;
	private Date created_at;
	//private long in_reply_to_status_id;
	//private String source;
	private long in_reply_to_user_id;
	//private String in_reply_to_screen_name;
	//private boolean favorited;
	private ArrayList<Attachment> attachments;
	//private boolean geo;
	//private double lon;
	//private double lat;
	//private String statusnet_text;
	private long repeated_id;
	//private String repeated_by_screen_name;
	//private String notice_type;
	//private long reply_count;
	//private String location;
	private String adcode;

	public String getadcode(){
		return adcode;
	}
	
	public void setadcode(String code){
		this.adcode = code;
	}
/*	
	public String getNotice_type() {
		return notice_type;
	}
	public void setNotice_type(String notice_type) {
		this.notice_type = notice_type;
	}
	public String getStatusnet_text() {
		return statusnet_text;
	}
	public void setStatusnet_text(String statusnet_text) {
		this.statusnet_text = statusnet_text;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public boolean isGeo() {
		return geo;
	}
	public void setGeo(boolean geo) {
		this.geo = geo;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
*/
	public ArrayList<Attachment> getAttachments() {
		return attachments;
	}
	public void setAttachments(ArrayList<Attachment> attachments) {
		this.attachments = attachments;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public boolean isTruncated() {
		return truncated;
	}
	public void setTruncated(boolean truncated) {
		this.truncated = truncated;
	}
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
/*
	public long getIn_reply_to_status_id() {
		return in_reply_to_status_id;
	}
	public void setIn_reply_to_status_id(long in_reply_to_status_id) {
		this.in_reply_to_status_id = in_reply_to_status_id;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
*/
	public long getIn_reply_to_user_id() {
		return in_reply_to_user_id;
	}
	public void setIn_reply_to_user_id(long in_reply_to_user_id) {
		this.in_reply_to_user_id = in_reply_to_user_id;
	}
/*
	public String getIn_reply_to_screen_name() {
		return in_reply_to_screen_name;
	}
	public void setIn_reply_to_screen_name(String in_reply_to_screen_name) {
		this.in_reply_to_screen_name = in_reply_to_screen_name;
	}
	public boolean isFavorited() {
		return favorited;
	}
	public void setFavorited(boolean favorited) {
		this.favorited = favorited;
	}
*/
        public long getRepeated_id() {
                return repeated_id;
        }
        public void setRepeated_id(long repeated_id) {
                this.repeated_id = repeated_id;
        }
/*
        public String getRepeated_by_screen_name() {
                return repeated_by_screen_name;
        }
        public void setRepeated_by_screen_name(String repeated_by_screen_name) {
                this.repeated_by_screen_name = repeated_by_screen_name;
        }
        public long getReply_count() {
            return reply_count;
    }
	    public void setReply_count(long reply_count) {
	            this.reply_count = reply_count;
	    }
*/        
        @Override
        public String toString() {
            return "Notice [id=" + id 
            		+ ", text=" + text 
            		+ ", truncated=" + truncated 
            		+ ", created_at=" + created_at
                    //+ ", in_reply_to_status_id=" + in_reply_to_status_id
                    //+ ", source=" + source 
                    + ", in_reply_to_user_id=" + in_reply_to_user_id
                    //+ ", in_reply_to_screen_name=" + in_reply_to_screen_name 
                    //+ ", favorited=" + favorited
                    + ", attachments=" + attachments 
                    //+ ", geo=" + geo
                    //+ ", lon=" + lon 
                    //+ ", lat=" + lat 
                    //+ ", statusnet_text="+ statusnet_text 
                    + ", repeated_id=" + repeated_id
                    //+ ", repeated_by_screen_name=" + repeated_by_screen_name
                    //+ ", reply_count=" + reply_count
					//+ ", location=" + location
                    //+ ", notice_type="+notice_type
                    +"]";
        }
        
}
