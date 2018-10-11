package com.hxd.jewelry.simple.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;

import com.hxd.jewelry.simple.R;

import java.util.Objects;

/**
 * 可取消的加载框
 * Created by Cazaea on 2017/7/icon_home_11.
 */

public class CustomDialog extends ProgressDialog {
    public CustomDialog(Context context) {
        super(context);
    }

    public CustomDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init(getContext());
    }

    private void init(Context context) {
        // 设置可取消
        setCancelable(true);
        // 点击其他区域不能取消
        setCanceledOnTouchOutside(false);

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
}
