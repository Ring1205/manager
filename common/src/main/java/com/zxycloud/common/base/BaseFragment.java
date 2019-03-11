package com.zxycloud.common.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.zxycloud.common.R;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.TimeUpUtils;
import com.zxycloud.common.utils.netWork.InDiskCookieStore;

/**
 * 基础Fragment
 *
 * @author leiming
 * @date 2019/1/18 16:55
 */
public abstract class BaseFragment extends Fragment {
    /**
     * 用于传递的上下文信息
     */
    protected Context mContext;
    protected Activity mActivity;
    /**
     * 当前碎片布局文件
     */
    private View currentLayout;
    private SparseArray<Long> clickIntervalTimes;
    private FragmentStartListener mListener;
    private BaseFragment fragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBundle(getArguments());
        fragment = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the title_layout for this fragment
        currentLayout = inflater.inflate(R.layout.common_base_layout, container, false);
        return currentLayout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setBaseContentView(getLayoutId());
    }

    /**
     * 引用头部布局
     *
     * @param layoutId 布局id
     */
    private void setBaseContentView(int layoutId) {
        FrameLayout layout = findViewById(R.id.fl_body_placeholder);
        //获取布局，并在BaseActivity基础上显示
        final View view = mActivity.getLayoutInflater().inflate(layoutId, null);
        //关闭键盘
        hideKeyBoard();
        //给EditText的父控件设置焦点，防止键盘自动弹出
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layout.addView(view, params);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context.getApplicationContext();
        mActivity = (Activity) context;
        if (context instanceof FragmentStartListener) {
            mListener = (FragmentStartListener) context;
        }
    }

    /**
     * 隐藏键盘
     */
    protected void hideKeyBoard() {
        View view = mActivity.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 设置点击事件
     *
     * @param viewIds 点击控件Id
     */
    protected void setOnClickListener(int... viewIds) {
        for (int viewId : viewIds) {
            findViewById(viewId).setOnClickListener(onClickListener);
        }
    }

    /**
     * 设置点击事件
     *
     * @param intervalTimes 两次点击之间的间隔
     * @param viewIds       点击控件Id
     */
    protected void setOnClickListener(long intervalTimes, int... viewIds) {
        if (CommonUtils.isEmpty(clickIntervalTimes)) {
            clickIntervalTimes = new SparseArray<>();
        }
        for (int viewId : viewIds) {
            clickIntervalTimes.put(viewId, intervalTimes);
            findViewById(viewId).setOnClickListener(onClickListener);
        }
    }

    protected <T extends View> T findViewById(@IdRes int id) {
        return currentLayout.findViewById(id);
    }

    /**
     * 初始化Bundle
     */
    protected abstract void getBundle(Bundle bundle);

    /**
     * 点击事件
     */
    protected abstract void onViewClick(View v);

    /**
     * 获取布局ID
     *
     * @return 获取的布局ID
     */
    protected abstract @LayoutRes
    int getLayoutId();

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
                    gestureDetector.onTouchEvent(event);
                    return true;
                }
            });
        }
    }

    /**
     * 跳转页面
     *
     * @param targetActivity 所跳转的目标Activity类
     */
    protected void start(Class<?> targetActivity) {
        Intent intent = new Intent(mContext, targetActivity);
        if (mActivity.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                CommonUtils.log().e(getName(), "mActivity not found for " + targetActivity.getSimpleName());
            }
        }
    }

    /**
     * 跳转页面
     *
     * @param targetActivity 所跳转的目的Activity类
     * @param bundle         跳转所携带的信息
     */
    protected void start(Class<?> targetActivity, Bundle bundle) {
        Intent intent = new Intent(mContext, targetActivity);
        if (bundle != null) {
            intent.putExtra("bundle", bundle);
        }
        if (mActivity.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                CommonUtils.log().e(getName(), "mActivity not found for " + targetActivity.getSimpleName());
            }
        }
    }

    /**
     * 跳转页面
     *
     * @param targetActivity 所跳转的Activity类
     * @param requestCode    请求码
     */
    protected void start(Class<?> targetActivity, int requestCode) {
        Intent intent = new Intent(mContext, targetActivity);
        if (mActivity.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
            try {
                startActivityForResult(intent, requestCode);
            } catch (ActivityNotFoundException e) {
                CommonUtils.log().e(getName(), "mActivity not found for " + targetActivity.getSimpleName());
            }
        }
    }

    /**
     * 跳转页面
     *
     * @param targetActivity 所跳转的Activity类
     * @param bundle         跳转所携带的信息
     * @param requestCode    请求码
     */
    protected void start(Class<?> targetActivity, int requestCode, Bundle bundle) {
        Intent intent = new Intent(mContext, targetActivity);
        if (bundle != null) {
            intent.putExtra("bundle", bundle);
        }
        if (mActivity.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
            try {
                startActivityForResult(intent, requestCode);
            } catch (ActivityNotFoundException e) {
                CommonUtils.log().e(getName(), "mActivity not found for " + targetActivity.getSimpleName());
            }
        }
    }

    /**
     * Fragment开启其他Fragment方法
     *
     * @param isCurrentClose 是否关闭当前的Fragment
     * @param fragments      待开启的Fragment列表
     */
    protected void start(boolean isCurrentClose, Fragment... fragments) {
        if (null != mListener) {
            mListener.startFragment(isCurrentClose, fragments);
        } else {
            throw new IllegalArgumentException("Please implements the FragmentStartListener first!");
        }
    }

    /**
     * Fragment开启其他Fragment方法（默认不关闭当前的Fragment）
     *
     * @param fragment 待开启的Fragment列表
     */
    protected void start(Fragment... fragment) {
        start(false, fragment);
    }

    /**
     * Fragment开启其他Fragment方法（默认不关闭当前的Fragment）
     *
     * @param fragment 待开启的Fragment列表
     */
    protected void show(Fragment... fragment) {
        start(false, fragment);
    }

    /**
     * Fragment开启其他Fragment方法（默认不关闭当前的Fragment）
     *
     * @param fragment 待开启的Fragment列表
     */
    protected void hide(Fragment... fragment) {
        start(false, fragment);
    }

    /**
     * Fragment关闭Fragment
     *
     * @param fragments 待关闭的Fragment列表
     */
    protected void remove(Fragment... fragments) {
        mListener.removeFragment(fragments);
    }

    /**
     * 获取当前Fragment类名
     *
     * @return 类名字符串
     */
    protected String getName() {
        return getClass().getSimpleName();
    }

    /**
     * 获取类名
     *
     * @param clz 需要获取名称的类
     * @return 类名字符串
     */
    protected String getName(Class<?> clz) {
        return clz.getClass().getSimpleName();
    }

    /**
     * 控件点击回调
     */
    private GestureDetector gestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
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
            return super.onDoubleTap(e);
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

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (CommonUtils.isEmpty(clickIntervalTimes)) {                                                         // 没有自定义点击间隔时间
                if (! CommonUtils.timeUpUtils().isTimeUp(TimeUpUtils.TIME_UP_CLICK, System.currentTimeMillis())) { // 若两次点击在默认时间间隔内，则不执行后一次点击事件的操作
                    return;
                }
            } else {                                                                                               // 自定义点击事件间隔
                Long clickIntervalTime = clickIntervalTimes.get(id);
                if (CommonUtils.isEmpty(clickIntervalTime)                                                         // 被点击控件没有设置点击时间间隔的前提下，若两次点击在默认时间间隔内，则不执行后一次点击事件的操作
                        && ! CommonUtils.timeUpUtils().isTimeUp(TimeUpUtils.TIME_UP_CLICK, System.currentTimeMillis())) {
                    return;
                } else if (CommonUtils.notEmpty(clickIntervalTime)                                                 // 被点击控件设置了点击事件间隔的前提下，若两次点击在设置的的时间间隔内，则不执行后一次点击事件的操作
                        && ! CommonUtils.timeUpUtils().isTimeUp(TimeUpUtils.TIME_UP_CLICK, System.currentTimeMillis(), clickIntervalTime)) {
                    return;
                }
            }
            onViewClick(v);
        }
    };

    public interface FragmentStartListener {
        // TODO: Update argument type and name
        void startFragment(boolean isCurrentClose, Fragment... fragments);

        void removeFragment(Fragment... fragments);
    }
}
