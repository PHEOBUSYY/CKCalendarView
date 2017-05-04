package com.justyan.library.utils;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * yanyi on 16/7/15.
 */
public class CalendarUtil {
    public static Calendar createCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static Calendar createCalendar(String dateStr, SimpleDateFormat dateFormat) {
        try {
            Date date = dateFormat.parse(dateStr);
            return createCalendar(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Calendar.getInstance();
    }
    public static boolean isSameDay(Calendar calendar, Calendar calendar2) {
        //注意不能使用equal判断，因为equal内部是通过毫秒数来判断的，同一天的毫秒数不一定一样
        return calendar.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
                calendar.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);
    }



    public static Calendar cutHourMinAndSecond(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar;
    }

    public static boolean isSameWeek(Calendar first, Calendar second) {
        for (int i = 0; i < 7; i++) {
            first.set(Calendar.DAY_OF_WEEK, i);
            if (isSameDay(first,second)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWeekReportSameWeek(Calendar first, Calendar second) {
        first = cutHourMinAndSecond(first);
        second = cutHourMinAndSecond(second);
        SimpleDateFormat mFirstDayFormatter = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        String firstTime = mFirstDayFormatter.format(first.getTime());
        String secondTime = mFirstDayFormatter.format(second.getTime());


        Calendar beginWeekCaledar = Calendar.getInstance();
        beginWeekCaledar.setTimeInMillis(second.getTimeInMillis());
        beginWeekCaledar.set(Calendar.DAY_OF_WEEK,2);

        Calendar endWeekCalendar = Calendar.getInstance();
        endWeekCalendar.setTimeInMillis(beginWeekCaledar.getTimeInMillis());
        endWeekCalendar.add(Calendar.DAY_OF_MONTH,6);


       String firstTime1 = mFirstDayFormatter.format(first.getTime());
       String secondTime1 = mFirstDayFormatter.format(second.getTime());

        if (first.compareTo(beginWeekCaledar)>=0 && first.compareTo(endWeekCalendar)<=0) {
            return true;
        }
        return false;
    }
    public static boolean isSameMonth(Calendar calendar,Calendar calendar2) {
        return calendar.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH);
    }

    public static boolean isSameYear(Calendar calendar,Calendar calendar2) {
        return calendar.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR);
    }

    public static boolean isCalendarOverRange(Calendar calendar,Calendar minCalendar,Calendar maxCalendar) {
        if (isSameDay(calendar, minCalendar)) {
            return false;
        }
        if (isSameDay(calendar, maxCalendar)) {
            return false;
        }
        return calendar.compareTo(minCalendar) < 0 || calendar.compareTo(maxCalendar) > 0;
    }
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 两个日期中间有多少周
     * @param from
     * @param end
     * @return
     */
    public static int diffWeek(Calendar from, Calendar end) {
        //把最小日期前移到周的第一天
        //把最后一天的日期后移到周的最后一天
        //用两个时间除以7就是周的个数
        long perSecond = 1000 * 60 * 60 * 24 * 7;
        return (int) ((end.getTimeInMillis() - from.getTimeInMillis()) / perSecond);
    }
    /**
     * 两个日期中间有多少周  上啦
     * @return
     */
    public static int diffShrikWeek(Calendar startCalendar, Calendar endCalendar) {
        startCalendar = cutHourMinAndSecond(startCalendar);
        endCalendar = cutHourMinAndSecond(endCalendar);
        startCalendar.set(Calendar.DAY_OF_WEEK, 2);

//        Logger.e("01startCalendar",startCalendar.get(Calendar.YEAR)+"/"+(startCalendar.get(Calendar.MONTH)+1)+"/"+startCalendar.get(Calendar.DAY_OF_MONTH)+"/"+startCalendar.get(Calendar.DAY_OF_WEEK));
//        Logger.e("01endCalendar",endCalendar.get(Calendar.YEAR)+"/"+(endCalendar.get(Calendar.MONTH)+1)+"/"+endCalendar.get(Calendar.DAY_OF_MONTH)+"/"+endCalendar.get(Calendar.DAY_OF_WEEK));

        if (endCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            endCalendar.set(Calendar.DAY_OF_WEEK, 7);
            endCalendar.add(Calendar.DAY_OF_YEAR,1);
        }

//        Logger.e("02startCalendar",startCalendar.get(Calendar.YEAR)+"/"+(startCalendar.get(Calendar.MONTH)+1)+"/"+startCalendar.get(Calendar.DAY_OF_MONTH)+"/"+startCalendar.get(Calendar.DAY_OF_WEEK));
//        Logger.e("02endCalendar",endCalendar.get(Calendar.YEAR)+"/"+(endCalendar.get(Calendar.MONTH)+1)+"/"+endCalendar.get(Calendar.DAY_OF_MONTH)+"/"+endCalendar.get(Calendar.DAY_OF_WEEK));
        long perSecond = 1000 * 60 * 60 * 24 * 7;
        return (int) ((endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis()) / perSecond);
    }

    public static int diffMonth(Calendar from, Calendar end) {
        final int diffYears = end.get(Calendar.YEAR) - from.get(Calendar.YEAR);
        return diffYears * 12 + (end.get(Calendar.MONTH) - from.get(Calendar.MONTH));
    }

    public static int diffYear(Calendar from, Calendar end){
        int diffYears = end.get(Calendar.YEAR) - from.get(Calendar.YEAR);
        return diffYears;
    }

    public static int diffQuarter(Calendar from, Calendar end){
        int endYear = end.get(Calendar.YEAR);
        int startYear = from.get(Calendar.YEAR);
        int startQuarter = getCurrentQuarter(from);
        int endQuarter =  getCurrentQuarter(end);
        return (endYear - startYear) * 4 + (endQuarter - startQuarter) ;
    }


    public static int constrain(int amount, int low, int height) {
        return amount < low ? low : (amount > height ? height : amount);
    }


    public static SimpleDateFormat getDefaultFormat() {
        return new SimpleDateFormat("yyyy-MM-dd");
    }

    /**
     * 去掉时分秒
     * @param calendar
     * @return
     */
    public static Calendar cutCalendarTime(Calendar calendar) {
        if (calendar != null) {
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return calendar;
        }
        return null;
    }
     /* 当前季度
     *
     * @return
     */
    public static int getCurrentQuarter( Calendar calendar) {
         int currentMonth = calendar.get(Calendar.MONTH);
         return currentMonth/3+1;
    }

    /**
     * 获取当年的最后一天
     * @param
     * @return
     */
    public static String getCurrYearLast(){
        Calendar currCal=Calendar.getInstance();
        int currentYear = currCal.get(Calendar.YEAR);
        return getYearLast(currentYear);
    }

    /**
     * 获取某年最后一天日期
     * @param year 年份
     * @return Date
     */
    public static String getYearLast(int year){
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        Date currYearLast = calendar.getTime();
        SimpleDateFormat dataFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
       String yearLast =  dataFormatter.format(currYearLast);


        return yearLast;
    }

    public static Calendar getReportDate(String time,String pattern){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Calendar calendar = Calendar.getInstance();;
        try {
            Date date = sdf.parse(time);
            calendar.setTime(date);
            return calendar;
        }catch (ParseException e){
            e.printStackTrace();
            return calendar;
        }

    }
    public static Calendar cloneCalendar(Calendar oldCalendar) {
        Calendar result = Calendar.getInstance();
        result.set(oldCalendar.get(Calendar.YEAR), oldCalendar.get(Calendar.MONTH), oldCalendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        return result;
    }
    /**
     * 获取当前周周的第一天，主要用来处理如果默认每周第一天是星期一的情况
     * @param curCalendar 当前周日期
     * @param mWeekStart Calendar.MONDAY or Calendar.SUNDAY
     * @return 每周第一天
     */
    public static Calendar getWeekFirstDay(Calendar curCalendar, int mWeekStart) {
        Calendar result = Calendar.getInstance();
        result.setTime(curCalendar.getTime());
        if (mWeekStart == Calendar.MONDAY) {
            if (curCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                result.set(Calendar.DAY_OF_WEEK, mWeekStart);
            } else {
                result.add(Calendar.DAY_OF_MONTH, -6);
            }
        } else if (mWeekStart == Calendar.SUNDAY) {
            result.set(Calendar.DAY_OF_WEEK, mWeekStart);
        }

        return result;
    }

    /**
     * 获取当前周的最后一天
     * @param curCalendar 当前周日期
     * @param mWeekStart Calendar.MONDAY or Calendar.SUNDAY
     * @return 每周最后一天
     */
    public static Calendar getWeekLastDay(Calendar curCalendar, int mWeekStart) {
        Calendar result = getWeekFirstDay(curCalendar, mWeekStart);
        result.add(Calendar.DAY_OF_MONTH, 6);
        return result;
    }
}
