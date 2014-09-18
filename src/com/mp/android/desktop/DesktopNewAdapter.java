package com.mp.android.desktop;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mp.android.R;

public class DesktopNewAdapter extends BaseAdapter  {
	//private Context mContext = null;
	private List<String> mChild = null;
	private LayoutInflater mInflater = null;
	
	public DesktopNewAdapter(Context pContext, List<String> pList) {
		//this.mContext = pContext;
		this.mChild = pList;
		this.mInflater = LayoutInflater.from(pContext);
		// TODO Auto-generated constructor stub
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.desktop_list_child, null);
			holder = new ViewHolder();
			holder.mChildName = (TextView) convertView.findViewById(R.id.desktop_list_child_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.mChildName.setText(mChild.get(position).toString());
		if (position == Desktop.mChooesId) {
			convertView.setBackgroundResource(R.drawable.desktop_list_item_pressed);
		} else {
			convertView.setBackgroundResource(R.drawable.desktop_list_item_bg);
		}
		return convertView;
	}

	private class ViewHolder {
		private TextView mChildName;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mChild.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mChild.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
}
