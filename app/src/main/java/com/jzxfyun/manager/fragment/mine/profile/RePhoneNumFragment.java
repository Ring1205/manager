package com.jzxfyun.manager.fragment.mine.profile;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.jzxfyun.manager.R;
import com.jzxfyun.manager.base.BaseBackFragment;

public class RePhoneNumFragment extends BaseBackFragment {
    private static String PHONE_TITLE = "phone_title";
    private Bundle args;
    private Toolbar mToolbar;
    private EditText editPhone;

    public static RePhoneNumFragment newInstance(String title) {
        Bundle args = new Bundle();
        RePhoneNumFragment fragment = new RePhoneNumFragment();
        args.putString(PHONE_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected int getLayoutId() {
        return R.layout.mine_phone;
    }

    @Override
    protected void initView() {
        mToolbar = findViewById(R.id.toolbar);

        if (args != null && !args.getString(PHONE_TITLE).isEmpty()) {
            mToolbar.setTitle(R.string.change_phone_number);
        }else {
            mToolbar.setTitle(R.string.add_phone_num);
        }
        initToolbarNav(mToolbar);

        editPhone = findViewById(R.id.edit_phone);

        setOnClickListener(R.id.btn_yes);
    }

    @Override
    protected void loadData() {
        args = getArguments();
    }

    @Override
    protected void onClickView(View view) {
        switch (view.getId()) {
            case R.id.btn_yes:
                Bundle bundle = new Bundle();
                bundle.putString(ProfileFragment.PHONE_NUMBER, editPhone.getText().toString());
                setFragmentResult(RESULT_OK, bundle);
                break;
        }
    }
}
