package com.mobica.widgets.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageButton;

public class OvalButton extends AppCompatImageButton {

    private static final int ALPHA_30 = 76; // 76 (0-255)
    private static final int ALPHA_100 = 255; // 255 (0-255)

    public OvalButton(Context context) {
        super(context);
    }

    public OvalButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OvalButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void refreshTheme() {
        getBackground().applyTheme(getContext().getTheme());
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        int alpha = enabled ? ALPHA_100 : ALPHA_30;
        setImageAlpha(alpha);
    }
}
