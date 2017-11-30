package com.paulvarry.intra42.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class AlarmReceiverNotifications extends BroadcastReceiver {

    public static final int REQUEST_CODE = 12345;

    public AlarmReceiverNotifications() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Intent i = new Intent(context, IntentServiceNotifications.class);
            context.startService(i);
        }
    }
}