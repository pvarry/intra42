package com.paulvarry.intra42.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.widget.ListView;

import com.paulvarry.intra42.Adapter.ListAdapterMarvinMeal;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.cantina.MarvinMeals;
import com.paulvarry.intra42.ui.BasicActivity;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

public class MarvinMealsActivity extends BasicActivity {

    List<MarvinMeals> marvinMealList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setContentView(R.layout.activity_marvin_meal);
        super.activeHamburger();
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
        return "cantina.42.us.org";
    }

    /**
     * Triggered when the activity start, after {@link BasicActivity#getDataOnMainThread()}.
     * <p>
     * This method is run on a Thread, so you can make API calls and other long stuff.
     *
     * @return Return true if something append on this method.
     */
    @Override
    public boolean getDataOnOtherThread() {

        try {
            Response<List<MarvinMeals>> response = app.getApiServiceCantina().getMeals().execute();
            if (response.isSuccessful())
                marvinMealList = response.body();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Triggered when the activity start.
     * <p>
     * This method is run on main Thread, so you can make api call.
     *
     * @return Return true if something append on this method, if false -> the activity run {@link BasicActivity#getDataOnOtherThread()}.
     */
    @Override
    public boolean getDataOnMainThread() {
        return false;
    }

    /**
     * Use to get the text on the toolbar, triggered when the activity start and after {@link BasicActivity#getDataOnOtherThread()} (only if it return true).
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
        ListView listView = (ListView) findViewById(R.id.listView);
        ListAdapterMarvinMeal adapterMarvinMeal = new ListAdapterMarvinMeal(this, marvinMealList);
        listView.setAdapter(adapterMarvinMeal);
    }

    /**
     * This text is useful when both {@link BasicActivity#getDataOnMainThread()} and {@link BasicActivity#getDataOnOtherThread()} return false.
     *
     * @return A simple text to display on screen, may return null;
     */
    @Override
    public String getEmptyText() {
        return null;
    }
}
