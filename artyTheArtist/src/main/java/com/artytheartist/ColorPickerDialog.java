package com.artytheartist;

/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.HashSet;
import java.util.Iterator;
import android.os.Bundle;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ColorPickerDialog extends Dialog {

	public interface OnColorChangedListener {
		void colorChanged(int color, int option, int brushopacity,
				int backgroundopacity);

	}

	private OnColorChangedListener mListener;
	private int mInitialColor;
	private RadioGroup mRadioGroup;
	private static int mOptionSelected;
	private static HashSet<Integer> mColorSamples;
	private static int mBrushOpacity;
	private static int mBackgroundOpacity;

	private static class ColorPickerView extends View {
		private Paint mPaint, mPaint2;
		private Paint mCenterPaint;
		private final int[] mColors, mColors2;
		private OnColorChangedListener mListener;
		RectF rectF1 = new RectF(0, 0, 0, 0);
		RectF rectF2 = new RectF(0, 0, 0, 0);

		ColorPickerView(Context c, OnColorChangedListener l, int color,
				HashSet colorsamples, int brushopacity, int backgroundopacity) {
			super(c);
			mListener = l;
			mColors = new int[] { 0xFFFF0000, 0xFFFF00FF, 0xFF0000FF,
					0xFF00FFFF, 0xFF00FF00, 0xFFFFFF00, 0xFFFF0000 };
			mColors2 = new int[] { 0xFF000000, 0x00000000, 0xFFFFFFFF,
					0xD2691E00 };

			// ///////////////mColorSamples=colorsamples;

			Shader s = new SweepGradient(0, 0, mColors, null);
			Shader s2 = new SweepGradient(0, 0, mColors2, null);

			mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mPaint.setShader(s);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeWidth(40);

			mPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
			mPaint2.setShader(s2);
			mPaint2.setStyle(Paint.Style.STROKE);
			mPaint2.setStrokeWidth(40);

			mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mCenterPaint.setColor(color);
			mCenterPaint.setStrokeWidth(5);
		}

		private boolean mTrackingCenter;
		private boolean mHighlightCenter;

		@Override
		protected void onDraw(Canvas canvas) {
			float r = CENTER_X - mPaint.getStrokeWidth() * .5f;
			float r2 = CENTER_X - 60 - mPaint2.getStrokeWidth() * 0.5f;

			canvas.translate(CENTER_X, CENTER_X);
			rectF1 = new RectF(-r, -r, r, r);
			// rectF1.set(-r, -r, -r, -r);
			rectF2 = new RectF(-r2, -r2, r2, r2);
			// rectF1.set(-r2, -r2, -r2, -r2);
			canvas.drawOval(rectF1, mPaint);
			canvas.drawOval(rectF2, mPaint2);
			canvas.drawCircle(0, 0, CENTER_RADIUS, mCenterPaint);

			if (mTrackingCenter) {
				int c = mCenterPaint.getColor();
				mCenterPaint.setStyle(Paint.Style.STROKE);

				if (mHighlightCenter) {
					mCenterPaint.setAlpha(0xFF);
				} else {
					mCenterPaint.setAlpha(0x80);
				}
				canvas.drawCircle(0, 0,
						CENTER_RADIUS + mCenterPaint.getStrokeWidth(),
						mCenterPaint);

				mCenterPaint.setStyle(Paint.Style.FILL);
				mCenterPaint.setColor(c);
			}
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			setMeasuredDimension(CENTER_X * 2, CENTER_Y * 2);

		}

		private static final int CENTER_X = 156;
		private static final int CENTER_Y = 156;
		private static final int CENTER_RADIUS = 32;
		private static final int FUDGEFACTOR = 20;

		private int floatToByte(float x) {
			int n = java.lang.Math.round(x);
			return n;
		}

		private int pinToByte(int n) {
			if (n < 0) {
				n = 0;
			} else if (n > 255) {
				n = 255;
			}
			return n;
		}

		private int ave(int s, int d, float p) {
			return s + java.lang.Math.round(p * (d - s));
		}

		private int interpColor(int colors[], float unit) {
			if (unit <= 0) {
				return colors[0];
			}
			if (unit >= 1) {
				return colors[colors.length - 1];
			}

			float p = unit * (colors.length - 1);
			int i = (int) p;
			p -= i;

			// now p is just the fractional part [0...1) and i is the index
			int c0 = colors[i];
			int c1 = colors[i + 1];
			int a = ave(Color.alpha(c0), Color.alpha(c1), p);
			int r = ave(Color.red(c0), Color.red(c1), p);
			int g = ave(Color.green(c0), Color.green(c1), p);
			int b = ave(Color.blue(c0), Color.blue(c1), p);

			return Color.argb(a, r, g, b);
		}

		private int rotateColor(int color, float rad) {
			float deg = rad * 180 / 3.1415927f;
			int r = Color.red(color);
			int g = Color.green(color);
			int b = Color.blue(color);

			ColorMatrix cm = new ColorMatrix();
			ColorMatrix tmp = new ColorMatrix();

			cm.setRGB2YUV();
			tmp.setRotate(0, deg);
			cm.postConcat(tmp);
			tmp.setYUV2RGB();
			cm.postConcat(tmp);

			final float[] a = cm.getArray();

			int ir = floatToByte(a[0] * r + a[1] * g + a[2] * b);
			int ig = floatToByte(a[5] * r + a[6] * g + a[7] * b);
			int ib = floatToByte(a[10] * r + a[11] * g + a[12] * b);

			return Color.argb(Color.alpha(color), pinToByte(ir), pinToByte(ig),
					pinToByte(ib));
		}

		private static final float PI = 3.1415926f;

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			;
			float x = event.getX() - CENTER_X;
			float y = event.getY() - CENTER_Y;
			double screenCalc = Math.sqrt(x * x + y * y);
			boolean inCenter = screenCalc <= CENTER_RADIUS;
			float r = CENTER_X - mPaint.getStrokeWidth() * .5f;
			float r2 = CENTER_X - 60 - mPaint2.getStrokeWidth() * 0.5f;
			boolean innerLoop = screenCalc <= r - FUDGEFACTOR
					&& screenCalc > CENTER_RADIUS;
			boolean outerLoop = screenCalc > r2;
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mTrackingCenter = inCenter;
				if (inCenter) {
					mHighlightCenter = true;
					invalidate();
					break;
				}
			case MotionEvent.ACTION_MOVE:
				if (mTrackingCenter) {
					if (mHighlightCenter != inCenter) {
						mHighlightCenter = inCenter;
						invalidate();
					}
				} else {
					float angle = (float) java.lang.Math.atan2(y, x);
					// need to turn angle [-PI ... PI] into unit [0....1]
					float unit = angle / (2 * PI);
					if (unit < 0) {
						unit += 1;
					}
					if (outerLoop) {
						// int screenWidth=this.getWidth();
						// int screenHeight=this.getHeight();
						// if(rectF1.contains(x/screenWidth,y/screenHeight)){
						mCenterPaint.setColor(interpColor(mColors, unit));
					}
					if (innerLoop) {
						// if(rectF2.contains(x/screenWidth,y/screenHeight)){
						mCenterPaint.setColor(interpColor(mColors2, unit));
					}
					invalidate();
				}
				break;
			case MotionEvent.ACTION_UP:
				if (mTrackingCenter) {
					if (inCenter) {
						mListener.colorChanged(mCenterPaint.getColor(),
								mOptionSelected, mBrushOpacity,
								mBackgroundOpacity);

					}
					mTrackingCenter = false; // so we draw w/o halo
					invalidate();
				}
				break;
			}
			return true;
		}
	}

	public ColorPickerDialog(Context context, OnColorChangedListener listener,
			int initialColor, HashSet colorSamples, int brushopacity,
			int backgroundopacity) {
		super(context);

		mListener = listener;
		mInitialColor = initialColor;
		mColorSamples = colorSamples;
		mBrushOpacity = brushopacity;
		mBackgroundOpacity = backgroundopacity;

	}

	private void createRadioButton(ViewGroup aColorPicker) {

		mRadioGroup = new RadioGroup(getContext()); // create the RadioGroup
		mRadioGroup.setOrientation(RadioGroup.HORIZONTAL);// or
															// RadioGroup.VERTICAL

		final RadioButton rbBrush = new RadioButton(getContext());
		rbBrush.setText(R.string.colorpickerbrush);

		// rbBrush.setSelected(true);
		// rbBrush.setChecked(true);

		final RadioButton rbBackGround = new RadioButton(getContext());
		rbBackGround.setText(R.string.colorpickerbackground);

		mRadioGroup.addView(rbBrush); // the RadioButtons are added to the
										// radioGroup instead of the layout
		mRadioGroup.addView(rbBackGround);
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				final int a = mRadioGroup.getChildAt(0).getId();
				final int b = mRadioGroup.getChildAt(1).getId();
				// int mOption=mRadioGroup.getCheckedRadioButtonId();
				if (checkedId == a) {
					mOptionSelected = 0;
					rbBackGround.setChecked(false);
				} else if (checkedId == b) {
					mOptionSelected = 1;
					rbBrush.setChecked(false);
				}
			}
		});

		aColorPicker.addView(mRadioGroup);// you add the whole RadioGroup to the
											// layout
		rbBrush.setChecked(true);
	}

	private void createColorSamples(ViewGroup aColorPicker) {
		Resources res = getContext().getResources();

		ImageView leftarrow = new ImageView(getContext());
		leftarrow.setImageDrawable(res.getDrawable(R.drawable.leftarrow));
		ImageView rightarrow = new ImageView(getContext());
		rightarrow.setImageDrawable(res.getDrawable(R.drawable.rightarrow));

		HorizontalScrollView scrollView = new HorizontalScrollView(getContext());
		scrollView.setBackgroundColor(Color.TRANSPARENT);
		// scrollView.setLayoutParams(new LayoutParams(300, 50));
		scrollView.setLayoutParams(new LayoutParams(312, 80));
		// scrollView.setPadding(12, 10, 2, 2);
		scrollView.setPadding(10, 20, 10, 10);
		scrollView.setClickable(true);
		LinearLayout ll = new LinearLayout(getContext());
		ll.setHorizontalFadingEdgeEnabled(true);
		ll.setHorizontalScrollBarEnabled(true);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		// ll.addView(leftarrow, 50, 50);

		Iterator<Integer> itr = mColorSamples.iterator();

		while (itr.hasNext()) {
			int mColor = (Integer) itr.next();
			if (mColor == 0) {
				continue;
			}
			View mView = new View(getContext());
			mView.setBackgroundColor(mColor);
			mView.setClickable(true);
			mView.setTag((Integer) mColor);
			mView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mColorSamples.contains(v.getTag())) {
						int tag = (Integer) v.getTag();
						mListener.colorChanged(tag, mOptionSelected,
								mBrushOpacity, mBackgroundOpacity);
						dismiss();

					}
				}
			});

			ll.addView(mView, 50, 50);
		}
		// ll.addView(rightarrow, 50, 50);
		scrollView.addView(ll);
		aColorPicker.addView(scrollView);// you add the whole Samples to the
											// layout
	}

	private void createAlphaSeekBar(ViewGroup aColorPicker) {
		Resources res = getContext().getResources();
		final SeekBar mAlpha;
		LinearLayout ll = new LinearLayout(getContext());
		ll.setOrientation(LinearLayout.HORIZONTAL);
		Drawable alphaDrawable = (Drawable) res
				.getDrawable(R.drawable.transparencytile);
//		Drawable barDrawable = (Drawable) res
//				.getDrawable(R.xml.progress_drawable);

		
		TextView alphaTitleLeft = new TextView(getContext());
		alphaTitleLeft.setText(R.string.transparent);
		alphaTitleLeft.setTextSize(12);
		alphaTitleLeft.setTextColor(Color.WHITE);
		// alphaTitleLeft.setLayoutParams(new LayoutParams(150, 80));
		alphaTitleLeft.setGravity(Gravity.LEFT);
		// alphaTitleLeft.setWidth(aColorPicker.getWidth()/2);
		alphaTitleLeft.setWidth(150);
		TextView alphaTitleRight = new TextView(getContext());
		alphaTitleRight.setText(R.string.opaque);
		alphaTitleRight.setTextSize(12);
		alphaTitleRight.setTextColor(Color.WHITE);
		// alphaTitleLeft.setLayoutParams(new LayoutParams(150, 80));
		alphaTitleRight.setGravity(Gravity.RIGHT);
		alphaTitleRight.setWidth(150);
		ll.addView(alphaTitleLeft);
		ll.addView(alphaTitleRight);
		mAlpha = new SeekBar(getContext()); // create the SeekBar
		mAlpha.setPadding(10, 10, 10, 10);
		mAlpha.setLayoutParams(new LayoutParams(300, 50));
		mAlpha.setThumb(alphaDrawable);
//		mAlpha.setProgressDrawable(barDrawable);
		mAlpha.setMax(255);
		if (mOptionSelected == 1) {
			mAlpha.setProgress(mBackgroundOpacity);
		} else {
			mAlpha.setProgress(mBrushOpacity);
		}
		mAlpha.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (mOptionSelected == 1) {
					mBackgroundOpacity = progress;
				} else {

					mBrushOpacity = progress;
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}
		});
		aColorPicker.addView(ll);
		aColorPicker.addView(mAlpha); // you add the whole SeekBar to the layout
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		OnColorChangedListener l = new OnColorChangedListener() {

			@Override
			public void colorChanged(int color, int option, int brushopacity,
					int backgroundopacity) {
				mOptionSelected = option;
				if (mOptionSelected == 1) {
					mBackgroundOpacity = backgroundopacity;
				} else {
					mBrushOpacity = brushopacity;
				}

				mListener.colorChanged(color, option, mBrushOpacity,
						mBackgroundOpacity);
				dismiss();
			}

		};
		ScrollView sv = new ScrollView(getContext());

		LinearLayout ll = new LinearLayout(getContext());
		ll.setPadding(10, 5, 10, 5);
		ll.setOrientation(LinearLayout.VERTICAL);
		
		createRadioButton(ll);
		View view = new ColorPickerView(getContext(), l, mInitialColor,
				mColorSamples, mBrushOpacity, mBackgroundOpacity);
		ll.addView(view);

		int orientation = getContext().getResources().getConfiguration().orientation;
		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
		
		LinearLayout lr = new LinearLayout(getContext());
		TableLayout layouttable = new TableLayout(getContext());
		TableRow trow= new TableRow(getContext()); 
        trow.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		lr.setPadding(10, 5, 10, 5);
		lr.setOrientation(LinearLayout.VERTICAL);
		createAlphaSeekBar(lr);

		createColorSamples(lr);
		trow.addView(ll);
		trow.addView(lr);
		layouttable.addView(trow);
		
		sv.addView(layouttable);

		} else{
			createAlphaSeekBar(ll);

			createColorSamples(ll);
			sv.addView(ll);
		}
		
		setContentView(sv);

		setTitle(R.string.colorselection);
	}
}