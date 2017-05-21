package com.yahs.king.video.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Scroller;


/**
 * Created by king on 2017/3/12.
 */

public class CircleImageView extends ImageView {

    private Paint mBorderPaint;
    private Paint mBitmapPaint;
    private Bitmap mBitmap;
    private Scroller scroller;
    private int mLastX;
    private int mLastY;
    private OnViewClickListener listener;
    private View view;





    public CircleImageView(Context context) {
        super(context);
        init();
    }

    private void init() {
        mBorderPaint = new Paint();
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scroller = new Scroller(getContext());


    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        invalidate();
    }



    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        invalidate();
    }

    @Override
    public void setImageIcon(Icon icon) {
        super.setImageIcon(icon);
        invalidate();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        invalidate();
    }


    private Bitmap getBitmap(){
        Drawable drawable = getDrawable();
        if (drawable==null){
            return null;
        }
        if (drawable instanceof BitmapDrawable){
            return ((BitmapDrawable) drawable).getBitmap();
        }

        return null;

    }
    private void setup(){
        mBitmap = getBitmap();
        if (mBitmap==null){
            invalidate();
            return;
        }
        setupShader();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        setup();
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, Math.min(getWidth() / 2, getHeight() / 2), mBitmapPaint);

    }

    private void setupShader() {
        BitmapShader bitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP,Shader.TileMode.CLAMP);
        int minBitmapSize = Math.min(mBitmap.getWidth(), mBitmap.getHeight());
        float minViewSize = Math.min(getHeight(), getWidth());
        float scale = minViewSize / minBitmapSize;
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        bitmapShader.setLocalMatrix(matrix);
        mBitmapPaint.setShader(bitmapShader);

    }
    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()){
            scrollTo(scroller.getCurrX(),scroller.getCurrY());
            invalidate();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastX = (int) event.getRawX();
                mLastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:
                int x = (int) event.getRawX();
                int Y = (int) event.getRawY();
                scroller.startScroll(getScrollX(),getScrollY(),-(x- mLastX),-(Y- mLastY));
                listener.onOneClick(view);
                invalidate();

                break;
        }

        return true;
    }
    public void setOnViewClickListener(OnViewClickListener listener){
        this.listener=listener;
    }
    public interface OnViewClickListener {
        void onOneClick(View view);
    }
}
