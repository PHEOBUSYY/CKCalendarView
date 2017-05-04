package com.justyan.library.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * yanyi on 16/7/8.
 * 设置高度为其中item的最大高度的viewpager
 */
public class CKCalendarViewPager extends ViewPager{
    public CKCalendarViewPager(Context context) {
        super(context);
        init();
    }

    public CKCalendarViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
    }

    int maxHeight =0;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        boolean wrapHeight = MeasureSpec.getMode(heightMeasureSpec)
                == MeasureSpec.AT_MOST;
        if(wrapHeight) {
            /*
             * The first super.onMeasure call made the pager take up all the
             * available height. Since we really wanted to wrap it, we need
             * to remeasure it. Luckily, after that call the first child is
             * now available. So, we take the height from it.
             */

            int width = getMeasuredWidth(), height = getMeasuredHeight();

            // Use the previously measured width but simplify the calculations
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);

            /* If the pager actually has any children, take the first child's
             * height and call that our own */

            if(getChildCount() > 0) {
                for (int i = 0; i < getChildCount(); i++) {
                    View firstChild = getChildAt(i);

                /* The child was previously measured with exactly the full height.
                 * Allow it to wrap this time around. */
                    firstChild.measure(widthMeasureSpec,
                            MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));

                    height = firstChild.getMeasuredHeight();
                    if (height > maxHeight) {
                        maxHeight = height;
                    }
                }
            }
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY);
            maxHeight = 0;
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }


}
