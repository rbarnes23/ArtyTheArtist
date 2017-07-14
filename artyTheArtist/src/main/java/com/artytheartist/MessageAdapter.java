package com.artytheartist;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

public class MessageAdapter extends SimpleAdapter {
	Resources res;
	private int[] colors = new int[] { 0x3066FF66, 0x30FFFF6F };
	private int checked=-3;

	public MessageAdapter(Context context,
			ArrayList<HashMap<String, CharSequence>> jsonMessagelist,
			int resource, String[] from, int[] to) {
		super(context, jsonMessagelist, resource, from, to);
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		boolean _NotifyOnChange = true;
		

	}

	public void SetCheckbox(int _checked) {
		// 0-do nothing,1-check all;2-uncheck all
		checked = _checked;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		
		
		
		CheckBox chk = (CheckBox) view.findViewById(R.id.membercheckbox);
		chk.setTag(position);
		
		if (checked == -1) {
			chk.setChecked(true);
		}else if (checked == -2) {
			chk.setChecked(false);
		}
		chk.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				CheckBox cb = (CheckBox) v;
				if (cb.isChecked()){
				//int id = chk.getId();
					cb.setChecked(false);
				} else {
					cb.setChecked(true);
				}
			}
		});

/*		else
			if (position==checked){
			boolean ischecked= (chk.isChecked()?false:true);
			chk.setChecked(ischecked);
			
		}
*/		
		// chk.setVisibility(position >= 0 ? View.VISIBLE
		// : View.GONE);

		int colorPos = position % colors.length;
		view.setBackgroundColor(colors[colorPos]);

		return view;
	}
	class ViewHolder {
		CheckBox membercheckbox;
		int id;
	}

}
