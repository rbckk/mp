package com.mp.android.newsfeed;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NewsFeedViewHolder {
	public ImageView mAvatar;
	public TextView mName;
	//public ImageButton mMore;
	public TextView mContent;

	public TextView mComment_Count;
	public TextView mComment_1;
	public TextView mComment_1_Time;
	public TextView mComment_2;
	public TextView mComment_2_Time;
	public ImageView mType;
	public TextView mTime;
	public TextView mSource;
	//public TextView mLocation;
	public ImageView mZhuanfa;
	public ImageView mDownloadcode;
	//public TextView mPinglun;
	//public TextView mShoucang;
	public TextView madcode;

	public LinearLayout mStatus_Root;
	public TextView mStatus_Name;
	public TextView mStatus_Content;

	public LinearLayout mSharePhoto_Root;
	public TextView mSharePhoto_Name;
	public TextView mSharePhoto_Description;
	public ImageView mSharePhoto_Image;
	public TextView mSharePhoto_Title;

	public LinearLayout mShareAlbum_Root;
	public TextView mShareAlbum_Name;
	public TextView mShareAlbum_Title;
	public ImageView mShareAlbum_Image;

	public LinearLayout mShareBlog_Root;
	public TextView mShareBlog_Name;
	public TextView mShareBlog_Title;
	public TextView mShareBlog_Description;

	public LinearLayout mPublishBlog_Root;
	public TextView mPublishBlog_Title;
	public TextView mPublishBlog_Description;

	public LinearLayout mPublishPhoto_Root;
	public TextView mPublishPhoto_Content;
	public ImageView mPublishPhoto_Image;
	public TextView mPublishPhoto_Title;
}
