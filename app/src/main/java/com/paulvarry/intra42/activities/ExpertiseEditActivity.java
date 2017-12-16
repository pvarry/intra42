package com.paulvarry.intra42.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.Spinner;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.SpinnerAdapterExpertises;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.ServiceGenerator;
import com.paulvarry.intra42.api.model.ExpertiseUsers;
import com.paulvarry.intra42.api.model.Expertises;
import com.paulvarry.intra42.cache.CacheExpertise;
import com.paulvarry.intra42.ui.BasicEditActivity;
import com.paulvarry.intra42.ui.BasicThreadActivity;
import com.paulvarry.intra42.utils.Tools;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ExpertiseEditActivity
        extends BasicEditActivity
        implements BasicThreadActivity.GetDataOnThread, CompoundButton.OnCheckedChangeListener, RatingBar.OnRatingBarChangeListener, AdapterView.OnItemSelectedListener {

    static final String PARAM = "expertise_full";
    boolean changed = false;
    private ExpertiseUsers expertiseUsers;
    private Spinner spinnerExpertises;
    private CheckBox checkboxInterested;
    private RatingBar ratingBar;
    private int originalSpinnerSelection = -1;
    private List<Expertises> expertisesList;

    public static void open(Context context, ExpertiseUsers expertiseUsers) {
        Intent intent = new Intent(context, ExpertiseEditActivity.class);
        intent.putExtra(PARAM, ServiceGenerator.getGson().toJson(expertiseUsers));
        context.startActivity(intent);
    }

    public static void open(Context context) {
        Intent intent = new Intent(context, ExpertiseEditActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setContentView(R.layout.activity_expertise_edit);

        String extra = getIntent().getStringExtra(PARAM);
        if (extra != null)
            expertiseUsers = ServiceGenerator.getGson().fromJson(extra, ExpertiseUsers.class);

        registerGetDataOnOtherThread(this);

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public String getUrlIntra() {
        return getString(R.string.base_url_intra_profile_expertises_users);
    }

    @Override
    public String getToolbarName() {
        if (isCreate())
            return getString(R.string.user_expertise_new);
        else
            return getString(R.string.user_expertise_edit);
    }

    @Override
    public void setViewContent() {

        spinnerExpertises = findViewById(R.id.spinnerExpertises);
        checkboxInterested = findViewById(R.id.checkboxInterested);
        ratingBar = findViewById(R.id.ratingBar);

        final SpinnerAdapterExpertises adapter = new SpinnerAdapterExpertises(this, expertisesList);
        spinnerExpertises.setAdapter(adapter);

        if (!isCreate()) {
            originalSpinnerSelection = -1;
            for (int i = 0; i < expertisesList.size(); i++) {
                if (expertisesList.get(i).id == expertiseUsers.expertise.id)
                    originalSpinnerSelection = i;
            }
            if (originalSpinnerSelection != -1)
                spinnerExpertises.setSelection(originalSpinnerSelection);

            checkboxInterested.setChecked(expertiseUsers.interested);
            ratingBar.setRating(expertiseUsers.value);
        }

        checkboxInterested.setOnCheckedChangeListener(this);
        ratingBar.setOnRatingBarChangeListener(this);
        spinnerExpertises.setOnItemSelectedListener(this);
    }

    @Override
    public String getEmptyText() {
        return null;
    }

    @Override
    public void getDataOnOtherThread() throws IOException, RuntimeException {
        expertisesList = CacheExpertise.getAllowInternet(app.cacheSQLiteHelper, app);
        if (expertisesList == null || expertisesList.size() == 0)
            throw new RuntimeException();
    }

    @Override
    protected boolean isCreate() {
        return expertiseUsers == null;
    }

    @Override
    protected boolean haveUnsavedData() {
        return changed;
    }

    @Override
    protected void onSave(final BasicEditActivity.Callback callBack) {
        Expertises selected = (Expertises) spinnerExpertises.getSelectedItem();

        Call<Expertises> call;
        ApiService api = app.getApiService();
        if (isCreate())
            call = api.createExpertisesUsers(selected.id, app.me.id, (int) ratingBar.getRating(), checkboxInterested.isChecked());
        else
            call = api.updateExpertisesUsers(expertiseUsers.id, selected.id, app.me.id, (int) ratingBar.getRating(), checkboxInterested.isChecked());
        call.enqueue(new retrofit2.Callback<Expertises>() {
            @Override
            public void onResponse(Call<Expertises> call, Response<Expertises> response) {
                if (Tools.apiIsSuccessfulNoThrow(response))
                    callBack.succeed();
                else
                    callBack.succeed();
            }

            @Override
            public void onFailure(Call<Expertises> call, Throwable t) {
                callBack.message(t.getMessage());
            }
        });
    }

    @Override
    protected void onDelete(final BasicEditActivity.Callback callBack) {
        Call<Expertises> call;
        ApiService api = app.getApiService();

        call = api.deleteExpertisesUsers(expertiseUsers.id);

        call.enqueue(new retrofit2.Callback<Expertises>() {
            @Override
            public void onResponse(Call<Expertises> call, Response<Expertises> response) {
                if (Tools.apiIsSuccessfulNoThrow(response))
                    callBack.succeed();
                else
                    callBack.succeed();
            }

            @Override
            public void onFailure(Call<Expertises> call, Throwable t) {
                callBack.message(t.getMessage());
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        changed = true;
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        changed = true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        changed = true;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
