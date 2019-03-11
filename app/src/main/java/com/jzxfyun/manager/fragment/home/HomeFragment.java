package com.jzxfyun.manager.fragment.home;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.jzxfyun.manager.R;
import com.jzxfyun.manager.base.BaseMainFragment;

public class HomeFragment extends BaseMainFragment {
    private Toolbar mToolbar;

    public static HomeFragment newInstance() {
        Bundle args = new Bundle();
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_home;
    }

    @Override
    protected void initView() {
        mToolbar = findViewById(R.id.toolbar);

        mToolbar.setTitle(R.string.home);
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void onClickView(View view) {

    }

}
