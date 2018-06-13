package com.paulvarry.intra42.activities.user;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.ViewPagerAdapter;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.ServiceGenerator;
import com.paulvarry.intra42.api.model.Coalitions;
import com.paulvarry.intra42.api.model.CursusUsers;
import com.paulvarry.intra42.api.model.Locations;
import com.paulvarry.intra42.api.model.Users;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.cache.CacheUsers;
import com.paulvarry.intra42.ui.BasicTabActivity;
import com.paulvarry.intra42.ui.BasicThreadActivity;
import com.paulvarry.intra42.ui.CustomViewPager;
import com.paulvarry.intra42.ui.tools.Navigation;
import com.paulvarry.intra42.utils.AppSettings;
import com.paulvarry.intra42.utils.ThemeHelper;
import com.paulvarry.intra42.utils.Tools;
import com.paulvarry.intra42.utils.UserImage;
import com.squareup.picasso.RequestCreator;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends BasicTabActivity
        implements UserOverviewFragment.OnFragmentInteractionListener, UserMarksFragment.OnFragmentInteractionListener,
        UserProjectsDoingFragment.OnFragmentInteractionListener, UserSkillsFragment.OnFragmentInteractionListener,
        UserAchievementsFragment.OnFragmentInteractionListener, UserForumFragment.OnFragmentInteractionListener,
        UserExpertisesFragment.OnFragmentInteractionListener, UserProjectsFragment.OnFragmentInteractionListener,
        UserPatronagesFragment.OnFragmentInteractionListener,
        BasicThreadActivity.GetDataOnMain, BasicThreadActivity.GetDataOnThread {

    private static final String INTENT_USER_LTE = "user_lte";
    private static final String INTENT_USER_FULL = "user_full";
    private static final String INTENT_LOGIN = "user_login";

    public Users user;
    public HashMap<String, Bitmap> picAchievements;
    public CursusUsers selectedCursus;
    String login;
    int selectedTab;

    public static void openIt(Context context, UsersLTE user) {
        if (user != null) {
            Intent intent = getIntent(context, user);
            context.startActivity(intent);
        }
    }

    public static Intent getIntent(Context context, UsersLTE user) {
        if (user != null) {
            Intent intent = new Intent(context, UserActivity.class);
            intent.putExtra(UserActivity.INTENT_USER_LTE, ServiceGenerator.getGson().toJson(user));
            return intent;
        }
        return null;
    }

    public static void openIt(final Context context, final UsersLTE user, Activity activity) {
        if (user != null)
            openIt(context, user.login, activity);
    }

    public static boolean openIt(final Context context, final String login, final Activity activity) {
        return activity != null && openIt(context, login, (AppClass) activity.getApplication());
    }

    public static boolean openIt(final Context context, final String login, final AppClass app) {
        if (app != null) {

            if (CacheUsers.isCached(app.cacheSQLiteHelper, login)) {
                UserActivity.openIt(context, login);
                return true;
            }

            final ApiService api = app.getApiService();
            final Call<Users> callUser = api.getUser(login);
            final Call<List<Coalitions>> callCoalition = api.getUsersCoalitions(login);
            final Handler mainHandler = new Handler(context.getMainLooper());

            final ProgressDialog dialog = new ProgressDialog(context);
            dialog.setTitle("");
            dialog.setMessage(context.getString(R.string.info_loading_please_wait));
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    callUser.cancel();
                    callCoalition.cancel();
                }
            });
            dialog.show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final Response<Users> responseUser = callUser.execute();
                        Users user;

                        if (Tools.apiIsSuccessfulNoThrow(responseUser)) {
                            Response<List<Coalitions>> responseCoalition = callCoalition.execute();
                            user = responseUser.body();
                            if (Tools.apiIsSuccessfulNoThrow(responseCoalition))
                                user.coalitions = responseCoalition.body();

                            UserActivity.openIt(context, user);

                            user.local_cachedAt = new Date();
                            String gson = ServiceGenerator.getGson().toJson(user);
                            CacheUsers.put(app.cacheSQLiteHelper, user, gson);
                        } else {
                            Runnable myRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, R.string.user_open_it_not_found, Toast.LENGTH_SHORT).show();
                                }
                            };
                            mainHandler.post(myRunnable);
                        }

                    } catch (final IOException e) {
                        e.printStackTrace();
                        if (callCoalition.isCanceled() || callUser.isCanceled())
                            return;

                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            } // This is your code
                        };
                        mainHandler.post(myRunnable);
                    } finally {
                        dialog.dismiss();
                    }
                }
            }).start();
            return true;
        }
        return false;
    }

    public static void openIt(Context context, String login) {

        Intent intent = new Intent(context, UserActivity.class);
        intent.putExtra(UserActivity.INTENT_LOGIN, login);
        context.startActivity(intent);

    }

    public static void openIt(Context context, Users user) {
        if (user != null) {
            Intent intent = new Intent(context, UserActivity.class);
            intent.putExtra(UserActivity.INTENT_USER_FULL, ServiceGenerator.getGson().toJson(user));
            context.startActivity(intent);
        }
    }

    public static void openLocation(final Context context, String location, final AppClass app) {
        final ProgressDialog dialog = ProgressDialog.show(context, "", context.getString(R.string.info_loading_please_wait), true);
        ApiService s = app.getApiService();

        Call<List<Locations>> call = s.getLocationsHost(location);
        call.enqueue(new Callback<List<Locations>>() {
            @Override
            public void onResponse(Call<List<Locations>> call, final Response<List<Locations>> response) {

                if (!response.isSuccessful() || response.body() == null || response.code() == 404)
                    Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();
                else {

                    AlertDialog.Builder d = new AlertDialog.Builder(context);

                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                            context,
                            android.R.layout.select_dialog_singlechoice);

                    for (Locations l : response.body()) {
                        arrayAdapter.add(l.user.login);
                    }
                    d.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int position) {
                            UserActivity.openIt(context, response.body().get(position).user, app);
                        }
                    });
                    d.create().show();
                }
                dialog.cancel();
            }

            @Override
            public void onFailure(Call<List<Locations>> call, Throwable t) {
                dialog.cancel();
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void openIt(final Context context, final UsersLTE user, AppClass app) {
        if (user != null)
            openIt(context, user.login, app);
    }

    @Override
    public final Object onRetainCustomNonConfigurationInstance() {
        return user;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //handle just logged users.
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                login = handleSendText(intent); // Handle text being sent
            }
        } else if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri;
            if ((uri = intent.getData()) != null && uri.getHost().equals("profile.intra.42.fr")) {
                List<String> tmp = intent.getData().getPathSegments();
                if (tmp.size() == 2)
                    login = tmp.get(1);
            }
        } else {
            if (intent.hasExtra(INTENT_USER_FULL)) {
                user = ServiceGenerator.getGson().fromJson(intent.getStringExtra(INTENT_USER_FULL), Users.class);
                if (user != null)
                    login = user.login;
            } else if (intent.hasExtra(INTENT_USER_LTE)) {
                login = ServiceGenerator.getGson().fromJson(intent.getStringExtra(INTENT_USER_LTE), UsersLTE.class).login;
            } else if (intent.hasExtra(INTENT_LOGIN))
                login = intent.getStringExtra(INTENT_LOGIN).toLowerCase();
        }

        user = (Users) getLastCustomNonConfigurationInstance();
        if (user != null)
            login = user.login;

        registerGetDataOnOtherThread(this);
        registerGetDataOnMainTread(this);

        super.setActionBarToggle(ActionBarToggle.ARROW);
        super.setSelectedMenu(Navigation.MENU_SELECTED_USERS);

        ThemeHelper.setActionBar(actionBar, AppSettings.Theme.EnumTheme.DEFAULT);
        if (user == null && login == null)
            finish();

        super.onCreateFinished();
    }

    private String handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            return sharedText.toLowerCase();
        }
        return null;
    }

    @Override
    protected void setViewContent() {

        if (user == null)
            setViewState(StatusCode.API_DATA_ERROR);
        else {
            selectedCursus = user.getCursusUsersToDisplay(this);

            super.setViewContent();
            ThemeHelper.setTheme(this, user);
            ThemeHelper.setActionBar(actionBar, AppSettings.Theme.getEnumTheme(this, user));

            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = getTheme();
            theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
            @ColorInt int color = typedValue.data;
            tabLayout.setSelectedTabIndicatorColor(color);
        }

        if (user != null && user.local_cachedAt != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(user.local_cachedAt);
            calendar.add(Calendar.HOUR, 1);
            Date timeout = calendar.getTime();
            if (timeout.before(new Date())) {

                Snackbar snackbar = Snackbar.make(coordinatorLayout, R.string.info_data_cached_long_time_ago, Snackbar.LENGTH_LONG);
                snackbar.setAction(R.string.refresh, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final Snackbar snackbarRefreshing = Snackbar.make(coordinatorLayout, R.string.refreshing, Snackbar.LENGTH_INDEFINITE);
                        snackbarRefreshing.show();

                        refresh(new Runnable() {
                            @Override
                            public void run() {
                                setViewState(StatusCode.CONTENT);
                                snackbarRefreshing.dismiss();
                            }
                        });
                    }
                });
                snackbar.show();
            }
        }
    }

    @Override
    public void setupViewPager(ViewPager viewPager) {

        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(UserOverviewFragment.newInstance(), getString(R.string.title_tab_user_overview));
        adapter.addFragment(UserForumFragment.newInstance(), getString(R.string.title_tab_user_forum));
        adapter.addFragment(UserProjectsFragment.newInstance(), getString(R.string.title_tab_user_projects));
//        adapter.addFragment(UserMarksFragment.newInstance(), getString(R.string.tab_user_marks));
//        adapter.addFragment(UserProjectsDoingFragment.newInstance(), getString(R.string.tab_user_projects));
        adapter.addFragment(UserExpertisesFragment.newInstance(), getString(R.string.title_tab_user_expertises));
        adapter.addFragment(UserAchievementsFragment.newInstance(), getString(R.string.title_tab_user_achievements));
        adapter.addFragment(UserSkillsFragment.newInstance(), getString(R.string.title_tab_user_skills));
        adapter.addFragment(UserPartnershipsFragment.newInstance(), getString(R.string.title_tab_user_partnerships));
        adapter.addFragment(UserPatronagesFragment.newInstance(), getString(R.string.title_tab_user_patronages));

        if (AppSettings.Advanced.getAllowAdvancedData(this)) {
            adapter.addFragment(UserAppsFragment.newInstance(), getString(R.string.title_tab_user_apps));
        }

        viewPager.setAdapter(adapter);

        ((CustomViewPager) viewPager).disableSwiping(getString(R.string.title_tab_user_projects));

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                selectedTab = position;
                app.mFirebaseAnalytics.setCurrentScreen(UserActivity.this, "User Profile -> " + adapter.getItem(position).getClass().getSimpleName(), null /* class override */);
            }
        });
        app.mFirebaseAnalytics.setCurrentScreen(UserActivity.this, "User Profile -> " + UserOverviewFragment.class.getSimpleName(), null /* class override */);
    }

    public void refresh(final Runnable runnable) {
        if (app == null)
            return;

        final Runnable localRunnable = new Runnable() {
            @Override
            public void run() {
                if (app.me != null && login != null && login.contentEquals(app.me.login))
                    user = app.me;
                if (user != null && user.cursusUsers != null && !user.cursusUsers.isEmpty()) {
                    selectedCursus = user.cursusUsers.get(0);
                }
                setViewContent();
                if (runnable != null)
                    runnable.run();
            }
        };

        if (login != null) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        user = null;
                        getDataOnOtherThread();
                        runOnUiThread(localRunnable);
                    } catch (IOException | RuntimeException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(R.string.menu_action_user_add_to_home).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                addShortCut();
                return true;
            }
        });

//        menu.add("Add to contacts").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Contacts.add(UserActivity.this, user);
//                    }
//                }).start();
//
//                return true;
//            }
//        });

        return true;
    }

    @Nullable
    @Override
    public String getUrlIntra() {
        if (login != null)
            return getString(R.string.base_url_intra_profile) + "users/" + login;
        return null;
    }

    @Override
    public void getDataOnOtherThread() throws IOException, UnauthorizedException, ErrorServerException {
        if (login != null) {
            if (user == null) {
                ApiService service = app.getApiService();
                Call<Users> call = service.getUser(login);

                Response<Users> ret = call.execute();
                if (Tools.apiIsSuccessful(ret)) {
                    user = ret.body();
                    user.local_cachedAt = new Date();
                }

                if (user != null) {
                    Response<List<Coalitions>> retCoalition = service.getUsersCoalitions(user.login).execute();
                    if (Tools.apiIsSuccessful(retCoalition))
                        user.coalitions = retCoalition.body();
                }
                if (Tools.apiIsSuccessful(ret) && user != null) {
                    CacheUsers.put(app.cacheSQLiteHelper, user);
                    if (app.me != null && login != null && login.contentEquals(app.me.login))
                        app.me = user;
                }
            }
            if (user != null && user.cursusUsers != null && !user.cursusUsers.isEmpty()) {
                selectedCursus = user.cursusUsers.get(0);
            }
        }
    }

    @Override
    public ThreadStatusCode getDataOnMainThread() {
        if (user != null)
            return ThreadStatusCode.FINISH;
        if (app == null)
            return ThreadStatusCode.NONE;

        if (app.me != null && login != null && login.contentEquals(app.me.login)) {
            user = app.me;
            return ThreadStatusCode.FINISH;
        }

        Users tmp = CacheUsers.get(app.cacheSQLiteHelper, login);
        if (tmp != null) {
            user = tmp;
            return ThreadStatusCode.FINISH;
        }

        return ThreadStatusCode.CONTINUE;
    }

    @Override
    public String getToolbarName() {
        if (user != null)
            return user.login;
        else
            return login;
    }

    /**
     * This text is useful when both {@link GetDataOnThread#getDataOnOtherThread()} and {@link BasicThreadActivity.GetDataOnMain#getDataOnMainThread()} return false.
     *
     * @return A simple text to display on screen, may return null;
     */
    @Override
    public String getEmptyText() {
        return null;
    }

    private void addShortCut() {
        if (user == null)
            return;

        final Intent addIntent = new Intent();
        final RequestCreator p = UserImage.getPicassoCorned(UserActivity.this, user);
        if (p == null)
            return;
        p.resize(200, 240);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap = p.get();
                    addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);
                } catch (final IOException e) {
                    e.printStackTrace();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UserActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Adding shortcut for MainActivity
                        //on Home screen
                        Intent shortcutIntent = new Intent(getApplicationContext(), UserActivity.class);

                        shortcutIntent.setAction(Intent.ACTION_MAIN);
                        shortcutIntent.putExtra(INTENT_LOGIN, user.login);


                        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
                        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, user.login);

                        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                        addIntent.putExtra("duplicate", false);  //may it's already there so don't duplicate
                        getApplicationContext().sendBroadcast(addIntent);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }
}
