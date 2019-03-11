package com.jzxfyun.manager.fragment.mine.profile;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.jzxfyun.manager.R;
import com.jzxfyun.manager.base.BaseBackFragment;

public class ReEmailAddsFragment extends BaseBackFragment {
    private static String EMAIL_TITLE = "email_title";
    private Bundle args;
    private Toolbar mToolbar;
    private EditText editEmail;

    public static ReEmailAddsFragment newInstance(String title) {
        Bundle args = new Bundle();
        ReEmailAddsFragment fragment = new ReEmailAddsFragment();
        args.putString(EMAIL_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void loadData() {
        args = getArguments();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.mine_email;
    }

    @Override
    protected void initView() {
        mToolbar = findViewById(R.id.toolbar);
        if (args != null && !args.getString(EMAIL_TITLE).isEmpty()) {
            mToolbar.setTitle(R.string.modify_mailbox);
        } else {
            mToolbar.setTitle(R.string.add_email_adds);
        }
        initToolbarNav(mToolbar);

        editEmail = findViewById(R.id.edit_email);

        setOnClickListener(R.id.btn_yes);
    }

    @Override
    protected void onClickView(View view) {
        switch (view.getId()) {
            case R.id.btn_yes:
                Bundle bundle = new Bundle();
                bundle.putString(ProfileFragment.EMAIL_ADDRESS, editEmail.getText().toString());
                setFragmentResult(RESULT_OK, bundle);
                break;
        }
    }
}
