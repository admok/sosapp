package com.emergency.EasySOS;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.SimpleCursorAdapter;

/**
 * @author: adrianm
 * Created Date:
 * Description:
 * Changes:
 */

public class ContactList extends ListActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts);

        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[] {ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);

        startManagingCursor(cursor);

        String[] from = new String[] { ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};

        int[] to = new int[] { R.id.name_entry, R.id.number_entry};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.contacts, cursor, from, to);
        this.setListAdapter(adapter);
    }
}
