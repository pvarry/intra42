package com.paulvarry.intra42.activities.tags;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.user.UserActivity;
import com.paulvarry.intra42.adapters.ViewStatePagerAdapter;
import com.paulvarry.intra42.api.ServiceGenerator;
import com.paulvarry.intra42.api.model.Tags;
import com.paulvarry.intra42.ui.BasicTabActivity;
import com.paulvarry.intra42.ui.BasicThreadActivity;
import com.paulvarry.intra42.utils.Tools;

import java.io.IOException;

import retrofit2.Response;

public class TagsActivity
        extends BasicTabActivity
        implements TagsForumFragment.OnFragmentInteractionListener, TagsNotionsFragment.OnFragmentInteractionListener, TagsProjectsFragment.OnFragmentInteractionListener, BasicThreadActivity.GetDataOnThread {

    private static final String INTENT_TAG = "login";
    private static final String INTENT_TAG_ID = "tag_id";
    public Tags tag;
    AppClass app;
    int tagId;

    public static boolean openIt(Context context, Tags tag) {
        if (tag != null) {
            Intent intent = new Intent(context, UserActivity.class);
            intent.putExtra(INTENT_TAG, ServiceGenerator.getGson().toJson(tag));
            context.startActivity(intent);
            return true;
        }
        return false;
    }

    public static boolean openIt(Context context, int tag) {

        Intent intent = new Intent(context, TagsActivity.class);
        intent.putExtra(INTENT_TAG_ID, tag);
        context.startActivity(intent);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        app = (AppClass) getApplication();

        Intent intent = getIntent();
        if (intent.hasExtra(INTENT_TAG_ID))
            tagId = intent.getIntExtra(INTENT_TAG_ID, -1);

        if (intent.hasExtra(INTENT_TAG))
            ServiceGenerator.getGson().fromJson(intent.getStringExtra(INTENT_TAG), Tags.class);

        super.activeHamburger();

        registerGetDataOnOtherThread(this);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void getDataOnOtherThread() throws UnauthorizedException, ErrorException, IOException {
        if (tag == null) {

            Response<Tags> response = app.getApiService().getTag(tagId).execute();
            if (Tools.apiIsSuccessful(response))
                tag = response.body();
        }
    }

    @Override
    public String getToolbarName() {
        if (tag != null)
            return tag.name;
        return String.valueOf(tagId);
    }

    /**
     * This text is useful when both {@link BasicThreadActivity.GetDataOnThread#getDataOnOtherThread()} and {@link BasicThreadActivity.GetDataOnMain#getDataOnMainThread()} return false.
     *
     * @return A simple text to display on screen, may return null;
     */
    @Override
    public String getEmptyText() {
        return null;
    }

    @Override
    public void setupViewPager(ViewPager viewPager) {
        ViewStatePagerAdapter adapter = new ViewStatePagerAdapter(getSupportFragmentManager());
//        adapter.addFragment(TagsProjectsFragment.newInstance(), getString(R.string.tab_tags_projects));
        adapter.addFragment(TagsForumFragment.newInstance(), getString(R.string.title_tab_tags_forum));
        adapter.addFragment(TagsNotionsFragment.newInstance(), getString(R.string.title_tab_tags_notions));
        viewPager.setAdapter(adapter);
    }

    public String getUrlIntra() {
        return null;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
