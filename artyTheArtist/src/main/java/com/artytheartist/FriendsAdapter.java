package com.artytheartist;

import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class FriendsAdapter extends SimpleAdapter {
	Resources res;
	private int[] colors = new int[] { 0x30FFFFFF, 0x3066FF66, 0x30FFFF00,
			0x30FFFF33, 0x30357EC7, 0x300000FF, 0x30000000 };
	List<HashMap<String, String>> friendslist;
	String mMemberid = AppSettings.getMemberid(MainActivity.getContext());

	public FriendsAdapter(Context context, List<HashMap<String, String>> items,
			int resource, String[] from, int[] to) {
		super(context, items, resource, from, to);
		friendslist = items;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		// Toast.makeText(mainActivity.getContext(), "Notify Changed " ,
		// Toast.LENGTH_LONG).show();

		boolean _NotifyOnChange = true;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);

		TextView fullname = (TextView) view.findViewById(R.id.fullname);
		TextView categoryname = (TextView) view.findViewById(R.id.categoryname);
		TextView sortnumber = (TextView) view.findViewById(R.id.sortnumber);
		TextView positionid = (TextView) view.findViewById(R.id.positionid);
		TextView mLoggedin = (TextView) view.findViewById(R.id.loggedin);

		ImageView showactive = (ImageView) view.findViewById(R.id.showactive);
		ImageView loggedin = (ImageView) view.findViewById(R.id.showlogin);

		loggedin.setVisibility(View.GONE);
		showactive.setVisibility(View.GONE);
		CharSequence pos = positionid.getText();
		view.setTag(pos);

		if (mLoggedin.getText().equals("Y")){
			loggedin.setVisibility(View.VISIBLE);
		} else {
			loggedin.setVisibility(View.INVISIBLE);
		}
			
		
		
		int li_positionid = Integer.parseInt((String) positionid.getText());
		int li_sortorder = Integer.parseInt((String) sortnumber.getText());

		if (li_positionid == -1) {
			view.setBackgroundColor(colors[4]);
			fullname.setClickable(true);
			fullname.setGravity(1); // Center Text
			fullname.setTypeface(null, Typeface.BOLD);
			categoryname.setVisibility(View.VISIBLE);
			categoryname.setClickable(true);
			mLoggedin.setClickable(true);
			mLoggedin.setVisibility(View.VISIBLE);
			
			if (li_sortorder == 3) {
				categoryname.setText(R.string.showhidefriend);
				mLoggedin.setText(R.string.loggedin);
				fullname.setText(R.string.activefriends);
			} else if (li_sortorder == 2) {
				categoryname.setVisibility(View.GONE);
				mLoggedin.setVisibility(View.GONE);
				fullname.setClickable(false);
				fullname.setText(R.string.waitforacceptance);
			} else if (li_sortorder == 1) {
				categoryname.setText(R.string.friendtoaccept);
				fullname.setText(R.string.friendtoaccept);
				
			}

			else if (li_sortorder == 0) {
				categoryname.setText(R.string.friendtoinvite);
				fullname.setText(R.string.friendtoinvite);
				
			}

		} else {
			fullname.setGravity(3); // Set Text to left
			fullname.setTypeface(null, Typeface.NORMAL);
			fullname.setClickable(false);
			categoryname.setClickable(false);
			categoryname.setVisibility(View.GONE);
			mLoggedin.setVisibility(View.GONE);
			switch (li_sortorder) {
			case 3: // Friend
				view.setBackgroundColor(colors[1]);
				HashMap<String, String> showlocation = friendslist
						.get(position);
				if (showlocation.get("showlocation").contentEquals("1")) {
					showactive.setVisibility(View.VISIBLE);
				} else {
					showactive.setVisibility(View.INVISIBLE);
				}
				break;
			case 1: // Accept Friend
				view.setBackgroundColor(colors[2]);
				break;
			case 2: // Awaiting Accept
				view.setBackgroundColor(colors[3]);
				break;
			case 0: // Invite
				view.setBackgroundColor(colors[0]);
				break;
			}
		}

		return view;
	}

}



