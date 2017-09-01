package com.paulvarry.intra42.notifications;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.model.Announcements;
import com.paulvarry.intra42.api.model.Events;
import com.paulvarry.intra42.api.model.EventsUsers;
import com.paulvarry.intra42.api.model.ScaleTeams;
import com.paulvarry.intra42.utils.AppSettings;
import com.paulvarry.intra42.utils.DateTool;
import com.paulvarry.intra42.utils.Pagination;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IntentServiceNotifications extends IntentService {

    SharedPreferences settings;
    String dateFilter;

    public IntentServiceNotifications() {
        super("IntentServiceNotifications");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        AppClass app = (AppClass) getApplication();

        if (app != null) {

            settings = PreferenceManager.getDefaultSharedPreferences(this);
            dateFilter = NotificationsUtils.getDateSince(settings);
            Log.d("notification date", dateFilter);
            if (dateFilter == null)
                return;

            if (AppSettings.Notifications.getNotificationsEvents(settings))
                notifyEvents(app, app.getApiServiceDisableRedirectActivity());
            if (AppSettings.Notifications.getNotificationsScales(settings)) {
                notifyScales(app, app.getApiServiceDisableRedirectActivity());
                notifyImminentScales(app, app.getApiServiceDisableRedirectActivity());
            }
//            if (AppSettings.Notifications.getNotificationsAnnouncements(settings))
//                notifyAnnouncements(app, apiService);
        }
    }

    void notifyEvents(AppClass app, ApiService apiService) {
        final Call<List<Events>> events;

        int campus = AppSettings.getUserCampus(app);
        int cursus = AppSettings.getUserCursus(app);

        if (cursus != -1 && cursus != 0 && campus != -1 && campus != 0)
            events = apiService.getEventCreatedAt(campus, cursus, dateFilter, Pagination.getPage(null));
        else if (cursus != -1 && cursus != 0)
            events = apiService.getEventCreatedAtCursus(cursus, dateFilter, Pagination.getPage(null));
        else if (campus != -1 && campus != 0)
            events = apiService.getEventCreatedAtCampus(campus, dateFilter, Pagination.getPage(null));
        else
            events = apiService.getEventCreatedAt(dateFilter, Pagination.getPage(null));

        try {
            Response<List<Events>> responseEvents = events.execute();
            if (!responseEvents.isSuccessful())
                return;

            SparseArray<EventsUsers> list = EventsUsers.get(app, apiService, responseEvents.body());
            if (list != null) {
                for (Events e : responseEvents.body()) {
                    NotificationsUtils.notify(getBaseContext(), e, list.get(e.id));
                }
            } else {
                for (Events e : responseEvents.body()) {
                    NotificationsUtils.notify(getBaseContext(), e);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void notifyScales(final AppClass app, ApiService apiService) {

        Call<List<ScaleTeams>> scaleTeams;
        scaleTeams = apiService.getScaleTeamsMe(dateFilter, 1);

        Response<List<ScaleTeams>> response;
        try {
            response = scaleTeams.execute();
            if (response.isSuccessful()) {
                for (ScaleTeams s : response.body()) {
                    NotificationsUtils.notify(app, s, false);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void notifyImminentScales(final AppClass app, ApiService apiService) {

        Date date_future = new Date(new Date().getTime() + (60000 * 15));
        String date = DateTool.getNowUTC() + "," + DateTool.getUTC(date_future);

        Call<List<ScaleTeams>> scaleTeams = apiService.getScaleTeamsMeBegin(date, 1);

        try {
            Response<List<ScaleTeams>> response = scaleTeams.execute();
            if (response.isSuccessful()) {
                for (ScaleTeams s : response.body()) {
                    NotificationsUtils.notify(app, s, true);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void notifyAnnouncements(final AppClass app, ApiService apiService) {

        Call<List<Announcements>> call;
        call = apiService.getAnnouncements(dateFilter, 1);


        call.enqueue(new Callback<List<Announcements>>() {
            @Override
            public void onResponse(Call<List<Announcements>> call, Response<List<Announcements>> response) {
                if (response.isSuccessful()) {
                    for (Announcements announcements : response.body()) {
                        NotificationsUtils.notify(app, announcements);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Announcements>> call, Throwable t) {

            }
        });
    }
}
