package com.zxycloud.common.base;

import android.content.Context;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author leiming
 * @date 2019/1/18.
 */
public class BaseGestureDetector extends GestureDetector {
    private BaseOnGestureListener listener;

    public BaseGestureDetector(Context context, BaseOnGestureListener listener) {
        super(context, listener);
        this.listener = listener;
    }

    public BaseGestureDetector(Context context, BaseOnGestureListener listener, Handler handler) {
        super(context, listener, handler);
        this.listener = listener;
    }

    public BaseGestureDetector(Context context, BaseOnGestureListener listener, Handler handler, boolean unused) {
        super(context, listener, handler, unused);
        this.listener = listener;
    }

    public void onViewTouch(View v, MotionEvent event) {
        listener.setView(v);
        onTouchEvent(event);
    }
}
