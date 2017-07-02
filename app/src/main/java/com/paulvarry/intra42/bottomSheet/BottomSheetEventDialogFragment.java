package com.paulvarry.intra42.bottomSheet;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.ServiceGenerator;
import com.paulvarry.intra42.api.model.Events;
import com.paulvarry.intra42.api.model.EventsUsers;
import com.paulvarry.intra42.ui.ListenedBottomSheetDialogFragment;
import com.paulvarry.intra42.utils.AppSettings;
import com.paulvarry.intra42.utils.DateTool;
import com.paulvarry.intra42.utils.Tag;
import com.veinhorn.tagview.TagView;

import java.util.List;

import in.uncod.android.bypass.Bypass;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottomSheetEventDialogFragment extends ListenedBottomSheetDialogFragment implements View.OnClickListener {

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

            linearLayoutProgress.setVisibility(View.INVISIBLE);
            progressBarButton.setVisibility(View.GONE);
            buttonSubscribe.setEnabled(true);
            eventsUsers = null;
            if (response.isSuccessful()) {
                if (response.body() != null && !response.body().isEmpty())
                    eventsUsers = response.body().get(0);

                setButtonSubscribe();
                if (call.request().method().equals("DELETE"))
                    Toast.makeText(getContext(), R.string.unsubscribed, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<List<EventsUsers>> call, Throwable t) {
            Context context = getContext();
            if (context != null)
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            linearLayoutProgress.setVisibility(View.INVISIBLE);
            progressBarButton.setVisibility(View.GONE);
            buttonSubscribe.setEnabled(true);
        }
    };

    private Callback<EventsUsers> callbackSubscribe = new Callback<EventsUsers>() {
        @Override
        public void onResponse(Call<EventsUsers> call, Response<EventsUsers> response) {

            linearLayoutProgress.setVisibility(View.INVISIBLE);
            progressBarButton.setVisibility(View.GONE);
            buttonSubscribe.setEnabled(true);
            eventsUsers = null;
            if (response.isSuccessful()) {
                eventsUsers = response.body();
                setButtonSubscribe();
                Toast.makeText(getContext(), R.string.subscribed, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<EventsUsers> call, Throwable t) {
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
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

        if (linearLayoutProgress != null)
            listCallEventsUsers.cancel();
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_bottom_sheet_event, null);
        dialog.setContentView(contentView);

        appClass = (AppClass) getActivity().getApplication();
        api = appClass.getApiService();

        TextView textViewTitle = contentView.findViewById(R.id.textViewTitle);
        TagView tagViewKind = contentView.findViewById(R.id.tagViewKind);
        TextView textViewDate = contentView.findViewById(R.id.textViewDate);
        TextView textViewTime = contentView.findViewById(R.id.textViewTime);
        LinearLayout linearLayoutPlace = contentView.findViewById(R.id.linearLayoutPlace);
        TextView textViewPlace = contentView.findViewById(R.id.textViewPlace);
        TextView textViewPeople = contentView.findViewById(R.id.textViewPeople);
        TextView textViewDescription = contentView.findViewById(R.id.textViewDescription);
        buttonSubscribe = contentView.findViewById(R.id.buttonSubscribe);
        linearLayoutProgress = contentView.findViewById(R.id.linearLayoutProgress);
        progressBarButton = contentView.findViewById(R.id.progressBarButton);

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

        if (event.location == null || event.location.isEmpty())
            linearLayoutPlace.setVisibility(View.GONE);
        else {
            linearLayoutPlace.setVisibility(View.VISIBLE);
            textViewPlace.setText(event.location);
        }

        String people;
        if (event.maxPeople == 0)
            people = getString(R.string.subscription_unavailable);
        else
            people = String.valueOf(event.nbrSubscribers) + " / " + String.valueOf(event.maxPeople);
        textViewPeople.setText(people);

        if (AppSettings.Advanced.getAllowMarkdownRenderer(getContext())) {
            Bypass bypass = new Bypass(getContext());
            CharSequence string = bypass.markdownToSpannable(event.description);
            textViewDescription.setText(string);
            textViewDescription.setMovementMethod(LinkMovementMethod.getInstance());
        } else
            textViewDescription.setText(event.description);

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
            api.createEventsUsers(event.id, appClass.me.id).enqueue(callbackSubscribe);
        else
            api.deleteEventsUsers(eventsUsers.id).enqueue(callback);
    }

    void setButtonSubscribe() {
        linearLayoutProgress.setVisibility(View.INVISIBLE);
        progressBarButton.setVisibility(View.GONE);
        buttonSubscribe.setEnabled(true);

        if (eventsUsers == null)
            buttonSubscribe.setText(R.string.subscribe);
        else
            buttonSubscribe.setText(R.string.unsubscribe);

        if (eventsUsers == null && event.nbrSubscribers >= event.maxPeople) {

            if (event.maxPeople == 0)
                buttonSubscribe.setText(R.string.subscription_unavailable);
            else
                buttonSubscribe.setText(R.string.event_full);
            buttonSubscribe.setEnabled(false);
        }
    }
}