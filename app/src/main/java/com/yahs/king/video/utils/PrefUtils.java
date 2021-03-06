package com.yahs.king.video.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by king on 2017/4/8.
 */

public class PrefUtils {
    public static boolean getBoolean(Context context, String key,boolean defValue ){
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        return sp.getBoolean(key,defValue);
    }

    public static void setBoolean(Context context, String key,boolean Value ){
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        sp.edit().putBoolean(key,Value).commit();
    }


    public static void setString(Context context, String key,String  Value ){
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        sp.edit().putString(key,Value).commit();
    }

    public static String getString(Context context, String key,String  Value ) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        return sp.getString(key, Value);
    }


        public static void setInt(Context context, String key,int  Value ){
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        sp.edit().putInt(key,Value).commit();
    }

    public static int getInt(Context context, String key,int  Value ){
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        return sp.getInt(key,Value);


    }
}
