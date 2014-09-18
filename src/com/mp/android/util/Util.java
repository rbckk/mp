package com.mp.android.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;

import com.mp.android.BaseApplication;
import com.mp.android.R;
import com.mp.android.RenRenData;
import com.mp.android.statusnet.MustardDbAdapter;
import com.mp.android.statusnet.Preferences;
import com.mp.android.statusnet.provider.StatusNet;
import com.mp.android.statusnet.statusnet.RowStatus;

/**
 * l�����繤����
 * 
 * @author rendongwei
 * 
 */
public class Util {
    /**
     * ��ȡJson���
     * 
     * @param paramMap
     *            �����෵�صĲ������
     * @return Json���
     */
    public static String GetJson(Map<String, String> paramMap) {
        String result = null;
/*        HttpPost httpRequest = new HttpPost(RenRen.APIURL);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> param : paramMap.entrySet()) {
            params.add(new BasicNameValuePair(param.getKey(), param.getValue()));
        }
        try {
            HttpEntity httpEntity = new UrlEncodedFormEntity(params, "UTF-8");
            httpRequest.setEntity(httpEntity);
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(httpResponse.getEntity());
                return result;
            }
        } catch (Exception e) {
            return null;
        }*/
        return null;
    }

    public static MustardDbAdapter getDbAdapter(Context context) {
        MustardDbAdapter dbAdapter = new MustardDbAdapter(context);
        dbAdapter.open();
        return dbAdapter;
    }

    public static StatusNet getStatusNet(Context context,
            BaseApplication mApplication, long mStatusNetAccountId) {
        MustardDbAdapter mDbHelper = getDbAdapter(context);
        StatusNet mStatusNet = null;
        if (mStatusNetAccountId >= 0) {
            mStatusNet = mApplication.checkAccount(mDbHelper,
                    mStatusNetAccountId);
        } else {
            mStatusNet = mApplication.checkAccount(mDbHelper);
        }
        mDbHelper.close();
        return mStatusNet;
    }

    public static ArrayList<RowStatus> selectDataArray(int mRowType) {
        switch (mRowType) {
        case MustardDbAdapter.ROWTYPE_PUBLIC:
            return RenRenData.mPublicTimelineStatus;
        case MustardDbAdapter.ROWTYPE_FRIENDS:
            return RenRenData.mFriendlineStatus;
        default:
            break;
        }
        return null;
    }

    public static StatusNet getStatusNetFromRowStatus(RowStatus rs,
            MustardDbAdapter dbHelper, BaseApplication application) {
        StatusNet sn = null;
        long aid = rs.getAccountId();
        if (aid > 0) {
            sn = application.checkAccount(dbHelper, false, aid);
        }
        return sn;
    }

    public static StatusNet getStatusNetFromRowid(Context context, long rowid,
            BaseApplication application) {
        StatusNet sn = null;
        MustardDbAdapter dbHelper = getDbAdapter(context);
        sn = getStatusNetFromRowid(rowid, dbHelper, application);
        dbHelper.close();
        return sn;
    }

    protected static StatusNet getStatusNetFromRowid(long rowid,
            MustardDbAdapter dbHelper, BaseApplication application) {
        StatusNet sn = null;
        RowStatus rs = getRowStatus(rowid, dbHelper);
        long aid = rs.getAccountId();
        if (aid > 0) {
            sn = application.checkAccount(dbHelper, false, aid);
        }
        return sn;
    }

    public static RowStatus getRowStatus(long rowid, MustardDbAdapter dbHelper) {
        RowStatus rs = new RowStatus();
        Cursor c = dbHelper.fetchStatus(rowid);
        int mIdIdx = c.getColumnIndex(MustardDbAdapter.KEY_ROWID);
        int mIdAccountIdx = c.getColumnIndex(MustardDbAdapter.KEY_ACCOUNT_ID);
        //int mGeolocationIdx = c.getColumnIndex(MustardDbAdapter.KEY_GEO);
        int mScreenNameIdx = c.getColumnIndex(MustardDbAdapter.KEY_SCREEN_NAME);
        int mIdUserIdx = c.getColumnIndex(MustardDbAdapter.KEY_USER_ID);
        int mStatusIdx = c.getColumnIndex(MustardDbAdapter.KEY_STATUS);
       // int mStatusHtmlIdx = c.getColumnIndex(MustardDbAdapter.KEY_STATUS_HTML);
       // int mLocationIdx = c.getColumnIndex(MustardDbAdapter.KEY_LOCATION);
        //int mFavaoritedIdx = c.getColumnIndex(MustardDbAdapter.KEY_FAVORITE);
        int mStatusIdIdx = c.getColumnIndex(MustardDbAdapter.KEY_STATUS_ID);
        int mDatetimeIdx = c.getColumnIndex(MustardDbAdapter.KEY_INSERT_AT);
        //int mSourceIdx = c.getColumnIndex(MustardDbAdapter.KEY_SOURCE);
        //int mInReplyToIdx = c.getColumnIndex(MustardDbAdapter.KEY_IN_REPLY_TO);
        //int mInReplyToScreenNameIdx = c
        //        .getColumnIndex(MustardDbAdapter.KEY_IN_REPLY_TO_SCREEN_NAME);
        int mProfileImageIdx = c.getColumnIndex(MustardDbAdapter.KEY_USER_IMAGE);
        //int mProfileUrlIdx = c.getColumnIndex(MustardDbAdapter.KEY_USER_URL);
        //int mLonIdx = c.getColumnIndex(MustardDbAdapter.KEY_LON);
        //int mLatIdx = c.getColumnIndex(MustardDbAdapter.KEY_LAT);
        int mAttachmentIdx = c.getColumnIndex(MustardDbAdapter.KEY_ATTACHMENT);
        int madcodeIdx = c.getColumnIndex(MustardDbAdapter.KEY_AD_CODE);
        //int mReplyCountIdx = c.getColumnIndex(MustardDbAdapter.KEY_REPLY_COUNT);
        rs.setId(c.getLong(mIdIdx));
        rs.setStatusId(c.getLong(mStatusIdIdx));
        rs.setAccountId(c.getLong(mIdAccountIdx));
        rs.setScreenName(c.getString(mScreenNameIdx));
        rs.setUserId(c.getLong(mIdUserIdx));
        //rs.setSource(c.getString(mSourceIdx));
        //rs.setInReplyTo(c.getLong(mInReplyToIdx));
        //rs.setInReplyToScreenName(c.getString(mInReplyToScreenNameIdx));
       // rs.setProfileImage(c.getString(mProfileImageIdx));
       // rs.setProfileUrl(c.getString(mProfileUrlIdx));
        rs.setDateTime(c.getLong(mDatetimeIdx));
        //rs.setGeolocation(c.getInt(mGeolocationIdx));
        //rs.setLon(c.getString(mLonIdx));
        //rs.setLat(c.getString(mLatIdx));
        rs.setAttachment(c.getInt(mAttachmentIdx));
        rs.setStatus(c.getString(mStatusIdx));
        //rs.setStatusHtml(c.getString(mStatusHtmlIdx));
        //rs.setLocation(c.getString(mLocationIdx));
        //rs.setFavorited(c.getInt(mFavaoritedIdx));
        //rs.setReplyCount(c.getInt(mReplyCountIdx));
        rs.setadcode(c.getString(madcodeIdx));
        c.close();
        return rs;
    }

    public static void fillData(Context context, int DB_ROW_TYPE,
            String DB_ROW_EXTRA) {
        MustardDbAdapter mDbHelper = getDbAdapter(context);
        Cursor cursor = null;
        try {
            cursor = mDbHelper.fetchAllStatuses(DB_ROW_TYPE, DB_ROW_EXTRA,
                    "DESC");
            if (cursor == null) {
                mDbHelper.close();
                return;
            }
            ArrayList<RowStatus> status = new ArrayList<RowStatus>();
            status.clear();
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(context);
            boolean ignoreHideUsers = prefs.getBoolean(
                    Preferences.IGNORE_HIDE_USERS, false);
            int mIdIdx = cursor.getColumnIndex(MustardDbAdapter.KEY_ROWID);
            int mUserId = cursor.getColumnIndex(MustardDbAdapter.KEY_USER_ID);
            int mIdAccountIdx = cursor
                    .getColumnIndex(MustardDbAdapter.KEY_ACCOUNT_ID);
            //int mGeolocationIdx = cursor
            //        .getColumnIndex(MustardDbAdapter.KEY_GEO);
            int mScreenNameIdx = cursor
                    .getColumnIndex(MustardDbAdapter.KEY_SCREEN_NAME);
            int mStatusIdx = cursor.getColumnIndex(MustardDbAdapter.KEY_STATUS);
            //int mStatusHtmlIdx = cursor
            //        .getColumnIndex(MustardDbAdapter.KEY_STATUS_HTML);
            //int mLocationIdx = cursor
            //		.getColumnIndex(MustardDbAdapter.KEY_LOCATION);
            //int mFavaoritedIdx = cursor.getColumnIndex(MustardDbAdapter.KEY_FAVORITE);
            int mStatusIdIdx = cursor
                    .getColumnIndex(MustardDbAdapter.KEY_STATUS_ID);
            int mDatetimeIdx = cursor
                    .getColumnIndex(MustardDbAdapter.KEY_INSERT_AT);
            //int mSourceIdx = cursor.getColumnIndex(MustardDbAdapter.KEY_SOURCE);
            //int mInReplyToIdx = cursor
            //        .getColumnIndex(MustardDbAdapter.KEY_IN_REPLY_TO);
            //int mInReplyToScreenNameIdx = cursor
            //        .getColumnIndex(MustardDbAdapter.KEY_IN_REPLY_TO_SCREEN_NAME);
            int mRepeatedIdIdx = cursor
                    .getColumnIndex(MustardDbAdapter.KEY_REPEATED_ID);
            //int mRepeatedByScreenNameIdx = cursor
            //        .getColumnIndex(MustardDbAdapter.KEY_REPEATED_BY_SCREEN_NAME);
            int mProfileImageIdx = cursor.getColumnIndex(MustardDbAdapter.KEY_USER_IMAGE);
            //int mProfileUrlIdx = cursor
            //        .getColumnIndex(MustardDbAdapter.KEY_USER_URL);
            //int mLonIdx = cursor.getColumnIndex(MustardDbAdapter.KEY_LON);
            //int mLatIdx = cursor.getColumnIndex(MustardDbAdapter.KEY_LAT);
            int mAttachmentIdx = cursor.getColumnIndex(MustardDbAdapter.KEY_ATTACHMENT);
            int madcodeIdx = cursor.getColumnIndex(MustardDbAdapter.KEY_AD_CODE);
            //int mReplyCountIdx = cursor
            //        .getColumnIndex(MustardDbAdapter.KEY_REPLY_COUNT);
            System.out.println("cursor.getCount() = " + cursor.getCount());
            while (cursor.moveToNext()) {
                long userId = cursor.getLong(mUserId);
                if (!(isHidden(context, userId) && ignoreHideUsers)) {
                    RowStatus rs = new RowStatus();
                    rs.setId(cursor.getLong(mIdIdx));
                    rs.setStatusId(cursor.getLong(mStatusIdIdx));
                    rs.setAccountId(cursor.getLong(mIdAccountIdx));
                    rs.setScreenName(cursor.getString(mScreenNameIdx));
                    //rs.setSource(cursor.getString(mSourceIdx));
                    //rs.setInReplyTo(cursor.getLong(mInReplyToIdx));
                    //rs.setInReplyToScreenName(cursor
                    //        .getString(mInReplyToScreenNameIdx));
                    rs.setRepeatedId(cursor.getLong(mRepeatedIdIdx));
                    //rs.setRepeatedByScreenName(cursor
                    //        .getString(mRepeatedByScreenNameIdx));
                    //rs.setProfileImage(cursor.getString(mProfileImageIdx));
                    //rs.setProfileUrl(cursor.getString(mProfileUrlIdx));
                    rs.setDateTime(cursor.getLong(mDatetimeIdx));
                    //rs.setGeolocation(cursor.getInt(mGeolocationIdx));
                    //rs.setLon(cursor.getString(mLonIdx));
                    //rs.setLat(cursor.getString(mLatIdx));
                    rs.setAttachment(cursor.getInt(mAttachmentIdx));
                    rs.setStatus(cursor.getString(mStatusIdx));
                    //rs.setStatusHtml(cursor.getString(mStatusHtmlIdx));
                    //rs.setLocation(cursor.getString(mLocationIdx));
                    //rs.setFavorited(cursor.getInt(mFavaoritedIdx));
                    //rs.setReplyCount(cursor.getInt(mReplyCountIdx));
                    rs.setadcode(cursor.getString(madcodeIdx));
                    status.add(rs);
                }
            }
        } finally {
            try {
                cursor.close();
            } catch (Exception e) {
            }
            mDbHelper.close();
        }

    }

    protected static boolean isHidden(Context context, long account) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String key = Preferences.HIDE_USERS + "." + account;
        boolean v = prefs.getBoolean(key, false);
        return v;
    }

    public static ArrayList<RowStatus> getData(Context context,
            int DB_ROW_TYPE, String DB_ROW_EXTRA) {
        MustardDbAdapter mDbHelper = getDbAdapter(context);
        Cursor cursor = null;
        ArrayList<RowStatus> status = new ArrayList<RowStatus>();
        try {
            cursor = mDbHelper.fetchAllStatuses(DB_ROW_TYPE, DB_ROW_EXTRA,
                    "DESC");
            if (cursor == null) {
                mDbHelper.close();
                return status;
            }

            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(context);
            boolean ignoreHideUsers = prefs.getBoolean(
                    Preferences.IGNORE_HIDE_USERS, false);
            int mIdIdx = cursor.getColumnIndex(MustardDbAdapter.KEY_ROWID);
            int mUserId = cursor.getColumnIndex(MustardDbAdapter.KEY_USER_ID);
            int mIdAccountIdx = cursor
                    .getColumnIndex(MustardDbAdapter.KEY_ACCOUNT_ID);
            //int mGeolocationIdx = cursor
            //        .getColumnIndex(MustardDbAdapter.KEY_GEO);
            int mScreenNameIdx = cursor
                    .getColumnIndex(MustardDbAdapter.KEY_SCREEN_NAME);
            int mStatusIdx = cursor.getColumnIndex(MustardDbAdapter.KEY_STATUS);
            //int mStatusHtmlIdx = cursor
            //        .getColumnIndex(MustardDbAdapter.KEY_STATUS_HTML);
            //int mLocationIdx = cursor
            //		.getColumnIndex(MustardDbAdapter.KEY_LOCATION);
            //int mFavaoritedIdx = cursor.getColumnIndex(MustardDbAdapter.KEY_FAVORITE);
            int mStatusIdIdx = cursor
                    .getColumnIndex(MustardDbAdapter.KEY_STATUS_ID);
            int mDatetimeIdx = cursor
                    .getColumnIndex(MustardDbAdapter.KEY_INSERT_AT);
            //int mSourceIdx = cursor.getColumnIndex(MustardDbAdapter.KEY_SOURCE);
            //int mInReplyToIdx = cursor
            //        .getColumnIndex(MustardDbAdapter.KEY_IN_REPLY_TO);
            //int mInReplyToScreenNameIdx = cursor
            //        .getColumnIndex(MustardDbAdapter.KEY_IN_REPLY_TO_SCREEN_NAME);
            int mRepeatedIdIdx = cursor
                    .getColumnIndex(MustardDbAdapter.KEY_REPEATED_ID);
            //int mRepeatedByScreenNameIdx = cursor
            //        .getColumnIndex(MustardDbAdapter.KEY_REPEATED_BY_SCREEN_NAME);
            int mProfileImageIdx = cursor.getColumnIndex(MustardDbAdapter.KEY_USER_IMAGE);
            //int mProfileUrlIdx = cursor
            //        .getColumnIndex(MustardDbAdapter.KEY_USER_URL);
            //int mLonIdx = cursor.getColumnIndex(MustardDbAdapter.KEY_LON);
            //int mLatIdx = cursor.getColumnIndex(MustardDbAdapter.KEY_LAT);
            int mAttachmentIdx = cursor
                    .getColumnIndex(MustardDbAdapter.KEY_ATTACHMENT);
            //int mNoticeType = cursor
            //        .getColumnIndex(MustardDbAdapter.KEY_NOTICE_TYPE);
            int mNameIdx = cursor.getColumnIndex(MustardDbAdapter.KEY_NAME);
            int madcodeIdx = cursor.getColumnIndex(MustardDbAdapter.KEY_AD_CODE);
            //int mReplyCountIdx = cursor
            //        .getColumnIndex(MustardDbAdapter.KEY_REPLY_COUNT);
            while (cursor.moveToNext()) {
                long userId = cursor.getLong(mUserId);
                if (!(isHidden(context, userId) && ignoreHideUsers)) {
                    RowStatus rs = new RowStatus();
                    rs.setId(cursor.getLong(mIdIdx));
                    rs.setStatusId(cursor.getLong(mStatusIdIdx));
                    rs.setAccountId(cursor.getLong(mIdAccountIdx));
                    rs.setScreenName(cursor.getString(mScreenNameIdx));
                    //rs.setSource(cursor.getString(mSourceIdx));
                    //rs.setInReplyTo(cursor.getLong(mInReplyToIdx));
                    //rs.setInReplyToScreenName(cursor
                    //        .getString(mInReplyToScreenNameIdx));
                    rs.setRepeatedId(cursor.getLong(mRepeatedIdIdx));
                    //rs.setRepeatedByScreenName(cursor
                    //        .getString(mRepeatedByScreenNameIdx));
                    //rs.setProfileImage(cursor.getString(mProfileImageIdx));
                    //rs.setProfileUrl(cursor.getString(mProfileUrlIdx));
                    rs.setDateTime(cursor.getLong(mDatetimeIdx));
                    //rs.setGeolocation(cursor.getInt(mGeolocationIdx));
                    //rs.setLon(cursor.getString(mLonIdx));
                    //rs.setLat(cursor.getString(mLatIdx));

                    //rs.setNotice_type(cursor.getString(mNoticeType));
                    // add by fancuiru at 2013-12-13
                    if (cursor.getInt(mAttachmentIdx) > 0) {
                        rs.setAttachment_url(mDbHelper.getATTACHMENT(cursor
                                .getLong(mIdIdx)));
                        Log.v("Util", "~~~~~~~~mAttachmentIdx" + mAttachmentIdx
                                + "~~~attUrl:" + rs.getAttachment_url());
                    }
                    // add end
                    // add by bin.zeng 2013-12-31
                    rs.setUserId(userId);
                    // add end
                    rs.setAttachment(cursor.getInt(mAttachmentIdx));
                    rs.setStatus(cursor.getString(mStatusIdx));
                    //rs.setStatusHtml(cursor.getString(mStatusHtmlIdx));
                    //rs.setLocation(cursor.getString(mLocationIdx));
                    //rs.setFavorited(cursor.getInt(mFavaoritedIdx));
                    rs.setName(cursor.getString(mNameIdx));
                    //rs.setReplyCount(cursor.getInt(mReplyCountIdx));
                    // if (DB_ROW_TYPE !=
                    // 6||cursor.getString(mSourceIdx).equals("activity")) {
                    rs.setadcode(cursor.getString(madcodeIdx));
                    status.add(rs);
                    // }
                }
            }
        } finally {
            try {
                cursor.close();
                cursor = null;
            } catch (Exception e) {
            }
            if (null != mDbHelper) {
                mDbHelper.close();
                mDbHelper = null;
            }
        }
        return status;
    }

    public static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, MustardDbAdapter.DATABASE_NAME, null,
                    MustardDbAdapter.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("DROP TABLE IF EXISTS "
                    + MustardDbAdapter.DATABASE_STATUS_TABLE);
            db.execSQL(MustardDbAdapter.DATABASE_CREATE_STATUS);
            db.execSQL("DROP TABLE IF EXISTS "
                    + MustardDbAdapter.DATABASE_ACCOUNT_TABLE);
            db.execSQL(MustardDbAdapter.DATABASE_CREATE_ACCOUNT);
            //db.execSQL("DROP TABLE IF EXISTS "
            //        + MustardDbAdapter.DATABASE_PARAM_TABLE);
            //db.execSQL(MustardDbAdapter.DATABASE_CREATE_PARAM);
            //db.execSQL("DROP TABLE IF EXISTS "
            //        + MustardDbAdapter.DATABASE_BOOKMARK_TABLE);
            //db.execSQL(MustardDbAdapter.DATABASE_CREATE_BOOKMARKS);
            //db.execSQL("DROP TABLE IF EXISTS "
            //        + MustardDbAdapter.DATABASE_OAUTH_TABLE);
            //db.execSQL(MustardDbAdapter.DATABASE_CREATE_OAUTH);
            db.execSQL("DROP TABLE IF EXISTS "
                    + MustardDbAdapter.DATABASE_ATTACHMENTS_TABLE);
            db.execSQL(MustardDbAdapter.DATABASE_CREATE_ATTACHMENTS);
            //db.execSQL("DROP TABLE IF EXISTS "
            //        + MustardDbAdapter.DATABASE_FILTER_TABLE);
            //db.execSQL(MustardDbAdapter.DATABASE_CREATE_FILTERS);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i("DatabaseHelper", "onUpgrade " + oldVersion + " >> "
                    + newVersion);
            boolean lastTableAccount = false;
            boolean lastTableParam = false;
            boolean lastTableBookmark = false;
            boolean lastTableOAuth = false;
            boolean lastTableFilter = false;
            // Simply I don't want to see warning message ;)
            // -->>
            if (!lastTableParam) {

            }
            if (!lastTableBookmark) {

            }
            // <<--
            if (oldVersion > 1) {
                if (oldVersion < 15) {
                    db.execSQL("DROP TABLE IF EXISTS "
                            + MustardDbAdapter.DATABASE_ACCOUNT_TABLE);
                    db.execSQL(MustardDbAdapter.DATABASE_CREATE_ACCOUNT);
                    lastTableAccount = true;
                }
                if (oldVersion < 21) {
                    //db.execSQL(MustardDbAdapter.DATABASE_CREATE_PARAM);
                    //lastTableParam = true;
                } else if (oldVersion < 24) {
                	/*
                    db.execSQL("CREATE TEMPORARY TABLE t1_backup(a,b)");
                    db.execSQL("INSERT INTO t1_backup SELECT code,value FROM "
                            + MustardDbAdapter.DATABASE_PARAM_TABLE
                            + " WHERE code = 'mid'");
                    db.execSQL("DROP TABLE IF EXISTS "
                            + MustardDbAdapter.DATABASE_PARAM_TABLE);
                    db.execSQL(MustardDbAdapter.DATABASE_CREATE_PARAM);
                    db.execSQL("INSERT INTO "
                            + MustardDbAdapter.DATABASE_PARAM_TABLE
                            + " SELECT a,b FROM t1_backup");
                    db.execSQL("DROP TABLE t1_backup");
                    lastTableParam = true;*/
                }

                if (oldVersion < 27) {
                    /*db.execSQL("DROP TABLE IF EXISTS "
                            + MustardDbAdapter.DATABASE_BOOKMARK_TABLE);
                    db.execSQL(MustardDbAdapter.DATABASE_CREATE_BOOKMARKS);
                    lastTableBookmark = true;*/
                }
                if (oldVersion < 28) {
                	/*
                    db.execSQL("DROP TABLE IF EXISTS "
                            + MustardDbAdapter.DATABASE_OAUTH_TABLE);
                    db.execSQL(MustardDbAdapter.DATABASE_CREATE_OAUTH);
                    lastTableOAuth = true;
                    if (!lastTableAccount) {
                        db.execSQL("ALTER TABLE "
                                + MustardDbAdapter.DATABASE_ACCOUNT_TABLE
                                + " ADD token text null");
                        db.execSQL("ALTER TABLE "
                                + MustardDbAdapter.DATABASE_ACCOUNT_TABLE
                                + " ADD token_secret text null");
                    }*/
                }
                if (oldVersion < 30) {/*
                    if (!lastTableOAuth) {
                        db.execSQL("ALTER TABLE "
                                + MustardDbAdapter.DATABASE_OAUTH_TABLE
                                + " ADD reserved int null");
                        db.execSQL("UPDATE "
                                + MustardDbAdapter.DATABASE_OAUTH_TABLE
                                + " set reserved = '1' ");
                    }
                    if (!lastTableAccount) {
                        db.execSQL("ALTER TABLE "
                                + MustardDbAdapter.DATABASE_ACCOUNT_TABLE
                                + " ADD " + MustardDbAdapter.KEY_MENTION_MAX_ID
                                + " integer ");
                    }*/
                }
                if (oldVersion < 32) {
                    if (!lastTableAccount) {
                        db.execSQL("ALTER TABLE "
                                + MustardDbAdapter.DATABASE_ACCOUNT_TABLE
                                + " ADD " + MustardDbAdapter.KEY_USER_ID
                                + " integer ");
                    }
                }
                if (oldVersion < 35) {
                    if (!lastTableAccount) {
                        db.execSQL("ALTER TABLE "
                                + MustardDbAdapter.DATABASE_ACCOUNT_TABLE
                                + " ADD " + MustardDbAdapter.KEY_TEXTLIMIT
                                + " integer ");
                    }
                }
                if (oldVersion < 37) {/*
                    if (!lastTableFilter) {
                        db.execSQL("DROP TABLE IF EXISTS "
                                + MustardDbAdapter.DATABASE_FILTER_TABLE);
                        db.execSQL(MustardDbAdapter.DATABASE_CREATE_FILTERS);
                    }*/
                }
                if (oldVersion < 38) {
                    if (!lastTableAccount) {
                        db.execSQL("ALTER TABLE "
                                + MustardDbAdapter.DATABASE_ACCOUNT_TABLE
                                + " ADD " + MustardDbAdapter.KEY_TO_MERGE
                                + " integer ");
                        db.execSQL("UPDATE "
                                + MustardDbAdapter.DATABASE_ACCOUNT_TABLE
                                + " set " + MustardDbAdapter.KEY_TO_MERGE
                                + " = 0 ");
                        db.execSQL("UPDATE "
                                + MustardDbAdapter.DATABASE_ACCOUNT_TABLE
                                + " set " + MustardDbAdapter.KEY_TO_MERGE
                                + " = 1  WHERE " + MustardDbAdapter.KEY_DEFAULT
                                + " = 1");
                    }
                }
                if (oldVersion < 43) {
                    if (!lastTableAccount) {
                        db.execSQL("ALTER TABLE "
                                + MustardDbAdapter.DATABASE_ACCOUNT_TABLE
                                + " ADD " + MustardDbAdapter.KEY_ATTACHLIMIT
                                + " integer ");
                        db.execSQL("UPDATE "
                                + MustardDbAdapter.DATABASE_ACCOUNT_TABLE
                                + " set " + MustardDbAdapter.KEY_ATTACHLIMIT
                                + " = 131072 ");
                    }
                }

            }
            db.execSQL("DROP TABLE IF EXISTS "
                    + MustardDbAdapter.DATABASE_STATUS_TABLE);
            db.execSQL(MustardDbAdapter.DATABASE_CREATE_STATUS);
            db.execSQL(MustardDbAdapter.DATABASE_DROP_ATTACHMENTS);
            db.execSQL(MustardDbAdapter.DATABASE_CREATE_ATTACHMENTS);
        }

    }

    public static ArrayList<RowStatus> getDatas(Context context,
            int DB_ROW_TYPE, String DB_ROW_EXTRA) {
        MustardDbAdapter mDbHelper = getDbAdapter(context);
        Cursor cursor = null;
        ArrayList<RowStatus> status = new ArrayList<RowStatus>();
        try {
            cursor = mDbHelper.fetchAllStatuses(DB_ROW_TYPE, DB_ROW_EXTRA,
                    "DESC");
            if (cursor == null) {
                mDbHelper.close();
                return status;
            }

            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(context);
            boolean ignoreHideUsers = prefs.getBoolean(
                    Preferences.IGNORE_HIDE_USERS, false);
            int mIdIdx = cursor.getColumnIndex(MustardDbAdapter.KEY_ROWID);
            int mUserId = cursor.getColumnIndex(MustardDbAdapter.KEY_USER_ID);
            int mIdAccountIdx = cursor
                    .getColumnIndex(MustardDbAdapter.KEY_ACCOUNT_ID);
            //int mGeolocationIdx = cursor
            //        .getColumnIndex(MustardDbAdapter.KEY_GEO);
            int mScreenNameIdx = cursor
                    .getColumnIndex(MustardDbAdapter.KEY_SCREEN_NAME);
            int mStatusIdx = cursor.getColumnIndex(MustardDbAdapter.KEY_STATUS);
            //int mStatusHtmlIdx = cursor
            //        .getColumnIndex(MustardDbAdapter.KEY_STATUS_HTML);
            //int mLocationIdx = cursor
            //		.getColumnIndex(MustardDbAdapter.KEY_LOCATION);
            //int mFavaoritedIdx = cursor.getColumnIndex(MustardDbAdapter.KEY_FAVORITE);
            int mStatusIdIdx = cursor
                    .getColumnIndex(MustardDbAdapter.KEY_STATUS_ID);
            int mDatetimeIdx = cursor
                    .getColumnIndex(MustardDbAdapter.KEY_INSERT_AT);
            //int mSourceIdx = cursor.getColumnIndex(MustardDbAdapter.KEY_SOURCE);
            //int mInReplyToIdx = cursor
            //        .getColumnIndex(MustardDbAdapter.KEY_IN_REPLY_TO);
            //int mInReplyToScreenNameIdx = cursor
            //        .getColumnIndex(MustardDbAdapter.KEY_IN_REPLY_TO_SCREEN_NAME);
            int mRepeatedIdIdx = cursor
                    .getColumnIndex(MustardDbAdapter.KEY_REPEATED_ID);
            //int mRepeatedByScreenNameIdx = cursor
            //        .getColumnIndex(MustardDbAdapter.KEY_REPEATED_BY_SCREEN_NAME);
            int mProfileImageIdx = cursor.getColumnIndex(MustardDbAdapter.KEY_USER_IMAGE);
            //int mProfileUrlIdx = cursor
            //        .getColumnIndex(MustardDbAdapter.KEY_USER_URL);
            //int mLonIdx = cursor.getColumnIndex(MustardDbAdapter.KEY_LON);
            //int mLatIdx = cursor.getColumnIndex(MustardDbAdapter.KEY_LAT);
            int mAttachmentIdx = cursor
                    .getColumnIndex(MustardDbAdapter.KEY_ATTACHMENT);
            int mNameIdx = cursor.getColumnIndex(MustardDbAdapter.KEY_NAME);
            //int mReplyCountIdx = cursor
            //        .getColumnIndex(MustardDbAdapter.KEY_REPLY_COUNT);
            while (cursor.moveToNext()) {
                long userId = cursor.getLong(mUserId);
                if (!(isHidden(context, userId) && ignoreHideUsers)) {
                    RowStatus rs = new RowStatus();
                    rs.setId(cursor.getLong(mIdIdx));
                    rs.setStatusId(cursor.getLong(mStatusIdIdx));
                    rs.setAccountId(cursor.getLong(mIdAccountIdx));
                    rs.setScreenName(cursor.getString(mScreenNameIdx));
                    //rs.setSource(cursor.getString(mSourceIdx));
                    //rs.setInReplyTo(cursor.getLong(mInReplyToIdx));
                    //rs.setInReplyToScreenName(cursor
                    //        .getString(mInReplyToScreenNameIdx));
                    rs.setRepeatedId(cursor.getLong(mRepeatedIdIdx));
                    //rs.setRepeatedByScreenName(cursor
                    //        .getString(mRepeatedByScreenNameIdx));
                    //rs.setProfileImage(cursor.getString(mProfileImageIdx));
                    //rs.setProfileUrl(cursor.getString(mProfileUrlIdx));
                    rs.setDateTime(cursor.getLong(mDatetimeIdx));
                    //rs.setGeolocation(cursor.getInt(mGeolocationIdx));
                    //rs.setLon(cursor.getString(mLonIdx));
                    //rs.setLat(cursor.getString(mLatIdx));
                    // add by fancuiru at 2013-12-13
                    if (cursor.getInt(mAttachmentIdx) > 0) {
                        rs.setAttachment_url(mDbHelper.getATTACHMENT(cursor
                                .getLong(mIdIdx)));
                        Log.v("Util", "~~~~~~~~mAttachmentIdx" + mAttachmentIdx
                                + "~~~attUrl:" + rs.getAttachment_url());
                    }
                    // add end
                    // add by bin.zeng 2013-12-31
                    rs.setUserId(userId);
                    // add end
                    rs.setAttachment(cursor.getInt(mAttachmentIdx));
                    rs.setStatus(cursor.getString(mStatusIdx));
                    //rs.setStatusHtml(cursor.getString(mStatusHtmlIdx));
                    //rs.setLocation(cursor.getString(mLocationIdx));
                    //rs.setFavorited(cursor.getInt(mFavaoritedIdx));
                    rs.setName(cursor.getString(mNameIdx));
                    //rs.setReplyCount(cursor.getInt(mReplyCountIdx));
                    //if (DB_ROW_TYPE == 6  && !cursor.getString(mSourceIdx).equals("activity")) {
                    if (DB_ROW_TYPE == 6){
                    	status.add(rs);
                    }
                }
            }
        } finally {
            try {
                cursor.close();
                cursor = null;
            } catch (Exception e) {
            }
            if (null != mDbHelper) {
                mDbHelper.close();
                mDbHelper = null;
            }
        }
        return status;
    }

    public static final boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A) {
            return true;
        }
        return false;
    }

    public static String getHanyuPinyin(char c) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        String[] pinyins = null;
        try {
            pinyins = PinyinHelper.toHanyuPinyinStringArray(c, format);
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return (pinyins == null || pinyins.length == 0) ? null : pinyins[0];

    }

    public static String getTimestampString(Context context, Date date) {
        StringBuffer buffer = new StringBuffer();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Calendar currentCalendar = Calendar.getInstance();
        int spacing = 0;
        if (currentCalendar.get(Calendar.YEAR) - calendar.get(Calendar.YEAR) != 0) {
            buffer.append(calendar.get(Calendar.YEAR));
            buffer.append(" ");
            spacing = -1;
        } else {
            spacing = currentCalendar.get(Calendar.DAY_OF_YEAR)
                    - calendar.get(Calendar.DAY_OF_YEAR);
        }
        switch (spacing) {
        case 0:
            break;
        case 1:
            buffer.append(context.getResources().getString(R.string.yesterday));
            break;
        case 2:
        case 3:
        case 4:
        default:
            buffer.append(calendar.get(Calendar.MONTH) + 1);
            buffer.append("-");
            buffer.append(calendar.get(Calendar.DAY_OF_MONTH));
            break;
        }
        buffer.append(" ");
        if (calendar.get(Calendar.HOUR_OF_DAY) < 10) {
            buffer.append("0");
        }
        buffer.append(calendar.get(Calendar.HOUR_OF_DAY));
        buffer.append(":");
        if (calendar.get(Calendar.MINUTE) < 10) {
            buffer.append("0");
        }
        buffer.append(calendar.get(Calendar.MINUTE));
        return buffer.toString();
    }

    private static final Pattern EMOTION_URL = Pattern.compile("\\[(\\S+?)\\]");

    /**
     * 处理字符串中的表情
     * 
     * @param context
     * @param message
     *            传入的需要处理的String
     * @param small
     *            是否需要小图片
     * @return
     */
    public static CharSequence convertNormalStringToSpannableString(
            Context context, String message, boolean small, int newHeight,
            int newWidth) {
        String hackTxt;
        if (message.startsWith("[") && message.endsWith("]")) {
            hackTxt = message + " ";
        } else {
            hackTxt = message;
        }
        SpannableString value = SpannableString.valueOf(hackTxt);

        Matcher localMatcher = EMOTION_URL.matcher(value);
        while (localMatcher.find()) {
            String str2 = localMatcher.group(0);
            int k = localMatcher.start();
            int m = localMatcher.end();
            if (m - k < 8) {
                if (BaseApplication.getInstance().getFaceMap()
                        .containsKey(str2)) {
                    int face = BaseApplication.getInstance().getFaceMap()
                            .get(str2);
                    Bitmap bitmap = BitmapFactory.decodeResource(
                            context.getResources(), face);
                    if (bitmap != null) {
                        if (small) {
                            int rawHeigh = bitmap.getHeight();
                            int rawWidth = bitmap.getHeight();
                            // 计算缩放因子
                            float heightScale = ((float) newHeight) / rawHeigh;
                            float widthScale = ((float) newWidth) / rawWidth;
                            // 新建立矩阵
                            Matrix matrix = new Matrix();
                            matrix.postScale(heightScale, widthScale);
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                                    rawWidth, rawHeigh, matrix, true);
                        }
                        ImageSpan localImageSpan = new ImageSpan(context,
                                bitmap, ImageSpan.ALIGN_BASELINE);
                        value.setSpan(localImageSpan, k, m,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
        }
        return value;
    }

    public static CharSequence convertNormalStringToSpannableString2(
            Context context, String message, boolean small, int newHeight,
            int newWidth) {
        String hackTxt = message + " ";
        final StringTokenizer tokenizer = new StringTokenizer(hackTxt, "\n \t",
                true);
        SpannableStringBuilder builder = new SpannableStringBuilder();
      /*  while (tokenizer.hasMoreTokens()) {
            String textFound = tokenizer.nextToken();
            if (BaseApplication.getInstance().getEmoticons()
                    .containsKey(textFound)) {
                Bitmap bitmap = BaseApplication.getInstance().getEmoticons()
                        .get(textFound).getBitmap();
                if (bitmap != null) {
                    if (small) {
                        int rawHeigh = bitmap.getHeight();
                        int rawWidth = bitmap.getHeight();
                        // 计算缩放因子
                        float heightScale = ((float) newHeight) / rawHeigh;
                        float widthScale = ((float) newWidth) / rawWidth;
                        // 新建立矩阵
                        Matrix matrix = new Matrix();
                        matrix.postScale(heightScale, widthScale);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, rawWidth,
                                rawHeigh, matrix, true);
                    }
                    ImageSpan imageSpan = new ImageSpan(context, bitmap,
                            ImageSpan.ALIGN_BASELINE);
                    SpannableString spannableString = new SpannableString(
                            textFound);
                    spannableString.setSpan(imageSpan, 0, textFound.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.append(spannableString);
                }
            } else {
                builder.append(textFound);
            }
        }*/
        return builder;
    }
/*
    private void  setTextViewUrlSpanClick(TextView tv,RowStatus rowStatus)
    {

		CharSequence text = tv.getText();  
		String user_url = "/StatusNet/index.php/user/";
		String group_url = "/StatusNet/index.php/group/";
		String poll_url = "/StatusNet/index.php/main/poll/";
		String meeting_url = "/StatusNet/index.php/event/";
		String question_url = "/StatusNet/index.php/question/";
        if(text instanceof Spannable){     
            int end = text.length(); 
            Spannable sp = (Spannable)tv.getText();  
            URLSpan[] urls=sp.getSpans(0, end, URLSpan.class);      
            SpannableStringBuilder style=new SpannableStringBuilder(text);
          //  style.clearSpans();//should clear old spans     
            for(URLSpan url : urls){ 
            
            	String u = url.getURL();
            	boolean isUser = u.contains(user_url);
            	boolean isGroup = u.contains(group_url);
            	boolean isPoll = u.contains(poll_url);
            	boolean isMeeting = u.contains(meeting_url);
            	boolean isQuestion = u.contains(question_url);
            	
            
            	
            
            	if(isUser)
            	{
            		String userId = u.substring(u.lastIndexOf("/")+1,u.length());
            		MyURLSpan myURLSpan = new MyURLSpan(u,MyURLSpan.URL_USER,userId,mApplication.mStatusNet,rowStatus); 
            		
            		//style.replace(sp.getSpanStart(url),sp.getSpanEnd(url), "asasasas");
            		style.removeSpan(url);
            		
            		
                
            		style.setSpan(myURLSpan,sp.getSpanStart(url),sp.getSpanEnd(url),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  
            	}
            	
            	
            }     
            tv.setText(style);     
        }  
	
    	
    }*/
    public static Spanned convertNormalStringToSpannableStringForNotice(
            Context context, CharSequence message, boolean small, int newHeight,
            int newWidth) {
    	 int index= 0;
        String hackTxt = message + " ";
        final StringTokenizer tokenizer = new StringTokenizer(hackTxt, "\n \t",
                true);
        SpannableStringBuilder builder = new SpannableStringBuilder(message);
       /*  while (tokenizer.hasMoreTokens()) {
            String textFound = tokenizer.nextToken();
            if (BaseApplication.getInstance().getEmoticons()
                    .containsKey(textFound)) {
                Bitmap bitmap = BaseApplication.getInstance().getEmoticons()
                        .get(textFound).getBitmap();
                if (bitmap != null) {
                    if (small) {
                        int rawHeigh = bitmap.getHeight();
                        int rawWidth = bitmap.getHeight();
                        // 璁＄畻缂╂斁鍥犲瓙
                        float heightScale = ((float) newHeight) / rawHeigh;
                        float widthScale = ((float) newWidth) / rawWidth;
                        // 鏂板缓绔嬬煩闃�                        
                        Matrix matrix = new Matrix();
                        matrix.postScale(heightScale, widthScale);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, rawWidth,
                                rawHeigh, matrix, true);
                    }
                    ImageSpan imageSpan = new ImageSpan(context, bitmap,
                            ImageSpan.ALIGN_BOTTOM);
                   // SpannableString spannableString = new SpannableString(
                    //        textFound);
                    builder.setSpan(imageSpan, index, index+textFound.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                   // builder.append(spannableString);
                }
            } 
            index = index + textFound.length();
        }*/
        return (Spanned)builder;
    }
}
