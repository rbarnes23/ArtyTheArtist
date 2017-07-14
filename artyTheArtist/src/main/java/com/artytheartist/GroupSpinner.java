package com.artytheartist;

import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class GroupSpinner extends Spinner {
	String id = "";
	Context mContext;
	int mSelectedPosition;
	private GroupAdapter mSpinnerAdapter;
	   String[] strings = {"CoderzHeaven","Google",
	            "Microsoft", "Apple", "Yahoo","Samsung"};
	 
	    String[] subs = {"Heaven of all working codes ","Google sub",
	            "Microsoft sub", "Apple sub", "Yahoo sub","Samsung sub"};
		   // references to our images
	    int [] arr_images = {
	            R.drawable.appicon36, R.drawable.colorline,
	            R.drawable.brush, R.drawable.clearme,
	            R.drawable.drawerasericon, R.drawable.eyedropper,
	    };
	 
	 
/*	    int arr_images[] = { R.drawable.arrow_left,
	                         R.drawable.arrowleft_fancy, R.drawable.bg_striped_img,
	                         R.drawable.eyedropper2, R.drawable.protractor, R.drawable.rounded_edges_red};	
*/
	    public GroupSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext =context;
		mSpinnerAdapter = new GroupAdapter(context, R.layout.galleryitem2, strings);
		this.setAdapter(mSpinnerAdapter);
	}


	public String getSelectionId() {
		return id;
	}
    public class GroupAdapter extends ArrayAdapter<String>{
    	 
        public GroupAdapter(Context context, int textViewResourceId,   String[] objects) {
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
        	if(convertView==null){
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.galleryitem2, parent, false);
        	}
/*            TextView label=(TextView)view.findViewById(R.id.company);
            label.setText(strings[position]);
 
            TextView sub=(TextView)view.findViewById(R.id.sub);
            sub.setText(subs[position]);
*/
        	
            ImageView icon=(ImageView)view.findViewById(R.id.thumbImage2);
            //icon.setLayoutParams(new ImageView.LayoutParams(40, 40));
            //icon.setScaleType(ImageView.ScaleType.FIT_CENTER);

            icon.setImageResource(arr_images[position]);
            icon.setTag(position);
    		icon.setOnClickListener(new OnClickListener() {

    			public void onClick(View v) {
    				mSelectedPosition = (Integer) v.getTag();

    				switch(mSelectedPosition){
    				case 1:
    					mContext.getContentResolver().notifyChange(
    							MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null);
    					Intent ImageIntent = new Intent(mContext, Images.class);
    					mContext.startActivity(ImageIntent);
    					break;
    				case 2:
    					MainActivity.setToast("TTT", 0);
    					break;
    				default:
    					break;
    				}
    				MainActivity.setToast("TEST",0);
    				
    			}
    		});

            return view;
            }
        }	
}