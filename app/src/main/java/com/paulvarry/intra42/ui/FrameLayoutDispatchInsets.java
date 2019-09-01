package com.paulvarry.intra42.ui;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.WindowInsets;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

public class FrameLayoutDispatchInsets extends FrameLayout {

    public FrameLayoutDispatchInsets(Context context) {
        super(context);
    }

    public FrameLayoutDispatchInsets(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FrameLayoutDispatchInsets(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override

    public WindowInsets onApplyWindowInsets(WindowInsets insets) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {

            for (int i = 0; i < getChildCount(); i++)
                getChildAt(i).dispatchApplyWindowInsets(insets);
            return insets;
        }
        return super.onApplyWindowInsets(insets);
    }
}
