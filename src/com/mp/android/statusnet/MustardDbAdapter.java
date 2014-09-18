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
package com.mp.android.statusnet;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.inputmethodservice.Keyboard.Key;
import android.os.Environment;
import android.util.Log;

import com.mp.android.R;
import com.mp.android.statusnet.statusnet.Attachment;
import com.mp.android.statusnet.statusnet.Notice;
import com.mp.android.statusnet.statusnet.Status;
import com.mp.android.statusnet.statusnet.User;
import com.mp.android.statusnet.util.MustardException;

public class MustardDbAdapter {

	public static final int ROWTYPE_ALL = 99;
	public static final int ROWTYPE_PUBLIC = 0;
	public static final int ROWTYPE_FRIENDS = 1;
	public static final int ROWTYPE_USER = 2;
	public static final int ROWTYPE_GROUP = 3;
	public static final int ROWTYPE_TAG = 4;
	public static final int ROWTYPE_SINGLE = 5;
	public static final int ROWTYPE_MENTION = 6;
	public static final int ROWTYPE_SEARCH = 7;
	public static final int ROWTYPE_FAVORITES = 8;
	public static final int ROWTYPE_CONVERSATION = 9;
	public static final int ROWTYPE_MEETING = 10;
	public static final int ROWTYPE_QUESTION = 11;
	public static final int ROWTYPE_NOTICEMENTIONS = 12;
	public static final int ROWTYPE_POLL = 13;
	
	private static final String TAG = "MustardDbAdapter";

	public static final String DATABASE_NAME = "data";
	public static final String DATABASE_STATUS_TABLE = "dents";
	public static final String DATABASE_ACCOUNT_TABLE = "accounts";
	public static final String DATABASE_ATTACHMENTS_TABLE = "attachments";
//	public static final String DATABASE_PARAM_TABLE = "params";
//	public static final String DATABASE_BOOKMARK_TABLE = "bookmarks";
//	public static final String DATABASE_FILTER_TABLE = "filters";
//	public static final String DATABASE_OAUTH_TABLE = "oauthkeys";

	public static final String KEY_ROWID = "_id";
	
	//
	public static final String KEY_DENT_ID = "dent_id";
	
	//data
	public static final String KEY_ACCOUNT_ID = "_account_id";
	public static final String KEY_ROWTYPE = "rowtype";//"backup"
	public static final String KEY_INSERT_AT = "insert_at";//"created_at"
	public static final String KEY_STATUS_ID = "status_id";//"ad_SN"
	public static final String KEY_USER_ID = "user_id";
	public static final String KEY_NAME = "name";//"backup"
	public static final String KEY_SCREEN_NAME = "screen_name";//"ad_seller"
	public static final String KEY_STATUS = "status";//"ad_text"
	public static final String KEY_ATTACHMENT = "attachment";
	public static final String KEY_REPEATED_ID = "repeated_id";
	public static final String KEY_USER_IMAGE = "user_image";
	public static final String KEY_AD_CODE = "ad_code";
	//public static final String KEY_REPEATED_BY_SCREEN_NAME = "repeated_by_screen_name";
	//public static final String KEY_GEO = "geo";
	//public static final String KEY_LON = "lon";
	//public static final String KEY_LAT = "lat";
	//public static final String KEY_KEY = "key";
	//public static final String KEY_STATUS_HTML = "status_html";
	//public static final String KEY_LOCATION = "location";
	//public static final String KEY_FAVORITE = "favorite";
	//public static final String KEY_FOLLOWING = "following";
	//public static final String KEY_BLOCKING = "blocking";
	//public static final String KEY_SOURCE = "source";
	//public static final String KEY_IN_REPLY_TO = "in_reply_to";
	//public static final String KEY_IN_REPLY_TO_SCREEN_NAME = "in_reply_to_screen_name";
	//public static final String KEY_USER_IMAGE_LARGE = "user_image_large";
	//public static final String KEY_USER_URL = "user_url";
	//public static final String KEY_NOTICE_TYPE = "notice_type";
	//public static final String KEY_ROWEXTRA = "rowextra";
	//public static final String KEY_REPLY_COUNT = "reply_count";
					
	public static final String KEY_SECRET = "secret";
	public static final String KEY_RESERVED = "reserved";

	//account
	public static final String KEY_USER = "user";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_INSTANCE = "instance";
	public static final String KEY_DEFAULT = "isdefault";
	public static final String KEY_VERSION = "version";
	public static final String KEY_TEXTLIMIT = "textlimit";
	public static final String KEY_ATTACHLIMIT = "attachlimit";
	public static final String KEY_TOKEN = "token";
	public static final String KEY_TOKEN_SECRET = "token_secret";
	public static final String KEY_TO_MERGE = "merge";
	public static final String KEY_MENTION_MAX_ID = "mention_max_id";

	//attachment	
	public static final String KEY_MIMETYPE = "mimetype";
	public static final String KEY_URL = "url";

	//param
	public static final String KEY_CODE = "code";
	public static final String KEY_VALUE = "value";
	
	//bookmark
	public static final String KEY_BTYPE = "btype";
	public static final String KEY_BPARAM = "bparam";
	public static final String KEY_COUNTER = "counter";
	
	//filter
	public static final String KEY_FILTER = "filter";
	
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	public static final String DOMAIN = "211.152.38.248";//"172.17.199.41";
	public static final String PORT = ":9090";
	public static final String INSTANCE = DOMAIN +"/StatusNet/index.php";
	public static final String SERVICE_URL = "http://"+DOMAIN+PORT+"/services/GroupUserInfoWebService";
	public static final String PATH_PKG = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.mp.android/RenRenForAndroid/";
	public static final String PATH_CACHE = PATH_PKG + "Cache/";
	public static final String PATH_IMG = PATH_PKG + "img";
	public static final String PATH_ETC = PATH_PKG + "Emoticons/";
	public static final String PATH_CAM = PATH_PKG + "Camera/";
	public static final String ACTION_REMOTE_SERVICE = "com.android.im.REMOTE_SERVICE";
	
	/**
	 * Database creation sql statement
	 */
	public static final String DATABASE_CREATE_STATUS =
		"create table " + DATABASE_STATUS_TABLE + " ( " +
		"_id integer primary key autoincrement, " +
		"_account_id integer not null, " +
		"rowtype integer not null," +
		//"rowextra text not null," +
		"status_id integer not null, " +
		"insert_at text not null, " +
		"user_id integer not null, " +
		//"user_url text not null, " +
		"user_image text not null, " +
		//"user_image_large text null, " +
		"screen_name text not null, " +
		"name text not null, " +
		//"source text null, " +
		//"in_reply_to integer null, " +
		//"in_reply_to_screen_name text null, " +
		"repeated_id integer null, " +
		//"repeated_by_screen_name text null, " +
		"status text not null," +
		//"status_html text not null," +
		//"location text null," +
		//"favorite int null, " +
		//"following int null, " +
		//"blocking int null, " +
		"attachment int null, " +
		"ad_code text not null, " +
		//"geo int not null," +
		//"lon real not null, " +
		//"lat real not null, " +
		//"notice_type text not null," +
		//"reply_count integer not null, " +
		"unique (_account_id, rowtype,status_id) )"; 

	public static final String DATABASE_CREATE_ACCOUNT =  
		"create table " + DATABASE_ACCOUNT_TABLE + " (" +
		"_id integer primary key autoincrement," +
		"user_id integer not null, " +
		"user text not null, " +
		"password text not null," +
		"instance text not null," +
		"isdefault integer not null," +
		"version text null, " +
		"textlimit integer null, " +
		"attachlimit integer null, " + 
		"token text null, " +
		"token_secret text null," +
		"mention_max_id integer, " +
		"merge integer null ) ";

	public static final String DATABASE_DROP_ATTACHMENTS = 
		"DROP TABLE IF EXISTS " + DATABASE_ATTACHMENTS_TABLE; 
	
	public static final String DATABASE_CREATE_ATTACHMENTS = 
		"CREATE TABLE " + DATABASE_ATTACHMENTS_TABLE  + " (" +
			"_id integer primary key autoincrement, " +
				"dent_id integer not null,  " +
				"url text not null," +
				"mimetype text not null, " +
				"FOREIGN KEY (dent_id) REFERENCES "+DATABASE_STATUS_TABLE+"("+KEY_ROWID+") ON DELETE CASCADE )";
/*	
	public static final String DATABASE_CREATE_PARAM =  
		"create table " + DATABASE_PARAM_TABLE + " (" +
		"code text not null," +
		"value text null, " +
		"primary key (code)) ";
	
	public static final String DATABASE_CREATE_BOOKMARKS = 
		"create table " + DATABASE_BOOKMARK_TABLE + " (" +
		"_id integer primary key autoincrement, " +
		"user_id integer not null," +
		"btype text not null, " +
		"bparam text not null, " +
		"counter int not null, " +
		"unique (user_id, btype,bparam)) ";
	
	public static final String DATABASE_CREATE_FILTERS = 
		"create table " + DATABASE_FILTER_TABLE + " (" +
		"_id integer primary key autoincrement, " +
		"filter text not null) ";
	
	public static final String DATABASE_CREATE_OAUTH = 
		"create table " + DATABASE_OAUTH_TABLE + " ( " +
		" _id integer primary key autoincrement, " +
		" instance text not null," +
		" key text not null, " +
		" secret text not null, " +
		" reserved int not null, " +
		" unique (instance) " + 
		" ) ";
*/	
	public static final int DATABASE_VERSION = 44;

	private final Context mCtx;
	private static DatabaseHelper instance;  
	
	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		//add by xiaosong.xu
		public static DatabaseHelper Instance(Context context){
	        if (instance == null) {  
	            instance = new DatabaseHelper(context);  
	        }   
	        return instance; 
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_STATUS_TABLE );
			db.execSQL(DATABASE_CREATE_STATUS);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_ACCOUNT_TABLE );	
			db.execSQL(DATABASE_CREATE_ACCOUNT);
			//db.execSQL("DROP TABLE IF EXISTS " + DATABASE_PARAM_TABLE );	
			//db.execSQL(DATABASE_CREATE_PARAM);
			//db.execSQL("DROP TABLE IF EXISTS " + DATABASE_BOOKMARK_TABLE);
			//db.execSQL(DATABASE_CREATE_BOOKMARKS);
			//db.execSQL("DROP TABLE IF EXISTS " + DATABASE_OAUTH_TABLE);
			//db.execSQL(DATABASE_CREATE_OAUTH);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_ATTACHMENTS_TABLE);
			db.execSQL(DATABASE_CREATE_ATTACHMENTS);
			//db.execSQL("DROP TABLE IF EXISTS " + DATABASE_FILTER_TABLE);
			//db.execSQL(DATABASE_CREATE_FILTERS);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.i(TAG,"onUpgrade " + oldVersion + " >> " + newVersion);
			boolean lastTableAccount=false;
			//boolean lastTableParam=false;
			//boolean lastTableBookmark=false;
			//boolean lastTableOAuth=false;
			//boolean lastTableFilter=false;
			
			// Simply I don't want to see warning message ;)
			// -->>
			//if(!lastTableParam) {
			//	
			//}
			//if(!lastTableBookmark) {
				
			//}
			// <<--
			if (oldVersion > 1) {
				if (oldVersion<15) {
					db.execSQL("DROP TABLE IF EXISTS " + DATABASE_ACCOUNT_TABLE );			
					db.execSQL(DATABASE_CREATE_ACCOUNT);
					lastTableAccount=true;
				}
				if (oldVersion<21) {
					//db.execSQL(DATABASE_CREATE_PARAM);
					//lastTableParam=true;
				} else if (oldVersion < 24) {
					
					//db.execSQL("CREATE TEMPORARY TABLE t1_backup(a,b)");
					//db.execSQL("INSERT INTO t1_backup SELECT code,value FROM " + DATABASE_PARAM_TABLE + " WHERE code = 'mid'");
					//db.execSQL("DROP TABLE IF EXISTS " + DATABASE_PARAM_TABLE);
					//db.execSQL(DATABASE_CREATE_PARAM);
					//db.execSQL("INSERT INTO " + DATABASE_PARAM_TABLE + " SELECT a,b FROM t1_backup");
					//db.execSQL("DROP TABLE t1_backup");
					//lastTableParam=true;
				}
				
				if (oldVersion < 27) {
					//db.execSQL("DROP TABLE IF EXISTS " + DATABASE_BOOKMARK_TABLE);
					//db.execSQL(DATABASE_CREATE_BOOKMARKS);
					//lastTableBookmark=true;
				}
				if (oldVersion < 28) {
					
					//db.execSQL("DROP TABLE IF EXISTS " + DATABASE_OAUTH_TABLE);
					//db.execSQL(DATABASE_CREATE_OAUTH);
					//lastTableOAuth=true;
					//if(!lastTableAccount) {
					//	db.execSQL("ALTER TABLE " + DATABASE_ACCOUNT_TABLE + " ADD token text null");
					//	db.execSQL("ALTER TABLE " + DATABASE_ACCOUNT_TABLE + " ADD token_secret text null");
					//}
				} 
				if (oldVersion < 30) {
					//if(!lastTableOAuth) {
					//	db.execSQL("ALTER TABLE " + DATABASE_OAUTH_TABLE + " ADD reserved int null");
					//	db.execSQL("UPDATE " + DATABASE_OAUTH_TABLE + " set reserved = '1' " );
					//}
					//if(!lastTableAccount) {
					//	db.execSQL("ALTER TABLE " + DATABASE_ACCOUNT_TABLE + " ADD " + KEY_MENTION_MAX_ID + " integer ");
					//}
				}
				if (oldVersion < 32) {
					if(!lastTableAccount) {
						db.execSQL("ALTER TABLE " + DATABASE_ACCOUNT_TABLE + " ADD " + KEY_USER_ID + " integer ");
					}
				}
				if (oldVersion < 35) {
					if(!lastTableAccount) {
						db.execSQL("ALTER TABLE " + DATABASE_ACCOUNT_TABLE + " ADD " + KEY_TEXTLIMIT + " integer ");
					}
				}
				if(oldVersion<37) {
					//if(!lastTableFilter) {
					//	db.execSQL("DROP TABLE IF EXISTS " +
					//			DATABASE_FILTER_TABLE);
					//	db.execSQL(DATABASE_CREATE_FILTERS);
					//}
				}
				if (oldVersion < 38) {
					if (!lastTableAccount) {
						db.execSQL("ALTER TABLE " + DATABASE_ACCOUNT_TABLE + " ADD " + KEY_TO_MERGE + " integer ");
						db.execSQL("UPDATE " + DATABASE_ACCOUNT_TABLE + " set " + KEY_TO_MERGE + " = 0 " );
						db.execSQL("UPDATE " + DATABASE_ACCOUNT_TABLE + " set " + KEY_TO_MERGE + " = 1  WHERE " + KEY_DEFAULT + " = 1" );
					}
				}
				if (oldVersion < 43) {
					if (!lastTableAccount) {
						db.execSQL("ALTER TABLE " + DATABASE_ACCOUNT_TABLE + " ADD " + KEY_ATTACHLIMIT + " integer ");
						db.execSQL("UPDATE " + DATABASE_ACCOUNT_TABLE + " set " + KEY_ATTACHLIMIT + " = 131072 " );
					}
				}
				
			}
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_STATUS_TABLE );
			db.execSQL(DATABASE_CREATE_STATUS);
			db.execSQL(DATABASE_DROP_ATTACHMENTS);
			db.execSQL(DATABASE_CREATE_ATTACHMENTS);
		}

	}

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx the Context within which to work
	 */
	public MustardDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	public String creareMustardId() {
		//Random r = new Random();
		//String token = Long.toString(Math.abs(r.nextLong()), 36);
		//addParameter("mid",token);
		//return token;
		return null;
	}
	
	public String getMustardId() {
		//String mid=getParameter("mid");
		//return mid;
		return null;
	}
/*	
	public boolean addParameter(String code, String value) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_CODE, code);
		initialValues.put(KEY_VALUE, value);
		long _rid = -1;
		try {
			_rid  = mDb.insertOrThrow(DATABASE_PARAM_TABLE, null, initialValues);
		} catch(SQLiteConstraintException e) {
			// No problem, update
		}
		if (_rid < 0) {
			// Update??
			return setParameter(code, value);
		} else {
			return true;
		}
	}
	
	public boolean setParameter(String code, String value) {
		ContentValues args = new ContentValues();
        args.put(KEY_VALUE, value);
        mDb.update(DATABASE_PARAM_TABLE, args, KEY_CODE + "= '" + code + "'", null);
        return true;
	}
*/	
	private long createAttachment(long status_id, String url, String mimetype) throws MustardException {

		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_DENT_ID, status_id);
		initialValues.put(KEY_URL, url);
		initialValues.put(KEY_MIMETYPE, mimetype);
		long ret = -1;
		try {
			 ret = mDb.insertOrThrow(DATABASE_ATTACHMENTS_TABLE, null, initialValues);
		} catch(SQLiteConstraintException e) {
			ret = 0;
		} catch (Exception e) {
			throw new MustardException(e.toString());
		}
		return ret;
	}

	public Cursor fetchAttachment(long statusId) throws SQLException {

		Cursor cursor =
			mDb.query(DATABASE_ATTACHMENTS_TABLE, 
					new String[] {
						KEY_URL,
						KEY_MIMETYPE
			}, 
			KEY_DENT_ID + " = ?", new String[]{Long.toString(statusId)},
			null, null, null, null);
		return cursor;

	}

	public long getUserMentionMaxId(long userid) {
		long mid=0;
		Cursor mCursor = null;
		try {
			mCursor = mDb.query(DATABASE_ACCOUNT_TABLE, 
					new String[] { KEY_MENTION_MAX_ID
			}, 
			KEY_ROWID + " = "+userid,
			null,
			null, null, null, null);
			if (mCursor != null) {
				if(mCursor.moveToFirst())
					mid = mCursor.getLong(0);
			}
		} catch(Exception e) {
			Log.e("Mustard","getUserMentionMaxId: " + e.getMessage());
		} finally {
			if(mCursor!=null)
				try {
					mCursor.close();
				} catch (Exception e) {
					Log.e("Mustard","getUserMentionMaxId[closing cursor] " + e.getMessage());
				}
		}
		return mid;
	}
	
	public boolean setUserMentionMaxId(long userid,long maxid) {
		ContentValues args = new ContentValues();
        args.put(KEY_MENTION_MAX_ID, maxid);
        return mDb.update(DATABASE_ACCOUNT_TABLE, args, KEY_ROWID + "= " + userid, null) > 0;
	}
	
	/**
	 * Open the notes database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to
	 * signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException if the database could be neither opened or created
	 */
	public MustardDbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);

		Log.v("test", "mDbHelper.getWritableDatabase().isDbLockedByOtherThreads()---"+
				mDbHelper.getWritableDatabase().isDbLockedByOtherThreads());
		while (mDbHelper.getWritableDatabase().isDbLockedByOtherThreads()){
            try {  
                Thread.sleep(10);  
            } catch (InterruptedException e) {  
                e.printStackTrace();  
            }  
		}
		mDb = mDbHelper.getWritableDatabase();

		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	public int getDbversion() {
		return mDb.getVersion();
	}

	public boolean createStatuses(long account_id, int type, String extra, ArrayList<Status> dents) {
		if(dents == null) {
			return false;
		}
		
		try {
			for (Status dent : dents){
					createStatus(account_id,type, extra, dent);
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public long createStatus(long account_id, int type, Status dent) throws MustardException {
		return createStatus(account_id, type,"",dent);
	}
	
	public long createStatus(long account_id, int type,String extra, Status dent) throws MustardException {
	    Notice n = dent.getNotice();
		User u = dent.getUser();
		boolean isTest=true;
		if (u==null) {
			isTest=false;
		}
		if(n==null) {
			isTest=false;
		}
		if(!isTest)
			return -1;
		long ret = 0;
		try {
			Date d = n.getCreated_at();
			String _d = "";
			if (d != null) {
				_d = ""+d.getTime();
			} else {
			}
			boolean hasAttachment = n.getAttachments() != null && n.getAttachments().size()>0 ? true : false;

			ret = createStatus(
				account_id, 
				type,
				//extra,
				n.getId(),
				_d,
				u.getScreen_name(), 
				u.getId(),
				u.getName(),
				//n.getSource(),
				//n.getIn_reply_to_status_id(),
				//n.getIn_reply_to_screen_name(),
				n.getRepeated_id(), 
				//n.getRepeated_by_screen_name(),
				n.getText(),
				//n.getStatusnet_text(),
				//n.getLocation(),
				//u.getProfile_url(),
				u.getProfile_image_url(),
				//u.getProfile_image_url_large(),
				//n.isFavorited() ? 1 : 0,
				//u.isFollowing() ? 1 : 0,
				//u.isBlocking() ? 1 : 0,
				hasAttachment ? 1 : 0,
				//n.isGeo(),
				//n.getLat(),
				//n.getLon(),
				//n.getNotice_type(),
				//n.getReply_count(),
				n.getadcode()
						);
			if (hasAttachment && ret > 0) {
				for (Attachment attachment: n.getAttachments()){
					createAttachment(ret, attachment.getUrl(), attachment.getMimeType());
				}
			}
		} catch (NullPointerException e) {
			throw new MustardException(e.toString());
		}
		return ret;
	}

	private long createStatus(
			long account_id,
			int type, 
			//String extra, 
			long status_id, 
			String insert_at, 
			String screen_name, 
			long user_id, 
			String name,
			//String source,
			//long in_reply_to, 
			//String in_reply_to_screen_name, 
			long repeated_id, 
			//String repeated_by_screen_name, 
			String status,
			//String status_html,
			//String location,
			//String user_url, 
			String user_image, 
			//String user_image_large,
			//int favorited, 
			//int following,
			//int blocking,
			int attachment, 
			//boolean geo,
			//double lat, 
			//double lon,
			//String notice_type,
			//long reply_count,
			String adcode
			) throws MustardException {

		ContentValues initialValues = new ContentValues();

		initialValues.put(KEY_ACCOUNT_ID, account_id);
		initialValues.put(KEY_ROWTYPE, type);
		initialValues.put(KEY_STATUS_ID, status_id);
		initialValues.put(KEY_INSERT_AT, insert_at);
		initialValues.put(KEY_SCREEN_NAME, screen_name);
		initialValues.put(KEY_USER_ID, user_id );
		initialValues.put(KEY_NAME, name );
		initialValues.put(KEY_STATUS, status);
		initialValues.put(KEY_ATTACHMENT, attachment);
		initialValues.put(KEY_USER_IMAGE, user_image);
		if(adcode == null){
			initialValues.put(KEY_AD_CODE, "blacksheepwall");	
		}else{
			initialValues.put(KEY_AD_CODE, adcode);	
		}
	
		long ret = -1;
		try {
			ret = mDb.insertOrThrow(DATABASE_STATUS_TABLE, null, initialValues);
		} catch(SQLiteConstraintException e) {
			ret = 0;
		} catch (Exception e) {
			throw new MustardException(e.toString());
		}
		return ret;
	}

/*	
	public boolean updateStatusFavor(long rowid, boolean favor) {
		return updateStatusFavor(Long.toString(rowid), favor);
	}
	
    public boolean updateStatusFavor(String rowid, boolean favor) {
        int i_favor = (favor) ? 1 : 0;
        ContentValues args = new ContentValues();
        args.put(KEY_FAVORITE, i_favor);
        return mDb.update(DATABASE_STATUS_TABLE,
                        args, KEY_ROWID + " = ? ",
                        new String[] { rowid}) > 0;
    }
   
    //add by zb for favorite by statusId
    public boolean updateStatusFavorByStatusId(long statusid, boolean favor) {
		return updateStatusFavorByStatusId(Long.toString(statusid), favor);
	}
    public boolean updateStatusFavorByStatusId(String statusid, boolean favor) {
        int i_favor = (favor) ? 1 : 0;
        ContentValues args = new ContentValues();
        args.put(KEY_FAVORITE, i_favor);
        return mDb.update(DATABASE_STATUS_TABLE,
                        args, KEY_STATUS_ID + " = ? ",
                        new String[] { statusid}) > 0;
    }
    //add end

    public boolean updateStatusBlocking(String userid, boolean blocking) {
        int i_blocking = (blocking) ? 1 : 0;
        ContentValues args = new ContentValues();
        args.put(KEY_BLOCKING, i_blocking);
        return mDb.update(DATABASE_STATUS_TABLE,
                        args, KEY_USER_ID + " = ?",
                        new String[] { userid}) > 0;
    }

	public boolean updateStatusFollowing(String userid, boolean follow) {
		ContentValues args = new ContentValues();
        args.put(KEY_FOLLOWING, follow ? 1 : 0);
        return mDb.update(DATABASE_STATUS_TABLE, args, KEY_USER_ID + " = ?", new String[] { userid }) > 0;
		
	}
*/	
	public boolean deleteStatuses(int rowtype,String extra) {
		if (rowtype == ROWTYPE_ALL) {
			mDb.delete(DATABASE_ATTACHMENTS_TABLE, "1", null);
			return mDb.delete(DATABASE_STATUS_TABLE, "1", null) > 0;
		} else {
			//return mDb.delete(DATABASE_STATUS_TABLE, KEY_ROWTYPE + " = " + rowtype + " and " + KEY_ROWEXTRA + " = '" + extra + "'", null) > 0 ;
			return mDb.delete(DATABASE_STATUS_TABLE, KEY_ROWTYPE + " = " + rowtype , null) > 0 ;
		}
	}
/*
	public boolean deleteOlderMergedStatuses(int rowtype,String extra) {
		// I need to get the 25th status:
		int limit = 25;
		
		Cursor cc = fetchAllAccountsToMerge();
		int aindx = cc.getColumnIndex(MustardDbAdapter.KEY_ROWID);
		while(cc.moveToNext()) {

			long _aid = cc.getLong(aindx);
			Cursor c = mDb.query(true,
					DATABASE_STATUS_TABLE, 
					new String[] {
					KEY_ROWID, KEY_STATUS_ID
				},
				KEY_ROWTYPE + " = " + rowtype + " and " + KEY_ROWEXTRA + " = '" + extra + "' and " + KEY_ACCOUNT_ID + " = " + _aid  , 
				null, null, null, KEY_ROWID +  " DESC", ""+(limit+1) );
			int i=0;
			long rowid = -1;
			if(c.getCount() <= limit ) {
				c.close();
				return true;
			}
			while (c.moveToNext()) {
				i++;
				if (i == limit) {
					rowid = c.getLong(c.getColumnIndex(KEY_ROWID));
					break;
				}
			}
			c.close();
			if(rowid < 0) {
				return mDb.delete(DATABASE_STATUS_TABLE, KEY_ROWTYPE + " = " + rowtype + " and " + KEY_ROWEXTRA + " = '" + extra + "' and " + KEY_ACCOUNT_ID + " = " + _aid , null) > 0 ;
			} else {
				return mDb.delete(DATABASE_STATUS_TABLE, KEY_ROWTYPE + " = " + rowtype + " and " + KEY_ROWEXTRA + " = '" + extra + "' and " + KEY_ACCOUNT_ID + " = " + _aid + " and " + KEY_ROWID + " < " + rowid, null) > 0 ;
			}
		}
		try {
			cc.close();
		} catch (Exception e) {
			
		}
		return true;
	}

	public boolean deleteOlderStatuses(int rowtype,String extra) {
		// I need to get the 25th status:
		int limit = 25;
		Cursor c = mDb.query(true,
				DATABASE_STATUS_TABLE, 
				new String[] {
				KEY_ROWID, KEY_STATUS_ID
			}, 
			KEY_ROWTYPE + " = " + rowtype + " and " + KEY_ROWEXTRA + " = '" + extra + "'", 
			null, null, null, KEY_ROWID +  " DESC", ""+(limit+1) );
		int i=0;
		long rowid = -1;
		int cco = c.getCount();
		if(cco <= limit ) {
			c.close();
			Log.d("Mustard", "Deleting 0 dents because I found " + cco + " rows");
			return true;
		}
		while (c.moveToNext()) {
			i++;
			if (i == limit) {
				rowid = c.getLong(c.getColumnIndex(KEY_ROWID));
				break;
			}
		}
		c.close();
		Log.d("Mustard", "Deleting dents older then " + rowid);
		if(rowid < 0) {
			return mDb.delete(DATABASE_STATUS_TABLE, KEY_ROWTYPE + " = " + rowtype + " and " + KEY_ROWEXTRA + " = '" + extra + "'", null) > 0 ;
		} else {
			int l = mDb.delete(DATABASE_STATUS_TABLE, KEY_ROWTYPE + " = " + rowtype + " and " + KEY_ROWEXTRA + " = '" + extra + "' and " + KEY_ROWID + " < " + rowid, null) ;
			Log.d("Mustard","Deleted " + l + " rows");
			return l > 0 ;
		}
	}
*/	
	public boolean deleteStatuses(long account_id,int rowtype,String extra,long maxId) {
		return mDb.delete(DATABASE_STATUS_TABLE, KEY_ACCOUNT_ID + " = " + account_id + " and " + KEY_ROWTYPE + " = " + rowtype + "' and " + KEY_STATUS_ID + " <= " + maxId , null) > 0 ;
	}
	
	/**
	 * Delete the note with the given rowId
	 * 
	 * @param rowId id of note to delete
	 * @return true if deleted, false otherwise
	 */
	public boolean deleteStatus(long rowId) {
		return mDb.delete(DATABASE_STATUS_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}

	/**
	 * Return a Cursor over the list of all notes in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetchAllStatuses(int rowtype, String extra) {
		return fetchAllStatuses(rowtype, extra, "DESC");
	}

	public Cursor fetchAllStatuses(int rowtype, String extra, String order) {
		Cursor ret = null;
		try {
			if (rowtype == ROWTYPE_ALL) {
				ret = mDb.query(true,DATABASE_STATUS_TABLE, 
						new String[] {
						KEY_ROWID, KEY_ACCOUNT_ID, KEY_STATUS_ID, KEY_STATUS,
						KEY_REPEATED_ID, KEY_SCREEN_NAME, KEY_INSERT_AT, KEY_USER_IMAGE,
						KEY_USER_ID, KEY_ATTACHMENT,
						KEY_NAME,KEY_AD_CODE
				}, 
						null, null, null, null, KEY_INSERT_AT +  " " + order,null);
			} else {
				ret = mDb.query(true,
						DATABASE_STATUS_TABLE, 
						new String[] {
						KEY_ROWID, KEY_ACCOUNT_ID, KEY_STATUS_ID, KEY_STATUS,
						KEY_REPEATED_ID, KEY_SCREEN_NAME, KEY_INSERT_AT,KEY_USER_IMAGE,
						KEY_USER_ID, KEY_ATTACHMENT,
						KEY_NAME,KEY_AD_CODE

					}, 
					//KEY_ROWTYPE + " = " + rowtype + " and " + KEY_ROWEXTRA + " = '" + extra + "'", null, null, null, KEY_INSERT_AT +  " " + order,null);
					KEY_ROWTYPE + " = " + rowtype , null, null, null, KEY_INSERT_AT +  " " + order,null);
			}
		} catch (Exception e) {
//			if (MustardApplication.DEBUG) Log.e(TAG,e.toString());
		}
		return ret;
	}
	
	
	//add by fancuiru at 2013-12-13 for show attachment
	public ArrayList<String> getATTACHMENT(long dent_id) {
	Cursor cursor=	mDb.query(true, DATABASE_ATTACHMENTS_TABLE,new String[] {KEY_URL}," dent_id = ?", new String[] {""+dent_id}, null,null ,null, null);
		ArrayList<String> urlList=new ArrayList<String>();
		String url = null;
		if(cursor.moveToNext()){
			 url= cursor.getString(cursor.getColumnIndex(MustardDbAdapter.KEY_URL));
		}
		if(null != url){
			urlList.add(url);
		}
		cursor.close();
		return urlList;
		
	}
	/**
	 * Return a Cursor positioned at the note that matches the given rowId
	 * 
	 * @param rowId id of note to retrieve
	 * @return Cursor positioned to matching note, if found
	 * @throws SQLException if note could not be found/retrieved
	 */
	public Cursor fetchStatus(long rowId) throws SQLException {

		Cursor mCursor =
			mDb.query(DATABASE_STATUS_TABLE, 
					new String[] {
					KEY_ROWID, 
					KEY_ACCOUNT_ID,
					KEY_STATUS_ID, 
					KEY_STATUS,
					//KEY_STATUS_HTML,
					//KEY_LOCATION,
					//KEY_IN_REPLY_TO,
					//KEY_IN_REPLY_TO_SCREEN_NAME,
					KEY_REPEATED_ID,
					//KEY_REPEATED_BY_SCREEN_NAME,
					//KEY_SOURCE,
					KEY_SCREEN_NAME, 
					KEY_INSERT_AT, 
					KEY_USER_IMAGE ,
					//KEY_USER_URL,
					//KEY_FAVORITE, 
					//KEY_FOLLOWING,
					//KEY_BLOCKING,
					KEY_USER_ID,
					KEY_AD_CODE,
					KEY_ATTACHMENT
					//KEY_GEO, KEY_LAT, KEY_LON
					//, KEY_NOTICE_TYPE,
					//KEY_USER_IMAGE_LARGE,
					//KEY_REPLY_COUNT
			}, 
			KEY_ROWID + "=" + rowId, null,
			null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public long createAccount(
			long userid,
			String user, 
			String password, 
			String instance, 
			int isDefault, 
			String version) {
		resetDefaultAccounts();
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_USER_ID, userid);
		initialValues.put(KEY_USER, user);
		initialValues.put(KEY_PASSWORD, password);
		initialValues.put(KEY_INSTANCE, instance);
		initialValues.put(KEY_DEFAULT, isDefault);
		initialValues.put(KEY_VERSION, version);
		initialValues.put(KEY_TO_MERGE,isDefault);
		return mDb.insertOrThrow(DATABASE_ACCOUNT_TABLE, null, initialValues);
	}
	
	public long createAccount(long userid, String user, String token, String token_secret, String instance, int isDefault, String version) {
		resetDefaultAccounts();
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_USER_ID, userid);
		initialValues.put(KEY_USER, user);
		initialValues.put(KEY_PASSWORD, "XXX");
		initialValues.put(KEY_TOKEN, token);
		initialValues.put(KEY_TOKEN_SECRET, token_secret);
		initialValues.put(KEY_INSTANCE, instance);
		initialValues.put(KEY_DEFAULT, isDefault);
		initialValues.put(KEY_VERSION, version);
		initialValues.put(KEY_TO_MERGE,isDefault);
		return mDb.insertOrThrow(DATABASE_ACCOUNT_TABLE, null, initialValues);
	}

	public boolean updateAccount(String newpassword) {
		ContentValues args = new ContentValues();
		args.put(KEY_PASSWORD, newpassword);
		Log.v(TAG,"newpassword = " + newpassword);
        return mDb.update(DATABASE_ACCOUNT_TABLE, args, KEY_DEFAULT + " = 1 ", null) > 0;
	}
	
	public boolean updateAccount(String username, String token, String token_secret, String instance, int isDefault, String version) {
		ContentValues args = new ContentValues();
		args.put(KEY_PASSWORD, "XXX");
		args.put(KEY_TOKEN, token);
		args.put(KEY_TOKEN_SECRET, token_secret);
		args.put(KEY_DEFAULT, isDefault);
		args.put(KEY_VERSION, version);
		args.put(KEY_TO_MERGE,isDefault);
        return mDb.update(DATABASE_ACCOUNT_TABLE, args, KEY_USER + " = '" + username + "' AND " + KEY_INSTANCE + " = '" + instance + "'", null) > 0;
	}

    public boolean updateAdcodeByStatusId(long statusid, String newcode) {
		return updateAdcodeByStatusId(Long.toString(statusid), newcode);
	}

	public boolean updateAdcodeByStatusId(String statusid, String newcode){
		ContentValues args = new ContentValues();
        args.put(KEY_AD_CODE, newcode);
        return mDb.update(DATABASE_STATUS_TABLE, args, KEY_STATUS_ID + " = ? ",new String[] { statusid}) > 0;
        //return true;	
	}

	public Cursor fetchAllAccounts() {
		return mDb.query(DATABASE_ACCOUNT_TABLE, new String[] {KEY_ROWID, KEY_USER, KEY_PASSWORD, KEY_TOKEN, KEY_TOKEN_SECRET, KEY_INSTANCE,KEY_DEFAULT, KEY_VERSION, KEY_TO_MERGE}, null, null, null, null, null);
	}
	
	public Cursor fetchAllAccountsToMerge() {
			return mDb.query(DATABASE_ACCOUNT_TABLE, 
						new String[] {
							KEY_ROWID, KEY_USER, KEY_USER_ID, KEY_PASSWORD, 
							KEY_TOKEN, KEY_TOKEN_SECRET, KEY_INSTANCE,KEY_DEFAULT,
							KEY_VERSION, KEY_TO_MERGE}, 
						KEY_TO_MERGE + " = 1 ", 
						null, null, null, null);
		}
	
	public Cursor fetchAllAccountsDefaultFirst() {
		return mDb.rawQuery("select " + KEY_ROWID + ", " + KEY_USER + "||'@'||replace(replace("+KEY_INSTANCE +",'http://',''),'https://','') " + KEY_USER + 
				", " + KEY_TEXTLIMIT + ", " + KEY_TO_MERGE +
				" from " + DATABASE_ACCOUNT_TABLE + 
				" order by " + KEY_DEFAULT + " desc",null);
	}
	
	public Cursor fetchAllNonDefaultAccounts() {
		return mDb.query(DATABASE_ACCOUNT_TABLE, 
					new String[] {
							KEY_ROWID, KEY_USER, KEY_USER_ID, KEY_PASSWORD, 
							KEY_TOKEN, KEY_TOKEN_SECRET, KEY_INSTANCE,KEY_DEFAULT,
							KEY_VERSION, KEY_TO_MERGE}, 
					KEY_DEFAULT + " = 0 ", 
					null, null, null, null);
	}
	
	public Cursor fetchAccount(long rowId) {
		Cursor mCursor = mDb.query(DATABASE_ACCOUNT_TABLE, 
				new String[] {
					KEY_ROWID, 
					KEY_USER,
					KEY_USER_ID,
					KEY_PASSWORD, 
					KEY_TOKEN,
					KEY_TOKEN_SECRET, 
					KEY_INSTANCE,
					KEY_DEFAULT,
					KEY_VERSION,
					KEY_TEXTLIMIT,
					KEY_ATTACHLIMIT
					}, 
				KEY_ROWID + "=" + rowId, 
				null, null, null, null);
		return mCursor;
	}
	
	public Cursor fetchDefaultAccount() {
		Cursor mCursor = mDb.query(DATABASE_ACCOUNT_TABLE, 
				new String[] {
					KEY_ROWID, 
					KEY_USER, 
					KEY_USER_ID,
					KEY_PASSWORD, 
					KEY_TOKEN, 
					KEY_TOKEN_SECRET, 
					KEY_INSTANCE, 
					KEY_DEFAULT, 
					KEY_VERSION,
					KEY_TEXTLIMIT,
					KEY_ATTACHLIMIT
					}, 
				KEY_DEFAULT + "= 1" , null,
				null, null, null);
		Log.v(TAG,"mCursor.getCount() =" + mCursor.getCount());
		if (mCursor.getCount()<1) {
			// Try ...
			Log.e(TAG,"mCursor.getCount()<1" );
			if(resetDefaultAccount())
				return fetchDefaultAccount();
		}
		return mCursor;
	}
	
	public boolean resetDefaultAccounts() {
		ContentValues args = new ContentValues();
        args = new ContentValues();
        args.put(KEY_DEFAULT, 0);
        return mDb.update(DATABASE_ACCOUNT_TABLE, args, null, null) > 0;
	}
	
	public boolean resetMergedAccounts() {
		ContentValues args = new ContentValues();
        args.put(KEY_TO_MERGE, 0);
        return mDb.update(DATABASE_ACCOUNT_TABLE, args, null, null) > 0;
	}
	
	public boolean setUserIdAccount(long _id, long userid) {
		ContentValues args = new ContentValues();
        args.put(KEY_USER_ID, userid);
        return mDb.update(DATABASE_ACCOUNT_TABLE, args, KEY_ROWID + " = " + _id, null) > 0;
	}
	
	public boolean setDefaultAccount(long _id) {
		ContentValues args = new ContentValues();
        args.put(KEY_DEFAULT, 1);
        args.put(KEY_TO_MERGE, 1);
        return mDb.update(DATABASE_ACCOUNT_TABLE, args, KEY_ROWID + " = " + _id, null) > 0;
	}
	
	public boolean setMergedAccount(long _id, boolean merged) {
		ContentValues args = new ContentValues();
        args.put(KEY_TO_MERGE, merged ? 1 : 0);
        return mDb.update(DATABASE_ACCOUNT_TABLE, args, KEY_ROWID + " = " + _id, null) > 0;
	}

	public boolean setVersionAccount(long _id,String version) {
		ContentValues args = new ContentValues();
        args.put(KEY_VERSION, version);
        return mDb.update(DATABASE_ACCOUNT_TABLE, args, KEY_ROWID + " = " + _id, null) > 0;
	}

	public boolean setVersionInstance(String instance,String version) {
		ContentValues args = new ContentValues();
        args.put(KEY_VERSION, version);
        return mDb.update(DATABASE_ACCOUNT_TABLE, args, KEY_INSTANCE + " = '" + instance + "'", null) > 0;
	}

	public boolean setTextlimitInstance(String instance,int textlimit) {
		ContentValues args = new ContentValues();
        args.put(KEY_TEXTLIMIT, textlimit);
        return mDb.update(DATABASE_ACCOUNT_TABLE, args, KEY_INSTANCE + " = '" + instance + "'", null) > 0;
	}
	
	public boolean setAttachmentLimitInstance(String instance,long attachmentlimit) {
		ContentValues args = new ContentValues();
        args.put(KEY_ATTACHLIMIT, attachmentlimit);
        return mDb.update(DATABASE_ACCOUNT_TABLE, args, KEY_INSTANCE + " = '" + instance + "'", null) > 0;
	}
	
	public boolean setAttachmentTextLimitInstance(String instance,int textlimit, long attachmentlimit) {
		ContentValues args = new ContentValues();
		args.put(KEY_TEXTLIMIT, textlimit);
        args.put(KEY_ATTACHLIMIT, attachmentlimit);
        return mDb.update(DATABASE_ACCOUNT_TABLE, args, KEY_INSTANCE + " = '" + instance + "'", null) > 0;
	}
	
	public long fetchMaxStatusesId(long account_id, int rowtype, String extra) {
		long ret=-1;
		//Cursor mCursor = mDb.query(DATABASE_STATUS_TABLE, new String[] { "MAX("+KEY_REPEATED_ID+")" },
		Cursor mCursor = mDb.query(DATABASE_STATUS_TABLE, new String[] { "MAX("+KEY_STATUS_ID+")" },
			KEY_ACCOUNT_ID + " = " + account_id + " and " + KEY_ROWTYPE + " = " + rowtype ,
			null,
			null, null, null, null);
		if (mCursor != null) {
			if(mCursor.moveToFirst())
				ret = mCursor.getLong(0);
			mCursor.close();
			
		}
		return ret;
	}
	
	public long fetchMinStatusesId(long account_id, int rowtype, String extra) {
		long ret=-1;
		//Cursor cursor = mDb.query(DATABASE_STATUS_TABLE, new String[] { "MIN("+KEY_REPEATED_ID+")" }, 
		Cursor cursor = mDb.query(DATABASE_STATUS_TABLE, new String[] { "MIN("+KEY_STATUS_ID+")" }, 
			KEY_ACCOUNT_ID + " = " + account_id + " and " + KEY_ROWTYPE + " = " + rowtype,
			null,
			null, null, null, null);
		if (cursor != null) {
			if(cursor.moveToFirst())
				ret = cursor.getLong(0);
			cursor.close();
			
		}
		return ret;
	}
	
	public boolean deleteAccount(long userId) {
		mDb.delete(DATABASE_ACCOUNT_TABLE, KEY_ROWID + "= " + userId , null);
		resetDefaultAccount();
		return true;
	}
/*	
	public boolean deleteBookmarks(long userId) {
		mDb.delete(DATABASE_BOOKMARK_TABLE, KEY_USER_ID + " = " + userId , null);
		return true;
	}
*/
	public boolean resetDefaultAccount() {
		boolean ret = false;
		Cursor cursor =
			mDb.query(DATABASE_ACCOUNT_TABLE, 
					new String[] { "MAX("+KEY_ROWID+")" 
			}, 
			null,
			null,
			null, null, null, null);
		if (cursor != null) {
			if(cursor.moveToFirst()) {
				Long _id = cursor.getLong(0);
				if (_id!=null && _id>0) {
					if(setDefaultAccount(_id))
						ret=true;
				}
			}
			cursor.close();
		}
		return ret;
	}

	public long userExists(String username,String instance) {
		long ret = -1;
		Cursor cursor = mDb.query(DATABASE_ACCOUNT_TABLE, 
				new String[] {KEY_ROWID}, 
				KEY_USER + " = '" + username + "' AND " + KEY_INSTANCE + " = '" + instance + "'", 
				null, null, null, null);
		if(cursor.moveToFirst()) {
			ret = cursor.getLong(cursor.getColumnIndex(KEY_ROWID));
		}
		try {
			cursor.close();
		}catch(Exception e){
		}
		return ret;
	}
/*	
	public Cursor fetchUserBookmarksByType(long userid,int btype) {
		return mDb.query(true,
				DATABASE_BOOKMARK_TABLE, 
				new String[] {
				KEY_ROWID, KEY_BPARAM
			},
			KEY_USER_ID + " = " + userid + " and " + KEY_BTYPE + " = " + btype , 
			null, null, null, null, null);
	}
	
	public String fetchBookmark(long id) {
		String param="";
		Cursor mCursor = null;
		try {
			mCursor = mDb.query(DATABASE_BOOKMARK_TABLE, 
					new String[] { KEY_BPARAM, KEY_BTYPE },
			KEY_ROWID + " = " + id,
			null,
			null, null, KEY_COUNTER + ", " + KEY_BPARAM, null);
			if (mCursor != null) {
				if(mCursor.moveToFirst())
					param = mCursor.getString(0);
			}
		} finally {
			mCursor.close();
		}
		return param;
	}
	
	public boolean createBookmark(long userid,int btype, String bparam) throws MustardException {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_USER_ID, userid);
		initialValues.put(KEY_BTYPE, btype);
		initialValues.put(KEY_BPARAM, bparam);
		initialValues.put(KEY_COUNTER, 0);
		try {
			mDb.insertOrThrow(DATABASE_BOOKMARK_TABLE, null, initialValues);
		} catch(SQLiteConstraintException e) {
			throw new MustardException(e.getMessage() == null ? e.toString() : e.getMessage());
		}
		return true;
	}

	public boolean increaseBookmarkCounter(long id) {
		try {
			mDb.execSQL("update bookmarks set counter = counter +1 where _id = " + id);
		} catch(Exception e) {
			return false;
		}
		return true;
	}

	public boolean deleteBookmark(long id) {
		try {
			mDb.delete(DATABASE_BOOKMARK_TABLE, KEY_ROWID + "= " + id , null);
		} catch(Exception e) {
			return false;
		}
		return true;
	}
	
	
	public Cursor fetchFilters() {
		Cursor mCursor = mDb.query(DATABASE_FILTER_TABLE, 
					new String[] { KEY_ROWID, KEY_FILTER },
			null,
			null,
			null, null, null, null);
		return mCursor;
	}
	
	public boolean createFilter(String filter) throws MustardException {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_FILTER, filter);
		try {
			mDb.insertOrThrow(DATABASE_FILTER_TABLE, null, initialValues);
		} catch(SQLiteConstraintException e) {
			throw new MustardException(e.getMessage() == null ? e.toString() : e.getMessage());
		}
		return true;
	}

	public boolean deleteFilter(long id) {
		try {
			mDb.delete(DATABASE_FILTER_TABLE, KEY_ROWID + "= " + id , null);
		} catch(Exception e) {
			return false;
		}
		return true;
	}
	
	public boolean insertOauth(String instance, String key, String secret,boolean reserved) {
		Cursor c = fetchOauth(instance);
		if (c.moveToFirst()) {
			int iReserved = c.getInt(c.getColumnIndex(KEY_RESERVED));
			if (iReserved == 1 && !reserved) {
				c.close();
				return false;
			}
			String localKey=c.getString(c.getColumnIndex(KEY_KEY));
			String localSecret=c.getString(c.getColumnIndex(KEY_SECRET));
			if (localKey.equals(key) && localSecret.equals(secret)) {
				c.close();
				return true;
			}
		}
		c.close();
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_INSTANCE, instance);
		initialValues.put(KEY_KEY, key);
		initialValues.put(KEY_SECRET, secret);
		initialValues.put(KEY_RESERVED, (reserved ? 1 : 0));
		long _rid = -1;
		try {
			if (mDb == null) {
				Log.d(TAG, "mDb is null!");
				return false;
			}
			_rid  = mDb.insertOrThrow(DATABASE_OAUTH_TABLE, null, initialValues);
		} catch(SQLiteConstraintException e) {
			// No problem, update
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		if (_rid < 0) {
			return updateOauth(instance, key, secret);
		} else {
			return true;
		}
	}
	
	public boolean updateOauth(String instance, String key, String secret) {
		ContentValues args = new ContentValues();
        args.put(KEY_KEY, key);
        args.put(KEY_SECRET, secret);
        mDb.update(DATABASE_OAUTH_TABLE, args, KEY_INSTANCE + "= '" + instance + "'", null);
        return true;
	}
	
	public boolean deleteOauth(long id) {
		try {
			mDb.delete(DATABASE_OAUTH_TABLE, KEY_ROWID + " = " + id , null);
		} catch(Exception e) {
			return false;
		}
		return true;
	}
	
	public boolean deleteAllOauth() {
		try {
			mDb.delete(DATABASE_OAUTH_TABLE, null, null);
		} catch(Exception e) {
			return false;
		}
		return true;
	}
	
	public Cursor fetchOauth(long id) {
		Cursor mCursor = mDb.query(
				DATABASE_OAUTH_TABLE, 
				new String[] {
						KEY_ROWID, KEY_INSTANCE, KEY_KEY, KEY_SECRET, KEY_RESERVED
						},
				KEY_ROWID + "=" + id, 
				null, null, null, null);
		return mCursor;
	}
	
	public Cursor fetchOauth(String instance) {
		Cursor mCursor = mDb.query(
				DATABASE_OAUTH_TABLE, 
				new String[] {
						KEY_ROWID, KEY_INSTANCE, KEY_KEY, KEY_SECRET, KEY_RESERVED
						},
						KEY_INSTANCE + "= '" + instance + "'", 
				null, null, null, null);
		return mCursor;
	}
*/	


	public boolean changeAvatar(long user_id,String avatar_url) {
		ContentValues values = new ContentValues();
		Log.v("fcr",KEY_USER_IMAGE + avatar_url + KEY_USER_ID  + user_id);
		values.put(KEY_USER_IMAGE, avatar_url);
	   return mDb.update(DATABASE_STATUS_TABLE, values, KEY_USER_ID+"=?", new String[]{""+ user_id})>0;
	}
/*		
	public boolean insertAvatarLarge(long user_id,String avatar_url_large) {
		ContentValues values = new ContentValues();
		Log.v("fancuiru",KEY_USER_IMAGE_LARGE + avatar_url_large + KEY_USER_ID  + user_id);
		values.put(KEY_USER_IMAGE_LARGE, avatar_url_large);
	   return mDb.update(DATABASE_STATUS_TABLE, values, KEY_USER_ID+"=?", new String[]{""+ user_id})>0;
	}

	public boolean deleteMessage(String status_id) {
		Log.v("fcr","deleteMessage" + status_id);
		return mDb.delete(DATABASE_STATUS_TABLE, KEY_STATUS_ID + "= ?", new String[]{""+status_id})>0;
	}
*/
}
