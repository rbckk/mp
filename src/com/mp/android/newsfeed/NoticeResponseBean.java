package com.mp.android.newsfeed;

import java.util.ArrayList;

import org.apache.http.HttpStatus;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.mp.android.RenRenData;
import com.mp.android.ResponseBean;
import com.mp.android.statusnet.MustardDbAdapter;
import com.mp.android.statusnet.Preferences;
import com.mp.android.statusnet.statusnet.RowStatus;
import com.mp.android.statusnet.statusnet.Status;
import com.mp.android.util.Util;

public class NoticeResponseBean extends ResponseBean {
    public ArrayList<Status> al;
    public int mStatusCode = HttpStatus.SC_OK;
    public String Message = "";
    public NoticeResponseBean() {

    }

    public NoticeResponseBean(ArrayList<Status> al) {
        this.al = al;
    }

    public void Resolve(Context context, int DB_ROW_TYPE, String DB_ROW_EXTRA) {
        if (HttpStatus.SC_OK != mStatusCode) {
            return;
        }
        Util.fillData(context, DB_ROW_TYPE, DB_ROW_EXTRA);
    }


}
