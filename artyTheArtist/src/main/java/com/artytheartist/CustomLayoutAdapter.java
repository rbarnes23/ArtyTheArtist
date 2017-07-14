package com.artytheartist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.artytheartist.MemberAdapter.ViewHolder;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class CustomLayoutAdapter extends SimpleAdapter {
	Context mContext;
	ArrayList<HashMap<String, View>> mCustomList;	

	public CustomLayoutAdapter(Context context,
			ArrayList<HashMap<String, View>> customList) {
		super(context, customList, 0, null, null);
		mCustomList = customList;
		mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;// super.getView(position, convertView, parent);

		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = mInflater.inflate(R.layout.viewlayout, null);
			holder = new ViewHolder(view);
			view.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.customView.setVisibility(View.VISIBLE);
		Object h  =(Object) mCustomList.get(position).get(
				"header");
		view = (View) h;
		
int vc=getCount();
		return view;
	}

	class ViewHolder {
		public View customView;
		public TextView viewInfo;

		public ViewHolder(View view) {
			customView = (View) view.findViewById(R.id.viewObject);
			viewInfo = (TextView) view.findViewById(R.id.viewInfo);
			/*
			 * membercheckbox.setOnClickListener(new OnClickListener() {
			 * 
			 * public void onClick(View v) { // TODO Auto-generated method stub
			 * CheckBox cb = (CheckBox) v; if (cb.isChecked()){ //int id =
			 * chk.getId(); cb.setChecked(true); } else { cb.setChecked(false);
			 * } } });
			 */
		}

	}
}