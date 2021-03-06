package com.zxycloud.common.widget.BswRecyclerView;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author leiming
 * @date 2018/9/7.
 */
public class LimitAnnotation {
    /**
     * 纵向布局
     */
    public static final int VERTICAL = 10;
    /**
     * 横向布局
     */
    private static final int HORIZONTAL = 11;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({VERTICAL, HORIZONTAL})
    @interface LayoutManagerType {
    }

    public static final int BOTTOM_DECORATION = 0x010;
    public static final int ROUND_DECORATION = 0x011;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({BOTTOM_DECORATION, ROUND_DECORATION})
    @interface DecorationType {
    }
}
