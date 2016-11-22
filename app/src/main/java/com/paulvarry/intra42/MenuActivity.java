package com.paulvarry.intra42;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.paulvarry.intra42.Interface.InterfaceViewActivity;
import com.paulvarry.intra42.Tools.UserImage;
import com.paulvarry.intra42.activity.AboutActivity;
import com.paulvarry.intra42.activity.MainActivity;
import com.paulvarry.intra42.activity.SettingsActivity;
import com.paulvarry.intra42.tab.forum.ForumActivity;
import com.paulvarry.intra42.tab.home.HomeActivity;
import com.paulvarry.intra42.tab.notions.NotionsActivity;
import com.paulvarry.intra42.tab.projects.ProjectsActivity;
import com.paulvarry.intra42.tab.users.UsersActivity;
import com.squareup.picasso.RequestCreator;

import static com.paulvarry.intra42.AppClass.CACHE_API_CAMPUS;
import static com.paulvarry.intra42.AppClass.CACHE_API_CURSUS;
import static com.paulvarry.intra42.AppClass.CACHE_API_ME;
import static com.paulvarry.intra42.AppClass.PREFS_NAME;

/**
 *
 */
@Deprecated
public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, InterfaceViewActivity.Tab {

    public static final int MENU_SELECTED_HOME = 0;
    public static final int MENU_SELECTED_USERS = 1;
    public static final int MENU_SELECTED_PROJECTS = 2;
    public static final int MENU_SELECTED_FORUM = 3;
    public static final int MENU_SELECTED_ELEARNING = 4;
    public AppClass app;
    public Toolbar toolbar;
    DrawerLayout drawer;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        app = (AppClass) this.getApplication();

        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);

            View headerLayout = navigationView.getHeaderView(0);
            ImageView imageView = (ImageView) headerLayout.findViewById(R.id.imageViewNav);
            TextView name = (TextView) headerLayout.findViewById(R.id.textViewNavName);
            TextView email = (TextView) headerLayout.findViewById(R.id.textViewNavEmail);

            if (app.me != null) {
                name.setText(app.me.login);
                email.setText(app.me.email);
                RequestCreator p = UserImage.getPicassoCorned(app, app.me);
                if (p != null)
                    p.into(imageView);
            }
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        new Thread(new Runnable() {
            @Override
            public void run() {

                getData();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setView();
                        setViewM();
                    }
                });

            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void setViewM() {

    }

    /**
     * Handle clicks on ActionBar
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection// Handle item selection
        Log.d("item", item.getTitle().toString());
        switch (item.getItemId()) {

            case R.id.action_settings:
                final Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                break;
            case R.id.action_open_intra:
                Toast.makeText(this, "Intra", Toast.LENGTH_SHORT).show();
                final Intent ii = new Intent(this, SettingsActivity.class);
                startActivity(ii);
                break;


        }
        return false;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(getApplication(), HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.nav_users) {
            Intent intent = new Intent(getApplication(), UsersActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.nav_projects) {
            Intent intent = new Intent(getApplication(), ProjectsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.nav_forum) {
            Intent intent = new Intent(getApplication(), ForumActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.nav_elearning) {
            Intent intent = new Intent(getApplication(), NotionsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(getApplication(), AboutActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.paulvarry.intra42");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } else if (id == R.id.nav_logout) {
            app.logout();
            SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(CACHE_API_ME);
            editor.remove(CACHE_API_CURSUS);
            editor.remove(CACHE_API_CAMPUS);
            editor.apply();

            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        } else if (id == R.id.nav_settings) {
            final Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setToggle(Activity activity, Toolbar toolbar) {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.addDrawerListener(toggle);
        }
        toggle.syncState();
    }

    public void setToggleNoHamburger(Activity activity, Toolbar toolbar) {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);

        setNoHamburger(toolbar);
    }

    public void setNoHamburger(Toolbar toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24px);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void setSelectedMenu(int position) {
        navigationView.getMenu().getItem(position).setChecked(true);
    }

    /**
     * This method is run on a Thread, so you can make API calls and other long stuff.
     */
    @Override
    public void getData() {
        throw new RuntimeException(toString()
                + " must implement InterfaceViewActivity.Tab");
    }

    /**
     * This method is run after getData(), there tou can set view with data previously obtained  .
     */
    @Override
    public void setView() {
        throw new RuntimeException(toString()
                + " must implement InterfaceViewActivity.Tab");
    }

    @Override
    public void setupViewPager(ViewPager viewPager) {
        throw new RuntimeException(toString()
                + " must implement InterfaceViewActivity.Tab");
    }

    static public class Tab extends MenuActivity {

        public Toolbar toolbar;
        public TabLayout tabLayout;
        public ViewPager viewPager;

        @Override
        protected void onCreate(Bundle savedInstanceState) {

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            viewPager = (ViewPager) findViewById(R.id.viewpager);
            tabLayout = (TabLayout) findViewById(R.id.tabs);

            super.onCreate(savedInstanceState);

            setSupportActionBar(toolbar);
            super.setToggle(this, toolbar);
        }

        @Override
        public void setViewM() {
            tabLayout.setVisibility(View.VISIBLE);
            setupViewPager(viewPager);
            tabLayout.setupWithViewPager(viewPager);
        }
    }

    static public class NoMenu extends MenuActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            super.onCreate(savedInstanceState);

            setSupportActionBar(toolbar);

            super.setNoHamburger(toolbar);
        }
    }
}
