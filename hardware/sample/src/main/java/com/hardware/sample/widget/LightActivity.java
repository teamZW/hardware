package com.hardware.sample.widget;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import com.hardware.sample.R;
import com.wiget.light.LightView;

public class LightActivity extends Activity implements View.OnClickListener {

    /**
     * 年月日的集合    [20160726, 20160725, 20160724]
     */
    /**
     * Called when the activity is first created.
     */
     private LightView mLightView;
     private SeekBar sbSeek;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加载MainView
        setContentView(R.layout.activity_light);
        mLightView=(LightView)findViewById(R.id.lv_light);
        sbSeek=(SeekBar)findViewById(R.id.sb_seek);
        initValue();
    }

    private void initValue() {
        sbSeek.setMax(4);
        mLightView.reSize(0);
        sbSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mLightView.reSize(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void onBack(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dc_left_arrow_layout:

                break;

        }
    }
}