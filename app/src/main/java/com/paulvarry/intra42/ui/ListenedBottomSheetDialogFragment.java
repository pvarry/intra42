package com.paulvarry.intra42.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog showedDialog = (BottomSheetDialog) dialog;
                FrameLayout bottomSheet = showedDialog.findViewById(android.support.design.R.id.design_bottom_sheet);
                if (bottomSheet == null)
                    return;
                BottomSheetBehavior behaviour = BottomSheetBehavior.from(bottomSheet);
                behaviour.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                            Log.d("ex", "ex");
                        }
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                    }
                });
            }
        });
        return dialog;
    }
}
