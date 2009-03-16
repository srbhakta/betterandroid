package com.betterandroid.openhome.digglive;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.LiveFolders;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Digg extends Activity implements OnClickListener
{

  static final Uri CONTENT_URI = Uri.parse("content://" +
      DiggProvider.AUTHORITY + "/" + DiggProvider.TABLE_NAME);
  private Button start,stop;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    final Intent intent = getIntent();
    final String action = intent.getAction();

    if (LiveFolders.ACTION_CREATE_LIVE_FOLDER.equals(action)) {
      
        setResult(RESULT_OK, createLiveFolder(this, CONTENT_URI,
                "Digg Top Stories",
                R.drawable.icon));
        finish();
        return;
    }
    setContentView(R.layout.main);
    
    start = (Button) findViewById(R.id.start);
    stop = (Button) findViewById(R.id.stop);
    start.setOnClickListener(this);
    stop.setOnClickListener(this);

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

  @Override
  public void onClick(View v) {
    if(v == start){
      startService(new Intent(this, DiggLiveFolderService.class));
    }else if(v == stop){
      stopService(new Intent(this, DiggLiveFolderService.class));
    }
  }
}
