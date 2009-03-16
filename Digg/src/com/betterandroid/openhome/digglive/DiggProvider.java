package com.betterandroid.openhome.digglive;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.LiveFolders;
import android.util.Log;


public class DiggProvider extends ContentProvider {
    private static final String LOG_TAG = "OpenHomeDigg";

    private static final String DATABASE_NAME = "openhomedigg.db";
    private static final int DATABASE_VERSION = 1;
    
    public static final String AUTHORITY = "com.betterandroid.openhome.livefolder.digg";
    public static final String TABLE_NAME = "digg";
   
    /**
     * The content:// style URL for this table
     */
    public static final Uri CONTENT_URI = Uri.parse("content://" +
            AUTHORITY + "/" + TABLE_NAME );
    
    private SQLiteOpenHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public int bulkInsert(Uri arg0, ContentValues[] values) {
      SQLiteDatabase db = mOpenHelper.getWritableDatabase();
      db.beginTransaction();
      int count = 0;
      try {
        db.execSQL("delete from "+TABLE_NAME); //delete old stories
          int numValues = values.length;
          for (int i = 0; i < numValues; i++) {
            count++;
              if (db.insert("digg", null, values[i]) < 0) return 0;
          }
          db.setTransactionSuccessful();
      } finally {
          db.endTransaction();
      }
      return count;
    }

    
    private static final String[] CURSOR_COLUMNS = new String[]{
      BaseColumns._ID, LiveFolders.NAME, LiveFolders.DESCRIPTION, LiveFolders.INTENT
    };
    private static final String[] CURSOR_ERROR_COLUMNS = new String[]{BaseColumns._ID, LiveFolders.NAME, LiveFolders.DESCRIPTION};
    private static final Object[] ERROR_MESSAGE = 
      new Object[]{-1, "No stories yet, try again later", "Close folder and reopen it."};
    private static MatrixCursor ERROR = new MatrixCursor(CURSOR_ERROR_COLUMNS);
    static {
      ERROR.addRow(ERROR_MESSAGE);
    }

    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
      getContext().startService(new Intent(getContext(), DiggLiveFolderService.class));
      try {
        //select everything, order is not so much relevant since the digg home page is not ordered in much a meaningful way anyway
        Cursor c = mOpenHelper.getReadableDatabase().query(TABLE_NAME, CURSOR_COLUMNS, null, null, null, null, null);
        if(c.getCount() <= 0){
          return ERROR;
        }else{
          return c;
        }
      } catch (Exception e) {
        return ERROR;
      }
    }


    
    @Override
    public String getType(Uri uri) {
      return getContext().getPackageName();
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

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE "+TABLE_NAME+" ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY, "
                    + LiveFolders.NAME + " TEXT, "
                    + LiveFolders.DESCRIPTION + " TEXT, "
                    + LiveFolders.INTENT + " TEXT);");
        }
        
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to " +
                    newVersion + ", which will destroy all old data");

            db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
            onCreate(db);
        }
    }
}