package com.zxycloud.common.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.zxycloud.common.FragmentListener;
import com.zxycloud.common.R;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.TimeUpUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author leiming
 * @date 2019/1/14.
 */
public abstract class BaseActivity extends AppCompatActivity implements BaseFragment.FragmentStartListener {

    /**
     * 用于传递的上下文信息
     */
    protected Context mContext;// 上下文对象
    protected Activity mActivity;// 当前Activity

    /**
     * 自定义事件间隔的点击监听Id缓存
     */
    private SparseArray<Long> clickIntervalTimes;

    private LinkedHashMap<String, Fragment> fragmentArray;

    private FragmentListener fragmentListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 禁止横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);

        init();
        // 传参获取
        getBundle(getIntent().getBundleExtra("bundle"));
    }

    /**
     * 初始化
     */
    private void init() {
        mContext = getApplicationContext();
        mActivity = this;

        // 防止输入法遮挡EditText
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // 沉浸式
        getWindow().getDecorView().setFitsSystemWindows(true);
        //透明状态栏 @顶部
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 获取bundle传递的参数
     *
     * @param bundle 参数载体
     */
    protected abstract void getBundle(Bundle bundle);

    /**
     * 防抖后的点击事件方法
     *
     * @param view 被点击控件
     */
    protected abstract void onViewClick(View view);

    /**
     * 双击的控件
     *
     * @param view 被双击的控件
     */
    protected abstract void onViewDoubleClick(View view);

    /**
     * 隐藏键盘
     */
    protected void hideKeyBoard() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 批量设置点击事件
     *
     * @param viewIds 被点击控件的Id
     */
    protected void setOnClickListener(@IdRes int... viewIds) {
        for (int layout : viewIds) {
            findViewById(layout).setOnClickListener(onClickListener);
        }
    }

    /**
     * 批量设置点击事件
     *
     * @param intervalTimes 点击防抖的时间间隔
     * @param viewIds       被点击控件的Id
     */
    protected void setOnClickListener(long intervalTimes, @IdRes int... viewIds) {
        if (CommonUtils.isEmpty(clickIntervalTimes)) {
            clickIntervalTimes = new SparseArray<>();
        }
        for (int layout : viewIds) {
            clickIntervalTimes.put(layout, intervalTimes);
            findViewById(layout).setOnClickListener(onClickListener);
        }
    }

    /**
     * 为控件添加点击事件
     *
     * @param viewIds 被点击控件Id列表
     */
    protected void setDoubleClickView(int... viewIds) {
        for (int viewId : viewIds) {
            findViewById(viewId).setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onViewTouch(v, event);
                    return true;
                }
            });
        }
    }

    /**
     * 开启Activity
     *
     * @param clz 待开启的Activity
     */
    protected void startActivity(Class clz) {
        startActivity(new Intent(this, clz));
    }

    /**
     * 开启Fragment
     *
     * @param fragment 待开启的Fragment
     */
    protected void startFragment(Fragment fragment) {
        startFragment(false, fragment);
    }

    /**
     * 控件点击回调
     */
    private BaseGestureDetector gestureDetector = new BaseGestureDetector(mContext, new BaseOnGestureListener() {

        /**
         * 发生确定的单击时执行
         * @param e 点击事件
         * @return 是否单击
         */
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {// 单击事件
            return super.onSingleTapConfirmed(e);
        }

        /**
         * 双击发生时的通知
         * @param e 点击事件
         * @return 是否双击
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {// 双击事件
            CommonUtils.log().i("onDoubleTap");
            onViewDoubleClick(getView());
            return true;
        }

        /**
         * 双击手势过程中发生的事件，包括按下、移动和抬起事件
         * @param e 点击事件
         * @return 双指操作时间
         */
        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            CommonUtils.log().i("onDoubleTapEvent");
//            mainRv.scrollToPosition(0);
            return true;
        }
    });


    @Override
    public void startFragment(boolean isCurrentClose, Fragment... fragments) {
        if (mActivity instanceof FragmentListener) {
            if (CommonUtils.judgeListNull(fragments) > 0) {
                if (null == fragmentListener) {
                    fragmentListener = (FragmentListener) BaseActivity.this;
                }
                if (null == fragmentArray) {
                    fragmentArray = new LinkedHashMap<>();
                }
                for (Fragment fragment : fragments) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    if (fragmentListener.fragmentStartType() == FragmentListener.FRAGMENT_START_REPLACE) {
                        transaction.replace(fragmentListener.fragmentLayoutId(), fragment, String.valueOf(System.currentTimeMillis()));
                    } else {
                        transaction.add(fragmentListener.fragmentLayoutId(), fragment, String.valueOf(System.currentTimeMillis()));
                    }
                    // 添加时间戳是为了避免Fragment复用时，key相同导致覆盖
                    fragmentArray.put(fragment.getTag(), fragment);
                    CommonUtils.log().i("添加Fragment的tag为：" + fragment.getTag() + " ********* tag " + fragmentListener.fragmentStartType());
                    transaction.commit();
                }
            }
        }
    }

    @Override
    public void removeFragment(Fragment... fragments) {
        if (mActivity instanceof FragmentListener) {
            if (null == fragmentListener) {
                fragmentListener = (FragmentListener) BaseActivity.this;
            }
            if (null == fragmentArray) {
                fragmentArray = new LinkedHashMap<>();
            }

            if (CommonUtils.judgeListNull(fragments) > 0) {
                for (Fragment fragment : fragments) {
                    CommonUtils.log().i("删除Fragment的tag为：" + fragment.getTag());
                    if (fragmentArray.containsValue(fragment)) {
                        //noinspection SuspiciousMethodCalls
                        fragmentArray.remove(fragment);
                    }
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.remove(fragment);
                    transaction.commit();
                }
            }
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (CommonUtils.isEmpty(clickIntervalTimes)) {                                                      // 没有自定义点击间隔时间
                if (! CommonUtils.timeUpUtils().isTimeUp(TimeUpUtils.TIME_UP_CLICK, System.currentTimeMillis())) {        // 若两次点击在默认时间间隔内，则不执行后一次点击事件的操作
                    return;
                }
            } else {                                                                                            // 自定义点击事件间隔
                Long clickIntervalTime = clickIntervalTimes.get(id);
                if (CommonUtils.isEmpty(clickIntervalTime)                                                        // 被点击控件没有设置点击时间间隔的前提下，若两次点击在默认时间间隔内，则不执行后一次点击事件的操作
                        && ! CommonUtils.timeUpUtils().isTimeUp(TimeUpUtils.TIME_UP_CLICK, System.currentTimeMillis())) {
                    return;
                } else if (CommonUtils.notEmpty(clickIntervalTime)                                                // 被点击控件设置了点击事件间隔的前提下，若两次点击在设置的的时间间隔内，则不执行后一次点击事件的操作
                        && ! CommonUtils.timeUpUtils().isTimeUp(TimeUpUtils.TIME_UP_CLICK, System.currentTimeMillis(), clickIntervalTime)) {
                    return;
                }
            }
            onViewClick(v);
        }
    };
}
