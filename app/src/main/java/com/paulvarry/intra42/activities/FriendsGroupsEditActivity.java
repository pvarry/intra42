package com.paulvarry.intra42.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.GridAdapterUsers;
import com.paulvarry.intra42.api.ApiService42Tools;
import com.paulvarry.intra42.api.tools42.Group;
import com.paulvarry.intra42.api.tools42.GroupLarge;
import com.paulvarry.intra42.api.tools42.GroupSmall;
import com.paulvarry.intra42.ui.BasicThreadActivity;
import com.paulvarry.intra42.utils.Tools;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendsGroupsEditActivity extends BasicThreadActivity implements BasicThreadActivity.GetDataOnThread, View.OnClickListener {

    static final String PARAM = "group";

    GroupLarge group;
    int groupId;

    ViewGroup layoutGroupName;
    TextView textViewSub;
    GridView gridView;
    TextView textViewFriendsInGroup;

    String newName;

    static void open(Context context, GroupSmall group) {
        Intent intent = new Intent(context, FriendsGroupsEditActivity.class);
        intent.putExtra(PARAM, group.id);
        context.startActivity(intent);
    }

    static void open(Context context) {
        Intent intent = new Intent(context, FriendsGroupsEditActivity.class);
        intent.putExtra(PARAM, 0);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_friends_groups_edit);
        super.onCreate(savedInstanceState);

        groupId = getIntent().getIntExtra(PARAM, -1);

        if (groupId == -1)
            finish();
        if (groupId != 0)
            registerGetDataOnOtherThread(this);

        layoutGroupName = findViewById(R.id.layoutGroupName);
        textViewSub = findViewById(R.id.textViewSub);
        gridView = findViewById(R.id.gridView);
        textViewFriendsInGroup = findViewById(R.id.textViewFriendsInGroup);

        layoutGroupName.setOnClickListener(this);

        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_edit_activity, menu);

        MenuItem menuItemSave = menu.findItem(R.id.actionSave);
        menuItemSave.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                actionSave();
                return true;
            }
        });

        MenuItem menuItemDelete = menu.findItem(R.id.actionDelete);
        if (groupId == 0)
            menuItemDelete.setVisible(false);
        else
            menuItemDelete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    actionDelete();
                    return true;
                }
            });

        return true;
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
            newName = group.name;
            textViewSub.setText(group.name);

            GridAdapterUsers adapter = new GridAdapterUsers(this, group.users);
            gridView.setAdapter(adapter);
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

    @Override
    public void onBackPressed() {
        if ((groupId == 0 && (newName == null || newName.isEmpty())) ||
                groupId != 0 && !newName.contentEquals(group.name)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.friends_groups_edit_unsaved_changes_title);
            builder.setMessage(R.string.friends_groups_edit_unsaved_changes_content);

            builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    actionSave();
                }
            });
            builder.setNegativeButton(R.string.friends_group_edit_discard, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FriendsGroupsEditActivity.super.onBackPressed();
                }
            });
            builder.setNeutralButton(R.string.cancel, null);

            builder.show();
        } else
            super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if (v == layoutGroupName) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            input.setText(newName);

            input.setSingleLine();
            input.setSelection(input.getText().length());

            FrameLayout container = new FrameLayout(this);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
            input.setLayoutParams(params);
            container.addView(input);
            builder.setView(container);

            builder.setTitle("Edit name");
            builder.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String tmp = input.getText().toString().trim();
                    if (tmp.isEmpty())
                        Toast.makeText(app, R.string.friends_groups_edit_no_empty_name, Toast.LENGTH_SHORT).show();
                    else
                        newName = tmp;
                    textViewSub.setText(tmp);
                }
            });
            builder.setNegativeButton(R.string.cancel, null);
            AlertDialog dialog = builder.create();

            Window window = dialog.getWindow();
            if (window != null)
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            dialog.show();
        }
    }

    void actionSave() {
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
        call.enqueue(new Callback<Group>() {
            @Override
            public void onResponse(Call<Group> call, Response<Group> response) {
                if (Tools.apiIsSuccessfulNoThrow(response)) {
                    Toast.makeText(FriendsGroupsEditActivity.this, R.string.done, Toast.LENGTH_SHORT).show();
                    finish();
                } else
                    Toast.makeText(FriendsGroupsEditActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Group> call, Throwable t) {
                Toast.makeText(FriendsGroupsEditActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    void actionDelete() {
        if (groupId == 0)
            return;
        Call<Void> call = app.getApiService42Tools().deleteFriendsGroup(groupId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (Tools.apiIsSuccessfulNoThrow(response)) {
                    Toast.makeText(FriendsGroupsEditActivity.this, R.string.done, Toast.LENGTH_SHORT).show();
                    finish();
                } else
                    Toast.makeText(FriendsGroupsEditActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(FriendsGroupsEditActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
