package com.tc.calendar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日历控件 功能：获得点选的日期区间
 * 
 */
public class CalendarView extends View implements View.OnTouchListener {
	private Date selectedStartDate;
	private Date selectedEndDate;
	private Date curDate; // 当前日历显示的月
	private Date today; // 今天的日期文字显示红色
	private Date downDate; // 手指按下状态时临时日期
	private Date showFirstDate, showLastDate; // 日历显示的第一个日期和最后一个日期
	private int downIndex=-1; // 按下的格子索引
	private int downIndexBack=-1; // 按下的格子索引
	private Calendar calendar;
	private Surface surface;
	private int[] date = new int[42]; // 日历显示数字
	private int curStartIndex, curEndIndex; // 当前显示的日历起始的索引
	private boolean completed = false; // 为false表示只选择了开始日期，true表示结束日期也选择了
	private boolean isSelectMore = false;
	//给控件设置监听事件
	private OnItemClickListener onItemClickListener;
	private OnItemClickListenerString onItemClickListenerString;
	// 当月    日的数据  （选中的年月日期显示成黑色，可点击）
	private ArrayList<String> currentMonthList;
	/** 上个月数据 */
	private ArrayList<String> preMonthList;
	/** 上上个月 */
	private ArrayList<String> prePreMonthList;
	/** 当前月份 */
	private String currentMonth;
	/** 当前月份是哪一个月 */
	private String currentOneMonth = currentMonth;
	private int currentDay=-1;
	private int currentDayBack=-1;
	private Bitmap mRecoderBjBitmap;
	private Bitmap mRecoderCuBitmap;
	private ArrayList<String> groupList;  //保存每个录像文件的时间值
	private boolean lockTouch=false;
	private boolean moveTouch=false;
	private int countPassLogStep=0;
	private MyCountDownTimer timer;
	private final long TIME = 1000L;  //1s
	private final long INTERVAL = 1000L;
	private int[] logcatSave = {5,6,7,8,9};

	public CalendarView(Context context) {
		super(context);
		init();
	}

	public CalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public void setData(ArrayList<String> currentMonthList,ArrayList<String> preMonthList,ArrayList<String> prePreMonthList,String currentMonth){
		this.currentMonthList = currentMonthList;
		this.preMonthList = preMonthList;
		this.prePreMonthList = prePreMonthList;
		this.currentMonth = currentMonth;

		for(int j= 0;j<currentMonthList.size();j++){
			//LogTools.e(TAG,"setData---currentMonthList="+currentMonthList.get(j));
		}
		for(int j1= 0;j1<preMonthList.size();j1++){
			//LogTools.e(TAG,"setData---preMonthList="+preMonthList.get(j1));
		}
		for(int j2= 0;j2<prePreMonthList.size();j2++){
			//LogTools.e(TAG,"setData---prePreMonthList="+prePreMonthList.get(j2));
		}
		//LogTools.e(TAG,"setData---currentMonth="+currentMonth);
	}

	public void setDataAll(ArrayList<String> groupList){
		this.groupList = groupList;

		for(int j= 0;j<groupList.size();j++){
			//LogTools.e(TAG,"setDataAll---groupList="+groupList.get(j));
		}
	}

	private void init() {
		Resources res=getResources();
		// 获取位图
		mRecoderBjBitmap= BitmapFactory.decodeResource(res, R.drawable.recoder_bj);
		mRecoderCuBitmap= BitmapFactory.decodeResource(res, R.drawable.recoder_cu);

		curDate = selectedStartDate = selectedEndDate = today = new Date();
		calendar = Calendar.getInstance();
		calendar.setTime(curDate);

		//获取设置的时间
		//LogTools.e(TAG,"init---curDate---获取初始化时间year="+calendar.get(Calendar.YEAR));
		//LogTools.e(TAG,"init---curDate---获取初始化时间month="+(calendar.get(Calendar.MONTH)+1)); //month+1
		//LogTools.e(TAG,"init---curDate---获取初始化时间day="+calendar.get(Calendar.DATE));
		//LogTools.e(TAG,"init---curDate---获取初始化时间day1="+calendar.get(Calendar.DAY_OF_MONTH));
		//LogTools.e(TAG,"init---curDate---获取初始化时间startWeekDay="+(calendar.get(Calendar.DAY_OF_WEEK)-1)); //表示本周的第几天，从周日开始计算
		//LogTools.e(TAG,"init---curDate---获取初始化时间startYearDay="+calendar.get(Calendar.DAY_OF_YEAR)); //表示本年的第几天
		//LogTools.e(TAG,"init---curDate---获取初始化时间endMonthDay="+calendar.getActualMaximum(Calendar.DATE)); //本月最后一天是

		surface = new Surface();
		surface.density = getResources().getDisplayMetrics().density;
		//LogTools.e(TAG,"init---surface.density="+surface.density);

		//设置整个日历的背景颜色
		setBackgroundColor(surface.bgColor);
		//设置整个日历的背景图片
		//setBackgroundDrawable(surface.cellSelectedBg);
		setOnTouchListener(this);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		surface.width = getResources().getDisplayMetrics().widthPixels;
		surface.height = (int) (getResources().getDisplayMetrics().heightPixels*2/5);
		//LogTools.e(TAG,"onMeasure---surface.width="+surface.width);
		//LogTools.e(TAG,"onMeasure---surface.height="+surface.height);
//		if (View.MeasureSpec.getMode(widthMeasureSpec) == View.MeasureSpec.EXACTLY) {
//			surface.width = View.MeasureSpec.getSize(widthMeasureSpec);
//		}
//		if (View.MeasureSpec.getMode(heightMeasureSpec) == View.MeasureSpec.EXACTLY) {
//			surface.height = View.MeasureSpec.getSize(heightMeasureSpec);
//		}
		widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(surface.width,View.MeasureSpec.EXACTLY);
		heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(surface.height,View.MeasureSpec.EXACTLY);
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,int bottom) {
		//LogTools.d(TAG, "[onLayout] changed:"+ (changed ? "new size" : "not change") + " left:" + left + " top:" + top + " right:" + right + " bottom:" + bottom);
		//LogTools.e(TAG,"onLayout---changed="+changed);
		if (changed) {
			surface.init();
		}
		super.onLayout(changed, left, top, right, bottom);
	}

	private class MyCountDownTimer extends CountDownTimer {
		public MyCountDownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			long time = millisUntilFinished / 1000;
		}

		@Override
		public void onFinish() {
			countPassLogStep=0;
		}
	}

	/**
	 * 开始倒计时
	 */
	private void startTimer() {
		if (timer == null) {
			timer = new MyCountDownTimer(TIME, INTERVAL);
		}
		timer.start();
	}

	/**
	 * 取消倒计时
	 */
	public void cancelTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// ---------------------------------画框-----------------------------------------------
		canvas.drawPath(surface.boxPath, surface.borderPaint);

		// 年月
		//String monthText = getYearAndmonth();
		//float textWidth = surface.monthPaint.measureText(monthText);
		//canvas.drawText(monthText, (surface.width - textWidth) / 2f,
		//		surface.monthHeight * 3 / 4f, surface.monthPaint);
		// 上一月/下一月
		//canvas.drawPath(surface.preMonthBtnPath, surface.monthChangeBtnPaint);
		//canvas.drawPath(surface.nextMonthBtnPath, surface.monthChangeBtnPaint);

		// ---------------------------------星期-----------------------------------------------
		float weekTextY = surface.monthHeight + surface.weekHeight * 3 / 4f;
		// 星期背景
//		surface.cellBgPaint.setColor(surface.textColor);
//		canvas.drawRect(surface.weekHeight, surface.width, surface.weekHeight, surface.width, surface.cellBgPaint);
		for (int i = 0; i < surface.weekText.length; i++) {
			float weekTextX = i
					* surface.cellWidth
					+ (surface.cellWidth - surface.weekPaint
					.measureText(surface.weekText[i])) / 2f;
			if(surface.weekText[i].length()>5){
				canvas.drawText(surface.weekText[i].substring(0, 5)+"...", weekTextX, weekTextY,surface.weekPaint);
			}else{
				canvas.drawText(surface.weekText[i], weekTextX, weekTextY,surface.weekPaint);
			}
		}

		// --------------------------------计算日期-----------------------------------------------
		calculateDate();
		int currentTouchNum = downIndex-curStartIndex + 1;
		if(currentTouchNum>=5 && currentTouchNum<=9){
			cancelTimer();
			startTimer();
		}
		switch(countPassLogStep){
			case 0:
				if(currentTouchNum == logcatSave[countPassLogStep]){
					countPassLogStep++;
				}else{
					countPassLogStep=0;
				}
				break;
			case 1:
				if(currentTouchNum == logcatSave[countPassLogStep]){
					countPassLogStep++;
				}else{
					countPassLogStep=0;
				}
				break;
			case 2:
				if(currentTouchNum == logcatSave[countPassLogStep]){
					countPassLogStep++;
				}else{
					countPassLogStep=0;
				}
				break;
			case 3:
				if(currentTouchNum == logcatSave[countPassLogStep]){
					countPassLogStep++;
				}else{
					countPassLogStep=0;
				}
				break;
			case 4:
				if(currentTouchNum == logcatSave[countPassLogStep]){
					countPassLogStep=0;
					onItemClickListenerString.logcatStart();
				}else{
					countPassLogStep=0;
				}
				break;
		}

		// --------------------------------时间设置-----------------------------------------------
		int todayIndex = -1;
		String currentMonthB;
		String currentYearB;

		//重新设置当前时间
		calendar.setTime(curDate);
		//LogTools.e(TAG,"onDraw---重新设置当前时间---year="+calendar.get(Calendar.YEAR));
		//LogTools.e(TAG,"onDraw---重新设置当前时间---month="+(calendar.get(Calendar.MONTH)+1)); //month+1
		//LogTools.e(TAG,"onDraw---重新设置当前时间---day="+calendar.get(Calendar.DAY_OF_MONTH));
		//得到当前时间（年+月）
		String curYearAndMonth = calendar.get(Calendar.YEAR) + "" + calendar.get(Calendar.MONTH);
		String dataIndex = getDateNow("");
		//得到当前显示的年
		currentYearB = dataIndex.substring(0, 4);
		//得到当前显示的月
		currentMonthB = dataIndex.substring(5, 7);
		//LogTools.e(TAG,"onDraw---得到当前显示的年---todayYear="+currentYearB);
		//LogTools.e(TAG,"onDraw---得到当前显示的月---todayMonth="+currentMonthB);
		//LogTools.e(TAG,"onDraw---得到当前时间（年+月）---curYearAndMonth="+curYearAndMonth);

		/*#############################可以不用设置##########################################*/
		//将时间设置成今天，为了重点显示今天的日期
//		calendar.setTime(today);
//		//LogTools.e(TAG,"onDraw---设置今天时间---year="+calendar.get(Calendar.YEAR));
//		//LogTools.e(TAG,"onDraw---设置今天时间---month="+(calendar.get(Calendar.MONTH)+1)); //month+1
//		//LogTools.e(TAG,"onDraw---设置今天时间---day="+calendar.get(Calendar.DAY_OF_MONTH));
//		//获取今天的年+月
//		String todayYearAndMonth = calendar.get(Calendar.YEAR) + "" + calendar.get(Calendar.MONTH);
//		//LogTools.e(TAG,"onDraw---得到今天时间（年+月）---todayYearAndMonth="+todayYearAndMonth);
//
//		// --------------------------------得到当前day及位置-----------------------------------------------
//		if (curYearAndMonth.equals(todayYearAndMonth)) {
//			//当前日期（26号）
//			int todayNumber = calendar.get(Calendar.DAY_OF_MONTH);  //获取当前day
//			//LogTools.e(TAG,"onDraw---获取当前day---todayNumber="+todayNumber);
//			todayIndex = curStartIndex + todayNumber - 1;
//			//LogTools.e(TAG,"onDraw---得到当前day在42个位置中的位置---todayIndex="+todayIndex);
//		}
		/*############################################################################*/

		// --------------------------------开始绘制 设置当前日期的文字颜色   42个位置-----------------------------------------------
		//todayNumber：今天的day
		//todayIndex：今天的索引
		//curStartIndex：这个月开始位置索引
		//curEndIndex：这个月结束位置索引
		//currentYearB：当前年
		//currentMonthB：当前月
		//downIndex：触摸索引
		//downdata：触摸时间
		for (int i = 0; i < 42; i++) {
			int color = surface.textColor;

			//是上个月或者是下个月的日子，则设置成透明不显示
			if (isLastMonth(i) || isNextMonth(i)) {
				color = surface.borderColor;
				//LogTools.e(TAG,"onDraw---是上个月或者是下个月的日子，则设置成透明不显示---位置i="+i);
			}else if (todayIndex != -1 && i == todayIndex) {  //todayIndex:当前day索引
//				int todayData = todayIndex - curStartIndex + 1;
//				//LogTools.e(TAG,"onDraw---当前day需要重点颜色---位置i="+i);
//				//LogTools.e(TAG,"onDraw---通过计算得到当前的day---todayData="+todayData);
//				//LogTools.d(TAG,"onDraw---当月正确日期---todayData="+todayData);
////				if(currentMonth!=null && currentOneMonth!=null){
////					if(currentMonth.equals(currentOneMonth)){
////						if(currentMonthList!=null && currentMonthList.size()>0){
////							for(int j= 0;j<currentMonthList.size();j++){
////								if(todayData ==  Integer.parseInt(currentMonthList.get(j))){
////									color = surface.validNumberColor;
////									LogTools.d(TAG,"onDraw---当月正确日期---color="+color);
////								}
////							}
////						}
////					}
////				}
//				//color = surface.todayNumberColor;
//				//LogTools.e(TAG,"onDraw---为当前day绘制背景");
//				color = surface.validNumberColor;
//				//绘制背景圈
//				drawCellBg(canvas, todayIndex , surface.todayNumberColor);
			}else{
				//当月其它day
				int currentData = i-curStartIndex + 1;

				//LogTools.e(TAG,"onDraw---当月的其它day---位置i="+i);
				//LogTools.e(TAG,"onDraw---当月的其它day---currentData="+currentData);

				//touch
				if(checkTouchTime(currentYearB,currentMonthB,downIndex) == true){
					if (downIndex == i) {
						color = surface.validNumberColor;
						downIndex = -1;
						//绘制背景圈
						drawCurrentBg(canvas, i, surface.todayNumberColor);
						lockTouch=true;
						// 向右边移出
						onItemClickListenerString.OnItemClick(currentYearB, currentMonthB, currentData);
					} else {
						if( checkRecoderPlay(currentYearB,currentMonthB,currentData)==true ) {
							color = surface.validNumberColor;
							//绘制背景圈
							drawRecoderBg(canvas, i, surface.todayNumberColor);
						}
					}
				}else{
					if( checkRecoderPlay(currentYearB,currentMonthB,currentData)==true ) {
						color = surface.validNumberColor;

						//刷新listview选择的日期
						if (currentData == currentDay) {
							//绘制背景圈
							drawCurrentBg(canvas, i, surface.todayNumberColor);
						} else {
							//绘制背景圈
							drawRecoderBg(canvas, i, surface.todayNumberColor);
						}
					}
				}
/*
				if(currentMonth!=null && currentOneMonth!=null){
					if(currentMonth.equals(currentOneMonth)){
						if(currentMonthList!=null && currentMonthList.size()>0){
							for(int j= 0;j<currentMonthList.size();j++){
								if(currentData ==  Integer.parseInt(currentMonthList.get(j))){
									color = surface.validNumberColor;
									LogTools.d(TAG,"onDraw---当月其它录像日期---color当月="+color);
									LogTools.d(TAG,"onDraw---当月其它录像日期绘制背景圈");
									//绘制背景圈
									drawRecoderBg(canvas, i , surface.todayNumberColor);
								}
							}
						}
					}else if((Integer.parseInt(currentMonth)-Integer.parseInt(currentOneMonth))==1){ //上月份
						if(preMonthList!=null && preMonthList.size()>0){
							for(int j= 0;j<preMonthList.size();j++){
								if(currentData ==  Integer.parseInt(preMonthList.get(j))){
									color = surface.validNumberColor;
									LogTools.d(TAG,"onDraw---上个月录像日期--color上月="+color);
									LogTools.d(TAG,"onDraw---上个月录像日期绘制背景圈");
									//绘制背景圈
									drawRecoderBg(canvas, i , surface.todayNumberColor);
								}
							}
						}
					}else if((Integer.parseInt(currentMonth)-Integer.parseInt(currentOneMonth))==2){//上上个月
						if(prePreMonthList!=null && prePreMonthList.size()>0){
							for(int j= 0;j<prePreMonthList.size();j++){
								if(currentData ==  Integer.parseInt(prePreMonthList.get(j))){
									color = surface.validNumberColor;
									LogTools.d(TAG,"onDraw---上上个月录像日期---color上上月="+color);
									LogTools.d(TAG,"onDraw---上上个月录像日期绘制背景圈");
									//绘制背景圈
									drawRecoderBg(canvas, i , surface.todayNumberColor);
								}
							}
						}
					}
				}
				*/
			}

			//LogTools.e(TAG,"onDraw---42个位置需要绘制的值--- date["+i+"]="+date[i]);
			drawCellText(canvas, i, date[i] + "", color);
		}
		if(currentDayBack != currentDay){
			currentDayBack = currentDay;
		}

		//touch没有坐落到录像坐标点上
//		if(downIndexBackup != downIndex){
//			//重新绘制上次录像touch坐标点
//			//绘制背景圈
//			drawCurrentBg(canvas, downIndexBackup , surface.todayNumberColor);
//			drawCellText(canvas, downIndexBackup, date[downIndexBackup] + "", surface.validNumberColor);
//			downIndexBackup=downIndex;
//		}

		super.onDraw(canvas);
	}

	private boolean checkRecoderPlay(String currentYear,String currentMonth,int currentDataM){
		//先判断年
		if(currentOneMonth!=null){
			if(groupList!=null && groupList.size()>0){
				for(int j= 0;j<groupList.size();j++){
					if(currentYear.equals(groupList.get(j).substring(0, 4))) {
						if (currentMonth.equals(groupList.get(j).substring(4, 6))) {
							if (currentDataM == Integer.parseInt(groupList.get(j).substring(6, 8))) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	private boolean checkTouchTime(String currentYear,String currentMonth,int index){
		try {
			String fString = yyyyMMddHHmmss;
			DateFormat df = new SimpleDateFormat(fString);
			String strTime = df.format(downDate);
			//LogTools.e(TAG,"checkTouchTime---strTime="+strTime);

			int currentDataMM = index-curStartIndex + 1;

			if(currentOneMonth!=null){
				if(groupList!=null && groupList.size()>0){
					for(int j= 0;j<groupList.size();j++){
						if(currentYear.equals(groupList.get(j).substring(0, 4))) {
							if (currentMonth.equals(groupList.get(j).substring(4, 6))) {
								if (currentDataMM == Integer.parseInt(groupList.get(j).substring(6, 8))) {
									return true;
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
		}
		return false;
	}

	public final String yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";
	private String getDateNow(String fString) {
		try {
			if ((fString == null) || (fString.equals("")))
				fString = yyyyMMddHHmmss;

			DateFormat df = new SimpleDateFormat(fString,Locale.CHINA);
			return df.format(calendar.getTime());
		} catch (Exception e) {
		}
		return null;
	}

	private boolean onStartMonth=false;
	private void calculateDate() {
		onStartMonth = false;
		//----------------------------设置时间-------------------------------------
		calendar.setTime(curDate);
		calendar.set(Calendar.DAY_OF_MONTH, 1); //为了记录42个位置的day，所以需要设置为这个月的1号

		//LogTools.d(TAG,"calculateDate---year="+calendar.get(Calendar.YEAR));
		//LogTools.d(TAG,"calculateDate---month="+(calendar.get(Calendar.MONTH)+1)); //month+1
		//LogTools.d(TAG,"calculateDate---day="+calendar.get(Calendar.DATE));
		//LogTools.d(TAG,"calculateDate---day1="+calendar.get(Calendar.DAY_OF_MONTH));
		//LogTools.d(TAG,"calculateDate---startWeekDay="+(calendar.get(Calendar.DAY_OF_WEEK)-1)); //表示本周的第几天，从周日开始计算
		//LogTools.d(TAG,"calculateDate---startYearDay="+calendar.get(Calendar.DAY_OF_YEAR)); //表示本年的第几天
		//LogTools.d(TAG,"calculateDate---endMonthDay="+calendar.getActualMaximum(Calendar.DATE)); //本月最后一天是

		//------------------------------------总共42个位置，判断哪个位置是上一个月的，哪个位置是这个月的，哪个位置是下个月的---------------------------------------------
		//----------------------------得到星期几-------------------------------------
		int dayInWeek = calendar.get(Calendar.DAY_OF_WEEK); //表示本周的第几天，从周日开始计算

		int monthStart = dayInWeek;
		if (monthStart == 1) {
			//monthStart = 8; //这个月的时间是从下一行开始算起
			onStartMonth = true;  //这个月的时间是从开始位置算起
		}
		monthStart -= 1;  //以日为开头-1，以星期一为开头-2
		curStartIndex = monthStart;  //得到当前是星期几

		date[monthStart] = 1;  //当前星期几置位
		//LogTools.e(TAG,"calculateDate---得到42个位置需要填写日期的开始位置---curStartIndex="+curStartIndex);

		//----------------------------last month-------------------------------------
		if (monthStart > 0) {
			calendar.set(Calendar.DAY_OF_MONTH, 0);  //设置为上个月的最后一天
			int dayInmonth = calendar.get(Calendar.DAY_OF_MONTH); //得到上个月的最后一天

			for (int i = monthStart - 1; i >= 0; i--) {   //在42个位置中填充上个月的day
				date[i] = dayInmonth;
				dayInmonth--;
			}
			calendar.set(Calendar.DAY_OF_MONTH, date[0]); //设置上个月的开始day,并且在42个位置的开始位置
		}
		showFirstDate = calendar.getTime();


		//----------------------------this month-------------------------------------
		// this month
		calendar.setTime(curDate);  //设置为当前时间
		calendar.add(Calendar.MONTH, 1); //月数+1
		calendar.set(Calendar.DAY_OF_MONTH, 0); //再次返回到当前时间对应的月的最后一天
		// calendar.get(Calendar.DAY_OF_MONTH));
		int monthDay = calendar.get(Calendar.DAY_OF_MONTH);  //得到这个月的最后一天

		for (int i = 1; i < monthDay; i++) {  //在42个位置中填充这个月的day
			date[monthStart + i] = i + 1;
		}
		curEndIndex = monthStart + monthDay;


		//----------------------------next month-------------------------------------
		// next month
		for (int i = monthStart + monthDay; i < 42; i++) {   //在42个位置中填充下个月的day
			date[i] = i - (monthStart + monthDay) + 1;
		}
		if (curEndIndex < 42) {
			// 显示了下一月的
			calendar.add(Calendar.DAY_OF_MONTH, 1);  //设置day+1，系统自动计算到下个月，也即下个月的1号
		}
		calendar.set(Calendar.DAY_OF_MONTH, date[41]); //设置时间到下个月的day,并且在42个位置的最后一个位置
		showLastDate = calendar.getTime();

	}

	/**
	 *
	 * @param canvas
	 * @param index
	 * @param text
	 */
	private void drawCellText(Canvas canvas, int index, String text, int color) {
		int x = getXByIndex(index);
		int y = getYByIndex(index);
		surface.datePaint.setColor(color);
		float cellY = surface.monthHeight + surface.weekHeight + (y - 1)
				* surface.cellHeight + surface.cellHeight * 3 / 4f;
		float cellX = (surface.cellWidth * (x - 1))
				+ (surface.cellWidth - surface.datePaint.measureText(text))
				/ 2f;
		canvas.drawText(text, cellX, cellY, surface.datePaint);
	}

	/**
	 *
	 * @param canvas
	 * @param index
	 * @param color
	 */
	private void drawCellBg(Canvas canvas, int index, int color) {
		int x = getXByIndex(index);
		int y = getYByIndex(index);
		surface.cellBgPaint.setColor(color);

		float left = surface.cellWidth * (x - 1) + surface.borderWidth;
		float top = surface.monthHeight + surface.weekHeight + (y - 1) * surface.cellHeight + surface.borderWidth;
		/*canvas.drawRect(left, top, left + surface.cellWidth- surface.borderWidth, top + surface.cellHeight - surface.borderWidth, surface.cellBgPaint);*/
		//背景画圆
		//canvas.drawCircle(left+surface.cellWidth/2-2, top + surface.cellHeight/2+3, 5*surface.cellWidth/14, surface.cellBgPaint);
		canvas.drawCircle(left+surface.cellWidth/2 + 1, top + surface.cellHeight/2+3, 5*surface.cellWidth/14, surface.cellBgPaint);
	}

	private void drawCurrentBg(Canvas canvas, int index, int color) {
		int x = getXByIndex(index);
		int y = getYByIndex(index);
		surface.cellBgPaint.setColor(color);

		float left = surface.cellWidth * (x - 1) + surface.borderWidth;
		float top = surface.monthHeight + surface.weekHeight + (y - 1) * surface.cellHeight + surface.borderWidth;
		/*canvas.drawRect(left, top, left + surface.cellWidth- surface.borderWidth, top + surface.cellHeight - surface.borderWidth, surface.cellBgPaint);*/
		//背景画圆
		//canvas.drawCircle(left+surface.cellWidth/2-2, top + surface.cellHeight/2+3, 5*surface.cellWidth/14, surface.cellBgPaint);
		//mRecoderBjBitmap
		int bit_width = mRecoderCuBitmap.getWidth();
		int bit_height = mRecoderCuBitmap.getHeight();
		//LogTools.e(TAG,"drawCurrentBg---bit_width="+bit_width+", bit_height="+bit_height);
		canvas.drawBitmap(mRecoderCuBitmap,(left+surface.cellWidth/2 + 1)-(bit_width/2), (top + surface.cellHeight/2+3)-(bit_height/2),surface.cellBgPaint);
		//canvas.drawCircle(left+surface.cellWidth/2 + 1, top + surface.cellHeight/2+3, 5*surface.cellWidth/14, surface.cellBgPaint);
	}

	private void drawRecoderBg(Canvas canvas, int index, int color) {
		int x = getXByIndex(index);
		int y = getYByIndex(index);
		surface.cellBgPaint.setColor(color);

		float left = surface.cellWidth * (x - 1) + surface.borderWidth;
		float top = surface.monthHeight + surface.weekHeight + (y - 1) * surface.cellHeight + surface.borderWidth;
		/*canvas.drawRect(left, top, left + surface.cellWidth- surface.borderWidth, top + surface.cellHeight - surface.borderWidth, surface.cellBgPaint);*/
		//背景画圆
		//canvas.drawCircle(left+surface.cellWidth/2-2, top + surface.cellHeight/2+3, 5*surface.cellWidth/14, surface.cellBgPaint);
		//mRecoderBjBitmap
		int bit_width = mRecoderBjBitmap.getWidth();
		int bit_height = mRecoderBjBitmap.getHeight();
		//LogTools.e(TAG,"drawCurrentBg---bit_width="+bit_width+", bit_height="+bit_height);
		canvas.drawBitmap(mRecoderBjBitmap,(left+surface.cellWidth/2 + 1)-(bit_width/2), (top + surface.cellHeight/2+3)-(bit_height/2),surface.cellBgPaint);
		//canvas.drawCircle(left+surface.cellWidth/2 + 1, top + surface.cellHeight/2+3, 5*surface.cellWidth/14, surface.cellBgPaint);
	}

	private void drawDownOrSelectedBg(Canvas canvas) {
		// down and not up
		if (downDate != null) {
			drawCellBg(canvas, downIndex, surface.cellDownColor);
		}
		/*
		// selected bg color
		if (!selectedEndDate.before(showFirstDate) && !selectedStartDate.after(showLastDate)) {
			int[] section = new int[] { -1, -1 };
			calendar.setTime(curDate);  //设置成当前日期
			calendar.add(Calendar.MONTH, -1); //设置到上个月
			findSelectedIndex(0, curStartIndex, calendar, section);
			if (section[1] == -1) {
				calendar.setTime(curDate);
				findSelectedIndex(curStartIndex, curEndIndex, calendar, section);
			}
			if (section[1] == -1) {
				calendar.setTime(curDate);
				calendar.add(Calendar.MONTH, 1);
				findSelectedIndex(curEndIndex, 42, calendar, section);
			}
			if (section[0] == -1) {
				section[0] = 0;
			}
			if (section[1] == -1) {
				section[1] = 41;
			}
			for (int i = section[0]; i <= section[1]; i++) {
				//drawCellBg(canvas, i, surface.cellSelectedColor);
			}
		}
		*/
	}

	private void findSelectedIndex(int startIndex, int endIndex,
								   Calendar calendar, int[] section) {
		for (int i = startIndex; i < endIndex; i++) {
			calendar.set(Calendar.DAY_OF_MONTH, date[i]);
			Date temp = calendar.getTime();
			// LogTools.d(TAG, "temp:" + temp.toLocaleString());
			if (temp.compareTo(selectedStartDate) == 0) {
				section[0] = i;
			}
			if (temp.compareTo(selectedEndDate) == 0) {
				section[1] = i;
				return;
			}
		}
	}

	public Date getSelectedStartDate() {
		return selectedStartDate;
	}

	public Date getSelectedEndDate() {
		return selectedEndDate;
	}

	private boolean isLastMonth(int i) {
		if (i < curStartIndex) {
			return true;
		}
		return false;
	}

	private boolean isNextMonth(int i) {
		if (i >= curEndIndex) {
			return true;
		}
		return false;
	}

	private int getXByIndex(int i) {
		return i % 7 + 1; // 1 2 3 4 5 6 7
	}

	private int getYByIndex(int i) {
		return i / 7 + 1; // 1 2 3 4 5 6
	}

	// 获得当前应该显示的年月
	public String getYearAndmonth() {
		calendar.setTime(curDate);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		return year + "-" + month;
	}

	public void setCurrentMonth(String currentOneMonth){
		this.currentOneMonth = currentOneMonth;
		invalidate();
	}

	//上一月
	public String clickLeftMonth(){
		calendar.setTime(curDate);
		calendar.add(Calendar.MONTH, -1);
		curDate = calendar.getTime();
		//invalidate();
		return getYearAndmonth();
	}
	//下一月
	public String clickRightMonth(){
		calendar.setTime(curDate);
		calendar.add(Calendar.MONTH, 1);
		curDate = calendar.getTime();
		//invalidate();
		return getYearAndmonth();
	}

	//设置日历时间
	public void setCalendarData(Date date,int day){
		calendar.setTime(date);
		curDate = calendar.getTime();
		currentDay = day;
		//LogTools.e(TAG,"shezhi---setCalendarData---this.currentDay="+currentDay);
		invalidate();
	}

	//获取日历时间
	public void getCalendatData(){
		calendar.getTime();
	}

	//设置是否多选
	public boolean isSelectMore() {
		return isSelectMore;
	}

	public void setSelectMore(boolean isSelectMore) {
		this.isSelectMore = isSelectMore;
	}

	private void setSelectedDateByCoor(float x, float y) {
		// change month
//		if (y < surface.monthHeight) {
//			// pre month
//			if (x < surface.monthChangeWidth) {
//				calendar.setTime(curDate);
//				calendar.add(Calendar.MONTH, -1);
//				curDate = calendar.getTime();
//			}
//			// next month
//			else if (x > surface.width - surface.monthChangeWidth) {
//				calendar.setTime(curDate);
//				calendar.add(Calendar.MONTH, 1);
//				curDate = calendar.getTime();
//			}
//		}
		// cell click down
		if (y > surface.monthHeight + surface.weekHeight) {
			int m = (int) (Math.floor(x / surface.cellWidth) + 1);
			int n = (int) (Math.floor((y - (surface.monthHeight + surface.weekHeight)) / Float.valueOf(surface.cellHeight)) + 1);
			int downIndexS = (n - 1) * 7 + m - 1;
			//LogTools.e(TAG,"setSelectedDateByCoor---得到点击区域值---downIndexS="+downIndexS);
			//LogTools.d(TAG, "downIndex:" + downIndex);

			if (isLastMonth(downIndexS)) {
				//calendar.add(Calendar.MONTH, -1);
				//LogTools.e(TAG,"setSelectedDateByCoor---点击到了上个月---downIndex="+downIndex);
				return;
			} else if (isNextMonth(downIndexS)) {
				//calendar.add(Calendar.MONTH, 1);
				//LogTools.e(TAG,"setSelectedDateByCoor---点击到了下个月---downIndex="+downIndex);
				return;
			}

			downIndex=downIndexS;
			//calendar.setTime(curDate);
			//calendar.set(Calendar.DAY_OF_MONTH, date[downIndex]);
			downDate = calendar.getTime();
			//LogTools.e(TAG,"setSelectedDateByCoor---得到点击时间downDate="+downDate);
		}
		//invalidate();
	}

	public void setLockTouch(boolean lock){
		if(lock==true){
			lockTouch=true;
		}else {
			lockTouch = false;
		}
	}

	private int mLastX;
	private int mLastY;
	private int moffsetX;
	private int moffsetY;
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		//LogTools.d(TAG,"onTouch---status="+event.getAction());
		if(lockTouch==false) {
			int x = (int) event.getRawX();
			int y = (int) event.getRawY();
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					//LogTools.e(TAG, "onTouch---ACTION_DOWN");
					mLastX = x;
					mLastY = y;
					setSelectedDateByCoor(event.getX(), event.getY());
					break;
				case MotionEvent.ACTION_MOVE:
					//LogTools.e(TAG, "onTouch---ACTION_MOVE");
					//moffsetX = x - mLastX;
					//moffsetY = y - mLastY;
					//layout(getLeft() + offsetX, getTop() + offsetY, getRight() + offsetX, getBottom() + offsetY);
					//重新设置初始坐标
					//mLastX = x;
					//mLastY = y;
					break;
				case MotionEvent.ACTION_UP:
					//LogTools.e(TAG, "onTouch---ACTION_UP---mLastX="+mLastX);
					//LogTools.e(TAG, "onTouch---ACTION_UP---moffsetX="+moffsetX);
					if (x - mLastX > 0 && (Math.abs(x - mLastX) > 200)) {
						downIndex=-1;
						//mLastX=x;
						//mLastY=y;
						//向右
						onItemClickListenerString.moveTouchLeft();
					} else if (x - mLastX < 0 && (Math.abs(x - mLastX) > 200)) {
						downIndex=-1;
						//mLastX=x;
						//mLastY=y;
						//向左
						onItemClickListenerString.moveTouchRight();
					}else{
						if (downDate != null) {
							//LogTools.e(TAG, "onTouch---ACTION_UP");
							if (isSelectMore) {
								if (!completed) {
									if (downDate.before(selectedStartDate)) {
										selectedEndDate = selectedStartDate;
										selectedStartDate = downDate;
									} else {
										selectedEndDate = downDate;
									}
									completed = true;
									//响应监听事件
									onItemClickListener.OnItemClick(selectedStartDate, selectedEndDate, downDate);
								} else {
									selectedStartDate = selectedEndDate = downDate;
									completed = false;
								}
							} else {
								//selectedStartDate = selectedEndDate = downDate;
								//响应监听事件
								//onItemClickListener.OnItemClick(selectedStartDate,selectedEndDate,downDate);
							}
						}
					}
					invalidate();

					break;
			}
		}
		return true;
	}

	//给控件设置监听事件
	public void setOnItemClickListener(OnItemClickListener onItemClickListener){
		this.onItemClickListener =  onItemClickListener;
	}
	//监听接口
	public interface OnItemClickListener {
		void OnItemClick(Date selectedStartDate,Date selectedEndDate, Date downDate);
	}
	//给控件设置监听事件
	public void setOnItemClickListenerString(OnItemClickListenerString onItemClickListenerString){
		this.onItemClickListenerString =  onItemClickListenerString;
	}
	//监听接口
	public interface OnItemClickListenerString{
		void OnItemClick(String year,String month, int data);
		void moveTouchRight();
		void moveTouchLeft();
		void logcatStart();
	}

	/**
	 *
	 * 1. 布局尺寸 2. 文字颜色，大小 3. 当前日期的颜色，选择的日期颜色
	 */
	private class Surface {
		public float density;
		public int width; // 整个控件的宽度
		public int height; // 整个控件的高度
		public float monthHeight; // 显示月的高度
		//public float monthChangeWidth; // 上一月、下一月按钮宽度
		public float weekHeight; // 显示星期的高度
		public float cellWidth; // 日期方框宽度
		public float cellHeight; // 日期方框高度	
		public float borderWidth;
		//整个日历的背景颜色
		public int bgColor = Color.parseColor("#FFFFFF");
		//设置背景图片
		//public Drawable bgDrawable =getResources().getDrawable(R.drawable.ic_calendar_circle);

		//本月有效日的颜色
		private int textColor = Color.parseColor("#E6E6E6");

		//private int textColorUnimportant = Color.parseColor("#666666");

		private int btnColor = Color.parseColor("#FF0000");
		//不是当月日的文字颜色
		private int borderColor = Color.parseColor("#00000000");

		//当前日期的文字颜色
		public int todayNumberColor = Color.parseColor("#50C7FA");
		//public int todayNumberColor = Color.RED;

		public int validNumberColor = Color.parseColor("#555555");

		//格子按下时的颜色（按下时的那一刻）
		public int cellDownColor = Color.parseColor("#FFFFFF");
		//格子选中的颜色
		public int cellSelectedColor = Color.parseColor("#50C7FA");

		public int black = Color.parseColor("#000000");

		public Paint borderPaint;
		public Paint monthPaint;
		public Paint weekPaint;
		public Paint datePaint;
		public Paint monthChangeBtnPaint;
		public Paint cellBgPaint;
		public Path boxPath; // 边框路径
		//public Path preMonthBtnPath; // 上一月按钮三角形
		//public Path nextMonthBtnPath; // 下一月按钮三角形

		//周 头
		public String[] weekText;
		//public String[] monthText = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

		public void init() {

			if(true){
				weekText  = new String[]{"日","一", "二", "三", "四", "五", "六"};
			}else{
				weekText  = new String[]{"Sun","Mon", "Tues", "Wed", "Thur", "Fri", "Sat"};
			}
//			weekText  = new String[]{getResources().getString(R.string.plug_seven),
//									 getResources().getString(R.string.plug_one),
//					                 getResources().getString(R.string.plug_two),
//									 getResources().getString(R.string.plug_three),
//									 getResources().getString(R.string.plug_four),
//					                 getResources().getString(R.string.plug_five),
//					                 getResources().getString(R.string.plug_six)};

			float temp = height / 7f;
			monthHeight = 0;//(float) ((temp + temp * 0.3f) * 0.6);
			//monthChangeWidth = monthHeight * 1.5f;
			//周的高度
			weekHeight = (float) ((temp + temp * 0.3f) * 0.9);

			cellHeight = (height - monthHeight - weekHeight) / 6f;
			cellWidth = width / 7f;


			borderPaint = new Paint();
			borderPaint.setColor(Color.parseColor("#CCCCCC"));
			borderPaint.setStyle(Paint.Style.STROKE);
			borderWidth = (float) (0.5 * density);
			// LogTools.d(TAG, "borderwidth:" + borderWidth);
			borderWidth = borderWidth < 1 ? 1 : borderWidth;
			borderPaint.setStrokeWidth(borderWidth);


			monthPaint = new Paint();
			monthPaint.setColor(textColor);
			monthPaint.setAntiAlias(true);
			float textSize = cellHeight * 0.4f;

			monthPaint.setTextSize(textSize);
			monthPaint.setTypeface(Typeface.DEFAULT_BOLD);


			weekPaint = new Paint();
			//上面一周的文字颜色
			weekPaint.setColor(Color.parseColor("#727270"));
			weekPaint.setAntiAlias(true);
			//周的字体大小
			float weekTextSize = weekHeight * 0.3f;
			weekPaint.setTextSize(weekTextSize);
			weekPaint.setTypeface(Typeface.DEFAULT);


			datePaint = new Paint();
			datePaint.setColor(textColor);
			datePaint.setAntiAlias(true);
			float cellTextSize = cellHeight * 0.5f;
			datePaint.setTextSize(cellTextSize);
			datePaint.setTypeface(Typeface.DEFAULT_BOLD);


			boxPath = new Path();
			//boxPath.addRect(0, 0, width, height, Direction.CW);
			//boxPath.moveTo(0, monthHeight);
			/*boxPath.rLineTo(width, 0);*/
			boxPath.moveTo(0, monthHeight + weekHeight);
			boxPath.rLineTo(width, 0);

			//下面具体日期的格子边框
			/*for (int i = 1; i < 6; i++) {
				boxPath.moveTo(0, monthHeight + weekHeight + i * cellHeight);
				boxPath.rLineTo(width, 0);
				boxPath.moveTo(i * cellWidth, monthHeight);
				boxPath.rLineTo(0, height - monthHeight);
			}
			boxPath.moveTo(6 * cellWidth, monthHeight);
			boxPath.rLineTo(0, height - monthHeight);*/

			//原来就注释的代码
			//preMonthBtnPath = new Path();
			//int btnHeight = (int) (monthHeight * 0.6f);
			//preMonthBtnPath.moveTo(monthChangeWidth / 2f, monthHeight / 2f);
			//preMonthBtnPath.rLineTo(btnHeight / 2f, -btnHeight / 2f);
			//preMonthBtnPath.rLineTo(0, btnHeight);
			//preMonthBtnPath.close();
			//nextMonthBtnPath = new Path();
			//nextMonthBtnPath.moveTo(width - monthChangeWidth / 2f,
			//		monthHeight / 2f);
			//nextMonthBtnPath.rLineTo(-btnHeight / 2f, -btnHeight / 2f);
			//nextMonthBtnPath.rLineTo(0, btnHeight);
			//nextMonthBtnPath.close();
			monthChangeBtnPaint = new Paint();
			monthChangeBtnPaint.setAntiAlias(true);
			monthChangeBtnPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			monthChangeBtnPaint.setColor(btnColor);

			//单元格背景
			cellBgPaint = new Paint();
			cellBgPaint.setAntiAlias(true);
			cellBgPaint.setStyle(Paint.Style.STROKE);
			cellBgPaint.setStrokeWidth(4);
			cellBgPaint.setColor(cellSelectedColor);
		}
	}
}
