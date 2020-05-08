package com.paulvarry.intra42.activities;

import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LaunchActivity extends AppCompatActivity {

    public AppClass app;
    private Button buttonLogin;
    private Button buttonViewSources;
    private TextView textViewLoadingInfo;
    private ProgressBar progressBarLoading;
    private TextView textViewStatus;

    public static void openActivity(Context context) {
        Intent i = new Intent(context, LaunchActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

        buttonLogin = findViewById(R.id.launch_buttonLogin);
        buttonViewSources = findViewById(R.id.buttonViewSources);
        textViewLoadingInfo = findViewById(R.id.textViewLoadingInfo);
        progressBarLoading = findViewById(R.id.progressBarLoading);
        textViewStatus = findViewById(R.id.textViewStatus);

        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(Credential.API_OAUTH_REDIRECT))// oauth callback
            setViewLoading();
        else if (ServiceGenerator.have42Token() && app.userIsLogged(false)) {
            setViewLoading();

            new Thread(() -> {
                final boolean ret = app.initCache(false, LaunchActivity.this);
                runOnUiThread(() -> {
                            if (ret) {
                                finishCache();
                            } else
                                setViewLogin();
                        }
                );

            }).start();
        } else
            setViewLogin();

        buttonViewSources.setOnLongClickListener(this::onViewSourcesLongClick);
        buttonViewSources.setOnClickListener(this::onViewSourcesClick);
        buttonLogin.setOnClickListener(this::onLoginClick);
    }

    private void setViewHide() {
        textViewLoadingInfo.setVisibility(View.GONE);
        textViewStatus.setVisibility(View.GONE);
        progressBarLoading.setVisibility(View.GONE);
        buttonLogin.setVisibility(View.GONE);
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
        buttonLogin.setVisibility(View.VISIBLE);
        buttonViewSources.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("resume", "resume");

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("resume", "new Intent");
    }

    private String getLoginReferrer() {
        String referrer = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Uri referrerUri = getReferrer();
            if (referrerUri != null)
                referrer = referrerUri.getHost();
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

                    new Thread(() -> {
                        app.initCache(true, LaunchActivity.this);
                        AppClass.scheduleAlarm(LaunchActivity.this);

                        runOnUiThread(() -> finishCache());
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
                        setViewLogin();
                        Toast.makeText(LaunchActivity.this, getString(R.string.error_server_contect_support) + "\n" + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                Analytics.signInError(t);
                setViewLogin();
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

    public void onLoginClick(View view) {
        Analytics.signInAttempt();
        Uri loginUri = Uri.parse(ApiService.API_BASE_URL + "/oauth/authorize?client_id=" + Credential.UID + "&redirect_uri=" + Credential.API_OAUTH_REDIRECT + "&response_type=code&scope=" + Credential.SCOPE);

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setShowTitle(true);
        builder.setInstantAppsEnabled(true);

        Intent defaultBrowserIntent = new Intent(Intent.ACTION_VIEW);
        defaultBrowserIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        defaultBrowserIntent.setData(loginUri);
        PendingIntent defaultBrowserPendingIntent = PendingIntent.getActivity(this, 0, defaultBrowserIntent, 0);

        builder.addMenuItem(getString(R.string.login_custom_chrome_tabs_open_default_browser), defaultBrowserPendingIntent);
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            customTabsIntent.launchUrl(this, loginUri);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
            Toast.makeText(app, R.string.login_error_web_browser_required, Toast.LENGTH_SHORT).show();
        }
    }

    public void onViewSourcesClick(View view) {
        Uri uri = Uri.parse(getString(R.string.Github_link));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        startActivity(intent);
        finish();
    }

    private boolean onViewSourcesLongClick(View view) {
        if (!AppSettings.Advanced.getAllowAdvanced(LaunchActivity.this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LaunchActivity.this);
            builder.setTitle(R.string.pref_title_advanced_data_save_logs);
            builder.setMessage(R.string.pref_summary_advanced_data_save_logs);
            builder.setNegativeButton(android.R.string.cancel, null);
            builder.setPositiveButton(getString(R.string.dialog_enable_app_logs), (dialog, which) -> {
                AppSettings.Advanced.setAllowAdvanced(LaunchActivity.this, true);
                AppSettings.Advanced.setAllowSaveLogs(LaunchActivity.this, true);
                Toast.makeText(LaunchActivity.this, getString(R.string.dont_forget_to_restart), Toast.LENGTH_LONG).show();
            });
            builder.show();
        } else {
            AppSettings.Advanced.setAllowSaveLogs(LaunchActivity.this, false);
            Toast.makeText(LaunchActivity.this, getString(R.string.toast_logs_disabled), Toast.LENGTH_LONG).show();
        }

        return true;
    }

    public void updateViewState(final String info, final String status, final int progress, final int progressMax) {
        runOnUiThread(() -> {
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
        });
    }
}
