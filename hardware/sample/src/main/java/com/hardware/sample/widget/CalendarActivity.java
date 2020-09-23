package com.hardware.sample.widget;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hardware.sample.R;
import com.tc.calendar.CalendarView;
import com.tc.calendar.DateUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalendarActivity extends Activity implements View.OnClickListener {
    private LinearLayout calender_layout;
    private View calendar_view;
    private TextView year_tv,month_tv;
    private ImageView cancel_iv,left_arrow_iv,right_arrow_iv;
    private RelativeLayout left_arrow_layout,right_arrow_layout;
    private CalendarView calendar;
    /**
     * 年月日的集合    [20160726, 20160725, 20160724]
     */
    private ArrayList<String> groupList;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加载MainView
        setContentView(R.layout.activity_calendar);

        calender_layout = (LinearLayout) findViewById(R.id.calender_layout); //calender
        LayoutInflater inflater = LayoutInflater.from(CalendarActivity.this);
        calendar_view = inflater.inflate(R.layout.content_calendar, null);
        calender_layout.addView(calendar_view);
        year_tv = (TextView) findViewById(R.id.dc_year_tv);
        month_tv = (TextView) findViewById(R.id.dc_month_tv);
        cancel_iv = (ImageView) findViewById(R.id.dc_cancel_iv);
        left_arrow_layout = (RelativeLayout) findViewById(R.id.dc_left_arrow_layout);
        left_arrow_layout.setOnClickListener(this);
        left_arrow_iv = (ImageView) findViewById(R.id.dc_left_arrow_iv);
        left_arrow_iv.setOnClickListener(this);
        right_arrow_layout = (RelativeLayout) findViewById(R.id.dc_right_arrow_layout);
        right_arrow_layout.setOnClickListener(this);
        right_arrow_iv = (ImageView) findViewById(R.id.dc_right_arrow_iv);
        right_arrow_iv.setOnClickListener(this);
        calendar = (CalendarView) findViewById(R.id.dc_calendar);

        //设置控件监听，可以监听到点击的每一天（大家也可以在控件中根据需求设定）
        calendar.setOnItemClickListenerString(new CalendarView.OnItemClickListenerString() {
            @Override
            public void OnItemClick(String year, String month, int data) {
                String strTime = null;
                if (data >= 0 && data < 10) {
                    strTime = year + month + "0" + data;
                } else {
                    strTime = year + month + "" + data;
                }

                //设置当前
                int mYear = Integer.parseInt(year);
                int mMonth = Integer.parseInt(month)-1;
                int mDay = data;
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(mYear, mMonth, mDay);  //年月日
                Date date = calendar1.getTime();//date就是你需要的时间
                calendar.setCalendarData(date, mDay);
                String rightYearAndmonth = calendar.getYearAndmonth();
                String[] yaRight = rightYearAndmonth.split("-");
                year_tv.setText(yaRight[0] + "");
                if (true) {
                    month_tv.setText(yaRight[1] + "月");
                } else {
                    month_tv.setText(yaRight[1]);
                }
                calendar.setCurrentMonth(yaRight[1]);
                calendar.setLockTouch(false);
            }

            @Override
            public void moveTouchRight() {
                //点击下一月
                String rightYearAndmonth = calendar.clickRightMonth();
                String[] yaRight = rightYearAndmonth.split("-");
                year_tv.setText(yaRight[0] + "");
                if (true) {
                    month_tv.setText(yaRight[1] + "月");
                } else {
                    month_tv.setText(yaRight[1]);
                }
                calendar.setCurrentMonth(yaRight[1]);
            }

            @Override
            public void moveTouchLeft() {
                //点击上一月 同样返回年月
                String leftYearAndmonth = calendar.clickLeftMonth();
                String[] yaLeft = leftYearAndmonth.split("-");
                year_tv.setText(yaLeft[0] + "");
                if (true) {
                    month_tv.setText(yaLeft[1] + "月");
                } else {
                    month_tv.setText(yaLeft[1]);
                }
                calendar.setCurrentMonth(yaLeft[1]);
            }

            @Override
            public void logcatStart() {

            }
        });

        initValue();
    }

    private void initValue(){
        //================初始化==============
        //获取系统当前时间   格式（2016-03-09 16:36:56）
        String currentTime = DateUtils.getDateNow("");
        //获取系统时间的月份
        String currentMonth = currentTime.substring(5, 7);
        //如果当前月份以0开头则去掉0
        if (currentMonth.startsWith("0")) {
            currentMonth = currentTime.substring(6, 7);
        }
        int tempBeforeMonth = Integer.parseInt(currentMonth);
        int beforeMonth = tempBeforeMonth - 2;
        //设置成单选
        calendar.setSelectMore(false);
        //未点击上、下月份时立即控件设置当月
        calendar.setCurrentMonth(currentMonth);
        //获取日历中年月 ya[0]为年，ya[1]为月（格式大家可以自行在日历控件中改）
        String[] ya = calendar.getYearAndmonth().split("-");
        //设置年份
        year_tv.setText(ya[0]);
        //设置月份
        if (true) {
            month_tv.setText(ya[1] + "月");
        } else {
            month_tv.setText(ya[1]);
        }

        //=============设置数据===============
        groupList = new ArrayList<>();
        groupList.add("20200902");
        groupList.add("20200910");
        groupList.add("20200915");
        calendar.setDataAll(groupList);
        //刷新日历，解决没有即时获取到数据而不显示的问题
        calendar.postInvalidate();

        //===================设置当前==========
        int mYear = 2020;
        int mMonth = 9-1;
        int mDay = 2;
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(mYear, mMonth, mDay);  //年月日
        Date date = calendar1.getTime();//date就是你需要的时间
        calendar.setCalendarData(date, mDay);
        String rightYearAndmonth = calendar.getYearAndmonth();
        String[] yaRight = rightYearAndmonth.split("-");
        year_tv.setText(yaRight[0] + "");
        if (true) {
            month_tv.setText(yaRight[1] + "月");
        } else {
            month_tv.setText(yaRight[1]);
        }
        calendar.setCurrentMonth(yaRight[1]);
        calendar.setLockTouch(false);
    }

    public void onBack(View view){
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (calendar != null) {
            calendar.cancelTimer();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dc_left_arrow_layout:
            case R.id.dc_left_arrow_iv:
                //点击上一月 同样返回年月
                String leftYearAndmonth = calendar.clickLeftMonth();
                String[] yaLeft = leftYearAndmonth.split("-");
                year_tv.setText(yaLeft[0] + "");
                if (true) {
                    month_tv.setText(yaLeft[1] + "月");
                } else {
                    month_tv.setText(yaLeft[1]);
                }
                calendar.setCurrentMonth(yaLeft[1]);
                break;
            case R.id.dc_right_arrow_layout:
            case R.id.dc_right_arrow_iv:
                //点击下一月
                String rightYearAndmonth = calendar.clickRightMonth();
                String[] yaRight = rightYearAndmonth.split("-");
                year_tv.setText(yaRight[0] + "");
                if (true) {
                    month_tv.setText(yaRight[1] + "月");
                } else {
                    month_tv.setText(yaRight[1]);
                }
                calendar.setCurrentMonth(yaRight[1]);
                break;
        }
    }
}