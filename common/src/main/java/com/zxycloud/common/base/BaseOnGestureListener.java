package com.zxycloud.common.base;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.zxycloud.common.utils.CommonUtils;

/**
 * @author leiming
 * @date 2019/1/18.
 */
public class BaseOnGestureListener extends GestureDetector.SimpleOnGestureListener {
    private View view;

    public void setView(View view) {
        this.view = view;
    }

    public View getView() {
        return view;
    }
}