package com.artytheartist;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class SpinnerImageAdapter extends SimpleAdapter {
	ArrayList<Drawable> mArraylist;
	private Context mContext;

	ViewHolder holder;
	

	public SpinnerImageAdapter(Context context,
			ArrayList<Drawable> arrayList, int resource,
			String[] i, int[] to) {
		super(context, null, resource, i, to);//context, mDrawableList, resource, i, to);
		mArraylist = arrayList;
		mContext = context;
	}


	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mArraylist.size();
	}

	@Override
	public Object getItem(int pos) {
		return mArraylist.get(pos);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		View view;
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = mInflater.inflate(R.layout.galleryitem2, null);
			holder = new ViewHolder(view);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.thumbimage.setId(position);
		
			
		
	
		  holder.thumbimage.setImageResource(mThumbIds[position]);
		

		return convertView;
	}
	
/*	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;		

		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = mInflater.inflate(R.layout.galleryitem2, null);
			
			holder = new ViewHolder(view);

			//view.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		//holder.thumbimage.setTag(position);
		
//Drawable test= (Drawable) mArraylist.get(position).;
//holder.thumbimage.setImageResource(mThumbIds[position]);//Drawable((Drawable) getItem(position));
//ImageView thumbimage = (ImageView) parent.findViewById(R.id.thumbImage);
//thumbimage.setBackgroundDrawable(test);

		return view;
	}
*/

	class ViewHolder {
		public ImageView thumbimage;
		public ViewHolder(View view) {
			thumbimage = (ImageView) view.findViewById(R.id.thumbImage2);
		}
	}
	   // references to our images
    private Integer[] mThumbIds = {
            R.drawable.appicon36, R.drawable.colorline,
            R.drawable.brush, R.drawable.clearme,
            R.drawable.drawerasericon, R.drawable.eyedropper,
    };

}
