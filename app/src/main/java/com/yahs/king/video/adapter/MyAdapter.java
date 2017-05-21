package com.yahs.king.video.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yahs.king.video.R;
import com.yahs.king.video.bean.VideoBean;
import com.yahs.king.video.utils.bitmap.DiskCacheUtils;
import com.yahs.king.video.utils.bitmap.MyBitmapUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by king on 2017/5/20.
 */
public class MyAdapter extends RecyclerView.Adapter{
    private String love;
    private Context context;
    private DiskCacheUtils mDiskCacheUtils;
    private ArrayList<VideoBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean> datas;
    private onItemClicListener mOnItemClicListener;
    private VideoBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean contentlistBean;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bitmap bitmap = (Bitmap) msg.obj;
            homeHolder.image.setImageBitmap(bitmap);

        }
    };
    private HomeHolder homeHolder;


    public MyAdapter(Context context, ArrayList<VideoBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean>datas){
        this.context=context;
        this.datas=datas;
        this.mDiskCacheUtils=new DiskCacheUtils();
    }
    @Override
    public int getItemCount() {
        return datas.size();

    }

    @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {

            View view = LayoutInflater.from(context).inflate(R.layout.item_video_content, parent, false);
            final HomeHolder holder = new HomeHolder(view);
            //点赞
            holder.likeNum.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    int position = holder.getAdapterPosition();
                    int loveNum = Integer.parseInt(contentlistBean.getLove());
                    love = String.valueOf(loveNum+1);
                    System.out.println("love"+ love);

                    holder.likeNum.setText(love);
                    System.out.println("getLove:"+contentlistBean.getLove());
                    if (viewType==position){
                        holder.likeNum.setEnabled(true);
                        holder.like.setEnabled(true);

                    }else {
                        holder.likeNum.setEnabled(false);
                        holder.like.setEnabled(false);
                    }
                    MyAdapter.this.notifyItemInserted(position-1);
                }

            });


            //不喜欢
            holder.hateNum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = holder.getAdapterPosition();
                    int loveNum = Integer.parseInt(contentlistBean.getHate());
                    String unLove = String.valueOf(loveNum+1);
                    holder.likeNum.setText(unLove);
                    if (viewType==position){
                        holder.hateNum.setEnabled(true);
                        holder.unlike.setEnabled(true);

                    }else {
                        holder.hateNum.setEnabled(false);
                        holder.unlike.setEnabled(false);
                    }
                    MyAdapter.this.notifyItemInserted(position-1);
                }
            });
            return new HomeHolder(view);
        }


        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof HomeHolder){
                homeHolder = (HomeHolder) holder;
                contentlistBean = datas.get(position);
                homeHolder.userName.setText(contentlistBean.getName());
                homeHolder.userTime.setText(contentlistBean.getCreate_time());
                homeHolder.likeNum.setText(contentlistBean.getLove());
                homeHolder.hateNum.setText(contentlistBean.getHate());
                homeHolder.text.setText(contentlistBean.getText());
                homeHolder.text.setText(contentlistBean.getText());
                homeHolder.text.setText(contentlistBean.getText());

                //加载头像以及第一帧图片
                MyBitmapUtils myBitmapUtils = new MyBitmapUtils(context);
                try {
                    myBitmapUtils.disPlay(homeHolder.ivHeader, contentlistBean.getProfile_image());

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
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
                            Message msg = Message.obtain();
                            msg.obj=bitmap;
                            mHandler.sendMessage(msg);

                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            retriever.release();
                        }
                    }).start();



                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }



    public void setOnItemClickListener(onItemClicListener listener){
        mOnItemClicListener=listener;
    }

        class HomeHolder extends RecyclerView.ViewHolder{

            private final TextView userName;
            private final TextView userTime;
            private final TextView text;
            private final ImageView image;
            private final ImageView like;
            private final ImageView unlike;
            private final ImageView ivHeader;
            private final TextView likeNum;
            private final TextView hateNum;

            public HomeHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mOnItemClicListener!=null){
                            mOnItemClicListener.onItemClickListener(view,getLayoutPosition());
                        }
                    }
                });
                ivHeader = (ImageView) itemView.findViewById(R.id.ivHeader);
                userName = (TextView) itemView.findViewById(R.id.tvUserName);
                userTime = (TextView) itemView.findViewById(R.id.tvTime);
                text = (TextView) itemView.findViewById(R.id.tvContent);
                likeNum = (TextView) itemView.findViewById(R.id.like_num);
                likeNum.setTag(getAdapterPosition());
                hateNum = (TextView) itemView.findViewById(R.id.hate_num);
                image = (ImageView) itemView.findViewById(R.id.ivContent);
                like = (ImageView) itemView.findViewById(R.id.like);
                unlike = (ImageView) itemView.findViewById(R.id.hate_item);


            }
        }
    public interface onItemClicListener{
        void onItemClickListener(View v, int pos);
    }
}
