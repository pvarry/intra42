package com.paulvarry.intra42.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.ServiceGenerator;
import com.paulvarry.intra42.api.model.Events;
import com.paulvarry.intra42.fragments.EventFragment;
import com.paulvarry.intra42.utils.Calendar;
import com.paulvarry.intra42.utils.Tag;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventActivity extends AppCompatActivity implements EventFragment.OnFragmentInteractionListener {

    public static final String ARG_EVENT = "event";
    private Events event;
    private int id;

    private ViewGroup layoutLoading;
    private ViewGroup layoutOnError;
    private EventFragment eventFragment;

    public static Intent getIntent(Context context, Events event) {
        Intent intent = new Intent(context, EventActivity.class);
        intent.putExtra(EventActivity.ARG_EVENT, ServiceGenerator.getGson().toJson(event));
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            finish();
            return;
        }
        if (extras.containsKey(ARG_EVENT)) {
            event = ServiceGenerator.getGson().fromJson(extras.getString(ARG_EVENT), Events.class);
        } else if (extras.containsKey(CalendarContract.Events.CUSTOM_APP_URI)) {
            String appUri = extras.getString(CalendarContract.Events.CUSTOM_APP_URI);
            id = Calendar.getEventIdFromUri(appUri);
        }

        setContentView(R.layout.activity_event);

        layoutLoading = findViewById(R.id.layoutLoading);
        layoutOnError = findViewById(R.id.layoutOnError);
        layoutOnError.setVisibility(View.GONE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24px);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        refresh();
    }

    void refresh() {

        layoutOnError.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.GONE);

        if (event != null)
            setView();
        else if (id > 0)
            getData();
        else
            finish();
    }

    void setView() {
        layoutLoading.setVisibility(View.GONE);
        layoutOnError.setVisibility(View.GONE);

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        AppBarLayout appBar = findViewById(R.id.app_bar);
        collapsingToolbarLayout.setTitle(event.name);
        appBar.setBackgroundColor(Tag.getTagColor(event));

        FragmentManager manager = getSupportFragmentManager();

        if (eventFragment != null)
            manager.beginTransaction().remove(eventFragment).commitNow();

        eventFragment = EventFragment.newInstance(event);
        manager.beginTransaction()
                .add(R.id.fragment_container, eventFragment).commit();
    }

    public Events getData() {

        layoutLoading.setVisibility(View.VISIBLE);

        AppClass app = (AppClass) getApplication();
        Call<Events> call = app.getApiService().getEvent(id);
        call.enqueue(new Callback<Events>() {
            @Override
            public void onResponse(Call<Events> call, Response<Events> response) {
                if (response.isSuccessful()) {
                    event = response.body();
                    setView();
                } else
                    setViewError();
            }

            @Override
            public void onFailure(Call<Events> call, Throwable t) {
                setViewError();
            }
        });
        return null;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    void setViewError() {
        layoutLoading.setVisibility(View.GONE);
        layoutOnError.setVisibility(View.VISIBLE);

        TextView textViewStatus = findViewById(R.id.textViewError);
        Button buttonRefresh = findViewById(R.id.buttonRefresh);
        ImageView imageView = findViewById(R.id.imageViewStatus);

        textViewStatus.setText(R.string.info_error_on_loading_this_page);
        imageView.setImageResource(R.drawable.ic_server_broken_black);
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
    }
}
