package com.betterandroid.openhome.digglive;

import org.xmlpull.v1.XmlPullParser;

public class XmlUtils{
    private XmlUtils(){}
    public static boolean notEndTag(String tagname, int eventType, XmlPullParser pullParser){
        return !(eventType == XmlPullParser.END_TAG && tagname.equalsIgnoreCase(pullParser.getName())) && eventType != XmlPullParser.END_DOCUMENT;
    }
    
    public static boolean isStartTag(String tagname, int eventType, XmlPullParser pullParser){
        return eventType == XmlPullParser.START_TAG && tagname.equalsIgnoreCase(pullParser.getName());
    }
}