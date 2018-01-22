package com.paulvarry.intra42.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.BaseListAdapterName;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.model.Attachments;
import com.paulvarry.intra42.api.model.Notions;
import com.paulvarry.intra42.api.model.Subnotions;
import com.paulvarry.intra42.ui.BasicThreadActivity;
import com.paulvarry.intra42.utils.Pagination;
import com.paulvarry.intra42.utils.Tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class SubnotionListActivity extends BasicThreadActivity implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, BasicThreadActivity.GetDataOnThread {

    private final static String INTENT_ID = "intent_subnotion_id";
    private final static String INTENT_SLUG = "intent_subnotion_slug";
    private final static String INTENT_NAME = "intent_subnotion_name";
    private SubnotionListActivity activity;
    private List<Subnotions> subnotionsList;

    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private BaseListAdapterName<Subnotions> adapter;

    private Call<List<Subnotions>> call;

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

        registerGetDataOnOtherThread(this);
        setContentView(R.layout.activity_subnotion);
        setActionBarToggle(ActionBarToggle.ARROW);

        activity = this;

        listView = findViewById(R.id.listView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(this);

        super.onCreateFinished();
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

    @Nullable
    @Override
    public String getUrlIntra() {
        return null;
    }

    @Override
    public String getToolbarName() {
        Intent i = getIntent();

        if (i.hasExtra(INTENT_NAME))
            return (i.getStringExtra(INTENT_NAME));
        else if (i.hasExtra(INTENT_SLUG))
            return (i.getStringExtra(INTENT_SLUG));
        else if (i.hasExtra(INTENT_ID))
            return (i.getStringExtra(INTENT_ID));

        return null;
    }

    @Override
    protected void setViewContent() {
        if (subnotionsList == null || subnotionsList.isEmpty()) {
            listView.setAdapter(null);
            setViewState(StatusCode.EMPTY);
        } else {
            if (adapter == null) {
                adapter = new BaseListAdapterName<>(this, subnotionsList);
                listView.setAdapter(adapter);
            }
            adapter.notifyDataSetChanged();
        }
        swipeRefreshLayout.setRefreshing(false);

        listView.setOnItemClickListener(this);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public String getEmptyText() {
        return null;
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        call.cancel();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        if (subnotionsList != null &&
                subnotionsList.size() > position &&
                subnotionsList.get(position).attachments != null &&
                !subnotionsList.get(position).attachments.isEmpty() &&
                subnotionsList.get(position).attachments.size() >= 1) {

            final Subnotions subnotion = subnotionsList.get(position);

            SpannableString iconPdf = getSpannableIcon(R.drawable.ic_picture_as_pdf_black_24dp, "[PDF]");
            SpannableString iconVideo = getSpannableIcon(R.drawable.ic_videocam_black_24dp, "[Video]");
            SpannableString iconHd = getSpannableIcon(R.drawable.ic_hd_black_24dp, "[HD]");

            List<CharSequence> list = new ArrayList<>();
            final List<String> list_urls = new ArrayList<>();
            SpannableStringBuilder stringBuilder;
            for (Attachments attachments : subnotion.attachments) {

                if (attachments.urls != null) {

                    list_urls.add(attachments.urls.url);
                    stringBuilder = new SpannableStringBuilder();
                    stringBuilder.append(iconVideo).append(" ");
                    stringBuilder.append(iconHd).append(" ");
                    if (attachments.language != null)
                        stringBuilder.append(attachments.language.getFlag()).append(" ");
                    stringBuilder.append("― ");
                    stringBuilder.append(attachments.name);
                    list.add(stringBuilder);

                    list_urls.add(attachments.urls.low_d);
                    stringBuilder = new SpannableStringBuilder();
                    stringBuilder.append(iconVideo).append(" ");
                    if (attachments.language != null)
                        stringBuilder.append(attachments.language.getFlag()).append(" ");
                    stringBuilder.append("― ");
                    stringBuilder.append(attachments.name);
                    list.add(stringBuilder);
                } else if (attachments.url != null) {
                    list_urls.add(attachments.url);
                    stringBuilder = new SpannableStringBuilder();
                    stringBuilder.append(iconPdf).append(" ");
                    if (attachments.language != null)
                        stringBuilder.append(attachments.language.getFlag()).append(" ");
                    stringBuilder.append("― ");
                    stringBuilder.append(attachments.name);
                    list.add(stringBuilder);
                }
            }

            final CharSequence[] items = new CharSequence[list.size()];
            int i = 0;
            for (CharSequence s : list) {
                items[i] = s;
                ++i;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.elearning_select_file_to_open);
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

    private SpannableString getSpannableIcon(int icon, String replacement) {
        SpannableString spannableIcon;
        try {
            Drawable image = ContextCompat.getDrawable(getApplicationContext(), icon);
            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());

            ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
            spannableIcon = new SpannableString(replacement);
            spannableIcon.setSpan(imageSpan, 0, replacement.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } catch (Resources.NotFoundException e) {
            spannableIcon = new SpannableString(replacement);
        }
        return spannableIcon;
    }

    @Override
    public void getDataOnOtherThread() throws IOException, RuntimeException {
        ApiService apiService = app.getApiService();
        Intent i = getIntent();

        if (i.hasExtra(INTENT_SLUG))
            call = apiService.getSubnotions(i.getStringExtra(INTENT_SLUG), Pagination.getPage(null));
        else
            call = apiService.getSubnotions(i.getIntExtra(INTENT_ID, 0), Pagination.getPage(null));

        Response<List<Subnotions>> response = call.execute();
        if (Tools.apiIsSuccessful(response)) {
            adapter = null;
            subnotionsList = response.body();
        }
    }
}
