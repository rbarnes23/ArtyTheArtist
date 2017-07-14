package com.artytheartist;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tokenlibrary.Token;

public class PlayListHelper extends Token{

	public List <String> getPlayLists(String imageList){
		
		List<String> list = new ArrayList<String>();
		if (imageList != null) {

			createToken(imageList);
			if (mToken != null) {
				JSONArray jArrayImages = null;

					try {
						jArrayImages = mToken.getJSONArray("IMAGES");
					for(int i=0;i<jArrayImages.length();i++){
						list.add(jArrayImages.getJSONObject(i).names().toString().replace("[\"", "").replace("\"]", ""));
					}
					} catch (JSONException e) {
						mMessage=e.getMessage();				}
					}}


		return list;
	}
		
	// Create a New Image List
	public List <String> removeImageList(String name,String currentList) {
		JSONObject list = new JSONObject(); 
		JSONArray jsonArray = null;
		if (currentList != null) {
			createToken(currentList);			
		} 

		try {
			jsonArray =mToken.getJSONArray("IMAGES");
			list.put("_id", "" + (System.currentTimeMillis() / 1000L));
			JSONArray jArray = new JSONArray("[]");
			list.put("IMAGES", jArray);

			int index = findIndexInJsonArray(name,
					jsonArray);
			if (index<0){return getPlayLists(currentList);}
			if (jsonArray != null) {
				int len = jsonArray.length();

			   for (int i=0;i<len;i++)
			   { 
			       //Excluding the item at position
			        if (i != index) 
			        {
			            list.getJSONArray("IMAGES").put(jsonArray.get(i));
			        }
			   } 

			}			
		} catch (JSONException e) {
			mMessage=e.getMessage();
		}

		AppSettings.setSelectedImages(list.toString());
		return getPlayLists(list.toString());
	}
	// Get an Image List
	public String[] getImageList(String name,String imageList) {
		String[] imageStrings = null;
		int index = -1;
		if (imageList != null) {

			createToken(imageList);
			if (mToken != null) {
				JSONArray jArrayImages = null;

				try {
					jArrayImages = mToken.getJSONArray("IMAGES");
					index = findIndexInJsonArray(name, jArrayImages);//Find index of image name passed i.e "My Photos"
					
					if (index < 0)  //if your name is not found then we dont have any images by this name
						return null;
					jArrayImages = jArrayImages.getJSONObject(index)
							.optJSONArray(name);  //Get the images by this name
					
					if (jArrayImages != null) {
						imageStrings = new String[jArrayImages.length()];
						for (int i = 0; i < jArrayImages.length(); i++) {
							imageStrings[i] = jArrayImages.getString(i);
						}
					}
				} catch (JSONException e) {
					mMessage=e.getMessage();
				}
			}
		}
		return imageStrings;
	}

	// Create a New Image List
	public List <String> setImageList(String name, String[] images,String currentList) {

		if (currentList != null) {
			createToken(currentList);
		} else {
			mToken = new JSONObject();
			setToken("_id", "" + (System.currentTimeMillis() / 1000L));
			setTokenArray("IMAGES", "[]");
		}
		// Create new image list
		String imageList = "{\"" + name + "\":[";
		for (int i = 0; i < images.length; i++) {
			imageList += "\"" + images[i] + "\",";
		}
		imageList = imageList.substring(0, imageList.lastIndexOf(",")) + "]}";

		try {

			int index = findIndexInJsonArray(name,
					mToken.getJSONArray("IMAGES"));
			JSONObject image = new JSONObject(imageList);
			if (index < 0) {
				mToken.getJSONArray("IMAGES").put(image);
			} else {
				mToken.getJSONArray("IMAGES").put(index, image);
			}
		} catch (JSONException e) {
			mMessage=e.getMessage();
		}

		AppSettings.setSelectedImages(mToken.toString());
		return getPlayLists(mToken.toString());

	}




}
