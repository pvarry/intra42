package com.paulvarry.intra42.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.RecyclerItem;
import com.paulvarry.intra42.adapters.SimpleHeaderRecyclerAdapter;
import com.paulvarry.intra42.api.cantina.MarvinMeals;
import com.paulvarry.intra42.ui.BasicThreadActivity;
import com.paulvarry.intra42.utils.DateTool;
import com.paulvarry.intra42.utils.Tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Response;

public class MarvinMealsActivity extends BasicThreadActivity implements BasicThreadActivity.GetDataOnThread {

    List<MarvinMeals> marvinMealList;
    private List<RecyclerItem<MarvinMeals>> items;

    public static void openIt(Context context) {
        Intent intent = new Intent(context, MarvinMealsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setContentView(R.layout.activity_marvin_meal);
        super.setActionBarToggle(ActionBarToggle.HAMBURGER);

        registerGetDataOnOtherThread(this);

        MenuItem menuItem = navigationView.getMenu().getItem(5).getSubMenu().getItem(3);
        if (menuItem != null)
            menuItem.setChecked(true);

        super.onCreateFinished();
    }

    /**
     * This is call when the user want to open this view on true Intra. Is triggered at the beginning to know if you want activate "show web version" on menu.
     *
     * @return The urls (on intra) to this page.
     */
    @Nullable
    @Override
    public String getUrlIntra() {
        return getString(R.string.base_url_cantina);
    }

    /**
     * Triggered when the activity start.
     * <p>
     * This method is run on main Thread, so you can make api call.
     */
    @Override
    public void getDataOnOtherThread() throws UnauthorizedException, ErrorServerException, IOException {

        Response<List<MarvinMeals>> response = app.getApiServiceCantina().getMeals().execute();
        if (Tools.apiIsSuccessful(response)) {
            marvinMealList = response.body();
            Collections.sort(marvinMealList, new Comparator<MarvinMeals>() {
                @Override
                public int compare(MarvinMeals o1, MarvinMeals o2) {
                    return o1.beginAt.after(o2.beginAt) ? 1 : -1;
                }
            });

            items = new ArrayList<>();

            MarvinMeals last = null;
            for (MarvinMeals m : marvinMealList) {
                if (last == null || !DateTool.sameDayOf(m.beginAt, last.beginAt))
                    items.add(new RecyclerItem<MarvinMeals>(DateTool.getDateLong(m.beginAt)));
                items.add(new RecyclerItem<>(m));
                last = m;
            }
        }
    }

    /**
     * Use to get the text on the toolbar, triggered when the activity start and after {@link GetDataOnThread#getDataOnOtherThread()} (only if it return true).
     *
     * @return Return the text on the toolbar.
     */
    @Override
    public String getToolbarName() {
        return null;
    }

    /**
     * Run when activity build view, just after getting data.
     */
    @Override
    public void setViewContent() {

        if (items == null || items.isEmpty()) {
            setViewState(StatusCode.EMPTY);
            return;
        }

        RecyclerView listView = findViewById(R.id.listView);
        SimpleHeaderRecyclerAdapter<MarvinMeals> adapterMarvinMeal = new SimpleHeaderRecyclerAdapter<>(this, items);
        listView.setAdapter(adapterMarvinMeal);
        listView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * This text is useful when both {@link GetDataOnThread#getDataOnOtherThread()} and {@link BasicThreadActivity.GetDataOnMain#getDataOnMainThread()} return false.
     *
     * @return A simple text to display on screen, may return null;
     */
    @Override
    public String getEmptyText() {
        return null;
    }
}
