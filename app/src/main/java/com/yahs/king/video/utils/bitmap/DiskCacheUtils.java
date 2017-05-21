package com.yahs.king.video.utils.bitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


/**-
 * Created by king on 2017/4/27.
 */

public class DiskCacheUtils {

    private static final String LOCAL_PATH= Environment.getExternalStorageDirectory().getAbsolutePath()+"/bitmapCache";

    /*
    * 存到本地
    * */
    public void putBitmapToFile(String url, Bitmap bitmap) throws Exception {
        String fileName = MD5Encoder.encode(url);
        File file = new File(LOCAL_PATH,fileName);
        File parentFile = file.getParentFile();
        if (!parentFile.exists()){
            parentFile.mkdir();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,new FileOutputStream(file));

    }

    /*
    * 读取图片
    *
    * */
    public Bitmap getBitmapFromFile(String url) throws Exception {
        String fileName = MD5Encoder.encode(url);
        File file = new File(LOCAL_PATH,fileName);

        if (file.exists()){
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));

            return bitmap;
        }else {
            return null;
        }

    }
}
