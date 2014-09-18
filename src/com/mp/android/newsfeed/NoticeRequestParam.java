package com.mp.android.newsfeed;

import com.mp.android.statusnet.provider.StatusNet;

import android.content.Context;

public class NoticeRequestParam {
    public Context context;
    public int DB_ROW_TYPE;
    public String DB_ROW_EXTRA = "";
    public StatusNet mStatusNet;
    public FETCHER_STATUSES_TYPE STATUSES_FETCHER_TYPE = FETCHER_STATUSES_TYPE.STATUSES_FETCHER;

    public enum FETCHER_STATUSES_TYPE {
        STATUSES_FETCHER, MULTI_STATUSES_FETCHER, MERGED_STATUSES_LOAD_MORE, STATUSES_LOAD_MORE
    }
}
