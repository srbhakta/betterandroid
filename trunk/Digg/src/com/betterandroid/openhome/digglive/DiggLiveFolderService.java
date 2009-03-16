package com.betterandroid.openhome.digglive;

import static com.betterandroid.openhome.digglive.XmlUtils.isStartTag;
import static com.betterandroid.openhome.digglive.XmlUtils.notEndTag;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.provider.LiveFolders;
import android.util.Log;
import android.widget.Toast;

public class DiggLiveFolderService extends Service{
    private static final String ACTION_QUERY_DIGG = "com.betterandroid.openhome.livefolder.digg";

    public class LocalBinder extends Binder {
        DiggLiveFolderService getService() {
            return DiggLiveFolderService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();
    
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if(ACTION_QUERY_DIGG.equals(intent.getAction())){
          try {
            ContentResolver cr = getContentResolver();
            Log.d(getPackageName(), "Querying digg stories");
            //query digg front page
            ContentValues[] stories = getPopularStoriesContents();
            if(stories != null){
              cr.bulkInsert(DiggProvider.CONTENT_URI, stories);
            }                
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
    }
    private static final String DIGG_POPULAR_URL = "http://services.digg.com/stories/popular?count=20&appkey=http://betterandroid.wordpress.com";
    private HttpAgent httpAgent = new HttpAgent();
    
    private ContentValues[] getPopularStoriesContents(){
      InputStream in = null;
      try {
        in = httpAgent.getUrl(DIGG_POPULAR_URL);
        XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
        parser.setInput(new InputStreamReader(in));
        return parseDiggResponse(parser);
      } catch (Exception e) {
        e.printStackTrace();
      }finally{
        if(in != null){
          try {in.close();} catch (IOException e) {}
        }
      }
      return null;      
    }
    
    private ContentValues[] parseDiggResponse(XmlPullParser parser) throws XmlPullParserException, IOException{
      ContentValues[] ret = new ContentValues[20];
      
      int eventType = parser.getEventType();
      int idx = 0;
      String time = new SimpleDateFormat().format(new Date(System.currentTimeMillis()));
      while(notEndTag("stories", eventType, parser) && idx < 20){
          if(isStartTag("story", eventType, parser)){
              ContentValues story = new ContentValues();
              story.put(LiveFolders.INTENT, parser.getAttributeValue(null, "link"));
              story.put(LiveFolders.DESCRIPTION, "("+parser.getAttributeValue(null, "diggs")+" diggs) "+ time);
              while(notEndTag("title", eventType, parser)){
                eventType = parser.next();
                if(isStartTag("title", eventType, parser)){
                  story.put(LiveFolders.NAME, parser.nextText());
                  break;
                }
              }
              ret[idx] = story;
              idx++;
          }
          eventType = parser.next();
      }
      
      return ret;
    }
    
    
    private static final int REQUEST_FROM_ALARM = 100;
    private AlarmManager am;
    private PendingIntent pi;

    @Override
    public void onCreate() {
      Toast.makeText(this, "Digg Live Folder background service started.", Toast.LENGTH_SHORT).show();
        am = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent i = new Intent(getApplicationContext(), DiggLiveFolderService.class);
        i.setAction(ACTION_QUERY_DIGG);
        pi = PendingIntent.getService(getApplicationContext(), REQUEST_FROM_ALARM, i, 
            0 );
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+5, 10*60*1000, pi);      
    }
    
    @Override
    public void onDestroy() {
      
      Toast.makeText(this, "Digg Live Folder background service stopped.", Toast.LENGTH_SHORT).show();
      if(pi != null){
        am.cancel(pi);
      }
      
    }

}