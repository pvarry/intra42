package com.paulvarry.intra42.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.user.UserActivity;
import com.paulvarry.intra42.adapters.ExpandableListAdapterTopic;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.ServiceGenerator;
import com.paulvarry.intra42.api.model.Messages;
import com.paulvarry.intra42.api.model.Topics;
import com.paulvarry.intra42.api.pack.Topic;
import com.paulvarry.intra42.ui.BasicThreadActivity;
import com.paulvarry.intra42.ui.tools.Navigation;
import com.paulvarry.intra42.utils.BypassPicassoImageGetter;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import in.uncod.android.bypass.Bypass;
import retrofit2.Call;
import retrofit2.Callback;

public class TopicActivity
        extends BasicThreadActivity
        implements AdapterView.OnItemLongClickListener, View.OnClickListener, BasicThreadActivity.GetDataOnThread, SwipeRefreshLayout.OnRefreshListener {

    private final static String INTENT_ID = "intent_topic_id";
    private final static String INTENT_TOPIC_JSON = "intent_topic_json";
    TopicActivity activity = this;
    @Nullable
    private Topic topic;
    private int id;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ExpandableListView listView;
    private ScrollView scrollViewReply;
    private EditText editTextReply;
    private ImageButton buttonReply;
    private TextView textViewPreviewMessage;

    public static void openIt(Context context, Topics topics) {
        Intent intent = new Intent(context, TopicActivity.class);
        intent.putExtra(INTENT_TOPIC_JSON, ServiceGenerator.getGson().toJson(topics));
        intent.putExtra(INTENT_ID, topics.id);
        context.startActivity(intent);
    }

    public static void openIt(Context context, int id) {
        Intent intent = new Intent(context, TopicActivity.class);
        intent.putExtra(INTENT_ID, id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setContentView(R.layout.activity_content_topic);

//        topics = Parcels.unwrap(getIntent().getParcelableExtra("lol"));
        id = getIntent().getIntExtra(INTENT_ID, 0);

        super.setSelectedMenu(Navigation.MENU_SELECTED_FORUM);

        registerGetDataOnOtherThread(this);

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public String getUrlIntra() {
        if (topic != null && topic.topic != null) {
            return getString(R.string.base_url_intra_forum) + "topics/" + String.valueOf(topic.topic.id);
        }
        return getString(R.string.base_url_intra_forum);
    }

    @Override
    public void getDataOnOtherThread() throws ErrorServerException, IOException, UnauthorizedException {
        String json = getIntent().getStringExtra(INTENT_TOPIC_JSON);

        if (json != null) {
            Topics topics = ServiceGenerator.getGson().fromJson(json, Topics.class);
            if (topics != null && topics.id != 0) {
                topic = Topic.get(this, app.getApiService(), topics);
            }
        }

        topic = Topic.get(this, app.getApiService(), id);
    }

    @Override
    public String getToolbarName() {
        if (topic != null && topic.topic != null) {
            return topic.topic.name;
        }
        return null;
    }

    @Override
    public void setViewContent() {

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        scrollViewReply = findViewById(R.id.scrollViewReply);
        editTextReply = findViewById(R.id.editTextReply);
        listView = findViewById(R.id.expandableListView);
        buttonReply = findViewById(R.id.buttonReply);
        textViewPreviewMessage = findViewById(R.id.textViewPreviewMessage);

        scrollViewReply.setVisibility(View.GONE);
        swipeRefreshLayout.setOnRefreshListener(this);
        buttonReply.setOnClickListener(this);

        if (topic == null) {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            return;
        }

        ExpandableListAdapterTopic adapterTopic = new ExpandableListAdapterTopic(this, topic);
        listView.setAdapter(adapterTopic);

        fabBaseActivity.setOnClickListener(this);
        fabBaseActivity.setVisibility(View.VISIBLE);
        scrollViewReply.setVisibility(View.GONE);
        editTextReply.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                if (text.isEmpty())
                    textViewPreviewMessage.setVisibility(View.GONE);
                else {
                    Bypass bypass = new Bypass(TopicActivity.this);
                    CharSequence messageContent = bypass.markdownToSpannable(text, new BypassPicassoImageGetter(textViewPreviewMessage, Picasso.with(TopicActivity.this)));
                    textViewPreviewMessage.setText(messageContent);
                    textViewPreviewMessage.setVisibility(View.VISIBLE);
                }
            }
        });

        for (int i = 0; i < topic.messages.size(); i++) {
            listView.expandGroup(i);
        }

        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public String getEmptyText() {
        return null;
    }

    public void newMessage(View view) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        int itemType = ExpandableListView.getPackedPositionType(id);
        long pos = listView.getExpandableListPosition(position);

        if (topic == null)
            return false;

        if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            int childPosition = ExpandableListView.getPackedPositionChild(pos);
            int groupPosition = ExpandableListView.getPackedPositionGroup(pos);
            if (childPosition >= 0 &&
                    topic.messages.size() > groupPosition &&
                    topic.messages.get(groupPosition).replies != null &&
                    topic.messages.get(groupPosition).replies.size() > childPosition) {
                UserActivity.openIt(app, topic.messages.get(groupPosition).replies.get(childPosition).author);
                return true; //true if we consumed the click, false if not
            }

        } else if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            int groupPosition = ExpandableListView.getPackedPositionGroup(pos);
            if (groupPosition >= 0) {
                if (topic.topic != null) {
                    if (groupPosition == 0 && topic.topic.author.isMe(app))
                        NewTopicActivity.openIt(activity, topic.topic);
                    else
                        UserActivity.openIt(app, topic.messages.get(groupPosition).author);
                    return true; //true if we consumed the click, false if not
                }
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v == buttonReply) {

            if (topic == null)
                return;
            ApiService api = activity.app.getApiService();
            Call<Messages> call = api.createTopicReply(topic.topic.id, app.me.id, editTextReply.getText().toString());
            call.enqueue(new Callback<Messages>() {
                @Override
                public void onResponse(Call<Messages> call, retrofit2.Response<Messages> response) {
                    if (response.isSuccessful())
                        Toast.makeText(TopicActivity.this, "Success\nDon't forget to refresh", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(TopicActivity.this, "Error: " + response.message() + "\nDon't forget to refresh", Toast.LENGTH_SHORT).show();
                    editTextReply.setText("");
                    fabBaseActivity.setVisibility(View.VISIBLE);
                    scrollViewReply.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<Messages> call, Throwable t) {
                    Toast.makeText(TopicActivity.this, "Failed: " + t.getMessage() + "\nDon't forget to refresh", Toast.LENGTH_SHORT).show();
                    editTextReply.setText("");
                    scrollViewReply.setVisibility(View.GONE);
                }
            });

        } else if (v == fabBaseActivity) {
            fabBaseActivity.setVisibility(View.GONE);
            scrollViewReply.setVisibility(View.VISIBLE);
            textViewPreviewMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRefresh() {
        super.refresh();
    }
}
