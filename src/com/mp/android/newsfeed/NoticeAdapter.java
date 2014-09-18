package com.mp.android.newsfeed;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.mp.android.BaseApplication;
import com.mp.android.R;
import com.mp.android.statusnet.MustardDbAdapter;
import com.mp.android.statusnet.provider.StatusNet;
import com.mp.android.statusnet.statusnet.Attachment;
import com.mp.android.statusnet.statusnet.RowStatus;
import com.mp.android.statusnet.view.QuickAction;
import com.mp.android.ui.base.LoadingDialog;
import com.mp.android.util.Text_Util;
import com.mp.android.util.Util;
import android.widget.SeekBar;
//import com.mp.android.audioplay.AudioPlayer;
import com.mp.android.note.ui.ShowAttachmentView;
import android.app.ProgressDialog;

public class NoticeAdapter extends BaseAdapter {
	private BaseApplication mApplication;
	private Context mContext = null;
	private Activity mActivity = null;
	private LayoutInflater mInflater = null;
	private Text_Util mText_Util = null;
	private final int VIEW_TYPE_NO_PICTURE = 0X0001;
	private final int VIEW_TYPE_ATTACHMENT_IMG = 0X00000;
	private final int VIEW_TYPE_ATTACHMENT_AUDIO = 0X0002;
	private ArrayList<RowStatus> datas = new ArrayList<RowStatus>();
	protected static final int K_MIN_HEIGHT_QA = 500;
	private LoadingDialog mDialog;
	private MediaPlayer mMediaPlayer;
	private int mPlayIndex = -1;
	private Map<String, ImageView> mAniImageViews = new HashMap<String, ImageView>();
	private Map<String, TextView> mAudioTextViews = new HashMap<String, TextView>();

	private static final int MSG_PROGRESS = 2;
	private static final int MSG_GEOLOCATION_OK = 3;
	private static final int MSG_GEOLOCATION_KO = 4;
	private static final int MSG_REFRESH = 5;
	private static final int MSG_NOTICEDELETED = 8;
    
    protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;
	DisplayImageOptions mAudioIconoptions;
	public static final String INTENT_TYPE_KEY = "type";
	private AudioPlayTimeCount mAudioCount;
	private long lastTime = 0; 
	ArrayList<Attachment> attachment;
  
//	public AudioPlayer mPlayer;

	public NoticeAdapter(BaseApplication application, Context context,
			Activity activity) {
		mApplication = application;
		mContext = context;
		mActivity = activity;
		mInflater = LayoutInflater.from(mContext);
		mText_Util = new Text_Util();
		mDialog = new LoadingDialog(mContext, R.style.dialog);
		mDialog.setCanceledOnTouchOutside(false);
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_default)
		.showImageForEmptyUri(R.drawable.ic_empty)
		.showImageOnFail(R.drawable.ic_error)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.considerExifParams(true)
		.displayer(new RoundedBitmapDisplayer(0))
		.build();
		mAudioIconoptions = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_default)
		.showImageForEmptyUri(R.drawable.audio_0)
		.showImageOnFail(R.drawable.ic_error)
		.cacheInMemory(false)
		.cacheOnDisc(false)
		.considerExifParams(true)
		.displayer(new RoundedBitmapDisplayer(0))
		.build();
	}

	public int getCount() {
		return datas.size();
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		RowStatus status = (RowStatus) getItem(position);
		if (null == status) {
			return VIEW_TYPE_NO_PICTURE;
		}
		if (null == status.getAttachment_url()) {
			return VIEW_TYPE_NO_PICTURE;
		} else {
			attachment = getAttachemntDetails(status.getId());
			String mime_type = attachment.get(0).getMimeType();
			if(mime_type.contains("image/")) {
				return VIEW_TYPE_ATTACHMENT_IMG;
			}
			else if(mime_type.contains("audio/")) {
				return VIEW_TYPE_ATTACHMENT_AUDIO ;
			}
		}
		return VIEW_TYPE_NO_PICTURE;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 3;
	}
	public Object getItem(int position) {

		return datas.get(position);
	}

	public long getItemId(int position) {
		return position;
	}
/*
	class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
		int progress;

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			this.progress = progress * mPlayer.mediaPlayer.getDuration()
					/ seekBar.getMax();
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			mPlayer.mediaPlayer.seekTo(progress);
		}
	}
*/
	protected MustardDbAdapter getDbAdapter() {
		MustardDbAdapter dbAdapter = new MustardDbAdapter(mContext);
		dbAdapter.open();
		return dbAdapter;
	}
	
	private ArrayList<Attachment> getAttachemntDetails(long statusId) {
		dismissQuickAction();
		MustardDbAdapter mDbHelper = getDbAdapter();
		Cursor c = mDbHelper.fetchAttachment(statusId);
		final CharSequence[] items = new CharSequence[c.getCount()];
		final ArrayList<Attachment> attachments = new ArrayList<Attachment>();
		int cc=0;
		while(c.moveToNext()) {
			Attachment a = new Attachment();
			String mimeType = c.getString(c.getColumnIndex(MustardDbAdapter.KEY_MIMETYPE));
			a.setMimeType(mimeType);
			a.setUrl(c.getString(c.getColumnIndex(MustardDbAdapter.KEY_URL)));
			attachments.add(a);
			if (mimeType.startsWith("image")) {
				items[cc]="Image";
			} else if (mimeType.startsWith("text/html")) {
				items[cc]="Html";
			} else {
				items[cc]="Unknown";
			}
			cc++;
		}
		try {
			c.close();
			mDbHelper.close();
		
		} catch (Exception e) {} finally { mDbHelper.close(); }
		return attachments;
	}
	
	void showAttachmentImage(Context context,
			WebView webView, String _url, boolean extraLink) {

		String url = _url.replace("https", "http");
		String summary = "<html><body>" + "<center>"
				+ "<img width=\"100%\" src=\"" + url + "\"/>";
		if (extraLink)

			summary += "<br/><a href=\"" + url
					+ "\">Open with Browser</a></center>";

		 summary += "</body></html>";
		 webView.loadDataWithBaseURL("fake://this/is/not/real",
		 summary,
		 "text/html", "utf-8", "");
	}
	
	private void findViewById(NewsFeedViewHolder holder, View convertView) {

		holder.mAvatar = (ImageView) convertView
				.findViewById(R.id.newsfeed_list_avatar);
		holder.mName = (TextView) convertView
				.findViewById(R.id.newsfeed_list_name);
		holder.mContent = (TextView) convertView
				.findViewById(R.id.newsfeed_list_content);
		//holder.mType = (ImageView) convertView
		//		.findViewById(R.id.newsfeed_list_type);
		holder.mTime = (TextView) convertView
				.findViewById(R.id.newsfeed_list_time);
		//holder.mSource = (TextView) convertView
		//		.findViewById(R.id.newsfeed_list_source);
		
		holder.mZhuanfa =  (ImageView) convertView.findViewById(R.id.zhuanfa);
		holder.mDownloadcode = (ImageView) convertView.findViewById(R.id.downloadcode);
		holder.madcode = (TextView) convertView.findViewById(R.id.ad_code);
	}

	protected TextView mFavoriteButton;
	protected LoadingDialog mWaitingDialog;

	public void Assignment(NewsFeedViewHolder holder, int position,	final RowStatus mRowStatus) {
		holder.mContent.setVisibility(View.VISIBLE);
		mApplication.mHeadBitmap.display(holder.mAvatar, mRowStatus.getProfileImage());

		holder.mContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//onShowNoticeMenu(v, mRowStatus.getId(),itemindex);
				//RowStatus mRowStatus
			//	NoticeComments.actionHandleNoticeComments(mContext,mRowStatus);
			}
		});
		holder.mName.setText(mRowStatus.getName());
		
		String adcode = mRowStatus.getadcode();
		String title = mContext.getString(R.string.adcode_title);
		if (adcode == null)
		{
			holder.madcode.setText(" "+ title + "showmethemoney" + " ");
		}else{
			holder.madcode.setText(" "+ title + adcode + " ");
		}
		holder.madcode.setVisibility(View.VISIBLE);
		
		Date d = new Date();
		d.setTime(mRowStatus.getDateTime());

		holder.mTime.setText(new SimpleDateFormat("yyyy-MM-dd").format(d));
		if (holder.mZhuanfa != null ) {

			holder.mZhuanfa.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//doForward(mRowStatus.getId());
					doForward(mRowStatus.getStatus() + "\n\n"+ mContext.getString(R.string.adcode_title) +" " + mRowStatus.getadcode());
				}
			});
		}
		
		if (holder.mDownloadcode != null){
			holder.mDownloadcode.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v){
					//downloadcode(mRowStatus.getStatusId());
					downloadcode(mRowStatus);
				}
			});
		}
	}

	public void Assignment(NewsFeedViewHolder holder, int position,int feedType, NewsFeedResult result) {
		mApplication.mHeadBitmap.display(holder.mAvatar, result.getHead_url());
		mText_Util.addIntentLinkToFriendInfo(mContext, mActivity, holder.mName,
				result.getName(), 0, result.getName().length(),
				result.getActor_id());
		holder.mContent.setVisibility(View.VISIBLE);
		holder.mContent.setText(mText_Util.replace(result.getMessage()));
		holder.mType.setImageResource(R.drawable.v5_0_1_newsfeed_status_icon);
		holder.mTime.setText(result.getUpdate_time());
		if (result.getSource_text() != null) {
			holder.mSource.setText("" + result.getSource_text());
		}

		if (result.getComments_count() > 0) {
//			holder.mComment_Root.setVisibility(View.VISIBLE);
			holder.mComment_Count.setText(result.getComments_count() + "");
			List<Map<String, Object>> commentList = result.getComments();
			if (commentList.size() > 0) {
				if (result.getComments_count() == 1) {
					holder.mComment_1
							.setText(mText_Util
									.replace(commentList.get(0).get("name")
											.toString()
											+ "   "
											+ commentList.get(0).get("text")
													.toString()));
					holder.mComment_1_Time.setText(commentList.get(0)
							.get("time").toString());
					holder.mComment_2.setVisibility(View.GONE);
					holder.mComment_2_Time.setVisibility(View.GONE);
				} else {
					holder.mComment_2.setVisibility(View.VISIBLE);
					holder.mComment_2_Time.setVisibility(View.VISIBLE);
					holder.mComment_1
							.setText(mText_Util
									.replace(commentList.get(0).get("name")
											.toString()
											+ "   "
											+ commentList.get(0).get("text")
													.toString()));
					holder.mComment_1_Time.setText(commentList.get(0)
							.get("time").toString());
					holder.mComment_2
							.setText(mText_Util
									.replace(commentList.get(1).get("name")
											.toString()
											+ "   "
											+ commentList.get(1).get("text")
													.toString()));
					holder.mComment_2_Time.setText(commentList.get(1)
							.get("time").toString());
				}
			} else {
			}

		} else {
		}


		if (result.getAttachment_count() > 0
				&& (feedType == 10 || feedType == 11)) {
			holder.mStatus_Root.setVisibility(View.VISIBLE);
			mText_Util.addIntentLinkToFriendInfo(mContext, mActivity,
					holder.mStatus_Name, result.getAttachment_owner_name(), 0,
					result.getAttachment_owner_name().length(),
					result.getAttachment_owner_id());
			holder.mStatus_Content.setText(mText_Util.replace(result
					.getAttachment_content()));
		} else {
			holder.mStatus_Root.setVisibility(View.GONE);
		}

	
		if (result.getAttachment_count() > 0
				&& (feedType == 32 || feedType == 36)) {
			holder.mSharePhoto_Root.setVisibility(View.VISIBLE);
			if (result.getTrace_text() == null
					|| result.getTrace_text().length() == 0) {
				holder.mContent.setVisibility(View.GONE);
			} else {
				holder.mContent.setVisibility(View.VISIBLE);
				holder.mContent.setText(mText_Util.replace(result
						.getTrace_text()));
			}
			mText_Util.addIntentLinkToFriendInfo(mContext, mActivity,
					holder.mSharePhoto_Name, result.getAttachment_owner_name(),
					0, result.getAttachment_owner_name().length(),
					result.getAttachment_owner_id());
			if (result.getDescription() == null
					|| result.getDescription().equals("")) {
				holder.mSharePhoto_Description.setVisibility(View.GONE);
			} else {
				holder.mSharePhoto_Description.setVisibility(View.VISIBLE);
				holder.mSharePhoto_Description.setText(mText_Util
						.replace(result.getDescription()));
			}
			if (result.getAttachment_raw_src() != null) {
				mApplication.mPhotoBitmap.display(holder.mSharePhoto_Image,
						result.getAttachment_raw_src());
			} else {
				holder.mSharePhoto_Image
						.setImageResource(R.drawable.v5_0_1_photo_default_img);
			}
			if (result.getTitle() == null || result.getTitle().equals("")) {
				holder.mSharePhoto_Title.setVisibility(View.GONE);
			} else {
				holder.mSharePhoto_Title.setVisibility(View.VISIBLE);
				holder.mSharePhoto_Title.setText("" + result.getTitle() + "");
			}
			holder.mType
					.setImageResource(R.drawable.v5_0_1_newsfeed_share_icon);
		} else {
			holder.mSharePhoto_Root.setVisibility(View.GONE);
		}

		if (result.getAttachment_count() > 0 && feedType == 33) {
			holder.mShareAlbum_Root.setVisibility(View.VISIBLE);
			if (result.getTrace_text() == null
					|| result.getTrace_text().length() == 0) {
				holder.mContent.setVisibility(View.GONE);
			} else {
				holder.mContent.setVisibility(View.VISIBLE);
				holder.mContent.setText(mText_Util.replace(result
						.getTrace_text()));
			}
			mText_Util.addIntentLinkToFriendInfo(mContext, mActivity,
					holder.mShareAlbum_Name, result.getAttachment_owner_name(),
					0, result.getAttachment_owner_name().length(),
					result.getAttachment_owner_id());
			holder.mShareAlbum_Title.setText("" + result.getTitle() + "");

			if (result.getAttachment_raw_src() != null) {
				mApplication.mPhotoBitmap.display(holder.mShareAlbum_Image,
						result.getAttachment_raw_src());
			} else {
				holder.mShareAlbum_Image
						.setImageResource(R.drawable.v5_0_1_photo_default_img);
			}
		} else {
			holder.mShareAlbum_Root.setVisibility(View.GONE);
		}


		if (result.getAttachment_count() > 0
				&& (feedType == 21 || feedType == 23)) {
			holder.mShareBlog_Root.setVisibility(View.VISIBLE);
			if (result.getTrace_text() == null
					|| result.getTrace_text().length() == 0) {
				holder.mContent.setVisibility(View.GONE);
			} else {
				holder.mContent.setVisibility(View.VISIBLE);
				holder.mContent.setText(mText_Util.replace(result
						.getTrace_text()));
			}
			mText_Util.addIntentLinkToFriendInfo(mContext, mActivity,
					holder.mShareBlog_Name, result.getAttachment_owner_name(),
					0, result.getAttachment_owner_name().length(),
					result.getAttachment_owner_id());

			mText_Util.addIntentLinkToBlog(mContext, mActivity,
					holder.mShareBlog_Title, result.getTitle(), 0, result
							.getTitle().length(), result
							.getAttachment_media_id(), result.getActor_id(),
					result.getName(), result.getDescription(), result
							.getActor_type(), result.getComments_count());
			holder.mShareBlog_Description.setText(result.getDescription());
		} else {
			holder.mShareBlog_Root.setVisibility(View.GONE);
		}


		if (feedType == 20 || feedType == 22) {
			holder.mContent.setVisibility(View.GONE);
			holder.mPublishBlog_Root.setVisibility(View.VISIBLE);
			mText_Util.addIntentLinkToBlog(mContext, mActivity,
					holder.mPublishBlog_Title, result.getTitle(), 0, result
							.getTitle().length(), result.getSource_id(), result
							.getActor_id(), result.getName(), result
							.getDescription(), result.getActor_type(), result
							.getComments_count());
			holder.mPublishBlog_Description.setText(result.getDescription());
			holder.mType.setImageResource(R.drawable.v5_0_1_newsfeed_blog_icon);
		} else {
			holder.mPublishBlog_Root.setVisibility(View.GONE);
		}


		if (result.getAttachment_count() > 0
				&& (feedType == 30 || feedType == 31)) {
			holder.mContent.setVisibility(View.GONE);
			holder.mPublishPhoto_Root.setVisibility(View.VISIBLE);
			if (result.getAttachment_content() == null
					|| result.getAttachment_content().equals("")) {
				holder.mPublishPhoto_Content.setVisibility(View.GONE);
			} else {
				holder.mPublishPhoto_Content.setVisibility(View.VISIBLE);
				holder.mPublishPhoto_Content.setText(mText_Util.replace(result
						.getAttachment_content()));
			}
			holder.mPublishPhoto_Title.setText("" + result.getTitle() + "");
			if (result.getAttachment_raw_src() != null) {
				mApplication.mPhotoBitmap.display(holder.mPublishPhoto_Image,
						result.getAttachment_raw_src());
			} else {
				holder.mPublishPhoto_Image
						.setImageResource(R.drawable.v5_0_1_photo_default_img);
			}
			holder.mType
					.setImageResource(R.drawable.v5_0_1_newsfeed_photo_icon);
		} else {
			holder.mPublishPhoto_Root.setVisibility(View.GONE);
		}
	}

//	private void downloadcode(long id){
// 		//id:status_id or repeated_id
//        getadcodeTask task = new getadcodeTask(mContext);
//        task.execute(id);	
//	}

	private void downloadcode(RowStatus status_item){
        getadcodeTask task = new getadcodeTask(mContext);
        task.execute(status_item);	
	}

    public class getadcodeTask extends AsyncTask<RowStatus, String, String> {
        ProgressDialog waitingdialog;
        long mstatus_id;
        RowStatus mStatus_item;
            
        @SuppressWarnings("deprecation")
		public getadcodeTask(Context context){
            waitingdialog = new ProgressDialog(context, 0);   
            waitingdialog.setButton("cancel", 
            	new DialogInterface.OnClickListener() {
                	public void onClick(DialogInterface dialog, int i) {
                	dialog.cancel();
                }
            });
            
            waitingdialog.setOnCancelListener(
            	new DialogInterface.OnCancelListener() {
                	public void onCancel(DialogInterface dialog) {
                	cancel(true);
                }
            });
            
            waitingdialog.setCancelable(true);
            waitingdialog.setMessage("Please wait while downloading...");
            waitingdialog.setIndeterminate(true);
            //waitingdialog.setMax(100);
            //waitingdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            waitingdialog.show();
        }

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			MustardDbAdapter mDbHelper = getDbAdapter();   
            waitingdialog.dismiss();
			
			try{
				mDbHelper.updateAdcodeByStatusId(mstatus_id, result);
				mDbHelper.close();
			}catch (Exception e) {
				mDbHelper.close();
			}
			mStatus_item.setadcode(result);
			notifyDataSetChanged();			
		}

		@Override
		protected void onCancelled(){
			super.onCancelled();
		}

		@Override
		protected String doInBackground(RowStatus... id) {
			// TODO Auto-generated method stub
			mStatus_item = id[0];
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "newcode";
		}

	}

	private void doForward(String text){
		Intent intent = new Intent(Intent.ACTION_SEND);
		
  		intent.setType("text/plain");    
  		intent.putExtra(Intent.EXTRA_TEXT, text);    
  		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    
  		mContext.startActivity(Intent.createChooser(intent, "choose an application"));   
	}

	private void doForward(long id) {
  		Intent intent = new Intent(Intent.ACTION_SEND);   
//        if (imgPath == null || imgPath.equals("")) {    
//            intent.setType("text/plain"); 
//        } else {    
//            File f = new File(imgPath);    
//            if (f != null && f.exists() && f.isFile()) {    
//                intent.setType("image/png");    
//                Uri u = Uri.fromFile(f);    
//                intent.putExtra(Intent.EXTRA_STREAM, u);    
//            }    
//        }    
//        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
  		intent.setType("text/plain");    
  		intent.putExtra(Intent.EXTRA_TEXT, "this is demo text");    
  		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    
  		mContext.startActivity(Intent.createChooser(intent, "choose an application"));    
	}

	private QuickAction mQuickAction;
	protected SharedPreferences mPreferences;
	String TAG = "NoticeAdapter";
	//private StatusesFetcher mFetcherTask = null;
	protected boolean mMergedTimeline = false;
	protected static final int DIALOG_FETCHING_ID = 0;

	private void dismissQuickAction() {
		if (mQuickAction != null)
			mQuickAction.dismiss();
	}
	public TimelineHandler mHandler = new TimelineHandler();

	class TimelineHandler extends Handler {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_PROGRESS:
				((Activity) mContext)
						.setProgressBarIndeterminateVisibility(msg.arg1 != 0);
				break;
			case MSG_GEOLOCATION_OK:
				//GeoName gn = (GeoName) msg.obj;
				//doShowGeolocation(gn);
				break;
			case MSG_GEOLOCATION_KO:
				//showErrorMessage((String) msg.obj);
				break;

			case MSG_REFRESH:
				//doSilentRefresh();
				//if(mUpdateUiHandler != null)
				//	mUpdateUiHandler.sendEmptyMessage(MSG_REFRESH);
			
			
				break;
			case MSG_NOTICEDELETED:
			{
				notifyDataSetChanged();
				
				Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show();
				
			}
				break;
			}
		}

		public void progress(boolean progress) {
			Message msg = new Message();
			msg.what = MSG_PROGRESS;
			msg.arg1 = progress ? 1 : 0;
			sendMessage(msg);
		}
/*
		public void showGeolocation(GeoName geoname) {
			Message msg = new Message();
			msg.what = MSG_GEOLOCATION_OK;
			msg.obj = geoname;
			sendMessage(msg);
		}

		public void errorGeolocation(String error) {
			Message msg = new Message();
			msg.what = MSG_GEOLOCATION_KO;
			msg.obj = error;
			sendMessage(msg);
		}
*/
	}

	public synchronized void notifyDataSetChanged(ArrayList<RowStatus> datas) {
		this.datas = datas;
		super.notifyDataSetChanged();
	}

	protected int DB_ROW_TYPE;
	protected String DB_ROW_EXTRA;
	protected boolean isRemoteTimeline = false;

	protected void showToastMessage(CharSequence message, boolean longView) {
		int popTime = longView ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
		Toast.makeText(mContext, message, popTime).show();
	}

	protected void showToastMessage(CharSequence message) {
		showToastMessage(message, false);
	}

	protected StatusNet getStatusNetFromRowStatus(RowStatus rs,	MustardDbAdapter dbHelper) {
		StatusNet sn = null;
		long aid = rs.getAccountId();
		if (aid > 0) {
			sn = ((BaseApplication) ((Activity) mContext).getApplication())
					.checkAccount(dbHelper, false, aid);
		}
		return sn;
	}
	public void setUpdateUiHandler(Handler handler)
	{
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		NewsFeedViewHolderPicture holderPicture = null;
		RowStatus result = null;
		int viewType = getItemViewType(position);
		if (convertView == null || viewType == VIEW_TYPE_ATTACHMENT_AUDIO) {
			holderPicture = new NewsFeedViewHolderPicture();
			convertView = mInflater.inflate(R.layout.newsfeed_item_picture,
					null);
			findViewById(holderPicture, convertView);
			if (viewType == VIEW_TYPE_ATTACHMENT_IMG) {
				holderPicture.mImgListener = new AttachmentOnClickListener(
						position);
				holderPicture.viewStub_image = (ViewStub) convertView
						.findViewById(R.id.viewStub_img_id);
				holderPicture.currentViewItem = holderPicture.viewStub_image
						.inflate();
				holderPicture.mStatus_img = (ImageView) holderPicture.currentViewItem
						.findViewById(R.id.attachment_img);
				holderPicture.mStatus_img
						.setOnClickListener(holderPicture.mImgListener);
			}
			if (viewType == VIEW_TYPE_ATTACHMENT_AUDIO) {
				holderPicture.viewStub_audio = (ViewStub) convertView.findViewById(R.id.viewStub_audio_id);
				holderPicture.currentViewItem = holderPicture.viewStub_audio.inflate();
				holderPicture.viewStub_audio_panel = (ImageView) holderPicture.currentViewItem.findViewById(R.id.audio_panel);
				holderPicture.viewStub_audio_time = (TextView) holderPicture.currentViewItem.findViewById(R.id.audio_time);
				holderPicture.mAudioListener = new AttachmentOnClickListener(position, holderPicture.viewStub_audio_time);
				holderPicture.viewStub_audio_panel.setOnClickListener(holderPicture.mAudioListener);
				if (mPlayIndex == position) {
					startAnim(holderPicture.viewStub_audio_panel,holderPicture.viewStub_audio_time, position);
				} else {
					releaseAnim(position);
				}
			}
			convertView.setTag(holderPicture);
		} else {
			holderPicture = (NewsFeedViewHolderPicture) convertView.getTag();
			if (viewType == VIEW_TYPE_ATTACHMENT_IMG) {
				holderPicture.mImgListener.updatePosition(position);
				holderPicture.mStatus_img
						.setOnClickListener(holderPicture.mImgListener);
			}
		}
		result = datas.get(position);
		/*
		Spanned sp = Html.fromHtml(result.getStatusHtml(), new Html.ImageGetter() {  
			    @Override  
			    public Drawable getDrawable(String source) {  
			        InputStream is = null;  
			        try {  
			            is = (InputStream) new URL(source).getContent();  
			            Drawable d = Drawable.createFromStream(is, "src");  
			            d.setBounds(0, 0, d.getIntrinsicWidth(),  
			                    d.getIntrinsicHeight());  
			            is.close();  
			            return d;  
			        } catch (Exception e) {  
			            return null;  
			        }  
			    }  
			}, null);
			*/
			if(viewType == VIEW_TYPE_NO_PICTURE) {
				//Spanned t = Util.convertNormalStringToSpannableStringForNotice(mContext, (CharSequence)sp, true, 20, 20);
				//holderPicture.mContent.setText(t);
				holderPicture.mContent.setText(result.getStatus());
			}
			else {
				if(attachment != null) {
					if(attachment.size()>0) {
						String _url= attachment.get(0).getUrl();
						//Spanned t = Util.convertNormalStringToSpannableStringForNotice(mContext, (CharSequence)sp, true, 20, 20);
						//holderPicture.mContent.setText(t);
						holderPicture.mContent.setText(result.getStatus());
							if(viewType == VIEW_TYPE_ATTACHMENT_IMG) {
								if ((null != _url) && (!_url.equals(""))) {
									String tag = _url.replace("https", "http");
									imageLoader.displayImage( tag, holderPicture.mStatus_img, options, null);
								}
							}
							else if(viewType == VIEW_TYPE_ATTACHMENT_AUDIO) {
								if ((null != _url) && (!_url.equals(""))) {
									holderPicture.viewStub_audio_time.setText(R.string.audio);
								}
							}
					}						
				}
			}
			Assignment(holderPicture, position, result);
			holderPicture.mContent.setMovementMethod(LinkMovementMethod.getInstance());
		return convertView;
	}

	public class AttachmentOnClickListener implements View.OnClickListener {

		private int mPosition;
		URL url;
		TextView mAudio_time;

		public AttachmentOnClickListener(int position) {
			mPosition = position;
		}

		public AttachmentOnClickListener(int position, TextView audio_time) {
			mPosition = position;
			mAudio_time = audio_time;
		}

		public void updatePosition(int position) {
			mPosition = position;
		}

		public void updatePosition(int position, TextView audio_time) {
			mPosition = position;
			mAudio_time = audio_time;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			ArrayList<Attachment> attachment = getAttachemntDetails(datas.get(mPosition).getId());
			String mime_type = attachment.get(0).getMimeType();
			String _url = attachment.get(0).getUrl();
			final String tag = _url.replace("https", "http");
			if (mime_type.contains("image/")) {
				Intent i = new Intent();
				i.putExtra("path", tag);
				i.putExtra(NoticeAdapter.INTENT_TYPE_KEY, 1);
				i.setClass(mContext, ShowAttachmentView.class);
				mContext.startActivity(i);
			} else {
				Date curDate = new   Date(System.currentTimeMillis());
				long diff = curDate.getTime() - lastTime; 
				Log.v(TAG,"curDate====>" + curDate.getTime()+ ",lastTime====>" + lastTime + "" +
						",diff=====>" + diff);
				if(diff<2000) {
					Log.v(TAG,"return");
					return;
				}
				lastTime = curDate.getTime();
				playRecorder(v, mAudio_time, tag, mPosition);
			}
		}

	}

	public void startAnim(ImageView imageView, TextView textview, int position) {
		if (null != imageView) {
			imageView.setImageResource(R.anim.attachment_audio_play_anim);
			AnimationDrawable animationDrawable = (AnimationDrawable) imageView
					.getDrawable();
			if (null != animationDrawable) {
				animationDrawable.start();
			}
			mAniImageViews.put(position + "", imageView);
			mAudioTextViews.put(position + "", textview);
		}
		if (null != mAudioCount) {
			mAudioCount.cancel();
			mAudioCount.onFinish();
			mAudioCount = null;
		}
		try {
			if (null != mMediaPlayer && mMediaPlayer.isPlaying()) {
				int curr = mMediaPlayer.getDuration()
						- mMediaPlayer.getCurrentPosition();
				mAudioCount = new AudioPlayTimeCount(curr, 1000, textview);
				mAudioCount.start();
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		notifyDataSetChanged();
	}

	public void releaseAnim(int position) {
		try {
			ImageView imageView = mAniImageViews.get(position + "");
			if(imageView != null) {
			AnimationDrawable animationDrawable = (AnimationDrawable) imageView
					.getDrawable();
			if (animationDrawable != null) {
				if (animationDrawable.isRunning()) {
					animationDrawable.stop();
					animationDrawable.selectDrawable(0);
					animationDrawable = null;
				}
			}
			mAniImageViews.remove(imageView);
			}
			TextView textV = mAudioTextViews.get(position + "");
			if (null != textV) {
				textV.setText(R.string.audio);
			}

			if (mPlayIndex == position && null != mAudioCount) {
				mAudioCount.cancel();
				mAudioCount.onFinish();
				mAudioCount = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void playRecorder(final View v, final TextView audio_time,
			String playPath, final int position) {
		if (null != mMediaPlayer) {
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		mMediaPlayer = new MediaPlayer();
		try {
			mMediaPlayer.setDataSource(playPath);
			mMediaPlayer.prepareAsync();
			mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					// TODO Auto-generated method stub
					mp.start();
					mPlayIndex = position;
					startAnim((ImageView) v, audio_time, position);
				}
			});
			 
			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer m) {
					// TODO Auto-generated method stub
					if (m != null) {
						try {
							m.release();
							releaseAnim(position);
							m = null;
							mPlayIndex = -1;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		notifyDataSetChanged();
	}
	
	 class AudioPlayTimeCount extends CountDownTimer {  
		 public TextView textView;
		
		 public AudioPlayTimeCount(long millisInFuture, long countDownInterval) {
	        	super(millisInFuture, countDownInterval);
		}

		public AudioPlayTimeCount(long millisInFuture, long countDownInterval,TextView textView) {
			super(millisInFuture, countDownInterval);
			this.textView = textView;
		}
		
		@Override
		public void onFinish() {
			textView.setText(R.string.audio);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			textView.setText("" + millisUntilFinished / 1000 + " ''");
		}
	}

}
