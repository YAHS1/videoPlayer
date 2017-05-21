package com.yahs.king.video.utils.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.widget.ImageView;

import com.yahs.king.video.bean.VideoBean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by king on 2017/4/27.
 */

public class MyBitmapUtils {
    private NetCacheUtils mNetCacheUtils;
    private DiskCacheUtils mDiskCacheUtils;
    private MemoryCacheUtils mMemoryCacheUtils;
    private MyAsycTasks myAsycTasks;
    private Context context;
    private VideoBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean contentlistBean;

    public MyBitmapUtils(Context context){
        mDiskCacheUtils=new DiskCacheUtils();
        mMemoryCacheUtils=new MemoryCacheUtils();
        this.context=context;
        mNetCacheUtils=new NetCacheUtils(mMemoryCacheUtils,mDiskCacheUtils);
    }
    public void disPlay(ImageView imageView,String url) throws Exception {
        //1.先内存中加载图片，如果没有就
        Bitmap bitmap = mMemoryCacheUtils.getCacheFromMemory(url);
        if (bitmap!=null){
            imageView.setImageBitmap(bitmap);
            return;
        }

        //2.在本地缓存中加载图片，如果没有
        bitmap= mDiskCacheUtils.getBitmapFromFile(url);
        if (bitmap!=null){
            imageView.setImageBitmap(bitmap);
        }
        //3.从网络下载图片

        mNetCacheUtils.getBitmapFromNet(imageView,url);
    }
    public void disPlayOne(final ImageView imageView, String url, final VideoBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean contentlistBean) throws Exception {
        this.contentlistBean=contentlistBean;
        //2.在本地缓存中加载图片，如果没有
        final Bitmap bitmap= mDiskCacheUtils.getBitmapFromFile(url);
        if (bitmap!=null){
            imageView.setImageBitmap(bitmap);
        }else {
            new MyAsycTasks() {
                @Override
                public void preTask() {

                }

                @Override
                public void doinTask() {
                    System.out.println("被执行了这个dointask");
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(contentlistBean.getVideo_uri(),new HashMap<String, String>());
                    Bitmap bitmap = retriever.getFrameAtTime();
                    FileOutputStream fos=null;
                    try {
                        fos=new FileOutputStream(new File(context.getExternalCacheDir().getAbsolutePath() + "/" + ".jpg"));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();

                    }
                    bitmap.compress(Bitmap.CompressFormat.JPEG,10,fos);
                }

                @Override
                public void postTask() {
                    imageView.setImageBitmap(bitmap);
                }
            }.execute();
        }
    }
}
