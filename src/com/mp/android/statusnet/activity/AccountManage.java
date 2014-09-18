package com.mp.android.statusnet.activity;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.IntentService;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.mp.android.BaseApplication;
import com.mp.android.R;
//import com.mp.android.im.roster.IRosterCallback;
//import com.mp.android.im.roster.RosterAdapter;
//import com.mp.android.im.service.IRosterConnection;
//import com.mp.android.im.service.JabberService;
import com.mp.android.statusnet.MustardDbAdapter;
import com.mp.android.statusnet.provider.StatusNet;
import com.mp.android.ui.DesktopActivity;
import com.mp.android.ui.Welcome;

public class AccountManage extends Activity implements  View.OnClickListener {

	private Button exitAccount;
	private ImageView account_avatar;
	private TextView account_name;
	protected int DB_ROW_TYPE;
	protected StatusNet mStatusNet = null;
	//NoticeListAdapter mNoticeCursorAdapter = null;
	private BaseApplication mApplication;
	private Intent serviceIntent;
	private ServiceConnection serviceConnection;
	//private RosterAdapter serviceAdapter;
	Handler mainHandler = new Handler();
	//private IRosterCallback.Stub callback;
	private static final String TAG = "AccountManage";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_manage);
		mApplication = (BaseApplication) getApplication();
		exitAccount = (Button) findViewById(R.id.exitAccount);
		account_avatar = (ImageView)findViewById(R.id.account_avatar);
		account_name = (TextView) findViewById(R.id.account_name);
		exitAccount.setOnClickListener(this);
		getStatusNet();
		//	mApplication.mHeadBitmap.display(account_avatar,
	    //             mApplication.nowAccountInfo
	    //                     .getProfile_image_url_large());
		account_name.setText(getString(R.string.account_name)+  mStatusNet.getMUsername().toString());
		createCallback();
	    registerJabberService();
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()) {
		case R.id.exitAccount:
			logout();
			break;
		}
		
	}
		
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		this.bindService(serviceIntent, serviceConnection,
                IntentService.BIND_AUTO_CREATE);
		Log.v(TAG,"AccountManager onResume  bindService");
	}
	
	  private void registerJabberService() {
	        serviceIntent = new Intent();
	        serviceIntent.setAction(MustardDbAdapter.ACTION_REMOTE_SERVICE);
	        serviceConnection = new ServiceConnection() {

	            @Override
	            public void onServiceConnected(ComponentName name, IBinder service) {
	            //    serviceAdapter = new RosterAdapter(
	            //            IRosterConnection.Stub.asInterface(service));
	            //    serviceAdapter.registerCallback(callback);
	            }

	            @Override
	            public void onServiceDisconnected(ComponentName name) {

	            }
	        };
	    }

	protected long mStatusNetAccountId = -1;
	private void getStatusNet() {
		MustardDbAdapter mDbHelper = getDbAdapter();
		mStatusNet = mApplication.checkAccount(mDbHelper);
		mDbHelper.close();
	}
	
	private void showLogin() {
		Intent intent = new Intent(AccountManage.this, Welcome.class);
		DesktopActivity.finshActivity();
	    startActivity(intent);
		this.finish();
	}

	private void logout() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.msg_logout)
		.setCancelable(false)
		.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface xdialog, int id) {
		       // if (null != serviceAdapter) {
		       //     if (serviceAdapter.isLogged()) {
	           //         serviceAdapter.disconnect();
	           //     } 
		       // }
				MustardDbAdapter mDbHelper = getDbAdapter();
				if(mStatusNet.getUserId() != 0) {
				mDbHelper.deleteAccount(mStatusNet.getUserId());
				mDbHelper.deleteStatuses(MustardDbAdapter.ROWTYPE_ALL, "");
				mDbHelper.close();
				showLogin();
				}
			}
		})
		.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface xdialog, int id) {
				xdialog.cancel();
			}
		});
		builder.create();
		builder.show();
	}
	
	protected MustardDbAdapter getDbAdapter() {
		MustardDbAdapter dbAdapter = new MustardDbAdapter(this);
		dbAdapter.open();
		return dbAdapter;
	}
	
	
	
	 private void createCallback() {
	 /*       callback = new IRosterCallback.Stub() {

	            @Override
	            public void rosterChanged() throws RemoteException {
	                mainHandler.post(new Runnable() {

	                    @Override
	                    public void run() {
	                    }
	                });
	            }

	            @Override
	            public void connectOk() throws RemoteException {
	                mainHandler.post(new Runnable() {

	                    @Override
	                    public void run() {
	                        
	                    }
	                });
	            }

	            @Override
	            public void connectFail() throws RemoteException {
	                mainHandler.post(new Runnable() {

	                    @Override
	                    public void run() {
	                       
	                    }
	                });
	            }

	            @Override
	            public void disconnect() throws RemoteException {
	            	
	            }

	            @Override
	            public void presenceChanged(final String id) throws RemoteException {
	                mainHandler.post(new Runnable() {
	                    @Override
	                    public void run() {
	                     
	                    }
	                });
	            }

	            @Override
	            public void chatOpened(String jabberid) throws RemoteException {

	            }
	        };*/
	    }
	 
	 @Override
		protected void onPause() {
			// TODO Auto-generated method stub
			unbindService(serviceConnection);
			Log.v("DesktopActivity","AccountManager onPause unbindService");
			super.onPause();
		}
		
		@Override
		protected void onStop() {
			// TODO Auto-generated method stub
			Log.v("DesktopActivity","AccountManager onStop");
			super.onStop();
		}
		
		@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			Log.v("DesktopActivity","AccountManager onDestroy");
			super.onDestroy();
		}
		
}
