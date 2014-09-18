package com.mp.android.util;

import com.mp.android.statusnet.MustardDbAdapter;

public class Constant {

	public static String SUCCESS="1";
	public static String FAILURE="0";
	public static String IM_LOGIN_USER = "im_login_user_name";
	public static String NOTIFY_URI = "notify_uri";
	public static String MESSAGE_FROM = "message_from";
	public static String MESSAGE_FROM_NOTIFICATION = "message_from__notification";
	public static String NOTIFY_ITEM_CHAT_LIST = "notify_item_chat_list";
	public static String CHAT_TYPE = "mChatType";
	public static String GROUPCHAT_MESSAGE_VOICE_PREFIX = "<group_chat_message_voice>";
	public static String URL_UPLOAD_FILE = "http://" + MustardDbAdapter.DOMAIN + MustardDbAdapter.PORT + "/plugins/chatlog/uploadservlet";
	public static String URL_DOWNLOAD_FILE = "http://" + MustardDbAdapter.DOMAIN + MustardDbAdapter.PORT + "/plugins/chatlog/downloadservlet?filepath=";
	public static String SEND_CHATGROUP_VOICE_SUCCESS = "send_chatgroup_voice_success";
	public static String SEND_CHATGROUP_VOICE_FAILURE = "send_chatgroup_voice_failure";
}
