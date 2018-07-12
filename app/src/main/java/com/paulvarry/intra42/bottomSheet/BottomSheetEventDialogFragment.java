package com.paulvarry.intra42.bottomSheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.ServiceGenerator;
import com.paulvarry.intra42.api.model.Events;
import com.paulvarry.intra42.fragments.EventFragment;
import com.paulvarry.intra42.ui.ListenedBottomSheetDialogFragment;

public class BottomSheetEventDialogFragment extends ListenedBottomSheetDialogFragment {

    private static final String ARG_EVENT = "event";

    private String json;

    public static BottomSheetEventDialogFragment newInstance(Events event) {
        BottomSheetEventDialogFragment fragment = new BottomSheetEventDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT, ServiceGenerator.getGson().toJson(event));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
            json = getArguments().getString(ARG_EVENT);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_event, container, false);

        EventFragment fragment = EventFragment.newInstance(json);
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();

        return view;
    }
}