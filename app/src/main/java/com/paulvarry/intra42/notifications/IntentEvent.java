package com.paulvarry.intra42.notifications;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.model.EventsUsers;
import com.paulvarry.intra42.utils.Analytics;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IntentEvent extends IntentService {

    public static String ACTION = "ACTION";
    public static String CONTENT_EVENT_ID = "content_event_id";
    public static String CONTENT_EVENT_USER_ID = "content_event_user_id";

    public static String ACTION_CREATE = "CREATE";
    public static String ACTION_DELETE = "DELETE";

    public IntentEvent() {
        super("IntentServiceEvent");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        final AppClass app = (AppClass) getApplication();

        if (intent == null)
            return;

        Bundle bundle = intent.getExtras();
        if (bundle == null)
            return;

        String action = bundle.getString(ACTION);
        final int eventId = bundle.getInt(CONTENT_EVENT_ID);
        int eventUserId = bundle.getInt(CONTENT_EVENT_USER_ID);

        if (app == null || action == null || eventId == 0)
            return;

        ApiService api = app.getApiService();

        if (action.contentEquals(ACTION_CREATE)) {
            api.createEventsUsers(eventId, app.me.id).enqueue(new Callback<EventsUsers>() {
                @Override
                public void onResponse(Call<EventsUsers> call, Response<EventsUsers> response) {
                    if (response.isSuccessful())
                        NotificationsUtils.notify(app, response.body().event, null, true);
                }

                @Override
                public void onFailure(Call<EventsUsers> call, Throwable t) {

                }
            });
            Analytics.eventSubscribe(eventId, app.me.id, Analytics.EventSource.NOTIFICATION);
        } else if (action.contentEquals(ACTION_DELETE)) {
            api.deleteEventsUsers(eventUserId).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful())
                        NotificationManagerCompat.from(app).cancel(app.getString(R.string.notifications_events_unique_id), eventId);
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                }
            });
            Analytics.eventUnsubscribe(eventId, app.me.id, Analytics.EventSource.NOTIFICATION);
        }
    }
}
