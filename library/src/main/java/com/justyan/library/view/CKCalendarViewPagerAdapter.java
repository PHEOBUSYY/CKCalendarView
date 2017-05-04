package com.justyan.library.view;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.justyan.library.R;
import com.justyan.library.utils.CalendarUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


/**
 * yanyi on 16/7/8.
 * 日历控件的viewpager
 */
class CKCalendarViewPagerAdapter extends PagerAdapter {
    private SparseArray<ViewHolder> lists;
    private boolean shrink;

    CKCalendarViewPagerAdapter() {
        lists = new SparseArray<>();
    }
    //curCalendar用来决定当前是在哪个位置
    //通过shrink回调来改变curCalendar，然后决定viewpager选中哪一个位置

    private Calendar maxCalendar, minCalendar, curPositionCalendar;
    private Calendar maxWeekCalendar = Calendar.getInstance();
    private Calendar minWeekCalendar = Calendar.getInstance();
    private Calendar curPositionWeekCalendar = Calendar.getInstance();
    private int mCount;

    void setRange(boolean shrink, Calendar minCalendar, Calendar maxCalendar, Calendar curCalendar) {
        this.shrink = shrink;
        this.minCalendar = minCalendar;
        this.maxCalendar = maxCalendar;
        this.curPositionCalendar = curCalendar;

        minWeekCalendar = CalendarUtil.getWeekFirstDay(minCalendar, CKCalendarPagerItemView.WEEK_START_DAY);
        maxWeekCalendar = CalendarUtil.getWeekLastDay(maxCalendar, CKCalendarPagerItemView.WEEK_START_DAY);
        curPositionCalendar = CalendarUtil.getWeekFirstDay(curCalendar, CKCalendarPagerItemView.WEEK_START_DAY);
    }

    /**
     * 计算当前应该选中哪个位置
     *
     * @param shrink 是否是收缩
     * @return 选中位置
     */
    int getShouldSelectPosition(boolean shrink) {
        int currentPosition;
        if (shrink) {
            int maxWeekDiff = CalendarUtil.diffWeek(minWeekCalendar, maxWeekCalendar);
            int selectWeekDiff = CalendarUtil.diffWeek(minWeekCalendar, curPositionWeekCalendar);
            currentPosition = CalendarUtil.constrain(selectWeekDiff, 0, maxWeekDiff);
            mCount = maxWeekDiff + 1;
        } else {
            int maxMonthDiff = CalendarUtil.diffMonth(minCalendar, maxCalendar);
            int selectMonthDiff = CalendarUtil.diffMonth(minCalendar, curPositionCalendar);
            currentPosition = CalendarUtil.constrain(selectMonthDiff, 0, maxMonthDiff);
            mCount = maxMonthDiff + 1;
        }
        notifyDataSetChanged();
        return currentPosition;
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ViewHolder) object).calendar;
    }

    private Calendar selectCalendar = Calendar.getInstance();

    CKCalendarPagerItemView getItemAt(int position) {
        if (lists.get(position) != null && lists.get(position).calendar != null) {
            return lists.get(position).calendar;
        }
        return null;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final CKCalendarPagerItemView customMonthView = (CKCalendarPagerItemView) LayoutInflater.from(container.getContext()).inflate(R.layout.ck_calendar_item, container, false);
        final ViewHolder holder = new ViewHolder(position, customMonthView);
        container.addView(customMonthView);
        lists.put(position, holder);
        if (shrink) {
            Calendar curPositionCalendar = getWeekCalendar(position);
            customMonthView.setMonthViewParams(shrink, curPositionCalendar, selectCalendar, minCalendar, maxCalendar);
        } else {
            Calendar curPositionCalendar = getMonthCalendar(position);
            customMonthView.setMonthViewParams(shrink, curPositionCalendar, selectCalendar, minCalendar, maxCalendar);
        }
        customMonthView.setOnShrinkListener(mOnShrinkListener);
        customMonthView.setOnDayClickListener(mOnDayClickListener);

        return holder;
    }

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    Calendar getCurPositionCalendar(int position) {
        return isShrink() ? getWeekCalendar(position) : getMonthCalendar(position);
    }

    String getCurPageCalendarRange(int position) {
        if (isShrink()) {
            Calendar weekCalendar = getWeekCalendar(position);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(weekCalendar.getTime());
            calendar.set(Calendar.DAY_OF_WEEK, 7);
            String beginStr = simpleDateFormat.format(weekCalendar.getTime());
            String endStr = simpleDateFormat.format(calendar.getTime());
            return beginStr + "~" + endStr;
        } else {
            Calendar monthCalendar = getMonthCalendar(position);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(monthCalendar.getTime());
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            String beginStr = simpleDateFormat.format(monthCalendar.getTime());
            String endStr = simpleDateFormat.format(calendar.getTime());
            return beginStr + "~" + endStr;
        }
    }

    /**
     * 每周第一天
     */
    private Calendar getWeekCalendar(int position) {
        Calendar result = Calendar.getInstance();
        result.setTime(minWeekCalendar.getTime());
        result.set(Calendar.DAY_OF_MONTH, result.get(Calendar.DAY_OF_MONTH) + position * 7);
        return result;
    }

    Calendar getCurMonthCalendar(int position) {
        if (!isShrink()) {
            return getMonthCalendar(position);
        }
        //如果是周模式，需要判断是不是混合周，即横跨两个月
        Calendar beginCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();
        Calendar weekCalendar = getWeekCalendar(position);
        beginCalendar.setTime(weekCalendar.getTime());
        endCalendar.setTime(weekCalendar.getTime());
        beginCalendar.set(Calendar.DAY_OF_WEEK, 1);
        endCalendar.set(Calendar.DAY_OF_WEEK, 7);
        if (beginCalendar.get(Calendar.MONTH) == endCalendar.get(Calendar.MONTH)) {
            return weekCalendar;
        } else {
            return endCalendar;
        }
    }

    /**
     * 每月的第一天
     */
    @NonNull
    private Calendar getMonthCalendar(int position) {
        int yearFromPosition = getYearFromPosition(position);
        int monthFromPosition = getMonthFromPosition(position);
        Calendar curPositionCalendar = Calendar.getInstance();
        curPositionCalendar.set(Calendar.YEAR, yearFromPosition);
        curPositionCalendar.set(Calendar.MONTH, monthFromPosition);
        curPositionCalendar.set(Calendar.DAY_OF_MONTH, 1);
        return curPositionCalendar;
    }

    private final CKCalendarPagerItemView.OnShrinkListener mOnShrinkListener = new CKCalendarPagerItemView.OnShrinkListener() {
        @Override
        public void onShrink(boolean shrink, Calendar calendar) {
            //先处理adapter的逻辑，再通过接口回调处理外部viewpager的逻辑
            //当前交互的是那个月的日期，为了保证收起之后还可以继续选中当前的时间
            CKCalendarViewPagerAdapter.this.shrink = shrink;
            curPositionCalendar = calendar;
            curPositionWeekCalendar.setTime(curPositionCalendar.getTime());
            curPositionWeekCalendar.set(Calendar.DAY_OF_WEEK, 7);
            if (getOnShrinkListener() != null) {
                int currentItem = getShouldSelectPosition(shrink);
                getOnShrinkListener().onShrink(currentItem, shrink);
            }
        }
    };
    private final CKCalendarPagerItemView.OnDayClickListener mOnDayClickListener = new CKCalendarPagerItemView.OnDayClickListener() {
        @Override
        public void onDayClick(Calendar calendar) {
            selectCalendar = calendar;
            //更新一下已经缓存了的日期view
            if (lists != null && lists.size() > 0) {
                for (int i = 0; i < lists.size(); i++) {
                    int key = lists.keyAt(i);
                    ViewHolder perPageViewHolder = lists.get(key);
                    if (perPageViewHolder != null && perPageViewHolder.calendar != null) {
                        perPageViewHolder.calendar.invalidate();
                    }
                }
            }
            //外部回调
            if (getOnDayClickListener() != null) {
                getOnDayClickListener().onDayClick(CKCalendarViewPagerAdapter.this, calendar);
            }

        }
    };

    private int getYearFromPosition(int position) {
        int yearOffset = (position + minCalendar.get(Calendar.MONTH)) / 12;
        return yearOffset + minCalendar.get(Calendar.YEAR);
    }

    private int getMonthFromPosition(int position) {
        return (position + minCalendar.get(Calendar.MONTH)) % 12;
    }


    @Override
    public int getItemPosition(Object object) {
        return ((ViewHolder) object).position;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewHolder holder = (ViewHolder) object;
        container.removeView(holder.calendar);
        lists.remove(position);
    }

    boolean isShrink() {
        return shrink;
    }


    private static class ViewHolder {
        public final int position;
        public final CKCalendarPagerItemView calendar;

        ViewHolder(int position, CKCalendarPagerItemView calendar) {
            this.position = position;
            this.calendar = calendar;
        }
    }

    interface OnDayClickListener {
        void onDayClick(CKCalendarViewPagerAdapter adapter, Calendar day);
    }

    interface OnShrinkListener {
        void onShrink(int currentItem, boolean shrink);
    }

    private OnDayClickListener onDayClickListener;
    private OnShrinkListener onShrinkListener;

    private OnDayClickListener getOnDayClickListener() {
        return onDayClickListener;
    }

    void setOnDayClickListener(OnDayClickListener onDayClickListener) {
        this.onDayClickListener = onDayClickListener;
    }

    private OnShrinkListener getOnShrinkListener() {
        return onShrinkListener;
    }

    void setOnShrinkListener(OnShrinkListener onShrinkListener) {
        this.onShrinkListener = onShrinkListener;
    }
}
