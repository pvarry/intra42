package com.paulvarry.intra42.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.widget.GridView;

import com.paulvarry.intra42.Adapter.GridAdapterTimeTool;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.ui.BasicActivity;

public class TimeActivity extends BasicActivity {

    private final Handler timerHandler = new Handler();
    private GridView gridView;
    private GridAdapterTimeTool adapterTimeTool;
    private Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            adapterTimeTool.notifyDataSetInvalidated();
            adapterTimeTool.notifyDataSetChanged();

            timerHandler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    @Nullable
    @Override
    public String getUrlIntra() {
        return null;
    }

    @Override
    public boolean getDataOnOtherThread() {
        return false;
    }

    @Override
    public boolean getDataOnMainThread() {
        return true;
    }

    @Override
    public String getToolbarName() {
        return getString(R.string.time);
    }

    @Override
    public void setViewContent() {

        gridView = (GridView) coordinatorLayout.findViewById(R.id.gridViewTime);
        adapterTimeTool = new GridAdapterTimeTool(this, app.allCampus);
        gridView.setAdapter(adapterTimeTool);

        timerHandler.postDelayed(timerRunnable, 0);

    }

    @Override
    public int getViewContentResID() {
        return R.layout.activity_content_time;
    }

    @Override
    public String getEmptyText() {
        return null;
    }
}
