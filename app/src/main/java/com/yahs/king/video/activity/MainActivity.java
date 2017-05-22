package com.yahs.king.video.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.yahs.king.video.R;
import com.yahs.king.video.adapter.MyAdapter;
import com.yahs.king.video.bean.VideoBean;
import com.yahs.king.video.global.GlobalConstants;
import com.yahs.king.video.utils.CacheUtils;
import com.yahs.king.video.utils.network.HttpCallBackListener;
import com.yahs.king.video.utils.network.HttpUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<VideoBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean> contentList;
    private RecyclerView rv_video;
    private MyAdapter mAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contentList=new ArrayList<>();

        rv_video = (RecyclerView) this.findViewById(R.id.rv_video);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv_video.setLayoutManager(linearLayoutManager);
        initData();

        mAdapter=new MyAdapter(this,contentList);
        rv_video.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new MyAdapter.onItemClicListener() {
            @Override
            public void onItemClickListener(View v, int pos) {
                //跳转到视频播放的界面
                Intent intent = new Intent(MainActivity.this, SurfaceViewActivity.class);
                String video_uri = contentList.get(pos).getVideo_uri();
                intent.putExtra("video_url", video_uri);

                startActivity(intent);
            }
        });
    }

    private void initData() {

        //判断有没有缓存
        String cache = CacheUtils.getCache(GlobalConstants.URL, this);
        if (!TextUtils.isEmpty(cache)){
            //从缓存得到数据
            processData(cache);
        }else {
            getDatafromServer();
        }
        getDatafromServer();
    }

    private void getDatafromServer() {
        HttpUtils.sendHttpRequest(GlobalConstants.URL, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                //得到服务器返回的内容
                String result = response.toString();
                //解析数据
                processData(result);

                //缓存到本地
                CacheUtils.setCache(MainActivity.this,GlobalConstants.URL,result);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();

            }
        });
    }

    private void processData(String result) {
        try{
            JSONObject jsonArray = new JSONObject(result);
            JSONObject showapi_res_body = jsonArray.getJSONObject("showapi_res_body");
            JSONObject pagebean = showapi_res_body.getJSONObject("pagebean");
            JSONArray contentlist = pagebean.getJSONArray("contentlist");
            for(int i =0;i<contentlist.length();i++){
                JSONObject content = contentlist.getJSONObject(i);
                VideoBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean contentlistBean = new VideoBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean();
                contentlistBean.setText(content.getString("text"));
                contentlistBean.setHate(content.getString("hate"));
                contentlistBean.setLove(content.getString("love"));
                contentlistBean.setCreate_time(content.getString("create_time"));
                contentlistBean.setProfile_image(content.getString("profile_image"));
                contentlistBean.setName(content.getString("name"));
                contentlistBean.setVideo_uri(content.getString("video_uri"));
                contentList.add(contentlistBean);
                System.out.println(contentlistBean.getName());

            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
