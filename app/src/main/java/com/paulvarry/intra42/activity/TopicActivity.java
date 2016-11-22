package com.paulvarry.intra42.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.paulvarry.intra42.Adapter.ExpandableListAdapterTopic;
import com.paulvarry.intra42.ApiService;
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.Interface.InterfaceViewActivity;
import com.paulvarry.intra42.MenuActivity;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.Tools.BypassPicassoImageGetter;
import com.paulvarry.intra42.api.Messages;
import com.paulvarry.intra42.api.Topics;
import com.paulvarry.intra42.api.model.Topic;
import com.paulvarry.intra42.oauth.ServiceGenerator;
import com.paulvarry.intra42.tab.user.UserActivity;
import com.squareup.picasso.Picasso;

import in.uncod.android.bypass.Bypass;
import retrofit2.Call;
import retrofit2.Callback;

public class TopicActivity extends MenuActivity.NoMenu
        implements InterfaceViewActivity, AdapterView.OnItemLongClickListener, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private final static String INTENT_ID = "intent_topic_id";
    private final static String INTENT_TOPIC_JSON = "intent_topic_json";
    public AppClass app;
    TopicActivity activity = this;
    @Nullable
    private Topic topic;
    private int id;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ExpandableListView listView;
    private ScrollView scrollViewReply;
    private EditText editTextReply;
    private ImageButton buttonReply;
    private FloatingActionButton fab;
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
        setContentView(R.layout.activity_topic);

//        topics = Parcels.unwrap(getIntent().getParcelableExtra("lol"));
        id = getIntent().getIntExtra(INTENT_ID, 0);
        app = (AppClass) getApplication();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        scrollViewReply = (ScrollView) findViewById(R.id.scrollViewReply);
        editTextReply = (EditText) findViewById(R.id.editTextReply);
        listView = (ExpandableListView) findViewById(R.id.expandableListView);
        buttonReply = (ImageButton) findViewById(R.id.buttonReply);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        textViewPreviewMessage = (TextView) findViewById(R.id.textViewPreviewMessage);

        scrollViewReply.setVisibility(View.GONE);
        swipeRefreshLayout.setOnRefreshListener(this);
        buttonReply.setOnClickListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        super.onCreate(savedInstanceState);
        super.setSelectedMenu(MENU_SELECTED_FORUM);
    }

    @Override
    public void getData() {
        String b = getIntent().getStringExtra(INTENT_TOPIC_JSON);

        if (b != null) {
            Topics topics = ServiceGenerator.getGson().fromJson(b, Topics.class);
            if (topics != null && topics.id != 0) {
                topic = Topic.get(app.getApiService(), topics);
                return;
            }
        }

        topic = Topic.get(app.getApiService(), id);
    }

    @Override
    public void setView() {
        if (topic == null) {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            return;
        }
        if (topic.topic != null) {
            getSupportActionBar().setTitle(topic.topic.name);
        }

        ExpandableListAdapterTopic adapterTopic = new ExpandableListAdapterTopic(this, topic);
        listView.setAdapter(adapterTopic);

        fab.setOnClickListener(this);
        fab.setVisibility(View.VISIBLE);
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

//        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//            public boolean onGroupClick(ExpandableListView arg0, View itemView, int itemPosition, long itemId) {
//                listView.expandGroup(itemPosition);
//                BottomSheetDialogFragment bottomSheetDialogFragment =
//                        BottomSheetTopicInfoDialogFragment.newInstance(topic.messages.get(itemPosition));
//                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
//                return true;
//            }
//        });
//        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//            @Override
//            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//
//                BottomSheetDialogFragment bottomSheetDialogFragment =
//                        BottomSheetTopicInfoDialogFragment.newInstance(topic.messages.get(groupPosition).replies.get(childPosition));
//                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
//                return true;
//            }
//        });
//        listView.setOnItemLongClickListener(this);

        for (int i = 0; i < topic.messages.size(); i++) {
            listView.expandGroup(i);
        }

        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                getData();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setView();
                    }
                });
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_old, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_open_intra) {
            if (topic != null && topic.topic != null) {
                String url = getString(R.string.base_url_intra_forum) + "topics/" + String.valueOf(topic.topic.id);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return true;
            }
            return false;
        } else if (id == R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    public void newMessage(View view) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        int itemType = ExpandableListView.getPackedPositionType(id);
        long pos = listView.getExpandableListPosition(position);

        if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            int childPosition = ExpandableListView.getPackedPositionChild(pos);
            int groupPosition = ExpandableListView.getPackedPositionGroup(pos);
            if (childPosition >= 0 &&
                    topic.messages.size() > groupPosition &&
                    topic.messages.get(groupPosition).replies != null &&
                    topic.messages.get(groupPosition).replies.size() > childPosition) {
                UserActivity.openIt(this, topic.messages.get(groupPosition).replies.get(childPosition).author, activity);
                return true; //true if we consumed the click, false if not
            }

        } else if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            int groupPosition = ExpandableListView.getPackedPositionGroup(pos);
            if (groupPosition >= 0) {
                if (topic.topic != null) {
                    if (groupPosition == 0 && topic.topic.author.isMe(app))
                        NewTopicActivity.openIt(activity, topic.topic);
                    else
                        UserActivity.openIt(this, topic.messages.get(groupPosition).author, app);
                    return true; //true if we consumed the click, false if not
                }
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v == buttonReply) {

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
                    fab.setVisibility(View.VISIBLE);
                    scrollViewReply.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<Messages> call, Throwable t) {
                    Toast.makeText(TopicActivity.this, "Failed: " + t.getMessage() + "\nDon't forget to refresh", Toast.LENGTH_SHORT).show();
                    editTextReply.setText("");
                    scrollViewReply.setVisibility(View.GONE);
                }
            });

        } else if (v == fab) {
            fab.setVisibility(View.GONE);
            scrollViewReply.setVisibility(View.VISIBLE);
            textViewPreviewMessage.setVisibility(View.GONE);
        }
    }
}
