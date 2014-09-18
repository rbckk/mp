package com.mp.android.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.mp.android.BaseApplication;
import com.mp.android.R;
import com.mp.android.statusnet.MustardDbAdapter;
import com.mp.android.statusnet.activity.BasicAuthLogin;
import com.mp.android.statusnet.provider.StatusNet;
import com.mp.android.util.Constant;

/**
 * ��ӭ����
 * 
 * @author rendongwei
 * 
 */
public class Welcome extends Activity implements Runnable {
	private BaseApplication mApplication;
	protected StatusNet mStatusNet = null;
	String TAG = "Welcome";
	public static int SDK_VERSION = getSDKVersionNumber();
	public static boolean PRE_CUPCAKE = 
		     getSDKVersionNumber() > 15 ? true : false;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		mApplication=(BaseApplication) getApplication();
		getStatusNet();
		new Thread(this).start();
	}

	protected long mStatusNetAccountId = -1;
	protected static final String EXTRA_ACCOUNT="mustard.account";
	
	private void getStatusNet() {
		MustardDbAdapter mDbHelper = getDbAdapter();
		if (mStatusNetAccountId>=0) {
			mStatusNet = mApplication.checkAccount(mDbHelper,mStatusNetAccountId);
		} else {
			mStatusNet = mApplication.checkAccount(mDbHelper);
		}
		
		mDbHelper.close();
	}
	
	protected MustardDbAdapter getDbAdapter() {
		MustardDbAdapter dbAdapter = new MustardDbAdapter(this);
		dbAdapter.open();
		return dbAdapter;
	}
	
	public void run() {
		try {
			Thread.sleep(1000);
			Log.v(TAG,"SDK_VERSION===>" + SDK_VERSION + ",PRE_CUPCAKE===>" +PRE_CUPCAKE);
			PRE_CUPCAKE = true;
			if(PRE_CUPCAKE) {
				if(mStatusNet == null){
					startActivity(new Intent(Welcome.this, BasicAuthLogin.class));
				}
				else
				{
					startActivity(new Intent(Welcome.this, DesktopActivity.class));
				}
				finish();
			}else {
				handler.sendEmptyMessage(0);
			}
		} catch (InterruptedException e) {

		}
	}
	
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0 :
				new AlertDialog.Builder(Welcome.this)
				.setTitle(getString(R.string.alert_dialog_title))
				.setMessage(getString(R.string.version_alert))
				.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						dialog.dismiss();
						// TODO Auto-generated method stub
						if(mStatusNet == null){
							startActivity(new Intent(Welcome.this, BasicAuthLogin.class));
						}
						else
						{
							startActivity(new Intent(Welcome.this, DesktopActivity.class));
						}
						finish();
					}
				})
				.setNegativeButton(getString(R.string.close), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						finish();
					}
				})
				.setOnCancelListener(new OnCancelListener() {
			        
			        @Override
			        public void onCancel(DialogInterface dialog) {
			            // TODO Auto-generated method stub
			        	finish();
			        }
			    })
				.show();
				
				break;
			}
		};
	};
	
	public static int getSDKVersionNumber() {
		   int sdkVersion;
		   try {
		     sdkVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
		   } catch (NumberFormatException e) {
		     sdkVersion = 0;
		   }
		   return sdkVersion;
		}
	
}
