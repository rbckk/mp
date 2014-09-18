package com.mp.android;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.mp.android.avatar.AvatarHelper;
import com.mp.android.avatar.GetAvatarRequestParam;
import com.mp.android.avatar.GetAvatarResponseBean;

import com.mp.android.newsfeed.NewsFeedHelper;
import com.mp.android.newsfeed.NewsFeedPublishResponseBean;
import com.mp.android.newsfeed.NoticeRequestParam;
import com.mp.android.newsfeed.NoticeResponseBean;

//import com.mp.android.user.GetInfoResponseBean;

//import com.mp.android.user.GetStatusResponseBean;

public class AsyncRenRen {
	private Executor mPool;
	//private EmoticonsHelper mEmoticonsHelper;
	private NewsFeedHelper mNewsFeedHelper;
	//private UserHelper mUserHelper;

	//private FriendsHelper mFriendsHelper;

	private AvatarHelper mAvatarHelper;
	//private GroupsHelper mGroupsHelper;

	public AsyncRenRen() {
		mPool = Executors.newFixedThreadPool(5);
		//mEmoticonsHelper = new EmoticonsHelper();
		mNewsFeedHelper = new NewsFeedHelper();
		//mUserHelper = new UserHelper();
	
		//mFriendsHelper = new FriendsHelper();
	
		mAvatarHelper = new AvatarHelper();
		//mGroupsHelper = new GroupsHelper();
		
	}
/*
	public void getEmoticons(EmoticonsRequestParam param,
			RequestListener<EmoticonsResponseBean> listener) {
		mEmoticonsHelper.asyncGet(mPool, param, listener);
	}

	public void downloadEmoticons(List<EmoticonsResult> resultList) {
		mEmoticonsHelper.downloadEmotcons(resultList);
	}
*/
//	public void getNewsFeed(NewsFeedRequestParam param,
//			RequestListener<NewsFeedResponseBean> listener) {
//		mNewsFeedHelper.asyncGet(mPool, param, listener);
//	}
	

	public void getNotices(RequestListener<NoticeResponseBean> listener,NoticeRequestParam param){
	    mNewsFeedHelper.asyncGetNotice(param, mPool, listener);
	}
//	public void publishNewsFeed(NewsFeedPublishRequestParam param,
//			RequestListener<NewsFeedPublishResponseBean> listener) {
//		mNewsFeedHelper.asyncPublish(mPool, param, listener);
//	}

//	public void getInfo(GetInfoRequestParam param,
//			RequestListener<GetInfoResponseBean> listener) {
//		mUserHelper.asyncGetInfo(mPool, param, listener);
//	}


//	public void getStatus(GetStatusRequestParam param,
//			RequestListener<GetStatusResponseBean> listener) {
//		mUserHelper.asyncGetStatus(mPool, param, listener);
//	}

/*
	public void getFriends(GetFriendsRequestParam param,
			RequestListener<GetFriendsResponseBean> listener) {
		mFriendsHelper.asyncGetFriends(mPool, param, listener);
	}
	// zb add for user's friendlist 2013-12-22
	public void getUserFriends(GetFriendsRequestParam param,
			RequestListener<GetFriendsResponseBean> listener) {
		mFriendsHelper.asyncGetUserFriends(mPool, param, listener);
	}

	public void getGroups(GetGroupsRequestParam param,
			RequestListener<GetGroupsResponseBean> listener) {
		mGroupsHelper.asyncGetGroups(mPool, param, listener);
	}
	public void getGroupMembers(GetGroupMembersRequestParam param,
			RequestListener<GetGroupMembersResponseBean> listener) {
		mGroupsHelper.asyncGetGroups(mPool, param, listener);
	}
	
	// add end

	public void findFriends(FriendsFindRequestParam param,
			RequestListener<FriendsFindResponseBean> listener) {
		mFriendsHelper.asyncFindFriends(mPool, param, listener);
	}
*/
	
	/*
	 * add by fancuiru at 2013-12-26 for chang avatar
	 */
	public void getAvatar(GetAvatarRequestParam param ,
			RequestListener<GetAvatarResponseBean> listener) {
		mAvatarHelper.asyncGetAvatar(mPool, param, listener);
	}
	//add end 
}
