package com.paulvarry.intra42.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.widget.ListView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.ListAdapterMarvinMeal;
import com.paulvarry.intra42.api.cantina.MarvinMeals;
import com.paulvarry.intra42.ui.BasicThreadActivity;
import com.paulvarry.intra42.utils.Tools;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

public class MarvinMealsActivity extends BasicThreadActivity implements BasicThreadActivity.GetDataOnThread {

    List<MarvinMeals> marvinMealList;

    public static void openIt(Context context) {
        Intent intent = new Intent(context, MarvinMealsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setContentView(R.layout.activity_marvin_meal);
        super.activeHamburger();

        registerGetDataOnOtherThread(this);

        super.onCreate(savedInstanceState);

        MenuItem menuItem = navigationView.getMenu().getItem(5).getSubMenu().getItem(3);
        if (menuItem != null)
            menuItem.setChecked(true);
    }

    /**
     * This is call when the user want to open this view on true Intra. Is triggered at the beginning to know if you want activate "show web version" on menu.
     *
     * @return The urls (on intra) to this page.
     */
    @Nullable
    @Override
    public String getUrlIntra() {
        return "https://cantina.42.us.org/";
    }

    /**
     * Triggered when the activity start.
     * <p>
     * This method is run on main Thread, so you can make api call.
     */
    @Override
    public void getDataOnOtherThread() throws UnauthorizedException, ErrorException, IOException {

        Response<List<MarvinMeals>> response = app.getApiServiceCantina().getMeals().execute();
        if (Tools.apiIsSuccessful(response)) {
            marvinMealList = response.body();
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

        if (marvinMealList == null || marvinMealList.isEmpty()) {
            setViewState(StatusCode.EMPTY);
            return;
        }


        ListView listView = findViewById(R.id.listView);
        ListAdapterMarvinMeal adapterMarvinMeal = new ListAdapterMarvinMeal(this, marvinMealList);
        listView.setAdapter(adapterMarvinMeal);
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
