package com.rabita.shabab.aqsa2;

import android.util.Log;
import android.widget.Toast;

/**
 * Created by macbookair on 8/4/15.
 */
public class utility  {
    public static String TAG="AqsaProject";

    public static String getHeader(String content)
    {
        String[] lines = content.split(System.getProperty("line.separator"));
        return lines[0];
    }
    public  static  String getContent(String content)
    {
        String[] lines = content.split(System.getProperty("line.separator"));
        String onlyContent = content.replace(lines[0],"");
        onlyContent = content.replace(lines[0]+"\n","");
        return onlyContent;
    }
}
