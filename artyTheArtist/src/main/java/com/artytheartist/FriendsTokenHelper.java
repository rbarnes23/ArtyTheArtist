package com.artytheartist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tokenlibrary.Token;

public class FriendsTokenHelper extends Token{

	public ArrayList<HashMap<String, String>> getResponse(String aType) {
		ArrayList<HashMap<String, String>> jsonlist = new ArrayList<HashMap<String, String>>();
		// ArrayList<GeoPoint> path = new ArrayList<GeoPoint>();
		List<Integer> mListMemberids = MainActivity.getMemberids();

		int li_sort = -1;
		try {
			mMessage = mToken.getString("response");
			JSONArray jArray = new JSONArray(mMessage);
			if (aType.contentEquals("friends")) {
				jArray = sortArray(jArray);
				jArrayFriends = jArray;
			} else if (aType.contentEquals("login")) {
				jArrayLogin = jArray;

			}
			int iarraylength = jArray.length();

			for (int icount = 0; icount < iarraylength; icount++) {
				JSONObject json_data;
				HashMap<String, String> temp = new HashMap<String, String>();
				try {
					json_data = jArray.getJSONObject(icount);

					if (aType.contentEquals("login")) {
						temp.put("memberid", json_data.getString("memberid"));
						temp.put("fullname", json_data.getString("fullname"));

					} else if (aType.contentEquals("friends")) {
						int li_sortorder = json_data.getInt("sortorder");
						if (li_sortorder != li_sort) {
							HashMap<String, String> temphead = new HashMap<String, String>();
							String is_heading = null;
							li_sort = li_sortorder;
							switch (li_sortorder) {

							case 0:
								// headingView.setText(R.string.invite);

								break;
							case 1:
								// headingView.setText(R.string.acceptfriend);
								// is_heading = "Accept Friends";
								break;
							case 2:
								// headingView.setText(R.string.waitforacceptance);
								// is_heading = "Awaiting Acceptance";
								break;
							case 3:
								// headingView.setText(R.string.activefriends);
								// is_heading = "Active Friends";
								break;
							}
							// is_heading = (String) headingView.getText();
							temphead.put("fullname", is_heading);
							temphead.put("sortnumber",
									Integer.toString(li_sort));
							temphead.put("positionid", "-1");
							temphead.put("showlocation", "-1");
							jsonlist.add(temphead);
						}
						// temp.put("firstname",
						// json_data.getString("firstname"));
						// temp.put("lastname",json_data.getString("lastname"));
						// temp.put("accepted",json_data.getString("accepted"));
						temp.put("fullname", json_data.getString("firstname")
								+ " " + json_data.getString("lastname"));
						temp.put("sortnumber", json_data.getString("sortorder"));
						temp.put("positionid", Integer.toString(icount));
						temp.put("showlocation",
								json_data.getString("showlocation"));
						int m = json_data.getInt("memberid");

						String myFriend = mListMemberids.contains(m) ? "Y"
								: "N";

						temp.put("loggedin", myFriend);
						jsonlist.add(temp);

					}
				} catch (JSONException e) {
					mMessage=e.getMessage();
				}
			}

		} catch (JSONException e) {
			mMessage = e.getMessage();
		}
			return jsonlist;
	}

	private JSONArray sortArray(JSONArray jArray) {
		int iarraylength = jArray.length();
		int jloop = 3;
		JSONArray jSort = new JSONArray();

		for (jloop = 3; jloop >= 0; jloop--) {
			for (int icount = 0; icount < iarraylength; icount++) {
				JSONObject json_data = null;

				try {
					json_data = jArray.getJSONObject(icount);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int sortno = 0;
				try {
					sortno = json_data.getInt("sortorder");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (jloop == 3 && sortno == 3) {
					jSort.put(json_data);
				}
				if (jloop == 2 && sortno == 2) {
					jSort.put(json_data);
				}
				if (jloop == 1 && sortno == 1) {
					jSort.put(json_data);
				}
				if (jloop == 0 && sortno == 0) {
					jSort.put(json_data);
				}

			}

		}

		return jSort;
	}

}
