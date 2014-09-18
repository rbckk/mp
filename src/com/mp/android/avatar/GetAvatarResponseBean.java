package com.mp.android.avatar;

import java.util.ArrayList;

import com.mp.android.ResponseBean;

public class GetAvatarResponseBean extends ResponseBean{
	public ArrayList<String> url = new ArrayList<String>();
	public String avatar_url = null;
	public String avatar_url_large = null;

	public GetAvatarResponseBean(ArrayList<String> response) {
		super(response);
		url = response;
		if(null != url) {
			if(url.size() != 0) {
				if(url.get(0) != null) {
				avatar_url = url.get(0);
				}
				if(url.get(1) != null) {
					avatar_url_large = url.get(1);
				}
			}
		}
	}
}
