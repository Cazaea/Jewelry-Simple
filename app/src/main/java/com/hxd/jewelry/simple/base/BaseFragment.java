package com.hxd.jewelry.simple.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 作 者： Cazaea
 * 日 期： 2018/4/27
 * 邮 箱： wistorm@sina.com
 */

public class BaseFragment extends Fragment implements View.OnClickListener {

    // 上下文对象
    public Context context;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initViews();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void initViews() {
        this.context = this.getActivity();
    }

    @Override
    public void onClick(View view) {

    }
}
