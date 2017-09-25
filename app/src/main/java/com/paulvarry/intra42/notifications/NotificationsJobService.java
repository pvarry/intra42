package com.paulvarry.intra42.notifications;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.utils.AppSettings;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NotificationsJobService extends JobService {
    private static final int JOB_ID = 1;

    public static void schedule(Context context) {

        SharedPreferences settings = AppSettings.getSharedPreferences(context);
        int notificationsFrequency = AppSettings.Notifications.getNotificationsFrequency(settings);

        ComponentName component = new ComponentName(context, NotificationsJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, component)
                .setPeriodic(60000 * notificationsFrequency);

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        doMyWork(getBaseContext());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        // whether or not you would like JobScheduler to automatically retry your failed job.
        return false;
    }

    private void doMyWork(Context context) {
        // I am on the main thread, so if you need to do background work,
        // be sure to start up an AsyncTask, Thread, or IntentService!

        new Thread(new Runnable() {
            @Override
            public void run() {
                NotificationsUtils.run(getBaseContext(), (AppClass) getApplication());
            }
        }).start();
    }
}