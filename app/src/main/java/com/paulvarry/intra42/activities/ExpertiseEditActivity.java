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
import com.paulvarry.intra42.adapters.ListAdapterExpertiseEdit;
import com.paulvarry.intra42.adapters.SpinnerAdapterExpertises;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.model.ExpertiseUsers;
import com.paulvarry.intra42.api.model.Expertises;
import com.paulvarry.intra42.cache.CacheExpertise;
import com.paulvarry.intra42.ui.BasicThreadActivity;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpertiseEditActivity extends BasicThreadActivity implements View.OnClickListener, BasicThreadActivity.GetDataOnThread {

    List<ExpertiseUsers> expertiseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setContentView(R.layout.activity_expertise_edit);

        registerGetDataOnOtherThread(this);

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public String getUrlIntra() {
        return getString(R.string.base_url_intra_profile_expertises_users);
    }

    @Override
    public void getDataOnOtherThread() throws UnauthorizedException, ErrorServerException, IOException {
        expertiseList = ExpertiseUsers.getExpertiseUsers(app.getApiService(), app.me);
    }

    @Override
    public String getToolbarName() {
        return getString(R.string.user_expertise_edit_plural);
    }

    @Override
    public void setViewContent() {

        if (expertiseList == null || expertiseList.size() == 0) {
            setViewState(StatusCode.EMPTY);
            return;
        }

        ListView listView = findViewById(R.id.listView);
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.activity_expertise_edit);

        ListAdapterExpertiseEdit adapterExpertisesEdit = new ListAdapterExpertiseEdit(this, expertiseList);
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

    public void prepareDialog(final ExpertiseUsers expertisesUsers) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Expertises> expertisesList = CacheExpertise.getAllowInternet(app.cacheSQLiteHelper, app);
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

    void openDialog(final List<Expertises> expertisesList, final ExpertiseUsers expertisesUsers) {
        TextView textViewTitle;
        final Spinner spinnerExpertises;
        final CheckBox checkboxInterested;
        final RatingBar ratingBar;
        Button buttonDiscard;
        Button buttonCreate;

        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_edit_expertises);

        textViewTitle = dialog.findViewById(R.id.textViewTitle);
        spinnerExpertises = dialog.findViewById(R.id.spinnerExpertises);
        checkboxInterested = dialog.findViewById(R.id.checkboxInterested);
        ratingBar = dialog.findViewById(R.id.ratingBar);
        buttonDiscard = dialog.findViewById(R.id.buttonDiscard);
        buttonCreate = dialog.findViewById(R.id.buttonCreate);

        if (expertisesUsers == null)
            textViewTitle.setText(R.string.user_expertise_new);
        else
            textViewTitle.setText(R.string.user_expertise_edit);

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
                            Toast.makeText(ExpertiseEditActivity.this, R.string.done, Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(ExpertiseEditActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                        ExpertiseEditActivity.this.refresh();
                        dialog.cancel();
                    }

                    @Override
                    public void onFailure(Call<Expertises> call, Throwable t) {
                        Toast.makeText(ExpertiseEditActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
            }
        });

        dialog.show();
    }

    public void deleteExpertiseUser(ExpertiseUsers expertisesUsers) {
        Call<Expertises> call;
        ApiService api = app.getApiService();

        call = api.deleteExpertisesUsers(expertisesUsers.id);

        call.enqueue(new Callback<Expertises>() {
            @Override
            public void onResponse(Call<Expertises> call, Response<Expertises> response) {
                if (response.isSuccessful())
                    Toast.makeText(ExpertiseEditActivity.this, R.string.user_expertise_deleted, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(ExpertiseEditActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                ExpertiseEditActivity.this.refresh();

            }

            @Override
            public void onFailure(Call<Expertises> call, Throwable t) {
                Toast.makeText(ExpertiseEditActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
