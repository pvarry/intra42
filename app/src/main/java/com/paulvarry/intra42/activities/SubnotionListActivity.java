package com.paulvarry.intra42.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.ListAdapterSubnotions;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.model.Attachments;
import com.paulvarry.intra42.api.model.Notions;
import com.paulvarry.intra42.api.model.Subnotions;
import com.paulvarry.intra42.utils.Pagination;
import com.paulvarry.intra42.utils.Tools;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubnotionListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    private final static String INTENT_ID = "intent_subnotion_id";
    private final static String INTENT_SLUG = "intent_subnotion_slug";
    private final static String INTENT_NAME = "intent_subnotion_name";
    private SubnotionListActivity activity;
    private List<Subnotions> subnotionsList;

    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView textView;

    private AppClass app;
    private ListAdapterSubnotions adapter;
    private Call<List<Subnotions>> call;

    private Callback<List<Subnotions>> callback = new Callback<List<Subnotions>>() {

        @Override
        public void onResponse(Call<List<Subnotions>> call, Response<List<Subnotions>> response) {
            List<Subnotions> list = response.body();
            if (subnotionsList != null && list != null)
                subnotionsList.addAll(list);
            else
                subnotionsList = list;

            setView();
        }

        @Override
        public void onFailure(Call<List<Subnotions>> call, Throwable t) {
            if (!call.isCanceled())
                Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show();
            t.printStackTrace();
            setView();
        }
    };

    public static void openIt(Context context, String notionSlug) {
        Intent intent = new Intent(context, SubnotionListActivity.class);
        intent.putExtra(INTENT_SLUG, notionSlug);
        context.startActivity(intent);
    }

    public static void openIt(Context context, int notionId, String name) {
        Intent intent = new Intent(context, SubnotionListActivity.class);
        intent.putExtra(INTENT_ID, notionId);
        intent.putExtra(INTENT_NAME, name);
        context.startActivity(intent);
    }

    public static void openIt(Context context, Notions notion) {
        Intent intent = new Intent(context, SubnotionListActivity.class);
        intent.putExtra(INTENT_ID, notion.id);
        intent.putExtra(INTENT_NAME, notion.name);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subnotion);
        Intent i = getIntent();

        app = (AppClass) getApplication();
        if (i.hasExtra(INTENT_NAME))
            setTitle(i.getStringExtra(INTENT_NAME));
        else if (i.hasExtra(INTENT_SLUG))
            setTitle(i.getStringExtra(INTENT_SLUG));
        else if (i.hasExtra(INTENT_ID))
            setTitle(i.getStringExtra(INTENT_ID));
        activity = this;
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        listView = (ListView) findViewById(R.id.listView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        textView = (TextView) findViewById(R.id.textView);

        swipeRefreshLayout.setOnRefreshListener(this);
        textView.setVisibility(View.GONE);
        onRefresh();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        ApiService apiService = app.getApiService();
        Intent i = getIntent();

        Call<List<Subnotions>> call;

        if (i.hasExtra(INTENT_SLUG))
            call = apiService.getSubnotions(i.getStringExtra(INTENT_SLUG), Pagination.getPage(null));
        else
            call = apiService.getSubnotions(i.getIntExtra(INTENT_ID, 0), Pagination.getPage(null));

        adapter = null;
        subnotionsList = null;

        this.call = call;
        call.enqueue(callback);
    }

    private void setView() {

        if (subnotionsList == null || subnotionsList.isEmpty()) {
            listView.setAdapter(null);
            textView.setVisibility(View.VISIBLE);
        } else {
            if (adapter == null) {
                adapter = new ListAdapterSubnotions(this, subnotionsList);
                listView.setAdapter(adapter);
            }
            adapter.notifyDataSetChanged();
            textView.setVisibility(View.GONE);
//            flag_loading = false;
        }
        swipeRefreshLayout.setRefreshing(false);

        listView.setOnItemClickListener(this);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onStop() {
        super.onStop();

        call.cancel();
    }

    @Override

    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        if (subnotionsList.get(position).attachments != null &&
                !subnotionsList.get(position).attachments.isEmpty() &&
                subnotionsList.get(position).attachments.size() >= 1) {

            final Subnotions subnotion = subnotionsList.get(position);

            List<String> list = new ArrayList<>();
            final List<String> list_urls = new ArrayList<>();
            for (Attachments attachments : subnotion.attachments) {
                if (attachments.urls != null) {
                    list_urls.add(attachments.urls.url);
                    list.add((attachments.language != null ? "[" + attachments.language.identifier + "]" : "") + "[VIDEO][Full] " + attachments.urls.url.substring(attachments.urls.url.lastIndexOf('/') + 1));
                    list_urls.add(attachments.urls.low_d);
                    list.add((attachments.language != null ? "[" + attachments.language.identifier + "]" : "") + "[VIDEO][Low] " + attachments.urls.low_d.substring(attachments.urls.low_d.lastIndexOf('/') + 1));
                } else if (attachments.url != null) {
                    list_urls.add(attachments.url);
                    list.add((attachments.language != null ? "[" + attachments.language.identifier + "]" : "") + "[PDF] " + attachments.url.substring(attachments.url.lastIndexOf('/') + 1));
                }
            }

            final CharSequence[] items = new CharSequence[list.size()];
            int i = 0;
            for (String s : list) {
                items[i] = s;
                ++i;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.select_file_to_open);
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection

                    Tools.openAttachment(activity, list_urls.get(item));
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }


    }
}
