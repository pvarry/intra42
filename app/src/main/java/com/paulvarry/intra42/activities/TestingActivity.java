package com.paulvarry.intra42.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.model.Events;
import com.paulvarry.intra42.notifications.NotificationsUtils;
import com.paulvarry.intra42.utils.DateTool;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestingActivity extends AppCompatActivity {

    AppClass app;
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        app = (AppClass) getApplication();
        apiService = app.getApiService();
    }

    public void notification(View view) {

        Date d = new Date(2018, 12, 10);
        Call<List<Events>> events = apiService.getEvent(DateTool.getNowUTC() + "," + DateTool.getUTC(d), 1);
        events.enqueue(new Callback<List<Events>>() {
            @Override
            public void onResponse(Call<List<Events>> call, Response<List<Events>> response) {
                if (response.isSuccessful()) {
                    NotificationsUtils.send(TestingActivity.this, response.body().get(0));
                    NotificationsUtils.send(TestingActivity.this, response.body().get(1));
                }
            }

            @Override
            public void onFailure(Call<List<Events>> call, Throwable t) {

            }
        });

        //                    Events events1 = new Events();
//                    events1.description = "lol";
//                    events1.beginAt = new Date();
//                    events1.endAt = new Date();
//                    events1.location = "location";
//                    events1.kind = "other";
//                    events1.name = "name";
//                    events1.maxPeople = "0";
//                    events1.nbrSubscribers = "0";
//
//                    NotificationsUtils.send(getBaseContext(), events1);

    }
}
