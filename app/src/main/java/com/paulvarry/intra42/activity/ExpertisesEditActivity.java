package com.paulvarry.intra42.activity;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.paulvarry.intra42.Adapter.ListAdapterExpertisesEdit;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.Tools.Pagination;
import com.paulvarry.intra42.api.Expertises;
import com.paulvarry.intra42.api.ExpertisesUsers;
import com.paulvarry.intra42.ui.BasicActivity;

import java.util.List;

public class ExpertisesEditActivity extends BasicActivity {

    List<ExpertisesUsers> expertisesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public String getUrlIntra() {
        return "https://profile.intra.42.fr/expertises_users";
    }

    @Override
    public boolean getDataOnOtherThread() {

        expertisesList = ExpertisesUsers.getExpertisesUsers(app.getApiService(), app.me);
        return expertisesList != null && expertisesList.size() != 0;
    }

    @Override
    public boolean getDataOnMainThread() {
        return false;
    }

    @Override
    public String getToolbarName() {
        return getString(R.string.edit_expertises);
    }

    @Override
    public void setViewContent() {
        ListView listView = (ListView) findViewById(R.id.listView);

        ListAdapterExpertisesEdit adapterExpertisesEdit = new ListAdapterExpertisesEdit(getBaseContext(), expertisesList);
        listView.setAdapter(adapterExpertisesEdit);
    }

    @Override
    public int getViewContentResID() {
        return R.layout.activity_expertises_edit;
    }

    @Override
    public String getEmptyText() {
        return null;
    }
}
