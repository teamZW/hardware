package com.hardware.sample.widget;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.hardware.sample.R;
import com.tc.lock.CircleLock;


public class CircleLockActivity extends AppCompatActivity {
    private CircleLock mCircleLock;
    private TextView valueTV1,valueTV2,valueTV3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circlelock);

        valueTV1 = (TextView)findViewById(R.id.valuetv1);
        valueTV2 = (TextView)findViewById(R.id.valuetv2);
        valueTV3 = (TextView)findViewById(R.id.valuetv3);
        mCircleLock = (CircleLock)findViewById(R.id.crpv_tick);
        mCircleLock.setOnTimeChangeListener(new CircleLock.OnTimeChangeListener() {
            @Override
            public void onChange(int value1, int value2, int value3) {
                Log.v("lock", "main onChange value1="+value1+", value2="+value2+", value3="+value3);
                valueTV3.setText(value1<10 ? ("0"+value1) : (value1+""));
                valueTV2.setText(value2<10 ? ("0"+value2) : (value2+""));
                valueTV1.setText(value3<10 ? ("0"+value3) : (value3+""));
            }

            @Override
            public void updateSingle(int index, int value) {
                if(index==1){
                    valueTV3.setText(value<10 ? ("0"+value) : (value+""));
                }else if(index==2){
                    valueTV2.setText(value<10 ? ("0"+value) : (value+""));
                }else if(index==3){
                    valueTV1.setText(value<10 ? ("0"+value) : (value+""));
                }
            }
//
//            @Override
//            public void onChange(int value) {
//                //Log.v("lock", "main onChange value="+value);
//            }
        });
    }

    public void onBack(View view){
        finish();
    }
}
