package com.jzxfyun.manager.base;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fragment.swipeback.SwipeBackFragment;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.TimeUpUtils;

public abstract class MyBaseFragment extends SwipeBackFragment {
    View layoutView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layoutView = inflater.inflate(getLayoutId(), container, false);
        return layoutView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    /**
     * 退出当前页面
     */
    public void finish(){
        _mActivity.onBackPressed();
    }

    /**
     * 设置点击事件并防止重复执行点击事件
     * @param layouts
     */
    protected void setOnClickListener(@IdRes int... layouts) {
        for (int layout : layouts) {
            findViewById(layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!CommonUtils.timeUpUtils().isTimeUp(TimeUpUtils.TIME_UP_CLICK, System.currentTimeMillis())) {
                        return;
                    }
                    onClickView(v);
                }
            });
        }
    }

    public <T extends View> T findViewById(int layout){
        return layoutView.findViewById(layout);
    }

    /**
     * 获取布局ID
     * @return 获取的布局ID
     */
    protected abstract int getLayoutId();

    /**
     * 初始化布局
     */
    protected abstract void initView();

    /**
     * 加载数据
     */
    protected abstract void loadData();

    /**
     * 点击事件
     * @param view
     */
    protected abstract void onClickView(View view);

}
