package com.yahs.king.video.utils;

import android.content.Context;

/**
 * Created by king on 2017/4/10.
 */

public class CacheUtils {

    public static void setCache(Context context,String url, String json){
        PrefUtils.setString(context,url,json);
    }

    public static String getCache(String url,Context context){
        return PrefUtils.getString(context,url,null);
    }
}
