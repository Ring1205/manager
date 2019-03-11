package com.jzxfyun.manager.fragment.mine;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jzxfyun.manager.MainFragment;
import com.jzxfyun.manager.R;
import com.jzxfyun.manager.base.BaseMainFragment;
import com.jzxfyun.manager.fragment.mine.profile.PasswdFragment;
import com.jzxfyun.manager.fragment.mine.profile.ProfileFragment;
import com.jzxfyun.manager.fragment.mine.profile.ReEmailAddsFragment;

public class MineFragment extends BaseMainFragment {
    private static final int REQ_MODIFY_FRAGMENT = 100;
    private ImageView imgHeader;
    private TextView tvEmail;

    public static MineFragment newInstance() {
        Bundle args = new Bundle();
        MineFragment fragment = new MineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_mine;
    }

    @Override
    protected void initView() {
        imgHeader = findViewById(R.id.mine_header);
        tvEmail = findViewById(R.id.tv_email);

        setOnClickListener(R.id.mine_header, R.id.rl_change_password, R.id.rl_modify_mailbox, R.id.rl_common_problem, R.id.rl_safety_fire_knowledge, R.id.rl_consumer_hotline, R.id.rl_service_agreement, R.id.rl_about_us, R.id.btn_quit);
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void onClickView(View view) {
        switch (view.getId()){
            case R.id.mine_header:
                ((MainFragment) getParentFragment()).startBrotherFragment(ProfileFragment.newInstance());
                break;
            case R.id.rl_change_password:
                ((MainFragment) getParentFragment()).startBrotherFragment(PasswdFragment.newInstance());
                break;
            case R.id.rl_modify_mailbox:
                ((MainFragment) getParentFragment()).startForResult(ReEmailAddsFragment.newInstance(tvEmail.getText().toString()), REQ_MODIFY_FRAGMENT);
                break;
            case R.id.rl_common_problem:
                break;
            case R.id.rl_safety_fire_knowledge:
                break;
            case R.id.rl_consumer_hotline:
                break;
            case R.id.rl_service_agreement:
                break;
            case R.id.rl_about_us:
                break;
            case R.id.btn_quit:
                break;
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == REQ_MODIFY_FRAGMENT && resultCode == RESULT_OK && data != null) {
            tvEmail.setText(R.string.modify_mailbox);
            // 保存被改变的 title
//            getArguments().putString(ARG_TITLE, mTitle);
        }
    }
}
