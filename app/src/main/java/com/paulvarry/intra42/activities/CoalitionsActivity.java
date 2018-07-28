package com.paulvarry.intra42.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.paulvarry.intra42.GraphLabelFormatter;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.RecyclerAdapterCoalitionsBlocs;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.ApiServiceAuthServer;
import com.paulvarry.intra42.api.model.Coalitions;
import com.paulvarry.intra42.api.model.CoalitionsBlocs;
import com.paulvarry.intra42.api.model.CoalitionsDataIntra;
import com.paulvarry.intra42.ui.BasicThreadActivity;
import com.paulvarry.intra42.utils.AppSettings;
import com.paulvarry.intra42.utils.ThemeHelper;
import com.paulvarry.intra42.utils.Tools;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoalitionsActivity
        extends BasicThreadActivity
        implements BasicThreadActivity.GetDataOnThread {

    private RecyclerView recyclerView;
    private LineChart chartView;
    private ViewGroup viewGroupChartInfo;
    private ProgressBar progressBarChart;

    private CoalitionsBlocs blocs;
    private List<CoalitionsDataIntra> graphData;

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

        recyclerView = findViewById(R.id.recyclerView);
        chartView = findViewById(R.id.chartView);
        viewGroupChartInfo = findViewById(R.id.viewGroupChartInfo);
        progressBarChart = findViewById(R.id.progressBarChart);

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

        RecyclerAdapterCoalitionsBlocs adapter = new RecyclerAdapterCoalitionsBlocs(this, coalitions);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        viewGroupChartInfo.setVisibility(View.GONE);
        chartView.setVisibility(View.GONE);
        progressBarChart.setVisibility(View.VISIBLE);
        if (graphData != null)
            setViewContentGraph();
        else {
            ApiServiceAuthServer extraApi = app.getApiServiceAuthServer();
            Call<List<CoalitionsDataIntra>> call = extraApi.getCoalitionsStart(blocs.id);
            call.enqueue(new Callback<List<CoalitionsDataIntra>>() {
                @Override
                public void onResponse(Call<List<CoalitionsDataIntra>> call, Response<List<CoalitionsDataIntra>> response) {
                    if (Tools.apiIsSuccessfulNoThrow(response)) {
                        graphData = response.body();
                    }
                    setViewContentGraph();
                }

                @Override
                public void onFailure(Call<List<CoalitionsDataIntra>> call, Throwable t) {
                    setViewContentGraph();
                }
            });
        }
    }

    void setViewContentGraph() {
        progressBarChart.setVisibility(View.GONE);
        if (graphData != null) {
            viewGroupChartInfo.setVisibility(View.VISIBLE);
            chartView.setVisibility(View.VISIBLE);
            chartView.setDescription(null);
            chartView.getAxisRight().setEnabled(false);
            chartView.setKeepPositionOnRotation(true);
            chartView.getLegend().setTextColor(Color.WHITE);

            YAxis yAxis = chartView.getAxisLeft();
            yAxis.setAxisMinimum(0);
            yAxis.setValueFormatter(new GraphLabelFormatter());
            yAxis.setTextColor(Color.WHITE);

            XAxis xAxis = chartView.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setValueFormatter(new GraphValueFormatter());
            xAxis.setLabelRotationAngle(45);
            xAxis.setCenterAxisLabels(false);
            xAxis.setTextColor(Color.WHITE);

            // use the interface ILineDataSet
            List<ILineDataSet> dataSets = new ArrayList<>();

            for (CoalitionsDataIntra c : graphData) {

                long[][] chart = c.data;
                List<Entry> entryList = new ArrayList<>();
                for (long[] p : chart) {
                    entryList.add(new Entry(p[0], p[1]));
                }

                LineDataSet dataSet = new LineDataSet(entryList, c.name);
                dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                dataSet.setColor(Color.parseColor(c.color));
                dataSet.setDrawHighlightIndicators(false);
                dataSet.setDrawValues(false);
                dataSet.disableDashedLine();
                dataSet.setDrawCircles(false);

                dataSets.add(dataSet);
            }

            LineData lineData = new LineData(dataSets);

            chartView.setData(lineData);
            chartView.invalidate(); // refresh
        } else
            chartView.setVisibility(View.GONE);
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

        Response<List<CoalitionsBlocs>> responseCoalitions = api.getCoalitionsBlocs().execute();
        if (Tools.apiIsSuccessful(responseCoalitions)) {
            for (CoalitionsBlocs b : responseCoalitions.body()) {
                if (b.campusId == campus && b.cursusId == cursus) {
                    blocs = b;
                    break;
                }
            }
        }
    }

    private class GraphValueFormatter implements IAxisValueFormatter {

        DateFormat dd = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            Date date = new Date((long) value);
            return dd.format(date);
        }
    }
}
