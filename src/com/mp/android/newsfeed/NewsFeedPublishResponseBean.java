package com.mp.android.newsfeed;

import org.json.JSONException;
import org.json.JSONObject;

import com.mp.android.ResponseBean;

public class NewsFeedPublishResponseBean extends ResponseBean {

	public int code;

	public NewsFeedPublishResponseBean(String response) {
		super(response);
		Resolve(response);
	}

	private void Resolve(String response) {
		try {
			JSONObject object = new JSONObject(response);
			if (object.has("result")) {
				code = object.getInt("result");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
