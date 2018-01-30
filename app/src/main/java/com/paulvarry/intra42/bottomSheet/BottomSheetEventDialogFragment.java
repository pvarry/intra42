package com.paulvarry.intra42.bottomSheet;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.ServiceGenerator;
import com.paulvarry.intra42.api.model.Events;
import com.paulvarry.intra42.api.model.EventsUsers;
import com.paulvarry.intra42.fragments.EventFragment;
import com.paulvarry.intra42.ui.ListenedBottomSheetDialogFragment;
import com.paulvarry.intra42.utils.Tag;
import com.veinhorn.tagview.TagView;

import java.util.List;

import retrofit2.Call;

public class BottomSheetEventDialogFragment extends ListenedBottomSheetDialogFragment {

    private static final String ARG_EVENT = "event";

    Button buttonSubscribe;
    LinearLayout linearLayoutProgress;
    ProgressBar progressBarButton;

    AppClass appClass;
    ApiService api;
    Call<List<EventsUsers>> listCallEventsUsers;

    private Events event;
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

        if (getArguments() != null) {
            json = getArguments().getString(ARG_EVENT);
            event = ServiceGenerator.getGson().fromJson(json, Events.class);
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

        if (linearLayoutProgress != null)
            listCallEventsUsers.cancel();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_event, container, false);

        EventFragment fragment = EventFragment.newInstance(json);
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();

        appClass = (AppClass) getActivity().getApplication();
        api = appClass.getApiService();

        TextView textViewTitle = view.findViewById(R.id.textViewTitle);
        TagView tagViewKind = view.findViewById(R.id.tagViewKind);
        buttonSubscribe = view.findViewById(R.id.buttonSubscribe);
        linearLayoutProgress = view.findViewById(R.id.linearLayoutProgress);
        progressBarButton = view.findViewById(R.id.progressBarButton);

        Tag.setTagEvent(event, tagViewKind);
        textViewTitle.setText(event.name);
        textViewTitle.setBackgroundColor(tagViewKind.getTagColor());

        return view;
    }
}