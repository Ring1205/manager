package com.zxycloud.common.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zxycloud.common.R;

/**
 * @author leiming
 * @date 2019/1/14.
 */
public abstract class BaseLayoutActivity extends BaseActivity {
    private boolean isTitleInit = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_base_layout);
        setBaseContentView(getLayoutId());
    }

    protected abstract int getLayoutId();

    /**
     * 自定义头布局
     *
     * @param customHeaderLayoutResId 自定义头布局资源Id
     */
    @SuppressLint("ClickableViewAccessibility")
    protected void setTitleLayout(@LayoutRes int customHeaderLayoutResId) {
        FrameLayout tlHeaderTemp = findViewById(R.id.fl_header_placeholder);
        if (isTitleInit) {
            tlHeaderTemp.removeAllViews();
        }
        tlHeaderTemp.addView(getLayoutInflater().inflate(customHeaderLayoutResId, null));
        isTitleInit = true;
        setDoubleClickView(R.id.fl_header_placeholder);
    }

    /**
     * 引用头部布局
     *
     * @param layoutId 布局id
     */
    private void setBaseContentView(int layoutId) {
        FrameLayout layout = findViewById(R.id.fl_body_placeholder);

        //获取布局，并在BaseActivity基础上显示
        final View view = getLayoutInflater().inflate(layoutId, null);
        //关闭键盘
        hideKeyBoard();
        //给EditText的父控件设置焦点，防止键盘自动弹出
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layout.addView(view, params);
    }

    /**
     * 设置头
     *
     * @param headerId 头文本Id
     */
    public void setTitle(int headerId) {
        setTitle(headerId, true);
    }

    /**
     * 设置头
     *
     * @param titleId 头文本Id
     * @param hasBack 是否添加返回键
     */
    protected void setTitle(@StringRes int titleId, boolean hasBack) {
        if (! isTitleInit) {
            setTitleLayout(R.layout.common_title_layout);
        }
        ((TextView) findViewById(R.id.base_title)).setText(titleId);
    }
}