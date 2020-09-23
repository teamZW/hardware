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
        CustomBean beanCircleTick = new CustomBean("wifi",
                "wifi",
                "com.hardware.sample.func.WifiActivity");
        mListData.add(beanCircleTick);

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
