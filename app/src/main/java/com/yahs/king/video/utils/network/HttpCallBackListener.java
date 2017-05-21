package com.yahs.king.video.utils.network;

/**
 * Created by king on 2017/4/10.
 */
public interface HttpCallBackListener {
    void onFinish(String response);
    void onError(Exception e);
}
