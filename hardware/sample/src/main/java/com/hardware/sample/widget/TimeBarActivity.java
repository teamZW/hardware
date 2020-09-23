package com.hardware.sample.widget;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hardware.sample.R;
import com.wiget.timebar.RecordAlarmTimeSegment;
import com.wiget.timebar.RecordSliderView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TimeBarActivity extends Activity implements View.OnClickListener {

    /**
     * 年月日的集合    [20160726, 20160725, 20160724]
     */
    /**
     * Called when the activity is first created.
     */
     private RecordSliderView my_timebar_view;
     private TextView currentTimeTextView;
    //一分钟多少毫秒
    private static long ONE_MINUTE_IN_MS = 60 * 1000;
    //一小时多少毫秒
    private static long ONE_HOUR_IN_MS = 60 * ONE_MINUTE_IN_MS;
    //一天多少毫秒
    private static long ONE_DAY_IN_MS = 24 * ONE_HOUR_IN_MS;

    private int recordDays = 7;
    private long currentRealDateTime = System.currentTimeMillis();

    private List<RecordAlarmTimeSegment> recordDataList;
    private SimpleDateFormat zeroTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加载MainView
        setContentView(R.layout.activity_timebar);
        my_timebar_view=(RecordSliderView)findViewById(R.id.rsv_timebar);
        currentTimeTextView=(TextView)findViewById(R.id.tv_current_time);
        initValue();
    }

    private void initValue() {
        long timebarRightEndPointTime = currentRealDateTime + 3 * ONE_HOUR_IN_MS;
        long timebarLeftEndPointTime = timebarRightEndPointTime - recordDays * ONE_DAY_IN_MS;
        long timabarCursorCurrentTime = timebarRightEndPointTime;
        my_timebar_view.initTimebarLengthAndPosition(timebarLeftEndPointTime,
                timebarRightEndPointTime, timabarCursorCurrentTime);
        //这里的时间段需要后台给予
        recordDataList = new ArrayList<>();
        recordDataList.add(new RecordAlarmTimeSegment(currentRealDateTime - ONE_MINUTE_IN_MS*10, currentRealDateTime + ONE_MINUTE_IN_MS*10));
        recordDataList.add(new RecordAlarmTimeSegment(currentRealDateTime - ONE_DAY_IN_MS, currentRealDateTime - ONE_DAY_IN_MS + ONE_MINUTE_IN_MS * 128));
        recordDataList.add(new RecordAlarmTimeSegment(currentRealDateTime - ONE_DAY_IN_MS + 18 * ONE_HOUR_IN_MS, currentRealDateTime - ONE_DAY_IN_MS + 18 * ONE_HOUR_IN_MS + ONE_MINUTE_IN_MS * 32));
        recordDataList.add(new RecordAlarmTimeSegment(currentRealDateTime - 5 * ONE_MINUTE_IN_MS, currentRealDateTime));
        my_timebar_view.setRecordDataExistTimeClipsList(recordDataList);
        //获取报警列表，保存本地数据
        my_timebar_view.setOnBarMoveListener(new  RecordSliderView.OnBarMoveListener() {
            @Override
            public void onBarMove(long screenLeftTime, long screenRightTime, long currentTime) {
                  currentTimeTextView.setText(zeroTimeFormat.format(currentTime));
                //    DeviceUtil.LogMsg("zeroTimeFormat="+currentTime);
                //   DeviceUtil.LogMsg("onBarMove()");
            }

            @Override
            public void OnBarMoveFinish(long screenLeftTime, long screenRightTime, long currentTime) {
                for (int i = 0; i < recordDataList.size(); i++) {
                    if (recordDataList.get(i).getStartTimeInMillisecond() < currentTime && recordDataList.get(i).getEndTimeInMillisecond() > currentTime) {
                     }
                }
                //   DeviceUtil.LogMsg("OnBarMoveFinish()");
            }
        });

        my_timebar_view.setOnBarScaledListener(new  RecordSliderView.OnBarScaledListener() {
            @Override
            public void onBarScaled(long screenLeftTime, long screenRightTime, long currentTime) {
                 currentTimeTextView.setText(zeroTimeFormat.format(currentTime));
                // DeviceUtil.LogMsg("onBarScaled()");
            }

            @Override
            public void onBarScaleFinish(long screenLeftTime, long screenRightTime, long currentTime) {
                //  DeviceUtil.LogMsg("onBarScaleFinish()");
            }
        });
        my_timebar_view.setCurrentTimeInMillisecond(currentRealDateTime);
        currentTimeTextView.setText(zeroTimeFormat.format(currentRealDateTime));
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