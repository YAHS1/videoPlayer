package com.yahs.king.video.global;

import android.graphics.Canvas;


public class Constants {

    /**
     * 记录播放位置
     */
    public static int playPosition=-1;
    
    private static  Canvas canvas;

    public static Canvas getCanvas() {
        return canvas;
    }

    public static void setCanvas(Canvas canvas) {
        Constants.canvas = canvas;
    }
    
    
}
