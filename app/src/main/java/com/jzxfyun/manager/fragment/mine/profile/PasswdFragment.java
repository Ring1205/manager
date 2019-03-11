package com.jzxfyun.manager.fragment.mine.profile;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.jzxfyun.manager.R;
import com.jzxfyun.manager.base.BaseBackFragment;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_TEXT_VARIATION_NORMAL;
import static android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;

public class PasswdFragment extends BaseBackFragment {
    private Toolbar mToolbar;
    private boolean isChecked;
    private EditText confirmNewPwd, newPwd, oldPwd;

    public static PasswdFragment newInstance() {
        PasswdFragment fragment = new PasswdFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.mine_passwd;
    }

    @Override
    protected void initView() {
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.modify_password);
        initToolbarNav(mToolbar);

        confirmNewPwd = findViewById(R.id.edit_confirm_new_pwd);
        newPwd = findViewById(R.id.edit_new_pwd);
        oldPwd = findViewById(R.id.edit_old_pwd);

        setOnClickListener(R.id.tv_forget_password, R.id.radio_display_password, R.id.btn_update_password);
    }

    @Override
    protected void onClickView(View view) {
        switch (view.getId()) {
            case R.id.tv_forget_password:
                break;
            case R.id.radio_display_password:
                setInputType(isChecked,oldPwd,newPwd,confirmNewPwd);
                if (isChecked) {
                    ((RadioButton) (view)).setChecked(false);
                }
                isChecked =!isChecked;
                break;
            case R.id.btn_update_password:
                break;
        }
    }

    private void setInputType(Boolean isChecked, EditText... edits) {
        for (EditText edit : edits) {
            if (!isChecked) {
                edit.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_NORMAL);
            }else {
                edit.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD);
            }
            edit.setSelection(edit.getText().toString().length());
        }
    }

}
