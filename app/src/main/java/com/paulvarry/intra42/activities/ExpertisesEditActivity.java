package com.paulvarry.intra42.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.ListAdapterExpertisesEdit;
import com.paulvarry.intra42.adapters.SpinnerAdapterExpertises;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.model.Expertises;
import com.paulvarry.intra42.api.model.ExpertisesUsers;
import com.paulvarry.intra42.cache.CacheExpertises;
import com.paulvarry.intra42.ui.BasicActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpertisesEditActivity extends BasicActivity implements View.OnClickListener {

    List<ExpertisesUsers> expertisesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setContentView(R.layout.activity_expertises_edit);
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
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_expertises_edit);

        ListAdapterExpertisesEdit adapterExpertisesEdit = new ListAdapterExpertisesEdit(this, expertisesList);
        listView.setAdapter(adapterExpertisesEdit);

        fabBaseActivity.setVisibility(View.VISIBLE);
        fabBaseActivity.setOnClickListener(this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public String getEmptyText() {
        fabBaseActivity.setVisibility(View.VISIBLE);
        fabBaseActivity.setOnClickListener(this);
        return null;
    }

    @Override
    public void onClick(View v) {
        if (v == fabBaseActivity) {
            prepareDialog(null);
        }
    }

    public void prepareDialog(final ExpertisesUsers expertisesUsers) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Expertises> expertisesList = CacheExpertises.getAllowInternet(app.cacheSQLiteHelper, app);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.cancel();
                        openDialog(expertisesList, expertisesUsers);
                    }
                });
            }
        }).start();
    }

    void openDialog(final List<Expertises> expertisesList, final ExpertisesUsers expertisesUsers) {
        TextView textViewTitle;
        final Spinner spinnerExpertises;
        final CheckBox checkboxInterested;
        final RatingBar ratingBar;
        Button buttonDiscard;
        Button buttonCreate;

        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_edit_expertises);

        textViewTitle = (TextView) dialog.findViewById(R.id.textViewTitle);
        spinnerExpertises = (Spinner) dialog.findViewById(R.id.spinnerExpertises);
        checkboxInterested = (CheckBox) dialog.findViewById(R.id.checkboxInterested);
        ratingBar = (RatingBar) dialog.findViewById(R.id.ratingBar);
        buttonDiscard = (Button) dialog.findViewById(R.id.buttonDiscard);
        buttonCreate = (Button) dialog.findViewById(R.id.buttonCreate);

        if (expertisesUsers == null)
            textViewTitle.setText(R.string.new_expertise);
        else
            textViewTitle.setText(R.string.edit_expertise);

        final SpinnerAdapterExpertises adapter = new SpinnerAdapterExpertises(this, expertisesList);
        spinnerExpertises.setAdapter(adapter);

        if (expertisesUsers != null) {
            int selection = -1;
            for (int i = 0; i < expertisesList.size(); i++) {
                if (expertisesList.get(i).id == expertisesUsers.expertise.id)
                    selection = i;
            }
            if (selection != -1)
                spinnerExpertises.setSelection(selection);
            checkboxInterested.setChecked(expertisesUsers.interested);
            ratingBar.setRating(expertisesUsers.value);
        }

        buttonDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Expertises selected = (Expertises) spinnerExpertises.getSelectedItem();

                Call<Expertises> call;
                ApiService api = app.getApiService();
                if (expertisesUsers == null)
                    call = api.createExpertisesUsers(selected.id, app.me.id, (int) ratingBar.getRating(), checkboxInterested.isChecked());
                else
                    call = api.updateExpertisesUsers(expertisesUsers.id, selected.id, app.me.id, (int) ratingBar.getRating(), checkboxInterested.isChecked());
                call.enqueue(new Callback<Expertises>() {
                    @Override
                    public void onResponse(Call<Expertises> call, Response<Expertises> response) {
                        if (response.isSuccessful())
                            Toast.makeText(ExpertisesEditActivity.this, R.string.done, Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(ExpertisesEditActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                        ExpertisesEditActivity.this.refresh();
                        dialog.cancel();
                    }

                    @Override
                    public void onFailure(Call<Expertises> call, Throwable t) {
                        Toast.makeText(ExpertisesEditActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
            }
        });

        dialog.show();
    }

    public void deleteExpertiseUser(ExpertisesUsers expertisesUsers) {
        Call<Expertises> call;
        ApiService api = app.getApiService();

        call = api.deleteExpertisesUsers(expertisesUsers.id);

        call.enqueue(new Callback<Expertises>() {
            @Override
            public void onResponse(Call<Expertises> call, Response<Expertises> response) {
                if (response.isSuccessful())
                    Toast.makeText(ExpertisesEditActivity.this, R.string.deleted, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(ExpertisesEditActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                ExpertisesEditActivity.this.refresh();

            }

            @Override
            public void onFailure(Call<Expertises> call, Throwable t) {
                Toast.makeText(ExpertisesEditActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
