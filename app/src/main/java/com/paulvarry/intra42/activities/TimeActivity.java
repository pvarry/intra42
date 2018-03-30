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
import com.paulvarry.intra42.ui.BasicThreadActivity;

import java.util.List;

public class TimeActivity extends BasicThreadActivity implements BasicThreadActivity.GetDataOnThread, BasicThreadActivity.GetDataOnMain {

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
        super.onCreate(savedInstanceState);

        super.setContentView(R.layout.activity_content_time);
        super.setActionBarToggle(ActionBarToggle.HAMBURGER);

        registerGetDataOnOtherThread(this);
        registerGetDataOnMainTread(this);

        navigationView.getMenu().getItem(5).getSubMenu().getItem(1).setChecked(true);

        super.onCreateFinished();
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
    public void getDataOnOtherThread() throws ErrorServerException {
        campusList = CacheCampus.getAllowInternet(app.cacheSQLiteHelper, app);
        if (campusList == null || campusList.size() == 0)
            throw new ErrorServerException();
    }

    @Override
    public ThreadStatusCode getDataOnMainThread() {
        campusList = CacheCampus.get(app.cacheSQLiteHelper);
        if (campusList == null || campusList.size() == 0)
            return ThreadStatusCode.CONTINUE;
        return ThreadStatusCode.FINISH;
    }

    @Override
    public String getToolbarName() {
        return getString(R.string.title_activity_time);
    }

    @Override
    public void setViewContent() {

        if (campusList == null)
            return;

        GridView gridView = coordinatorLayout.findViewById(R.id.gridViewTime);
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
