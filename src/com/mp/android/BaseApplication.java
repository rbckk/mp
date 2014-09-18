package com.mp.android;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.mp.android.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
//import com.mp.android.im.emoticon.Emoticon;
import com.mp.android.statusnet.Account;
import com.mp.android.statusnet.MustardDbAdapter;
import com.mp.android.statusnet.Preferences;
import com.mp.android.statusnet.provider.StatusNet;
import com.mp.android.statusnet.receiver.StartupReceiver;
import com.mp.android.statusnet.statusnet.User;
//import com.mp.android.statusnet.util.ImageManager;
//import com.mp.android.user.UserInfo;
import com.mp.android.util.Constant;
import com.mp.android.util.StringUtil;
import com.mp.android.util.Text_Util;

public class BaseApplication extends Application {
	public static Map<String, Object> session = new HashMap<String, Object>() ;
    public String mLocation;
    public double mLongitude;
    public double mLatitude;
    public String mImagePath;
    public int mImageType = -1;
    //public UserInfo mUserInfo = new UserInfo();
    public AsyncRenRen mAsyncRenRen = new AsyncRenRen();
    public Text_Util mText_Util = new Text_Util();
    //public RenRen mRenRen = null;
    public FinalBitmap mHeadBitmap = null;
    //public FinalBitmap mAlbumBitmap = null;
    public FinalBitmap mPhotoBitmap = null;
    //public FinalBitmap mNearByBitmap = null;
    public static final String CACHE = MustardDbAdapter.PATH_CACHE;
    public static final String APPLICATION_NAME = "Phicomm";
    public static User nowAccountInfo = new User();
    public StatusNet mStatusNet = null;
    private Map<String, Integer> mEmoticonsMap = new LinkedHashMap<String, Integer>();
    private static Context mContext;
    //public static AppIconManager mIconManger;
    public static String new_avatar_url;

    private static BaseApplication groupContext;
    public static final int NUM_PAGE = 6;// 总共有多少页
    public static int NUM = 21;// 每页20个表情,还有最后一个删除button

    public void onCreate() {
        super.onCreate();
        //mRenRen = new RenRen(this);
        initHeadBitmap();
        //initAlbumBitmap();
        initPhotoBitmap();
        //initNearByBitmap();
        initStatusnet();
        groupContext = this;
        mContext = getApplicationContext();
        //mIconManger = AppIconManager.getInstance();
        initImageLoader(getApplicationContext());
        Intent intent = new Intent();
        intent.setAction(MustardDbAdapter.ACTION_REMOTE_SERVICE);
        emotion_size = (int) getResources().getInteger(R.integer.emotion_size);
        startService(intent);
        new Thread() {
            public void run() {
                initEmoticonsMap();
                initEmoticonPack();
            };

        }.start();
    }

    int emotion_size;

    public static BaseApplication getInstance() {
        return groupContext;
    }

    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    private void initHeadBitmap() {
        mHeadBitmap = new FinalBitmap(this);
        mHeadBitmap.configBitmapLoadThreadSize(5);
        mHeadBitmap.configDiskCachePath(CACHE);
        mHeadBitmap.configLoadfailImage(R.drawable.v5_0_1_widget_default_head);
        mHeadBitmap.configLoadingImage(R.drawable.v5_0_1_widget_default_head);
        mHeadBitmap.configMemoryCacheSize(1);
        mHeadBitmap.init();
    }

    /**
     * Òì²½ÏÂÔØÍ¼Æ¬
     * 
     * @return
     */
//    public static AppIconManager getIconManger() {
//        return mIconManger;
//    }

    /**
     * get App Context
     * 
     * @return context
     */
    public static Context getContext() {
        return mContext;
    }

//    private void initAlbumBitmap() {
//        mAlbumBitmap = new FinalBitmap(this);
//        mAlbumBitmap.configBitmapLoadThreadSize(5);
//        mAlbumBitmap.configDiskCachePath(CACHE);
//        mAlbumBitmap
//                .configLoadfailImage(R.drawable.v5_0_1_select_album_item_default_img);
//        mAlbumBitmap
//                .configLoadingImage(R.drawable.v5_0_1_select_album_item_default_img);
//        mAlbumBitmap.configMemoryCacheSize(1);
//        mAlbumBitmap.init();
//    }

    private void initPhotoBitmap() {
        mPhotoBitmap = new FinalBitmap(this);
        mPhotoBitmap.configBitmapLoadThreadSize(5);
        mPhotoBitmap.configDiskCachePath(CACHE);
        mPhotoBitmap.configLoadfailImage(R.drawable.v5_0_1_photo_default_img);
        mPhotoBitmap.configLoadingImage(R.drawable.v5_0_1_photo_default_img);
        mPhotoBitmap.configMemoryCacheSize(1);
        mPhotoBitmap.init();
    }

//    private void initNearByBitmap() {
//        mNearByBitmap = new FinalBitmap(this);
//        mNearByBitmap.configBitmapLoadThreadSize(5);
//        mNearByBitmap.configDiskCachePath(CACHE);
//        mNearByBitmap
//                .configLoadfailImage(R.drawable.v5_0_1_nearby_activity_photo_bg);
//        mNearByBitmap
//                .configLoadingImage(R.drawable.v5_0_1_nearby_activity_photo_bg);
//        mNearByBitmap.configMemoryCacheSize(1);
//        mNearByBitmap.init();
//    }

    /**
     * Statusnet
     * 
     */
    public static final boolean DEBUG = false;

    public static final String TAG = "BaseApplication";
    //public static ImageManager sImageManager;
    public static String sVersionName;
    public static final String SN_MIN_VERSION = "0.8.1";
    public static final String MUSTARD_FONT_NAME = "DejaVuSans.ttf";
    public static Location sLocation = null;

    @Override
    public void onTerminate() {
        super.onTerminate();
        try {
            MustardDbAdapter dbAdapter = new MustardDbAdapter(this);
            dbAdapter.open();
            dbAdapter.deleteStatuses(MustardDbAdapter.ROWTYPE_ALL, null);
            dbAdapter.close();
        } catch (Exception e) {
            if (DEBUG)
                Log.d(TAG, e.getMessage());
        }
    }

    private SharedPreferences mSharedPreferences = null;

    private void initStatusnet() {
        if (DEBUG)
            Log.d(TAG, "onCreate");

        //sImageManager = new ImageManager(this);

        PackageManager pm = getPackageManager();
        PackageInfo pi;

        try {
            pi = pm.getPackageInfo(getApplicationContext().getPackageName(), 0);
            sVersionName = pi.versionName;
            if (DEBUG)
                Log.d(TAG, "Version: " + sVersionName);
        } catch (Exception e) {

        }
        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        StartupReceiver.onStartupApplication(getApplicationContext());
    }

    public boolean checkVersion(String version) {
        return StringUtil.compareVersion(version, SN_MIN_VERSION) >= 0;
    }

    public StatusNet loadAccount(MustardDbAdapter mDbHelper, long userid) {
        return checkAccount(mDbHelper, false, userid);
    }

    public StatusNet checkAccount(MustardDbAdapter mDbHelper) {
        mStatusNet = checkAccount(mDbHelper, true, -1);
        return mStatusNet;
    }

    public StatusNet checkAccount(MustardDbAdapter mDbHelper, long userid) {
        return checkAccount(mDbHelper, false, userid);
    }

    public StatusNet checkAccount(MustardDbAdapter mDbHelper,
            boolean loadDefault, long userid) {
        StatusNet sn = new StatusNet(this);
        String s_maxNotices = mSharedPreferences.getString(
                Preferences.FETCH_MAX_ITEMS_KEY,
                getString(R.string.pref_rows_fetch_default));
        int maxNotices = Integer.parseInt(s_maxNotices);
        sn.setMaxNotices(maxNotices);
        Cursor accountCursor = null;
        try {
            if (loadDefault)
                accountCursor = mDbHelper.fetchDefaultAccount();
            else
                accountCursor = mDbHelper.fetchAccount(userid);
        } catch (Exception e) {
            e.printStackTrace();
            // if (MustardApplication.DEBUG) Log.e(TAG,e.toString());
            if (accountCursor != null)
                try {
                    accountCursor.close();
                } catch (Exception ee) {
                }
            return null;
        }

        if (accountCursor != null && accountCursor.getCount() < 1) {
            if (accountCursor != null)
                try {
                    accountCursor.close();
                } catch (Exception e) {
                }
            return null;
        }
        try {
            if (accountCursor.moveToNext()) {
                int userIndex = accountCursor.getColumnIndexOrThrow(MustardDbAdapter.KEY_USER);
                int userIdIndex = accountCursor.getColumnIndexOrThrow(MustardDbAdapter.KEY_USER_ID);
                int passwordIndex = accountCursor.getColumnIndexOrThrow(MustardDbAdapter.KEY_PASSWORD);
                int instanceIndex = accountCursor.getColumnIndexOrThrow(MustardDbAdapter.KEY_INSTANCE);
                int rowidIndex = accountCursor
                        .getColumnIndexOrThrow(MustardDbAdapter.KEY_ROWID);
                int tokenIndex = accountCursor
                        .getColumnIndexOrThrow(MustardDbAdapter.KEY_TOKEN);
                int tokenSecretIndex = accountCursor
                        .getColumnIndexOrThrow(MustardDbAdapter.KEY_TOKEN_SECRET);
                int versionIndex = accountCursor
                        .getColumnIndexOrThrow(MustardDbAdapter.KEY_VERSION);
                int textLimitIndex = accountCursor
                        .getColumnIndexOrThrow(MustardDbAdapter.KEY_TEXTLIMIT);
                int attachLimitIndex = accountCursor
                        .getColumnIndexOrThrow(MustardDbAdapter.KEY_ATTACHLIMIT);
                String user = accountCursor.getString(userIndex);
                String password = accountCursor.getString(passwordIndex);
                String instance = accountCursor.getString(instanceIndex);
                long userId = accountCursor.getLong(rowidIndex);
                long usernameId = accountCursor.getLong(userIdIndex);
                int textLimit = accountCursor.getInt(textLimitIndex);
                long attachlimit = accountCursor.getLong(attachLimitIndex);
                String version = accountCursor.getString(versionIndex);
                if (version == null) { // Twitter creation fix
                    version = "0.9.4";
                }
                Account account = new Account();
                account.setId(userId);
                account.setUsername(user);
                nowAccountInfo.setName(account.getUsername());
                session.put(Constant.IM_LOGIN_USER, account.getUsername());
                account.setVersion(version);
                account.setTextLimit(textLimit);
                account.setInstance(instance);
                account.setAttachlimit(attachlimit);
                String token = accountCursor.getString(tokenIndex);
                String tokenSecret = accountCursor.getString(tokenSecretIndex);
                sn.setURL(new URL(instance));
                if (token == null && tokenSecret == null)
                    sn.setCredentials(user, password);

                else {
                    Log.v("xiaosong", "test");
                    // String instanceNoPrefix = instance.startsWith("https") ?
                    // instance.substring(8) : instance.substring(7);
                    //
                    // OAuthLoader om = new OAuthLoader(mDbHelper) ;
                    // OAuthInstance oi = om.get(instanceNoPrefix);
                    //
                    // CommonsHttpOAuthConsumer consumer = new
                    // CommonsHttpOAuthConsumer (
                    // oi.key,
                    // oi.secret);
                    //
                    // consumer.setTokenWithSecret( token, tokenSecret);
                    // sn.setCredentials(consumer, user);
                }

                if (usernameId <= 0) {
                    User u = sn.getUser(user);
                    usernameId = u.getId();
                    mDbHelper.setUserIdAccount(userId, usernameId);
                }
                sn.setUsernameId(usernameId);
                sn.setUserId(userId);
                sn.setAccount(account);
                mStatusNet = sn;
            }
        } catch (Exception e) {
            // if (MustardApplication.DEBUG) e.printStackTrace();
            Log.v(TAG, "checkAccount  Exception");
            return null;
        } finally {
            if (accountCursor != null)
                try {
                    accountCursor.close();
                } catch (Exception e) {
                }
        }
        return sn;
    }

    public Map<String, Integer> getFaceMap() {
        if (!mEmoticonsMap.isEmpty())
            return mEmoticonsMap;
        return null;
    }

    private void initEmoticonsMap() {
        // TODO Auto-generated method stub
        mEmoticonsMap.put("[呲牙]", R.drawable.f_static_000);
        mEmoticonsMap.put("[调皮]", R.drawable.f_static_001);
        mEmoticonsMap.put("[流汗]", R.drawable.f_static_002);
        mEmoticonsMap.put("[偷笑]", R.drawable.f_static_003);
        mEmoticonsMap.put("[再见]", R.drawable.f_static_004);
        mEmoticonsMap.put("[敲打]", R.drawable.f_static_005);
        mEmoticonsMap.put("[擦汗]", R.drawable.f_static_006);
        mEmoticonsMap.put("[猪头]", R.drawable.f_static_007);
        mEmoticonsMap.put("[玫瑰]", R.drawable.f_static_008);
        mEmoticonsMap.put("[流泪]", R.drawable.f_static_009);
        mEmoticonsMap.put("[大哭]", R.drawable.f_static_010);
        mEmoticonsMap.put("[嘘]", R.drawable.f_static_011);
        mEmoticonsMap.put("[酷]", R.drawable.f_static_012);
        mEmoticonsMap.put("[抓狂]", R.drawable.f_static_013);
        mEmoticonsMap.put("[委屈]", R.drawable.f_static_014);
        mEmoticonsMap.put("[便便]", R.drawable.f_static_015);
        mEmoticonsMap.put("[炸弹]", R.drawable.f_static_016);
        mEmoticonsMap.put("[菜刀]", R.drawable.f_static_017);
        mEmoticonsMap.put("[可爱]", R.drawable.f_static_018);
        mEmoticonsMap.put("[色]", R.drawable.f_static_019);
        mEmoticonsMap.put("[害羞]", R.drawable.f_static_020);

        mEmoticonsMap.put("[得意]", R.drawable.f_static_021);
        mEmoticonsMap.put("[吐]", R.drawable.f_static_022);
        mEmoticonsMap.put("[微笑]", R.drawable.f_static_023);
        mEmoticonsMap.put("[发怒]", R.drawable.f_static_024);
        mEmoticonsMap.put("[尴尬]", R.drawable.f_static_025);
        mEmoticonsMap.put("[惊恐]", R.drawable.f_static_026);
        mEmoticonsMap.put("[冷汗]", R.drawable.f_static_027);
        mEmoticonsMap.put("[爱心]", R.drawable.f_static_028);
        mEmoticonsMap.put("[示爱]", R.drawable.f_static_029);
        mEmoticonsMap.put("[白眼]", R.drawable.f_static_030);
        mEmoticonsMap.put("[傲慢]", R.drawable.f_static_031);
        mEmoticonsMap.put("[难过]", R.drawable.f_static_032);
        mEmoticonsMap.put("[惊讶]", R.drawable.f_static_033);
        mEmoticonsMap.put("[疑问]", R.drawable.f_static_034);
        mEmoticonsMap.put("[睡]", R.drawable.f_static_035);
        mEmoticonsMap.put("[亲亲]", R.drawable.f_static_036);
        mEmoticonsMap.put("[憨笑]", R.drawable.f_static_037);
        mEmoticonsMap.put("[爱情]", R.drawable.f_static_038);
        mEmoticonsMap.put("[衰]", R.drawable.f_static_039);
        mEmoticonsMap.put("[撇嘴]", R.drawable.f_static_040);
        mEmoticonsMap.put("[阴险]", R.drawable.f_static_041);

        mEmoticonsMap.put("[奋斗]", R.drawable.f_static_042);
        mEmoticonsMap.put("[发呆]", R.drawable.f_static_043);
        mEmoticonsMap.put("[右哼哼]", R.drawable.f_static_044);
        mEmoticonsMap.put("[拥抱]", R.drawable.f_static_045);
        mEmoticonsMap.put("[坏笑]", R.drawable.f_static_046);
        mEmoticonsMap.put("[飞吻]", R.drawable.f_static_047);
        mEmoticonsMap.put("[鄙视]", R.drawable.f_static_048);
        mEmoticonsMap.put("[晕]", R.drawable.f_static_049);
        mEmoticonsMap.put("[大兵]", R.drawable.f_static_050);
        mEmoticonsMap.put("[可怜]", R.drawable.f_static_051);
        mEmoticonsMap.put("[强]", R.drawable.f_static_052);
        mEmoticonsMap.put("[弱]", R.drawable.f_static_053);
        mEmoticonsMap.put("[握手]", R.drawable.f_static_054);
        mEmoticonsMap.put("[胜利]", R.drawable.f_static_055);
        mEmoticonsMap.put("[抱拳]", R.drawable.f_static_056);
        mEmoticonsMap.put("[凋谢]", R.drawable.f_static_057);
        mEmoticonsMap.put("[饭]", R.drawable.f_static_058);
        mEmoticonsMap.put("[蛋糕]", R.drawable.f_static_059);
        mEmoticonsMap.put("[西瓜]", R.drawable.f_static_060);
        mEmoticonsMap.put("[啤酒]", R.drawable.f_static_061);
        mEmoticonsMap.put("[飘虫]", R.drawable.f_static_062);

        mEmoticonsMap.put("[勾引]", R.drawable.f_static_063);
        mEmoticonsMap.put("[OK]", R.drawable.f_static_064);
        mEmoticonsMap.put("[爱你]", R.drawable.f_static_065);
        mEmoticonsMap.put("[咖啡]", R.drawable.f_static_066);
        mEmoticonsMap.put("[钱]", R.drawable.f_static_067);
        mEmoticonsMap.put("[月亮]", R.drawable.f_static_068);
        mEmoticonsMap.put("[美女]", R.drawable.f_static_069);
        mEmoticonsMap.put("[刀]", R.drawable.f_static_070);
        mEmoticonsMap.put("[发抖]", R.drawable.f_static_071);
        mEmoticonsMap.put("[差劲]", R.drawable.f_static_072);
        mEmoticonsMap.put("[拳头]", R.drawable.f_static_073);
        mEmoticonsMap.put("[心碎]", R.drawable.f_static_074);
        mEmoticonsMap.put("[太阳]", R.drawable.f_static_075);
        mEmoticonsMap.put("[礼物]", R.drawable.f_static_076);
        mEmoticonsMap.put("[足球]", R.drawable.f_static_077);
        mEmoticonsMap.put("[骷髅]", R.drawable.f_static_078);
        mEmoticonsMap.put("[挥手]", R.drawable.f_static_079);
        mEmoticonsMap.put("[闪电]", R.drawable.f_static_080);
        mEmoticonsMap.put("[饥饿]", R.drawable.f_static_081);
        mEmoticonsMap.put("[困]", R.drawable.f_static_082);
        mEmoticonsMap.put("[咒骂]", R.drawable.f_static_083);

        mEmoticonsMap.put("[折磨]", R.drawable.f_static_084);
        mEmoticonsMap.put("[抠鼻]", R.drawable.f_static_085);
        mEmoticonsMap.put("[鼓掌]", R.drawable.f_static_086);
        mEmoticonsMap.put("[糗大了]", R.drawable.f_static_087);
        mEmoticonsMap.put("[左哼哼]", R.drawable.f_static_088);
        mEmoticonsMap.put("[哈欠]", R.drawable.f_static_089);
        mEmoticonsMap.put("[快哭了]", R.drawable.f_static_090);
        mEmoticonsMap.put("[吓]", R.drawable.f_static_091);
        mEmoticonsMap.put("[篮球]", R.drawable.f_static_092);
        mEmoticonsMap.put("[乒乓球]", R.drawable.f_static_093);
        mEmoticonsMap.put("[NO]", R.drawable.f_static_094);
        mEmoticonsMap.put("[跳跳]", R.drawable.f_static_095);
        mEmoticonsMap.put("[怄火]", R.drawable.f_static_096);
        mEmoticonsMap.put("[转圈]", R.drawable.f_static_097);
        mEmoticonsMap.put("[磕头]", R.drawable.f_static_098);
        mEmoticonsMap.put("[回头]", R.drawable.f_static_099);
        mEmoticonsMap.put("[跳绳]", R.drawable.f_static_100);
        mEmoticonsMap.put("[激动]", R.drawable.f_static_101);
        mEmoticonsMap.put("[街舞]", R.drawable.f_static_102);
        mEmoticonsMap.put("[献吻]", R.drawable.f_static_103);
        mEmoticonsMap.put("[左太极]", R.drawable.f_static_104);
        mEmoticonsMap.put("[右太极]", R.drawable.f_static_105);
        mEmoticonsMap.put("[闭嘴]", R.drawable.f_static_106);
    }
/*
    private Map<String, Emoticon> mEmoticonsMap2 = new LinkedHashMap<String, Emoticon>();

    public Map<String, Emoticon> getEmoticons() {
        return mEmoticonsMap2;
    }
*/
    public void initEmoticonPack() {
 /*       InputStream is = null;
        SAXReader saxReader = new SAXReader();
        try {
            is = getAssets().open("Emoticons.plist");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Document emoticonFile = null;
        try {
            emoticonFile = saxReader.read(is);
        } catch (DocumentException e) {
            e.printStackTrace();
            return;
        }
        Node root = emoticonFile.selectSingleNode("/plist/dict/dict");
        List<?> keyList = root.selectNodes("key");
        List<?> dictonaryList = root.selectNodes("dict");
        Iterator<?> keys = keyList.iterator();
        Iterator<?> dicts = dictonaryList.iterator();
        while (keys.hasNext()) {
            Element keyEntry = (Element) keys.next();
            String key = keyEntry.getText();
            Element dict = (Element) dicts.next();
            String name = dict.selectSingleNode("string").getText();
            final List<String> equivs = new ArrayList<String>();
            final List<?> equivilants = dict.selectNodes("array/string");
            for (Object equivilant : equivilants) {
                Element equElement = (Element) equivilant;
                String equElementString = equElement.getText();
                if (TextUtils.isEmpty(equElementString)) {
                    continue;
                }
                equivs.add(equElementString);
            }
            Emoticon emoticon = new Emoticon(key, name, equivs);
            try {
                InputStream inputStream = getAssets().open(
                        emoticon.getImageName());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if (null != bitmap) {
                    Bitmap scalBitmap = Bitmap.createScaledBitmap(bitmap,
                            dip2px(emotion_size), dip2px(emotion_size), true);
                    if (bitmap != scalBitmap) {
                        bitmap.recycle();
                        bitmap = null;
                        bitmap = scalBitmap;
                    }
                    emoticon.setBitmap(bitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            mEmoticonsMap2.put(emoticon.getEquivalants().get(0), emoticon);
        }*/
    }

    static int dip2px(int dipValue) {
        float reSize = BaseApplication.getInstance().getResources()
                .getDisplayMetrics().density;
        return (int) ((dipValue * reSize) + 0.5);
    }
    
}
