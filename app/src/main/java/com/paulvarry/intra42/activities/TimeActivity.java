package com.paulvarry.intra42.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.widget.GridView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.GridAdapterTimeTool;
import com.paulvarry.intra42.api.model.Campus;
import com.paulvarry.intra42.cache.CacheCampus;
import com.paulvarry.intra42.ui.BasicActivity;

import java.util.List;

public class TimeActivity extends BasicActivity {

    private final Handler timerHandler = new Handler();
    private GridAdapterTimeTool adapterTimeTool;
    private Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            adapterTimeTool.notifyDataSetInvalidated();
            adapterTimeTool.notifyDataSetChanged();

            timerHandler.postDelayed(this, 500);
        }
    };

    private List<Campus> campusList;

    public static void openIt(Context context) {
        Intent intent = new Intent(context, TimeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setContentView(R.layout.activity_content_time);
        super.activeHamburger();
        super.onCreate(savedInstanceState);

        navigationView.getMenu().getItem(5).getSubMenu().getItem(1).setChecked(true);
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
        campusList = CacheCampus.getAllowInternet(app.cacheSQLiteHelper, app);
        return !(campusList == null || campusList.size() == 0);
    }

    @Override
    public boolean getDataOnMainThread() {
        campusList = CacheCampus.get(app.cacheSQLiteHelper);
        return !(campusList == null || campusList.size() == 0);
    }

    @Override
    public String getToolbarName() {
        return getString(R.string.time);
    }

    @Override
    public void setViewContent() {

        if (campusList == null)
            return;

        GridView gridView = (GridView) coordinatorLayout.findViewById(R.id.gridViewTime);
        adapterTimeTool = new GridAdapterTimeTool(this, campusList);
        gridView.setAdapter(adapterTimeTool);

        timerHandler.postDelayed(timerRunnable, 0);

    }

    @Override
    public String getEmptyText() {
        return null;
    }

    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
    }
}
