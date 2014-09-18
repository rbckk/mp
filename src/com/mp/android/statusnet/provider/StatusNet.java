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

package com.mp.android.statusnet.provider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import android.R.array;
import android.R.integer;
import android.R.string;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.mp.android.BaseApplication;
import com.mp.android.RequestListener;
import com.mp.android.avatar.GetAvatarResponseBean;
//import com.mp.android.meeting.MeetingItemInfo;
//import com.mp.android.meeting.MeetingPeopleInfo;
//import com.mp.android.poll.PollDetails;
//import com.mp.android.poll.PollOption;
//import com.mp.android.question.AnswerInfo;
//import com.mp.android.question.QuestionItemInfo;
import com.mp.android.statusnet.Account;
import com.mp.android.statusnet.MustardDbAdapter;
import com.mp.android.statusnet.Preferences;
import com.mp.android.statusnet.rsd.Service;
import com.mp.android.statusnet.statusnet.RowStatus;
import com.mp.android.statusnet.statusnet.Status;
import com.mp.android.statusnet.statusnet.StatusNetService;
import com.mp.android.statusnet.statusnet.User;
import com.mp.android.statusnet.util.AuthException;
import com.mp.android.statusnet.util.HttpManager;
//import com.mp.android.statusnet.util.ImageUtil;
import com.mp.android.statusnet.util.MustardException;
import com.mp.android.util.log;

public class StatusNet {
	
	protected static final String TAG="StatusNet";

	// RSD
	private final String RSD = "/rsd.xml";
	
	// STATUSNET API
	private final String API_STATUSNET_VERSION = "/statusnet/version.json";
	private final String API_STATUSNET_CONFIG = "/statusnet/config.json";
	private final String API_HELP_TEST = "/help/test.json";

	// GLOBAL API
	private final String API_PUBLIC_TIMELINE = "/statuses/public_timeline.json";
	private final String API_PUBLIC_TIMELINE_TWITTER = "statuses/sample.json";
	
	private final String API_STATUS_SHOW = "/statuses/show/%s.json";
	
	// USER API
	private final String API_USER_CHECK =  "/account/verify_credentials.json";
	private final String API_USER_SHOW = "/users/show.json?screen_name=%s";
	private final String API_USER_SHOW_BY_ID = "/users/show.json?id=%s";
	private final String API_USER_TIMELINE = "/statuses/user_timeline.json?screen_name=%s";
	private final String API_USER_MENTIONS = "/statuses/mentions.json?screen_name=%s";
	private final String API_USER_MENTIONS_TWITTER = "/statuses/mentions_timeline.json?screen_name=%s";

	private final String API_USER_FAVORITES = "/favorites.json?screen_name=%s";
	private final String API_USER_FAVORITES_TWITTER = "/favorites/list.json?screen_name=%s";
	
	private final String API_USER_FRIENDS_TIMELINE = "/statuses/news_timeline.json?screen_name=%s";  //friends->news
	private final String API_USER_FRIENDS_TIMELINE_TWITTER = "/statuses/home_timeline.json?";
	private final String API_USER_SUBSCRIBE = "/friendships/create.json?screen_name=%s";
	private final String API_USER_UNSUBSCRIBE = "/friendships/destroy.json?screen_name=%s";
	
	private final String API_FRIENDSHIP_SHOW = "/api/friendships/show.json?target_screen_name=%s";
	private final String API_FRIENDSHIP_SHOW_TWITTER = "/friendships/show.json?target_screen_name=%t&source_screen_name=%s";
		
	private final String API_USER_BLOCK = "/blocks/create/%s.json";
	private final String API_USER_UNBLOCK = "/blocks/destroy/%s.json";
	private final String API_USER_AVATAR = "/account/update_profile_image.json";
	
	// DIRECT MESSAGES
	private final String API_DM_IN = "/direct_messages.json";
	private final String API_DM_OUT = "/direct_messages/sent.json";
	private final String API_DM_ADD = "/direct_messages/new.json";
	
	// NOTICE API
	private final String API_NOTICE_ADD = "/statuses/update.json";
	private final String API_NOTICE_DELETE = "/statuses/destroy/%s.json";
	private final String API_NOTICE_FAVOR = "/favorites/create/%s.json";
	private final String API_NOTICE_DISFAVOR = "/favorites/destroy/%s.json";
	private final String API_NOTICE_REPEAT = "/statuses/retweet/%s.json";

	// GROUP API
	private final String API_GROUP_SHOW = "/statusnet/groups/show/%s.json";
	private final String API_GROUP_TIMELINE = "/statusnet/groups/timeline/%s.json";
	private final String API_GROUP_JOIN = "/statusnet/groups/join/%s.json";
	private final String API_GROUP_LEAVE = "/statusnet/groups/leave/%s.json";
	private final String API_GROUP_IS_MEMBER = "/statusnet/groups/is_member.json?group_name=%s";
	
	//zb add 2013-12-10
	private final String API_GROUP_LIST = "/statusnet/groups/list.json";
	private final String API_FRIENDS_FRIENDS = "/statuses/friends.json";
	private final String API_FRIENDS_FOLLOWBY = "/statuses/followers.json";
	//private final String API_FRIENDS_FOLLOWING = "/statuses/attention.json";
	private final String API_GROUP_MEMBERLIST = "/statusnet/groups/membership/%s.json";
	private final String API_USERFRIENDS_FOLLOWING = "/statuses/attention/%s.json";
	private final String API_USERFRIENDS_FOLLOWBY = "/statuses/followers/%s.json";
	private final String API_USERFRIENDS_FRIENDS = "/statuses/friends/%s.json";
	//add end
	
	// TAG API
	private final String API_TAG_TIMELINE = "/statusnet/tags/timeline/%s.json";

	// SEARCH API
	private final String API_SEARCH = "/search.json";
	private final String API_SEARCH_TWITTER = "/search/tweets.json";
	
	// MESSAGE HAS READ隆隆API add  by fancuiru at 2014-01-07
	private final String API_MESSAGE_READ = "/statuses/mentions/read.json";
	//MEETING
	private final String API_MEETING="/statusnet/meeting/create.json";
	private final String API_MEETING_JOIN="/statusnet/meeting/join.json";
	private final String API_MEETING_CANCLE="/statusnet/meeting/cancel.json";
	private final String API_MEETING_TIMELINE="/statusnet/meeting_timeline.json";
	private final String API_MEETING_PARTICIPATOR = "/statusnet/meeting/participator_info.json";
	
	//QUESTION
	private final String API_QUESTION="/statusnet/question/create.json";
	private final String API_QUESTION_CLOSE="/statusnet/question/close.json";
	private final String API_QUESTION_ANSWER="/statusnet/question/answer.json";
	private final String API_QUESTION_CHOOSE_BEST_ANSWER="/statusnet/question/choose.json";
	private final String API_QUESTION_TIMELINE="/statusnet/question_timeline.json";
	private final String API_QUESTION_ANSWER_INFO="/statusnet/question/answer_info.json";
	
	//POLL
	private final String API_POLL_TIMELINE="/status/poll_timeline.json";
	private final String API_POLL="/status/poll_create.json";
	private final String API_POLL_UPDATE="/status/poll_update.json";
	private final String API_POLL_RESPONSE="/status/poll_response_info.json";
	
	private final String API_NOTICE_MENTIONS = "/statuses/reply_timeline.json?notice_id=%s";
	//   /statuses/reply_timeline.json?notice_id=  comments for notice
	//REGISTER
	private final String API_REGISTER="/statuses/regeister.json";

	private HttpManager mHttpManager;
	private URL mURL;
	private String mUsername;
	private String mPassword;
	
	private long mUserId;
	private long mUsernameId;
	private int mMaxNotices = Preferences.FETCH_MAX_ITEMS;
	private Context mContext;
	private static DateFormat dfs =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH);
	
	private Account mAccount;
	
	public Account getAccount() {
		return mAccount;
	}

	public void setAccount(Account account) {
		this.mAccount = account;
	}

	private boolean isTwitter = false;
	
	public StatusNet(Context context) {
		mContext=context;
	}
	
	public void setMaxNotices(int maxNotices) {
		//mMaxNotices=maxNotices;
		mMaxNotices=2;
	}
	
	public void setURL(URL url) {
		mURL = url;
		String host = url.getHost();
		Log.v("mustard", "host---"+host);
		if(host.equalsIgnoreCase("twitter.com")) {
			host="api.twitter.com";
			try {
				mURL = new URL("https://"+host+"/1.1");
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			isTwitter=true;
		}
		mHttpManager = new HttpManager(mContext,host);
	}

	public URL getURL() {
		return mURL;
	}

	public void setCredentials(String username, String password) throws MustardException {
		if (mHttpManager == null) {
			throw new MustardException("You must call setURL prior"); 
		}
		mHttpManager.setCredentials(username, password);
		this.mUsername=username;
		this.mPassword = password;
		return ;
	}

	public void setCredentials(CommonsHttpOAuthConsumer consumer,String username) throws MustardException {
		if (mHttpManager == null) {
			throw new MustardException("You must call setURL prior"); 
		}
		mHttpManager.setOAuthConsumer(consumer);
		this.mUsername=username;
	}

	public void setUserId(long user_id) {
		this.mUserId=user_id;
	}
	
	public long getUserId() {
		return mUserId;
	}
	
	public void setUsernameId(long username_id) {
		this.mUsernameId=username_id;
	}
	
	public long getUsernameId() {
		return mUsernameId;
	} 
	public String getMUsername() {
		return mUsername;
	}

	public User getUser(String username) throws MustardException {
		if(username==null || "".equals(username))
			throw new MustardException("Username is null");
		User user = null;
		JSONObject json = null;
		try {
			String userURL = mURL.toExternalForm()
					+ (!isTwitter ? "/api" : "") + API_USER_SHOW.replace("%s", username.toLowerCase());
			json = mHttpManager.getJsonObject(userURL, HttpManager.GET);
		} catch (MustardException e) {
			throw e;
		} catch (Exception e) {
			//if (MustardApplication.DEBUG) e.printStackTrace();
			throw new MustardException(e.getMessage() == null ? e.toString() : e.getMessage());
		}
		if (json != null) {
			try {
				user = StatusNetJSONUtil.getUser(json);
			} catch (JSONException e) {
				//if (MustardApplication.DEBUG) e.printStackTrace();
				throw new MustardException(e.getMessage() == null ? e.toString() : e.getMessage());
			}
		}
		return user;
	}
	public User getUserById(String userId) throws MustardException {
		if(userId==null || "".equals(userId))
			throw new MustardException("Username is null");
		User user = null;
		JSONObject json = null;
		try {
			String userURL = mURL.toExternalForm()
					+ (!isTwitter ? "/api" : "") + API_USER_SHOW_BY_ID.replace("%s", userId.toLowerCase());
			json = mHttpManager.getJsonObject(userURL, HttpManager.GET);
		} catch (MustardException e) {
			throw e;
		} catch (Exception e) {
			//if (MustardApplication.DEBUG) e.printStackTrace();
			throw new MustardException(e.getMessage() == null ? e.toString() : e.getMessage());
		}
		if (json != null) {
			try {
				user = StatusNetJSONUtil.getUser(json);
			} catch (JSONException e) {
				//if (MustardApplication.DEBUG) e.printStackTrace();
				throw new MustardException(e.getMessage() == null ? e.toString() : e.getMessage());
			}
		}
		return user;
	}
	
	private String getTagValueWithMultiItem(Element eElement){
		String returnVal = "" ;
		Node eNode ;
		int NumOFItem = eElement.getElementsByTagName("link").getLength();
		for (int y = 0; y < NumOFItem; y++) {
			eNode = eElement.getElementsByTagName("link").item(y);
			NamedNodeMap attributes = eNode.getAttributes();
			for (int g = 0; g < attributes.getLength(); g++) {
				Attr attribute = (Attr)attributes.item(g);
				if(attribute.getNodeName().equals("rel")&&attribute.getNodeValue().equals("alternate"))
				{
					try {
						if(eNode.getAttributes().getNamedItem("type").getNodeValue().equals("application/atom+xml"))
							returnVal =eNode.getAttributes().getNamedItem("href").getNodeValue();
					}  
					catch (Exception e) {
						//if(MustardApplication.DEBUG)
//							e.printStackTrace();
					}
				} 
			}
		}
		return returnVal;    
	}
	
	
	public User getRemoteUser(String profileUrl) throws MustardException {
		if(profileUrl==null || "".equals(profileUrl))
			throw new MustardException("Username is null");
		User user = null;
		JSONObject json = null;
		String userURL = "";
		try {
			// I need a new HttpManager.. I don't have to send Auth
			URL _url = new URL(profileUrl);
			String host = _url.getHost();
			HttpManager _hm = new HttpManager(mContext,host);
			Document d = _hm.getDocument(profileUrl, HttpManager.GET, null);
			userURL = getTagValueWithMultiItem(d.getDocumentElement());
			Log.d("Mustard","Found: " + userURL);
			if(!userURL.equals("")) {
				String baseURL=userURL.substring(0, userURL.indexOf("statuses/user_timeline"));
				baseURL +="users/show.json?id=";
				String userid = userURL.substring(userURL.lastIndexOf("/")+1, userURL.lastIndexOf(".atom"));
				baseURL +=userid;
				Log.d("Mustard","Proviamo con " + baseURL);
				json = _hm.getJsonObject(baseURL, HttpManager.GET);
			}
		} catch (MustardException e) {
			throw e;
		} catch (Exception e) {
//			if (MustardApplication.DEBUG) 
//				e.printStackTrace();
			throw new MustardException(e.getMessage() == null ? e.toString() : e.getMessage());
		}
		if (json != null) {
			try {
				user = StatusNetJSONUtil.getUser(json);
				user.setProfile_json_url(userURL);
			} catch (JSONException e) {
//				if (MustardApplication.DEBUG) e.printStackTrace();
				throw new MustardException(e.getMessage() == null ? e.toString() : e.getMessage());
			}
		}
		return user;
	}
/*
	public Group getGroup(String groupname) throws MustardException {
		Group group = null;
		JSONObject json = null;
		try {
			String userURL = mURL.toExternalForm()
					+ (!isTwitter ? "/api" : "") + API_GROUP_SHOW.replace("%s", groupname.toLowerCase());
			json = mHttpManager.getJsonObject(userURL, HttpManager.GET);
		} catch (MustardException e) {
			throw e;
		} catch (Exception e) {
			//if (MustardApplication.DEBUG) e.printStackTrace();
			throw new MustardException(e.getMessage() == null ? e.toString() : e.getMessage());
		}
		if (json != null) {
			try {
				group = StatusNetJSONUtil.getGroup(json);
			} catch (JSONException e) {
				throw new MustardException(e.toString());
			}
		}
		return group;
	}
	
	//zb add 2013-12-11
	public ArrayList<Group> getGroupList() throws MustardException {
		Group group = null;
		ArrayList<Group> groups = new ArrayList<Group>();
		JSONArray json = null;
		try {
			String userURL = mURL.toExternalForm()
					+ (!isTwitter ? "/api" : "") + API_GROUP_LIST;
			json = mHttpManager.getJsonArray(userURL, HttpManager.GET);
			//JSONArray ja = jsonObjSplit.getJSONArray("data");
		} catch (MustardException e) {
			throw e;
		} catch (Exception e) {
			//if (MustardApplication.DEBUG) e.printStackTrace();
			throw new MustardException(e.getMessage() == null ? e.toString() : e.getMessage());
		}
		if (json != null) {
			for(int i=0; i<json.length();i++)
			{
				JSONObject o;
				try {
					o = json.getJSONObject(i);
					groups.add(StatusNetJSONUtil.getGroup(o));
					//group = StatusNetJSONUtil.getGroup(json);
				} catch (JSONException e) {
					throw new MustardException(e.toString());
				}
			}
			
		}
		return groups;
	}
	public ArrayList<User> getGroupMemberList(String groupname) throws MustardException {
		//Group group = null;
		ArrayList<User> users = new ArrayList<User>();
		JSONArray json = null;
		try {
			String userURL = mURL.toExternalForm()
					+ (!isTwitter ? "/api" : "") + API_GROUP_MEMBERLIST.replace("%s", groupname.toLowerCase());
			json = mHttpManager.getJsonArray(userURL, HttpManager.GET);
			//JSONArray ja = jsonObjSplit.getJSONArray("data");
		} catch (MustardException e) {
			throw e;
		} catch (Exception e) {
			//if (MustardApplication.DEBUG) e.printStackTrace();
			throw new MustardException(e.getMessage() == null ? e.toString() : e.getMessage());
		}
		if (json != null) {
			for(int i=0; i<json.length();i++)
			{
				JSONObject o;
				try {
					o = json.getJSONObject(i);
					users.add(StatusNetJSONUtil.getUser(o));
					//group = StatusNetJSONUtil.getGroup(json);
				} catch (JSONException e) {
					throw new MustardException(e.toString());
				}
			}
			
		}
		return users;
	}
	public ArrayList<User> getUserFriendFollowingList(String username) throws MustardException {
		//Group group = null;
		ArrayList<User> users = new ArrayList<User>();
		JSONArray json = null;
		try {
			String userURL = mURL.toExternalForm()
					+ (!isTwitter ? "/api" : "") + API_USERFRIENDS_FOLLOWING.replace("%s", username.toLowerCase());
			json = mHttpManager.getJsonArray(userURL, HttpManager.GET);
			//JSONArray ja = jsonObjSplit.getJSONArray("data");
		} catch (MustardException e) {
			throw e;
		} catch (Exception e) {
			//if (MustardApplication.DEBUG) e.printStackTrace();
			throw new MustardException(e.getMessage() == null ? e.toString() : e.getMessage());
		}
		if (json != null) {
			for(int i=0; i<json.length();i++)
			{
				JSONObject o;
				try {
					o = json.getJSONObject(i);
					users.add(StatusNetJSONUtil.getUser(o));
					//group = StatusNetJSONUtil.getGroup(json);
				} catch (JSONException e) {
					throw new MustardException(e.toString());
				}
			}
			
		}
		return users;
	}
	public ArrayList<User> getUserFriendFollowbyList(String name) throws MustardException {
		//Group group = null;
		ArrayList<User> users = new ArrayList<User>();
		JSONArray json = null;
		try {
			String userURL = mURL.toExternalForm()
					+ (!isTwitter ? "/api" : "") + API_USERFRIENDS_FOLLOWBY.replace("%s", name.toLowerCase());
			json = mHttpManager.getJsonArray(userURL, HttpManager.GET);
			//JSONArray ja = jsonObjSplit.getJSONArray("data");
		} catch (MustardException e) {
			throw e;
		} catch (Exception e) {
			//if (MustardApplication.DEBUG) e.printStackTrace();
			throw new MustardException(e.getMessage() == null ? e.toString() : e.getMessage());
		}
		if (json != null) {
			for(int i=0; i<json.length();i++)
			{
				JSONObject o;
				try {
					o = json.getJSONObject(i);
					users.add(StatusNetJSONUtil.getUser(o));
					//group = StatusNetJSONUtil.getGroup(json);
				} catch (JSONException e) {
					throw new MustardException(e.toString());
				}
			}
			
		}
		return users;
	}
	public ArrayList<User> getUserFriendFriendsList(String name) throws MustardException {
		//Group group = null;
		ArrayList<User> users = new ArrayList<User>();
		JSONArray json = null;
		try {
			String userURL = mURL.toExternalForm()
					+ (!isTwitter ? "/api" : "") + API_USERFRIENDS_FRIENDS.replace("%s", name.toLowerCase());
			json = mHttpManager.getJsonArray(userURL, HttpManager.GET);
			//JSONArray ja = jsonObjSplit.getJSONArray("data");
		} catch (MustardException e) {
			throw e;
		} catch (Exception e) {
			//if (MustardApplication.DEBUG) e.printStackTrace();
			throw new MustardException(e.getMessage() == null ? e.toString() : e.getMessage());
		}
		if (json != null) {
			for(int i=0; i<json.length();i++)
			{
				JSONObject o;
				try {
					o = json.getJSONObject(i);
					users.add(StatusNetJSONUtil.getUser(o));
					//group = StatusNetJSONUtil.getGroup(json);
				} catch (JSONException e) {
					throw new MustardException(e.toString());
				}
			}
			
		}
		return users;
	}
	public ArrayList<User> getFriendFriendsList() throws MustardException {
		//Group group = null;
		ArrayList<User> users = new ArrayList<User>();
		JSONArray json = null;
		try {
			String userURL = mURL.toExternalForm()
					+ (!isTwitter ? "/api" : "") + API_FRIENDS_FRIENDS;
			json = mHttpManager.getJsonArray(userURL, HttpManager.GET);
			//JSONArray ja = jsonObjSplit.getJSONArray("data");
		} catch (MustardException e) {
			throw e;
		} catch (Exception e) {
			//if (MustardApplication.DEBUG) e.printStackTrace();
			throw new MustardException(e.getMessage() == null ? e.toString() : e.getMessage());
		}
		if (json != null) {
			for(int i=0; i<json.length();i++)
			{
				JSONObject o;
				try {
					o = json.getJSONObject(i);
					users.add(StatusNetJSONUtil.getUser(o));
					//group = StatusNetJSONUtil.getGroup(json);
				} catch (JSONException e) {
					throw new MustardException(e.toString());
				}
			}
			
		}
		return users;
	}
	public ArrayList<User> getFriendFollowingList() throws MustardException {
		//Group group = null;
		ArrayList<User> users = new ArrayList<User>();
		JSONArray json = null;
		try {
			
			//String userURL = mURL.toExternalForm()
			//		+ (!isTwitter ? "/api" : "") + API_FRIENDS_FOLLOWING;
			String userURL = mURL.toExternalForm()
					+ (!isTwitter ? "/api" : "") + API_USERFRIENDS_FOLLOWING.replace("%s", getAccount().getUsername().toLowerCase());
			json = mHttpManager.getJsonArray(userURL, HttpManager.GET);
			//JSONArray ja = jsonObjSplit.getJSONArray("data");
		} catch (MustardException e) {
			throw e;
		} catch (Exception e) {
			//if (MustardApplication.DEBUG) e.printStackTrace();
			throw new MustardException(e.getMessage() == null ? e.toString() : e.getMessage());
		}
		if (json != null) {
			for(int i=0; i<json.length();i++)
			{
				JSONObject o;
				try {
					o = json.getJSONObject(i);
					users.add(StatusNetJSONUtil.getUser(o));
					//group = StatusNetJSONUtil.getGroup(json);
				} catch (JSONException e) {
					throw new MustardException(e.toString());
				}
			}
			
		}
		return users;
	}
	public ArrayList<User> getFriendFollowbyList() throws MustardException {
		//Group group = null;
		ArrayList<User> users = new ArrayList<User>();
		JSONArray json = null;
		try {
			String userURL = mURL.toExternalForm()
					+ (!isTwitter ? "/api" : "") + API_FRIENDS_FOLLOWBY;
			json = mHttpManager.getJsonArray(userURL, HttpManager.GET);
			//JSONArray ja = jsonObjSplit.getJSONArray("data");
		} catch (MustardException e) {
			throw e;
		} catch (Exception e) {
			//if (MustardApplication.DEBUG) e.printStackTrace();
			throw new MustardException(e.getMessage() == null ? e.toString() : e.getMessage());
		}
		if (json != null) {
			for(int i=0; i<json.length();i++)
			{
				JSONObject o;
				try {
					o = json.getJSONObject(i);
					users.add(StatusNetJSONUtil.getUser(o));
					//group = StatusNetJSONUtil.getGroup(json);
				} catch (JSONException e) {
					throw new MustardException(e.toString());
				}
			}
			
		}
		return users;
	}
	//add end
*/	
	public ArrayList<Status> getSearch(String str)  throws MustardException {
		String q="";
		try {
			q=URLEncoder.encode(str, HTTP.UTF_8);
		} catch (Exception e) {
			throw new MustardException(e.toString());
		}
		String lURL = "";
		if(isTwitter) {
			lURL = mURL.toExternalForm() + API_SEARCH_TWITTER + "?q="+q+ "&count="+ mMaxNotices + "&result_type=recent";
			
		} else {
			lURL = mURL.toExternalForm() + "/api" + API_SEARCH + "?q="+q+ "&count="+ mMaxNotices ;
		}
		ArrayList<Status> statuses = new ArrayList<Status>();
		try {
			JSONObject bo = mHttpManager.getJsonObject(lURL);
			//System.out.println(bo.toString());
			JSONArray aj = null;
			if(bo.has("results")) {
				aj = bo.getJSONArray("results");
				for (int i = 0; i < aj.length(); i++) {
					try {
						JSONObject o = aj.getJSONObject(i);
						statuses.add(StatusNetJSONUtil.getStatusFromSearch(o));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			if(bo.has("statuses")) {
				aj = bo.getJSONArray("statuses");
				for (int i = 0; i < aj.length(); i++) {
					try {
						JSONObject o = aj.getJSONObject(i);
						statuses.add(StatusNetJSONUtil.getStatus(o));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			

			//if (MustardApplication.DEBUG) Log.d(TAG,"Found " + aj.length() + " dents");
		} catch (IOException e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		} catch (MustardException e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		} catch (AuthException e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		}
		return statuses;
	}

	public ArrayList<Status> getRemote(int type, String extra,long sinceId,boolean higher) throws MustardException {
		switch (type) {
		case MustardDbAdapter.ROWTYPE_USER:
			return getRemoteUserTimeline(extra,sinceId,higher);

		}
		return null;
	}
	
	public ArrayList<Status> get(int type, String extra,long sinceId,boolean higher) throws MustardException {
		switch (type) {
		case MustardDbAdapter.ROWTYPE_FRIENDS:
			return getFriendsTimeline(extra,sinceId,higher);

		case MustardDbAdapter.ROWTYPE_PUBLIC:
			return getPublicTimeline(sinceId,higher);

		case MustardDbAdapter.ROWTYPE_MENTION:
			return getMentions(extra,sinceId,higher);
			
			
		//bin.zeng add 2014-1-21
			
		case MustardDbAdapter.ROWTYPE_NOTICEMENTIONS:
			return getNoticeMentions(extra,sinceId,higher);
			
		//add end

		case MustardDbAdapter.ROWTYPE_USER:
			return getUserTimeline(extra,sinceId,higher);
		
		case MustardDbAdapter.ROWTYPE_GROUP:
			return getGroupTimeline(extra,sinceId,higher);
			
		case MustardDbAdapter.ROWTYPE_TAG:
			return getTagTimeline(extra,sinceId,higher);
			
		case MustardDbAdapter.ROWTYPE_SINGLE:
			return getStatus(extra);
			
		case MustardDbAdapter.ROWTYPE_SEARCH:
			return getSearch(extra);
			
		case MustardDbAdapter.ROWTYPE_FAVORITES:
			return getFavorites(extra, sinceId,higher);
			
		case MustardDbAdapter.ROWTYPE_CONVERSATION:
			return getConversation(extra);
		
		case MustardDbAdapter.ROWTYPE_MEETING:
			return getMeetingTimeLine(sinceId, higher);
		
		case MustardDbAdapter.ROWTYPE_QUESTION:
			return getQuestionTimeLine(sinceId, higher);
		
		case MustardDbAdapter.ROWTYPE_POLL:
			return getPollTimeLine(sinceId, higher);
		}
		return null;
	}
	
	private String buildParams(long limit,boolean higher) {
		if(limit<=0)
			return  isTwitter ? "&include_rts=true" : "";
		String sideVersus = "";
		if(higher)
			sideVersus="since_id";
		else
			sideVersus="max_id";
		return "&"+sideVersus+"="+limit  + (isTwitter ? "&include_rts=true" : "");
	}
	
	public ArrayList<Status> getPublicTimeline(long sinceId, boolean since) throws MustardException {
		String sinceParam = buildParams(sinceId,since);
		String lURL = "";
		
		if(isTwitter) {
			lURL = mURL.toExternalForm() + API_PUBLIC_TIMELINE_TWITTER + "?count="
					+ mMaxNotices + sinceParam;
		} else {
			lURL = mURL.toExternalForm() + "/api"  + API_PUBLIC_TIMELINE + "?count="
				+ mMaxNotices + sinceParam;
		}
		return getGeneralStatuses(lURL);
	}

	public ArrayList<Status> getCurrentUserTimeline(long limit,boolean higher) throws MustardException {
		return getUserTimeline(mUsername,limit,higher);
	}
	
	public ArrayList<Status> getRemoteUserTimeline(String url,long limit,boolean higher) throws MustardException {
		String sinceParam = buildParams(limit, higher);
		String lURL = url.replace(".atom", ".json") + "?count=" + mMaxNotices + sinceParam;
		return getGeneralRemoteStatuses(lURL);
	}
	public ArrayList<Status> getUserTimeline(String username,long limit,boolean higher) throws MustardException {
		String sinceParam = buildParams(limit, higher);
		String lURL = mURL.toExternalForm()
				+ (!isTwitter ? "/api" : "") + API_USER_TIMELINE.replace("%s", username.toLowerCase())
				+ "&count=" + mMaxNotices + sinceParam;
		return getGeneralStatuses(lURL);
	}
	
	public ArrayList<Status> getGroupTimeline(String groupname,long limit,boolean higher) throws MustardException {
		String sinceParam = buildParams(limit, higher);
		String lURL = mURL.toExternalForm()
				+ (!isTwitter ? "/api" : "") + API_GROUP_TIMELINE.replace("%s", groupname.toLowerCase())
				+ "?count=" + mMaxNotices + sinceParam;
		return getGeneralStatuses(lURL);
	}

	public ArrayList<Status> getCurrentUserFriendsTimeline(long limit,boolean higher) throws MustardException {
		return getFriendsTimeline(mUsername,limit,higher);
	}
	
	public ArrayList<Status> getFriendsTimeline(String username,long limit,boolean higher) throws MustardException {
		String sinceParam = buildParams(limit, higher);
		String lURL = "";
		if(isTwitter) {
			lURL = mURL.toExternalForm() + API_USER_FRIENDS_TIMELINE_TWITTER + "count=" + mMaxNotices + sinceParam;
		} else {
			lURL = mURL.toExternalForm()
				+ "/api" + API_USER_FRIENDS_TIMELINE.replace("%s", username
						.toLowerCase()) + "&count=" + mMaxNotices + sinceParam;
		}
		return getGeneralStatuses(lURL);
	}
	
	public ArrayList<Status> getMentions(String username,long limit,boolean higher) throws MustardException {
		String sinceParam = buildParams(limit, higher);
		if(username.equals("-1"))
			username=getMUsername();
		String lURL = "";
		if(isTwitter) {
			lURL = mURL.toExternalForm() + API_USER_MENTIONS_TWITTER.replace("%s", username.toLowerCase())
					+ "&count=" + mMaxNotices + sinceParam;
		} else {
			lURL = mURL.toExternalForm() 
					+ "/api" +  API_USER_MENTIONS.replace("%s", username.toLowerCase())
				+ "&count=" + mMaxNotices + sinceParam;
		}
		return getGeneralStatuses(lURL);
	}
	public ArrayList<Status> getNoticeMentions(String notice_id,long limit,boolean higher) throws MustardException {
		String sinceParam = buildParams(limit, higher);
		String lURL = "";
		if(isTwitter) {
			lURL = mURL.toExternalForm() + API_USER_MENTIONS_TWITTER.replace("%s", notice_id)
					+ "&count=" + mMaxNotices + sinceParam;
		} else {
			lURL = mURL.toExternalForm() 
					+ "/api" +  API_NOTICE_MENTIONS.replace("%s", notice_id)
				+ "&count=" + mMaxNotices + sinceParam;
		}
		return getGeneralStatuses(lURL);
	}
/*	
	public ArrayList<DirectMessage> getDirectMessages(long limit,boolean higher) throws MustardException {
		String sinceParam = buildParams(limit, higher);
		String lURL = mURL.toExternalForm()
				+ (!isTwitter ? "/api" : "") + API_DM_IN + "?count=" + mMaxNotices + sinceParam;
		return getGeneralDirectMessages(DirectMessage.K_IN,lURL);
	}
	
	public ArrayList<DirectMessage> getDirectMessagesSent(long limit,boolean higher) throws MustardException {
		String sinceParam = buildParams(limit, higher);
		String lURL = mURL.toExternalForm()
				+ (!isTwitter ? "/api" : "") + API_DM_OUT + "?count=" + mMaxNotices + sinceParam;
		return getGeneralDirectMessages(DirectMessage.K_OUT,lURL);
	}
*/	
	public ArrayList<Status> getFavorites(String username,long limit,boolean higher) throws MustardException {
		String sinceParam = buildParams(limit, higher);
		String lURL = "";
		if(isTwitter) {
			lURL = mURL.toExternalForm()
					+ API_USER_FAVORITES_TWITTER.replace("%s", username.toLowerCase())
					+ "&count=" + mMaxNotices + sinceParam;
		} else {
			/*
			lURL = mURL.toExternalForm()
				+ "/api" + API_USER_FAVORITES.replace("%s", username.toLowerCase())
				+ "&count=" + mMaxNotices + sinceParam;
				*/
			lURL = mURL.toExternalForm()
					+ "/api" + API_USER_FAVORITES.replace("%s", username.toLowerCase());
		}
		return getGeneralStatuses(lURL);
	}

	public ArrayList<Status> getTagTimeline(String tag,long limit,boolean higher) throws MustardException {
		String sinceParam = buildParams(limit, higher);
		String lURL = mURL.toExternalForm()
		+ (!isTwitter ? "/api" : "") + API_TAG_TIMELINE.replace("%s", tag.toLowerCase())
		+ "?count=" + mMaxNotices + sinceParam;
		return getGeneralStatuses(lURL);
	}

	public Status getStatus(String id,boolean single) throws MustardException {
		Status s = null;
		String lURL = mURL.toExternalForm()
		+ (!isTwitter ? "/api" : "") + API_STATUS_SHOW.replace("%s", id);
		try {
			JSONObject o = mHttpManager.getJsonObject(lURL);
			s = StatusNetJSONUtil.getStatus(o);
		} catch (Exception e) {
			//if (MustardApplication.DEBUG)
//				e.printStackTrace();
		}
		return s;
	}

	public ArrayList<Status> getConversation (String id) throws MustardException {
		ArrayList<Status> tmp = new ArrayList<Status>();
/*		
		Status s = getStatus(id,true);
		tmp.add(s);
		while(true) {
			try {
				long prev = s.getNotice().getIn_reply_to_status_id();
				if (prev > 0) {
					s = getStatus(Long.toString(prev),true);
					tmp.add(s);
				} else {
					break;
				}
			} catch (Exception e) {
				//if(MustardApplication.DEBUG)
//					e.printStackTrace();
				break;
			}
		}*/
		return tmp;
	}
	
	public ArrayList<Status> getStatus(String id) throws MustardException {
		ArrayList<Status> ret = new ArrayList<Status>(1);
		ret.add(getStatus(id,true));
		return ret;
	}

	private ArrayList<Status> getGeneralRemoteStatuses(String url) throws MustardException {
		//if(MustardApplication.DEBUG)
//			Log.i("Mustard",url);
		ArrayList<Status> statuses = new ArrayList<Status>();
		try {
			URL u = new URL(url);
			HttpManager _hm = new HttpManager(mContext,u.getHost());
			JSONArray aj = _hm.getJsonArray(url);
			for (int i = 0; i < aj.length(); i++) {
				try {
					JSONObject o = aj.getJSONObject(i);
					statuses.add(StatusNetJSONUtil.getStatus(o));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		} catch (MustardException e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		} catch (AuthException e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		}
		return statuses;
	}
	private ArrayList<Status> getGeneralStatuses(String url) throws MustardException {
//		if(MustardApplication.DEBUG)
//			Log.i("Mustard",url);
		ArrayList<Status> statuses = new ArrayList<Status>();
		try {
			JSONArray aj = mHttpManager.getJsonArray(url);
			for (int i = 0; i < aj.length(); i++) {
				try {
					JSONObject o = aj.getJSONObject(i);
					statuses.add(StatusNetJSONUtil.getStatus(o));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		} catch (MustardException e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		} catch (AuthException e) {
			e.printStackTrace();
			throw new MustardException(404, e.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		}
		return statuses;
	}
/*	
	private ArrayList<DirectMessage> getGeneralDirectMessages(int inOut, String url) throws MustardException {
//		if(MustardApplication.DEBUG)
//			Log.i("Mustard",url);
		ArrayList<DirectMessage> statuses = new ArrayList<DirectMessage>();
		try {
			JSONArray aj = mHttpManager.getJsonArray(url,HttpManager.GET,null,false);
			for (int i = 0; i < aj.length(); i++) {
				try {
					JSONObject o = aj.getJSONObject(i);
					statuses.add(StatusNetJSONUtil.getDirectMessage(inOut,o));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		} catch (MustardException e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		} catch (AuthException e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		}
		return statuses;
	}
	
*/	
	public User checkUser() throws MustardException {

		User user = null;
		JSONObject json = null;
		try {
			String userURL = mURL.toExternalForm() + (!isTwitter ? "/api" : "") + API_USER_CHECK;
			json = mHttpManager.getJsonObject(userURL, HttpManager.GET);
		} catch (MustardException e) {
			throw e;
		} catch (Exception e) {
//			if (MustardApplication.DEBUG) e.printStackTrace();
			throw new MustardException(e.getMessage() == null ? e.toString() : e.getMessage());
		}
		if (json != null) {
			try {
				user = StatusNetJSONUtil.getUser(json);
			} catch (JSONException e) {
//				if (MustardApplication.DEBUG) e.printStackTrace();
				throw new MustardException(e.getMessage() == null ? e.toString() : e.getMessage());
			}
		}
		System.out.println("user ------------>"+user);
		return user;
	}
	
	public boolean delete(String id) {
		
		String deleteURL = mURL.toExternalForm() + (!isTwitter ? "/api" : "") + API_NOTICE_DELETE.replace("%s", id);
		try {
			mHttpManager.getResponseAsString(deleteURL, HttpManager.POST);
		} catch (Exception e) {
//			if (MustardApplication.DEBUG) Log.e(TAG,e.toString());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	//add by fcr at 2014-01-06 for send the id of  the message which has read to server
	
	public boolean readMsg(String id) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair("id", id));
	    String msgReadURL = mURL.toExternalForm() + (!isTwitter ? "/api" : "") + API_MESSAGE_READ;
	    JSONObject json = null;
		try {
			json = mHttpManager.getJsonObject(msgReadURL, HttpManager.POST, params);
			Log.v("tag","ss = " + json.toString() + ",json.getString()==> " + json.getString("status") );
			if(json.getString("status").toString().equals("true")) {
				return true;
			}
		} catch (Exception e) {
			Log.v(TAG,"e111===>" +e.toString());
			e.printStackTrace();
		}
		return false;
	}
	//add  end 

	public boolean doFavour(String id) {	
		String favorURL = mURL.toExternalForm() + (!isTwitter ? "/api" : "") + API_NOTICE_FAVOR.replace("%s", id);
		try {
			mHttpManager.getResponseAsString(favorURL, HttpManager.POST);
		} catch (Exception e) {
//			if (MustardApplication.DEBUG) Log.e(TAG,e.toString());
			return false;
		}
		return true;
	}
		
	public boolean doDisfavour(String id) {	
		String disfavorURL = mURL.toExternalForm() + (!isTwitter ? "/api" : "") + API_NOTICE_DISFAVOR.replace("%s", id);
		try {
			mHttpManager.getResponseAsString(disfavorURL, HttpManager.POST);
			return true;
		} catch (Exception e) {
//			if (MustardApplication.DEBUG) Log.e(TAG,e.toString());
			return false;
		}
	}
	
	public boolean doRepeat(String id) {
		String favorURL = mURL.toExternalForm() + (!isTwitter ? "/api" : "") + API_NOTICE_REPEAT.replace("%s", id);
		try {
			mHttpManager.getResponseAsString(favorURL, HttpManager.POST);
		} catch (Exception e) {
//			if (MustardApplication.DEBUG) Log.e(TAG,e.toString());
			return false;
		}
		return true;
	}
	
	public boolean doSubscribe(String id)  throws MustardException {
		String subscribeURL = mURL.toExternalForm() + (!isTwitter ? "/api" : "") + API_USER_SUBSCRIBE.replace("%s", id);
		try {
//			if (MustardApplication.DEBUG)Log.v(TAG, subscribeURL);
			mHttpManager.getResponseAsString(subscribeURL, HttpManager.POST);
		} catch (MustardException e) {
			throw e;
		} catch (Exception e) { 
//			e.printStackTrace();
//			if (MustardApplication.DEBUG) Log.e(TAG,e.toString());
			throw new MustardException(e.getMessage() == null ? e.toString() : e.getMessage());
		}
		return true;
	}
	
	public boolean doUnsubscribe(String id) throws MustardException {	
		String unsubscribeURL = mURL.toExternalForm() + (!isTwitter ? "/api" : "") + API_USER_UNSUBSCRIBE.replace("%s", id);
		try {
//			if (MustardApplication.DEBUG) Log.v(TAG, unsubscribeURL);
			mHttpManager.getResponseAsString(unsubscribeURL, HttpManager.POST);
		} catch (MustardException e) {
			throw e;
		} catch (Exception e) { 
//			e.printStackTrace();
//			if (MustardApplication.DEBUG) Log.e(TAG,e.toString());
			throw new MustardException(e.getMessage() == null ? e.toString() : e.getMessage());
		}
		return true;
	}
	
	public boolean doBlock(String id) {	
		String subscribeURL = mURL.toExternalForm() + (!isTwitter ? "/api" : "") + API_USER_BLOCK.replace("%s", id);
		try {
			mHttpManager.getResponseAsString(subscribeURL, HttpManager.POST);
//			Log.d(TAG, "BLOCK: " + ret);
		} catch (Exception e) {
//			if (MustardApplication.DEBUG)
//				Log.e(TAG,e.toString());
			return false;
		}
		return true;
	}
	
	public boolean doUnblock(String id) {	
		String unsubscribeURL = mURL.toExternalForm() + (!isTwitter ? "/api" : "") + API_USER_UNBLOCK.replace("%s", id);
		try {
			mHttpManager.getResponseAsString(unsubscribeURL, HttpManager.POST);
//			Log.d(TAG, "UNBLOCK: " + ret);
		} catch (Exception e) {
//			if (MustardApplication.DEBUG)
//				Log.e(TAG,e.toString());
			return false;
		}
		return true;
	}
	
	public boolean doJoinGroup(String id) {	
		String joinGroupURL = mURL.toExternalForm() + (!isTwitter ? "/api" : "") + API_GROUP_JOIN.replace("%s", id);
		try {
			mHttpManager.getResponseAsString(joinGroupURL, HttpManager.POST);
		} catch (Exception e) {
//			if (MustardApplication.DEBUG) Log.e(TAG,e.toString());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean doLeaveGroup(String id) {	
		String leaveGroupURL = mURL.toExternalForm() + (!isTwitter ? "/api" : "") + API_GROUP_LEAVE.replace("%s", id);
		try {
			mHttpManager.getResponseAsString(leaveGroupURL, HttpManager.POST);
		} catch (Exception e) {
//			if (MustardApplication.DEBUG) Log.e(TAG,e.toString());
			return false;
		}
		return true;
	}
/*	
	public Relationship getFriendshipStatus(String user) {
		Relationship r = null;
		String friendshipExistsURL = mURL.toExternalForm() +
			API_FRIENDSHIP_SHOW.replace("%s", user);
		if(isTwitter)
			friendshipExistsURL = mURL.toExternalForm() +
			API_FRIENDSHIP_SHOW_TWITTER.replace("%t", user).replace("%s", mUsername);
		try {
			JSONObject friendship = mHttpManager.getJsonObject(friendshipExistsURL, HttpManager.GET);
			r = StatusNetJSONUtil.getRelationship(friendship);
		} catch (Exception e) {
//			if (MustardApplication.DEBUG) Log.e(TAG,e.toString());
			e.printStackTrace();
		}
		return r;
	}

*/
	public boolean isGroupMember(String group) {
		String subscribeURL = mURL.toExternalForm() + (!isTwitter ? "/api" : "") + API_GROUP_IS_MEMBER.replace("%s", group);
		try {
			JSONObject isMember = mHttpManager.getJsonObject(subscribeURL, HttpManager.POST);
			return isMember.getBoolean("is_member");
		} catch (Exception e) {
			e.printStackTrace();
//			if (MustardApplication.DEBUG) Log.e(TAG,e.toString());
			return false;
		}
	}
	
	public String getVersion() {

		String version = null;
		try {
			String versionURL = mURL.toExternalForm() + (!isTwitter ? "/api" : "") + API_STATUSNET_VERSION;
			version = mHttpManager.getResponseAsString(versionURL, HttpManager.GET);
			if (version!=null) {
				version = version.trim();
				if(version.startsWith("\""))
					version=version.replaceAll("\"", "");
			}
		} catch (MustardException e) {
			e.printStackTrace();
		} catch (AuthException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return version;
		
	}
	
	public Service getRsd() {

		Service rsd = null;
		InputStream is = null;
		try {
			String rsdURL = mURL.toExternalForm() + RSD;
			is = mHttpManager.requestData(rsdURL, HttpManager.GET,null);
			
			rsd = StatusNetXMLUtil.getRsd(is);
		} catch (MustardException e) {
			e.printStackTrace();
		} catch (AuthException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					
				}
			}
		}
		return rsd;
		
	}
	
	public boolean isHelpTest() {

		boolean pass = false;
		try {
			String helptestURL = mURL.toExternalForm() + (!isTwitter ? "/api" : "") + API_HELP_TEST;
			String helpTest = mHttpManager.getResponseAsString(helptestURL, HttpManager.GET);
			if (helpTest!=null) {
				helpTest = helpTest.trim();
				if(helpTest.startsWith("\""))
					helpTest=helpTest.replaceAll("\"", "");
				if (helpTest.equalsIgnoreCase("ok"))
					pass = true;
			}
		} catch (MustardException e) {
			e.printStackTrace();
		} catch (AuthException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pass;
		
	}
	
	public long sendDirectMessage(String text, String screen_name) throws MustardException, AuthException {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair("text", text));
	    params.add(new BasicNameValuePair("screen_name", screen_name));
	    params.add(new BasicNameValuePair("source", BaseApplication.APPLICATION_NAME));
	    String updateURL = mURL.toExternalForm() + (!isTwitter ? "/api" : "") + API_DM_ADD;
	    JSONObject json = null;
	    long id=-1;
	    try {
	    	String res = mHttpManager.getResponseAsString(updateURL, HttpManager.POST, params);
	    	json = new JSONObject(res);
	    	id = json.getLong("id");
	    } catch (AuthException e) {
	    	throw e;
	    } catch (MustardException e) {
//	    	if(MustardApplication.DEBUG) 
//	    		e.printStackTrace();
	    	throw e;
	    } catch (Exception e) {
//	    	if(MustardApplication.DEBUG) 
//	    		e.printStackTrace();
	    	throw new MustardException(e.getMessage());
	    }
		return id;
	}

	public StatusNetService getConfiguration() throws MustardException {

		StatusNetService config = new StatusNetService();
		JSONObject o = null;
		try {
			String configURL = mURL.toExternalForm() + (!isTwitter ? "/api" : "") + API_STATUSNET_CONFIG;
			//add by xiaosong.xu for test
			//configURL = "http://192.168.1.130/api/StatusNet/config.json";			
			Log.v("mustard", "configURL---"+configURL);
			o = mHttpManager.getJsonObject(configURL);
			Log.v("mustard", "getConfiguration() 1");
			// System.out.println(o.toString(3));
			config = StatusNetJSONUtil.getService(o);
			Log.v("mustard", "getConfiguration() 2");
		} catch (MustardException e) {
			throw e;
		} catch (AuthException e) {
			// Impossible here :P
			throw new MustardException(e.getMessage());
		} catch (IOException e) {
			throw new MustardException(e.getMessage());
		} catch (JSONException e) {
			throw new MustardException(e.getMessage());
		}
		return config;
	}
	
	public long update(String status, String in_reply_to) throws MustardException, AuthException {
		return update(status, in_reply_to,null, null,null, null,null, null,null);
	}

	public long update(String status, String in_reply_to,String lon, String lat,String local_address) throws MustardException, AuthException {
		return update(status, in_reply_to,null, lon, lat,null, null,null,local_address);
	}
	
	public long update(String status, String in_reply_to,String repeat_of, String lon, String lat,String notice_to,String notice_private, File media,String local_address) throws MustardException, AuthException {
	    ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair("status", status));
	    params.add(new BasicNameValuePair("notice_to", notice_to));
	    params.add(new BasicNameValuePair("notice_private", notice_private));
	    params.add(new BasicNameValuePair("source", BaseApplication.APPLICATION_NAME));
	    if(!in_reply_to.equals("") && !in_reply_to.equals("-1"))
	    	params.add(new BasicNameValuePair("in_reply_to_status_id", in_reply_to));
	    if(lon!=null && !"".equals(lon))
	    	params.add(new BasicNameValuePair("long", lon));
	    if(lat!=null && !"".equals(lat))
	    	params.add(new BasicNameValuePair("lat", lat));
	    if(local_address!=null && !"".equals(local_address))
	    	params.add(new BasicNameValuePair("address", local_address));
	    if(repeat_of!=null && !"".equals(repeat_of))
	    	params.add(new BasicNameValuePair("repeat_of", repeat_of));
	   

	    String updateURL = mURL.toExternalForm() + (!isTwitter ? "/api" : "") + API_NOTICE_ADD;
	    JSONObject json = null;
	    long id=-1;
	    try {
	    	if (media == null) {
//	    		String yfrogurl = "";
//	    		if(isTwitter) {
//	    			yfrogurl = Yfrog.test(status,media);
//	    			Log.i(TAG, "Yfrog response: " + yfrogurl);
//	    		}
	    		json = mHttpManager.getJsonObject(updateURL, HttpManager.POST, params);
	    	} else {
	    		if(!isTwitter) {
	    			json = mHttpManager.getJsonObject(updateURL, params,"media",media);
	    		}
	    		else {
//	    			try {
//	    				String rr = Twitpic.upload(mContext,mHttpManager.getOAuthConsumer(),status, media);
//	    				Log.d("Mustard","twitpic id: " + rr);
//	    				String newstatus = status + " " + rr;
//	    				if (newstatus.length() > 140) {
//	    					newstatus = status.substring(0, 136 - rr.length()) + "... " + rr;
//	    				}
//	    				params.remove(0);
//	    				params.add(new BasicNameValuePair("status", newstatus));
//	    				json = mHttpManager.getJsonObject(updateURL, HttpManager.POST, params);
//	    			} catch (Exception e) {
//	    				e.printStackTrace();
//	    			}
	    		}
	    	}
//	    	System.out.println(json.toString(1));
	    	Status s = StatusNetJSONUtil.getStatus(json);
	    	id = s.getNotice().getId();
	    } catch (AuthException e) {
	    	throw e;
	    } catch (MustardException e) {
//	    	if(MustardApplication.DEBUG) 
//	    		e.printStackTrace();
	    	throw e;
	    } catch (Exception e) {
//	    	if(MustardApplication.DEBUG) 
//	    		e.printStackTrace();
	    	throw new MustardException(e.getMessage());
	    }
		return id;
	}
	
	public ArrayList<String> updateAvatar(File media) throws MustardException {
		String updateURL = mURL.toExternalForm() + (!isTwitter ? "/api" : "") + API_USER_AVATAR;
		Log.v(TAG,"updateURL===>" + updateURL.toString());
		JSONObject object = null;
		ArrayList<String> url = new ArrayList<String>();
		String avatarUrl = null;
		String avatarUrlLarge = null;
		try {
			Log.v(TAG,"isTwittwe===>" +isTwitter);
			    	if (media == null) {
			    		object = mHttpManager.getJsonObject(updateURL, HttpManager.POST, null);
			    	} else {
			    		if(!isTwitter)
			    			object = mHttpManager.getJsonObject(updateURL, null,"image",media);
			    			Log.v(TAG,"object==>" + object.toString());
			    	}
			avatarUrl = object.getString("profile_image_url");
			avatarUrlLarge = object.getString("profile_max_image_url");
			Log.v(TAG,"profile_image_url==>updateAvatar success" + avatarUrl);
			Log.v(TAG,"profile_max_image_url==>updateAvatar success" + avatarUrlLarge);
			
			url.add(avatarUrl);
			url.add(avatarUrlLarge);
	/*		listener.onComplete(new GetAvatarResponseBean(object.getString("profile_image_url")));*/
		} catch (Exception e) {
//			if (MustardApplication.DEBUG) e.printStackTrace();
			Log.v(TAG,"updateAvatar Exception" + e.toString());
			throw new MustardException(e.getMessage());
		}
		Log.v(TAG,"avatarUrl = ==>" + avatarUrl);
		return url ;
		
	}
	
	public long sendMeeting(String title,String startTime, String endTime,String location,String type,String notice_to) throws MustardException, AuthException{
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair("meeting_title", title));
	    params.add(new BasicNameValuePair("meeting_startTime", startTime));
	    params.add(new BasicNameValuePair("meeting_endTime", endTime));
	    params.add(new BasicNameValuePair("meeting_location", location));
	    params.add(new BasicNameValuePair("meeting_type", type));
	    params.add(new BasicNameValuePair("notice_to", notice_to));

	    String updateURL = mURL.toExternalForm() + (!isTwitter ? "/api" : "") + API_MEETING;
	    JSONObject json = null;
	    long id=-1;
	    try {
	    	json = mHttpManager.getJsonObject(updateURL, HttpManager.POST, params);
	    	Status s = StatusNetJSONUtil.getStatus(json);
	    	id = s.getNotice().getId();
	    } catch (AuthException e) {
	    	throw e;
	    } catch (MustardException e) {
	    	throw e;
	    } catch (Exception e) {
	    	throw new MustardException(e.getMessage());
	    }
		return id;
	}
	
	public long sendQusetion(String title,String content,String notice_to) throws MustardException, AuthException{
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair("question_title", title));
	    params.add(new BasicNameValuePair("question_content", content));
	    params.add(new BasicNameValuePair("notice_to", notice_to));

	    String updateURL = mURL.toExternalForm() + (!isTwitter ? "/api" : "") + API_QUESTION;
	    JSONObject json = null;
	    long id=-1;
	    try {
	    	json = mHttpManager.getJsonObject(updateURL, HttpManager.POST, params);
	    	Status s = StatusNetJSONUtil.getStatus(json);
	    	id = s.getNotice().getId();
	    } catch (AuthException e) {
	    	throw e;
	    } catch (MustardException e) {
	    	throw e;
	    } catch (Exception e) {
	    	throw new MustardException(e.getMessage());
	    }
		return id;
	}
	
	public long joinMeeting(String isJoin, String notice_id) throws MustardException, AuthException{
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair("Is_join_in", isJoin));
	    params.add(new BasicNameValuePair("notice_id", notice_id));

	    String updateURL = mURL.toExternalForm() + (!isTwitter ? "/api" : "") + API_MEETING_JOIN;
	    JSONObject json = null;
	    long id=-1;
	    try {
	    	json = mHttpManager.getJsonObject(updateURL, HttpManager.POST, params);
	    	if(json.getString("text")!=null){
	    		id = 1;
	    	}
	    } catch (AuthException e) {
	    	throw e;
	    } catch (MustardException e) {
	    	throw e;
	    } catch (Exception e) {
	    	throw new MustardException(e.getMessage());
	    }
		return id;
	}
	
	public long cancleMeeting(String notice_id) throws AuthException, MustardException{
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair("notice_id", notice_id));

	    String updateURL = mURL.toExternalForm() + (!isTwitter ? "/api" : "") + API_MEETING_CANCLE;
	    JSONObject json = null;
	    long id=-1;
	    try {
	    	json = mHttpManager.getJsonObject(updateURL, HttpManager.POST, params);
	    	if(json!=null){
	    		id = 1;
	    	}
	    } catch (AuthException e) {
	    	throw e;
	    } catch (MustardException e) {
	    	throw e;
	    } catch (Exception e) {
	    	throw new MustardException(e.getMessage());
	    }
		return id;
	}
	
	public boolean isTwitterInstance() {
		return isTwitter;
	}
	
/*	
	public ArrayList<MeetingPeopleInfo> getPeopleInfo(String notice_id) throws JSONException{
		ArrayList<MeetingPeopleInfo> meetingPeopleInfos = new ArrayList<MeetingPeopleInfo>();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair("notice_id", notice_id));
		String updateURL = mURL.toExternalForm() + (!isTwitter ? "/api" : "") + API_MEETING_PARTICIPATOR;
		JSONObject aj;
		try {
			aj = mHttpManager.getJsonObject(updateURL, HttpManager.POST, params);
			JSONArray jaYes = aj.getJSONArray("yes");
			JSONArray jaNo = aj.getJSONArray("no");
			JSONArray jaMaybe = aj.getJSONArray("maybe");
			MeetingItemInfo.setIsJoin(aj.getString("select"));
			RowStatus mStatus = new RowStatus();
			mStatus.setScreenName(aj.getString("happing_created_fullname"));
			try {
				mStatus.setCreated(dfs.parse(aj.getString("happing_created_time")));
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			mStatus.setUserId(aj.getLong("happing_created_id"));
			mStatus.setProfileImage(aj.getString("image_path"));
			mStatus.setName(aj.getString("happing_created_nickname"));
			mStatus.setStatus(aj.getString("notices"));
			mStatus.setStatusId(Long.parseLong(notice_id));
			MeetingItemInfo.setmStatus(mStatus);
				try {
					for(int j = 0;j<jaYes.length();j++){
						JSONObject jo = jaYes.getJSONObject(j);
						MeetingPeopleInfo info = new MeetingPeopleInfo();
						info.setId(jo.getString("id"));
						info.setPeopleName(jo.getString("name"));
						info.setPeopleUrl(jo.getString("image_path"));
						info.setIsJoin("yes");
						meetingPeopleInfos.add(info);
					}
					for(int j = 0;j<jaNo.length();j++){
						JSONObject jo = jaNo.getJSONObject(j);
						MeetingPeopleInfo info = new MeetingPeopleInfo();
						info.setId(jo.getString("id"));
						info.setPeopleName(jo.getString("name"));
						info.setPeopleUrl(jo.getString("image_path"));
						info.setIsJoin("no");
						meetingPeopleInfos.add(info);
					}
					for(int j = 0;j<jaMaybe.length();j++){
						JSONObject jo = jaMaybe.getJSONObject(j);
						MeetingPeopleInfo info = new MeetingPeopleInfo();
						info.setId(jo.getString("id"));
						info.setPeopleName(jo.getString("name"));
						info.setPeopleUrl(jo.getString("image_path"));
						info.setIsJoin("maybe");
						meetingPeopleInfos.add(info);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (MustardException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (AuthException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return meetingPeopleInfos;
	}
*/	
	public ArrayList<Status> getMeetingTimeLine(long sinceId, boolean since) throws MustardException {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair("since_id", sinceId+""));
	    if(since){
	    	params.add(new BasicNameValuePair("since_id", "true"));
	    }else{
	    	params.add(new BasicNameValuePair("since_id", "false"));
	    }
		String url = "";
		url = mURL.toExternalForm() + "/api"  + API_MEETING_TIMELINE;
		ArrayList<Status> statuses = new ArrayList<Status>();
		try {
			JSONArray aj = mHttpManager.getJsonArray(url, HttpManager.POST, params);
			for (int i = 0; i < aj.length(); i++) {
				try {
					JSONObject o = aj.getJSONObject(i);
					statuses.add(StatusNetJSONUtil.getStatus(o));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		} catch (MustardException e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		} catch (AuthException e) {
			e.printStackTrace();
			throw new MustardException(404, e.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		}
		return statuses;
	}
	
	private ArrayList<Status> getQuestionTimeLine(long sinceId, boolean since) throws MustardException {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair("since_id", sinceId+""));
	    if(since){
	    	params.add(new BasicNameValuePair("since_id", "true"));
	    }else{
	    	params.add(new BasicNameValuePair("since_id", "false"));
	    }
		String url = "";
		url = mURL.toExternalForm() + "/api"  + API_QUESTION_TIMELINE;
		ArrayList<Status> statuses = new ArrayList<Status>();
		try {
			JSONArray aj = mHttpManager.getJsonArray(url, HttpManager.POST, params);
			for (int i = 0; i < aj.length(); i++) {
				try {
					JSONObject o = aj.getJSONObject(i);
					statuses.add(StatusNetJSONUtil.getStatus(o));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		} catch (MustardException e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		} catch (AuthException e) {
			e.printStackTrace();
			throw new MustardException(404, e.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		}
		return statuses;
	}
	
	private ArrayList<Status> getPollTimeLine(long sinceId, boolean since) throws MustardException {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair("since_id", sinceId+""));
	    if(since){
	    	params.add(new BasicNameValuePair("since_id", "true"));
	    }else{
	    	params.add(new BasicNameValuePair("since_id", "false"));
	    }
		String url = "";
		url = mURL.toExternalForm() + "/api"  + API_POLL_TIMELINE;
		ArrayList<Status> statuses = new ArrayList<Status>();
		try {
			JSONArray aj = mHttpManager.getJsonArray(url, HttpManager.POST, params);
			//String ss = mHttpManager.getResponseAsString(url, HttpManager.POST, params);
			for (int i = 0; i < aj.length(); i++) {
				try {
					JSONObject o = aj.getJSONObject(i);
					statuses.add(StatusNetJSONUtil.getStatus(o));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		} catch (MustardException e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		} catch (AuthException e) {
			e.printStackTrace();
			throw new MustardException(404, e.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		}
		return statuses;
	}
/*	
	public ArrayList<AnswerInfo> getQuestionAnswerInfo(String question_id) throws MustardException {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair("notice_id", question_id));
		String url = "";
		url = mURL.toExternalForm() + "/api"  + API_QUESTION_ANSWER_INFO;
		ArrayList<AnswerInfo> ais = new ArrayList<AnswerInfo>();
		try {
			JSONArray ajs = mHttpManager.getJsonArray(url, HttpManager.POST, params);
			Object k = ajs.getJSONObject(0).get("is_close");
			if(k.equals("1")){
				QuestionItemInfo.setClose(true);
			}else{
				QuestionItemInfo.setClose(false);
			}
			int k2 = (Integer) ajs.getJSONObject(1).get("is_answer");
			if(k2==1){
				QuestionItemInfo.setAnswer(true);
			}else{
				QuestionItemInfo.setAnswer(false);
			}
			RowStatus mStatus = new RowStatus();
			mStatus.setUserId(Long.parseLong(ajs.getJSONObject(2).getString("question_created_id")));
			mStatus.setName(ajs.getJSONObject(3).getString("question_created_nickname"));
			mStatus.setScreenName(ajs.getJSONObject(4).getString("question_created_fullname"));
			mStatus.setStatusId(Long.parseLong(question_id));
			try {
				mStatus.setCreated(dfs.parse(ajs.getJSONObject(5).getString("question_created_time")));
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			mStatus.setProfileImage(ajs.getJSONObject(6).getString("image_path"));
			mStatus.setStatus(ajs.getJSONObject(7).getString("notices"));
			QuestionItemInfo.setmStatus(mStatus);
			for (int i = 8; i < ajs.length(); i++) {
				try {
					JSONObject o = ajs.getJSONObject(i);
					AnswerInfo ai = new AnswerInfo();
					ai.setContent(o.getString("content"));
					ai.setBest(o.getString("best"));
					ai.setCreated(dfs.parse(o.getString("created")));
					ai.setFullname(o.getString("fullname"));
					ai.setId(o.getString("id"));
					ai.setImage_url(o.getString("image_url"));
					ai.setNickname(o.getString("nickname"));
					ai.setProfile_id(o.getString("profile_id"));
					if(o.getString("best").equals("1")){
						QuestionItemInfo.setBestAnswer(ai);
					}
					ais.add(ai);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		} catch (MustardException e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		} catch (AuthException e) {
			e.printStackTrace();
			throw new MustardException(404, e.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		}
		return ais;
	}
*/	
	
	public long ChooseBestAnswer(String notice_id){
		long id = -1;
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair("notice_id", notice_id));
		String url = "";
		url = mURL.toExternalForm() + "/api"  + API_QUESTION_CHOOSE_BEST_ANSWER;
		try {
			JSONArray jo = mHttpManager.getJsonArray(url, HttpManager.POST, params);
			if(jo!=null){
	    		id = 1;
	    	}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MustardException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AuthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;
	}
	
	public long UpdateAnswer(String notice_id,String answer){
		long results = -1;
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair("notice_id", notice_id));
	    params.add(new BasicNameValuePair("answer_content", answer));
		String url = "";
		url = mURL.toExternalForm() + "/api"  + API_QUESTION_ANSWER;
		try {
			String result = mHttpManager.getResponseAsString(url, HttpManager.POST, params);;
			if(result.equals("closed")){
				results = 2;
	    	}else{
	    		results = 1;
	    	}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MustardException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AuthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}
	
	public long CloseQuestion(String notice_id){
		long id = -1;
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair("notice_id", notice_id));
		String url = "";
		url = mURL.toExternalForm() + "/api"  + API_QUESTION_CLOSE;
		try {
			JSONObject jo = mHttpManager.getJsonObject(url, HttpManager.POST, params);
			if(jo!=null){
	    		id = 1;
	    	}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MustardException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AuthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;
	}
	
	public long CreatPoll(String title,String option1,String option2,String option3,String option4,String option5,String notice_to){
		long id  = -1;
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair("notice_to", notice_to));
	    params.add(new BasicNameValuePair("question", title));
	    params.add(new BasicNameValuePair("option1", option1));
	    params.add(new BasicNameValuePair("option2", option2));
	    params.add(new BasicNameValuePair("option3", option3));
	    params.add(new BasicNameValuePair("option4", option4));
	    params.add(new BasicNameValuePair("option5", option5));
		String url = "";
		url = mURL.toExternalForm() + "/api"  + API_POLL;
		try {
			JSONObject ja = mHttpManager.getJsonObject(url, HttpManager.POST, params);
			if(ja!=null){
	    		id = 1;
	    	}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MustardException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AuthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;
	}
/*	
	public PollDetails getPollInfo(String notice_id) throws MustardException {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("notice_id", notice_id));
		String url = "";
		url = mURL.toExternalForm() + "/api" + API_POLL_RESPONSE;
		PollDetails details = new PollDetails();
		try {
			JSONObject ajs = mHttpManager.getJsonObject(url, HttpManager.POST,params);
			details.setJoin(ajs.getBoolean("is_poll"));
			details.setQuestion(ajs.getString("poll_name"));
			details.setCreated(dfs.parse(ajs.getString("created")));
			details.setImage_url(ajs.getString("image_url"));
			details.setProfile_id(ajs.getString("profile_id"));
			details.setName(ajs.getString("nickname"));
			details.setId(ajs.getString("poll_created_id"));
			JSONArray jaOption = ajs.getJSONArray("poll_options");
			JSONObject jaOptionCount = ajs.getJSONObject("options_count");
			for(int i = 0; i<jaOption.length();i++){
				int k = i+1;
				Object ob = jaOption.get(i);
				String detail = ob.toString();
				PollOption option = new PollOption();
				option.setOption(detail);
				option.setOption_num(jaOptionCount.getInt(""+k));
				details.addOption(option);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		} catch (MustardException e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		} catch (AuthException e) {
			e.printStackTrace();
			throw new MustardException(404, e.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		}
		return details;
	}
*/
	public boolean pollUpdate(String notice_id,String select_id)throws MustardException, JSONException{
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("notice_id", notice_id));
		params.add(new BasicNameValuePair("select_id", select_id));
		String url = "";
		url = mURL.toExternalForm() + "/api" + API_POLL_UPDATE;
		boolean isSucce = false;
		try {
			JSONObject ajs = mHttpManager.getJsonObject(url, HttpManager.POST,params);
			if(ajs.getInt("id")>0){
				isSucce = true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AuthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isSucce;
	}
	public int register(String nickname,String password,String email,String fullname,String homepage,String bio,String location,String code) throws IOException, MustardException, AuthException, JSONException{
		int result = -1;
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair("nickname", nickname));
	    params.add(new BasicNameValuePair("password", password));
	    params.add(new BasicNameValuePair("email", email));
	    params.add(new BasicNameValuePair("fullname", fullname));
	    params.add(new BasicNameValuePair("homepage", homepage));
	    params.add(new BasicNameValuePair("bio", bio));
	    params.add(new BasicNameValuePair("location", location));
	    params.add(new BasicNameValuePair("code", code));
		String url = "";
		url = mURL.toExternalForm() + "/api"  + API_REGISTER;
		JSONObject jo = mHttpManager.getJsonObject(url, HttpManager.POST, params);
		String resultCode = jo.getString("status");
		if(resultCode.equals("succeed")){
			result = 1;//娉ㄥ唽鎴愬姛
		}else if(resultCode.equals("404")){
			result = -1;//鎵句笉鍒版湇鍔″櫒
		}else if(resultCode.equals("emialIllegal")){
			result = 2;//閭欢涓嶅悎娉�
		}else if(resultCode.equals("nicknameIllegal")){
			result = 3;//涓嶅悎娉曠殑璐︽埛
		}else if(resultCode.equals("nicknameExists")){
			result = 4;//璐︽埛宸插瓨鍦�
		}else if(resultCode.equals("emailExists")){
			result = 5;//閭欢鍦板潃宸插瓨鍦�
		}else if(resultCode.equals("invalid")){
			result = 6;//鏃犳晥鐨勭敤鎴峰悕鍜屽瘑鐮�
		}
		return result;
	}

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }
	
}
