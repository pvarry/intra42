package com.paulvarry.intra42.bottomSheet;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.UsersLTE;
import org.parceler.Parcels;

public class BottomSheetUserDialogFragment extends BottomSheetDialogFragment {

    BottomSheetUserDialogFragment dialogFragment;
    Activity activity;
    UsersLTE mUser;

    ImageView imageViewProfile;
    TextView textViewName;

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    public static BottomSheetUserDialogFragment newInstance(UsersLTE user) {
        BottomSheetUserDialogFragment fragment = new BottomSheetUserDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("lol", Parcels.wrap(user));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialogFragment = this;

        activity = getActivity();
        if (getArguments() != null) {

            mUser = Parcels.unwrap(getArguments().getParcelable("lol"));
        }
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_bottom_sheet_user, null);
        dialog.setContentView(contentView);

        imageViewProfile = contentView.findViewById(R.id.imageViewProfile);
        textViewName = contentView.findViewById(R.id.textViewName);

        textViewName.setText(mUser.login);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }


}