package com.jzxfyun.manager.fragment.service;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jzxfyun.manager.R;
import com.jzxfyun.manager.base.BaseMainFragment;
import com.jzxfyun.manager.fragment.service.child.ContentFragment;
import com.jzxfyun.manager.fragment.service.child.MenuListFragment;

import java.util.ArrayList;
import java.util.Arrays;

public class ServiceFragment extends BaseMainFragment {
    private Toolbar mToolbar;
    private ArrayList<String> listMenus;

    public static ServiceFragment newInstance() {
        Bundle args = new Bundle();
        ServiceFragment fragment = new ServiceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void loadData() {
        listMenus = new ArrayList<>();
        listMenus.add("设备安装");
        listMenus.add("设备巡查");
        listMenus.add("视频信息");
        listMenus.add("系统管理");
        listMenus.add("账号管理");
        listMenus.add("运行记录");
        listMenus.add("单位信息");
        listMenus.add("设备信息");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_service;
    }

    @Override
    protected void initView() {
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.service);

        if (findChildFragment(MenuListFragment.class) == null) {
            MenuListFragment menuListFragment = MenuListFragment.newInstance(listMenus);
            loadRootFragment(R.id.fl_list_container, menuListFragment);
            // false:  不加入回退栈;  false: 不显示动画
            loadRootFragment(R.id.fl_content_container, ContentFragment.newInstance(listMenus.get(0)), false, false);
        }
    }

    @Override
    protected void onClickView(View view) {

    }

    /**
     * 替换加载 内容Fragment
     * @param fragment
     */
    public void switchContentFragment(ContentFragment fragment) {
        BaseMainFragment contentFragment = findChildFragment(ContentFragment.class);
        if (contentFragment != null) {
            contentFragment.replaceFragment(fragment, false);
        }
    }

}
