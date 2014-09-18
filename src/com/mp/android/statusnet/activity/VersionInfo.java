package com.mp.android.statusnet.activity;

import com.mp.android.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class VersionInfo extends Activity{
	
	private TextView appVersion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.version_info);
		appVersion = (TextView)findViewById(R.id.app_version);
		appVersion.setText(getString(R.string.version_stationary_text)+"V1.4_0410");
	}	

}
