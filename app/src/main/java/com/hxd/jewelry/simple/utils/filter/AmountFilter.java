package com.hxd.jewelry.simple.utils.filter;

import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 金额输入过滤
 *
 * @author Cazaea
 * @time 2018/3/6 16:05
 * @mail wistorm@sina.com
 */
public class AmountFilter implements InputFilter {

    // 输入的最大金额
    private static final int MAX_VALUE = Integer.MAX_VALUE;
    // 小数点后的限制位数
    private static final int POINTER_LENGTH = 2;
    // 小数点
    private static final String POINTER = ".";

    private static final String ZERO = "0";

    Pattern mPattern;

    public AmountFilter() {
        // 用于匹配输入的是0-9  .  这几个数字和字符
        mPattern = Pattern.compile("([0-9]|\\.)*");
    }

    /**
     * @param source 新输入的字符串
     * @param start  新输入的字符串起始下标，一般为0
     * @param end    新输入的字符串终点下标，一般为source长度-1
     * @param dest   输入之前文本框内容
     * @param dStart 原内容起始坐标，一般为0
     * @param dEnd   原内容终点坐标，一般为dest长度-1
     * @return 输入内容
     */
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dStart, int dEnd) {
        String sourceText = source.toString();
        String destText = dest.toString();
        // 验证删除等按键
        if (TextUtils.isEmpty(sourceText)) {
            return "";
        }
        Matcher matcher = mPattern.matcher(source);
        // 已经输入小数点的情况下，只能输入数字
        if (destText.contains(POINTER)) {
            if (!matcher.matches()) {
                return "";
            } else {
                // 只能输入一个小数点
                if (POINTER.contentEquals(source)) {
                    return "";
                }
            }
            // 验证小数点精度，保证小数点后只能输入两位
            int index = destText.indexOf(POINTER);
            int length = destText.length() - index;
            // 如果长度大于2，并且新插入字符在小数点之后
            if (length > POINTER_LENGTH && index < dStart) {
                // 超出2位返回null
                return "";
            }
        } else {
            // 没有输入小数点的情况下，只能输入小数点和数字，但首位不能输入小数点
            if (!matcher.matches()) {
                return "";
            } else {
                // 第一个位置输入小数点的情况
                if ((POINTER.contentEquals(source)) && TextUtils.isEmpty(destText)) {
                    return "";
                }
            }
        }
        // 验证输入金额的大小
        double sumText = Double.parseDouble(destText + sourceText);
        if (sumText > MAX_VALUE) {
            return dest.subSequence(dStart, dEnd);
        }
        return dest.subSequence(dStart, dEnd) + sourceText;
    }


    /**
     * 金额监听器
     * 用户监听金额的输入，从而控制按钮
     */
    class MoneyWatcher implements TextWatcher {

        // 控制按钮显示隐藏
        private View view;

        public MoneyWatcher(View view) {
            this.view = view;
        }

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().trim().length() > 0) {
                final String m = s.toString();
                if (TextValidateUtil.isDouble(m)) {
                    Double money = Double.parseDouble(m);
                    if (money > 0) {
                        view.setEnabled(true);
                    }
                } else {
                    view.setEnabled(false);
                }
            } else {
                view.setEnabled(false);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
    }

}