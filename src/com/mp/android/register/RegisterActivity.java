package com.mp.android.register;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;

//import com.mp.android.BaseApplication;
import com.mp.android.R;
import com.mp.android.statusnet.MustardDbAdapter;
import com.mp.android.statusnet.provider.StatusNet;
import com.mp.android.statusnet.util.AuthException;
import com.mp.android.statusnet.util.MustardException;
import com.mp.android.ui.base.LoadingDialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.util.Rfc822Tokenizer;
//import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
//import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class RegisterActivity extends Activity {

	private ImageButton cancleButton;
	private ImageButton registerUpdateButton;
	private EditText nickname;
	private EditText password;
	private EditText passwordAffirm;
//	private EditText email;
//	private EditText fullname;
//	private EditText homepage;
//	private EditText bio;
//	private EditText location;
	private ImageView nickname_right;
	private ImageView password_right;
	private ImageView passwordAffirm_right;
//	private ImageView email_right;
//	private ImageView fullname_right;
//	private ImageView homepage_right;
//	private ImageView bio_right;
//	private ImageView location_right;
//	private ImageView registerBack;
//	private MustardDbAdapter mDbHelper;
	private StatusNet mStatusNet;
//	private BaseApplication mBaseApplication;

	private boolean nickname_ok = false;
	private boolean password_ok = false;
	private boolean passwordAffirm_ok = false;
//	private boolean email_ok = false;
//	private boolean fullname_ok = false;
//	private boolean homepage_ok = false;
//	private boolean bio_ok = false;
//	private boolean location_ok = false;
	private String ERROR_EMPTY;
	private String ERROR_ILLEGALITY;
	private String mInstance;
	private String mSURL;
	private URL mURL;
	private LoadingDialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_layout);
//		mBaseApplication = (BaseApplication) getApplication();
		mDialog = new LoadingDialog(this, R.style.dialog);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setCancelable(false);
		ERROR_EMPTY = getResources().getString(R.string.error_empty);
		ERROR_ILLEGALITY = getResources().getString(R.string.error_illegality);
		mInstance = MustardDbAdapter.INSTANCE;
		mSURL = "http://" + mInstance;
		findView();
		setOnclickLinstener();
	}

	private void findView() {
		cancleButton = (ImageButton) findViewById(R.id.register_cancle);
		registerUpdateButton = (ImageButton) findViewById(R.id.register_update_button);
		nickname = (EditText) findViewById(R.id.nickname);
		password = (EditText) findViewById(R.id.password);
		passwordAffirm = (EditText) findViewById(R.id.password_affirm);
		//email = (EditText) findViewById(R.id.email);
		//fullname = (EditText) findViewById(R.id.fullname);
		//homepage = (EditText) findViewById(R.id.homepage);
		//bio = (EditText) findViewById(R.id.bio);
		//location = (EditText) findViewById(R.id.location);
		nickname_right = (ImageView) findViewById(R.id.nickname_right);
		password_right = (ImageView) findViewById(R.id.password_right);
		passwordAffirm_right = (ImageView) findViewById(R.id.password_affirm_right);
		//email_right = (ImageView) findViewById(R.id.email_right);
		//fullname_right = (ImageView) findViewById(R.id.fullname_right);
		//homepage_right = (ImageView) findViewById(R.id.homepage_right);
		//bio_right = (ImageView) findViewById(R.id.bio_right);
		//location_right = (ImageView) findViewById(R.id.location_right);
		//registerBack = (ImageView) findViewById(R.id.register_back);
	}

	private void setOnclickLinstener() {
		cancleButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		registerUpdateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				registerUpdateButton.setEnabled(false);
				try {
					mURL = new URL(mSURL);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (checkInput()) {
					mDialog.show();
					new RegisterUpdate().execute("");
				} else {
					Toast.makeText(getApplicationContext(),
							getString(R.string.register_info_error),
							Toast.LENGTH_SHORT).show();
					registerUpdateButton.setEnabled(true);
				}

			}
		});
//		registerBack.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				finish();
//			}
//		});
		nickname.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				checkNickname();
			}
		});
		password.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				checkPassWord();
			}
		});
		passwordAffirm.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				checkPassWordAffirm();
			}
		});
		/*
		email.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				checkEmail();
			}
		});
		fullname.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				checkFullName();
			}
		});
		homepage.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				checkHomePage();
			}
		});
		bio.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				checkBio();
			}
		});
		location.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				checkLocation();
			}
		});*/
	}

	private String getContentEdit(EditText t) {
		return t.getText().toString().trim();
	}

	private boolean checkInput() {
		boolean allRight = false;
		checkNickname();
		checkPassWord();
		checkPassWordAffirm();
		/*
		checkFullName();
		checkEmail();
		checkHomePage();
		checkBio();
		checkLocation();
		*/
//		if (nickname_ok && password_ok && passwordAffirm_ok && email_ok
//				&& fullname_ok && homepage_ok && bio_ok && location_ok) {
//			allRight = true;
//		}
		if (nickname_ok && password_ok && passwordAffirm_ok) {
			allRight = true;
		}
		return allRight;
	}

	private boolean isUserNameLegal(String userName) {
		String regex = "([0-9]|[a-z]){1,64}";
		if (TextUtils.isEmpty(userName)) {
			return false;
		}
		return userName.matches(regex);
	}

	private void checkNickname() {
		String nickName = getContentEdit(nickname);
		if (!TextUtils.isEmpty(nickname.getError())) {
			nickname_ok = false;
			nickname_right.setVisibility(View.INVISIBLE);
		}
		if (nickName.isEmpty()) {
			nickname_ok = false;
			//nickname.setError(ERROR_EMPTY);
			nickname_right.setVisibility(View.INVISIBLE);
		} else if (isUserNameLegal(nickName)) {
			nickname_ok = true;
			nickname_right.setVisibility(View.VISIBLE);
		} else {
			nickname_ok = false;
			//nickname.setError(ERROR_ILLEGALITY);
			nickname_right.setVisibility(View.INVISIBLE);
		}
	}

	private void checkPassWord() {
		String passWord = getContentEdit(password);
		if (TextUtils.isEmpty(passWord)) {
			//password.setError(ERROR_EMPTY);
			password_ok = false;
			password_right.setVisibility(View.INVISIBLE);
		} else if (passWord.length() < 6 || passWord.length() > 15) {
//			if (passWord.length() < 6) {
//				password.setError(getResources().getString(
//						R.string.password_too_short));
//			} else {
//				password.setError(getResources().getString(
//						R.string.password_too_long));
//			}
			password_ok = false;
			password_right.setVisibility(View.INVISIBLE);
		} else {
			password_ok = true;
			password_right.setVisibility(View.VISIBLE);
		}
	}

	private void checkPassWordAffirm() {
		String passWordAffirm = getContentEdit(passwordAffirm);
		if (TextUtils.isEmpty(passWordAffirm)) {
			//passwordAffirm.setError(ERROR_EMPTY);
			passwordAffirm_ok = false;
			passwordAffirm_right.setVisibility(View.INVISIBLE);
			return;
		}
		if (!TextUtils.equals(getContentEdit(password), passWordAffirm)) {
			//passwordAffirm.setError(getResources().getString(
			//		R.string.password_checking_error));
			passwordAffirm_ok = false;
			passwordAffirm_right.setVisibility(View.INVISIBLE);
		} else {
			passwordAffirm_ok = true;
			passwordAffirm_right.setVisibility(View.VISIBLE);
		}
	}
/*
	private void checkEmail() {
		String emails = getContentEdit(email);
		if (!TextUtils.isEmpty(email.getError())) {
			email_ok = false;
			email_right.setVisibility(View.INVISIBLE);
			return;
		}
		if (emails.isEmpty()) {
			email.setError(ERROR_EMPTY);
			email_ok = false;
			email_right.setVisibility(View.INVISIBLE);
		} else if (isAllValid(emails)) {
			email_ok = true;
			email_right.setVisibility(View.VISIBLE);
		} else {
			email.setError(ERROR_ILLEGALITY);
			email_right.setVisibility(View.INVISIBLE);
			email_ok = false;
		}
	}

	private void checkFullName() {
		String fullName = getContentEdit(fullname);
		if (!TextUtils.isEmpty(fullname.getError())) {
			fullname_ok = false;
			fullname_right.setVisibility(View.INVISIBLE);
			return;
		}
		if (fullName.isEmpty()) {
			fullname.setError(ERROR_EMPTY);
			fullname_right.setVisibility(View.INVISIBLE);
			fullname_ok = false;
		} else {
			fullname_ok = true;
			fullname_right.setVisibility(View.VISIBLE);
		}
	}

	private void checkHomePage() {
		String homePage = getContentEdit(homepage);
		if (!TextUtils.isEmpty(homepage.getError())) {
			homepage_ok = false;
			homepage_right.setVisibility(View.INVISIBLE);
			return;
		}
		if (homePage.isEmpty()) {
			homepage.setError(ERROR_EMPTY);
			homepage_right.setVisibility(View.INVISIBLE);
			homepage_ok = false;
		} else {
			homepage_ok = true;
			homepage_right.setVisibility(View.VISIBLE);
		}
	}

	private void checkBio() {
		String bios = getContentEdit(bio);
		if (!TextUtils.isEmpty(bio.getError())) {
			bio_ok = false;
			bio_right.setVisibility(View.INVISIBLE);
			return;
		}
		if (bios.isEmpty()) {
			bio.setError(ERROR_EMPTY);
			bio_right.setVisibility(View.INVISIBLE);
			bio_ok = false;
		} else {
			bio_ok = true;
			bio_right.setVisibility(View.VISIBLE);
		}
	}

	private void checkLocation() {
		String locations = getContentEdit(location);
		if (!TextUtils.isEmpty(location.getError())) {
			location_ok = false;
			location_right.setVisibility(View.INVISIBLE);
			return;
		}
		if (locations.isEmpty()) {
			location.setError(ERROR_EMPTY);
			location_right.setVisibility(View.INVISIBLE);
			location_ok = false;
		} else {
			location_ok = true;
			location_right.setVisibility(View.VISIBLE);
		}
	}
*/
	private int register() throws JSONException {
		int id = -1;
		try {
			mStatusNet = new StatusNet(getBaseContext());
			mStatusNet.setURL(mURL);
			if (mStatusNet == null) {
				return id;
			}
			try {
//				id = mStatusNet.register(getContentEdit(nickname),
//						getContentEdit(password), getContentEdit(email),
//						getContentEdit(fullname), getContentEdit(homepage),
//						getContentEdit(bio), getContentEdit(location), null);
				id = mStatusNet.register(getContentEdit(nickname),
						getContentEdit(password), "email@hack.com",
						"fullname", "homepage",
						"bio", "location", null);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (MustardException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AuthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;
	}

	public class RegisterUpdate extends AsyncTask<String, Integer, Integer> {

		@Override
		protected Integer doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			int result = -1;
			try {
				result = register();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			try {
				switch (result) {
				case 1:
					Toast.makeText(getApplicationContext(),
							getString(R.string.register_succeed),
							Toast.LENGTH_SHORT).show();
					finish();
					break;
				case 2:
					Toast.makeText(getApplicationContext(),
							getString(R.string.register_email_illegal),
							Toast.LENGTH_SHORT).show();
					break;
				case 3:
					Toast.makeText(getApplicationContext(),
							getString(R.string.register_nickname_illegal),
							Toast.LENGTH_SHORT).show();
					break;
				case 4:
					Toast.makeText(getApplicationContext(),
							getString(R.string.nickname_exists),
							Toast.LENGTH_SHORT).show();
					break;
				case 5:
					Toast.makeText(getApplicationContext(),
							getString(R.string.email_exists),
							Toast.LENGTH_SHORT).show();
					break;
				case 6:
					Toast.makeText(getApplicationContext(),
							getString(R.string.register_email_name_invalid),
							Toast.LENGTH_SHORT).show();
					break;
				case -1:
					Toast.makeText(getApplicationContext(),
							getString(R.string.register_not_find),
							Toast.LENGTH_SHORT).show();
					break;
				default:
					Toast.makeText(getApplicationContext(),
							getString(R.string.register_fail),
							Toast.LENGTH_SHORT).show();
					break;
				}
			} catch (IllegalArgumentException e) {
			} finally {
				if(mDialog.isShowing()){
					mDialog.dismiss();
				}
				registerUpdateButton.setEnabled(true);
			}
			super.onPostExecute(result);
		}

	}

	public boolean isAllValid(String addressList) {
		if (addressList != null && addressList.length() > 0) {
			android.text.util.Rfc822Token[] tokens = Rfc822Tokenizer
					.tokenize(addressList);
			for (int i = 0, length = tokens.length; i < length; ++i) {
				android.text.util.Rfc822Token token = tokens[i];
				String address = token.getAddress();
				if (!TextUtils.isEmpty(address) && !isValidAddress(address)) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean isValidAddress(String address) {
		// Note: Some email provider may violate the standard, so here we only
		// check that
		// address consists of two part that are separated by '@', and domain
		// part contains
		// at least one '.'.
		int len = address.length();
		int firstAt = address.indexOf('@');
		int lastAt = address.lastIndexOf('@');
		int firstDot = address.indexOf('.', lastAt + 1);
		int lastDot = address.lastIndexOf('.');
		return firstAt > 0 && firstAt == lastAt && lastAt + 1 < firstDot
				&& firstDot <= lastDot && lastDot < len - 1;
	}

}
