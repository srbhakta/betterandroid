package com.betterandroid.livefolder;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.LiveFolders;
import android.provider.Contacts.People;


public class ContactsProvider extends ContentProvider {

    public static final String AUTHORITY = "com.betterandroid.openhome.livefolder.contacts";
   
    /**
     * The content:// style URL for this table
     */
    public static final Uri CONTACTS_URI = Uri.parse("content://" +
            AUTHORITY + "/contacts"   );
    public static final Uri FAVORITES_URI = Uri.parse("content://" +
        AUTHORITY + "/contacts/favorites"   );    

    private static final int TYPE_ALL = 0;
    private static final int TYPE_FAVORITE = 1;
    
    private static final UriMatcher URI_MATCHER;
    static{
      URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
      URI_MATCHER.addURI(AUTHORITY, "contacts", TYPE_ALL);
      URI_MATCHER.addURI(AUTHORITY, "contacts/favorites", TYPE_FAVORITE);
    }
    
    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public int bulkInsert(Uri arg0, ContentValues[] values) {
      return 0; //nothing to insert
    }

    
    private static final String[] CURSOR_COLUMNS = new String[]{
      BaseColumns._ID, LiveFolders.NAME, LiveFolders.DESCRIPTION, LiveFolders.INTENT, LiveFolders.ICON_PACKAGE, LiveFolders.ICON_RESOURCE
    };
    private static final String[] CURSOR_ERROR_COLUMNS = new String[]{
      BaseColumns._ID, LiveFolders.NAME, LiveFolders.DESCRIPTION};
    private static final Object[] ERROR_MESSAGE = 
      new Object[]{-1, "No contacts found", "Check your contacts database"};
    private static MatrixCursor ERROR = new MatrixCursor(CURSOR_ERROR_COLUMNS);
    static {
      ERROR.addRow(ERROR_MESSAGE);
    }

    private static final String[] CONTACTS_COLUMN = new String[]{
      People._ID, People.DISPLAY_NAME, People.TIMES_CONTACTED, People.STARRED
    };
    
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
      
      int type = URI_MATCHER.match(uri);
      
      if(type == UriMatcher.NO_MATCH){
        return ERROR;
      }
      
      MatrixCursor mc = new MatrixCursor(CURSOR_COLUMNS);
      Cursor allContacts = null;
      
      try {
        String filter = type == TYPE_FAVORITE ? People.STARRED +" = 1" : null;
        allContacts = getContext().getContentResolver().query(People.CONTENT_URI, CONTACTS_COLUMN, filter, null, People.DISPLAY_NAME);
        while(allContacts.moveToNext()){
          String contactedDate = "Time contacted: "+allContacts.getInt(2);
          boolean isStarred = allContacts.getInt(3) > 0;
          mc.addRow(new Object[]{allContacts.getLong(0), allContacts.getString(1), contactedDate, 
              Uri.parse("content://contacts/people/"+allContacts.getLong(0)),
              isStarred ? getContext().getPackageName() : null,
              isStarred ? R.drawable.star_on : null
              });
        }
        return mc;
      } catch (Exception e) {
        return ERROR;
      }finally{
        allContacts.close();
      }
    }


    
    @Override
    public String getType(Uri uri) {
      return getContext().getPackageName(); //not very meaningful
    }

    public Uri insert(Uri uri, ContentValues initialValues) {
      throw new UnsupportedOperationException("no insert");
    }

    
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

}