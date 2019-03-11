package com.jzxfyun.manager.fragment.service.child;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jzxfyun.manager.R;
import com.jzxfyun.manager.base.BaseMainFragment;

/**
 * Created by YoKeyword on 16/2/9.
 */
public class ContentFragment extends BaseMainFragment {
    private static final String ARG_MENU = "arg_menu";

    private TextView mTvContent;

    private String mMenu;

    public static ContentFragment newInstance(String menu) {
        Bundle args = new Bundle();
        args.putString(ARG_MENU, menu);

        ContentFragment fragment = new ContentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.service_install;
    }

    @Override
    protected void initView() {
        mTvContent = findViewById(R.id.tv_content);

        mTvContent.setText(mMenu);
    }

    @Override
    protected void loadData() {
        Bundle args = getArguments();
        if (args != null) {
            mMenu = args.getString(ARG_MENU);
        }
    }

    @Override
    protected void onClickView(View view) {

    }

}
