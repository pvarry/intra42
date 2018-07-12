package com.paulvarry.intra42.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.GridAdapterUsers;
import com.paulvarry.intra42.api.ApiService42Tools;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.api.tools42.Group;
import com.paulvarry.intra42.api.tools42.GroupLarge;
import com.paulvarry.intra42.api.tools42.GroupSmall;
import com.paulvarry.intra42.ui.BasicEditActivity;
import com.paulvarry.intra42.ui.BasicThreadActivity;
import com.paulvarry.intra42.utils.Tools;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;

public class FriendsGroupsEditActivity extends BasicEditActivity implements BasicThreadActivity.GetDataOnThread {

    private static final String PARAM = "group";

    @Nullable
    private GroupLarge group;
    private int groupId;

    private EditText editTextGroupName;
    private TextView textViewFriendsInGroup;
    private GridView gridView;

    static Intent getIntent(Context context, GroupSmall group) {
        Intent intent = new Intent(context, FriendsGroupsEditActivity.class);
        intent.putExtra(PARAM, group.id);
        return intent;
    }

    static Intent getIntent(Context context) {
        Intent intent = new Intent(context, FriendsGroupsEditActivity.class);
        intent.putExtra(PARAM, 0);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_friends_groups_edit);

        groupId = getIntent().getIntExtra(PARAM, -1);

        if (groupId == -1)
            finish();
        if (groupId != 0)
            registerGetDataOnOtherThread(this);

        editTextGroupName = findViewById(R.id.editTextGroupName);
        textViewFriendsInGroup = findViewById(R.id.textViewFriendsInGroup);
        gridView = findViewById(R.id.gridView);

        super.onCreateFinished();
    }

    @Override
    protected boolean isCreate() {
        return groupId == 0;
    }

    @Override
    protected boolean haveUnsavedData() {
        return group != null && !group.name.equals(editTextGroupName.getText().toString());
    }

    @Override
    protected void onSave(final Callback callBack) {
        String newName = editTextGroupName.getText().toString();
        if (newName.isEmpty()) {
            Toast.makeText(app, R.string.friends_groups_edit_no_empty_name, Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService42Tools api = app.getApiService42Tools();
        Call<Group> call;
        if (groupId == 0)
            call = api.createFriendsGroup(newName);
        else
            call = api.updateFriendsGroup(groupId, newName);

        call.enqueue(new retrofit2.Callback<Group>() {
            @Override
            public void onResponse(Call<Group> call, Response<Group> response) {
                if (Tools.apiIsSuccessfulNoThrow(response))
                    callBack.succeed();
                else if (response != null && response.message() != null)
                    callBack.failed(response.message());
                else
                    callBack.failed();
            }

            @Override
            public void onFailure(Call<Group> call, Throwable t) {
                callBack.failed(t.getMessage());
            }
        });
    }

    @Override
    protected void onDelete(final Callback callBack) {
        if (groupId == 0)
            return;
        Call<Void> call = app.getApiService42Tools().deleteFriendsGroup(groupId);
        call.enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (Tools.apiIsSuccessfulNoThrow(response))
                    callBack.succeed();
                else if (response != null && response.message() != null)
                    callBack.failed(response.message());
                else
                    callBack.failed();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callBack.failed(t.getMessage());
            }
        });
    }

    @Nullable
    @Override
    public String getUrlIntra() {
        return null;
    }

    @Override
    public String getToolbarName() {
        if (groupId == 0)
            return getString(R.string.friends_groups_new);
        else
            return getString(R.string.friends_groups_edit);
    }

    @Override
    protected void setViewContent() {
        if (group != null) {
            editTextGroupName.setText(group.name);

            if (group.users != null) {
                TreeSet<UsersLTE> friends = new TreeSet<>(group.users);

                GridAdapterUsers adapter = new GridAdapterUsers(this, new ArrayList<>(friends));
                gridView.setAdapter(adapter);
            }
        } else {
            gridView.setVisibility(View.GONE);
            textViewFriendsInGroup.setVisibility(View.GONE);
        }
    }

    @Override
    public String getEmptyText() {
        return null;
    }

    @Override
    public void getDataOnOtherThread() throws IOException, RuntimeException {
        ApiService42Tools api = app.getApiService42Tools();

        Response<GroupLarge> ret = api.getFriendsGroups(groupId).execute();
        if (Tools.apiIsSuccessful(ret))
            group = ret.body();
        else
            group = null;
    }
}
