package com.paulvarry.intra42.ui.tools;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.AboutActivity;
import com.paulvarry.intra42.activities.FriendsActivity;
import com.paulvarry.intra42.activities.HolyGraphActivity;
import com.paulvarry.intra42.activities.MarvinMealsActivity;
import com.paulvarry.intra42.activities.TimeActivity;
import com.paulvarry.intra42.activities.clusterMap.ClusterMapActivity;
import com.paulvarry.intra42.activities.forum.ForumActivity;
import com.paulvarry.intra42.activities.home.HomeActivity;
import com.paulvarry.intra42.activities.notions.NotionsActivity;
import com.paulvarry.intra42.activities.settings.SettingsActivity;
import com.paulvarry.intra42.activities.users.UsersActivity;

public class Navigation {

    public static final int MENU_SELECTED_HOME = 0;
    public static final int MENU_SELECTED_USERS = 1;
    public static final int MENU_SELECTED_PROJECTS = 2;
    public static final int MENU_SELECTED_FORUM = 3;
    public static final int MENU_SELECTED_ELEARNING = 4;
    public static final int MENU_SELECTED_TIME_ON_CAMPUS = 5;
    public static final int MENU_SELECTED_CLUSTER_MAP = 6;
    public static final int MENU_SELECTED_ABOUT = 7;
    public static final int MENU_SELECTED_SHARE = 8;
    public static final int MENU_SELECTED_SETTINGS = 10;

    public static boolean onNavigationItemSelected(Activity activity, MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(activity, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
        } else if (id == R.id.nav_users) {
            Intent intent = new Intent(activity, UsersActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
        } else if (id == R.id.nav_projects) {
            Intent intent = new Intent(activity, HolyGraphActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
        } else if (id == R.id.nav_forum) {
            Intent intent = new Intent(activity, ForumActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
        } else if (id == R.id.nav_elearning) {
            Intent intent = new Intent(activity, NotionsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(activity, AboutActivity.class);
            activity.startActivity(intent);
        } else if (id == R.id.nav_friends) {
            Intent intent = new Intent(activity, FriendsActivity.class);
            activity.startActivity(intent);
        } else if (id == R.id.nav_time_on_campus) {
            Intent intent = new Intent(activity, TimeActivity.class);
            activity.startActivity(intent);
        } else if (id == R.id.nav_cluster_map) {
            Intent intent = new Intent(activity, ClusterMapActivity.class);
            activity.startActivity(intent);
        } else if (id == R.id.nav_cantina_menu) {
            Intent intent = new Intent(activity, MarvinMealsActivity.class);
            activity.startActivity(intent);
        } else if (id == R.id.nav_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.paulvarry.intra42");
            sendIntent.setType("text/plain");
            activity.startActivity(sendIntent);
        } else if (id == R.id.nav_logout) {
            ((AppClass) activity.getApplication()).logoutAndRedirect();
        } else if (id == R.id.nav_settings) {
            final Intent i = new Intent(activity, SettingsActivity.class);
            activity.startActivity(i);
        }
        return true;
    }
}
