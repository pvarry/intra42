package com.paulvarry.intra42.BottomSheet;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.paulvarry.intra42.ApiService;
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.Tools.AppSettings;
import com.paulvarry.intra42.Tools.DateTool;
import com.paulvarry.intra42.Tools.Tag;
import com.paulvarry.intra42.api.Events;
import com.paulvarry.intra42.api.EventsUsers;
import com.paulvarry.intra42.oauth.ServiceGenerator;
import com.veinhorn.tagview.TagView;

import java.util.List;

import in.uncod.android.bypass.Bypass;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottomSheetEventDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private static final String ARG_EVENT = "event";

    Button buttonSubscribe;
    LinearLayout linearLayoutProgress;
    ProgressBar progressBarButton;

    AppClass appClass;
    ApiService api;
    Call<List<EventsUsers>> listCallEventsUsers;
    private Events event;
    private EventsUsers eventsUsers;
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

    private Callback<List<EventsUsers>> callback = new Callback<List<EventsUsers>>() {
        @Override
        public void onResponse(Call<List<EventsUsers>> call, Response<List<EventsUsers>> response) {
            if (response.isSuccessful()) {
                if (!response.body().isEmpty())
                    eventsUsers = response.body().get(0);
                linearLayoutProgress.setVisibility(View.INVISIBLE);
                progressBarButton.setVisibility(View.GONE);
                buttonSubscribe.setEnabled(true);

                if (eventsUsers == null)
                    buttonSubscribe.setText(R.string.subscribe);
                else
                    buttonSubscribe.setText(R.string.unsubscribe);

                if (call.request().method().equals("POST"))
                    Toast.makeText(getContext(), R.string.subscribed, Toast.LENGTH_SHORT).show();
                else if (call.request().method().equals("DELETE"))
                    Toast.makeText(getContext(), "Unsuscibed", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<List<EventsUsers>> call, Throwable t) {
            Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            linearLayoutProgress.setVisibility(View.INVISIBLE);
            progressBarButton.setVisibility(View.GONE);
            buttonSubscribe.setEnabled(true);
        }
    };

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
            event = ServiceGenerator.getGson().fromJson(getArguments().getString(ARG_EVENT), Events.class);
        }
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_bottom_sheet_event, null);
        dialog.setContentView(contentView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        appClass = (AppClass) getActivity().getApplication();
        api = appClass.getApiService();

        TextView textViewTitle = (TextView) contentView.findViewById(R.id.textViewTitle);
        TagView tagViewKind = (TagView) contentView.findViewById(R.id.tagViewKind);
        TextView textViewDate = (TextView) contentView.findViewById(R.id.textViewDate);
        TextView textViewTime = (TextView) contentView.findViewById(R.id.textViewTime);
        TextView textViewPlace = (TextView) contentView.findViewById(R.id.textViewPlace);
        TextView textViewPeople = (TextView) contentView.findViewById(R.id.textViewPeople);
        TextView textViewDescription = (TextView) contentView.findViewById(R.id.textViewDescription);
        buttonSubscribe = (Button) contentView.findViewById(R.id.buttonSubscribe);
        linearLayoutProgress = (LinearLayout) contentView.findViewById(R.id.linearLayoutProgress);
        progressBarButton = (ProgressBar) contentView.findViewById(R.id.progressBarButton);

        Tag.setTagEvent(event, tagViewKind);
        textViewTitle.setText(event.name);
        textViewTitle.setBackgroundColor(tagViewKind.getTagColor());

        String date = DateTool.getTodayTomorrow(getContext(), event.beginAt, true);

        if (DateTool.sameDayOf(event.beginAt, event.endAt)) {
            date += DateTool.getDateLong(event.beginAt);
            textViewDate.setText(date);
            String time = DateTool.getTimeShort(event.beginAt) + " - " + DateTool.getTimeShort(event.endAt);
            textViewTime.setText(time);
        } else {
            date += DateTool.getDateTimeLong(event.beginAt);
            textViewDate.setText(date);
            String time = DateTool.getDateTimeLong(event.endAt);
            textViewTime.setText(time);
        }

        textViewPlace.setText(event.location);
        String people = event.nbrSubscribers + " / " + event.maxPeople;
        textViewPeople.setText(people);

        if (AppSettings.Advanced.getAllowMarkdownRenderer(getContext())) {
            Bypass bypass = new Bypass(getContext());
            CharSequence string = bypass.markdownToSpannable(event.description);
            textViewDescription.setText(string);
            textViewDescription.setMovementMethod(LinkMovementMethod.getInstance());
        } else
            textViewDescription.setText(event.description);

        buttonSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Soon", Toast.LENGTH_SHORT).show();
            }
        });

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

        progressBarButton.setVisibility(View.GONE);
        buttonSubscribe.setEnabled(false);
        linearLayoutProgress.setVisibility(View.VISIBLE);
        listCallEventsUsers = api.getEventsUsers(appClass.me.id, event.id);
        listCallEventsUsers.enqueue(callback);

        buttonSubscribe.setOnClickListener(this);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

        if (linearLayoutProgress != null)
            listCallEventsUsers.cancel();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        progressBarButton.setVisibility(View.VISIBLE);
        buttonSubscribe.setEnabled(false);
        if (eventsUsers == null)
            api.createEventsUsers(event.id, appClass.me.id).enqueue(callback);
        else
            api.deleteEventsUsers(eventsUsers.id).enqueue(callback);
    }
}