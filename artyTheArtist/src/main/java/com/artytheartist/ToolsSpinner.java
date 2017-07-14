package com.artytheartist;

import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;


public class ToolsSpinner extends Spinner {
	String id = "";
	Context mContext;
	int mSelectedPosition;
	private ToolsAdapter mSpinnerAdapter;
	   String[] strings = {"CoderzHeaven","Google",
	            "Microsoft", "Apple", "Yahoo","Samsung"};
	 
	    String[] subs = {"Heaven of all working codes ","Google sub",
	            "Microsoft sub", "Apple sub", "Yahoo sub","Samsung sub"};
		   // references to our images
	    int [] arr_images = {
	            R.drawable.appicon36, R.drawable.colorline,
	            R.drawable.brush, R.drawable.clearme,
	            R.drawable.drawerasericon, R.drawable.eyedropper
	    };
	 
	 
	    public ToolsSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext =context;
		mSpinnerAdapter = new ToolsAdapter(context, R.layout.galleryitem2, strings);
		mSpinnerAdapter.setDropDownViewResource(R.layout.galleryitem2);
		this.setAdapter(mSpinnerAdapter);
	}


	public String getSelectionId() {
		return id;
	}
    public class ToolsAdapter extends ArrayAdapter<String>{
    	 
        public ToolsAdapter(Context context, int textViewResourceId,   String[] objects) {
            super(context, textViewResourceId, objects);
        }
 
        @Override
        public View getDropDownView(int position, View convertView,ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }
 
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }
 
        public View getCustomView(int position, View convertView, ViewGroup parent) {
        	View view = convertView;
        	OnItemSelectedListener onItemSelectedListener= new OnItemSelectedListener(){

    			@Override
    			public void onItemSelected(AdapterView<?> parentView, View view,
    					int position, long id) {
       				mSelectedPosition = position;

    				switch(mSelectedPosition){
    				case 1:
    					mContext.getContentResolver().notifyChange(
    							MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null);
    					Intent ImageIntent = new Intent(mContext, Images.class);
    					mContext.startActivity(ImageIntent);
    					break;
    				case 9:
    					
    					break;
    				default:
    					MainActivity.setToast(Integer.toString(mSelectedPosition), 0);
    					break;
    				}
    				
    			}

    			@Override
    			public void onNothingSelected(AdapterView<?> arg0) {
    				// TODO Auto-generated method stub
    				
    			}};

    		setOnItemSelectedListener(onItemSelectedListener);
 
        	if(convertView==null){
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.galleryitem2, parent, false);
            
        	}
        	
        	
            ImageView icon=(ImageView)view.findViewById(R.id.thumbImage2);
            
            icon.setScaleType(ImageView.ScaleType.FIT_CENTER);

            icon.setImageResource(arr_images[position]);
            icon.setTag(position);
            return view;
            }
        }	
}