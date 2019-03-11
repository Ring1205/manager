package com.jzxfyun.manager.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.jzxfyun.manager.R;
import com.jzxfyun.manager.listener.OnNavigationListener;

/**
 * Created by YoKeyword on 16/2/7.
 */
public abstract class BaseBackFragment extends MyBaseFragment {
    private static final String TAG = "Fragmentation";

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setParallaxOffset(0.5f);
    }

    protected void initToolbarNav(Toolbar toolbar) {
//        toolbar.setNavigationIcon(R.drawable.icon_back);
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (navigationListener == null || navigationListener.addNavigationBack())
                    _mActivity.onBackPressed();
            }
        });
    }

    /**
     * 此处可以做退出判断，确认是否退出
     *
     * @return
     */
    protected void addNavigationBack(OnNavigationListener navigationListener) {
        this.navigationListener = navigationListener;
    }

    private OnNavigationListener navigationListener;

}
