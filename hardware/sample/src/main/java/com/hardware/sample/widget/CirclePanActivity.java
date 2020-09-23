package com.hardware.sample.widget;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.hardware.sample.R;
import com.tc.pan.CirclePanView;

public class CirclePanActivity extends Activity {
    CirclePanView mCircleTouchView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加载MainView
        setContentView(R.layout.activity_circlepan);

        mCircleTouchView = (CirclePanView)findViewById(R.id.circleView);
        mCircleTouchView.setCircleOnClickListener(new CirclePanView.CircleOnClickListener() {
            @Override
            public void OnChoiceDirectionCallBack(boolean up, boolean down, boolean left, boolean right) {
                Log.v("customs","circlePan： choice up="+up+", down="+down+", left="+left+", right="+right);
            }
        });
        mCircleTouchView.setTouchSensitivity(0.0f);
        mCircleTouchView.setBgExplicitMode(true);
    }

    public void onBack(View view){
        finish();
    }
}