package com.zxycloud.common.utils;

import android.support.annotation.IntDef;
import android.util.SparseArray;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author leiming
 * @date 2019/1/16.
 */
public class TimeUpUtils {
    public static final int TIME_UP_CLICK = 55;
    public static final int TIME_UP_JUMP = 56;
    private SparseArray<Long> lastSaveTimes;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TIME_UP_CLICK, TIME_UP_JUMP})
    @interface TimeUpType {
    }

    public boolean isTimeUp(@TimeUpType int type, long judgeTime) {
        return isTimeUp(type, judgeTime, 500L);
    }

    public boolean isTimeUp(@TimeUpType int type, long judgeTime, long intervalTime) {
        if (CommonUtils.isEmpty(lastSaveTimes)) {
            lastSaveTimes = new SparseArray<>();
        }
        Long tempTime = lastSaveTimes.get(type);
        if (CommonUtils.isEmpty(tempTime)) {
            lastSaveTimes.put(type, judgeTime);
            return true;
        } else {
            if (judgeTime - tempTime > intervalTime) {
                lastSaveTimes.put(type, judgeTime);
                return true;
            } else {
                return false;
            }
        }
    }
}
