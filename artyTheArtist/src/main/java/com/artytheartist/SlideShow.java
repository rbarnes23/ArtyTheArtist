package com.artytheartist;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;

public class SlideShow {
	private long mInterval;
	private boolean mSlideShow;
	private int mCount;
	private static final int IMAGE_MAX_SIZE = 450;

	public void startSlideShow(final String[] slides,int countToStartAt, final long interval) {	
		Timer timer= new Timer();
		final Handler mHandler = new Handler();
		final int cnt = slides.length;
		mInterval=interval;
		timer.scheduleAtFixedRate(new TimerTask() {

		    public void run() {

				mHandler.post(new Runnable() {

					@Override
					public void run() {
						restore(slides[mCount]);
						if (mCount == cnt - 1) {
							mCount = 0;
						} else {
							mCount++;
						}

					}
				});


		    }

		    }, 0, interval);
			}

	public Bitmap restore(String aFileName) {
		// Restore Bitmap to background
		int mReturn = -1;

		File filename = new File(aFileName);
		Bitmap bm = decodeFile(filename);
			return bm;
	}
	public Bitmap decodeFile(File f) {
		Bitmap b = null;
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;

			FileInputStream fis = new FileInputStream(f);
			BitmapFactory.decodeStream(fis, null, o);
			fis.close();

			int scale = 1;
			if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
				scale = (int) Math.pow(
						2,
						(int) Math.round(Math.log(IMAGE_MAX_SIZE
								/ (double) Math.max(o.outHeight, o.outWidth))
								/ Math.log(0.5)));
			}

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			fis = new FileInputStream(f);
			b = BitmapFactory.decodeStream(fis, null, o2);
			fis.close();
		} catch (IOException e) {
		}
		return b;
	}

	
	
}

