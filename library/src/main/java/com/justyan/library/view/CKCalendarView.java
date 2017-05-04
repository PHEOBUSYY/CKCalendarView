package com.justyan.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.justyan.library.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;


/**
 * yanyi on 16/7/12.
 * 日历控件
 */
public class CKCalendarView extends ViewGroup implements ViewPager.OnPageChangeListener {
    public CKCalendarView(Context context) {
        super(context);
        init(null);
    }

    public CKCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CKCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    //默认日期最大值和最小值
    public static final String DEFAULT_MIN_DATE = "1970-01-01";
    public static final String DEFAULT_MAX_DATE = "2030-12-31";

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CKCalendarView);
        String minDateStr = typedArray.getString(R.styleable.CKCalendarView_minDate);
        if (TextUtils.isEmpty(minDateStr)) {
            minDateStr = DEFAULT_MIN_DATE;
        }
        String maxDateStr = typedArray.getString(R.styleable.CKCalendarView_maxDate);
        if (TextUtils.isEmpty(maxDateStr)) {
            maxDateStr = DEFAULT_MAX_DATE;
        }
        boolean isShrink = typedArray.getBoolean(R.styleable.CKCalendarView_shrink, false);
        typedArray.recycle();

        ViewGroup content = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.custom_calendar_view, null, false);
        // Transfer all children from content to here.
        while (content.getChildCount() > 0) {
            final View child = content.getChildAt(0);
            content.removeViewAt(0);
            addView(child);
        }
        pre = (ImageButton) findViewById(R.id.pre);
        next = (ImageButton) findViewById(R.id.next);
        ckCalendarViewPagerAdapter = new CKCalendarViewPagerAdapter();
        CKCalendarViewPager = (CKCalendarViewPager) findViewById(R.id.calendar);
        CKCalendarViewPager.setAdapter(ckCalendarViewPagerAdapter);
        ckCalendarViewPagerAdapter.setOnDayClickListener(onAdapterDayClickListener);
        ckCalendarViewPagerAdapter.setOnShrinkListener(onAdapterShrinkListener);
        CKCalendarViewPager.setOnPageChangeListener(this);
        pre.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CKCalendarViewPager.setCurrentItem(CKCalendarViewPager.getCurrentItem() - 1, true);
            }
        });
        next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CKCalendarViewPager.setCurrentItem(CKCalendarViewPager.getCurrentItem() + 1, true);
            }
        });
        setRange(isShrink, minDateStr, maxDateStr);
    }

    /**
     * 默认初始设置
     */
    public void setRange(boolean shrink, Calendar minCalendar, Calendar maxCalendar, Calendar curCalendar) {
        ckCalendarViewPagerAdapter.setRange(shrink, minCalendar, maxCalendar, curCalendar);
        int currentItem = ckCalendarViewPagerAdapter.getShouldSelectPosition(shrink);
        CKCalendarViewPager.setCurrentItem(currentItem);
    }


    public void setRange(String minDateStr, String maxDateStr) {
        setRange(false, minDateStr, maxDateStr);
    }
    public void setRange(boolean shrink, String minDateStr, String maxDateStr) {
        try {
            Calendar minCalendar = Calendar.getInstance();
            Calendar maxCalendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            minCalendar.setTime(dateFormat.parse(minDateStr));
            maxCalendar.setTime(dateFormat.parse(maxDateStr));
            setRange(shrink, minCalendar, maxCalendar, Calendar.getInstance());
        } catch (ParseException e) {
            throw new IllegalArgumentException("minDate or maxDate format is error!!! please use correct formatString ");
        }
    }

    private ImageButton pre, next;
    private CKCalendarViewPager CKCalendarViewPager;
    private CKCalendarViewPagerAdapter ckCalendarViewPagerAdapter;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final ImageButton leftButton;
        final ImageButton rightButton;
        leftButton = pre;
        rightButton = next;
        final int width = right - left;
        final int height = bottom - top;
        CKCalendarViewPager.layout(0, 0, width, height);
        final CKCalendarPagerItemView monthView = (CKCalendarPagerItemView) CKCalendarViewPager.getChildAt(0);
        final int monthHeight = monthView.getMonthTitleHeight();
        final float cellWidth = monthView.getCellWidth();

        // Vertically center the previous/next buttons within the month
        // header, horizontally center within the day cell.
        final int leftDW = leftButton.getMeasuredWidth();
        final int leftDH = leftButton.getMeasuredHeight();
        final int leftIconTop = monthView.getPaddingTop() + (monthHeight - leftDH) / 2;
        final int leftIconLeft = (int) (monthView.getPaddingLeft() + (cellWidth - leftDW) / 2);
        leftButton.layout(leftIconLeft, leftIconTop, leftIconLeft + leftDW, leftIconTop + leftDH);

        final int rightDW = rightButton.getMeasuredWidth();
        final int rightDH = rightButton.getMeasuredHeight();
        final int rightIconTop = monthView.getPaddingTop() + (monthHeight - rightDH) / 2;
        final int rightIconRight = (int) (width - monthView.getPaddingRight() - (cellWidth - rightDW) / 2);
        rightButton.layout(rightIconRight - rightDW, rightIconTop,
                rightIconRight, rightIconTop + rightDH);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final ViewPager viewPager = CKCalendarViewPager;
        measureChild(viewPager, widthMeasureSpec, heightMeasureSpec);

        final int measuredWidthAndState = viewPager.getMeasuredWidthAndState();
        final int measuredHeightAndState = viewPager.getMeasuredHeightAndState();
        setMeasuredDimension(measuredWidthAndState, measuredHeightAndState);

        final int pagerWidth = viewPager.getMeasuredWidth();
        final int pagerHeight = viewPager.getMeasuredHeight();
        final int buttonWidthSpec = MeasureSpec.makeMeasureSpec(pagerWidth, MeasureSpec.AT_MOST);
        final int buttonHeightSpec = MeasureSpec.makeMeasureSpec(pagerHeight, MeasureSpec.AT_MOST);
        pre.measure(buttonWidthSpec, buttonHeightSpec);
        next.measure(buttonWidthSpec, buttonHeightSpec);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        final float alpha = Math.abs(0.5f - positionOffset) * 2.0f;
        pre.setAlpha(alpha);
        next.setAlpha(alpha);
    }

    private final CKCalendarViewPagerAdapter.OnDayClickListener onAdapterDayClickListener = new CKCalendarViewPagerAdapter.OnDayClickListener() {
        @Override
        public void onDayClick(CKCalendarViewPagerAdapter adapter, Calendar day) {
            if (getOnDayClickListener() != null) {
                getOnDayClickListener().onDayClick(CKCalendarView.this, day);
            }
        }
    };

    public Calendar getCurMonthCalendar() {
        return ckCalendarViewPagerAdapter.getCurMonthCalendar(CKCalendarViewPager.getCurrentItem());
    }

    public Calendar getCurPositionCalendar() {
        return ckCalendarViewPagerAdapter.getCurPositionCalendar(CKCalendarViewPager.getCurrentItem());
    }

    public String getCalendarRange(int position) {
        return ckCalendarViewPagerAdapter.getCurPageCalendarRange(position);
    }

    public int getCurrentItem() {
        return CKCalendarViewPager.getCurrentItem();
    }

    private final CKCalendarViewPagerAdapter.OnShrinkListener onAdapterShrinkListener = new CKCalendarViewPagerAdapter.OnShrinkListener() {
        @Override
        public void onShrink(int currentItem, boolean shrink) {
            //更新viewPager的数据，通过setAdapter的方法
            mShrink = shrink;
            CKCalendarViewPager.setAdapter(ckCalendarViewPagerAdapter);
            CKCalendarViewPager.setCurrentItem(currentItem);
            if (shrink) {
                pre.setVisibility(GONE);
                next.setVisibility(GONE);
            }
            if (getOnShrinkListener() != null) {
                getOnShrinkListener().onShrink(CKCalendarView.this, shrink);
            }
        }
    };

    @Override
    public void onPageSelected(int position) {
        updateButtonVisibility(position);
        if (getOnPageSelectedListener() != null) {
            getOnPageSelectedListener().onPageSelected(position);
        }
    }

    public interface onPageSelectedListener {
        void onPageSelected(int position);
    }

    private onPageSelectedListener onPageSelectedListener;

    public CKCalendarView.onPageSelectedListener getOnPageSelectedListener() {
        return onPageSelectedListener;
    }

    public void setOnPageSelectedListener(CKCalendarView.onPageSelectedListener onPageSelectedListener) {
        this.onPageSelectedListener = onPageSelectedListener;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void updateButtonVisibility(int position) {
        final boolean hasPrev = position > 0;
        final boolean hasNext = position < (CKCalendarViewPager.getAdapter().getCount() - 1);
        if (ckCalendarViewPagerAdapter.isShrink()) {
            pre.setVisibility(GONE);
            next.setVisibility(GONE);
        } else {
            pre.setVisibility(hasPrev ? View.VISIBLE : View.INVISIBLE);
            next.setVisibility(hasNext ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private boolean mShrink;

    public boolean ismShrink() {
        return mShrink;
    }

    public void shrink(boolean shrink) {
        if (mShrink == shrink) {
            return;
        }
        mShrink = shrink;
        int currentItem = CKCalendarViewPager.getCurrentItem();
        CKCalendarPagerItemView itemAt = ckCalendarViewPagerAdapter.getItemAt(currentItem);
        if (itemAt != null) {
            itemAt.onShrink(mShrink);
        }
    }

    public void setHasEventSet(HashSet<String> eventSet) {
        int currentItem = CKCalendarViewPager.getCurrentItem();
        CKCalendarPagerItemView itemAt = ckCalendarViewPagerAdapter.getItemAt(currentItem);
        if (itemAt != null) {
            itemAt.setHasEventSet(eventSet);
        }
    }

    public interface OnDayClickListener {
        void onDayClick(CKCalendarView ckCalendarView, Calendar calendar);
    }

    private OnDayClickListener onDayClickListener;

    public OnDayClickListener getOnDayClickListener() {
        return onDayClickListener;
    }

    public void setOnDayClickListener(OnDayClickListener onDayClickListener) {
        this.onDayClickListener = onDayClickListener;
    }

    public interface OnShrinkListener {
        void onShrink(CKCalendarView ckCalendarView, boolean shrink);
    }

    private OnShrinkListener onShrinkListener;

    public OnShrinkListener getOnShrinkListener() {
        return onShrinkListener;
    }

    public void setOnShrinkListener(OnShrinkListener onShrinkListener) {
        this.onShrinkListener = onShrinkListener;
    }

}
