package com.mp.android.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import com.mp.android.BaseApplication;
import com.mp.android.R;


import com.mp.android.desktop.Desktop;
import com.mp.android.desktop.Desktop.onChangeViewListener;
import com.mp.android.newsfeed.NewsFeed;
import com.mp.android.statusnet.MustardDbAdapter;
import com.mp.android.statusnet.provider.StatusNet;
import com.mp.android.ui.base.FlipperLayout;
import com.mp.android.ui.base.FlipperLayout.OnOpenListener;
//import com.mp.android.user.UserPage;
import com.mp.android.util.Constant;
import com.mp.android.util.DataUpdateObservable;
import com.mp.android.util.Util;
import com.mp.android.util.View_Util;

public class DesktopActivity extends Activity implements OnOpenListener,
		Observer {
	private BaseApplication mApplication;
	private FlipperLayout mRoot;
	private static Desktop mDesktop;
	//private UserPage mUser;
	//private Meeting mMeeting;
	private NewsFeed mNewsFeed;
	//private DirectMessages mMessage;
	//private MessageFollower messageFollower;
	//private Chat mChat;
	//private Friends mFriends;
	//private Groups mGroups;
	//private PollMain mPollMain;
	//private QuestionView mQuestion;
	//private Location mLocation;
	//private AppsCenter mAppsCenter;
	static DesktopActivity context;
	private File mFilename;
	//private IMFriends mIMFriends;
	//private NotesListActivity mNotesListActivity;

	private Intent sIntent;
	private ServiceConnection serviceConnection;
	//private RosterAdapter adapter;
	//private IRosterCallback.Stub callback;
	Handler mHandler = new Handler();
	private static final String TAG = "DesktopActivity";
	public static final String AVATAR_PATH = MustardDbAdapter.PATH_PKG;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApplication = (BaseApplication) getApplication();
		mRoot = new FlipperLayout(DesktopActivity.this);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		mRoot.setLayoutParams(params);
		mDesktop = new Desktop(mApplication, this);
		//mUser = new UserPage(mApplication, this, this);
		//mMeeting = new Meeting(this, mApplication, this);
		//mQuestion = new QuestionView(this, mApplication, this);
		context = this;
		DataUpdateObservable.getInstance().addObserver(this);


		MustardDbAdapter mMustardDbAdapter = Util.getDbAdapter(this);
		StatusNet mStatusNet = ((BaseApplication) getApplication())
					.checkAccount(mMustardDbAdapter);
		if (mMustardDbAdapter != null) {
			mMustardDbAdapter.close();
			mMustardDbAdapter = null;
		}
		mNewsFeed = new NewsFeed(mApplication, this, this, mStatusNet,
					PreferenceManager.getDefaultSharedPreferences(this));
		//messageFollower = new MessageFollower(mApplication, this, this,mStatusNet,
		//			PreferenceManager.getDefaultSharedPreferences(this));

		//mMessage = new DirectMessages(this, mApplication);
		//mChat = new Chat(this);
		//mFriends = new Friends(mApplication, this, this);
		//mGroups = new Groups(mApplication, this, this);
		//mLocation = new Location(mApplication, this, this);
		//mAppsCenter = new AppsCenter(this);
		// mIMFriends = new IMFriends(this);
		//mImMain = new IMMainInterface(this);
		//mPollMain = new PollMain(mApplication, this, null, this);
		//mNotesListActivity = new NotesListActivity(this, this);
		mRoot.addView(mDesktop.getView(), params);
		mRoot.addView(mNewsFeed.getView(), params);
		setContentView(mRoot);
		setListener();
		onDataChange();
	}

	private void setListener() {
		mNewsFeed.setOnOpenListener(this);
		//mUser.setOnOpenListener(this);
		//mMessage.setOnOpenListener(this);
		//mChat.setOnOpenListener(this);
		//mFriends.setOnOpenListener(this);
		//mGroups.setOnOpenListener(this); // add for flip
		//mLocation.setOnOpenListener(this);
		//mSearch.setOnOpenListener(this);
		//mPollMain.setOnOpenListener(this);
		// mIMFriends.setOnOpenListener(this);
		//mMeeting.setOnOpenListener(this);
		//mQuestion.setOnOpenListener(this);

		//mImMain.setOnOpenListener(this);

		//messageFollower.setOnOpenListener(this);
		//mNotesListActivity.setOnOpenListener(this);
		mDesktop.setOnChangeViewListener(new onChangeViewListener() {

			public void onChangeView(int arg0) {
				switch (arg0) {
				case View_Util.Information:
					//mUser.init();
					//mRoot.close(mUser.getView());
					break;

				case View_Util.NewsFeed:
					mRoot.close(mNewsFeed.getView());
					break;

				case View_Util.Message:
					mRoot.close(mNewsFeed.getView());
					break;
/*
				case View_Util.DirectMessage:
					mRoot.close(mMessage.getView());
					break;

				case View_Util.InstantMessage:
					mRoot.close(mImMain.getView());
					mImMain.sendNotify();
					mImMain.onResume();
					break;
				case View_Util.Friends:
					// if (RenRenData.mFriendsResults.size() == 0) {
					mFriends.init_ex(FriensCommon.List_Friends);
					// }
					mRoot.close(mFriends.getView());
					break;
				case View_Util.Group:
					mGroups.init();
					mRoot.close(mGroups.getView());
					break;
				case View_Util.Search:
					mRoot.close(mSearch.getView());
					break;
				case View_Util.Poll:
					mPollMain.init();
					mRoot.close(mPollMain.getView());
					break;
				case View_Util.Meeting:
					mMeeting.init();
					mRoot.close(mMeeting.getView());
					break;
				case View_Util.Question:
					mQuestion.init();
					mRoot.close(mQuestion.getView());
					break;
				case View_Util.PersonalNote:
					mRoot.close(mNotesListActivity.getView());
					break;*/
				}
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Uri uri = null;
		switch (requestCode) {
		case 0:
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					Toast.makeText(this, R.string.str_sdcarduseless,
							Toast.LENGTH_SHORT).show();
					return;
				}
				mApplication.mImageType = 0;
				//startActivity(new Intent(DesktopActivity.this, PhotosEdit.class));
				//overridePendingTransition(R.anim.roll_up, R.anim.roll);
			} else {
				Toast.makeText(this, R.string.str_canceltkpic,
						Toast.LENGTH_SHORT).show();
			}
			break;

		case 1:
			if (data == null) {
				Toast.makeText(this, R.string.str_cancelupload,
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					Toast.makeText(this, R.string.str_sdcarduseless,
							Toast.LENGTH_SHORT).show();
					return;
				}
				uri = data.getData();
				String[] proj = { MediaStore.Images.Media.DATA };
				Cursor cursor = managedQuery(uri, proj, null, null, null);
				if (cursor != null) {
					int column_index = cursor
							.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					if (cursor.getCount() > 0 && cursor.moveToFirst()) {
						mApplication.mImagePath = cursor
								.getString(column_index);
						if (mApplication.mImagePath != null) {
							mApplication.mImageType = 1;
						//	startActivity(new Intent(DesktopActivity.this,
						//			PhotosEdit.class));
						//	overridePendingTransition(R.anim.roll_up,
						//			R.anim.roll);
						} else {
							Toast.makeText(this, R.string.str_unablefdpic,
									Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(this, R.string.str_unablefdpic,
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(this, R.string.str_unablefdpic,
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(this, R.string.str_geterror, Toast.LENGTH_SHORT)
						.show();
			}
			break;
		case 2:
			if (resultCode == RESULT_OK) {
				MustardDbAdapter mMustardDbAdapter = Util.getDbAdapter(context);
				StatusNet mStatusNet = mApplication
						.checkAccount(mMustardDbAdapter);
				if (mMustardDbAdapter != null) {
					mMustardDbAdapter.close();
					mMustardDbAdapter = null;
				}
				File file = new File(mApplication.mImagePath);
				Log.v("StatusNet", "mApplication.mImagePath = "
						+ mApplication.mImagePath + "mStatusNet = "
						+ mStatusNet.toString());
				startPhotoZoom(Uri.fromFile(file));
			} else {
				Toast.makeText(this, R.string.str_canceltkpic,
						Toast.LENGTH_SHORT).show();
			}
			break;
		case 3:
			if (data != null) {
				MustardDbAdapter mMustardDbAdapter = Util.getDbAdapter(context);
				StatusNet mStatusNet = mApplication
						.checkAccount(mMustardDbAdapter);
				if (mMustardDbAdapter != null) {
					mMustardDbAdapter.close();
					mMustardDbAdapter = null;
				}
				Bundle extras = data.getExtras();
				Bitmap photo = extras.getParcelable("data");
				saveBitmapTofile(photo, "avatar.png");
				File f = new File(AVATAR_PATH + "avatar.png");
				//new UserPage(mApplication, context, this).sendAvatar(mStatusNet, f,	true);
			}
			break;
		}
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mRoot.getScreenState() == FlipperLayout.SCREEN_STATE_CLOSE) {
				mRoot.open();
			} else {
				dialog();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void dialog() {
		AlertDialog.Builder builder = new Builder(DesktopActivity.this);
		builder.setMessage(R.string.str_confimquit);
		builder.setPositiveButton(R.string.str_yes,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						/*if (null != mImMain.rosterAdapter) {
							if (mImMain.rosterAdapter.isLogged()) {
								mImMain.rosterAdapter.disconnect();
							}
						}*/
						Log.v(TAG, "exit ok");
						Intent intent = new Intent();
						intent.setAction("com.mp.android.finishService");
						sendBroadcast(intent);
						finish();
					}
				});
		builder.setNegativeButton(R.string.str_cancel,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		builder.create().show();
	}

	public void open() {
		if (mRoot.getScreenState() == FlipperLayout.SCREEN_STATE_CLOSE) {
			mRoot.open();
		}
	}

	public static void finshActivity() {
		if (null != mDesktop) {
			mDesktop.finishDesktop();
		}
		if (context != null) {
			context.finish();
		}
	}

	/*
	 * update data when change avatar add by fancuiru at 2013-12-28
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		Log.v("fcr", "DesktopActivity update begin");
		{
			MustardDbAdapter mMustardDbAdapter = Util.getDbAdapter(this);
			StatusNet mStatusNet = ((BaseApplication) getApplication())
					.checkAccount(mMustardDbAdapter);
			if (mMustardDbAdapter != null) {
				mMustardDbAdapter.close();
				mMustardDbAdapter = null;
			}
			mNewsFeed = new NewsFeed(mApplication, this, this, mStatusNet,
					PreferenceManager.getDefaultSharedPreferences(this));
			//messageFollower = new MessageFollower(mApplication, this, this,
			//		mStatusNet,
			//		PreferenceManager.getDefaultSharedPreferences(this));
		}
		Log.v("fcr", "DesktopActivity update end");
	}

	// add end

	@Override
	protected void onResume() {
	/*	if (QuestionItemInfo.isRefresh) {
			mQuestion.reFresh();
			QuestionItemInfo.isRefresh = false;
		}
		if (PollInfo.isRefresh) {
			mPollMain.reFresh();
			PollInfo.isRefresh = false;
		}
		if (MeetingItemInfo.isRefresh) {
			mMeeting.reFresh();
			MeetingItemInfo.isRefresh = false;
		}
		mImMain.onResume();*/
		onDataChange();
		Log.v(TAG, "DesktopActivity onResume");
		super.onResume();
	}

	private void onDataChange() {
		//@SuppressWarnings("unchecked")
		//List<String> tbs = (List<String>) BaseApplication.session
		//		.get(Constant.NOTIFY_ITEM_CHAT_LIST);
		//if (tbs != null) {
		//	mImMain.onFromNotify();
		//	BaseApplication.session.remove(Constant.NOTIFY_ITEM_CHAT_LIST);
		//}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		//mImMain.onPause();
		super.onDestroy();
		System.exit(0);
	}

	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 350);
		intent.putExtra("outputY", 350);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 3);
	}

	public static boolean saveBitmapTofile(Bitmap bmp, String filename) {
		CompressFormat format = Bitmap.CompressFormat.JPEG;
		int quality = 100;
		OutputStream stream = null;
		try {
			stream = new FileOutputStream(AVATAR_PATH + filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return bmp.compress(format, quality, stream);
	}
}
