package com.yahs.king.video.utils.bitmap;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by king on 2017/4/27.
 */

public class MemoryCacheUtils {

    private final LruCache<String, Bitmap> mLruCache;

    public MemoryCacheUtils(){
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        mLruCache = new LruCache<String, Bitmap>((maxMemory) / 8){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes();
            }
        };

    }


    /*
     * 存到内存中
    * */
    public void putCacheToMemory(String url,Bitmap bitmap){
        mLruCache.put(url, bitmap);
    }


    /*
    * 从内存中读取图片
    * */
    public Bitmap getCacheFromMemory(String url){

        Bitmap bitmap = mLruCache.get(url);
        return bitmap;
    }

}
