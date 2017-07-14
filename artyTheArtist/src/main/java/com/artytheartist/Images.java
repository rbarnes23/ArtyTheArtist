package com.artytheartist;

import java.util.ArrayList;
import java.util.List;

import com.tokenlibrary.Token;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
//import android.graphics.Typeface;
//import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
//import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
//import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

public class Images extends Activity {
	private int count;
	private Bitmap[] thumbnails;
	private boolean[] thumbnailsselection;
	private List<String> arrayList = new ArrayList<String>();
	private ImageAdapter imageAdapter;
	private int mSlideShowInterval;
	private Cursor imagecursor;
	private String[] selectedImages;
	private Token mToken = new Token();
	protected ImageLoadTask imageLoadTask;
	private String imageList;
	private String mPlayListSelected;
	private Dialog mDialog = null;
	private static final int SAVESLIDESHOW = 1;
	private static final int DELETESLIDESHOW = 2;
	private ArrayAdapter<String> slideshowsAdapter;
	private List<String> playList;
	private int colornotselected;
	private int colorselected;
	PlayListHelper playListHelper = new PlayListHelper();


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.images);
		colornotselected=getResources().getColor(android.R.color.transparent);
		colorselected=getResources().getColor(R.color.green);

		imageList = AppSettings.getSelectedImages();
		playList = playListHelper.getPlayLists(imageList);
		playList.add(0,(String) getText(R.string.clearselectedimages));
		playList.add(1,(String) getText(R.string.selectallimages));
		slideshowsAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, playList);
		slideshowsAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner spinnerPlayList = (Spinner) findViewById(R.id.spinnerslideshows);
		spinnerPlayList.setAdapter(slideshowsAdapter);

		spinnerPlayList.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				TextView itemSelected = (TextView) selectedItemView;
				mPlayListSelected = (String) itemSelected.getText();
				AppSettings.setSelectedPlayList(mPlayListSelected);
				
				if (arrayList != null) {
					if (mPlayListSelected.contentEquals(getText(R.string.selectallimages))) {
						checkAll();
						return;
					}
					uncheckAll();
					imageList = AppSettings.getSelectedImages();  //Get the latest and greatest images
					selectedImages = playListHelper.getImageList(mPlayListSelected,
							imageList);
					if (selectedImages == null) {
						return;
					}
					for (int j = 0; j < selectedImages.length; j++) {
						
						for (int i = 0; i < arrayList.size(); i++) {

							if (arrayList.get(i).contentEquals(
									selectedImages[j])) {
								thumbnailsselection[i] = true;
								break;
							}
						}
					}
					imageAdapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				
			}

		});
		// spinnerPlayList.setAdapter(slideshowsAdapter);

		final String[] columns = { MediaStore.Images.Media.DATA,
				MediaStore.Images.Media._ID };
		final String orderBy = MediaStore.Images.Media._ID;
		imagecursor = managedQuery(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
				null, orderBy);
		if (imagecursor != null) {
			final int image_column_index = imagecursor
					.getColumnIndex(MediaStore.Images.Media._ID);
			this.count = imagecursor.getCount();
			this.thumbnails = new Bitmap[this.count];
			this.thumbnailsselection = new boolean[this.count];

			GridView imagegrid = (GridView) findViewById(R.id.ImageGrid);
			imageAdapter = new ImageAdapter();
			imagegrid.setAdapter(imageAdapter);

			final TextView seekBarText = (TextView) findViewById(R.id.slideshowseconds);
			mSlideShowInterval = AppSettings.getSlideShowInterval();
			seekBarText.setText(Integer.toString(mSlideShowInterval));
			final SeekBar seekBarSlideShowDelay = (SeekBar) findViewById(R.id.slideshowdelay);
			seekBarSlideShowDelay.setProgress(mSlideShowInterval);
			seekBarSlideShowDelay
					.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
						@Override
						public void onProgressChanged(SeekBar seekBar,
								int progress, boolean fromUser) {
							mSlideShowInterval = progress;
							seekBarText.setText(Integer.toString(progress));
						}

						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {
						}

						@Override
						public void onStopTrackingTouch(SeekBar seekBar) {
							AppSettings
									.setSlideShowInterval(mSlideShowInterval);
						}

					});

			final RadioButton radioSelectAll = (RadioButton) findViewById(R.id.radioselectall);
			radioSelectAll.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					checkAll();
				}

			});

			final RadioButton radioClearAll = (RadioButton) findViewById(R.id.radioclearall);
			radioClearAll.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					uncheckAll();
				}

			});

			final Button addImageList = (Button) findViewById(R.id.saveBtn);
			addImageList.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showDialog(SAVESLIDESHOW);
				}

			});

			final Button deleteImageList = (Button) findViewById(R.id.deleteBtn);
			deleteImageList.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showDialog(DELETESLIDESHOW);
					MainActivity.setToast(getString(R.string.deleteplaylist), 0);
				}

			});

			final Button selectBtn = (Button) findViewById(R.id.selectBtn);
			selectBtn.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					imageLoadTask.cancel(true);
					if (getSelectedImages() > 0) {
						Intent returnIntent = new Intent();
						returnIntent.putExtra("result", selectedImages);
						returnIntent.putExtra("slideshowinterval",
								mSlideShowInterval);
						setResult(RESULT_OK, returnIntent);
						//freememory();
						finish();

					}

				}
			});

			imageLoadTask = new ImageLoadTask();
			imageLoadTask.execute(image_column_index);

		} else {
			Intent returnIntent = new Intent();
			setResult(RESULT_CANCELED, returnIntent);
			finish();
		}

	}
	

	// ASYNC TASK TO LAZY LOAD FOR SLOW MACHINES AND AVOID CHOKING UP UI THREAD
	class ImageLoadTask extends AsyncTask<Integer, Integer, Integer> {
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			imageAdapter.notifyDataSetChanged();
		}

		@Override
		protected Integer doInBackground(Integer... image_column) {
			for (int i = 0; i < count; i++) {
				imagecursor.moveToPosition(i);
				int id = imagecursor.getInt(image_column[0]);
				int dataColumnIndex = imagecursor
						.getColumnIndex(MediaStore.Images.Media.DATA);
				thumbnails[i] = MediaStore.Images.Thumbnails.getThumbnail(
						getApplicationContext().getContentResolver(), id,
						MediaStore.Images.Thumbnails.MICRO_KIND, null);

				arrayList.add(imagecursor.getString(dataColumnIndex));
				if (selectedImages != null) {
					for (int x = 0; x < selectedImages.length; x++) {
						int j = arrayList.indexOf(selectedImages[x]);
						if (j > -1) {
							thumbnailsselection[j] = true;
						}
					}
				}

				publishProgress(i);
			}

			return null;
		}

		// -- called if the cancel button is pressed
		@Override
		protected void onCancelled() {
			super.onCancelled();

		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onPostExecute(Integer ret) {
			// imagecursor.close();
			Button selectBtn = (Button) findViewById(R.id.selectBtn);
			selectBtn.setEnabled(true);
			selectBtn.setText(R.string.slideshow);
		}
	}

	public void uncheckAll() {
		for (int i = 0; i < thumbnailsselection.length; i++) {
			thumbnailsselection[i] = false;
		}
		imageAdapter.notifyDataSetChanged();
	}

	public void checkAll() {
		for (int i = 0; i < thumbnailsselection.length; i++) {
			thumbnailsselection[i] = true;
		}
		imageAdapter.notifyDataSetChanged();
	}

	 private void freememory() {
	 Runtime freememory = Runtime.getRuntime();
	 freememory.gc();
	 }

	public class ImageAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public ImageAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			return count;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.galleryitem, null);
				holder.imageview = (ImageView) convertView
						.findViewById(R.id.thumbImage);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.imageview.setId(position);

/*			holder.imageview.setOnLongClickListener(new OnLongClickListener() {
				
				//@Override
				public boolean onLongClick(View v) {
					imageLoadTask.cancel(true);
					return true;
				}
			});
*/			
			holder.imageview.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					//imageLoadTask.cancel(true);
					int id = v.getId();
					if (thumbnailsselection[id]) {
						v.setBackgroundColor(colornotselected);
						thumbnailsselection[id] = false;
					} else {
						v.setBackgroundColor(colorselected);
						thumbnailsselection[id] = true;
					}

					/*					Intent returnIntent = new Intent();
					returnIntent.putExtra("imageresult",
							arrayList.get(id));
					setResult(RESULT_OK, returnIntent);
					finish();
*/				}
			});
			holder.imageview.setImageBitmap(thumbnails[position]);
			holder.imageview.setBackgroundColor(thumbnailsselection[position]?colorselected:colornotselected);
//			holder.checkbox.setChecked(thumbnailsselection[position]);
			holder.id = position;
			return convertView;
		}

	}

	class ViewHolder {
		ImageView imageview;
		//CheckBox checkbox;
		int id;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		mDialog = null;
		final View layout;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final Context sContext = getApplicationContext();
		LayoutInflater inflater = (LayoutInflater) sContext
				.getSystemService(LAYOUT_INFLATER_SERVICE);

		switch (id) {

		case SAVESLIDESHOW:
			layout = inflater.inflate(R.layout.newfile,
					(ViewGroup) findViewById(R.id.newfile));

			builder.setView(layout);
			builder.setCancelable(false)
					.setTitle(getString(R.string.filename))
					.setPositiveButton(getString(android.R.string.yes),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									EditText filename = ((EditText) layout
											.findViewById(R.id.et_filename));
									String mText = filename.getText()
											.toString();
									if (mText.length() > 0) {
										if (getSelectedImages() > 0) {
											
											playList=playListHelper.setImageList(mText,
													selectedImages, imageList);
											//imageList=AppSettings.getSelectedImages();
											 slideshowsAdapter.notifyDataSetChanged();
											 imageAdapter.notifyDataSetChanged();

										}
									}
									removeDialog(SAVESLIDESHOW);

								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									removeDialog(SAVESLIDESHOW);
								}
							});

			break;
		case DELETESLIDESHOW:
			layout = inflater.inflate(R.layout.about, null);
			builder.setTitle(getString(R.string.app_name));
			builder.setIcon(R.drawable.appicon);
			builder.setMessage(getText(R.string.deleteplaylist) + mPlayListSelected);
			//builder.setView(layout);
			builder.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							imageList=AppSettings.getSelectedImages();
							playList= playListHelper.removeImageList(mPlayListSelected, imageList);
							slideshowsAdapter.notifyDataSetChanged();
							removeDialog(DELETESLIDESHOW);
							// int selectedPosition = ((AlertDialog) dialog)
							// .getListView().getCheckedItemPosition();
							// String mSelection = mList[selectedPosition];
							// AppSettings.setTypeFace(MainActivity.getContext(),
							// mSelection);

						}
					}).setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							removeDialog(DELETESLIDESHOW);
							
							//dialog.dismiss();
						}
					});

			break;

		}
		mDialog = builder.create();
		mDialog.show();
		return mDialog;
	}

	private int getSelectedImages() {
		final int len = thumbnailsselection.length;
		int cnt = 0;

		String selectImages = "";
		for (int i = 0; i < len; i++) {
			if (thumbnailsselection[i]) {
				cnt++;
				selectImages = selectImages + arrayList.get(i) + ",";
			}
		}
		if (cnt == 0) {
			MainActivity.setToast(getString(R.string.selectoneimage), 1);
		} else {
			selectImages = selectImages.substring(0,
					selectImages.lastIndexOf(","))
					+ "";

			selectedImages = selectImages.split(",");

		}
		return cnt;
	}

}
