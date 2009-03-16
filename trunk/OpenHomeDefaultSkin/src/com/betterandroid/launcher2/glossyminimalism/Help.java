package com.betterandroid.launcher2.glossyminimalism;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Help extends Activity implements OnClickListener{
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.main);
      
      Button b = (Button) findViewById(R.id.more);
      b.setOnClickListener(this);
  }

  @Override
  public void onClick(View arg0) {
    Intent i = new Intent(Intent.ACTION_VIEW);
    i.setData(Uri.parse("http://market.android.com/search?q=pub:\"Better Android\""));
    startActivity(i);
  }
}