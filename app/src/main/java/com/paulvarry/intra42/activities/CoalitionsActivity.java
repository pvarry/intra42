package com.paulvarry.intra42.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import androidx.annotation.Nullable;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.ListAdapterCoalitionsBlocs;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.model.Coalitions;
import com.paulvarry.intra42.api.model.CoalitionsBlocs;
import com.paulvarry.intra42.ui.BasicThreadActivity;
import com.paulvarry.intra42.utils.AppSettings;
import com.paulvarry.intra42.utils.ThemeHelper;
import com.paulvarry.intra42.utils.Tools;
import retrofit2.Response;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CoalitionsActivity
        extends BasicThreadActivity
        implements BasicThreadActivity.GetDataOnThread {

    private ListView listview;
    private CoalitionsBlocs blocs;

    public static void openIt(Context context) {
        Intent intent = new Intent(context, CoalitionsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_coalitions);
        super.setActionBarToggle(ActionBarToggle.HAMBURGER);

        registerGetDataOnOtherThread(this);
        ThemeHelper.setActionBar(actionBar, AppSettings.Theme.EnumTheme.DEFAULT);

        listview = findViewById(R.id.listView);

        super.onCreateFinished();
    }

    @Nullable
    @Override
    public String getUrlIntra() {
        if (blocs != null)
            return String.format(getString(R.string.base_url_intra_profile_coalitions), blocs.id);
        else
            return getString(R.string.base_url_intra_profile);
    }

    @Override
    public String getToolbarName() {
        return null;
    }

    @Override
    protected void setViewContent() {
        if (blocs == null) {
            setViewState(StatusCode.EMPTY);
            return;
        }
        List<Coalitions> coalitions = blocs.coalitions;
        Collections.sort(coalitions, new Comparator<Coalitions>() {
            @Override
            public int compare(Coalitions o1, Coalitions o2) {
                if (o1.score == o2.score)
                    return 0;
                return o1.score < o2.score ? 1 : -1;
            }
        });

        ListAdapterCoalitionsBlocs adapter = new ListAdapterCoalitionsBlocs(this, coalitions);
        listview.setAdapter(adapter);
    }

    @Override
    public String getEmptyText() {
        return getString(R.string.coalitions_nothing_selected_cursus_campus);
    }

    @Override
    public void getDataOnOtherThread() throws IOException, RuntimeException {
        ApiService api = app.getApiService();
        int campus = AppSettings.getAppCampus(app);
        int cursus = AppSettings.getAppCursus(app);

        Response<List<CoalitionsBlocs>> response = api.getCoalitionsBlocs().execute();
        if (Tools.apiIsSuccessful(response)) {
            for (CoalitionsBlocs b : response.body()) {
                if (b.campusId == campus && b.cursusId == cursus) {
                    blocs = b;
                    return;
                }
            }
        }
    }
}
