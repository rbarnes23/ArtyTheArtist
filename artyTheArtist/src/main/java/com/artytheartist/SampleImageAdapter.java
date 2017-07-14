package com.artytheartist;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class SampleImageAdapter extends SimpleAdapter {
	ArrayList<HashMap<String, Object>> mArraylist;
	private Context mContext;
	private ArrayList<Bitmap> mBitmapArray = new ArrayList<Bitmap>();
	ViewHolder holder;
	private String[] mSlideList;
	private int mOption;

	public SampleImageAdapter(Context context,
			ArrayList<HashMap<String, Object>> _arraylist, int resource,
			String[] from, int[] to) {
		super(context, _arraylist, resource, from, to);
		mArraylist = _arraylist;
		mContext = context;
		//LoadImages task=new LoadImages();
		//task.execute();
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
	public void setNotesOption(int option) {
		mOption=option;
	}

	public String[] getSlideList() {
		return mSlideList;
	}

	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;		

		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = mInflater.inflate(R.layout.samplegalleryitem, null);
			holder = new ViewHolder(view);
			mSlideList= new String[mArraylist.size()];
			view.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.thumbimage.setTag(position);
		String name = (String) mArraylist.get(position).get("filename");
		mSlideList[position]= name.length()>0?name:"";
		holder.filename.setText(name);
		
		holder.imageno.setText((String) mArraylist.get(position).get("imageno"));
		holder.notes.setText((String) mArraylist.get(position).get("notes"));
		Bitmap bm = decodeFile(new File(name), 30);
		if (bm != null) {
			mBitmapArray.add(position, bm);
		//if(mBitmapArray.size()>0){
			holder.thumbimage.setImageBitmap(mBitmapArray.get(position));
		}
		holder.notes.setVisibility(View.VISIBLE);
		
		if (mOption == 1) {
			holder.notes.setVisibility(View.GONE);
		} else if (mOption == 2) {
			//holder.thumbimage.setFocusable(false);
			holder.notes.setFocusable(true);
			holder.notes.setFocusableInTouchMode(true);
			holder.notes.setEnabled(true);
			holder.notes.requestFocus();

		} else {
			//Default 0			
			holder.notes.setFocusable(false);
			holder.notes.setFocusableInTouchMode(false);
			holder.notes.setEnabled(false);
			holder.thumbimage.requestFocus();

		}

//		holder.thumbimage.setImageBitmap((Bitmap) mArraylist.get(position)
//		 .get("thumbimage"));

		return view;
	}

	public Bitmap decodeFile(File f, int imagesize) {
		// if (imagesize==0){imagesize=IMAGE_MAX_SIZE;}
		Bitmap b = null;
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;

			FileInputStream fis = new FileInputStream(f);
			BitmapFactory.decodeStream(fis, null, o);
			fis.close();

			int scale = 1;

			if (o.outHeight > imagesize || o.outWidth > imagesize) {
				scale = (int) Math.pow(
						2.0,
						(int) Math.round(Math.log(imagesize
								/ (double) Math.max(o.outHeight, o.outWidth))
								/ Math.log(0.5)));
			}

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			fis = new FileInputStream(f);
			b = BitmapFactory.decodeStream(fis, null, o2);
			fis.close();
			// freememory();
		} catch (IOException e) {
		}
		return b;
	}

	class ViewHolder {
		public ImageView thumbimage;
		public TextView filename;
		public TextView imageno;
		public EditText notes;
		public ViewHolder(View view) {
			thumbimage = (ImageView) view.findViewById(R.id.thumbimage);
			filename = (TextView) view.findViewById(R.id.filename);
			imageno = (TextView) view.findViewById(R.id.imageno);
			notes = (EditText) view.findViewById(R.id.notes);
		}
	}
	public class LoadImages extends AsyncTask<Void, Bitmap, Void>
	{
		int i=0;
		@Override
		protected Void doInBackground(Void... params) {
			for (i=0;i<getCount();i++){
				String name = (String) mArraylist.get(i).get("filename");
				Bitmap bm = decodeFile(new File(name), 30);
				publishProgress(bm);
				
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
	}
		@Override
		protected void onProgressUpdate(Bitmap... values) {
			super.onProgressUpdate(values);
			Bitmap bm = values[0];
			if (bm != null) {
//				mBitmapArray.add(i, bm);
				holder.thumbimage.setImageBitmap(mBitmapArray.get(i));
			}
	
	}}

}
