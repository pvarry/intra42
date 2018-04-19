package com.paulvarry.intra42.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.reflect.TypeToken;
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.GraphLabelFormatter;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.ServiceGenerator;
import com.paulvarry.intra42.api.model.CoalitionsDataIntra;
import com.paulvarry.intra42.api.model.Events;
import com.paulvarry.intra42.api.model.EventsUsers;
import com.paulvarry.intra42.notifications.NotificationsUtils;
import com.paulvarry.intra42.utils.AppSettings;
import com.paulvarry.intra42.utils.DateTool;
import com.paulvarry.intra42.utils.Pagination;
import com.paulvarry.intra42.utils.Tools;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;

public class TestingActivity extends AppCompatActivity {

    private AppClass app;
    private ApiService apiService;

    private LineChart chartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        app = (AppClass) getApplication();
        apiService = app.getApiService();

        chartView = findViewById(R.id.chartView);

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
        xAxis.setValueFormatter(new MyCustomFormatter());
        xAxis.setLabelRotationAngle(45);
        xAxis.setCenterAxisLabels(false);
        xAxis.setTextColor(Color.WHITE);

        InputStream ins = getResources().openRawResource(R.raw._coalitions);
        String file = Tools.readTextFile(ins);

        Type listType = new TypeToken<ArrayList<CoalitionsDataIntra>>() {
        }.getType();
        List<CoalitionsDataIntra> data = ServiceGenerator.getGson().fromJson(file, listType);

        // use the interface ILineDataSet
        List<ILineDataSet> dataSets = new ArrayList<>();

        for (CoalitionsDataIntra c : data) {

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

        // 25 000
        // 2 mois
        float scaleY = chartView.getYMax() / 25_000;
        float scaleX = (chartView.getXChartMax() - chartView.getXChartMin()) / 5.256e+9f;
//        chartView.zoom(scaleX, scaleY, 0, 0);
    }

    public void notification(View view) {

        new Thread(new Runnable() {
            @Override
            public void run() {


                Date d = new Date(2017, 7, 0);

                int campus = AppSettings.getUserCampus(app);
                int cursus = AppSettings.getUserCursus(app);
                Call<List<Events>> events;
                String dateFilter = "2017-07-01T00:00:00.000Z" + "," + DateTool.getNowUTC();

                if (cursus != -1 && cursus != 0 && campus != -1 && campus != 0)
                    events = apiService.getEventCreatedAt(campus, cursus, dateFilter, Pagination.getPage(null));
                else if (cursus != -1 && cursus != 0)
                    events = apiService.getEventCreatedAtCursus(cursus, dateFilter, Pagination.getPage(null));
                else if (campus != -1 && campus != 0)
                    events = apiService.getEventCreatedAtCampus(campus, dateFilter, Pagination.getPage(null));
                else
                    events = apiService.getEventCreatedAt(dateFilter, Pagination.getPage(null));

                try {
                    Response<List<Events>> responseEvent = events.execute();
                    if (!responseEvent.isSuccessful())
                        return;

                    Events a = responseEvent.body().get(0);
                    Events b = responseEvent.body().get(1);
                    SparseArray<EventsUsers> list;

                    list = EventsUsers.get(app, apiService, responseEvent.body());
                    if (list != null) {
                        NotificationsUtils.notify(TestingActivity.this, a, list.get(a.id));
                        NotificationsUtils.notify(TestingActivity.this, b, list.get(b.id));
                    } else {
                        NotificationsUtils.notify(TestingActivity.this, a, null);
                        NotificationsUtils.notify(TestingActivity.this, b, null);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void runJob(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NotificationsUtils.run(TestingActivity.this, (AppClass) getApplication());
            }
        }).start();
    }

    private class MyCustomFormatter implements IAxisValueFormatter {

        DateFormat dd = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            Date date = new Date((long) value);
            return dd.format(date);
        }
    }
}
