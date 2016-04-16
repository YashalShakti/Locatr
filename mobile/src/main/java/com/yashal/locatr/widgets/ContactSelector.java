package com.yashal.locatr.widgets;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.yashal.locatr.R;

/**
 * Created by yashal on 16/4/16.
 */
public class ContactSelector extends FrameLayout {
    private final static String[] FROM_COLUMNS = {
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
    };
    private final static int[] TO_IDS = {
            android.R.id.text1
    };
    private static final String[] PROJECTION =
            {
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.LOOKUP_KEY,
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY

            };
    private static final String SELECTION =
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ?";
    private static final int CONTACT_ID_INDEX = 0;
    private static final int LOOKUP_KEY_INDEX = 1;
    ListView mContactsList;
    long mContactId;
    String mContactKey;
    Uri mContactUri;
    private String mSearchString;
    private String[] mSelectionArgs = {mSearchString};
    private Context mContext;
    private SimpleCursorAdapter mCursorAdapter;

    public ContactSelector(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public ContactSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();

    }

    public ContactSelector(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.contact_selector, this);
        mContactsList = (ListView) findViewById(R.id.contacts_list_view);
        mCursorAdapter = new SimpleCursorAdapter(
                mContext,
                R.layout.contact_selector_item,
                null,
                FROM_COLUMNS, TO_IDS,
                0);
        mContactsList.setAdapter(mCursorAdapter);
        mContactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the Cursor
                CursorAdapter cursorAdapter = (CursorAdapter) parent.getAdapter();
                Cursor cursor = cursorAdapter.getCursor();
                // Move to the selected contact
                cursor.moveToPosition(position);
                // Get the _ID value
                mContactId = (CONTACT_ID_INDEX);
                // Get the selected LOOKUP KEY
                mContactKey = String.valueOf(LOOKUP_KEY_INDEX);
                // Create the contact's content Uri
                mContactUri = ContactsContract.Contacts.getLookupUri(mContactId, mContactKey);
            }
        });
    }

    public void query(String searchString){
        mSearchString = searchString;
        mSelectionArgs[0] = "%" + mSearchString + "%";
        // Starts the query
        CursorLoader cursorLoader =  new CursorLoader(
                mContext,
                ContactsContract.Contacts.CONTENT_URI,
                PROJECTION,
                SELECTION,
                mSelectionArgs,
                null
        );

        cursorLoader.registerListener(0, new Loader.OnLoadCompleteListener<Cursor>() {
            @Override
            public void onLoadComplete(Loader<Cursor> loader, Cursor cursor) {
                mCursorAdapter.swapCursor(cursor);
            }
        });

        cursorLoader.registerOnLoadCanceledListener(new Loader.OnLoadCanceledListener<Cursor>() {
            @Override
            public void onLoadCanceled(Loader<Cursor> loader) {
                mCursorAdapter.swapCursor(null);
            }
        });
    }
}