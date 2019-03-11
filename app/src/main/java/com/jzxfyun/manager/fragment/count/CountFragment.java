package com.jzxfyun.manager.fragment.count;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jzxfyun.manager.R;
import com.jzxfyun.manager.base.BaseMainFragment;

public class CountFragment extends BaseMainFragment {
    private Toolbar mToolbar;

    public static CountFragment newInstance() {
        Bundle args = new Bundle();
        CountFragment fragment = new CountFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_count;
    }

    @Override
    protected void onClickView(View view) {

    }

    @Override
    protected void initView() {
        mToolbar = findViewById(R.id.toolbar);

        mToolbar.setTitle(R.string.count);
    }

    @Override
    protected void loadData() {

    }

}
