package com.paulvarry.intra42.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.BuildConfig;
import com.paulvarry.intra42.Credential;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.Tools.AppSettings;
import com.paulvarry.intra42.Tools.Token;
import com.paulvarry.intra42.activity.home.HomeActivity;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.ServiceGenerator;
import com.paulvarry.intra42.api.model.AccessToken;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public AppClass app;
    private LinearLayout linearLayoutNeedLogin;
    private TextView textViewLoading;

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

        linearLayoutNeedLogin = (LinearLayout) findViewById(R.id.linearLayoutNeedLogin);
        textViewLoading = (TextView) findViewById(R.id.textViewLoading);

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
                                              Intent intent = new Intent(getApplication(), HomeActivity.class);
                                              intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                              startActivity(intent);
                                              finish();
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

    private void setViewLoading() {
        textViewLoading.setVisibility(View.VISIBLE);
        linearLayoutNeedLogin.setVisibility(View.GONE);
    }

    private void setViewLogin() {
        textViewLoading.setVisibility(View.GONE);
        linearLayoutNeedLogin.setVisibility(View.VISIBLE);
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
                                    app.initCache(true);

                                    final Intent intent = new Intent(getApplication(), HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(intent);
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

    public void open(View view) {
        Uri u = Uri.parse(ServiceGenerator.API_BASE_URL + "/oauth/authorize?client_id=" + Credential.UID + "&redirect_uri=" + Credential.API_OAUTH_REDIRECT + "&response_type=code&scope=" + Credential.SCOPE);
        Intent intent = new Intent(Intent.ACTION_VIEW, u);
        // This flag is set to prevent the browser with the login form from showing in the history stack
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        startActivity(intent);
        finish();
    }

}
