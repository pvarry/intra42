package com.paulvarry.intra42.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.paulvarry.intra42.AppClass;

public class AutoStart extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && (action.contentEquals(Intent.ACTION_MY_PACKAGE_REPLACED) ||
                action.contentEquals(Intent.ACTION_BOOT_COMPLETED)))
            AppClass.scheduleAlarm(context);
    }

}