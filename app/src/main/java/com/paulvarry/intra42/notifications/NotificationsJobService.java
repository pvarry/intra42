package com.paulvarry.intra42.notifications;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import androidx.annotation.RequiresApi;
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.utils.AppSettings;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NotificationsJobService extends JobService {

    private static final int JOB_ID = 1;

    public static void schedule(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler == null)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (jobScheduler.getPendingJob(JOB_ID) != null)
                return;
        } else {
            List<JobInfo> allPendingJobs = jobScheduler.getAllPendingJobs();
            if (!allPendingJobs.isEmpty()) {
                for (JobInfo j : allPendingJobs) {
                    if (j.getId() == JOB_ID)
                        return;
                }
            }
        }

        SharedPreferences settings = AppSettings.getSharedPreferences(context);
        int notificationsFrequency = AppSettings.Notifications.getNotificationsFrequency(settings);

        ComponentName component = new ComponentName(context, NotificationsJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, component)
                .setPeriodic(60000 * notificationsFrequency);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NOT_ROAMING);
        else
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);


        jobScheduler.schedule(builder.build());
    }

    @Override
    public boolean onStartJob(final JobParameters params) {
        // I am on the main thread, so if you need to do background work,
        // be sure to start up an AsyncTask, Thread, or IntentService!

        final AppClass app = (AppClass) getApplication();

        if (app.userIsLogged(false))

            new Thread(new Runnable() {
                @Override
                public void run() {
                    NotificationsUtils.run(getBaseContext(), app);
                    jobFinished(params, true);
                }
            }).start();
        else
            jobFinished(params, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        // whether or not you would like JobScheduler to automatically retry your failed job.
        return false;
    }
}