package com.paulvarry.intra42.notifications;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.paulvarry.intra42.ApiService;
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.BuildConfig;
import com.paulvarry.intra42.Tools.ApiParams;
import com.paulvarry.intra42.Tools.AppSettings;
import com.paulvarry.intra42.Tools.DateTool;
import com.paulvarry.intra42.Tools.Pagination;
import com.paulvarry.intra42.api.Events;
import com.paulvarry.intra42.api.ScaleTeams;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IntentServiceNotifications extends IntentService {

    SharedPreferences settings;

    public IntentServiceNotifications() {
        super("IntentServiceNotifications");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        AppClass app = (AppClass) getApplication();

        if (app != null) {

            ApiService apiService = app.getApiService();
            settings = PreferenceManager.getDefaultSharedPreferences(this);

            if (AppSettings.Notifications.getNotificationsEvents(settings))
                notifyEvents(app, apiService);
            if (AppSettings.Notifications.getNotificationsScales(settings)) {
                notifyScales(app, apiService);
                notifyImminentScales(app, apiService);
            }
        }
    }

    void notifyEvents(AppClass app, ApiService apiService) {
        SharedPreferences sharedPreferences = ApiParams.getSharedPreferences(this);
        final Call<List<Events>> events;

        String date = NotificationsTools.getDateSince(settings);
        if (date == null)
            return;
        int campus = ApiParams.getCampus(sharedPreferences);
        int cursus = ApiParams.getCursus(sharedPreferences);

        if (BuildConfig.DEBUG && false)
            events = apiService.getEventCreatedAt("2016-09-28T22:14:29.224Z,2016-09-29T22:29:29.232Z", 1);
        else if (cursus != -1 && cursus != 0 && campus != -1 && campus != 0)
            events = apiService.getEventCreatedAt(campus, cursus, date, Pagination.getPage(null));
        else if (cursus != -1 && cursus != 0)
            events = apiService.getEventCreatedAtCursus(cursus, date, Pagination.getPage(null));
        else if (campus != -1 && campus != 0)
            events = apiService.getEventCreatedAtCampus(campus, date, Pagination.getPage(null));
        else
            events = apiService.getEventCreatedAt(date, Pagination.getPage(null));

        events.enqueue(new Callback<List<Events>>() {
            @Override
            public void onResponse(Call<List<Events>> call, Response<List<Events>> response) {
                if (response.isSuccessful()) {
                    for (Events e : response.body()) {
                        NotificationsTools.send(getBaseContext(), e);
                    }

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
//                    NotificationsTools.send(getBaseContext(), events1);
                }
            }

            @Override
            public void onFailure(Call<List<Events>> call, Throwable t) {

            }
        });
    }

    void notifyScales(final AppClass app, ApiService apiService) {

        Call<List<ScaleTeams>> scaleTeams;
        if (BuildConfig.DEBUG && false)
            scaleTeams = apiService.getScaleTeamsMe("2016-09-15T22:14:29.224Z,2016-09-16T22:29:29.232Z", 1);
        else
            scaleTeams = apiService.getScaleTeamsMe(NotificationsTools.getDateSince(settings), 1);


        scaleTeams.enqueue(new Callback<List<ScaleTeams>>() {
            @Override
            public void onResponse(Call<List<ScaleTeams>> call, Response<List<ScaleTeams>> response) {
                if (response.isSuccessful()) {
                    for (ScaleTeams s : response.body()) {
                        NotificationsTools.send(app, s, false);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ScaleTeams>> call, Throwable t) {

            }
        });
    }

    void notifyImminentScales(final AppClass app, ApiService apiService) {

        Date date_future = new Date(new Date().getTime() + (60000 * 15));
        String date = DateTool.getNowUTC() + "," + DateTool.getUTC(date_future);

        Call<List<ScaleTeams>> scaleTeams = apiService.getScaleTeamsMeBegin(date, 1);

        scaleTeams.enqueue(new Callback<List<ScaleTeams>>() {
            @Override
            public void onResponse(Call<List<ScaleTeams>> call, Response<List<ScaleTeams>> response) {
                if (response.isSuccessful()) {
                    for (ScaleTeams s : response.body()) {
                        NotificationsTools.send(app, s, true);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ScaleTeams>> call, Throwable t) {

            }
        });
    }
}
