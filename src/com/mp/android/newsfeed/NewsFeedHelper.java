package com.mp.android.newsfeed;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executor;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.mp.android.BaseApplication;
import com.mp.android.R;
import com.mp.android.RequestListener;
import com.mp.android.statusnet.MustardDbAdapter;
import com.mp.android.statusnet.provider.StatusNet;
import com.mp.android.statusnet.statusnet.Status;
import com.mp.android.statusnet.util.MustardException;
import com.mp.android.util.Util;

public class NewsFeedHelper {
//    public void asyncGet(Executor pool, final NewsFeedRequestParam param,
//            final RequestListener<NewsFeedResponseBean> listener) {
//        pool.execute(new Runnable() {
//
//            public void run() {
//                listener.onStart();
//                NewsFeedResponseBean bean = get(param);
//                listener.onComplete(bean);
//            }
//        });
//    }

    public void asyncGetNotice(final NoticeRequestParam param, Executor pool,
            final RequestListener<NoticeResponseBean> listener) {
        pool.execute(new Runnable() {
            
            public void run() {
                listener.onStart();
                System.out.println("param.STATUSES_FETCHER ="+param.STATUSES_FETCHER_TYPE);
                switch (param.STATUSES_FETCHER_TYPE) {
                case STATUSES_FETCHER:
                    StatusesFetcher(param, listener);
                    break;
                case MULTI_STATUSES_FETCHER:
                    MultiStatusesFetcher(param, listener);
                    break;
                case STATUSES_LOAD_MORE:
                    StatusesLoadMore(param, listener);
                    break;
                case MERGED_STATUSES_LOAD_MORE:
                    MergedStatusesLoadMore(param, listener);
                    break;
                default:
                    break;
                }
                
            }
        });
    }

    private void StatusesFetcher(final NoticeRequestParam param,
            final RequestListener<NoticeResponseBean> listener) {
        ArrayList<Status> al = null;
        boolean mGetdents = false;
        System.out.println("StatusesFetcher");
        NoticeResponseBean bean = new NoticeResponseBean(al);
        MustardDbAdapter mDbHelper = getDbAdapter(param.context);
        try {
            if (param.mStatusNet == null) {
                bean.Message = param.context.getString(R.string.str_forgetlogin);
                bean.mStatusCode = -1;
                listener.onComplete(bean);
                return;
            }
            long maxId = mDbHelper.fetchMaxStatusesId(
                    param.mStatusNet.getUserId(), param.DB_ROW_TYPE,
                    param.DB_ROW_EXTRA);
            al = param.mStatusNet.get(param.DB_ROW_TYPE, param.DB_ROW_EXTRA,
                    maxId, true);
            bean.al = al;
            if (al == null || al.size() < 1) {
                bean.Message = param.context.getString(R.string.str_newest);
                bean.mStatusCode = -1;
            } else {
                long lowestid = al.get(al.size() - 1).getNotice().getId();
                if (maxId > 0 && lowestid - 1 > maxId) {
                    mDbHelper.deleteStatuses(param.mStatusNet.getUserId(),
                            param.DB_ROW_TYPE, param.DB_ROW_EXTRA, maxId);
                }
                mGetdents = mDbHelper.createStatuses(
                        param.mStatusNet.getUserId(), param.DB_ROW_TYPE,
                        param.DB_ROW_EXTRA, al);
            }
        } catch (MustardException e) {
            bean.Message = e.getMessage();
            bean.mStatusCode = e.getCode();
        } catch (Exception e) {
            bean.Message = e.getMessage();
            bean.mStatusCode = -1;
        } finally {
            if (null != mDbHelper) {
                mDbHelper.close();
                mDbHelper = null;
            }
        }
        bean.al = al;
        listener.onComplete(bean);
    }

    private void StatusesLoadMore(final NoticeRequestParam param,
            final RequestListener<NoticeResponseBean> listener) {
        boolean mGetdents = false;
        MustardDbAdapter mDbHelper = getDbAdapter(param.context);
        NoticeResponseBean bean = new NoticeResponseBean(null);
        if (param.mStatusNet == null) {
            bean.Message = param.context.getString(R.string.str_forgetlogin);;
            bean.mStatusCode = -1;
            listener.onComplete(bean);
            return;
        }
        long minId = mDbHelper.fetchMinStatusesId(param.mStatusNet.getUserId(),
                param.DB_ROW_TYPE, param.DB_ROW_EXTRA);
        if (minId - 1 < 1) {
            bean.mStatusCode = -1;
            bean.Message = param.context.getString(R.string.str_newest2);
            listener.onComplete(bean);
            return;
        }
        ArrayList<Status> al = null;
        try {
            al = param.mStatusNet.get(param.DB_ROW_TYPE, param.DB_ROW_EXTRA,
                    minId - 1, false);
            if (al == null) {
                bean.Message = param.context.getString(R.string.str_newest2);
                bean.mStatusCode = -1;
            } else if (al.size() < 1) {
                bean.Message = param.context.getString(R.string.str_newest2);
                bean.mStatusCode = -1;
            } else {
                mGetdents = mDbHelper.createStatuses(
                        param.mStatusNet.getUserId(), param.DB_ROW_TYPE,
                        param.DB_ROW_EXTRA, al);
            }
        } catch (Exception e) {
            bean.mStatusCode = -1;
            bean.Message = e.getMessage();
        } finally {
            if (null != mDbHelper) {
                mDbHelper.close();
                mDbHelper = null;
            }
        }
        bean.al = al;
        listener.onComplete(bean);
    }

    protected HashMap<Long, Boolean> mHMNoMoreDents = new HashMap<Long, Boolean>();

    private void MergedStatusesLoadMore(final NoticeRequestParam param,
            final RequestListener<NoticeResponseBean> listener) {
        boolean mGetdents = false;
        boolean haveAtLeastOneStatus = false;
        BaseApplication _ma = (BaseApplication) ((Activity) param.context)
                .getApplication();
        MustardDbAdapter mDbHelper = getDbAdapter(param.context);
        Cursor c = mDbHelper.fetchAllAccountsToMerge();
        NoticeResponseBean bean = new NoticeResponseBean(null);
        while (c.moveToNext()) {
            long _aid = c.getLong(c.getColumnIndex(MustardDbAdapter.KEY_ROWID));
            if (null == mHMNoMoreDents) {
                break;
            }
            if (mHMNoMoreDents.containsKey(_aid)) {
                continue;
            }
            ArrayList<Status> al = null;
            StatusNet _sn = _ma.checkAccount(mDbHelper, false, _aid);
            long minId = mDbHelper.fetchMinStatusesId(_aid, param.DB_ROW_TYPE,
                    param.DB_ROW_EXTRA);
            if (minId - 1 < 1) {
                bean.Message = param.context.getString(R.string.str_newest3);
                bean.mStatusCode = -1;
                break;
            }
            try {
                al = _sn.get(param.DB_ROW_TYPE, _sn.getMUsername(), minId - 1,
                        false);
                bean.al = al;
            } catch (MustardException e) {
                mHMNoMoreDents.put(_aid, true);
                bean.Message = e.getMessage();
                bean.mStatusCode = e.getCode();
                listener.onComplete(bean);
            }
            if (al == null || al.size() < 1) {
                continue;
            } else {
                mGetdents = mDbHelper.createStatuses(_aid, param.DB_ROW_TYPE,
                        param.DB_ROW_EXTRA, al);
            }
        }
        if (null != c) {
            c.close();
            c = null;
        }
        listener.onComplete(bean);
    }

    private void MultiStatusesFetcher(final NoticeRequestParam param,
            final RequestListener<NoticeResponseBean> listener) {
        boolean mGetdents = false;
        MustardDbAdapter mDbHelper = null;
        NoticeResponseBean bean = new NoticeResponseBean(null);
        try {
            listener.onStart();
            mDbHelper = getDbAdapter(param.context);
            Activity activity = (Activity) (param.context);
            BaseApplication _ma = (BaseApplication) activity.getApplication();
            StatusNet _sn = null;
            boolean haveAtLeastOneAccount = false;
            boolean haveAtLeastOneStatus = false;
            Cursor c = mDbHelper.fetchAllAccountsToMerge();
            while (c.moveToNext()) {
                ArrayList<Status> al = null;
                haveAtLeastOneAccount = true;
                long _aid = c.getLong(c
                        .getColumnIndex(MustardDbAdapter.KEY_ROWID));
                _sn = _ma.checkAccount(mDbHelper, false, _aid);
                long maxId = mDbHelper.fetchMaxStatusesId(_aid,
                        param.DB_ROW_TYPE, param.DB_ROW_EXTRA);
                al = _sn.get(param.DB_ROW_TYPE, param.DB_ROW_EXTRA, maxId - 1,
                        true);
                if (al == null || al.size() < 1) {
                    continue;
                } else {
                    haveAtLeastOneStatus = true;
                    long lowestid = al.get(al.size() - 1).getNotice().getId();
                    if (maxId > 0 && lowestid > maxId) {
                        mDbHelper.deleteStatuses(_aid, param.DB_ROW_TYPE,
                                param.DB_ROW_EXTRA, maxId);
                    }
                    mGetdents = mDbHelper.createStatuses(_aid,
                            param.DB_ROW_TYPE, param.DB_ROW_EXTRA, al);
                }
            }
            c.close();
            if (!haveAtLeastOneAccount) {
                bean.mStatusCode = -10;
                listener.onComplete(bean);
                return;
            }
            if (!haveAtLeastOneStatus) {
                bean.mStatusCode = 0;
                listener.onComplete(bean);
                return;
            }
        } catch (MustardException e) {
            e.printStackTrace();
            bean.mStatusCode = -1;
            bean.Message = e.getMessage();
            listener.onComplete(bean);
            return;
        } finally {
            mDbHelper.close();
        }
        listener.onComplete(bean);
    }

    protected MustardDbAdapter getDbAdapter(Context context) {
        MustardDbAdapter dbAdapter = new MustardDbAdapter(context);
        dbAdapter.open();
        return dbAdapter;
    }

//    public NewsFeedResponseBean get(NewsFeedRequestParam param) {
//        String response = null;
//        response = Util.GetJson(param.getParams());
//        return new NewsFeedResponseBean(response);
//    }

//    public void asyncPublish(Executor pool,
//            final NewsFeedPublishRequestParam param,
//            final RequestListener<NewsFeedPublishResponseBean> listener) {
//        pool.execute(new Runnable() {
//
//            public void run() {
//                listener.onStart();
//                NewsFeedPublishResponseBean bean = publish(param);
//                listener.onComplete(bean);
//            }
//        });
//    }

//    public NewsFeedPublishResponseBean publish(NewsFeedPublishRequestParam param) {
//        String response = null;
//        response = Util.GetJson(param.getParams());
//        return new NewsFeedPublishResponseBean(response);
//    }
}
