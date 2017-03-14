package com.paulvarry.intra42.ui;

import android.content.DialogInterface;
import android.support.design.widget.BottomSheetDialogFragment;

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

}
