package com.paulvarry.intra42.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.MatrixCursor;
import android.provider.BaseColumns;
import android.widget.Toast;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.SubnotionListActivity;
import com.paulvarry.intra42.activities.TopicActivity;
import com.paulvarry.intra42.activities.project.ProjectActivity;
import com.paulvarry.intra42.activities.tags.TagsActivity;
import com.paulvarry.intra42.activities.user.UserActivity;

import java.net.MalformedURLException;
import java.net.URL;

import androidx.annotation.ArrayRes;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

public class SuperSearch {

    public static final String[] suggestions = {
            "u [user] - search on users", "u.[login] - open a user", "p [project] - search on projects", "p.[slug] - open a project", "t [topic] - search on topics", "t.[topic_id] - open a topic", "tag.[tag_id] - open a tag"
    };

    public static final String[] suggestionsReplace = {
            "u ", "u.", "p ", "p.", "t ", "t.", "tag.", "a./v2/"
    };

    /**
     * Try to open query before make search
     *
     * @param activity The current context
     * @param query    Query to open stuff
     * @return Return if somethings is opened
     */
    public static boolean open(Activity activity, String query) {
        if (query.contains("intra.42.")) {
            return openURL(activity, query);
        }
        return openActivity(activity, query);
    }

    private static boolean openURL(Activity activity, String query) {
        URL url;
        try {
            url = new URL(query);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        }

        String[] split = url.getPath().split("/");
        if (split.length <= 1)
            return false;

        if (query.contains("projects.intra.42")) {
            if (split.length != 3)
                return false;
            if (split[1].contentEquals("projects")) {
                Analytics.search("OPEN_URL_PROJECT", query);
                ProjectActivity.openIt(activity, split[2]);
                return true;
            } else if (split[2].contentEquals("mine") || true) {
                Analytics.search("OPEN_URL_PROJECT", query);
                ProjectActivity.openIt(activity, split[1]);
                return true;
            } else {
                ProjectActivity.openIt(activity, split[1], split[2]);
                return true;
            }
        } else if (query.contains("profile.intra.42")) {
            if (split.length == 3 && split[1].contentEquals("users")) {
                Analytics.search("OPEN_URL_USER", query);
                UserActivity.openIt(activity, split[2], activity);
                return true;
            }
        } else if (query.contains("forum.intra.42")) {
            if (split.length >= 3 && split[1].contentEquals("topics")) {
                Analytics.search("OPEN_URL_TOPIC", query);
                TopicActivity.openIt(activity, Integer.parseInt(split[2]));
                return true;
            }
        } else if (query.contains("elearning.intra.42")) {
            if (split.length < 3)
                return false;
            if (split[1].contentEquals("notions")) {
                Analytics.search("OPEN_URL_NOTION", query);
                SubnotionListActivity.openIt(activity, split[2]);
                return true;
            }
        } else if (split.length >= 3 && split[1].contentEquals("tags")) {
            Analytics.search("OPEN_URL_TAG", query);
            TagsActivity.openIt(activity, Integer.parseInt(split[2]));
        }
        return false;
    }

    /**
     * Open content from search bar like u.pvarry (open pvarry's profile page)
     *
     * @param activity A activity for context
     * @param query    The query
     * @return if open is catch
     */
    private static boolean openActivity(Activity activity, String query) {
        String[] split = query.split("\\.");
        if (split.length <= 1)
            return false;

        try {
            if (searchOnArray(R.array.search_users, split[0], activity)) {
                Analytics.search("OPEN_USER", query);
                UserActivity.openIt(activity, split[1], activity);
                return true;
            } else if (searchOnArray(R.array.search_projects, split[0], activity)) {
                Analytics.search("OPEN_PROJECT", query);
                ProjectActivity.openIt(activity, split[1]);
                return true;
            } else if (searchOnArray(R.array.search_topics, split[0], activity)) {
                Analytics.search("OPEN_TOPIC", query);
                TopicActivity.openIt(activity, Integer.parseInt(split[1]));
                return true;
            } else if (searchOnArray(R.array.search_tags, split[0], activity)) {
                Analytics.search("OPEN_TAG", query);
                TagsActivity.openIt(activity, Integer.parseInt(split[1]));
                return true;
            } else if (searchOnArray(R.array.search_location, split[0], activity)) {
                Analytics.search("OPEN_LOCATION", query);
                UserActivity.openLocation(activity, split[1], (AppClass) activity.getApplication());
                return true;
            } else if (query.contains("@student.42")) {
                Analytics.search("OPEN_USER_WITH_MAIL", query);
                UserActivity.openIt(activity, query.split("@")[0]);
                return true;
            } else
                return false;
        } catch (NumberFormatException e) {
            Toast.makeText(activity, activity.getString(R.string.search_error_on_open_this_element), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public static boolean searchOnArray(@ArrayRes int l, String elem, Context context) {
        Resources res = context.getResources();
        String[] array = res.getStringArray(l);

        for (String s : array) {
            if (s.equals(elem)) {
                return true;
            }
        }
        return false;
    }

    public static SimpleCursorAdapter setSearchSuggestionAdapter(Context context) {
        final String[] from = new String[]{"name"};
        final int[] to = new int[]{android.R.id.text1};
        SimpleCursorAdapter searchAdapter = new SimpleCursorAdapter(context, android.R.layout.simple_list_item_1, null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);


        final MatrixCursor c = new MatrixCursor(new String[]{BaseColumns._ID, "name"});
        for (int i = 0; i < SuperSearch.suggestions.length; i++) {
            c.addRow(new Object[]{i, SuperSearch.suggestions[i]});
        }
        searchAdapter.changeCursor(c);

        return searchAdapter;
    }

}
