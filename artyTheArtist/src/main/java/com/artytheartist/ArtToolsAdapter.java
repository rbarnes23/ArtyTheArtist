package com.artytheartist;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;


public class ArtToolsAdapter extends BaseAdapter {
    private Context mContext;
    private int mCurrentPosition;
    public ArtToolsAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }
    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(40, 40));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setTag(position);
            imageView.setOnClickListener(new OnClickListener() {
			
				public void onClick(View v) {
					mCurrentPosition =(Integer) v.getTag();
					MainActivity.setToast(Integer.toString(mCurrentPosition), 0);
				}
			});
            //imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    // references to our images
    private Integer[] mThumbIds = {
            R.drawable.appicon36, R.drawable.colorline,
            R.drawable.brush, R.drawable.clearme,
            R.drawable.drawerasericon, R.drawable.eyedropper,
    };
}
