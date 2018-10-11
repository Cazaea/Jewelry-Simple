package com.hxd.jewelry.simple.view;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.widget.EditText;

/**
 * 动态控制输入框粘贴内容
 * 作 者： Cazaea
 * 日 期： 2018/6/27
 * 邮 箱： wistorm@sina.com
 */
@SuppressLint("AppCompatCustomView")
public class NormalEditText extends EditText {
    public NormalEditText(Context context) {
        super(context);
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        if (id == android.R.id.paste) {
            // 只设置粘贴文本
            int lastCursorPosition = getSelectionStart();
            // 拿到粘贴板的文本,setSpan的时候第二个参数last+文本的长度
            ClipboardManager clip = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            String text = clip != null ? clip.getPrimaryClip().getItemAt(0).getText().toString() : null;
            // 之后,设置光标的时候,填这第二个参数即可
            super.onTextContextMenuItem(android.R.id.paste);
            SpannableString ss = new SpannableString(getText());
            // 这里之所以分两种情况是因为android系统的粘贴,为了用户体验,会在粘贴的文本前后加上空格,表示是粘贴的内容
            // 如果在文本中间粘贴,会在粘贴文本前后都加上空格;如果在文末粘贴,会在粘贴文本前加上空格;如果空的内容中粘贴,则不加空格
            assert text != null;
            if (lastCursorPosition != 0) {
                ss.setSpan(new StyleSpan(Typeface.BOLD), lastCursorPosition + 1, lastCursorPosition + 1 + text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                setText(ss);
                NormalEditText.this.setSelection(lastCursorPosition + 1 + text.length());
            } else {
                ss.setSpan(new StyleSpan(Typeface.BOLD), lastCursorPosition, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                setText(ss);
                NormalEditText.this.setSelection(text.length());
            }
            return true;
        }
        return super.onTextContextMenuItem(id);
    }
}
