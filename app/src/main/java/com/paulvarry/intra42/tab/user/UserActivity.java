package com.paulvarry.intra42.tab.user;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.paulvarry.intra42.Adapter.ViewPagerAdapter;
import com.paulvarry.intra42.ApiService;
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.Tools.Friends;
import com.paulvarry.intra42.Tools.UserImage;
import com.paulvarry.intra42.api.CursusUsers;
import com.paulvarry.intra42.api.Locations;
import com.paulvarry.intra42.api.User;
import com.paulvarry.intra42.api.UserLTE;
import com.paulvarry.intra42.cache.CacheUsers;
import com.paulvarry.intra42.oauth.ServiceGenerator;
import com.paulvarry.intra42.ui.BasicTabActivity;
import com.paulvarry.intra42.ui.tools.Navigation;
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
        UserExpertisesFragment.OnFragmentInteractionListener {

    private static final String INTENT_USER_LTE = "user_lte";
    private static final String INTENT_USER_FULL = "user_full";
    private static final String INTENT_LOGIN = "user_login";

    public User user;
    public HashMap<String, Bitmap> picAchievements;
    public CursusUsers userCursus;
    String login;

    public static void openIt(Context context, UserLTE user) {
        if (user != null) {
            Intent intent = getIntent(context, user);
            context.startActivity(intent);
        }
    }

    public static void openIt(final Context context, final UserLTE user, Activity activity) {
        openIt(context, user.login, activity);
    }

    public static void openIt(final Context context, final UserLTE user, AppClass app) {
        openIt(context, user.login, app);
    }

    public static boolean openIt(final Context context, final String login, final Activity activity) {
        return activity != null && openIt(context, login, (AppClass) activity.getApplication());
    }

    public static boolean openIt(final Context context, final String login, final AppClass app) {
        if (app != null) {

            if (CacheUsers.isCached(app.usersDbHelper, login)) {
                UserActivity.openIt(context, login);
                return true;
            }

            final ProgressDialog dialog = ProgressDialog.show(context, "", context.getString(R.string.loading_please_wait), true);
            ApiService s = app.getApiService();

            Call<User> call = s.getUser(login);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {

                    if (!response.isSuccessful() && response.code() != 404)
                        Toast.makeText(context, "Error on get user", Toast.LENGTH_SHORT).show();
                    else if (response.body() == null || response.code() == 404)
                        Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show();
                    else {

                        UserActivity.openIt(context, response.body());

                        String gson = ServiceGenerator.getGson().toJson(response.body());
                        CacheUsers.put(app.usersDbHelper, response.body(), gson);

                    }
                    dialog.cancel();
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    dialog.cancel();
                    Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        }
        return false;
    }

    public static void openIt(Context context, User user) {
        if (user != null) {
            Intent intent = new Intent(context, UserActivity.class);
            intent.putExtra(UserActivity.INTENT_USER_FULL, ServiceGenerator.getGson().toJson(user));
            context.startActivity(intent);
        }
    }

    public static void openIt(Context context, String login) {

        Intent intent = new Intent(context, UserActivity.class);
        intent.putExtra(UserActivity.INTENT_LOGIN, login);
        context.startActivity(intent);

    }

    public static Intent getIntent(Context context, UserLTE user) {
        if (user != null) {
            Intent intent = new Intent(context, UserActivity.class);
            intent.putExtra(UserActivity.INTENT_USER_LTE, ServiceGenerator.getGson().toJson(user));
            return intent;
        }
        return null;
    }

    public static void openLocation(final Context context, String location, final AppClass app) {
        final ProgressDialog dialog = ProgressDialog.show(context, "", context.getString(R.string.loading_please_wait), true);
        ApiService s = app.getApiService();

        Call<List<Locations>> call = s.getLocationsHost(location);
        call.enqueue(new Callback<List<Locations>>() {
            @Override
            public void onResponse(Call<List<Locations>> call, final Response<List<Locations>> response) {

                if (!response.isSuccessful() && response.code() != 404)
                    Toast.makeText(context, "Error on get", Toast.LENGTH_SHORT).show();
                else if (response.body() == null || response.code() == 404)
                    Toast.makeText(context, "not found", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //handle just logged users.
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                login = handleSendText(intent); // Handle text being sent
            }
        } else if (Intent.ACTION_VIEW.equals(action)) {
            if (intent.getData().getHost().equals("profile.intra.42.fr")) {
                List<String> tmp = intent.getData().getPathSegments();
                if (tmp.size() == 2)
                    login = tmp.get(1);
            }
        } else {
            if (intent.hasExtra(INTENT_USER_FULL)) {
                user = ServiceGenerator.getGson().fromJson(intent.getStringExtra(INTENT_USER_FULL), User.class);
                if (user != null)
                    login = user.login;
            } else if (intent.hasExtra(INTENT_USER_LTE)) {
                login = ServiceGenerator.getGson().fromJson(intent.getStringExtra(INTENT_USER_LTE), UserLTE.class).login;
            } else if (intent.hasExtra(INTENT_LOGIN))
                login = intent.getStringExtra(INTENT_LOGIN).toLowerCase();
        }

        super.onCreate(savedInstanceState);
        if (user == null && login == null)
            finish();
        else {
            super.setSelectedMenu(Navigation.MENU_SELECTED_USERS);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(R.string.action_user_add_to_home).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
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

        menu.add(R.string.action_user_add_friends).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Friends.actionAddRemove(app.firebaseRefFriends, user);
                return true;
            }
        });

        return true;
    }

    private String handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            return sharedText.toLowerCase();
        }
        return null;
    }

    @Override
    public boolean getDataOnOtherThread() {
        if (app != null && app.me != null && login != null && login.contentEquals(app.me.login))
            user = app.me;
        else if (login != null) {
            if (user == null) {
                ApiService service = app.getApiService();
                Call<User> call = service.getUser(login);
                try {
                    Response<User> ret = call.execute();
                    if (ret.code() == 200)
                        user = ret.body();
                    else
                        return false;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (user != null && !user.cursusUsers.isEmpty()) {
                userCursus = user.cursusUsers.get(0);
            }
        }
        return true;
    }

    @Override
    public boolean getDataOnMainThread() {
        if (user != null)
            return true;

        if (app != null && app.me != null && login != null && login.contentEquals(app.me.login)) {
            user = app.me;
            return true;
        }

        User tmp = CacheUsers.get(app.usersDbHelper, login);
        if (tmp != null) {
            user = tmp;
            return true;
        }

        return false;
    }

    @Override
    public String getToolbarName() {
        if (user != null)
            return user.login;
        else
            return login;
    }

    public void refresh(final Runnable runnable) {
        if (app == null)
            return;

        final Runnable localRunnable = new Runnable() {
            @Override
            public void run() {
                if (app.me != null && login != null && login.contentEquals(app.me.login))
                    user = app.me;
                if (user != null && !user.cursusUsers.isEmpty()) {
                    userCursus = user.cursusUsers.get(0);
                }
                if (runnable != null)
                    runnable.run();
            }
        };


        if (app.me != null && login != null && login.contentEquals(app.me.login)) {
            app.refreshUser(localRunnable);

        } else if (login != null) {

            app.getApiService().getUser(login).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()) {
                        user = response.body();
                        CacheUsers.put(app.usersDbHelper, user);
                    }
                    localRunnable.run();
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    localRunnable.run();
                }
            });

        }
    }

    @Override
    public void setupViewPager(final ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(UserOverviewFragment.newInstance(), getString(R.string.tab_user_overview));
        adapter.addFragment(UserForumFragment.newInstance(), getString(R.string.tab_user_forum));
        adapter.addFragment(UserMarksFragment.newInstance(), getString(R.string.tab_user_marks));
        adapter.addFragment(UserProjectsDoingFragment.newInstance(), getString(R.string.tab_user_projects));
        adapter.addFragment(UserExpertisesFragment.newInstance(), getString(R.string.tab_user_expertises));
        adapter.addFragment(UserAchievementsFragment.newInstance(), getString(R.string.tab_user_achivements));
        adapter.addFragment(UserSkillsFragment.newInstance(), getString(R.string.tab_user_skills));
        adapter.addFragment(UserPartnershipsFragment.newInstance(), getString(R.string.tab_user_partnerships));
        viewPager.setAdapter(adapter);

        if (user != null && user.local_cachedAt != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(user.local_cachedAt);
            calendar.add(Calendar.MINUTE, 15);
            Date timeout = calendar.getTime();
            if (user.local_cachedAt != null && timeout.before(new Date())) {

                Snackbar s = Snackbar.make(coordinatorLayout, "Data cached long time ago", Snackbar.LENGTH_LONG);
                s.setAction(R.string.refresh, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final Snackbar s = Snackbar.make(coordinatorLayout, "Refreshing", Snackbar.LENGTH_INDEFINITE);
                        s.show();

                        refresh(new Runnable() {
                            @Override
                            public void run() {
                                UserActivity.super.setView();
                                s.dismiss();
                            }
                        });
                    }
                });
                s.show();
            }
        }
    }

    @Nullable
    @Override
    public String getUrlIntra() {
        if (user != null)
            return getString(R.string.base_url_intra_profile) + "users/" + user.login;
        return null;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    private void addShortCut() {
        final Intent addIntent = new Intent();
        final RequestCreator p = UserImage.getPicassoCorned(UserActivity.this, user);
        if (p != null)
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
}
