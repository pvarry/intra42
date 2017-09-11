package com.paulvarry.intra42.notifications;

import android.app.IntentService;
import android.content.Intent;

import com.paulvarry.intra42.AppClass;

public class IntentServiceNotifications extends IntentService {


    String dateFilter;

    public IntentServiceNotifications() {
        super("IntentServiceNotifications");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        NotificationsUtils.run(this, (AppClass) getApplication());
    }
}
