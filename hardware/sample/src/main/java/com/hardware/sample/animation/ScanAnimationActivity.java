package com.hardware.sample.animation;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.hardware.sample.R;

public class ScanAnimationActivity extends Activity {


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加载MainView
        setContentView(R.layout.activity_scananimation);

    }

    public void onBack(View view){
        finish();
    }
}