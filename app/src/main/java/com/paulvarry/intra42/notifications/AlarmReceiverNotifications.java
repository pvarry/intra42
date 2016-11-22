package com.paulvarry.intra42.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiverNotifications extends BroadcastReceiver {

    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = "com.paulvarry.intra42.servicesdemo.alarm";

    public AlarmReceiverNotifications() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, IntentServiceNotifications.class);
        context.startService(i);
    }
}