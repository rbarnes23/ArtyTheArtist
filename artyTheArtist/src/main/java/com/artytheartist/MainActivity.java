package com.artytheartist;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.edilibrary.EdiSupport;
import com.tokenlibrary.Token;

public class MainActivity extends Activity implements
		ColorPickerDialog.OnColorChangedListener {
	private DrawView drawView;
	private String mEmail;
	private String mMemberid;
	private static final int ABOUT = 1;
	private static final int STROKEWIDTH = 2;
	private static final int SAVECANVAS = 3;
	private static final int RESTORECANVAS = 4;
	private static final int PROTRACTOR = 5;
	private static final int FONTTYPE = 6;
	private static final int MOVEFORWARD = 1;
	private static final int MOVEBACKWARD = -1;
	private static final int LONG = 1;
	private static final int SHORT = 0;

	private static Context sContext;
	private Handler mHandler = new Handler();
	boolean mRedraw = false;
	private String mTo = null;
	private Dialog mDialog = null;
	int mAssetFilesCount = 0;
	AssetManager assetManager;
	private String[] mFontList;
	// private static final int SELECT_PICTURE = 1;
	private static final int SELECT_CONTACTS = 2;
	private static final int SELECT_IMAGE = 3;
	private String mSelectedImagePath;
	private String mTypeFacePath = "fonts/";
	private boolean mNetworkOn;
	private static List<Integer> sMemberIds = new ArrayList<Integer>();
	private TextView strokeprogress;
	private TextView eraserprogress;
	private TextView textsizeprogress;
	private boolean mLoggedin;
	private Spinner mSpinnerFont;
	// Only want to retrieve contactlist from google once
	private String mContacts;
	private static String sFriends;
	InputMethodManager imm;
	private boolean mSampleStatus = false;
	private boolean mErase = false;
	private int mBrushOpacity = -1;
	private int mBackgroundOpacity = -1;
	private Integer mBrushProgress, mTextProgress, mEraserProgress;
	private EditText mEditText;
	private View mDrawText;
	private Button mDelete;
	private int maxScrollX;
	private ListView mMessageList;
	private MessageAdapter messageAdapter;
	private ListView mMemberList;
	private ListView mArtToolsList;
	private MemberAdapter memberAdapter;
	private ListView mImagesList;
	private SampleImageAdapter imagesAdapter;
	private ArrayList<HashMap<String, CharSequence>> jsonMessagelist = new ArrayList<HashMap<String, CharSequence>>();
	private ArrayList<HashMap<String, String>> mGroupList = new ArrayList<HashMap<String, String>>();
	private List<String> mDetailList = new ArrayList<String>();
	private LinkedHashMap<String, HeaderInfo> mGroups = new LinkedHashMap<String, HeaderInfo>();
	private ArrayList<HeaderInfo> groupList = new ArrayList<HeaderInfo>();
	private String mSessions;
	// private spinnerAdapter spinnerAdapter;
	private static TCPClient mTcpClient;
	private static ArrayList<String> arrayList;
	private static TCPAdapter mAdapter;
	private ListView mList;
	private String mMessage;
	private int mGroupSelectedPosition;
	private String mGroupSelected;
	private int mPlaylistSelectedPosition;
	private boolean isloading = false;
	private int number = 0;
	private int MAX_ITEMS_PER_PAGE = 3;
	private int TOTAL_ITEMS;
	private ArrayList<HashMap<String, Object>> mImagesInfo;
	private ArrayList<HashMap<String, CharSequence>> mMemberInfo = new ArrayList();
	private static ArrayList<HashMap<String, String>> mMemberFindList = new ArrayList<HashMap<String, String>>();

	public static void sendMessage(String _message) {
		final String message = _message;
		// Thread t = new Thread(){
		// public void run(){

		// sends the message to the server
		if (mTcpClient != null) {
			mTcpClient.sendMessage(message);
			arrayList.add(message);
			mAdapter.notifyDataSetChanged();
		}
		// }
		// };
		// t.start();

	}

	/*
	 * public class LoadImages extends AsyncTask<Void, Void, Void> {
	 * 
	 * @Override protected Void doInBackground(Void... params) { final String
	 * imageList = AppSettings.getSelectedImages(); final String playList =
	 * AppSettings.getSelectedPlayList();
	 * 
	 * Token mToken = new Token(); String[] filenames =
	 * mToken.getImageList(playList, imageList); // If the filename passed in
	 * getimageList does not exist and // nothing to do //if (filenames == null)
	 * { // return; //} TOTAL_ITEMS = filenames.length; if(TOTAL_ITEMS >
	 * number){ isloading = true;
	 * 
	 * Bitmap bm = null;
	 * 
	 * for (int i = 0; i < MAX_ITEMS_PER_PAGE; i++) { File filename = new
	 * File(filenames[number]); // if (i==0){ bm = drawView.decodeFile(filename,
	 * 30); // bm = BitmapFactory.decodeResource(getResources(), //
	 * R.color.red); // } // May want to adjust size from 30 to less or more
	 * based on // # of photos and available memory // or check
	 * scrollbarobserver and only load the ones that // fit on the screen // bm
	 * = bm.createScaledBitmap(bm, 30, 30, false); HashMap mHash = new
	 * HashMap(); mHash.put("filename", filenames[number]);
	 * mHash.put("thumbimage", bm); mImagesInfo.add(mHash); number+=1; // bm =
	 * null; } //MAX_ITEMS_PER_PAGE=mImagesList.getLastVisiblePosition();
	 * 
	 * }
	 * 
	 * return null; }
	 * 
	 * @Override protected void onPostExecute(Void result) {
	 * imagesAdapter.notifyDataSetChanged
	 * ();//mImagesList.getAdapter().notifyDataSetChanged(); isloading = false;
	 * 
	 * if(imagesAdapter.getCount() == TOTAL_ITEMS){
	 * //header.setText("All "+adapter.getCount()+" Items are loaded.");
	 * mImagesList.setOnScrollListener(null);
	 * //mListView.removeFooterView(footer); } else{ //number--;
	 * //mImagesList.setSelection(number);
	 * //header.setText("Loaded items - "+adapter
	 * .getCount()+" out of "+TOTAL_ITEMS); } } }
	 */
	private void setMessageInfo() {
		// Add reference to the message list
		mMessageList = (ListView) findViewById(R.id.messagelist);
		// mMessageList.setLayoutParams(new RelativeLayout.LayoutParams(200,
		// LayoutParams.WRAP_CONTENT));

		messageAdapter = new MessageAdapter(this, jsonMessagelist,
				R.layout.messagerowlist,
				new String[] { "fullname", "message" }, new int[] {
						R.id.fullname, R.id.message });
		// Make sure we dont add a new header if one already exists
		if (mMessageList.getHeaderViewsCount() < 1) {
			LayoutInflater inflater = getLayoutInflater();
			View header = inflater.inflate(R.layout.messagerowlist,
					(ViewGroup) findViewById(R.id.message_header_row_layout));
			header.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.squarebox_input));
			TextView fullnameView = (TextView) header
					.findViewById(R.id.fullname);

			fullnameView.setText(R.string.messages);
			mMessageList.addHeaderView(header);
		}
		mMessageList.setAdapter(messageAdapter);
	}

	private void setMemberInfo() {

		// Add reference to the member list
		mMemberList = (ListView) findViewById(R.id.memberlist);

		// ArrayList<HashMap<String, CharSequence>> mMemberInfo = new
		// ArrayList();
		memberAdapter = new MemberAdapter(this, mMemberInfo,
				R.layout.memberrowlist, new String[] { "fullname", "message",
						"membercheckbox" }, new int[] { R.id.fullname,
						R.id.message, R.id.membercheckbox });
		if (mMemberList.getHeaderViewsCount() < 1) {
			// Set up Header
			String groupName = mGroupList.get(mGroupSelectedPosition).get(
					"group");

			LayoutInflater inflater = getLayoutInflater();
			View header = inflater.inflate(R.layout.memberheader,
					(ViewGroup) findViewById(R.id.memberheader));
			header.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.squarebox_input));
			CheckBox groupCheckBox = (CheckBox) header
					.findViewById(R.id.checkboxheader);
			// if(groupCheckBox.isChecked()){memberAdapter.SetCheckbox(1);}else{memberAdapter.SetCheckbox(2);}
			// memberAdapter.SetCheckbox(-1);

			Spinner groupSpinner = (Spinner) header
					.findViewById(R.id.spinnersessionheader);
			spinnerAdapter spinnerAdapter = new spinnerAdapter(this,
					mGroupList, R.layout.spinner_layout, new String[] { "_id",
							"group", "sessionNo" }, new int[] { R.id.spinnerId,
							R.id.spinnerTarget, R.id.spinnerNo });

			spinnerAdapter.setDropDownViewResource(R.layout.spinner_layout);
			groupSpinner.setAdapter(spinnerAdapter);
			groupSpinner.setSelection(mGroupSelectedPosition, true);
			groupSpinner
					.setOnItemSelectedListener(new OnItemSelectedListener() {
						boolean mFirst = true;

						@Override
						public void onItemSelected(AdapterView<?> parentView,
								View selectedItemView, int position, long id) {
							TextView groupNo = (TextView) selectedItemView
									.findViewById(R.id.spinnerNo);
							TextView groupName = (TextView) selectedItemView
									.findViewById(R.id.spinnerTarget);
							TextView groupId = (TextView) selectedItemView
									.findViewById(R.id.spinnerId);
							// String s = groupId.getText().toString();
							AppSettings
									.setGroupId(groupId.getText().toString());
							mGroupSelectedPosition = position;

							populateMembers();
							// setMemberInfo();
							// memberAdapter.notifyDataSetChanged();
						}

						@Override
						public void onNothingSelected(AdapterView<?> parentView) {
							// your code here
						}

					});

			mMemberList.addHeaderView(header, null, false);
		}
		mMemberList.setAdapter(memberAdapter);
		populateMembers();
	}

	private void populateMembers() {
		mMemberInfo.clear();
		JSONArray jsonMemberList;
		try {
			jsonMemberList = new JSONArray(mGroupList.get(
					mGroupSelectedPosition).get("members"));
			for (int i = 0; i < jsonMemberList.length(); i++) {
				HashMap mHash = new HashMap();
				mHash.put("fullname", jsonMemberList.getJSONObject(i)
						.getString("name"));
				mHash.put("message",
						jsonMemberList.getJSONObject(i).getString("id"));
				mHash.put("membercheckbox", true);

				mMemberInfo.add(mHash);
			}
		} catch (JSONException e) {
		}
		memberAdapter.notifyDataSetChanged();
	}

	private void setArtToolsInfo() {

		// Add reference to the message list
		mArtToolsList = (ListView) findViewById(R.id.arttoolslist);

		// Set up Header String
		LayoutInflater inflater = getLayoutInflater();
		View header = inflater.inflate(R.layout.arttoolsheader,
				(ViewGroup) findViewById(R.id.arttoolsheader));
		header.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.squarebox_input));
		// GridView gridview = (GridView)
		// header.findViewById(R.id.arttoolsgrid);
		// ArtToolsAdapter artToolsAdapter = new ArtToolsAdapter(sContext);
		// gridview.setAdapter(artToolsAdapter);

		ArrayList<HashMap<String, String>> mGroupList = new ArrayList<HashMap<String, String>>();
		spinnerAdapter spinnerAdapter = new spinnerAdapter(this, mGroupList,
				R.layout.spinner_layout, new String[] { "_id", "group",
						"sessionNo" }, new int[] { R.id.spinnerId,
						R.id.spinnerTarget, R.id.spinnerNo });
		if (mArtToolsList.getHeaderViewsCount() < 1) {
			mArtToolsList.addHeaderView(header, null, false);
		}

		mArtToolsList.setAdapter(spinnerAdapter);

	}

	private SpinnerImageAdapter buildSpinnerList() {
		ArrayList mDrawableList = new ArrayList();
		mDrawableList.add(R.drawable.appicon36);
		mDrawableList.add(R.drawable.colorline);

		SpinnerImageAdapter result = new SpinnerImageAdapter(this,
				new ArrayList<Drawable>(mDrawableList), R.layout.galleryitem2,
				new String[] { "thumbImage" }, new int[] { R.id.thumbImage2 });

		result.setDropDownViewResource(R.layout.galleryitem2);

		return (result);
	}

	private void setArtToolsInfo2() {

		// LayoutInflater inflater = getLayoutInflater();
		// View header = inflater.inflate(R.layout.main,
		// (ViewGroup) findViewById(R.id.menubar));
		// Add reference to the message list
		// LinearLayout mArtToolsView = (LinearLayout)
		// findViewById(R.id.menubar);
		// ToolsSpinner mArtToolsListView = (ToolsSpinner)
		// findViewById(R.id.spinnerTools);
		// buildSpinnerList();
		// mArtToolsListView.setAdapter(buildSpinnerList());

	}

	private void setImagesInfo() {

		// Add reference to the message list
		mImagesList = (ListView) findViewById(R.id.imageslist);
		final ArrayAdapter<String> slideshowsAdapter;
		final String imageList = AppSettings.getSelectedImages();
		final String playList = AppSettings.getSelectedPlayList();
		// If there are no imagelists stored in Appsettings no need to do
		// anything else here
		if (imageList == null) {
			return;
		}
		// final ArrayList<HashMap<String, Object>> mImagesInfo = new
		// ArrayList();
		mImagesInfo = new ArrayList();
		imagesAdapter = new SampleImageAdapter(this, mImagesInfo,
				R.layout.samplegalleryitem, new String[] { "filename",
						"thumbimage", "imageno" }, new int[] { R.id.filename,
						R.id.thumbImage, R.id.imageno });

		if (mImagesList.getHeaderViewsCount() < 1) {
			// Set up Header String
			LayoutInflater inflater = getLayoutInflater();
			View header = inflater.inflate(R.layout.imageheader,
					(ViewGroup) findViewById(R.id.imageheader));
			header.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.squarebox_input));
			Spinner editSpinner = (Spinner) header
					.findViewById(R.id.showhideeditheader);
			editSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parentView,
						View selectedItemView, int position, long id) {
					imagesAdapter.setNotesOption(position);
					imagesAdapter.notifyDataSetChanged();
					/*
					 * View layout; LayoutInflater inflater = (LayoutInflater)
					 * sContext .getSystemService(LAYOUT_INFLATER_SERVICE);
					 * 
					 * layout = inflater.inflate(R.layout.samplegalleryitem,
					 * (ViewGroup) findViewById(R.id.samplegalleryitem));
					 * 
					 * EditText notes = (EditText)
					 * layout.findViewById(R.id.notes); if (position == 1) {
					 * notes.setVisibility(View.GONE); } else if (position == 2)
					 * { notes.setFocusable(true); notes.setEnabled(true); }
					 * else { //Default 0
					 * selectedItemView.setVisibility(View.VISIBLE);
					 * notes.setFocusable(false); notes.setEnabled(false);
					 * 
					 * }
					 */
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}

			});
			Spinner imageSpinner = (Spinner) header
					.findViewById(R.id.spinnerimageheader);
			Token mToken = new Token();
			PlayListHelper playListHelper = new PlayListHelper();
			List playLists = playListHelper.getPlayLists(imageList);
			// playLists.add(0, (String) getText(R.string.clearselectedimages));
			// playLists.add(1, (String) getText(R.string.selectallimages));

			slideshowsAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, playLists);
			slideshowsAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			imageSpinner.setAdapter(slideshowsAdapter);
			mPlaylistSelectedPosition = playLists.indexOf(playList);
			if (mPlaylistSelectedPosition > 0) {
				imageSpinner.setSelection(mPlaylistSelectedPosition, true);
			}
			imageSpinner
					.setOnItemSelectedListener(new OnItemSelectedListener() {
						@Override
						public void onItemSelected(AdapterView<?> parentView,
								View selectedItemView, int position, long id) {
							mPlaylistSelectedPosition = position;
							TextView itemSelected = (TextView) selectedItemView;
							String mPlayListSelected = (String) itemSelected
									.getText();
							AppSettings.setSelectedPlayList(mPlayListSelected);

							final String imageList = AppSettings
									.getSelectedImages();
							final String playList = AppSettings
									.getSelectedPlayList();
							Token mToken = new Token();
							PlayListHelper playListHelper = new PlayListHelper();
							String[] filenames = playListHelper.getImageList(
									playList, imageList);
							// If the filename passed in getimageList does not
							// exist and
							// nothing to do
							if (filenames == null) {
								return;
							}
							int len = filenames.length;
							mImagesInfo.clear();
							// Bitmap bm = null;
							for (int i = 0; i < len; i++) {
								// File filename = new File(filenames[i]);
								// if (i==0){
								// bm = drawView.decodeFile(filename, 30);
								// bm =
								// BitmapFactory.decodeResource(getResources(),
								// R.color.red);
								// }
								// May want to adjust size from 30 to less or
								// more based on
								// # of photos and available memory
								// or check scrollbarobserver and only load the
								// ones that
								// fit on the screen
								// bm = bm.createScaledBitmap(bm, 30, 30,
								// false);
								HashMap mHash = new HashMap();
								mHash.put("filename", filenames[i]);
								mHash.put("imageno",
										"Image: " + Integer.toString(i + 1)
												+ "/" + Integer.toString(len));
								// mHash.put("thumbimage", bm);
								mImagesInfo.add(mHash);
								// bm = null;
							}
							imagesAdapter.notifyDataSetChanged();

							// setImagesInfo();

							slideshowsAdapter.notifyDataSetChanged();
						}

						@Override
						public void onNothingSelected(AdapterView<?> parentView) {
							// your code here
						}

					});

			mImagesList.addHeaderView(header, null, false);
		}
		/*
		 * mImagesList.setOnScrollListener(new AbsListView.OnScrollListener() {
		 * 
		 * @Override public void onScrollStateChanged(AbsListView view, int
		 * scrollState) { switch (scrollState) { case
		 * OnScrollListener.SCROLL_STATE_IDLE: // when list scrolling stops
		 * 
		 * // manipulateWithVisibleViews(view);
		 * 
		 * break; case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: break; case
		 * OnScrollListener.SCROLL_STATE_FLING: break; }
		 * 
		 * }
		 * 
		 * @Override public void onScroll(AbsListView view, int
		 * firstVisibleItem, int visibleItemCount, int totalItemCount) { int
		 * loadedItems = firstVisibleItem + visibleItemCount; if ((loadedItems
		 * == totalItemCount) && !isloading) { number=loadedItems++; // if(task
		 * != null && (task.getStatus() == //AsyncTask.Status.FINISHED)){
		 * LoadImages task = new LoadImages(); task.execute(); //} } } });
		 */

		// fullnameView.setText(R.string.angle);
		mImagesList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView,
					View selectedView, int position, long id) {
				// drawView.stopSlideShow(false);
				HashMap<String, Object> m = mImagesInfo.get((int) id);
				String mFileName = (String) m.get("filename");
				drawView.restore(mFileName, false);
			}

		});

		// Token mToken = new Token();
		PlayListHelper playListHelper = new PlayListHelper();
		String[] filenames = playListHelper.getImageList(playList, imageList);
		// If the filename passed in getimageList does not exist and
		// nothing to do
		if (filenames == null) {
			return;
		}
		int len = filenames.length;
		// Bitmap bm = null;
		for (int i = 0; i < len; i++) {
			// File filename = new File(filenames[i]);
			// if (i==0){
			// bm = drawView.decodeFile(filename, 30);
			// bm = BitmapFactory.decodeResource(getResources(),
			// R.color.red);
			// }
			// May want to adjust size from 30 to less or more based on
			// # of photos and available memory
			// or check scrollbarobserver and only load the ones that
			// fit on the screen
			// bm = bm.createScaledBitmap(bm, 30, 30, false);
			HashMap mHash = new HashMap();
			mHash.put("filename", filenames[i]);
			mHash.put("imageno", "Image No: " + Integer.toString(i + 1));
			// mHash.put("thumbimage", bm);
			mImagesInfo.add(mHash);
			// bm = null;
		}
		mImagesList.setAdapter(imagesAdapter);
		// LoadImages task = new LoadImages();
		// task.execute();

		/*
		 * // Add the images Thread t = new Thread() { public void run() {
		 * 
		 * Token mToken = new Token(); String[] filenames =
		 * mToken.getImageList(playList, imageList); // If the filename passed
		 * in getimageList does not exist and // nothing to do if (filenames ==
		 * null) { return; }
		 * 
		 * int len = filenames.length; // Bitmap bm = null; for (int i = 0; i <
		 * len; i++) { File filename = new File(filenames[i]); Bitmap bm =
		 * drawView.decodeFile(filename, 30); // May want to adjust size from 30
		 * to less or more based on // # of photos and available memory // or
		 * check scrollbarobserver and only load the ones that // fit on the
		 * screen // bm = bm.createScaledBitmap(bm, 30, 30, false); HashMap
		 * mHash = new HashMap(); mHash.put("filename", filenames[i]);
		 * mHash.put("thumbimage", bm); mImagesInfo.add(mHash); bm = null; }
		 * 
		 * }
		 * 
		 * };
		 * 
		 * t.start();
		 */
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sContext = getApplicationContext();
		arrayList = new ArrayList<String>();
		drawScreen(); // Draw the Screen

		//
		// Handler handler = new Handler();
		//
		// handler.postDelayed(new Runnable() {
		// @Override
		// public void run() {
		// Intent intent = new Intent(sContext, SplashActivity.class);
		//
		// // Intent intent = new Intent(SplashActivity.this,
		// // MainActivity.class);
		// startActivity(intent);
		//
		// }
		//
		// }, 2000);

		// drawScreen(); // Draw the Screen
		setScrollBarObserver();
		getPreferences(); // Get the saved preferences

		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		mDelete = (Button) findViewById(R.id.cleartext);
		mDelete.setVisibility(mEditText.getText().length() > 0 ? View.VISIBLE
				: View.GONE);
		mEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				// ListView list = (ListView) findViewById(R.id.memberlist);
				// list.setVisibility(View.VISIBLE);
				mDelete.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
			}
		});

		mDrawText = (View) findViewById(R.id.draw_text_layout);
		drawView.setTextView(mEditText, mDrawText, imm);

		/*
		 * // Connect the chat client String mStatus = sChat.getStatus(); if
		 * (mStatus == null) { mStatus =
		 * getString(R.string.networknotavailable); } // Toast.makeText(this,
		 * mStatus, Toast.LENGTH_LONG).show(); setToast(mStatus, LONG);
		 * setToast(getString(R.string.menuinformation), LONG);
		 */

		setMessageInfo();
		setImagesInfo();
		setArtToolsInfo();

		// relate the listView from java to the one created in xml
		mList = (ListView) findViewById(R.id.listview);
		mAdapter = new TCPAdapter(this, arrayList);
		mList.setAdapter(mAdapter);

		// connect to the server

		new connectTask().execute("");
		if (mEmail.length() > 0) {
			// setToast(mEmail, SHORT);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Token loginToken = new Token();
			// String message = loginToken.createLoginToken(mMemberid);
			sendMessage(loginToken.createLoginToken(mMemberid));
		}
		// Decode the files and put them into the image adapter
		// ArrayAdapter<String> slideshowsAdapter = new
		// ArrayAdapter<String>(this,
		// android.R.layout.simple_gallery_item, playList);
		// ListView spinnerPlayList = (ListView) findViewById(R.id.imageslist);
		// spinnerPlayList.setAdapter(slideshowsAdapter);

		/*
		 * tcpDraw = new TCPDraw(new TCPDraw.OnMessageReceived() {
		 * 
		 * @Override // here the messageReceived method is implemented public
		 * void messageReceived(String message) { // this method calls the
		 * onProgressUpdate if (message.length() > 0) { //sendMessage(message);
		 * arrayList.add(message); mAdapter.notifyDataSetChanged(); } } });
		 */
		if (mNetworkOn) {
			// startMessaging(); // Start thread to get messages
		}

	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		// setNotification();
		if (AppSettings.isChanged()) {
			getPreferences();
		}
		super.onResume();
	}

	@Override
	public void onDestroy() {
		mNetworkOn = false;
		try {
			this.finish();
		} catch (Throwable e) {
			// Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			setToast(e.getMessage(), LONG);

		}

		super.onDestroy();
		System.exit(0);
	}

	public static void setToast(String text, int duration) {
		Toast ImageToast = new Toast(sContext);
		LinearLayout toastLayout = new LinearLayout(sContext);

		toastLayout.setOrientation(LinearLayout.HORIZONTAL);
		toastLayout.setBackgroundResource(R.drawable.rounded_edges);
		ImageView image = new ImageView(sContext);
		image.setImageResource(R.drawable.appicon);
		toastLayout.addView(image);
		TextView tv = new TextView(sContext);
		tv.setTextColor(Color.BLACK);
		tv.setTextSize(18);
		tv.setTypeface(Typeface.DEFAULT);
		tv.setBackgroundColor(Color.TRANSPARENT);
		tv.setText(text);
		toastLayout.addView(tv);
		ImageToast.setView(toastLayout);
		ImageToast.setDuration(duration == 0 ? Toast.LENGTH_SHORT
				: Toast.LENGTH_LONG);
		ImageToast.show();
	}

	private void setScrollBarObserver() {
		final int PAD = 10;
		final HorizontalScrollView hz = (HorizontalScrollView) findViewById(R.id.hzmenu);
		ViewTreeObserver vto = hz.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				maxScrollX = hz.getWidth() - PAD;
			}
		});

		hz.setOnTouchListener(new OnTouchListener() {

			final ImageView right = (ImageView) findViewById(R.id.next);
			final ImageView left = (ImageView) findViewById(R.id.previous);

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int hzX = hz.getScrollX();
				if (hzX >= maxScrollX) {
					right.setVisibility(View.GONE);
					left.setVisibility(View.VISIBLE);

				} else if ((hzX >= PAD) && (hzX <= maxScrollX)) {
					left.setVisibility(View.VISIBLE);
					right.setVisibility(View.VISIBLE);
				} else {
					left.setVisibility(View.GONE);
					right.setVisibility(View.VISIBLE);

				}
				return false;
			}
		});

	}

	private void drawScreen() {
		// drawView = new DrawView(this);
		// drawView.setBackgroundColor(Color.WHITE);
		setContentView(R.layout.main);

		// drawView = new DrawView(this);
		drawView = (DrawView) findViewById(R.id.DrawView);
		drawView.setBackgroundColor(Color.WHITE);

		drawView.requestFocus();
	}

	/*
	 * private void startMessaging() { setMessageInfo(); new Thread(new
	 * Runnable() {
	 * 
	 * @Override public void run() { // while (true) {
	 * 
	 * while (mNetworkOn) { if(sChat==null){ sChat = new TCPChatClient(); if
	 * (mEmail.length() > 0) { // setToast(mEmail, SHORT); Token loginToken =
	 * new Token(); sendData(loginToken.createLoginToken(mMemberid)); }
	 * 
	 * } try { if (mRedraw) { Thread.sleep(4000); //
	 * mNetworkOn=AppSettings.getNetworkStatus(); } else { Thread.sleep(100); }
	 * String mMessage = getData("getmessage");
	 * 
	 * if (mMessage != null) { try { JSONArray drawList = new
	 * JSONArray(mMessage);
	 * 
	 * for (int i = 0; i < drawList.length(); i++) {
	 * 
	 * final JSONObject chatmessage = drawList .getJSONObject(i);
	 * 
	 * mHandler.post(new Runnable() {
	 * 
	 * @Override public void run() { try { if (chatmessage .optBoolean("MSG")) {
	 * String mMessage = chatmessage .getString("response"); String[] mNames =
	 * chatmessage .getString("names") .split(","); mTo=mNames[0] .replace(
	 * "\"", "") .replace( "[", "");
	 * 
	 * setToast( mTo + " says: " + mMessage, LONG); HashMap<String,
	 * CharSequence> messageMap = new HashMap<String, CharSequence>();
	 * messageMap.put("fullname", mTo); messageMap.put("message", mMessage);
	 * jsonMessagelist.add(0, messageMap);
	 * messageAdapter.notifyDataSetChanged();
	 * 
	 * }
	 * 
	 * if (!mLoggedin){ drawView.reDraw (chatmessage.toString());
	 * mLoggedin=true;} else{
	 * 
	 * mRedraw = drawView .chatDraw(chatmessage .toString()); // } } catch
	 * (JSONException e) { e.printStackTrace(); } } });
	 * 
	 * } } catch (JSONException e) { // e.printStackTrace(); } } mMessage =
	 * null; mMessage = getData("login"); if (mMessage != null) { try {
	 * JSONObject jsonLoginData = new JSONObject( mMessage); JSONArray jnames =
	 * jsonLoginData .getJSONArray("names"); JSONArray jto = jsonLoginData
	 * .getJSONArray("to");
	 * 
	 * mTo = jsonLoginData.getString("memberid"); // Set first item to senderid
	 * if (jto.length() > 0) { jto.put(0, mTo); } for (int i = 0; i <
	 * jto.length(); i++) { if (mTo.contentEquals(jto.getString(i))) { mTo =
	 * jnames.getString(i); sMemberIds.add(jto.getInt(i));
	 * 
	 * mHandler.post(new Runnable() {
	 * 
	 * @Override public void run() { // Let the new user get the // latest
	 * drawing Token mMessageToken = new Token(); // Send info to friends if
	 * (!mLoggedin) { String chatString; chatString = mMessageToken
	 * .createMessagetoFriendsToken( mMemberid, drawView.getLatestDrawing(),
	 * true); sendData(chatString);
	 * 
	 * }
	 * 
	 * setToast(mTo.toString() + " just logged in.", LONG);
	 * 
	 * Toast.makeText( MainActivity.this, mTo.toString() + " just logged in.",
	 * Toast.LENGTH_LONG) .show();
	 * 
	 * } });
	 * 
	 * break; } }
	 * 
	 * } catch (JSONException e) { // Dont track JSON exception }
	 * 
	 * } mMessage = null; mMessage = getData("getsessions"); if (mMessage !=
	 * null) { mSessions=mMessage; getSessions(mMessage); }
	 * 
	 * mMessage = null; mMessage = getData("getfriends");
	 * 
	 * if (mMessage != null) {
	 * 
	 * try { sFriends = mMessage; JSONObject jsonLoginData = new JSONObject(
	 * mMessage); JSONObject jResponse = jsonLoginData
	 * .getJSONObject("response"); JSONArray jto = jsonLoginData
	 * .getJSONArray("to");
	 * 
	 * mTo = jsonLoginData.getString("memberid"); // Set first item to senderid
	 * if (jto.length() > 0) { jto.put(0, mTo); } for (int i = 0; i <
	 * jto.length(); i++) { if (mTo.contentEquals(jto.getString(i))) { // mTo =
	 * jnames.getString(i); sMemberIds.add(jto.getInt(i));
	 * 
	 * mHandler.post(new Runnable() {
	 * 
	 * @Override public void run() { // Let the new user get the // latest
	 * drawing
	 * 
	 * setToast(mTo.toString() + " just logged in.", LONG); } });
	 * 
	 * break; } }
	 * 
	 * } catch (JSONException e) { // Dont track JSON exception }
	 * 
	 * }
	 * 
	 * } catch (InterruptedException e) { // Dont interrupt } catch
	 * (JSONException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 * 
	 * } }
	 * 
	 * }).start();
	 * 
	 * };
	 */
	private void getLogin(String _message) throws JSONException {

		List<Integer> mMemberIds = new ArrayList<Integer>();
		String mMessage = null;
		if (_message != null) {
			mMessage = _message;

			JSONObject jsonLoginData = new JSONObject(mMessage);
			JSONArray jnames = jsonLoginData.getJSONArray("names");
			JSONArray jto = jsonLoginData.getJSONArray("to");

			mTo = jsonLoginData.getString("memberid");
			// Set first item to senderid
			if (jto.length() > 0) {
				jto.put(0, mTo);
			}
			for (int i = 0; i < jto.length(); i++) {
				if (mTo.contentEquals(jto.getString(i))) {
					mTo = jnames.getString(i);
					sMemberIds.add(jto.getInt(i));

					// Let the new user get the
					// latest drawing
					Token mMessageToken = new Token();
					// Send info to friends
					if (!mLoggedin) {
						String chatString;
						chatString = mMessageToken.createMessagetoFriendsToken(
								mMemberid, drawView.getLatestDrawing(), false);
						sendMessage(chatString);

					}

					setToast(mTo.toString() + " just logged in.", LONG);
				}

				break;
			}
		}
	}

	private void getMessage(String _Message) {
		if (_Message != null) {
			try {
				JSONObject jResponse = new JSONObject(_Message);
				// Redraw any items that should be redrawn on screen;need to
				// move this to a redraw type of message
				mRedraw = drawView.chatDraw(_Message.toString());
				/*
				 * //New Stuff _Message = jResponse.getString("response");
				 * JSONArray jsonArray = new JSONArray(_Message); int
				 * len=jsonArray.length(); String image; for (int i = 0; i <
				 * jsonArray.length(); i++) { //Add this to mImagesList as a
				 * thumbnail image=jsonArray.getJSONObject(i).getString("BKG");
				 * Bitmap b = drawView.getBitmapFromString(image); }
				 */
				return;
			} catch (JSONException e1) {
				// Different message types exist handle them
				e1.printStackTrace();
			}
			// If a regular message add to the Message list
			try {

				final JSONObject chatmessage = new JSONObject(_Message);

				// if
				// (chatmessage.optBoolean("isMsg")||chatmessage.optBoolean("MSG"))
				// {
				if (chatmessage.optBoolean("MSG")) {
					String mMessage = chatmessage.optString("response");
					if (mMessage.length() < 1) {
						mMessage = chatmessage.optString("message");
					}
					// JSONArray mstat = chatmessage.optJSONArray("mStat");

					// mTo = mstat.optJSONObject(0).getString("name");
					mTo = getTo(chatmessage);

					// if (chatmessage.optString("names").length() > 0) {
					// String[] mNames = chatmessage.getString("names").split(
					// ",");
					// mTo = mNames[0].replace("\"", "").replace("[", "");
					// }
					HashMap<String, CharSequence> messageMap = new HashMap<String, CharSequence>();
					messageMap.put("fullname", mTo);
					messageMap.put("message", mMessage);
					// Add to the top of the list as messages come in
					jsonMessagelist.add(0, messageMap);
					messageAdapter.notifyDataSetChanged();

				}
			} catch (JSONException e1) {
				// Different message types exist handle them
				e1.printStackTrace();
			}

		}
	}

	private String getTo(JSONObject chatmessage) throws JSONException {
		String to = "Unknown";
		JSONArray mstat = chatmessage.optJSONArray("mStat");
		if (mstat != null) {
			String from = chatmessage.getString("memberid");
			for (int i = 0; i < mstat.length(); i++) {
				if (mstat.getJSONObject(i).getString("id").contentEquals(from)) {
					to = mstat.optJSONObject(i).getString("name");
					break;
				}
			}
		}
		return to;

	}

	private void getFriends(String _message) {
		String mFriends = _message;
		if (_message != null) {

			try {
				mFriends = _message;
				JSONObject jsonLoginData = new JSONObject(mFriends);
				JSONObject jResponse = jsonLoginData.getJSONObject("response");
				JSONArray jto = jsonLoginData.getJSONArray("to");

				mTo = jsonLoginData.getString("memberid");
				// Set first item to senderid
				if (jto.length() > 0) {
					jto.put(0, mTo);
				}
				for (int i = 0; i < jto.length(); i++) {
					if (mTo.contentEquals(jto.getString(i))) {
						// mTo = jnames.getString(i);
						sMemberIds.add(jto.getInt(i));

						mHandler.post(new Runnable() {
							@Override
							public void run() {
								setToast(mTo.toString() + " just logged in.",
										LONG);
							}
						});

						break;
					}
				}

			} catch (JSONException e) {
				// Dont track JSON exception
			}

		}
	}

	private void findMember(String _Message) throws JSONException {
		clearMemberList();
		JSONObject chatmessage;
		if (_Message != null) {

			JSONObject jData = new JSONObject(_Message);
			String mResponse = jData.getString("response");
			String mTokenId = jData.getString("_id");
			// for (int i = 0; i < jData.length(); i++) {

			// String mResponse = jData.getJSONObject(i).get("response")
			// .toString();
			// String mTokenId = jData.getJSONObject(i).get("_id").toString();
			JSONArray jMembers = new JSONArray(mResponse);

			for (int j = 0; j < jMembers.length(); j++) {
				chatmessage = jMembers.getJSONObject(j);
				HashMap<String, String> memberMap = new HashMap<String, String>();
				memberMap.put("tokenid", mTokenId);

				memberMap.put("id", chatmessage.get("id").toString());
				memberMap.put("firstname", chatmessage.get("firstname")
						.toString());
				memberMap.put("lastname", chatmessage.get("lastname")
						.toString());
				memberMap.put("name", chatmessage.get("knownas").toString());
				if (!mMemberFindList.contains(memberMap)) {
					mMemberFindList.add(memberMap);
				}
			}

		}

	}

	// Clear the memberlist so another search can be done
	public static ArrayList<HashMap<String, String>> getMemberList() {
		return mMemberFindList;
	}

	// Clear the memberlist so another search can be done
	private int clearMemberList() {
		mMemberFindList.clear();
		return mMemberFindList.size();
	}

	/*
	 * private void findMember(String _Message) throws JSONException {
	 * 
	 * JSONObject chatmessage; if (_Message != null) {
	 * 
	 * JSONArray jData = new JSONArray(_Message);
	 * 
	 * for (int i = 0; i < jData.length(); i++) {
	 * 
	 * String mResponse = jData.getJSONObject(i).get("response") .toString();
	 * String mTokenId = jData.getJSONObject(i).get("_id").toString(); JSONArray
	 * jMembers = new JSONArray(mResponse);
	 * 
	 * for (int j = 0; j < jMembers.length(); j++) { chatmessage =
	 * jMembers.getJSONObject(j); HashMap<String, String> memberMap = new
	 * HashMap<String, String>(); memberMap.put("tokenid", mTokenId);
	 * 
	 * memberMap.put("id", chatmessage.get("id").toString());
	 * memberMap.put("firstname", chatmessage.get("firstname") .toString());
	 * memberMap.put("lastname", chatmessage.get("lastname") .toString());
	 * memberMap .put("name", chatmessage.get("knownas").toString()); if
	 * (!mMemberList.contains(memberMap)) { mMemberList.add(memberMap); } //
	 * test = mMemberList.toString(); } mHandler.post(new Runnable() {
	 * 
	 * @Override public void run() { } });
	 * 
	 * break; } }
	 * 
	 * }
	 */

	// /REAL
	private void getSessions(String _message) throws JSONException {
		mGroupSelected = AppSettings.getGroupId();
		if (mGroupSelected == null) {
			mGroupSelected = "";
		}

		if (_message != null) {

			JSONObject chatmessage = new JSONObject(_message);
			String message = chatmessage.optString("message");

			JSONArray jSessionsArray = new JSONArray(message);
			int lensessions = jSessionsArray.length();
			for (int m = 0; m < lensessions; m++) {
				if (m == 0) {
					// JSONObject jmessage= jSessionsArray.getJSONObject(m);
					// String groupmessage=jmessage.getString("message");
					JSONArray jGroupArray = new JSONArray(
							jSessionsArray.getString(m));
					mSessions = jGroupArray.toString();
					String mMessage = "";// = chatmessage.optString("message");
					// JSONArray jGroupArray = new JSONArray(
					// chatmessage.getString("message"));

					int len = jGroupArray.length();
					for (int j = 0; j < len; j++) {
						String group = jGroupArray.getJSONObject(j).getString(
								"sessionName")
								+ " "
								+ jGroupArray.getJSONObject(j).getString(
										"sessionNo") + "  ";
						JSONObject jgroupobject = jGroupArray.getJSONObject(j);
						if (jgroupobject.getString("_id").contentEquals(
								mGroupSelected)) {
							mGroupSelectedPosition = j;
						}
						HashMap<String, String> fieldsMap = new HashMap<String, String>();
						Iterator iter = jgroupobject.keys();
						while (iter.hasNext()) {
							String key = (String) iter.next();
							String value = jgroupobject.getString(key);
							fieldsMap.put(key, value);
						}
						fieldsMap.put("group", group);
						// Add all group information to the grouplist
						mGroupList.add(fieldsMap);

						// Detail Array has info on members that belong to group
						JSONArray jDetailArray = new JSONArray(jGroupArray
								.getJSONObject(j).getString("members"));
						for (int k = 0; k < jDetailArray.length(); k++) {
							String detail = jDetailArray.getJSONObject(k)
									.optString("id")
									+ " "
									+ jDetailArray.getJSONObject(k).optString(
											"name") + "  ";
							mDetailList.add(detail);
							addMemberToGroup(group, detail);
						}
						mMessage += group;
					}
					// }
					mHandler.post(new Runnable() {
						@Override
						public void run() {

							// addItemstoGroupSpinner();
							setMemberInfo();
						}
					});
				} else if (m > 0) {
					JSONObject jmessages = new JSONObject(
							jSessionsArray.getString(m));
					getMessage(jmessages.toString());
				}
			}
		}
	}

	/*
	 * private void addItemstoGroupSpinner() { final Spinner mSpinner =
	 * (Spinner) findViewById(R.id.spinnersession);
	 * 
	 * spinnerAdapter = new spinnerAdapter(this, mGroupList,
	 * R.layout.spinner_layout, new String[] { "_id", "group", "sessionNo" },
	 * new int[] { R.id.spinnerId, R.id.spinnerTarget, R.id.spinnerNo });
	 * 
	 * spinnerAdapter.setDropDownViewResource(R.layout.spinner_layout);
	 * mSpinner.setAdapter(spinnerAdapter);
	 * 
	 * mSpinner.setSelection(mGroupSelectedPosition, true);
	 * mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() { boolean
	 * mFirst = true;
	 * 
	 * @Override public void onItemSelected(AdapterView<?> parentView, View
	 * selectedItemView, int position, long id) { TextView groupNo = (TextView)
	 * selectedItemView .findViewById(R.id.spinnerNo); TextView groupName =
	 * (TextView) selectedItemView .findViewById(R.id.spinnerTarget); TextView
	 * groupId = (TextView) selectedItemView .findViewById(R.id.spinnerId); //
	 * String s = groupId.getText().toString();
	 * AppSettings.setGroupId(groupId.getText().toString());
	 * mGroupSelectedPosition = position; setMemberInfo();
	 * mSpinner.setVisibility(View.GONE); mFirst = false; }
	 * 
	 * @Override public void onNothingSelected(AdapterView<?> parentView) { //
	 * your code here }
	 * 
	 * }); }
	 */
	// here we maintain members in various groups
	private int addMemberToGroup(CharSequence _group, String _memberName) {

		int groupPosition = 0;
		HeaderInfo headerInfo = null;

		// check the hash map if the group already exists
		headerInfo = mGroups.get(_group);
		// add the group if doesn't exists
		if (headerInfo == null) {

			headerInfo = new HeaderInfo();
			headerInfo.setName(_group);
			mGroups.put((String) _group, headerInfo);
			groupList.add(headerInfo);

		}

		// get the children for the group
		ArrayList<DetailInfo> detailList = headerInfo.getDetailList();
		// size of the children list
		int listSize = detailList.size();
		// add to the counter
		listSize++;

		// create a new child and add that to the group
		DetailInfo detailInfo = new DetailInfo();
		detailInfo.setSequence(String.valueOf(listSize));
		detailInfo.setName(_memberName);
		detailList.add(detailInfo);
		headerInfo.setDetailList(detailList);

		// find the group position inside the list
		groupPosition = groupList.indexOf(headerInfo);
		return groupPosition;
	}

	public static String getFriends() {
		return sFriends;
	}

	/*
	 * public static void sendData(String aMessage) { // final String message =
	 * aMessage; // new Thread(new Runnable() { // public void run() {
	 * TCPChatClient.sendData(aMessage); // } // }).start();
	 * 
	 * 
	 * final class sendDataTask<String,Integer,Void> extends AsyncTask {
	 * 
	 * @Override protected Integer doInBackground(Object... message) {
	 * TCPChatClient.sendData((java.lang.String) message[0]); return null; }
	 * 
	 * 
	 * 
	 * }
	 * 
	 * };
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.friends) {
			if (!mNetworkOn) {
				setToast(getString(R.string.networknotavailable), SHORT);
				// drawView.stopSlideShow(false);

				return false;
			}
			Bundle bundle = new Bundle();
			bundle.putString("memberid", mMemberid);
			// Intent newIntent = new Intent(this, ContactDetail.class);
			Intent newIntent = new Intent(this, FriendsDetail.class);
			newIntent.putExtras(bundle);
			startActivityForResult(newIntent, SELECT_CONTACTS);

			return true;
		} else if (id == R.id.sessionsetup) {
			Bundle bundle = new Bundle();
			bundle.putString("memberid", mMemberid);
			bundle.putString("sessions", mSessions);

			Intent newIntent = new Intent(this, FriendsActivity.class);
			newIntent.putExtras(bundle);
			startActivity(newIntent);
			// startActivityForResult(newIntent, SELECT_CONTACTS);
			return true;

		} else if (id == R.id.about) {
			showDialog(ABOUT);
		} else if (id == R.id.setup) {
			startActivity(new Intent(this, AppSettings.class));
		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressLint("NewApi")
	@Override
	public void colorChanged(int color, int option, int brushopacity,
			int backgroundopacity) {
		mBrushOpacity = brushopacity;
		AppSettings.setBrushOpacity(sContext, mBrushOpacity);
		mBackgroundOpacity = backgroundopacity;
		AppSettings.setBackgroundOpacity(sContext, mBackgroundOpacity);

		if (option == 0) {
			drawView.setColor(color);
			drawView.setBrushOpacity(mBrushOpacity);
			// EditText drawText = (EditText) findViewById(R.id.draw_text);
			mEditText.setTextColor(color);
			if (Build.VERSION.SDK_INT > 10) {
				mEditText.setAlpha(mBrushOpacity);
			}
			AppSettings.setColor(sContext, color);
		} else {
			drawView.setBackgroundOpacity(mBackgroundOpacity);
			drawView.setBackGroundColor(color);
		}
	}

	public static Context getContext() {
		return sContext;
	}

	private void addItemstoSpinner() {
		if (mFontList != null) {
			return;
		}
		assetManager = getAssets();
		mSpinnerFont = (Spinner) findViewById(R.id.spinnerfont);
		try {
			mFontList = assetManager.list("fonts");
		} catch (IOException e) {
			// TODO NOTHING
		}
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, mFontList);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinnerFont.setAdapter(dataAdapter);

		mSpinnerFont
				.setOnItemSelectedListener(new CustomOnItemSelectedListener());
	}

	private String setTextString() {
		// EditText t = (EditText) findViewById(R.id.draw_text);
		String mTextString = mEditText.getText().toString();
		mEditText.setText("");
		View lay = (View) findViewById(R.id.draw_text_layout);

		lay.setVisibility(View.GONE);

		imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);

		return mTextString;
	}

	@SuppressLint("NewApi")
	private void getPreferences() {
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		// Shows who is logged in
		mEmail = AppSettings.getEmailAddress(this);
		mMemberid = AppSettings.getMemberid(this);
		// New generated memberid philosophy
		// mMemberid = AppSettings.getMemberid();
		mNetworkOn = AppSettings.getNetworkStatus();
		mEditText = (EditText) findViewById(R.id.draw_text);

		int sc = AppSettings.getColor(this);
		if (sc != 0) {
			drawView.setColor(sc);
			mEditText.setTextColor(sc);

		}

		mBrushOpacity = AppSettings.getBrushOpacity();
		if (mBrushOpacity < 1) {
			mBrushOpacity = 255;
		} // opaque
		drawView.setBrushOpacity(mBrushOpacity);
		if (Build.VERSION.SDK_INT > 10) {
			mEditText.setAlpha(mBrushOpacity);
		}

		mBackgroundOpacity = AppSettings.getBackgroundOpacity();
		if (mBackgroundOpacity < 1) {
			mBackgroundOpacity = 255;
		} // opaque
		drawView.setBackgroundOpacity(mBackgroundOpacity);

		mBrushProgress = AppSettings.getBrushWidth(sContext).intValue();
		if (mBrushProgress < 1) {
			mBrushProgress = 10;
			AppSettings.setBrushWidth(sContext, mBrushProgress);
		}
		drawView.setStrokeWidth(mBrushProgress);

		mTextProgress = AppSettings.getTextSize(this);
		if (mTextProgress < 1) {
			mTextProgress = 80;
			AppSettings.setTextSize(sContext, mTextProgress);
		}
		drawView.setTextSize(mTextProgress);
		// drawText.setTextSize(mTextProgress);

		mEraserProgress = AppSettings.getEraserWidth(this);
		if (mEraserProgress < 1) {
			mEraserProgress = 50;
			AppSettings.setEraserWidth(sContext, mEraserProgress);
		}
		drawView.setEraserWidth(mEraserProgress);

		int mSlideShowProgress = AppSettings.getSlideShowInterval();
		if (mSlideShowProgress < 1) {
			mSlideShowProgress = 15;
			AppSettings.setSlideShowInterval(mSlideShowProgress);
		}
		int stroketype = AppSettings.getStrokeType(this);
		drawView.setMaskFilter(stroketype);

		addItemstoSpinner();

		String typeface = AppSettings.getTypeFace();

		if (typeface == null) {
			typeface = "Roboto-Thin.ttf";
		}
		Typeface font = Typeface.createFromAsset(assetManager, mTypeFacePath
				+ typeface);

		drawView.setTypeFace(font, typeface);
		mEditText.setTypeface(font);

		/*
		 * if (mEmail.length() > 0) { setToast(mEmail, SHORT); Token loginToken
		 * = new Token(); sendData(loginToken.createLoginToken(mMemberid)); }
		 */}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
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

		case PROTRACTOR:
			builder.setTitle(R.string.protractortitle);
			final String[] arr = { "-90", "-75", "-60", "-45", "-30", "-15",
					"0", "15", "30", "45", "60", "75", "90" };
			builder.setSingleChoiceItems(arr, 6, null);
			builder.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.dismiss();
							int selectedPosition = ((AlertDialog) dialog)
									.getListView().getCheckedItemPosition();
							drawView.rotateText(Integer
									.parseInt(arr[selectedPosition]));
							// setToast(arr[selectedPosition]+ "Degrees",SHORT);
						}
					});
			break;
		case FONTTYPE:
			builder.setTitle(R.string.selectfonts);
			builder.setSingleChoiceItems(mFontList, 0, null);
			builder.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.dismiss();
							int selectedPosition = ((AlertDialog) dialog)
									.getListView().getCheckedItemPosition();
							String mSelection = mFontList[selectedPosition];
							AppSettings.setTypeFace(MainActivity.getContext(),
									mSelection);

							Typeface font = Typeface.createFromAsset(
									assetManager, mTypeFacePath + mSelection);
							// EditText drawText = (EditText)
							// findViewById(R.id.draw_text);
							mEditText.setTypeface(font);

							drawView.setTypeFace(font, mSelection);
						}
					});
			break;

		case ABOUT:

			layout = inflater.inflate(R.layout.about, null);
			builder.setTitle(getString(R.string.app_name));
			builder.setView(layout);

			builder.setPositiveButton(android.R.string.ok,
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

							dialog.dismiss();
						}
					});
			builder.setNeutralButton(R.string.faq, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

					Uri uri = Uri
							.parse("http://artytheartist.com/artytheartistfaq.html");
					startActivity(new Intent(Intent.ACTION_VIEW, uri));
					dialog.dismiss();
				}
			});

			break;
		case SAVECANVAS:
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
									File folder = drawView.save(filename
											.getText().toString());
									sendBroadcast(new Intent(
											Intent.ACTION_MEDIA_MOUNTED,
											Uri.parse("file://"
													+ Environment
															.getExternalStorageDirectory())));

									// appInfoAdapter.notifyDataSetChanged();
									// dialog.cancel();
									removeDialog(SAVECANVAS);
									removeDialog(RESTORECANVAS); // Effectively
																	// required
																	// RESTORECANVAS
																	// dialog to
																	// be
																	// renewed
									setToast(getString(R.string.savetext),
											SHORT);
								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {

									dialog.dismiss();
								}
							});

			break;
		case RESTORECANVAS:
			break;
		case STROKEWIDTH:
			View layoutorientation = null;

			layoutorientation = inflater.inflate(R.layout.seekbar,
					(ViewGroup) findViewById(R.id.stroke));
			layout = layoutorientation;

			strokeprogress = (TextView) layout.findViewById(R.id.stroketext);
			eraserprogress = (TextView) layout.findViewById(R.id.erasertext);
			textsizeprogress = (TextView) layout
					.findViewById(R.id.textsizetext);

			builder.setTitle(R.string.brush);
			builder.setView(layout);

			final RadioGroup radiostroketype = (RadioGroup) layout
					.findViewById(R.id.radiostroketype);
			switch (AppSettings.getStrokeType(sContext)) {
			case 0: // Normal
				radiostroketype.check(R.id.radionormal);
				break;
			case 1: // Blur
				radiostroketype.check(R.id.radioblur);
				break;
			case 2: // Emboss
				radiostroketype.check(R.id.radioemboss);
				break;
			case 3: // Emboss
				radiostroketype.check(R.id.radioglow);
				break;
			default:
				radiostroketype.check(R.id.radionormal);
			}

			SeekBar sbstrokewidth = (SeekBar) layout
					.findViewById(R.id.strokewidth);

			mBrushProgress = AppSettings.getBrushWidth(sContext).intValue();

			strokeprogress.setText(mBrushProgress.toString());
			sbstrokewidth.setMax(50);
			sbstrokewidth.setProgress(mBrushProgress);
			sbstrokewidth
					.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
						@Override
						public void onProgressChanged(SeekBar seekBar,
								int progress, boolean fromUser) {
							mBrushProgress = progress;
							strokeprogress.setText(Integer.toString(progress));
						}

						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onStopTrackingTouch(SeekBar seekBar) {
							strokeprogress.setText(Integer.toString(seekBar
									.getProgress()));
						}

					});
			SeekBar sberaser = (SeekBar) layout.findViewById(R.id.eraserwidth);
			sberaser.setMax(100);
			mEraserProgress = AppSettings.getEraserWidth(sContext).intValue();
			sberaser.setProgress(mEraserProgress);
			eraserprogress.setText(mEraserProgress.toString());

			sberaser.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					mEraserProgress = progress;
					eraserprogress.setText(Integer.toString(progress));

				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
				}
			});

			SeekBar sbtextsize = (SeekBar) layout.findViewById(R.id.textsize);
			sbtextsize.setMax(300);
			mTextProgress = AppSettings.getTextSize(sContext).intValue();

			sbtextsize.setProgress(mTextProgress);
			textsizeprogress.setText(mTextProgress.toString());

			sbtextsize
					.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
						@Override
						public void onProgressChanged(SeekBar seekBar,
								int progress, boolean fromUser) {
							mTextProgress = progress;
							textsizeprogress.setText(Integer.toString(progress));

						}

						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onStopTrackingTouch(SeekBar seekBar) {

						}
					});

			builder.setPositiveButton(android.R.string.ok,
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							int id = radiostroketype.getCheckedRadioButtonId();
							if (id == R.id.radionormal) {
								drawView.setMaskFilter(0); // NORMAL
								AppSettings.setStrokeType(sContext, 0);
							} else if (id == R.id.radioblur) {
								drawView.setMaskFilter(1); // BLUR
								AppSettings.setStrokeType(sContext, 1);
							} else if (id == R.id.radioemboss) {
								drawView.setMaskFilter(2); // EMBOSS
								AppSettings.setStrokeType(sContext, 2);
							} else if (id == R.id.radioglow) {
								drawView.setMaskFilter(3); // GLOW
								AppSettings.setStrokeType(sContext, 3);
							}
							drawView.setStrokeWidth((float) mBrushProgress);
							AppSettings.setBrushWidth(sContext, mBrushProgress);

							drawView.setTextSize(mTextProgress);
							AppSettings.setTextSize(sContext, mTextProgress);
							// EditText drawText = (EditText)
							// findViewById(R.id.draw_text);
							// drawText.setTextSize(mTextProgress);

							drawView.setEraserWidth(mEraserProgress);
							AppSettings.setEraserWidth(sContext,
									mEraserProgress);

							dialog.dismiss();
						}
					});

			break;
		}

		mDialog = builder.create();
		mDialog.show();
		return mDialog;
	}

	@Override
	protected void onPrepareDialog(final int id, final Dialog dialog) {
		switch (id) {
		case SAVECANVAS: // Ultimately when i do a save with the Filelist
							// adapter then notifydatachanged
			break;
		}
	}

	public void ClickHandler(View v) throws InterruptedException {
		if (v.getId() != R.id.img_colorsampler) {
			mSampleStatus = drawView.setSampleStatus(false);
		}
		if (v.getId() != R.id.img_erase) {
			mErase = drawView.erase(false);
		}
		if (v.getId() == R.id.img_messagelist) {
			if (mMessageList.getVisibility() == View.GONE) {
				mMessageList.setVisibility(View.VISIBLE);
			} else {
				mMessageList.setVisibility(View.GONE);
			}
		} else if (v.getId() == R.id.checkboxheader) {
			CheckBox cb = (CheckBox) v;
			int len = mMemberInfo.size();
			for (int i = 0; i < len; i++) {
				HashMap checkstatus = new HashMap();
				// Get the hashmap for the row
				checkstatus = mMemberInfo.get(i);
				// Set each rows hashmap item membercheckbox so it is the same
				// as the header checkbox
				checkstatus.put("membercheckbox", cb.isChecked());
			}
			memberAdapter.notifyDataSetChanged();
		} else if (v.getId() == R.id.membercheckbox) {
			CheckBox cb = (CheckBox) v;
			int row = (Integer) v.getTag();
			HashMap checkstatus = new HashMap();
			checkstatus = mMemberInfo.get(row);
			checkstatus.put("membercheckbox", cb.isChecked());
			mMemberInfo.set(row, checkstatus);
			// memberAdapter.SetCheckbox(row);
			memberAdapter.notifyDataSetChanged();
			// HashMap mHash = (HashMap) memberAdapter.getItem(row);
			// mMemberList.setItemChecked(row, cb.isChecked());

			// String test = (String) mHash.get("message");
			// mHash.put("message",
			// jsonMemberList.getJSONObject(i).getString("id"));
			// mHash.put("membercheckbox",true);

			// mMemberInfo.add(mHash);

			// setToast(test, SHORT);
			// }

		} else if (v.getId() == R.id.img_memberlist) {
			if (mMemberList.getVisibility() == View.GONE) {
				mMemberList.setVisibility(View.VISIBLE);
			} else {
				mMemberList.setVisibility(View.GONE);
			}
		} else if (v.getId() == R.id.img_imageslist) {
			if (mImagesList.getVisibility() == View.GONE) {
				mImagesList.setVisibility(View.VISIBLE);
			} else {
				mImagesList.setVisibility(View.GONE);
			}
		} else if (v.getId() == R.id.img_arttools) {
			if (mArtToolsList.getVisibility() == View.GONE) {
				mArtToolsList.setVisibility(View.VISIBLE);
			} else {
				mArtToolsList.setVisibility(View.GONE);
			}

		} else if (v.getId() == R.id.cleartext) {
			imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
			mEditText.setText("");
		} else if (v.getId() == R.id.img_overflow) {
			openOptionsMenu();
		} else if (v.getId() == R.id.previous) {
			final HorizontalScrollView hz = (HorizontalScrollView) findViewById(R.id.hzmenu);
			final ImageView right = (ImageView) findViewById(R.id.next);
			final ImageView left = (ImageView) findViewById(R.id.previous);
			hz.scrollTo(0, 0);
			right.setVisibility(View.VISIBLE);
			left.setVisibility(View.GONE);
		} else if (v.getId() == R.id.next) {
			final HorizontalScrollView hz = (HorizontalScrollView) findViewById(R.id.hzmenu);
			final ImageView right = (ImageView) findViewById(R.id.next);
			final ImageView left = (ImageView) findViewById(R.id.previous);
			hz.scrollTo(maxScrollX, 0);
			right.setVisibility(View.GONE);
			left.setVisibility(View.VISIBLE);

		} else if (v.getId() == R.id.img_protractor) {
			showDialog(PROTRACTOR);
		} else if (v.getId() == R.id.img_fonttype) {
			showDialog(FONTTYPE);
		} else if (v.getId() == R.id.img_text) {
			// View mDrawText = (View) findViewById(R.id.draw_text_layout);
			drawView.setTextView(mEditText, mDrawText, imm);
			View lay = (View) findViewById(R.id.draw_text_layout);
			lay.setVisibility(View.VISIBLE);
		} else if (v.getId() == R.id.img_message) {
			imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
			if (!mNetworkOn) {
				setToast(getString(R.string.messagenotavailable), LONG);
				return;
			}
			// String genId = AppSettings.generateUniqueId(30);
			// String mMessage[];
			String mTextString = mEditText.getText().toString();

			// EdiSupport ediTest = new EdiSupport();
			// Get a Message for testing
			// mMessage = ediTest.createTest("204data20stops");
			// mTextString = mMessage[4];
			// if (mTextString == null) {
			// mTextString = mEditText.getText().toString();
			// }
			if (mTextString.length() < 1) {
				setToast(getString(R.string.nomessage), SHORT);
				return;
			}
			String chatString;
			// for (int r=0;r<100000;r++){
			// String mTextStringtest =mTextString+Integer.toString(r);
			int membersize = mMemberInfo.size();
			JSONArray jarray = new JSONArray();
			for (int i = 0; i < membersize; i++) {

				HashMap mHash = (HashMap) mMemberInfo.get(i);
				boolean cb = Boolean.parseBoolean(mHash.get("membercheckbox")
						.toString());
				if (cb) {
					JSONObject jMembers = new JSONObject();
					try {
						jMembers.put("name", mHash.get("fullname"));
						jMembers.put("id", mHash.get("message"));
						jMembers.put("status", "U");

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// jarray.put(mHash.get("message").toString());
					jarray.put(jMembers);
				}
			}

			Token mMessageToken = new Token();
			// chatString = mMessageToken.createMessagetoFriendsToken(mMemberid,
			// mTextString, true, true, jarray);
			// sendMessage(chatString);
			// //delay
			// Thread.sleep(1000);

			// Send 204
			// mTextString = mMessage[0];
			chatString = mMessageToken.createMessagetoFriendsToken(mMemberid,
					mTextString, true, true, jarray);
			sendMessage(chatString);
			// delay
			Thread.sleep(1000);

			// // Send Reply
			// chatString = mMessageToken.createMessagetoFriendsToken(mMemberid,
			// mMessage[1], true, true, jarray);
			// sendMessage(chatString);
			// //delay
			// Thread.sleep(1000);
			// // Send Reply
			// chatString = mMessageToken.createMessagetoFriendsToken(mMemberid,
			// mMessage[2], true, true, jarray);
			// sendMessage(chatString);
			// //delay
			// Thread.sleep(1000);
			// // Send Reply
			// chatString = mMessageToken.createMessagetoFriendsToken(mMemberid,
			// mMessage[3], true, true, jarray);
			// sendMessage(chatString);

			// }
			mEditText.setText("");
			HashMap<String, CharSequence> messageMap = new HashMap<String, CharSequence>();

			/*
			 * messageMap.put("fullname", "Me"); messageMap.put("message",
			 * mTextString); jsonMessagelist.add(0, messageMap);
			 * messageAdapter.notifyDataSetChanged();
			 */
			// Toast.makeText(this, R.string.messagesent ,
			// Toast.LENGTH_SHORT).show();
			setToast(getString(R.string.messagesent), SHORT);
		} else if (v.getId() == R.id.img_color) {
			setToast(getString(R.string.colorpickerexplain), SHORT);
			new ColorPickerDialog(this, this, AppSettings.getColor(this),
					drawView.getColorSampleList(), mBrushOpacity,
					mBackgroundOpacity).show();
		} else if (v.getId() == R.id.img_brush) {
			showDialog(STROKEWIDTH);
		} else if (v.getId() == R.id.img_clear) {
			try {
				drawView.clearall();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Toast.makeText(this, R.string.cleartext,
			// Toast.LENGTH_SHORT).show();
			setToast(getString(R.string.cleartext), SHORT);
		} else if (v.getId() == R.id.img_erase) {
			mErase = (mErase == true) ? false : true;
			mErase = drawView.erase(mErase);
			if (mErase) {
				setToast(getString(R.string.erasetext), SHORT);
			} else {
				setToast(getString(R.string.erasecancel), SHORT);
			}
		} else if (v.getId() == R.id.img_colorsampler) {
			// boolean currentStatus=(mSampleStatus== false)?true:false;
			if (mSampleStatus == true) {
				mSampleStatus = false;
				setToast(getString(R.string.colorsamplernotenabled), SHORT);
			} else {
				mSampleStatus = true;
				setToast(getString(R.string.colorsamplerenabled), LONG);
			}
			mSampleStatus = drawView.setSampleStatus(mSampleStatus);
		} else if (v.getId() == R.id.img_save) {
			showDialog(SAVECANVAS);
		} else if (v.getId() == R.id.img_restore) {
			// drawView.stopSlideShow(false);

			try {
				drawView.clearall();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			getContentResolver().notifyChange(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null);
			Intent ImageIntent = new Intent(this, Images.class);
			startActivityForResult(ImageIntent, SELECT_IMAGE);
			// return true;
		} else if (v.getId() == R.id.img_share) {
			String shareFile = getText(R.string.sharefile).toString();
			File folder = drawView.save(shareFile);
			Uri uriToImage = null;
			uriToImage = Uri.fromFile(folder);
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
			shareIntent.setType("image/png");
			startActivity(Intent.createChooser(shareIntent, getResources()
					.getText(R.string.send_to)));
		} else if (v.getId() == R.id.img_information) {
			Intent infoIntent = new Intent(this, InfoWebActivity.class);
			startActivity(infoIntent);
		} else if (v.getId() == R.id.img_undo) {
			try {
				drawView.reDraw(MOVEFORWARD, true);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (v.getId() == R.id.img_redo) {
			try {
				drawView.reDraw(MOVEBACKWARD, true);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (v.getId() == R.id.radiostroketype) {
			// Toast.makeText(this, "TEXT", Toast.LENGTH_SHORT).show();
			setToast("TEXT", Toast.LENGTH_SHORT);

		} else if (v.getId() == R.id.radioemboss) {
			setToast("embTEXT", Toast.LENGTH_SHORT);
			// Toast.makeText(this, "embTEXT", Toast.LENGTH_SHORT).show();
		} else if (v.getId() == R.id.leftbutton) {
			v.getBackground().setLevel(0);
			drawView.backOneSlide();
		} else if (v.getId() == R.id.centerbutton) {
			if (v.getBackground().getLevel() == 1) {
				v.getBackground().setLevel(0);
				drawView.pauseSlideShow(true);
			} else {
				v.getBackground().setLevel(1);
				if (drawView.isSlideShowStarted()) {
					drawView.pauseSlideShow(false);
				} else {
					drawView.startSlideShow(imagesAdapter.getSlideList(), 0,
							5000);
				}
			}

		} else if (v.getId() == R.id.rightbutton) {
			v.getBackground().setLevel(0);
			drawView.forwardOneSlide();
		} else {
			// Toast.makeText(this, "" + v.getId(), Toast.LENGTH_SHORT).show();
			setToast(getString(v.getId()), Toast.LENGTH_SHORT);
		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// int i = newConfig.orientation;
		// setToast(Integer.toString(lz.width)+"|"+Integer.toString(lz.height) ,
		// Toast.LENGTH_LONG);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// drawView.freememory();

		if (requestCode == SELECT_CONTACTS) {
			String mystuff = data.getStringExtra("mycontacts");
			mContacts = data.getStringExtra("mycontacts");
		}

		if (resultCode == RESULT_OK) {
			/*
			 * if (requestCode == SELECT_PICTURE) { Uri selectedImageUri =
			 * data.getData(); mSelectedImagePath = getPath(selectedImageUri);
			 * if (mSelectedImagePath == null) {
			 * setToast(getString(R.string.imagedoesnotexist), 0); return; }
			 * drawView.restore(mSelectedImagePath, false); //
			 * drawView.displayUserBitmap(mSelectedImagePath, 100, 0); }
			 */
			if (requestCode == SELECT_IMAGE) {
				// mSelectedImagePath = data.getStringExtra("imageresult");
				// if (mSelectedImagePath != null) {
				// drawView.restore(mSelectedImagePath, false);
				// drawView.displayUserBitmap(mSelectedImagePath, 100, 0);
				// }
				int slideshowinterval = data.getIntExtra("slideshowinterval",
						15);
				// if (slideshowinterval == 0) {
				if (mNetworkOn && (slideshowinterval < 8)) {
					// Don't allow slideshow to run faster than it can
					// process it. Change to producer/consume later
					slideshowinterval = 8;
				}

				slideshowinterval *= 1000;

				String[] result = data.getStringArrayExtra("result");

				if (result != null) {
					setImagesInfo();
					if (result.length == 1) {
						drawView.restore(result[0], false);
					} else {
						// Start slideshow
						setToast(getString(R.string.slideshowcancel), LONG);
						drawView.startSlideShow(result, 0, slideshowinterval);
					}
				}
			}
		} else if (resultCode == RESULT_CANCELED) {
			setToast(getString(R.string.noimages), LONG);
		}
	}

	/*
	 * public String getPath(Uri uri) { String[] projection = {
	 * MediaStore.Images.Media.DATA }; Cursor cursor = managedQuery(uri,
	 * projection, null, null, null); int column_index = cursor
	 * .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	 * cursor.moveToFirst(); return cursor.getString(column_index); }
	 */
	// List of memberids that are logged in
	public static List<Integer> getMemberids() {
		return sMemberIds;
	}

	public class CustomOnItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			String fontString = parent.getItemAtPosition(pos).toString();
			AppSettings.setTypeFace(MainActivity.getContext(), fontString);

			Typeface font = Typeface.createFromAsset(assetManager,
					mTypeFacePath + fontString);

			drawView.setTypeFace(font, fontString);

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		}

	}

	/*
	 * @Override public void onAttachedToWindow() { openOptionsMenu(); };
	 */
	public class connectTask extends AsyncTask<String, String, TCPClient> {

		@Override
		protected TCPClient doInBackground(String... message) {

			// we create a TCPClient object and
			mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
				@Override
				// here the messageReceived method is implemented
				public void messageReceived(String message) {
					// this method calls the onProgressUpdate
					if (message.length() > 0) {
						publishProgress(message);
					}
				}
			});
			mTcpClient.run();

			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			mMessage = values[0];
			if (mMessage.startsWith("CONNECTED:")) {
				return;
			}
			Token mToken = new Token();
			mToken.createToken(mMessage);

			// Change to a produce/consume thread...i.e. wait/notify using
			// arraylist of messages get rid of the sleep below

			String mType = mToken.getType();
			if (mType.contentEquals("getmessage")) {

				// sends the message to the server
				if (mTcpClient != null) {
					getMessage(mMessage);
				}

			} else if (mType.contentEquals("getsessions")) {
				// mSessions = mMessage;
				try {
					getSessions(mMessage);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (mType.contentEquals("getfriends")) {
				getFriends(mMessage);
			} else if (mType.contentEquals("findmember")) {
				try {
					findMember(mMessage);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else if (mType.contentEquals("login")) {
				try {
					getLogin(mMessage);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// Keep from jamming up on messages
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// in the arrayList we add the messaged received from server
			// tcpDraw.chatDraw(values[0]);
			// Moved adding to array to TCPDraw messagereceived
			// arrayList.add(values[0]);
			// notify the adapter that the data set has changed. This means that
			// new message received
			// from server was added to the list
			// mAdapter.notifyDataSetChanged();
		}
	}
}