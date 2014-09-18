package com.mp.android.newsfeed;


import com.mp.android.newsfeed.NoticeAdapter.AttachmentOnClickListener;

import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



public class NewsFeedViewHolderPicture extends NewsFeedViewHolder {
	
	public ImageView mStatus_img;
	public AttachmentOnClickListener mImgListener;
	public LinearLayout mAttachment_panel;
	public ViewStub viewStub_audio;
	public ViewStub viewStub_image;
	public View currentViewItem;
	public TextView viewStub_audio_time ;
	public ImageView viewStub_audio_panel;
	public AttachmentOnClickListener mAudioListener;
}
