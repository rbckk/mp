package com.mp.android.note.ui;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.mp.android.R;
import com.mp.android.newsfeed.NoticeAdapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ShowAttachmentView extends Activity{
	private ImageView showPicture;
	 protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options = new DisplayImageOptions.Builder()
	.showImageOnLoading(R.drawable.ic_default)
	.showImageForEmptyUri(R.drawable.ic_empty)
	.showImageOnFail(R.drawable.ic_error)
	.cacheInMemory(true)
	.cacheOnDisc(true)
	.considerExifParams(true)
	.displayer(new RoundedBitmapDisplayer(0))
	.build();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_attachment_ui);
		showPicture = (ImageView)findViewById(R.id.note_attachment_show_img);
		String imgPath = getIntent().getExtras().getString("path");
		int type = getIntent().getExtras().getInt(NoticeAdapter.INTENT_TYPE_KEY);
		if(type == 1) {
			imageLoader.displayImage(imgPath, showPicture, options, null);
		}else {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 2;
			Bitmap bm = BitmapFactory.decodeFile(imgPath,options);
			showPicture.setImageBitmap(bm);
		}
		showPicture.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finishActivity();
			}
		});
	}
	
	public void finishActivity() {
		this.finish();
	}
	
	
}
