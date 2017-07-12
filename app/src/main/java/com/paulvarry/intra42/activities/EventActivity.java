package com.paulvarry.intra42.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.ServiceGenerator;
import com.paulvarry.intra42.api.model.Events;
import com.paulvarry.intra42.utils.DateTool;
import com.paulvarry.intra42.utils.Tag;
import com.paulvarry.intra42.utils.Tools;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

public class EventActivity extends AppCompatActivity {

    public static final String ARG_EVENT = "event";
    private Events event;

    public static Intent getIntent(Context context, Events event) {
        Intent intent = new Intent(context, EventActivity.class);
        intent.putExtra(EventActivity.ARG_EVENT, ServiceGenerator.getGson().toJson(event));
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle i = getIntent().getExtras();
        if (i.containsKey(ARG_EVENT)) {
            event = ServiceGenerator.getGson().fromJson(i.getString(ARG_EVENT), Events.class);
        }

        setContentView(R.layout.activity_event);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24px);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        AppBarLayout app_bar = findViewById(R.id.app_bar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });


//        TextView textViewTitle = (TextView) findViewById(R.id.textViewTitle);
//        TagView tagViewKind = (TagView) findViewById(R.id.tagViewKind);
        TextView textViewDate = findViewById(R.id.textViewDate);
        TextView textViewTime = findViewById(R.id.textViewTime);
        TextView textViewPlace = findViewById(R.id.textViewPlace);
        TextView textViewPeople = findViewById(R.id.textViewPeople);
        TextView textViewDescription = findViewById(R.id.textViewDescription);
        Button buttonSubscribe = findViewById(R.id.buttonSubscribe);

//        Tag.setTagEvent(event, tagViewKind);
        collapsingToolbarLayout.setTitle(event.name);
        app_bar.setBackgroundColor(Tag.getTagColor(event));

        String date = DateTool.getTodayTomorrow(this, event.beginAt, true);

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

        Tools.setMarkdown(this, textViewDescription, event.description);

        buttonSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EventActivity.this, "Soon", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_old, menu);
        return true;
    }
}
