package com.artytheartist;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

public class ContactDetail extends ListActivity {
	String[] emailNames;
	String[] emailNumbers;

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Cursor mCursor = getContacts();
		getContacts2();

		startManagingCursor(mCursor);
		// Now create a new list adapter bound to the cursor.
		// SimpleListAdapter is designed for binding to a Cursor.
		ListAdapter adapter = new SimpleCursorAdapter(this, // Context.
				R.layout.list_contacts_details, // Specify the row template
												// to use (here, two
												// columns bound to the
												// two retrieved cursor
												// rows).
				mCursor, // Pass in the cursor to bind to.
				// Array of cursor columns to bind to.
				new String[] { BaseColumns._ID,
						ContactsContract.Contacts.DISPLAY_NAME },
				// Parallel array of which template objects to bind to those
				// columns.
				new int[] { R.id.name_entry, R.id.number_entry });

		// Bind to our new adapter.
		setListAdapter(adapter);
	}

	private Cursor getContacts() {
		// Run query
		Uri uri = ContactsContract.Contacts.CONTENT_URI;

		String[] projection = new String[] { BaseColumns._ID,
				ContactsContract.Contacts.DISPLAY_NAME };

		String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '"
				+ ("1") + "'";
		String[] selectionArgs = null;
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
				+ " COLLATE LOCALIZED ASC";

		return managedQuery(uri, projection, selection, selectionArgs,
				sortOrder);
	}

	private Cursor getContacts2() {
		String name;
		Cursor emails = null;
		ArrayList<HashMap<String, String>> contactlist = new ArrayList<HashMap<String, String>>();
		//HashMap<String,String> mContactlist = new HashMap<String,String>();

		ContentResolver cr = getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				null, null, null);
		int index = 0;

		if (cur.getCount() > 0) {

			emailNames = new String[cur.getCount()];
			emailNumbers = new String[cur.getCount()];

			
			while (cur.moveToNext()) {
				String id = cur.getString(cur
						.getColumnIndex(ContactsContract.Contacts._ID));
				name = cur
						.getString(cur
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				emailNames[index] = name;
				HashMap<String,String> mContactlist = new HashMap<String,String>();
				mContactlist.put("enames", emailNames[index]);
				emails = getContentResolver().query(
						ContactsContract.CommonDataKinds.Email.CONTENT_URI,
						null,
						ContactsContract.CommonDataKinds.Email.CONTACT_ID
								+ " = " + id, null, null);
				mContactlist.put("enumbers", "No Email Address");
				while (emails.moveToNext()) {
					// This would allow you get several email addresses
					String emailAddress = emails
							.getString(emails
									.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
					// Log.v("email==>", emailAddress);

					emailNumbers[index] = emailAddress;
					mContactlist.put("enumbers", emailAddress);
					

				}
				contactlist.add(index,mContactlist);
				index++;
			}
			emails.close();
			

		}
		cur.close();
		return null;
	}
}
/*
 * import android.app.ListActivity; import android.database.Cursor; import
 * android.os.Bundle; import android.provider.Contacts.People; import
 * android.widget.SimpleCursorAdapter;
 * 
 * // starting the class public class ContactDetail extends ListActivity {
 * 
 * 
 * @Override public void onCreate(Bundle savedInstance) {
 * setContentView(R.layout.list_contacts);
 * 
 * // some code
 * 
 * Cursor cursor = getContentResolver().query(People.CONTENT_URI, new String[]
 * {People._ID, People.NAME, People.NUMBER}, null, null, null);
 * startManagingCursor(cursor);
 * 
 * // the desired columns to be bound String[] columns = new String[] {
 * People.NAME, People.NUMBER }; // the XML defined views which the data will be
 * bound to int[] to = new int[] { R.id.name_entry, R.id.number_entry };
 * 
 * // create the adapter using the cursor pointing to the desired data as well
 * as the layout information SimpleCursorAdapter mAdapter = new
 * SimpleCursorAdapter(this, R.layout.list_contacts_details, cursor, columns,
 * to);
 * 
 * // set this adapter as your ListActivity's adapter
 * this.setListAdapter(mAdapter); } }
 */