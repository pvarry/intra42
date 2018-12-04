package com.paulvarry.intra42.activities;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.AndroidRuntimeException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.Credential;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.clusterMap.ClusterMapActivity;
import com.paulvarry.intra42.activities.home.HomeActivity;
import com.paulvarry.intra42.activities.intro.IntroActivity;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.ApiService42Tools;
import com.paulvarry.intra42.api.ServiceGenerator;
import com.paulvarry.intra42.api.model.AccessToken;
import com.paulvarry.intra42.utils.Analytics;
import com.paulvarry.intra42.utils.AppSettings;
import com.paulvarry.intra42.utils.ThemeHelper;
import com.paulvarry.intra42.utils.Token;
import com.paulvarry.intra42.utils.Tools;

import java.io.IOException;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LaunchActivity extends AppCompatActivity {

    public AppClass app;
    private ViewGroup viewGroupNeedLogin;
    private Button buttonViewSources;
    private TextView textViewLoadingInfo;
    private ProgressBar progressBarLoading;
    private TextView textViewStatus;

    public static void openActivity(Context context) {
        Intent i = new Intent(context, LaunchActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(i);
        } catch (AndroidRuntimeException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        app = (AppClass) getApplication();
        ThemeHelper.setTheme(this, app);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        AppClass.scheduleAlarm(this);

        viewGroupNeedLogin = findViewById(R.id.linearLayoutNeedLogin);
        buttonViewSources = findViewById(R.id.buttonViewSources);
        textViewLoadingInfo = findViewById(R.id.textViewLoadingInfo);
        progressBarLoading = findViewById(R.id.progressBarLoading);
        textViewStatus = findViewById(R.id.textViewStatus);

        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(Credential.API_OAUTH_REDIRECT))// oauth callback
            setViewLoading();
        else if (ServiceGenerator.have42Token() && app.userIsLogged(false)) {
            setViewLoading();

            new Thread(new Runnable() {
                @Override
                public void run() {

                    final boolean ret = app.initCache(false, LaunchActivity.this);
                    runOnUiThread(new Runnable() {
                                      @Override
                                      public void run() {
                                          if (ret) {
                                              finishCache();
                                          } else
                                              setViewLogin();
                                      }
                                  }
                    );

                }
            }).start();
        } else
            setViewLogin();
    }

    private void setViewHide() {
        textViewLoadingInfo.setVisibility(View.GONE);
        textViewStatus.setVisibility(View.GONE);
        progressBarLoading.setVisibility(View.GONE);
        viewGroupNeedLogin.setVisibility(View.GONE);
        buttonViewSources.setVisibility(View.GONE);
    }

    private void setViewLoading() {
        setViewHide();
        textViewLoadingInfo.setVisibility(View.VISIBLE);
        textViewStatus.setVisibility(View.VISIBLE);
        progressBarLoading.setVisibility(View.VISIBLE);
    }

    private void setViewLogin() {
        setViewHide();
        viewGroupNeedLogin.setVisibility(View.VISIBLE);
        buttonViewSources.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(Credential.API_OAUTH_REDIRECT)) {
            String code = uri.getQueryParameter("code");
            if (code != null) {
                Analytics.signInHaveCode(getLoginReferrer());
                getTokenWithCode(code);
            } else { // Handle a missing code in the redirect URI
                Toast.makeText(LaunchActivity.this, "code is missing", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getLoginReferrer() {
        String referrer = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Uri referrerUri = getReferrer();
            if (referrerUri != null)
                referrer = referrerUri.getHost();
        }
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        if (referrer == null && am != null) {
            try {
                List<ActivityManager.RecentTaskInfo> recentTasks = am.getRecentTasks(10000, ActivityManager.RECENT_WITH_EXCLUDED);
                ActivityManager.RecentTaskInfo t = recentTasks.get(1);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
                    referrer = t.baseActivity.getPackageName();
                else {
                    if (t.origActivity != null)
                        referrer = t.origActivity.getPackageName();
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        return referrer;
    }

    private void getTokenWithCode(String code) {
        if (Credential.API_OAUTH_REDIRECT == null || Credential.SCOPE == null || Credential.UID == null) {
            throw new RuntimeException("API Credentials must be specified");
        }

        Call<AccessToken> call;
        if (Credential.SECRET != null && !Credential.SECRET.isEmpty()) {
            ApiService client = ServiceGenerator.createService(ApiService.class);
            call = client.getNewAccessToken(code, Credential.UID,
                    Credential.SECRET, Credential.API_OAUTH_REDIRECT,
                    "authorization_code");
        } else {
            ApiService42Tools client = app.getApiService42Tools();
            call = client.auth42Api(Credential.UID, code, Credential.API_OAUTH_REDIRECT);
        }

        call.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                if (Tools.apiIsSuccessfulNoThrow(response)) {
                    Analytics.signInSuccess();
                    AccessToken token = response.body();
                    Token.save(LaunchActivity.this, token);
                    ServiceGenerator.setToken(token);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            app.initCache(true, LaunchActivity.this);
                            AppClass.scheduleAlarm(LaunchActivity.this);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    finishCache();
                                }
                            });
                        }
                    }).start();

                } else if (response.code() == 200) {
                    Analytics.signInError(response);
                    AlertDialog.Builder builder = new AlertDialog.Builder(LaunchActivity.this);
                    builder.setMessage(R.string.sign_in_error_invalid_grant);
                    builder.setPositiveButton(R.string.ok, null);
                    builder.show();
                    setViewLogin();
                } else {
                    Analytics.signInError(response);
                    try {
                        Toast.makeText(LaunchActivity.this, response.errorBody().string(), Toast.LENGTH_LONG).show();
                        setViewLogin();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                Analytics.signInError(t);
                Toast.makeText(LaunchActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void finishCache() {
        Intent intent = null;
        if (getIntent() != null) {
            String shortcut = getIntent().getStringExtra("shortcut");
            if (shortcut != null) {
                if (shortcut.contentEquals("friends")) {
                    Analytics.shortcutFriends();
                    intent = new Intent(LaunchActivity.this, FriendsActivity.class);
                } else if (shortcut.contentEquals("clusterMap")) {
                    Analytics.shortcutClusterMap();
                    intent = new Intent(LaunchActivity.this, ClusterMapActivity.class);
                } else if (shortcut.contentEquals("galaxy")) {
                    Analytics.shortcutGalaxy();
                    intent = new Intent(LaunchActivity.this, HolyGraphActivity.class);
                }
            }
        }

        if (intent == null) {
            if (AppSettings.getIntroductionFinished(this))
                intent = new Intent(this, HomeActivity.class);
            else
                intent = new Intent(this, IntroActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    public void login(View view) {
        Analytics.signInAttempt();
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();

        Uri loginUri = Uri.parse(ApiService.API_BASE_URL + "/oauth/authorize?client_id=" + Credential.UID + "&redirect_uri=" + Credential.API_OAUTH_REDIRECT + "&response_type=code&scope=" + Credential.SCOPE);

        Intent defaultBrowserIntent = new Intent(Intent.ACTION_VIEW);
        defaultBrowserIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        defaultBrowserIntent.setData(loginUri);
        PendingIntent defaultBrowserPendingIntent = PendingIntent.getActivity(this, 0, defaultBrowserIntent, 0);


        builder.addMenuItem(getString(R.string.login_custom_chrome_tabs_open_default_browser), defaultBrowserPendingIntent);
        CustomTabsIntent customTabsIntent = builder.build();
        try {
            customTabsIntent.launchUrl(this, loginUri);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
            Toast.makeText(app, R.string.login_error_web_browser_required, Toast.LENGTH_SHORT).show();
        }
    }

    public void openSources(View view) {
        Uri uri = Uri.parse(getString(R.string.Github_link));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        startActivity(intent);
        finish();
    }

    public void updateViewState(final String info, final String status, final int progress, final int progressMax) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (info != null)
                    textViewLoadingInfo.setText(info);
                if (status != null)
                    textViewStatus.setText(status);
                if (progressBarLoading != null) {
                    progressBarLoading.setIndeterminate(false);
                    progressBarLoading.setProgress(progress);
                    if (progressMax != -1)
                        progressBarLoading.setMax(progressMax);
                }
            }
        });
    }
}
