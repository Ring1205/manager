package com.jzxfyun.manager.fragment.service.child;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jzxfyun.manager.R;
import com.jzxfyun.manager.adapter.MenuAdapter;
import com.jzxfyun.manager.base.BaseMainFragment;
import com.jzxfyun.manager.fragment.service.ServiceFragment;
import com.jzxfyun.manager.listener.OnItemClickListener;

import java.util.ArrayList;

/**
 * Created by YoKeyword on 16/2/9.
 */
public class MenuListFragment extends BaseMainFragment {
    private static final String ARG_MENUS = "arg_menus";
    private static final String SAVE_STATE_POSITION = "save_state_position";

    private RecyclerView mRecy;
    private MenuAdapter mAdapter;

    private ArrayList<String> mMenus;
    private int mCurrentPosition = -1;

    public static MenuListFragment newInstance(ArrayList<String> menus) {
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_MENUS, menus);
        MenuListFragment fragment = new MenuListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.recycler;
    }

    @Override
    protected void initView() {
        mRecy = findViewById(R.id.recycler);
    }

    @Override
    protected void loadData() {
        Bundle args = getArguments();
        if (args != null) {
            mMenus = args.getStringArrayList(ARG_MENUS);
        }
    }

    @Override
    protected void onClickView(View view) {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LinearLayoutManager manager = new LinearLayoutManager(_mActivity);
        mRecy.setLayoutManager(manager);
        mAdapter = new MenuAdapter(_mActivity);
        mRecy.setAdapter(mAdapter);
        mAdapter.setDatas(mMenus);

        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, RecyclerView.ViewHolder vh) {
                showContent(position);
            }
        });

        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(SAVE_STATE_POSITION);
            mAdapter.setItemChecked(mCurrentPosition);
        } else {
            mCurrentPosition = 0;
            mAdapter.setItemChecked(0);
        }
    }

    private void showContent(int position) {
        if (position == mCurrentPosition || position < 0 || position > mMenus.size()) {
            return;
        }

        mCurrentPosition = position;

        mAdapter.setItemChecked(position);

        ContentFragment fragment = ContentFragment.newInstance(mMenus.get(position));

        ((ServiceFragment) getParentFragment()).switchContentFragment(fragment);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // 保证该页面被动刷新时页面不变，比如横竖屏切换
        outState.putInt(SAVE_STATE_POSITION, mCurrentPosition);
    }
}
