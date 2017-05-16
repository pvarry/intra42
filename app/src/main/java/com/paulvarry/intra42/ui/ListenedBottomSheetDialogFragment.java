package com.paulvarry.intra42.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.paulvarry.intra42.R;

public class ListenedBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private DialogInterface.OnDismissListener mTheListener;

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (mTheListener != null) {
            mTheListener.onDismiss(dialog);
        }
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener listen) {
        mTheListener = listen;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = getContext().getResources().getDimensionPixelSize(R.dimen.bottom_sheet_width);

            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(width > 0 ? width : ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }
        }
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        Window window = dialog.getWindow();
        if (window != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

}
