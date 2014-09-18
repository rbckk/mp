package com.mp.android.statusnet.activity;
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

import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.mp.android.BaseApplication;
import com.mp.android.R;
import com.mp.android.statusnet.MustardDbAdapter;
import com.mp.android.statusnet.provider.StatusNet;
import com.mp.android.statusnet.statusnet.StatusNetService;
import com.mp.android.statusnet.statusnet.User;
import com.mp.android.statusnet.util.MustardException;
import com.mp.android.ui.DesktopActivity;
import com.mp.android.util.Constant;


public class BasicAuthLogin extends Activity {
	
	protected static final int DIALOG_VERIFING_ID=0;
	protected static final int DIALOG_AUTHENTICATING_ID=1;
	
	protected static String EXTRA_INSTANCE = "extra.instance";
	
	private MustardDbAdapter mDbHelper;
	// Views.
	private EditText mUsernameEdit;
	private EditText mPasswordEdit;
	private EditText mInstanceEdit;
	private CheckBox mForceSSLEdit;
	private Button mSaveButton;
	//private Button mOAuthButton;
	private Button mRegisterButton;
	
	private String mUsername;
	private String mPassword;
	private String mInstance;
	private URL  mURL;
	private String mSURL;
	private StatusNet mStatusNet;
	
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setTitle(getString(R.string.app_name)+" - " + getString(R.string.lbl_create_account));
		mDbHelper = new MustardDbAdapter(this);
		mDbHelper.open();
		mUsernameEdit = (EditText) findViewById(R.id.edit_username);
		mUsernameEdit.setText("");
		mPasswordEdit = (EditText) findViewById(R.id.edit_password);
		mPasswordEdit.setText("");
		//mInstanceEdit = (EditText) findViewById(R.id.edit_instance);
		//mInstanceEdit.setText("");
		//mForceSSLEdit = (CheckBox) findViewById(R.id.force_ssl);
		//mForceSSLEdit.setVisibility(View.GONE);//add by xiaosong
		
		Intent intent = getIntent();

		//String instance = intent.getStringExtra(EXTRA_INSTANCE);
		//if (instance != null && !"".equals(instance)) {
		//	mInstanceEdit.setText(instance);
		//}
		
		mSaveButton = (Button) findViewById(R.id.button_login);
		mSaveButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				doLogin();
			}
		});
//		mSaveButton.setOnTouchListener(new View.OnTouchListener(){
//			public boolean onTouch(View v, MotionEvent event){
//				if(event.getAction() == MotionEvent.ACTION_DOWN){
//					mSaveButton.getBackground().setAlpha(100);				
//				}
//				return true;
//			}
//		});
		
		//mOAuthButton = (Button) findViewById(R.id.button_oauth);
 		//add by xiaosong
		//mOAuthButton.setVisibility(View.GONE);
		//end by xiaosong
		//mOAuthButton.setOnClickListener(new View.OnClickListener() {
		//	public void onClick(View v) {
		//		doOAuthLogin();
		//	}
		//});
		mRegisterButton = (Button)findViewById(R.id.register);
		mRegisterButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent();
				i.setClassName("com.mp.android", "com.mp.android.register.RegisterActivity");
				startActivity(i);
			}
		});
	}

	private void doOAuthLogin() {
//		Intent i = new Intent(getApplicationContext(), OAuthLogin.class);
//		mInstance = mInstanceEdit.getText().toString();
//		if (mInstance != null && !"".equals(mInstance))
//			i.putExtra(OAuthLogin.EXTRA_INSTANCE, mInstance );
//		startActivity(i);
////		setResult(RESULT_OK);
//		finish();
	}
	
	private void doLogin() {
		
		//mInstance = mInstanceEdit.getText().toString();
		//add by xiaosong.xu for test
		mInstance = MustardDbAdapter.INSTANCE;
		Log.v("mustard", "mInstance----"+mInstance);
		if (mInstance == null || "".equals(mInstance))
			return;
		if (mInstance.endsWith("/"))
			mInstance=mInstance.substring(0, mInstance.length()-1);
		if (mInstance.toLowerCase().startsWith("http"))
			mSURL = mInstance;
		else {
			mSURL = "http://" + mInstance;
		}
		try {
			mURL = new URL(mSURL);
		} catch (MalformedURLException e) {
			new AlertDialog.Builder(BasicAuthLogin.this)
            .setTitle(getString(R.string.error))
            .setMessage(getString(R.string.error_invalid_url))
            .setNeutralButton(getString(R.string.close), null).show();
			return;
		}
		// If it's twitter I force OAuth login
		if(mURL.getHost().equalsIgnoreCase("twitter.com")) {
			doOAuthLogin();
		}
		mUsername = mUsernameEdit.getText().toString();
		if (mUsername == null || "".equals(mUsername))
			return;
		mPassword = mPasswordEdit.getText().toString();
		if (mPassword == null || "".equals(mPassword))
			return;
		
		try {
			final long uid = mDbHelper.userExists(mUsername,mSURL);
			if(uid > 0) {
				new AlertDialog.Builder(BasicAuthLogin.this)
				.setTitle(getString(R.string.error))
	            .setMessage(getString(R.string.error_duplicate_account))
	            .setPositiveButton(R.string.yes,new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface xdialog, int id) {
	    				mDbHelper.deleteAccount(uid);
	    				mDbHelper.resetDefaultAccounts();
	    				new AuthenticateAccountTask().execute();
	    			}
	    		}).setNeutralButton(R.string.no, null).show();
				
				return;
			}
		} catch(Exception e) {
			Log.e(getClass().getCanonicalName(),e.toString());
			e.printStackTrace();
		}
		 
		new AuthenticateAccountTask().execute();
	}

	private void completeAction() {
		if (mInstance.endsWith("identi.ca") && mStatusNet != null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getString(R.string.msg_welcome,mUsername))
			.setTitle(getString(R.string.title_welcome))
			.setCancelable(false)
			.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface xdialog, int id) {
					try {
//						mStatusNet.doSubscribe("95570");
						mStatusNet.doSubscribe("mustard");
					} catch (MustardException e) {
						Log.e("mustard"," Error subscribing.. " + e.getMessage());
					}
					startMainActivity();
//					setResult(RESULT_OK);
//			        finish();
				}
			})
			.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface xdialog, int id) {
					xdialog.cancel();
					startMainActivity();
//					setResult(RESULT_OK);
//			        finish();
				}
			});
			builder.create();
			builder.show();
		} else if  (mSURL.endsWith("twitter.com") && mStatusNet != null) {
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getString(R.string.msg_welcome,mUsername))
			.setTitle(getString(R.string.title_welcome))
			.setCancelable(false)
			.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface xdialog, int id) {
					try {
						mStatusNet.doSubscribe("179569425");
					} catch (MustardException e) {
						Log.e("mustard"," Error subscribing.. " + e.getMessage());
					}
//					setResult(RESULT_OK);
//			        finish();
				}
			})
			.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface xdialog, int id) {
					xdialog.cancel();
//					setResult(RESULT_OK);
//			        finish();
				}
			});
			builder.create();
			builder.show();
		} else {
			startMainActivity();
//			setResult(RESULT_OK);
//	        finish();
		}
	}
	
	private void startMainActivity() {
		//MustardMain.actionHandleTimeline(this);
		//test by xiaosong
		startActivity(new Intent(this, DesktopActivity.class));
		finish();
	}
	
	public void onDestroy() {
		if(mDbHelper != null) {
			mDbHelper.close();
		}
		super.onDestroy();
	}
	

	@Override
	protected Dialog onCreateDialog(int id) {
		ProgressDialog dialog;
    	dialog = new ProgressDialog(this);
    	dialog.setIndeterminate(true);
    	dialog.setCancelable(false);
	    switch(id) {
	    case DIALOG_VERIFING_ID:
	        // do the work to define the pause Dialog
	    	dialog.setMessage(getString(R.string.contacting_site));
	        break;
	       
	    case DIALOG_AUTHENTICATING_ID:
	    	dialog.setMessage(getString(R.string.authenticating_account));
	        break;
	        
	    default:
	        dialog = null;
	    }
	    return dialog;
	
	}
	
	public class AuthenticateAccountTask extends AsyncTask<Void, Integer, Long> {

		private	boolean error=false;
		private String errorMsg="";
		private int progress = 0;
		@Override
		protected Long doInBackground(Void... v) {
			
			mStatusNet = new StatusNet(getBaseContext());
			mStatusNet.setURL(mURL);
			StatusNetService sns = null;
			//add by xiaosong.xu for test
			Log.v("mustard", "mStatusNet.isTwitterInstance()--"+mStatusNet.isTwitterInstance());
			if(!mStatusNet.isTwitterInstance()) {
				
				try {
					// Check if mURL is a statusnet instance
					sns = mStatusNet.getConfiguration();
					Log.v("mustard", "############################## textlimit: " + sns.site.textlimit);
				} catch (MustardException e) {
					//				if(BaseApplication.DEBUG)
					e.printStackTrace();
					error=true;
					errorMsg=getString(R.string.error_help_test);
				} catch (Exception e) {
					e.printStackTrace();
					errorMsg=e.getMessage();
					error=true;
				}
				if (sns == null) {
					// this is not a SN instance..
					error=true;
					errorMsg=getString(R.string.error_help_test);
				}
				if(error) {
					return null;
				}
			} else {
				sns = new StatusNetService();
				sns.site.textlimit=140;
			}
			publishProgress(1);
			User user = null;
			try {
				mStatusNet.setCredentials(mUsername,mPassword);
				user = mStatusNet.checkUser();
				if (user==null) {
					error=true;
					errorMsg=getString(R.string.error_wrong_userpass);
					return null;
				}
			} catch (Exception e) {
				error=true;
				errorMsg=getString(R.string.error_wrong_userpass);
				return null;
			}
			String version = "";
			if(!mStatusNet.isTwitterInstance()) {
				version = mStatusNet.getVersion();
				if (version == null) {
					error=true;
					errorMsg=getString(R.string.error_instance_unkown_version,mInstance,BaseApplication.SN_MIN_VERSION);
					return null;
				}
				// Hack: version is returned inside ""
				if(version.startsWith("\""))
					version=version.replaceAll("\"", "");
				BaseApplication _ma = (BaseApplication)getApplication();
				if (!_ma.checkVersion(version)) {
					error=true;
					errorMsg=getString(R.string.error_instance_too_old_version,version,BaseApplication.SN_MIN_VERSION);
					return null;
				}
			} else {
				version = BaseApplication.SN_MIN_VERSION;
			}
			long usernameId = 0;
			
			usernameId = user.getId();
			long userid = mDbHelper.createAccount(usernameId, mUsername, mPassword, mSURL, 1,version);
			
			try {
				mDbHelper.setAttachmentTextLimitInstance(mURL.toExternalForm(), sns.site.textlimit, sns.site.attachmentMaxSize);
			} catch(Exception e) {
				Log.e("mustard", e.getMessage());
			}
			return userid; 
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(DIALOG_VERIFING_ID);
		}

		protected void onProgressUpdate (Integer... values) {
			if (values[0]==1) {
				progress=1;
				dismissDialog(DIALOG_VERIFING_ID);
				showDialog(DIALOG_AUTHENTICATING_ID);
			}
		}
		
		protected void onPostExecute(Long result) {
			if(progress==1)
				dismissDialog(DIALOG_AUTHENTICATING_ID);
			else
				dismissDialog(DIALOG_VERIFING_ID);
			if (error) {
				new AlertDialog.Builder(BasicAuthLogin.this)
	            .setTitle(getString(R.string.error))
	            .setMessage(errorMsg)
	            .setNeutralButton(getString(R.string.close), null).show();
			} else {
				completeAction();
			}
		}

	}
	
	public static void actionHandleLogin(Context context) {
		Intent intent = new Intent(context, BasicAuthLogin.class);
	    context.startActivity(intent);
	}
	
	public static void actionHandleLogin(Context context,String host) {
		Intent intent = new Intent(context, BasicAuthLogin.class);
		intent.putExtra(EXTRA_INSTANCE, host);
	    context.startActivity(intent);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
			System.exit(0);
			android.os.Process.killProcess(android.os.Process.myPid());
			Log.v("fancuiru","BackKey");
		}
		return super.onKeyDown(keyCode, event);
	}
}
