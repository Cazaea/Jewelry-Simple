package com.hxd.jewelry.simple.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.hxd.jewelry.simple.R;

import java.util.Objects;

/**
 * 加载中等待框
 *
 * @author Cazaea
 * @time 2017/icon_home_11/17 14:48
 * @mail wistorm@sina.com
 */

public class LoadingDialog extends ProgressDialog {

    private TextView mHints;

    public LoadingDialog(Context context) {
        super(context);
        init(context);
    }

    public LoadingDialog(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化布局
        initView(getContext());
    }

    private void init(Context context) {
        //设置不可取消，点击其他区域不能取消，实际中可以抽出去封装供外包设置
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        // 拿到布局提示信息
        View view = LayoutInflater.from(context).inflate(R.layout.layout_loading_dialog, null);
        mHints = view.findViewById(R.id.tv_loading_dialog);

    }

    private void initView(Context context) {
        setContentView(R.layout.layout_loading_dialog);
        WindowManager.LayoutParams params = Objects.requireNonNull(getWindow()).getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
    }

    @Override
    public void show() {
        super.show();
    }

    public void setHints(String hints) {
        mHints.setText(hints);
    }
}
