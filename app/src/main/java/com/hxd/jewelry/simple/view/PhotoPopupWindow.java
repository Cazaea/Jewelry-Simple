package com.hxd.jewelry.simple.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

import com.hxd.jewelry.simple.R;

/**
 * Created by Lister on 2017-icon_home_06-12.
 * 自定义 popupWindow 类
 */

public class PhotoPopupWindow extends PopupWindow {

    private View mMenuView; // PopupWindow 菜单布局
    private Context context; // 上下文参数
    private View.OnClickListener takeOnClick;
    private View.OnClickListener chooseOnClick;

    public PhotoPopupWindow(Activity context,
                            View.OnClickListener takeOnClick, View.OnClickListener chooseOnClick) {
        super(context);
        this.context = context;
        this.takeOnClick = takeOnClick;
        this.chooseOnClick = chooseOnClick;
        Init();
    }

    private void Init() {
        /**
         * popupWindow 导入
         */
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.layout_photo_popup_window, null);
        Button btn_camera = (Button) mMenuView
                .findViewById(R.id.icon_btn_camera);
        Button btn_photo = (Button) mMenuView
                .findViewById(R.id.icon_btn_select);
        Button btn_cancel = (Button) mMenuView
                .findViewById(R.id.icon_btn_cancel);

        btn_camera.setOnClickListener(takeOnClick);
        btn_photo.setOnClickListener(chooseOnClick);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        /**
         * 导入布局
         */
        this.setContentView(mMenuView);
        /**
         * 设置动画效果
         */
        this.setAnimationStyle(R.style.popup_window_anim_style);
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        /**
         * 设置可触
         */
        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x0000000);
        this.setBackgroundDrawable(dw);
        /**
         * 单击 popupWindow 以外即关闭
         */
        mMenuView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height = mMenuView.findViewById(R.id.ll_pop).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }
}
