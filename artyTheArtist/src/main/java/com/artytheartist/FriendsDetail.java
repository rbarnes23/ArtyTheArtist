package com.artytheartist;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tokenlibrary.Token;

// starting the class  
public class FriendsDetail extends Activity implements OnItemClickListener {
	private String mMemberid;
	private String is_message; // message to display in the alert dialog
	private TextView lempty;
	private FriendsTokenHelper friendsToken = null;
	private FriendsAdapter friendsAdapter;
	private String mFriendsTokenid;
	private ArrayList<HashMap<String, String>> jsonlist = new ArrayList<HashMap<String, String>>();

	// private ArrayList<HashMap<String, String>> contactlist = new
	// ArrayList<HashMap<String, String>>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = this.getIntent().getExtras();
		mMemberid = bundle.getString("memberid");

		setContentView(R.layout.main);
		TextView editText = (TextView) findViewById(R.id.draw_text);
		editText.setFocusable(false);
		lempty = (TextView) findViewById(R.id.empty);
		new PopulateFriends().execute();

	}

	@Override
	public void onResume() {
		super.onResume();
	}

	private class PopulateFriends extends
			AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {
		final ProgressDialog mPd = new ProgressDialog(FriendsDetail.this);

		// -- called if cancelled
		@Override
		protected void onCancelled() {
			super.onCancelled();

		}

		@Override
		protected void onPreExecute() {
			lempty.setText(R.string.waitforfriends);
			is_message = (String) lempty.getText();
			mPd.setIcon(R.drawable.appicon);
			mPd.setTitle(R.string.app_name);
			mPd.setIndeterminate(true);
			mPd.setMessage(is_message);
			mPd.setCancelable(false);
			mPd.show();
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> jsonlist) {
			ListView listView;
			listView = (ListView) findViewById(R.id.listview);
			lempty.setText(R.string.notloadingfriends);
			listView.setEmptyView(lempty);
			friendsAdapter = new FriendsAdapter(getApplicationContext(),
					jsonlist, R.layout.friendsrowlist,
					new String[] { "fullname", "sortnumber", "positionid",
							"loggedin" }, new int[] { R.id.fullname,
							R.id.sortnumber, R.id.positionid, R.id.loggedin });

			listView.setAdapter(friendsAdapter);
			listView.setOnItemClickListener(FriendsDetail.this);
			Intent mReturn = getIntent();
			// String mContact = contactlist.toString();
			// mContact.replaceAll("[^A-Za-z0-9()\\[\\]]", "");
			// mContact.replaceAll("[^\\w\\[\\]\\(\\)]", "");

			// JSONArray mcontact= new JSONArray(contactlist);
			// mReturn.putExtra("mycontacts",mContact );

			setResult(2, mReturn);

			mPd.dismiss();
		}

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				Void... params) {
			return populateFriendsList();
			// ArrayList<HashMap<String, String>> test = null;
			// return test;

		}

		private ArrayList<HashMap<String, String>> populateFriendsList() {
			int iarraylength;
			TextView lmessage = (TextView) findViewById(R.id.empty);
			lmessage.setText(R.string.loadingfriends);
			String is_acceptedoption = "1";
			// friendsToken = new FriendsTokenHelper();
			String mFriends = friendsToken.createFriendsToken(mMemberid,
					is_acceptedoption);
			MainActivity.sendMessage(mFriends);
			mFriendsTokenid = friendsToken.getID();

			int li_sort = -1;
			for (int j = 1; j < 30; j++) {
				try {
					// mFriends = MainActivity.getData("getfriends");
					mFriends = MainActivity.getFriends();
					if (mFriends != null) {
						JSONObject friendsTemp = new JSONObject(mFriends);
						// friendsTemp.parseObject(mFriends);
						String mID = (String) friendsTemp.get("_id");
						if (mID.contentEquals(mFriendsTokenid)) {
							friendsToken = new FriendsTokenHelper();
							friendsToken.createToken(mFriends);
							jsonlist = friendsToken.getResponse("friends");
							// int index = jsonlist.size();
							// jsonlist.addAll(getContacts(index));
							break;
						}
					}
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Toast.makeText(MainActivity.getContext(), e.getMessage(),
							Toast.LENGTH_LONG).show();

				}
			}
			return jsonlist;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> listView, View view, int position,
			long id) {
		boolean mExecute = true;

		if (view.getId() == R.id.friends_header_row_layout) {
			TextView sortnumber = (TextView) view.findViewById(R.id.sortnumber);
			int li_sortorder = Integer.parseInt((String) sortnumber.getText());
			JSONObject json_data = null;
			String mTag = (String) view.getTag();
			if (mTag.contentEquals("-1")) {
				return;
			}
			if (friendsToken.getFriends() != null) {
				try {
					if (view.getTag() != null) {
						position = Integer.parseInt(mTag);
					}

					json_data = friendsToken.getFriends().getJSONObject(
							position);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if (json_data == null) {
				// Connect the chat client
				// Toast.makeText(this, MainActivity.getStatus(),
				// Toast.LENGTH_SHORT).show();
				return;
			}
			switch (li_sortorder) {
			case 3: // SHOW
				Integer mShowLocation = null;
				try {
					mShowLocation = json_data.getInt("showlocation");
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					// e1.printStackTrace();
				}
				if (mShowLocation == 0) {
					try {
						json_data.put("showlocation", 1);
						friendsToken.updateJson(position, json_data);
						Token token = new Token();
						String mShow = token.createUpdateFriendStatusToken(
								mMemberid, json_data.getString("memberid"),
								"SHO", json_data.getString("email"),
								json_data.getString("firstname"),
								json_data.getString("lastname"));
						MainActivity.sendMessage(mShow);
						mFriendsTokenid = token.getID();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					try {
						json_data.put("showlocation", 0);
						friendsToken.updateJson(position, json_data);
						Token token = new Token();
						String mShow = token.createUpdateFriendStatusToken(
								mMemberid, json_data.getString("memberid"),
								"SHO", json_data.getString("email"),
								json_data.getString("firstname"),
								json_data.getString("lastname"));
						MainActivity.sendMessage(mShow);
						mFriendsTokenid = token.getID();

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						// e.printStackTrace();
					}
				}

				break;

			case 2: // Awaiting Acceptance
				mExecute = false;
				break;
			case 1: // Accept Friend
				try {
					json_data.put("accepted", 1);
					json_data.put("sortorder", 3);
					friendsToken.updateJson(position, json_data);
					Token token = new Token();
					String mAccept = token.createUpdateFriendStatusToken(
							mMemberid, json_data.getString("memberid"), "ACC",
							json_data.getString("email"),
							json_data.getString("firstname"),
							json_data.getString("lastname"));
					MainActivity.sendMessage(mAccept);
					mFriendsTokenid = token.getID();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}

				break;

			case 0: // Invite Friend
				try {

					json_data.put("invited", 1);
					json_data.put("sortorder", 2);
					friendsToken.updateJson(position, json_data);
					Token token = new Token();
					String mInvited = token.createUpdateFriendStatusToken(
							mMemberid, json_data.getString("memberid"), "INV",
							json_data.getString("email"),
							json_data.getString("firstname"),
							json_data.getString("lastname"));
					MainActivity.sendMessage(mInvited);
					mFriendsTokenid = token.getID();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}

				break;

			}
			if (mExecute) {
				new PopulateFriends().execute();
			}
		}

	}

	public void ClickHandler(View v) {

		switch (v.getId()) {
		// case R.id.backbutton:
		// finish();
		// break;
		}

	}

	@Override
	public void onDestroy() {
		try {
			this.finish();
		} catch (Throwable e) {
		}
		super.onDestroy();
	}

}

/*
 * From onCreate String mContactlist=bundle.getString("mycontacts");
 * 
 * 
 * if (mContactlist!=null){ try {
 * //mContactlist=mContactlist.replaceAll("[^\\w\\[\\]\\(\\)]", "");
 * //mContactlist=mContactlist.replaceAll(" ", "");
 * //mContactlist=mContactlist.replaceAll (" ", "\\ ");
 * mContactlist=mContactlist.replaceAll("^n","");
 * 
 * JSONArray mycontactlist = new JSONArray(mContactlist);
 * 
 * 
 * for(int i=0;i<mycontactlist.length();i++){ //HashMap<String, String> map =
 * new HashMap<String, String>(); //JSONObject temp =
 * mycontactlist.getJSONObject(i); //map.putAll((Map<? extends String, ? extends
 * String>) temp); //contactlist.add(map); } } catch (JSONException e) { // TODO
 * Auto-generated catch block e.printStackTrace(); } }
 */
/*
 * private ArrayList<HashMap<String, String>> getContacts(int index) { String
 * fullname; Cursor additionalinfo = null; //ArrayList<HashMap<String, String>>
 * contactlist = new ArrayList<HashMap<String, String>>();
 * 
 * ContentResolver cr = getContentResolver(); String sortOrder =
 * ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC"; Cursor cur
 * = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null,
 * sortOrder); //int index = 0; index ++; if (cur.getCount() > 0) {
 * 
 * while (cur.moveToNext()) { String id = cur.getString(cur
 * .getColumnIndex(ContactsContract.Contacts._ID)); fullname = cur
 * .getString(cur .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)); //
 * emailNames[index] = name; HashMap<String, String> mContactlist = new
 * HashMap<String, String>(); mContactlist.put("fullname", fullname);
 * mContactlist.put("sortnumber", "0"); mContactlist.put("positionid",
 * Integer.toString(index)); mContactlist.put("showlocation","0");
 * 
 * // String myFriend=mListMemberids.contains(m)?"Y":"N";
 * 
 * mContactlist.put("loggedin", "N");
 * 
 * additionalinfo = getContentResolver().query(
 * ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
 * ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + id, null, null);
 * // mContactlist.put("email", "No Email Address"); //
 * mContactlist.put("phone", "No Phone"); while (additionalinfo.moveToNext()) {
 * // This would allow you get several email addresses String email =
 * additionalinfo .getString(additionalinfo
 * .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
 * 
 * mContactlist.put("email", email); } if (mContactlist.get("email") != null) {
 * contactlist.add(mContactlist); index++; } } additionalinfo.close();
 * 
 * } cur.close(); return contactlist; }
 */
