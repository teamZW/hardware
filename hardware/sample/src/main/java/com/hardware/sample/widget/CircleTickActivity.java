package com.hardware.sample.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.hardware.sample.R;
import com.tc.tick.CircleTickView;


public class CircleTickActivity extends AppCompatActivity {
    private CircleTickView mCircleTickView;
    private int screenWidth,screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circletick);
        screenWidth = getScreenWidth(CircleTickActivity.this);
        screenHeight = getScreenHeight(CircleTickActivity.this);

        mCircleTickView = (CircleTickView)findViewById(R.id.crpv_tick);
        mCircleTickView.setOnTimeChangeListener(new CircleTickView.OnTimeChangeListener() {
            @Override
            public void onChange(int value) {
                Log.v("customs","circleTick： onChange value="+value);
            }
        });
        RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams )mCircleTickView.getLayoutParams();
        int[] position = CircleTickView.getPositionForAngle(screenWidth,screenHeight);
        param.setMargins(position[0], position[1], 0, 0);
        mCircleTickView.setLayoutParams(param);
    }

    public void onBack(View view){
        finish();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = windowManager.getDefaultDisplay();
        Point outPoint = new Point();
        defaultDisplay.getRealSize(outPoint);
        return outPoint.x;
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = windowManager.getDefaultDisplay();
        Point outPoint = new Point();
        defaultDisplay.getRealSize(outPoint);
        return outPoint.y;
    }

}
