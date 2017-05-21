package com.yahs.king.video.utils.bitmap;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetCacheUtils {

    private MemoryCacheUtils mMemoryCacheUtils;
    private DiskCacheUtils mDiskCacheUtils;

    public NetCacheUtils(MemoryCacheUtils memoryCacheUtils,
             DiskCacheUtils diskCacheUtils){
        mMemoryCacheUtils=memoryCacheUtils;
        mDiskCacheUtils=diskCacheUtils;
    }

    public void getBitmapFromNet(ImageView imageView,String url){
        BitmapTask bitmapTask= new BitmapTask();
        bitmapTask.execute(new Object[]{imageView,url});

    }

    private class BitmapTask extends AsyncTask<Object,Void,Bitmap>{


        private ImageView imageView;
        private String tagUrl;

        /*
         * 返回的bitmap会回传到onPostExecute
         */
        @Override
        protected Bitmap doInBackground(Object... objects) {
            imageView = (ImageView) objects[0];
            tagUrl = (String) objects[1];
            Bitmap bitmap = downloadBitmap(tagUrl);

            return bitmap;
        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            //这里的bitmap就是doInBackground里面返回的bitmap
            imageView.setTag(tagUrl);
            String url = (String) imageView.getTag();
            if (url.equals(tagUrl)){
                /*
                *确保listView设置的是正确是图片(因为listView有时候会有重用机制，多个item会共用一个imageView
                * 从而导致图片错乱)
                * */
                imageView.setImageBitmap(bitmap);
                //从内存保存图片
                mMemoryCacheUtils.putCacheToMemory(tagUrl,bitmap);
                //从本地磁盘保存图片
                try {
                    mDiskCacheUtils.putBitmapToFile(tagUrl,bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


        }


        private Bitmap downloadBitmap(String url) {
            HttpURLConnection conn= null;
            try{
                conn= (HttpURLConnection) new URL(url).openConnection();
                //设置超时时间
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                //设置请求方式
                conn.setRequestMethod("GET");
                int code = conn.getResponseCode();
                if (code==200){
                    InputStream inputStream = conn.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    return bitmap;
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                conn.disconnect();
            }

            return null;
        }
    }
}
