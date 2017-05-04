package com.justyan.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import com.justyan.library.R;
import com.justyan.library.utils.CalendarUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;

import static com.justyan.library.utils.CalendarUtil.getWeekFirstDay;


/**
 * yanyi on 16/7/11.
 * 日历控件的单页view，包含了月份和星期浏览模式
 */
public class CKCalendarPagerItemView extends View {
    private static final String TAG = "CKCalendarView";
    //用来表示当前view的状态，true 收起状态，表明是星期浏览模式，false 展开状态，表明是月份浏览模式
    private boolean isShrink;
    //是否可以收起
    public boolean canShrink = true;
    private int titleTextColor;
    private int dividerStroke;
    private int mMonthTitleTextSize;
    private int defaultDayTextColor;
    private int invalidDayTextColor;
    private int selectedDayTextColor;
    private int weekTitleTextColor;
    private int todayBg;
    private int selectedDayBg;
    private int headerBg;
    private int weekTitleTextSize;
    private int dayTextSize;
    private int dividerColor;
    private int selectDayStroke;
    private int selectDayStokeColor;
    private int todayTextColor;

    public CKCalendarPagerItemView(Context context) {
        super(context);
        init(null);
    }

    public CKCalendarPagerItemView(Context context, @NonNull AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CKCalendarPagerItemView(Context context, @NonNull AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    /**
     * see {@link SimpleDateFormat}
     * <tr> <td>{@code E}</td> <td>day of week</td>             <td>(Text)</td>        <td>{@code E}/{@code EE}/{@code EEE}:Tue, {@code EEEE}:Tuesday, {@code EEEEE}:T</td> </tr>
     */
    public void init(AttributeSet attrs) {

        try {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CKCalendarPagerItemView, 0, R.style.CKMonthView);

            //是否可以收起
            canShrink = typedArray.getBoolean(R.styleable.CKCalendarPagerItemView_shrinkable, true);

            //月标题高度
            mDesiredMonthTitleHeight = typedArray.getDimensionPixelSize(R.styleable.CKCalendarPagerItemView_desiredMonthTitleHeight, 40);
            //星期标题高度
            mDesiredDayOfWeekHeight = typedArray.getDimensionPixelSize(R.styleable.CKCalendarPagerItemView_desiredDayOfWeekHeight, 36);
            //日期高度
            mDesiredDayHeight = typedArray.getDimensionPixelSize(R.styleable.CKCalendarPagerItemView_desiredDayHeight, 32);
            //日期宽度
            mDesiredCellWidth = typedArray.getDimensionPixelSize(R.styleable.CKCalendarPagerItemView_desiredCellWidth, 46);

            //月标题字大小
            mMonthTitleTextSize = typedArray.getDimensionPixelSize(R.styleable.CKCalendarPagerItemView_monthTitleTextSize, 14);
            //星期标题字大小
            weekTitleTextSize = typedArray.getDimensionPixelSize(R.styleable.CKCalendarPagerItemView_weekTitleTextSize, 12);
            //日期字大小
            dayTextSize = typedArray.getDimensionPixelSize(R.styleable.CKCalendarPagerItemView_dayTextSize, 12);
            //选中日期的边框宽度
            selectDayStroke = typedArray.getDimensionPixelSize(R.styleable.CKCalendarPagerItemView_selectDayStoke, 4);
            //月标题字体颜色
            titleTextColor = typedArray.getColor(R.styleable.CKCalendarPagerItemView_monthTitleTextColor, Color.BLACK);
            //默认日期字体颜色
            defaultDayTextColor = typedArray.getColor(R.styleable.CKCalendarPagerItemView_defaultDayTextColor, Color.BLACK);
            //无效日期字体颜色
            invalidDayTextColor = typedArray.getColor(R.styleable.CKCalendarPagerItemView_invalidDayTextColor, Color.BLACK);
            //选中日期字体颜色
            selectedDayTextColor = typedArray.getColor(R.styleable.CKCalendarPagerItemView_selectDayTextColor, Color.BLACK);
            //星期标题字体颜色
            weekTitleTextColor = typedArray.getColor(R.styleable.CKCalendarPagerItemView_weekTitleTextColor, Color.BLACK);
            //今天日期背景
            todayBg = typedArray.getColor(R.styleable.CKCalendarPagerItemView_todayBg, Color.BLUE);
            //今天日期字体颜色
            todayTextColor = typedArray.getColor(R.styleable.CKCalendarPagerItemView_todayTextColor, Color.WHITE);
            //选中日期背景色
            selectedDayBg = typedArray.getColor(R.styleable.CKCalendarPagerItemView_selectDayBg, Color.BLACK);
            //顶部背景色
            headerBg = typedArray.getColor(R.styleable.CKCalendarPagerItemView_headerBg, Color.GRAY);
            //显示分割线
            showDivider = typedArray.getBoolean(R.styleable.CKCalendarPagerItemView_showDivider, true);
            //分割线宽度
            dividerStroke = typedArray.getDimensionPixelSize(R.styleable.CKCalendarPagerItemView_dividerStroke, 1);
            //分割线颜色
            dividerColor = typedArray.getColor(R.styleable.CKCalendarPagerItemView_dividerColor, Color.GRAY);
            //选中日期边框颜色
            selectDayStokeColor = typedArray.getColor(R.styleable.CKCalendarPagerItemView_selectDayStokeColor, Color.BLACK);
            //星期显示格式
            String weekTitleFormat = typedArray.getString(R.styleable.CKCalendarPagerItemView_weekTitleFormat);
            if (TextUtils.isEmpty(weekTitleFormat)) {

                weekTitleFormat = "EEE";
            }

            //月标题显示格式
            String titleFormat = typedArray.getString(R.styleable.CKCalendarPagerItemView_titleFormatter);
            if (TextUtils.isEmpty(titleFormat)) {
                titleFormat = "yyyy.MM";
            }

            //日期显示格式
            String dayFormat = typedArray.getString(R.styleable.CKCalendarPagerItemView_dayFormatter);
            if (TextUtils.isEmpty(dayFormat)) {
                dayFormat = "d";
            }

            //选中日期的背景形状
            selectedBgShape = typedArray.getInt(R.styleable.CKCalendarPagerItemView_selectedBgShape, SHAPE_RECT);
            //周模式下是否显示星期标题
            showWeekTitleWhenShrink = typedArray.getBoolean(R.styleable.CKCalendarPagerItemView_showWeekTitleWhenShrink, true);

            titleFormatter = new SimpleDateFormat(titleFormat, Locale.getDefault());
            mDayOfWeekFormatter = new SimpleDateFormat(weekTitleFormat, Locale.getDefault());
            mDayFormatter = new SimpleDateFormat(dayFormat, Locale.getDefault());
            typedArray.recycle();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "init CKCalendarPagerItemView exception is  " + e.getMessage());
        }
        initPaint();
    }

    private boolean showWeekTitleWhenShrink;
    private int selectedBgShape;
    public static final int SHAPE_RECT = 0;
    public static final int SHAPE_CIRCULAR = 1;

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mMonthTitlePaint.setAntiAlias(true);
        mMonthTitlePaint.setTextSize(mMonthTitleTextSize);
//      mMonthTitlePaint.setTypeface(Typeface.create(monthTypeface, 0));
        mMonthTitlePaint.setTextAlign(Paint.Align.CENTER);
        mMonthTitlePaint.setColor(titleTextColor);
        //设置粗体
//      mMonthTitlePaint.setFakeBoldText(true);
        mMonthTitlePaint.setStyle(Paint.Style.FILL);

        mDayOfWeekPaint.setAntiAlias(true);
        mDayOfWeekPaint.setTextSize(weekTitleTextSize);
        mDayOfWeekPaint.setColor(weekTitleTextColor);
//      mDayOfWeekPaint.setTypeface(Typeface.create(dayOfWeekTypeface, 0));
        mDayOfWeekPaint.setTextAlign(Paint.Align.CENTER);
        mDayOfWeekPaint.setStyle(Paint.Style.FILL);


        mDaySelectorPaint.setAntiAlias(true);
        mDaySelectorPaint.setColor(selectDayStokeColor);
        mDaySelectorPaint.setColor(selectedDayBg);
        mDaySelectorPaint.setStyle(Paint.Style.FILL);

        mTodayPaint.setAntiAlias(true);
        mTodayPaint.setColor(todayBg);
        mTodayPaint.setStrokeWidth(selectDayStroke);
        mTodayPaint.setStyle(Paint.Style.STROKE);


        dividerPaint.setAntiAlias(true);
        dividerPaint.setColor(dividerColor);
        dividerPaint.setStrokeWidth(dividerStroke);
        dividerPaint.setStyle(Paint.Style.FILL);

        mDayPaint.setAntiAlias(true);
        mDayPaint.setTextSize(dayTextSize);
        mDayPaint.setColor(defaultDayTextColor);
//      mDayPaint.setTypeface(Typeface.create(dayTypeface, 0));
        mDayPaint.setTextAlign(Paint.Align.CENTER);
        mDayPaint.setStyle(Paint.Style.FILL);


        headerBgPaint.setAntiAlias(true);
        headerBgPaint.setStyle(Paint.Style.FILL);
        headerBgPaint.setColor(headerBg);
    }

    public static int WEEK_START_DAY = Calendar.MONDAY;

    public int getMonthTitleHeight() {
        return mMonthTitleHeight;
    }

    public float getCellWidth() {
        return mCellWidth;
    }

    private Calendar minCalendar, maxCalendar;


    /**
     * 设置需要的参数，外部传入
     *
     * @param isShrink       是否是收起状态
     * @param curCalendar    当前的日期
     * @param selectCalendar 已经选中的日期
     * @param minCalendar    最小的有效日期（包含）
     * @param maxCalendar    最大的有效日期（包含）
     */
    public void setMonthViewParams(boolean isShrink, Calendar curCalendar, Calendar selectCalendar, Calendar minCalendar, Calendar maxCalendar) {
        this.isShrink = isShrink;
        this.selectCalendar = selectCalendar;
        mCalendar = curCalendar;
        mDaysInMonth = getMaxDaysInMonth();
        this.minCalendar = minCalendar;
        this.maxCalendar = maxCalendar;
        requestLayout();
    }

    //用来表示当前的日期，如果是月份模式的话就是该月的第一天，如果是星期模式的话就是该星期的最后一天
    private Calendar mCalendar = Calendar.getInstance();
    //当前选中的日期
    private Calendar selectCalendar = Calendar.getInstance();

    private int getMaxDaysInMonth() {
        return mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    //真实的view高度和宽度（减去了padding）
    int mPaddedWidth, mPaddedHeight;
    //默认要求的标题高度
    int mDesiredMonthTitleHeight;
    //默认要求的星期标题高端
    int mDesiredDayOfWeekHeight;

    int mDesiredDayHeight;
    int mDesiredCellWidth;
    //单个日期的宽度  通过view的真实宽度/7得出
    float mCellWidth;
    //顶部标题，星期标题，单个日期的高度
    int mMonthTitleHeight, mDayOfWeekHeight, mDayHeight;
    //当天
    private Calendar todayCalendar = Calendar.getInstance();

    /**
     * 先获取位置
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (!changed) {
            return;
        }
        //计算真实的总宽度和总高度
        int paddedWidth = right - left - getPaddingLeft() - getPaddingRight();
        int paddedHeight = bottom - top - getPaddingTop() - getPaddingBottom();
        if (paddedWidth == mPaddedWidth || paddedHeight == mPaddedHeight) {
            return;
        }
        mPaddedWidth = paddedWidth;
        mPaddedHeight = paddedHeight;
        //计算缩放值
        int measuredPaddedHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        float scaleH = paddedHeight / (float) measuredPaddedHeight;
        mMonthTitleHeight = (int) (mDesiredMonthTitleHeight * scaleH);
        mDayOfWeekHeight = (int) (mDesiredDayOfWeekHeight * scaleH);
        mDayHeight = (int) (mDesiredDayHeight * scaleH);
        //这里要注意是float的，如果是int会因为精度问题影响最后的划线的位置
        mCellWidth = paddedWidth / (long) MAX_WEEK_DAY;
    }

    //每周的天数
    public static final int MAX_WEEK_DAY = 7;

    /**
     * 把布局画上
     * 这里是整个view的关键
     */
    @Override
    protected void onDraw(Canvas canvas) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        canvas.translate(paddingLeft, paddingTop);
        //顶部标题
        drawMonthTitle(canvas);
        //顶部的星期标题
        drawDayOfWeek(canvas);
        //核心的日期
        drawDays(canvas);
        //分割线
        if (showDivider) {
            drawDivider(canvas);
        }
        canvas.translate(-paddingLeft, -paddingTop);
    }

    private boolean showDivider = true;

    private Paint mDaySelectorPaint = new Paint();
    private Paint mTodayPaint = new Paint();
    private final TextPaint mMonthTitlePaint = new TextPaint();
    private final TextPaint mDayOfWeekPaint = new TextPaint();
    private final TextPaint mDayPaint = new TextPaint();
    private final Paint dividerPaint = new Paint();
    private final Paint headerBgPaint = new Paint();


    private String getTitle() {
        return titleFormatter.format(mCalendar.getTime());
    }


    //画顶部的标题
    private void drawMonthTitle(Canvas canvas) {
        if (isShrink) {
            return;
        }
        float x = mPaddedWidth / 2f;
        //字体的上下边界
        float lineHeight = mMonthTitlePaint.ascent() + mMonthTitlePaint.descent();
        float y = (mMonthTitleHeight - lineHeight) / 2f;
        //整行
        canvas.drawRect(0, 0, mPaddedWidth, mMonthTitleHeight, headerBgPaint);
        //标题文字
        canvas.drawText(getTitle(), x, y, mMonthTitlePaint);
    }

    //画星期标题
    private void drawDayOfWeek(Canvas canvas) {
        if (isShrink && !showWeekTitleWhenShrink) {
            return;
        }
        float lineHeight = mDayOfWeekPaint.ascent() + mDayOfWeekPaint.descent();
        //高度需要加上顶上的标题
        float y = mMonthTitleHeight + mDayOfWeekHeight / 2 - (lineHeight) / 2f;
        if (isShrink) {
            y = mDayOfWeekHeight / 2 - (lineHeight) / 2f;
        }
        canvas.drawRect(0, mMonthTitleHeight, mPaddedWidth, mDayOfWeekHeight + mMonthTitleHeight, headerBgPaint);
        for (int col = 0; col < MAX_WEEK_DAY; col++) {
            float x = mCellWidth / 2 + col * mCellWidth;
            int dayOfWeek = (col + WEEK_START_DAY) % MAX_WEEK_DAY;
            String label = getDayOfWeekLabel(dayOfWeek);
            canvas.drawText(label, x, y, mDayOfWeekPaint);
        }
    }

    private Calendar mDayOfWeekLabelCalendar = Calendar.getInstance();
    //周标题显示格式
    private SimpleDateFormat mDayOfWeekFormatter;
    //顶部标题显示格式
    private SimpleDateFormat titleFormatter;

    private SimpleDateFormat mDayFormatter;
    private SimpleDateFormat mFirstDayFormatter = new SimpleDateFormat("M月/d", Locale.getDefault());

    /**
     * 格式化顶部星期标题
     *
     * @param dayOfWeek 星期几
     */
    private String getDayOfWeekLabel(int dayOfWeek) {
        mDayOfWeekLabelCalendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        String result = mDayOfWeekFormatter.format(mDayOfWeekLabelCalendar.getTime());
        try {
            if (result != null && result.length() > 3) {
                return result.substring(0, 1);
            }
        } catch (Exception ignored) {
        }
        return result;
    }

    private boolean isSelectDay(Calendar calendar) {
        return CalendarUtil.isSameDay(calendar, selectCalendar);
    }

    private int mDaysInMonth;
    //如果是每月的第一天显示格式类似 4月/1
    public boolean showCurrentMonthAtFirstDay = true;


    //画具体的天数
    private void drawDays(Canvas canvas) {
        int headerHeight = mMonthTitleHeight + mDayOfWeekHeight;
        if (isShrink) {
            if (showWeekTitleWhenShrink) {
                headerHeight = mDayOfWeekHeight;
            } else {
                headerHeight = 0;
            }

        }
        int y = headerHeight + mDayHeight / 2;
        float fontHeight = (mDayPaint.ascent() + mDayPaint.descent()) / 2f;
        if (isShrink) {
            Calendar calendar = getWeekFirstDay(mCalendar, WEEK_START_DAY);
            for (int col = 0; col < MAX_WEEK_DAY; col++) {
                float x = mCellWidth / 2 + mCellWidth * col;
                float tempY = y - fontHeight;
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                showDayState(canvas, calendar, y, col);
                if (showCurrentMonthAtFirstDay && day == 1) {
                    canvas.drawText(mFirstDayFormatter.format(calendar.getTime()), x, tempY, mDayPaint);
                } else {
                    canvas.drawText(mDayFormatter.format(calendar.getTime()), x, tempY, mDayPaint);
                }
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        } else {
            Calendar calendar = CalendarUtil.cloneCalendar(mCalendar);
            for (int day = 1, col = findDayOffset(); day <= mDaysInMonth; day++) {
                calendar.set(Calendar.DAY_OF_MONTH, day);
                float x = mCellWidth / 2 + mCellWidth * col;
                float tempY = y - fontHeight;
                showDayState(canvas, calendar, y, col);
                canvas.drawText(mDayFormatter.format(calendar.getTime()), x, tempY, mDayPaint);
                col++;
                if (col == MAX_WEEK_DAY) {
                    col = 0;
                    y = y + mDayHeight;
                }
            }
        }
    }

    //本月含有事件的天数
    private HashSet<String> hasEventSet;

    public void setHasEventSet(HashSet<String> hasEventSet) {
        this.hasEventSet = (hasEventSet == null ? new HashSet<String>() : hasEventSet);
        invalidate();
    }

    //用来和服务器返回的日期格式匹配
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    /**
     * 显示日期的状态
     * 比如选中，当天，或者滑动效果,当天有无事件等
     *
     * @param canvas   画布
     * @param calendar day日期
     * @param y        y坐标
     * @param col      列数
     */
    private void showDayState(Canvas canvas, Calendar calendar, int y, int col) {
        if (CalendarUtil.isCalendarOverRange(calendar, minCalendar, maxCalendar)) {
            //越界天数
            mDayPaint.setColor(invalidDayTextColor);
        } else if (isSelectDay(calendar)) {
            //选中天
            if (selectedBgShape == SHAPE_CIRCULAR) {
                canvas.drawCircle(mCellWidth * col + mCellWidth / 2, y, mDayHeight / 2, mDaySelectorPaint);
            } else {
                canvas.drawRect(mCellWidth * col, y - mDayHeight / 2, mCellWidth * (col + 1), y - mDayHeight / 2 + mDayHeight, mDaySelectorPaint);
            }
            mDayPaint.setColor(selectedDayTextColor);
        } else if (CalendarUtil.isSameDay(calendar, todayCalendar)) {
            //今天
            if (selectedBgShape == SHAPE_CIRCULAR) {
                canvas.drawCircle(mCellWidth * col + mCellWidth / 2, y, mDayHeight / 2 - 2, mTodayPaint);
            } else {
                canvas.drawRect(mCellWidth * col, y - mDayHeight / 2, mCellWidth * (col + 1), y - mDayHeight / 2 + mDayHeight, mTodayPaint);
            }
            mDayPaint.setColor(todayTextColor);
        } else if (hasEventSet != null && hasEventSet.contains(simpleDateFormat.format(calendar.getTime()))) {
            //当前天有任务
            mDayPaint.setColor(selectDayStokeColor);
        } else {
            //默认
            mDayPaint.setColor(defaultDayTextColor);
        }
    }

    /**
     * 画分割线
     */
    private void drawDivider(Canvas canvas) {
        int headerHeight = mMonthTitleHeight + mDayOfWeekHeight;
        if (isShrink) {
            headerHeight = 0;
        }
        int tempRow = findDayOffset() + mDaysInMonth;
        int row = tempRow % MAX_WEEK_DAY == 0 ? tempRow / MAX_WEEK_DAY : tempRow / MAX_WEEK_DAY + 1;
        if (isShrink) {
            row = 1;
        }
        for (int i = 0; i <= MAX_WEEK_DAY; i++) {
            //竖线
            canvas.drawLine(mCellWidth * i, headerHeight, mCellWidth * i, headerHeight + mDayHeight * row, dividerPaint);
            if (i <= row) {
                //横线
                canvas.drawLine(0, headerHeight + (i) * mDayHeight, mPaddedWidth, headerHeight + (i) * mDayHeight, dividerPaint);
            }
        }
    }

    /**
     * 计算每月开始的偏移量，比如1号是周三那么就需要偏移3天
     */
    private int findDayOffset() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mCalendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int mCurWeekDay = calendar.get(Calendar.DAY_OF_WEEK);
        int offset = mCurWeekDay - WEEK_START_DAY;
        if (offset < 0) {
            offset = offset + MAX_WEEK_DAY;
        }
        return offset;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int preferredHeight = mDesiredDayHeight * 6
                + mDesiredDayOfWeekHeight + mDesiredMonthTitleHeight
                + getPaddingTop() + getPaddingBottom();
        if (isShrink) {
            if (showWeekTitleWhenShrink) {
                preferredHeight = mDesiredDayOfWeekHeight + mDesiredDayHeight
                        + getPaddingTop() + getPaddingBottom();

            } else {

                preferredHeight = mDesiredDayHeight
                        + getPaddingTop() + getPaddingBottom();
            }
        }
        final int preferredWidth = mDesiredCellWidth * MAX_WEEK_DAY
                + getPaddingLeft() + getPaddingRight();
        final int resolvedWidth = resolveSize(preferredWidth, widthMeasureSpec);
        final int resolvedHeight = resolveSize(preferredHeight, heightMeasureSpec);
        setMeasuredDimension(resolvedWidth, resolvedHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return onDayTouch(event);
    }

    //加速度计算
    private VelocityTracker velocityTracker;
    private float startX, startY;

    private boolean onDayTouch(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (velocityTracker == null) {
                    velocityTracker = VelocityTracker.obtain();
                } else {
                    velocityTracker.clear();
                }
                startX = event.getX();
                startY = event.getY();
                velocityTracker.addMovement(event);
                break;
            case MotionEvent.ACTION_MOVE:
                velocityTracker.addMovement(event);
                break;
            case MotionEvent.ACTION_UP:
                float x1 = event.getX();
                float y1 = event.getY();
                int locationX1 = (int) (x1 + 0.5f);
                int locationY1 = (int) (y1 + 0.5f);
                velocityTracker.computeCurrentVelocity(1000);
                float yVelocity = velocityTracker.getYVelocity();
                velocityTracker.recycle();
                velocityTracker = null;
                float distanceX = x1 - startX;
                float distanceY = y1 - startY;
                //触发条件
                if (canShrink && triggerShrink(yVelocity, distanceX, distanceY)) {
                    onShrink(distanceY < 0);
                } else {
                    //选中日期
                    Calendar selectedCalendar = getCalendarFromLocation(locationX1, locationY1);
                    if (selectedCalendar != null) {
                        selectCalendar.setTime(selectedCalendar.getTime());
                        //当选择了当前屏幕的日期之后，同时更新mCalendar
                        mCalendar.setTime(selectedCalendar.getTime());
                        if (getOnDayClickListener() != null) {
                            getOnDayClickListener().onDayClick(selectCalendar);
                        }
                    }
                }
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                invalidate();
                break;
        }
        return true;
    }

    /**
     * 触发收缩展开效果 可能不同的手机会有不同的误差，后期适配调整
     *
     * @param yVelocity 纵向加速度
     * @param distanceX 横向移动距离
     * @param distanceY 纵向移动距离
     * @return true 是否触发效果
     */
    private boolean triggerShrink(float yVelocity, float distanceX, float distanceY) {
        return Math.abs(yVelocity) > 2000
                || (Math.abs(distanceX) < Math.abs(distanceY) && Math.abs(distanceY) / Math.abs(distanceX) > 2);
    }

    /**
     * 触发了状态变化
     */
    public void onShrink(boolean isShrink) {
        this.isShrink = isShrink;
        if (getOnShrinkListener() != null) {
//            if (isShrink) {
//                //说明之前是月份模式，由于传入的是月份的第一天，这里mCalendar应该做处理，如果在本月有选中，那么mCalendar应该和选中的日期是一样
//                //如果没有选中，那么返回应该是当前月第一天所在的周
//                if (CalendarUtil.isSameMonth(mCalendar, selectCalendar)) {
//                    mCalendar.setTime(selectCalendar.getTime());
//                }
//            } else {
//                //说明之前是周浏览模式，应该选择这周的最后一天返回，否者月份可能会少一个月
//                if (CalendarUtil.isSameWeek(mCalendar, selectCalendar)) {
//                    mCalendar.setTime(selectCalendar.getTime());
//                }
//            }
            //20161012调整需求
            //以前是如果不在当前日期月份，那么收起的时候会显示viewPage的currentItem的月份
            //现在调整为收起和展开的时候回到当前选中日期所在的月份
            //比如现在选中了10月13号，但是把日历滑动到8月份，收起之后显示的是10月13号所在的周，展开后自动回到10月份
            getOnShrinkListener().onShrink(isShrink, selectCalendar);
        }
    }

    /**
     * 根据坐标来决定点击的是哪一天
     */
    private Calendar getCalendarFromLocation(int x, int y) {
        Calendar calendar = Calendar.getInstance();
        //去除标题和星期，还有前面的为空的日期
        int paddingX = x - getPaddingLeft();
        int paddingY = y - getPaddingTop();

        int day;
        if (isShrink) {
            int minY = showWeekTitleWhenShrink ? mDayOfWeekHeight:0;
            int maxY = showWeekTitleWhenShrink ? mDayOfWeekHeight + mDayHeight : mDayHeight;
            if (paddingY < minY || paddingY > maxY) {
                return null;
            }
            int col = paddingX * MAX_WEEK_DAY / mPaddedWidth;
            calendar.setTime(mCalendar.getTime());
            calendar = getWeekFirstDay(mCalendar, WEEK_START_DAY);
            calendar.add(Calendar.DAY_OF_MONTH, col);
        } else {
            if (paddingY < (mMonthTitleHeight + mDayOfWeekHeight) || paddingY > mPaddedHeight) {
                return null;
            }
            int row = (paddingY - (mMonthTitleHeight + mDayOfWeekHeight)) / mDayHeight;
            int col = paddingX * MAX_WEEK_DAY / mPaddedWidth;
            int index = col + row * MAX_WEEK_DAY;
            day = index + 1 - findDayOffset();
            if (!isValidDayOfMonth(day)) {
                return null;
            }
            calendar.setTime(mCalendar.getTime());
            calendar.set(Calendar.DAY_OF_MONTH, day);
        }
        if (CalendarUtil.isCalendarOverRange(calendar, minCalendar, maxCalendar)) {
            return null;
        }
        return calendar;
    }

    private boolean isValidDayOfMonth(int day) {
        return day >= 1 && day <= mDaysInMonth;
    }

    //收起状态回调参数
    interface OnShrinkListener {
        void onShrink(boolean shrink, Calendar curCalendar);
    }

    interface OnDayClickListener {
        void onDayClick(Calendar calendar);
    }

    private OnShrinkListener onShrinkListener;
    private OnDayClickListener onDayClickListener;

    public OnShrinkListener getOnShrinkListener() {
        return onShrinkListener;
    }

    public void setOnShrinkListener(OnShrinkListener onShrinkListener) {
        this.onShrinkListener = onShrinkListener;
    }

    public OnDayClickListener getOnDayClickListener() {
        return onDayClickListener;
    }

    public void setOnDayClickListener(OnDayClickListener onDayClickListener) {
        this.onDayClickListener = onDayClickListener;
    }
}
