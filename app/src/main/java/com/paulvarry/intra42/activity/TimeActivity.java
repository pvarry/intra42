package com.paulvarry.intra42.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.GridView;

import com.paulvarry.intra42.Adapter.GridAdapterTimeTool;
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;

public class TimeActivity extends AppCompatActivity {

    private final Handler timerHandler = new Handler();
    private AppClass app;
    private GridView gridView;
    private Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {

            GridAdapterTimeTool adapterTimeTool = new GridAdapterTimeTool(TimeActivity.this, app.allCampus);
            gridView.setAdapter(adapterTimeTool);

            timerHandler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        app = (AppClass) getApplication();

        gridView = (GridView) findViewById(R.id.gridViewTime);
        GridAdapterTimeTool adapterTimeTool = new GridAdapterTimeTool(this, app.allCampus);
        gridView.setAdapter(adapterTimeTool);

        timerHandler.postDelayed(timerRunnable, 0);

    }

    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
