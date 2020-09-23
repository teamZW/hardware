package com.hardware.sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hardware.sample.data.CustomAdapter;
import com.hardware.sample.data.CustomBean;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView list;
    private CustomAdapter mAdapter;
    private ArrayList<CustomBean> mListData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = findViewById(R.id.list);

        initValue();
        initListen();
    }

    private void initValue(){
        mListData = new ArrayList<>();

        //add circleTick
        CustomBean beanCircleTick = new CustomBean("circleTick",
                "１,父布局自适应大小 2,测量指定大小 3,onDraw",
                "com.example.sample.widget.CircleTickActivity");
        mListData.add(beanCircleTick);
        //add circlePan
        CustomBean beanCirclePan = new CustomBean("circlePan",
                "１,父布局指定大小 2,资源自适应大小 3,onLayout",
                "com.example.sample.widget.CirclePanActivity");
        mListData.add(beanCirclePan);
        //add calendar
        CustomBean beanCalendar = new CustomBean("calendar",
                "１,父布局自适应大小 2,测量指定大小 3,onDraw",
                "com.example.sample.widget.CalendarActivity");
        mListData.add(beanCalendar);

        CustomBean beanDraw = new CustomBean("draw",
                "区域绘制",
                "com.example.sample.widget.MoveDarwActivity");
        mListData.add(beanDraw);

        CustomBean beanlight = new CustomBean("Light",
                "智能设想灯",
                "com.example.sample.widget.LightActivity");
        mListData.add(beanlight);


        CustomBean beanTimeBar = new CustomBean("TimeBar",
                "时间轴",
                "com.example.sample.widget.TimeBarActivity");
        mListData.add(beanTimeBar);

        //add animation
        CustomBean beanScanAnimation = new CustomBean("scanAnimation",
                "scanAnimation",
                "com.example.sample.animation.ScanAnimationActivity");
        mListData.add(beanScanAnimation);
        CustomBean beanLoadingAnimation = new CustomBean("loadingAnimation",
                "loadingAnimation",
                "com.example.sample.animation.LoadingAnimationActivity");
        mListData.add(beanLoadingAnimation);
        CustomBean beanFallingBallAnimation = new CustomBean("fallingBallAnimation",
                "fallingBallAnimation",
                "com.example.sample.animation.FallingBallAnimationActivity");
        mListData.add(beanFallingBallAnimation);
        CustomBean beanMenuAnimation = new CustomBean("menuAnimation",
                "menuAnimation",
                "com.example.sample.animation.MenuAnimationActivity");
        mListData.add(beanMenuAnimation);
        CustomBean beanTanAnimation = new CustomBean("tanAnimation",
                "tanAnimation",
                "com.example.sample.animation.TanAnimationActivity");
        mListData.add(beanTanAnimation);

        //add lock
        CustomBean beanLock = new CustomBean("lock",
                "密码锁",
                "com.example.sample.widget.CircleLockActivity");
        mListData.add(beanLock);

        mAdapter = new CustomAdapter(MainActivity.this,mListData);
        list.setAdapter(mAdapter);
    }

    private void initListen(){
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CustomBean bean = mListData!=null?mListData.get(position):null;
                if(bean!=null){
                    Intent in = new Intent();
                    in.setClassName(MainActivity.this, bean.getActivityUri());
                    MainActivity.this.startActivity(in);
                }
            }
        });
    }
}
