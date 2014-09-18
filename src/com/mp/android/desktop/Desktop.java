package com.mp.android.desktop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mp.android.BaseApplication;
import com.mp.android.R;
//import com.mp.android.settings.Settings;
import com.mp.android.statusnet.MustardDbAdapter;
import com.mp.android.statusnet.activity.AccountManage;
import com.mp.android.statusnet.provider.StatusNet;
import com.mp.android.statusnet.util.MustardException;
import com.mp.android.util.DataUpdateObservable;
import com.mp.android.util.Util;
import com.mp.android.util.View_Util;

public class Desktop implements Observer{
	private BaseApplication mApplication;
	private Context mContext;
	private View mDesktop;
	private LinearLayout mInformation;
	private TextView mName;
	private ListView mDisplay;
	private List<Map<String, Object>> mGroup = new ArrayList<Map<String, Object>>();
	private List<List<Map<String, Object>>> mChild = new ArrayList<List<Map<String, Object>>>();
	private String[] mGroupName;
	private String[] mChildFavorite;
	private List<String> mChildFavoriteList;
	private int[] mChildFavoriteIcon;
	private String[] mChildAction;
	private int[] mChildActionIcon;
	public static int mChooesId = 0;
	private DesktopNewAdapter mAdapter;
	private onChangeViewListener mOnChangeViewListener;
	private Handler mainHandler = new Handler();
	public Desktop(BaseApplication application, Context context) {
		mApplication = application;
		mContext = context;
		mDesktop = LayoutInflater.from(context).inflate(R.layout.fragment_slide_left, null);
		findViewById();
		init();
		setListener();
		DataUpdateObservable.getInstance().addObserver(this);
	}

	private void findViewById() {
		mInformation = (LinearLayout) mDesktop
				.findViewById(R.id.desktop_top_layout);

		mName = (TextView) mDesktop.findViewById(R.id.desktop_top_name);
		mDisplay = (ListView) mDesktop.findViewById(R.id.index_left_listview);
	}

	private void setListener() {
		mInformation.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				mAdapter.notifyDataSetChanged();
			}
		});

        mDisplay.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
            		if (position!=2){
                		mChooesId = position;
            		}
					mAdapter.notifyDataSetChanged();
					switch (position) {
						case 0:
							mOnChangeViewListener
									.onChangeView(View_Util.NewsFeed);
							break;

						case 1:
							mOnChangeViewListener
									.onChangeView(View_Util.Message);
							break;
							
						case 2:
							mContext.startActivity(new Intent(mContext,
								AccountManage.class));
							break;
							
						default:
							break;
						}
            }
        });
	}

	private void init() {
		init_Data();
        MustardDbAdapter mMustardDbAdapter = Util.getDbAdapter(mContext);
        final StatusNet mStatusNet = mApplication.checkAccount(mMustardDbAdapter);
        if (null != mMustardDbAdapter) {
            mMustardDbAdapter.close();
            mMustardDbAdapter = null;
        }
        new Thread() {
            public void run() {
                try {
                    mApplication.nowAccountInfo = mStatusNet.checkUser();
                } catch (final MustardException e) {
                    mainHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }
            };
        }.start();
        mName.setText(mStatusNet.getMUsername());
		mAdapter = new DesktopNewAdapter(mContext,mChildFavoriteList);
		mDisplay.setAdapter(mAdapter);
	}

	private void init_Data() {
		mGroupName = mContext.getResources().getStringArray(
				R.array.desktop_list_head_strings);
		mChildFavorite = mContext.getResources().getStringArray(
				R.array.desktop_list_item_favorite_strings);
		mChildFavoriteList = Arrays.asList(mChildFavorite);
		mChildAction = mContext.getResources().getStringArray(
				R.array.desktop_list_item_action_strings);
		mChildFavoriteIcon = new int[3];
		mChildActionIcon = new int[1];
		mChildFavoriteIcon[0] = R.drawable.v5_0_1_desktop_list_newsfeed;
		mChildFavoriteIcon[1] = R.drawable.v5_0_1_desktop_list_message;

		mChildActionIcon[0] = R.drawable.v5_0_1_desktop_list_settings;
		getGroupList();
		getChildList();
	}

	private void getGroupList() {
		for (int i = 0; i < mGroupName.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", mGroupName[i]);
			mGroup.add(map);
		}
	}

	private void getChildList() {
		for (int i = 0; i < mGroupName.length; i++) {
			if (i == 0) {
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				for (int j = 0; j < mChildFavorite.length; j++) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("icon", mChildFavoriteIcon[j]);
					map.put("name", mChildFavorite[j]);
					map.put("click", false);
					list.add(map);
				}
				mChild.add(list);
			} else if (i == 1) {
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				for (int j = 0; j < mChildAction.length; j++) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("icon", mChildActionIcon[j]);
					map.put("name", mChildAction[j]);
					map.put("click", false);
					list.add(map);
				}
				mChild.add(list);
			}
		}
		// Ĭ��ѡ�������һ��
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("icon", mChildFavoriteIcon[0]);
		map.put("name", mChildFavorite[0]);
		map.put("click", true);
		mChild.get(0).set(0, map);
	}

	public View getView() {
		return mDesktop;
	}

	public interface onChangeViewListener {
		public abstract void onChangeView(int arg0);
	}

	public void setOnChangeViewListener(
			onChangeViewListener onChangeViewListener) {
		mOnChangeViewListener = onChangeViewListener;
	}
	
	/*
	 * add by fancuiru at 2013-12-28 for update Data when change avatar 
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		Log.v("fcr","Desktop update begin");
		MustardDbAdapter mMustardDbAdapter = Util.getDbAdapter(mContext);
        final StatusNet mStatusNet = mApplication.checkAccount(mMustardDbAdapter);
        if (null != mMustardDbAdapter) {
            mMustardDbAdapter.close();
            mMustardDbAdapter = null;
        }
        new Thread() {
            public void run() {
                try {
                    mApplication.nowAccountInfo = mStatusNet.checkUser();
                } catch (final MustardException e) {
                    mainHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }
                /*
                mainHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        mName.setText(mApplication.nowAccountInfo.getName());
                        mApplication.mHeadBitmap.display(mAvatar,
                                mApplication.nowAccountInfo
                                        .getProfile_image_url());
                    }
                });*/
            };
        }.start();
        Log.v("fcr","Desktop update end");
	}
	//add end
	
	public void finishDesktop() {
		mChooesId = 0;
		if(null != mInformation) {
			mInformation = null;
		}
		//if(null != mAvatar) {
		//	mAvatar = null;
		//}
		if(null != mGroup) {
			mGroup = null;
		}
		if(null != mChild) {
			mChild = null;
		}
		if(null != mDesktop) {
			mDesktop = null;
		}
		
	}
}
