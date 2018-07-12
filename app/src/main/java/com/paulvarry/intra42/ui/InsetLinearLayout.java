package com.paulvarry.intra42.ui;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.WindowInsets;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;

public class InsetLinearLayout extends LinearLayout {

    public InsetLinearLayout(Context context) {
        super(context);
    }

    public InsetLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public InsetLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override

    public WindowInsets onApplyWindowInsets(WindowInsets insets) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            int insetBottom = insets.getSystemWindowInsetBottom();

            insets = insets.replaceSystemWindowInsets(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(), insets.getSystemWindowInsetRight(), 0);

            WindowInsets used = super.onApplyWindowInsets(insets);
            return insets.replaceSystemWindowInsets(used.getSystemWindowInsetLeft(), used.getSystemWindowInsetTop(), used.getSystemWindowInsetRight(), insetBottom);
        }
        return super.onApplyWindowInsets(insets);
    }
}
