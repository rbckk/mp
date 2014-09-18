package com.mp.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mp.android.newsfeed.NewsFeedResult;
import com.mp.android.statusnet.statusnet.RowStatus;

//import com.mp.android.groups.GroupsResult;

public class RenRenData {
	/**
	 * ±íÇé
	 */
	//public static List<EmoticonsResult> mEmoticonsResults = new ArrayList<EmoticonsResult>();
	/**
	 * È«²¿ÐÂÏÊÊÂ
	 */
	public static List<NewsFeedResult> mNewsFeedAllResults = new ArrayList<NewsFeedResult>();

	/**
	 * ÐÂÏÊÊÂ
	 */
	public static List<NewsFeedResult> mNewsFeedResults = new ArrayList<NewsFeedResult>();

	/**
	 * ×´Ì¬
	 */
//	public static List<StatusResult> mStatusResults = new ArrayList<StatusResult>();

	/**
	 * ºÃÓÑ
	 */
	//public static List<FriendsResult> mFriendsResults = new ArrayList<FriendsResult>();
	// ¸ù¾ÝÊ××ÖÄ¸´æ·ÅÊý¾Ý
	//public static Map<String, List<FriendsResult>> mFriendsMap = new HashMap<String, List<FriendsResult>>();
	// Ê××ÖÄ¸¶ÔÓ¦µÄÎ»ÖÃ
	public static Map<String, Integer> mFriendsIndexer = new HashMap<String, Integer>();
	// Ê××ÖÄ¸¼¯
	public static List<String> mFriendsSections = new ArrayList<String>();
	// Ê××ÖÄ¸Î»ÖÃ¼¯
	public static List<Integer> mFriendsPositions = new ArrayList<Integer>();
	// Ã¿¸öÊ×ºº×ÖµÄÎ»ÖÃ
	public static Map<String, Map<String, Integer>> mFriendsFirstNamePosition = new HashMap<String, Map<String, Integer>>();
	// ²éÑ¯ºÃÓÑ
	//public static List<FriendsFindResult> mFriendsFindResults = new ArrayList<FriendsFindResult>();
	
	/**
	 * Groups
	 */
	//public static List<GroupsResult> mGroupsResults = new ArrayList<GroupsResult>();
	// ¸ù¾ÝÊ××ÖÄ¸´æ·ÅÊý¾Ý
	//	public static Map<String, List<GroupsResult>> mGroupsMap = new HashMap<String, List<GroupsResult>>();
		// Ê××ÖÄ¸¶ÔÓ¦µÄÎ»ÖÃ
		public static Map<String, Integer> mGroupsIndexer = new HashMap<String, Integer>();
		// Ê××ÖÄ¸¼¯
		public static List<String> mGroupsSections = new ArrayList<String>();
		// Ê××ÖÄ¸Î»ÖÃ¼¯
		public static List<Integer> mGroupsPositions = new ArrayList<Integer>();
		// Ã¿¸öÊ×ºº×ÖµÄÎ»ÖÃ
		public static Map<String, Map<String, Integer>> mGroupsFirstNamePosition = new HashMap<String, Map<String, Integer>>();
		
	
		/**
		 * GroupMembers
		 */
		//public static List<FriendsResult> mGroupMembersResults = new ArrayList<FriendsResult>();
		// ¸ù¾ÝÊ××ÖÄ¸´æ·ÅÊý¾Ý
		//	public static Map<String, List<FriendsResult>> mGroupMembersMap = new HashMap<String, List<FriendsResult>>();
			// Ê××ÖÄ¸¶ÔÓ¦µÄÎ»ÖÃ
			public static Map<String, Integer> mGroupMembersIndexer = new HashMap<String, Integer>();
			// Ê××ÖÄ¸¼¯
			public static List<String> mGroupMembersSections = new ArrayList<String>();
			// Ê××ÖÄ¸Î»ÖÃ¼¯
			public static List<Integer> mGroupMembersPositions = new ArrayList<Integer>();
			// Ã¿¸öÊ×ºº×ÖµÄÎ»ÖÃ
			public static Map<String, Map<String, Integer>> mGroupMembersFirstNamePosition = new HashMap<String, Map<String, Integer>>();
	

	// ����ĸ��Ӧ��λ��
	public static Map<String, Integer> mPageIndexer = new HashMap<String, Integer>();
	// Ê××ÖÄ¸¼¯
	public static List<String> mPageSections = new ArrayList<String>();
	// Ê××ÖÄ¸Î»ÖÃ¼¯
	public static List<Integer> mPagePositions = new ArrayList<Integer>();
	// Ã¿¸öÊ×ºº×ÖµÄÎ»ÖÃ
	public static Map<String, Map<String, Integer>> mPageFirstNamePosition = new HashMap<String, Map<String, Integer>>();

	/**
	 * ¸½½üÓÅ»Ý
	 */

	
	/**
	 * ¸½½üµØµã
	 */
	
    public static ArrayList<RowStatus> mPublicTimelineStatus = new ArrayList<RowStatus>();
    public static ArrayList<RowStatus> mFriendlineStatus = new ArrayList<RowStatus>();
}
