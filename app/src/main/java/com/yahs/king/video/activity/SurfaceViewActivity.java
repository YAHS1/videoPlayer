package com.yahs.king.video.activity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import com.yahs.king.video.R;
import com.yahs.king.video.global.Constants;
import android.content.res.Configuration;

public class SurfaceViewActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener,
        View.OnClickListener {
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private SeekBar seekBar;
    private Button playButton;
    private Button videoSizeButton;
    private ProgressBar progressBar;
    private MediaPlayer mediaPlayer;
    private int playPosition = -1;
    private boolean seekBarAutoFlag = false;
    private TextView vedioTiemTextView;
    private String videoTimeString;
    private long videoTimeLong;
    private int screenWidth, screenHeight;
    private String video_url;
    private Button replayButton;
    private DownloadManager manager;
    private DownloadManager.Request request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface_view);
        // 获取屏幕的宽度和高度
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        Intent intent = getIntent();
        video_url = intent.getStringExtra("video_url");
        manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        initViews();
    }


    public void initViews() {

        // 初始化控件
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        playButton = (Button) findViewById(R.id.button_play);
        vedioTiemTextView = (TextView) findViewById(R.id.textView_showTime);
        videoSizeButton = (Button) findViewById(R.id.button_videoSize);
        replayButton= (Button) findViewById(R.id.button_replay);



        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceCallback());

    }

    // SurfaceView的callBack
    private class SurfaceCallback implements SurfaceHolder.Callback {
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        public void surfaceCreated(SurfaceHolder holder) {
            // 设置播放资源
            playVideo();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            if (null != mediaPlayer) {
                mediaPlayer.release();
                mediaPlayer = null;
            }

        }

    }

    /**
     * 播放视频
     */
    public void playVideo() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.reset();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        final Uri uri = Uri.parse(video_url);
        try {

            mediaPlayer.setDataSource(SurfaceViewActivity.this, uri);
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "加载视频错误！", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 视频加载完毕监听
     *
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        progressBar.setVisibility(View.GONE);
        if (Constants.playPosition >= 0) {
            mediaPlayer.seekTo(Constants.playPosition);
            Constants.playPosition = -1;
        }

        seekBarAutoFlag = true;
        seekBar.setMax(mediaPlayer.getDuration());
        videoTimeLong = mediaPlayer.getDuration();
        videoTimeString = getShowTime(videoTimeLong);
        vedioTiemTextView.setText("00:00:00/" + videoTimeString);
        seekBar.setOnSeekBarChangeListener(new SeekBarChangeListener());
        playButton.setOnClickListener(SurfaceViewActivity.this);
        replayButton.setOnClickListener(SurfaceViewActivity.this);
        videoSizeButton.setOnClickListener(SurfaceViewActivity.this);
        mediaPlayer.start();
        mediaPlayer.setDisplay(surfaceHolder);
        // 开启线程 刷新进度条
        new Thread(runnable).start();
        // 设置surfaceView保持在屏幕上
        mediaPlayer.setScreenOnWhilePlaying(true);
        surfaceHolder.setKeepScreenOn(true);

    }

    /**
     * 滑动条变化线程
     */
    private Runnable runnable = new Runnable() {

        public void run() {
            try {
                while (seekBarAutoFlag) {

                    if (null != SurfaceViewActivity.this.mediaPlayer
                            && SurfaceViewActivity.this.mediaPlayer.isPlaying()) {
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * seekBar拖动监听类
     *
     */
    private class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (progress >= 0) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
                // 设置当前播放时间
                vedioTiemTextView.setText(getShowTime(progress) + "/" + videoTimeString);
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        public void onStopTrackingTouch(SeekBar seekBar) {

        }

    }

    /**
     * 按钮点击事件监听
     */
    public void onClick(View v) {
        //重新播放
        if (v == replayButton) {
            // mediaPlayer不空，则直接跳转
            if (null != mediaPlayer) {
                // MediaPlayer和进度条都跳转到开始位置
                mediaPlayer.seekTo(0);
                seekBar.setProgress(0);

                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            } else {
                // 为空则重新设置mediaPlayer
                playVideo();
            }

        }
        if (v == playButton) {
            if (null != mediaPlayer) {
                if (mediaPlayer.isPlaying()) {
                    Constants.playPosition = mediaPlayer.getCurrentPosition();
                    mediaPlayer.pause();
                    playButton.setText("播放");
                } else {
                    if (Constants.playPosition >= 0) {
                        mediaPlayer.seekTo(Constants.playPosition);
                        mediaPlayer.start();
                        playButton.setText("暂停");
                        Constants.playPosition = -1;
                    }
                }

            }
        }
        //改变大小
        if (v == videoSizeButton) {
            changeVideoSize();
        }

    }

    /**
     * 播放完毕监听
     *
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        // 设置seeKbar跳转到最后位置
        seekBar.setProgress(Integer.parseInt(String.valueOf(videoTimeLong)));
        // 设置播放标记为false
        seekBarAutoFlag = false;
    }

    /**
     * 视频缓存大小监听
     */
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.e("text", "onBufferingUpdate-->" + percent);


    }

    /**
     * 错误监听
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Toast.makeText(this, "MEDIA_ERROR_UNKNOWN", Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Toast.makeText(this, "MEDIA_ERROR_SERVER_DIED", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

        switch (extra) {
            case MediaPlayer.MEDIA_ERROR_IO:
                Toast.makeText(this, "MEDIA_ERROR_IO", Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                Toast.makeText(this, "MEDIA_ERROR_MALFORMED", Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Toast.makeText(this, "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK",
                        Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                Toast.makeText(this, "MEDIA_ERROR_TIMED_OUT", Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                Toast.makeText(this, "MEDIA_ERROR_UNSUPPORTED", Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }

    /**
     * 从暂停中恢复
     */
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // 判断播放位置
        if (Constants.playPosition >= 0) {

            if (mediaPlayer!=null) {
                seekBarAutoFlag = true;
                mediaPlayer.seekTo(Constants.playPosition);
                mediaPlayer.start();
            } else {
                playVideo();
            }

        }
    }

    /**
     * 页面处于暂停状态
     */
    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (null != mediaPlayer && mediaPlayer.isPlaying()) {
                Constants.playPosition = mediaPlayer.getCurrentPosition();
                mediaPlayer.pause();
                seekBarAutoFlag = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发生屏幕旋转时调用
     */
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != mediaPlayer) {
            // 保存播放位置
            Constants.playPosition = mediaPlayer.getCurrentPosition();
        }
    }

    /**
     * 屏幕旋转完成时调用
     */
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        playVideo();
        super.onRestoreInstanceState(savedInstanceState);



    }

    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
    }

    /**
     * 屏幕销毁时调用
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (null != SurfaceViewActivity.this.mediaPlayer) {
                seekBarAutoFlag = false;
                // 如果正在播放，则停止。
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                Constants.playPosition = -1;
                // 释放mediaPlayer
                SurfaceViewActivity.this.mediaPlayer.release();
                SurfaceViewActivity.this.mediaPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 转换播放时间
     */
    public String getShowTime(long milliseconds) {
        // 获取日历函数
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        SimpleDateFormat dateFormat = null;
        if (milliseconds / 60000 > 60) {
            dateFormat = new SimpleDateFormat("hh:mm:ss");
        } else {
            dateFormat = new SimpleDateFormat("mm:ss");
        }
        return dateFormat.format(calendar.getTime());
    }



    /**
     * 改变视频的显示大小，全屏，窗口，内容
     */
    public void changeVideoSize() {
        // 改变视频大小
        String videoSizeString = videoSizeButton.getText().toString();
        // 获取视频的宽度和高度
        int width = mediaPlayer.getVideoWidth();
        int height = mediaPlayer.getVideoHeight();
        // 如果按钮文字为窗口则设置为窗口模式
        if ("窗口".equals(videoSizeString)) {

            if (width > screenWidth || height > screenHeight) {

                float vWidth = (float) width / (float) screenWidth;
                float vHeight = (float) height / (float) screenHeight;

                float max = Math.max(vWidth, vHeight);
                // 计算出缩放大小,取接近的正值
                width = (int) Math.ceil((float) width / max);
                height = (int) Math.ceil((float) height / max);
            }
            // 设置SurfaceView的大小并居中显示
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width,
                    height);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            surfaceView.setLayoutParams(layoutParams);
            videoSizeButton.setText("全屏");
        } else if ("全屏".equals(videoSizeString)) {
            // 设置全屏
            // 设置SurfaceView的大小并居中显示
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(screenWidth,
                    screenHeight);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            surfaceView.setLayoutParams(layoutParams);
            videoSizeButton.setText("窗口");
        }
    }

}


