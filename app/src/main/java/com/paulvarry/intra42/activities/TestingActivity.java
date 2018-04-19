package com.paulvarry.intra42.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class TestingActivity extends AppCompatActivity {

    private AppClass app;
    private ApiService apiService;

    private GraphView graphView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        app = (AppClass) getApplication();
        apiService = app.getApiService();

        graphView = findViewById(R.id.graphView);

        InputStream ins = getResources().openRawResource(R.raw._coalitions);
        String file = Tools.readTextFile(ins);

        Type listType = new TypeToken<ArrayList<CoalitionsDataIntra>>() {
        }.getType();
        List<CoalitionsDataIntra> data = ServiceGenerator.getGson().fromJson(file, listType);

        for (CoalitionsDataIntra c : data) {
            long[][] chart = c.data;
            DataPoint[] points = new DataPoint[chart.length];

            for (int i = 0; i < chart.length; i++) {
                long[] p = chart[i];
                points[i] = new DataPoint(p[0], p[1]);
            }

            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
            series.setColor(Color.parseColor(c.color));
            graphView.addSeries(series);
        }

        Viewport viewport = graphView.getViewport();
        viewport.setScrollable(true);
        viewport.setScrollableY(true);
        viewport.setScalable(true);
        viewport.setYAxisBoundsManual(true);

        GridLabelRenderer labelRenderer = graphView.getGridLabelRenderer();
        labelRenderer.setLabelFormatter(new GraphLabelFormatter(this));
        labelRenderer.setHorizontalLabelsAngle(45);
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
}
