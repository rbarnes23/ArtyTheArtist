/**
 * 
 */
package com.artytheartist;

/**
 * @author Rick
 *
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.tokenlibrary.Token;

public class TCPAdapter extends BaseAdapter {
	private ArrayList<String> mListItems;
	private LayoutInflater mLayoutInflater;
	private Context context;
	private int[] colors = new int[] { 0x3000FFFF, 0x30FFFFFF };

	public TCPAdapter(Context context, ArrayList<String> arrayList) {
		this.context = context;
		mListItems = arrayList;

		// get the layout inflater
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// getCount() represents how many items are in the list
		return mListItems.size();
	}

	@Override
	// get the data of an item from a specific position
	// i represents the position of the item in the list
	public Object getItem(int i) {
		return null;
	}

	@Override
	// get the position id of the item from the list
	public long getItemId(int i) {
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		// check to see if the reused view is null or not, if is not null then
		// reuse it
		if (view == null) {
			view = mLayoutInflater.inflate(R.layout.tcplist_item, null);

		}
		String message = null;
		final TextView itemName = (TextView) view
				.findViewById(R.id.list_item_text_view);
		final ImageView itemToDisplay= (ImageView) view
				.findViewById(R.id.thumbImageToDisplay);


		// get the string item from the position "position" from array list to
		// put it on the TextView
		final String stringItem = mListItems.get(position);
		if (position>=0){
		//mListItems.remove(position);
		}
		if (stringItem != null) {
			if (stringItem.contains("getsessions")) {
				message=stringItem;
				
			}			
			if (stringItem.contains("getmessage")) {
				// get the image to display
				//	message =tcpDraw.chatDraw(stringItem);
				Token chatToken = new Token();
				chatToken.createToken(stringItem);
				message = chatToken.getString("message");

				try {
					JSONArray jArray=new JSONArray(message);
					for (int i =0;i<jArray.length();i++){
						message=jArray.getJSONObject(i).optString("BKG");
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}

			if (itemName != null) {
				// set the item name on the TextView
				if (message!=null){
					if (message.length()<1000){itemName.setText(stringItem);}
				}				
			}

			if (itemToDisplay != null) {
				// set the item name on the TextView
				Bitmap bm =getBitmapFromString(message);
				BitmapDrawable mBackGround = new BitmapDrawable(bm);
				if (mBackGround != null) {
					itemToDisplay.setBackgroundDrawable(mBackGround);
				}
			}

		}


		// this method must return the view corresponding to the data at the
		// specified position.
		return view;

	}

	//Take the background image passed in the json string,decode and turn into bitmap
	private Bitmap getBitmapFromString(String jsonString) {
		/*
		 * This Function converts the String back to Bitmap
		 */
		byte[] decodedString = Base64.decode(jsonString);
		Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0,
				decodedString.length);
		return decodedByte;
	}

}