package com.betterandroid.livefolder;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.LiveFolders;
public class ContactsLiveFolders {

    public static class FavoriteContacts extends Activity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            final Intent intent = getIntent();
            final String action = intent.getAction();
            
            if (LiveFolders.ACTION_CREATE_LIVE_FOLDER.equals(action)) {
              
                setResult(RESULT_OK, createLiveFolder(this, ContactsProvider.FAVORITES_URI,
                        getString(R.string.liveFolder_favorites_label),
                        R.drawable.ic_launcher_contacts_starred));
            } else {
                setResult(RESULT_CANCELED);
            }

            finish();
        }
    }
    
    public static class AllContacts extends Activity {

      @Override
      protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);

          final Intent intent = getIntent();
          final String action = intent.getAction();
          
          if (LiveFolders.ACTION_CREATE_LIVE_FOLDER.equals(action)) {
            
              setResult(RESULT_OK, createLiveFolder(this, ContactsProvider.CONTACTS_URI,
                      getString(R.string.liveFolder_all_label),
                      R.drawable.ic_launcher_contacts));
          } else {
              setResult(RESULT_CANCELED);
          }

          finish();
      }
  }

    private static Intent createLiveFolder(Context context, Uri uri, String name,
            int icon) {

        final Intent intent = new Intent();

        intent.setData(uri);
        intent.putExtra(LiveFolders.EXTRA_LIVE_FOLDER_NAME, name);
        intent.putExtra(LiveFolders.EXTRA_LIVE_FOLDER_ICON,
                Intent.ShortcutIconResource.fromContext(context, icon));
        intent.putExtra(LiveFolders.EXTRA_LIVE_FOLDER_DISPLAY_MODE, LiveFolders.DISPLAY_MODE_LIST);

        return intent;
    }
}
