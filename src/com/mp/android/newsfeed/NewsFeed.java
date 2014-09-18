package com.mp.android.newsfeed;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mp.android.BaseApplication;
import com.mp.android.R;
import com.mp.android.RequestListener;
import com.mp.android.newsfeed.NoticeRequestParam.FETCHER_STATUSES_TYPE;
import com.mp.android.pullview.PullListView;
import com.mp.android.pullview.PullListView.IXListViewListener;
import com.mp.android.statusnet.MustardDbAdapter;
import com.mp.android.statusnet.Preferences;
import com.mp.android.statusnet.provider.StatusNet;
import com.mp.android.statusnet.statusnet.RowStatus;
import com.mp.android.ui.base.FlipperLayout.OnOpenListener;
import com.mp.android.ui.base.LoadingDialog;
import com.mp.android.ui.base.ModePopAdapter;
import com.mp.android.util.Util;

public class NewsFeed {
	private BaseApplication mApplication;
	private Context mContext;
	private Activity mActivity;
	private View mNewsFeed;
	private ImageView mFlip;
	private RelativeLayout mModeLayout;
	private TextView mModeText;
	private ImageView mCamera;
	private PullListView mNewsFeedDisplay;
	private PullListView mPiazzaDisplay;
	private boolean mFromSavedState = false;
	private PopupWindow mModePopupWindow;
	private View mModeView;
	private ListView mModeListView;

	private NoticeAdapter mNewsFeedAdapter;
	private NoticeAdapter mPiazzaAdapter;
	private LoadingDialog mDialog;
	private OnOpenListener mOnOpenListener;

	private int[] mModeIcon = {
			R.drawable.v5_0_1_newsfeed_popupwindow_type_all_background,
			R.drawable.v5_0_1_newsfeed_popupwindow_type_specialfocus_background};
	private String modeName1 = null;
	private String modeName2 = null;
	private String[] mModeName = {modeName1, modeName2};
	private static final String TYPE_ALL = "10,11,20,21,22,23,30,31,32,33,36";
	private static final String TYPE_SPECIALFOCUS = null;
	private static final String TYPE_STATUS = "10,11";
	private static final String TYPE_PHOTO = "30,31";
	private static final String TYPE_PLACE = null;
	private static final String TYPE_SHARE = "21,23,32,33,36";
	private static final String TYPE_BLOG = "20,22";
	private String mNewsFeedType = TYPE_ALL;
	private boolean mRefresh = false;
	private int mPage = 1;
	private boolean mIsAdd = false;
	private boolean mIsHaveData = false;
	private int mChooseId = 0;
	private StatusNet mStatusNet;
	private SharedPreferences mPreferences;
	private boolean mMergedTimeline = false;
	private int rowType = MustardDbAdapter.ROWTYPE_FRIENDS;
	public NewsFeed(BaseApplication application, Context context,Activity activity) {
	
		mApplication = application;
		mContext = context;
		
		//choose news or publicnews
		mModeName[0] = mContext.getString(R.string.str_news);
		mModeName[1] = mContext.getString(R.string.str_publicnews);
		mModeView = LayoutInflater.from(context).inflate(R.layout.mode_popupwindow, null);
		mModePopupWindow = new PopupWindow(mModeView, LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT, true);
		mModePopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mModePopupWindow.setAnimationStyle(R.style.ModePopupAnimation);
				
		mActivity = activity;
		mDialog = new LoadingDialog(context, R.style.dialog);
		mNewsFeed = LayoutInflater.from(context).inflate(R.layout.newsfeed,null);


		findViewById();
		mNewsFeedDisplay.setAdapter(mNewsFeedAdapter);
		setListener();	
		getNewsFeed(mNewsFeedType);
	}

    public NewsFeed(BaseApplication application, final Context context,
            Activity activity, final StatusNet mStatusNet,SharedPreferences mPreferences) {
        mApplication = application;
        mContext = context;
        mModeName[0] = mContext.getString(R.string.str_news);
		mModeName[1] = mContext.getString(R.string.str_publicnews);
        this.mPreferences = mPreferences;
        mActivity = activity;
        this.mStatusNet = mStatusNet;
        mDialog = new LoadingDialog(context, R.style.dialog);
        mNewsFeed = LayoutInflater.from(context).inflate(R.layout.newsfeed,null);
        mModeView = LayoutInflater.from(context).inflate(R.layout.mode_popupwindow, null);
        mModePopupWindow = new PopupWindow(mModeView, LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT, true);
        mModePopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mModePopupWindow.setAnimationStyle(R.style.ModePopupAnimation);
        mMergedTimeline = mPreferences.getBoolean(Preferences.CHECK_MERGED_TL_KEY, false);
        findViewById();
        mNewsFeedAdapter = new NoticeAdapter(mApplication, context, activity);
        mNewsFeedAdapter.setUpdateUiHandler(mHandler);
        mNewsFeedDisplay.setAdapter(mNewsFeedAdapter);
        mPiazzaAdapter = new NoticeAdapter(mApplication, mContext, mActivity);
        mPiazzaAdapter.setUpdateUiHandler(mHandler);
        mPiazzaDisplay.setAdapter(mPiazzaAdapter);
        setListener();
        new Thread() {
            public void run() {
                final ArrayList<RowStatus> mNewsFeedStatus = Util.getData(context,
                        MustardDbAdapter.ROWTYPE_FRIENDS,
                        mMergedTimeline ? "MERGED" : mStatusNet.getMUsername());
                final ArrayList<RowStatus> mPiazzaStatus = Util.getData(context,
                        MustardDbAdapter.ROWTYPE_PUBLIC,"");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mNewsFeedAdapter.notifyDataSetChanged(mNewsFeedStatus);
                        mPiazzaAdapter.notifyDataSetChanged(mPiazzaStatus);
                        mNewsFeedDisplay.startRefresh();
                        mPiazzaDisplay.startRefresh();
                    }
                });
            };
        }.start();
    }

    
	private void findViewById() {
		mFlip = (ImageView) mNewsFeed.findViewById(R.id.newsfeed_flip);
		mModeLayout = (RelativeLayout) mNewsFeed
				.findViewById(R.id.newsfeed_mode_layout);
		mModeText = (TextView) mNewsFeed.findViewById(R.id.newsfeed_mode_text);
		mCamera = (ImageView) mNewsFeed.findViewById(R.id.newsfeed_camera);
		mNewsFeedDisplay = (PullListView) mNewsFeed.findViewById(R.id.newsfeed_display);
		mNewsFeedDisplay.setPullLoadEnable(true);
		mNewsFeedDisplay.setFastScrollEnabled(true);
		mPiazzaDisplay = (PullListView) mNewsFeed.findViewById(R.id.piazza_display);
		mPiazzaDisplay.setPullLoadEnable(true);
		mPiazzaDisplay.setFastScrollEnabled(true);

		mModeListView = (ListView) mModeView.findViewById(R.id.mode_pop_list);
	}

	private void setListener() {
		mFlip.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (mOnOpenListener != null) {
					mOnOpenListener.open();
				}
			}
		});
		mModeLayout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				initModePopupWindow(mChooseId);
			}
		});
		mNewsFeedDisplay.setXListViewListener(new IXListViewListener() {
            
            @Override
            public void onRefresh() {
                NoticeRequestParam param = new NoticeRequestParam();
                param.DB_ROW_TYPE = MustardDbAdapter.ROWTYPE_FRIENDS;
                param.DB_ROW_EXTRA = mMergedTimeline ? "MERGED"
                        : mStatusNet.getMUsername();
                getNewsFeed(mStatusNet, param);
            }
            
            @Override
            public void onLoadMore() {
                NoticeRequestParam param = new NoticeRequestParam();
                param.DB_ROW_TYPE = MustardDbAdapter.ROWTYPE_FRIENDS;
                param.DB_ROW_EXTRA = mMergedTimeline ? "MERGED" : mStatusNet
                        .getMUsername();
                param.STATUSES_FETCHER_TYPE = FETCHER_STATUSES_TYPE.STATUSES_LOAD_MORE;
                getNewsFeed(mStatusNet, param);
            }
        });
		mPiazzaDisplay.setXListViewListener(new IXListViewListener() {
            
            @Override
            public void onRefresh() {
                NoticeRequestParam param = new NoticeRequestParam();
                param.DB_ROW_TYPE = MustardDbAdapter.ROWTYPE_PUBLIC;
                getPiazza(mStatusNet, param);
            }
            
            @Override
            public void onLoadMore() {
                NoticeRequestParam param = new NoticeRequestParam();
                param.DB_ROW_TYPE = MustardDbAdapter.ROWTYPE_PUBLIC;
                param.STATUSES_FETCHER_TYPE = FETCHER_STATUSES_TYPE.STATUSES_LOAD_MORE;
                getPiazza(mStatusNet, param);
            }
        });

		mModeListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mPage = 1;
				mChooseId = position;
				mModeText.setText(mModeName[position]);
				mModePopupWindow.dismiss();
				switch (position) {
				case 0:
                    mNewsFeedDisplay.setVisibility(View.VISIBLE);
                    mPiazzaDisplay.setVisibility(View.INVISIBLE);
                    mNewsFeedDisplay.startRefresh();
					break;

				case 1:
				    mNewsFeedDisplay.setVisibility(View.INVISIBLE);
                    mPiazzaDisplay.setVisibility(View.VISIBLE);
                    mPiazzaDisplay.startRefresh();
					break;

				}
//				mDisplay.startRefresh();
			}
			
		});
		mCamera.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				dialog();
			}
		});
	}

	private void getNewsFeed(String type) {
//		NewsFeedRequestParam param = new NewsFeedRequestParam(mApplication.mRenRen, type, null, null, String.valueOf(mPage),null);
//		
//		RequestListener<NewsFeedResponseBean> listener = new RequestListener<NewsFeedResponseBean>() {
//			public void onStart() {
//				handler.sendEmptyMessage(0);
//			}
//
//			public void onComplete(NewsFeedResponseBean bean) {
//				bean.Resolve(mIsAdd, true);
//				handler.sendEmptyMessage(1);
//			}
//		};
//		mApplication.mAsyncRenRen.getNewsFeed(param, listener);
	}


    private void getNewsFeed(StatusNet mStatusNet,
            final NoticeRequestParam param) {
        RequestListener<NoticeResponseBean> listener = new RequestListener<NoticeResponseBean>() {

            public void onStart() {
            }

            public void onComplete(final NoticeResponseBean bean) {
                if (bean.mStatusCode != 200) {
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            mNewsFeedDisplay.stopLoadMore();
                            mNewsFeedDisplay.stopRefresh();
                            if (mNewsFeedDisplay.getVisibility() == View.VISIBLE) {
                                Toast.makeText(mContext, bean.Message,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    return;
                }
                final ArrayList<RowStatus> status = Util.getData(mContext,
                        param.DB_ROW_TYPE, param.DB_ROW_EXTRA);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mNewsFeedDisplay.stopLoadMore();
                        mNewsFeedDisplay.stopRefresh();
                        mNewsFeedAdapter.notifyDataSetChanged(status);
                    }
                });
                return;

            }
        };
        param.context = mContext;
        param.mStatusNet = mStatusNet;
        mApplication.mAsyncRenRen.getNotices(listener, param);
    }
    
    private void getPiazza(StatusNet mStatusNet, final NoticeRequestParam param) {
        RequestListener<NoticeResponseBean> listener = new RequestListener<NoticeResponseBean>() {

            public void onStart() {
            }

            public void onComplete(final NoticeResponseBean bean) {
                if (bean.mStatusCode != 200) {
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            mPiazzaDisplay.stopLoadMore();
                            mPiazzaDisplay.stopRefresh();
                            if (mPiazzaDisplay.getVisibility() == View.VISIBLE) {
                                Toast.makeText(mContext, bean.Message,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    return;
                }
                final ArrayList<RowStatus> status = Util.getData(mContext,
                        param.DB_ROW_TYPE, param.DB_ROW_EXTRA);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mPiazzaDisplay.stopLoadMore();
                        mPiazzaDisplay.stopRefresh();
                        mPiazzaAdapter.notifyDataSetChanged(status);
                    }
                });
                return;

            }
        };
        param.context = mContext;
        param.mStatusNet = mStatusNet;
        mApplication.mAsyncRenRen.getNotices(listener, param);
    }

	private void initModePopupWindow(int chooseId) {
		ModePopAdapter adapter = new ModePopAdapter(mContext, mModeIcon,
				mModeName, chooseId);
		mModeListView.setAdapter(adapter);
		if (mModePopupWindow == null) {
			mModePopupWindow = new PopupWindow(mModeView,
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, true);
			mModePopupWindow.setBackgroundDrawable(new BitmapDrawable());
			mModePopupWindow.setAnimationStyle(R.style.ModePopupAnimation);
		}
		if (mModePopupWindow.isShowing()) {
			mModePopupWindow.dismiss();
		} else {
			mModePopupWindow.showAsDropDown(mModeLayout, 0, 0);
		}
	}

	private void dialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(mContext.getString(R.string.upload_picture));
		builder.setItems(new String[] {mContext.getString(R.string.from_cammera), mContext.getString(R.string.from_local) },
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = null;
						switch (which) {
						case 0:
							intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							File dir = new File(
									MustardDbAdapter.PATH_CAM);
							if (!dir.exists()) {
								dir.mkdirs();
							}
							mApplication.mImagePath = MustardDbAdapter.PATH_CAM
									+ UUID.randomUUID().toString();
							File file = new File(mApplication.mImagePath);
							if (!file.exists()) {
								try {
									file.createNewFile();
								} catch (IOException e) {

								}
							}
							intent.putExtra(MediaStore.EXTRA_OUTPUT,
									Uri.fromFile(file));
							mActivity.startActivityForResult(intent, 0);
							break;

						case 1:
							intent = new Intent(Intent.ACTION_PICK, null);
							intent.setDataAndType(
									MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
									"image/*");
							mActivity.startActivityForResult(intent, 1);
							break;
						}
					}
				});
		builder.create().show();
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
		}
	};
    

	public void setOnOpenListener(OnOpenListener onOpenListener) {
		mOnOpenListener = onOpenListener;
	}

	public View getView() {
//		mDisplay.setSelection(0);
		return mNewsFeed;
	}

	protected void showToastMessage(CharSequence message) {
        showToastMessage(message,false);
    }

    protected void showToastMessage(CharSequence message,boolean longView) {
        int popTime = longView ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
        Toast.makeText(mContext,
                message,
                popTime).show();
    }
  
    protected MustardDbAdapter getDbAdapter() {
        MustardDbAdapter dbAdapter = new MustardDbAdapter(mContext);
        dbAdapter.open();
        return dbAdapter;
    }

    public boolean ismFromSavedState() {
        return mFromSavedState;
    }

    public void setmFromSavedState(boolean mFromSavedState) {
        this.mFromSavedState = mFromSavedState;
    }
    public void newsFeedRefresh()
    {
    	
    	mNewsFeedDisplay.startRefresh();
		
        mPiazzaDisplay.startRefresh();
    }
    
    protected TimelineHandler mHandler = new TimelineHandler();

	class TimelineHandler extends Handler {

		private static final int MSG_PROGRESS = 2;
		private static final int MSG_GEOLOCATION_OK = 3;
		private static final int MSG_GEOLOCATION_KO = 4;
		private static final int MSG_REFRESH = 5;

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_PROGRESS:
				//setProgressBarIndeterminateVisibility(msg.arg1 != 0);
				break;
			case MSG_GEOLOCATION_OK:
				//GeoName gn = (GeoName)msg.obj;
				//doShowGeolocation(gn);
				break;
			case MSG_GEOLOCATION_KO:
				showErrorMessage((String)msg.obj);
				break;

			case MSG_REFRESH:
				//doSilentRefresh();
				newsFeedRefresh();
				break;
			}
		}

		public void progress(boolean progress) {
			Message msg = new Message();
			msg.what = MSG_PROGRESS;
			msg.arg1 = progress ? 1 : 0;
			sendMessage(msg);
		}
	}

	private void showErrorMessage(String reason) {
		Toast.makeText(mContext, mContext.getString(R.string.error_generic_detail,reason), Toast.LENGTH_LONG).show();
	}
	
}
