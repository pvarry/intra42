package com.paulvarry.intra42.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.BuildConfig;
import com.paulvarry.intra42.Credential;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.clusterMap.ClusterMapActivity;
import com.paulvarry.intra42.activities.home.HomeActivity;
import com.paulvarry.intra42.activities.projects.ProjectsActivity;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.ServiceGenerator;
import com.paulvarry.intra42.api.model.AccessToken;
import com.paulvarry.intra42.interfaces.RefreshCallbackMainActivity;
import com.paulvarry.intra42.utils.AppSettings;
import com.paulvarry.intra42.utils.Token;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public AppClass app;
    private LinearLayout linearLayoutNeedLogin;
    private Button buttonViewSources;
    private TextView textViewLoadingInfo;
    private ProgressBar progressBarLoading;
    private TextView textViewStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences(AppClass.PREFS_NAME, 0);
        int appVersion = sharedPreferences.getInt(AppClass.PREFS_APP_VERSION, 0);
        if (appVersion == 0 || appVersion != BuildConfig.VERSION_CODE) {
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putInt(AppClass.PREFS_APP_VERSION, BuildConfig.VERSION_CODE);
            edit.apply();

            if (appVersion < 20170411) {
                AppSettings.Notifications.setNotificationsAllow(this, true);
                Log.i("notifications", "Notifications activated due to upgrade");
            }
        }

        app = (AppClass) getApplication();

        AppClass.scheduleAlarm(this);

        linearLayoutNeedLogin = findViewById(R.id.linearLayoutNeedLogin);
        buttonViewSources = findViewById(R.id.buttonViewSources);
        textViewLoadingInfo = findViewById(R.id.textViewLoadingInfo);
        progressBarLoading = findViewById(R.id.progressBarLoading);
        textViewStatus = findViewById(R.id.textViewStatus);

        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(Credential.API_OAUTH_REDIRECT))// oauth callback
            setViewLoading();
        else if (app.accessToken != null) {
            setViewLoading();

            new Thread(new Runnable() {
                @Override
                public void run() {

                    final boolean ret = app.initCache(false);
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
        linearLayoutNeedLogin.setVisibility(View.GONE);
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
        linearLayoutNeedLogin.setVisibility(View.VISIBLE);
        buttonViewSources.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(Credential.API_OAUTH_REDIRECT)) {
            String code = uri.getQueryParameter("code");
            if (code != null) {

                ApiService client = ServiceGenerator.createService(ApiService.class);
                Call<AccessToken> call = client.getNewAccessToken(code, Credential.UID,
                        Credential.SECRET, Credential.API_OAUTH_REDIRECT,
                        "authorization_code");
                call.enqueue(new Callback<AccessToken>() {
                    @Override
                    public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                        int statusCode = response.code();
                        if (statusCode == 200) {
                            AccessToken token = response.body();
                            Token.save(MainActivity.this, token);
                            app.accessToken = token;

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    app.initCache(true, new RefreshCallbackMainActivity() {
                                        @Override
                                        public void update(final String info, final String status, final int progress, final int progressMax) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (info != null)
                                                        textViewLoadingInfo.setText(info);
                                                    if (status != null)
                                                        textViewStatus.setText(status);
                                                    progressBarLoading.setIndeterminate(false);
                                                    progressBarLoading.setProgress(progress);
                                                    if (progressMax != -1)
                                                        progressBarLoading.setMax(progressMax);
                                                }
                                            });
                                        }
                                    });

                                    final Intent intent = new Intent(getApplication(), HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //          startActivity(intent);
                                        }
                                    });
                                }
                            }).start();

                        } else {
                            try {
                                Toast.makeText(MainActivity.this, response.errorBody().string(), Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<AccessToken> call, Throwable t) {
                        Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else { // Handle a missing code in the redirect URI
                Toast.makeText(MainActivity.this, "code is missing", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void finishCache() {
        Intent intent = null;
        if (getIntent() != null) {
            String shortcut = getIntent().getStringExtra("shortcut");
            if (shortcut != null) {
                if (shortcut.contentEquals("friends")) {
                    intent = new Intent(MainActivity.this, FriendsActivity.class);
                } else if (shortcut.contentEquals("clusterMap"))
                    intent = new Intent(MainActivity.this, ClusterMapActivity.class);
                else if (shortcut.contentEquals("galaxy"))
                    intent = new Intent(MainActivity.this, ProjectsActivity.class);
            }
        }

        if (intent == null)
            intent = new Intent(getApplication(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void open(View view) {
        Uri u = Uri.parse(ServiceGenerator.API_BASE_URL + "/oauth/authorize?client_id=" + Credential.UID + "&redirect_uri=" + Credential.API_OAUTH_REDIRECT + "&response_type=code&scope=" + Credential.SCOPE);
        Intent intent = new Intent(Intent.ACTION_VIEW, u);
        // This flag is set to prevent the browser with the login form from showing in the history stack
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        startActivity(intent);
        finish();
    }

    public void openSources(View view) {
        Uri u = Uri.parse("https://github.com/pvarry/intra42");
        Intent intent = new Intent(Intent.ACTION_VIEW, u);

        startActivity(intent);
        finish();
    }
}
