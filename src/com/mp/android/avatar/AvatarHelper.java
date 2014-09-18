package com.mp.android.avatar;

import java.util.ArrayList;
import java.util.concurrent.Executor;

import android.util.Log;

import com.mp.android.RequestListener;
import com.mp.android.statusnet.util.MustardException;


public class AvatarHelper {
	
	public void asyncGetAvatar(Executor pool,
			final GetAvatarRequestParam param,
			final RequestListener<GetAvatarResponseBean> listener) {
		pool.execute(new Runnable() {

			public void run() {
				listener.onStart();
					GetAvatarResponseBean bean  = getAvatarUrl(param);
					listener.onComplete(bean);
			}
		});
	}
	
	public GetAvatarResponseBean getAvatarUrl  (GetAvatarRequestParam param) {
		ArrayList<String> url = null;
			try {
				url = param.mStatusNet.updateAvatar(param.file);
				return new GetAvatarResponseBean(url);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.v("fancuiru","eeee==>" +e.toString());
				e.printStackTrace();
			}
			return null;

	}

}
