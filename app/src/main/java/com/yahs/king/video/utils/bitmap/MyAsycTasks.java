package com.yahs.king.video.utils.bitmap;

import android.os.Handler;
import android.os.Message;

/**
 * Created by king on 2017/3/9.
 */

public abstract class MyAsycTasks {
    private Handler handler  =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            postTask();
        }
    };
    /*
    *在子线程之前执行的方法
     */

    public abstract void preTask();
    /*
    * 在子线程之中执行的方法
    * */
    public abstract void doinTask();
    /*
    * 在子线程之后执行的方法
    * */
    public abstract void postTask();


    /*
    * 执行
    * */
    public void execute(){
        preTask();
        new Thread(){
            @Override
            public void run() {
                doinTask();
                handler.sendEmptyMessage(0);
            }
        }.start();
    }
}
