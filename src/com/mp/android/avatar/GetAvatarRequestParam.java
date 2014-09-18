package com.mp.android.avatar;

import java.io.File;

import com.mp.android.statusnet.provider.StatusNet;

public class GetAvatarRequestParam {
	public StatusNet mStatusNet;
	public File file;
	
	public GetAvatarRequestParam (StatusNet mStatusNet,File file) {
		this.mStatusNet = mStatusNet;
		this.file = file;
	}
}
