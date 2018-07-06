package com.paulvarry.intra42.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.IBaseItemSmall;

import java.util.List;

public abstract class BasicFragmentSpinner<T extends IBaseItemSmall, SPINNER_ADAPTER extends BaseAdapter>
        extends Fragment
        implements AdapterView.OnItemSelectedListener {

    private final static String SAVED_STATE_ID_TAG = "saved_state_tag";

    private TextView textView;
    private Spinner spinner;

    private int positionSelected;

    @Nullable
    protected List<T> listSpinnerHeader;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment__basic_spinner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewGroup layoutContent = view.findViewById(R.id.layoutContent);
        textView = view.findViewById(R.id.textView);
        spinner = view.findViewById(R.id.spinner);

        View childView = onCreateChildView(LayoutInflater.from(getContext()), layoutContent, savedInstanceState);
        layoutContent.addView(childView);

        textView.setVisibility(View.GONE);

        listSpinnerHeader = getSpinnerElemList();
        setupHeaderSpinner(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (listSpinnerHeader != null && positionSelected != -1 && listSpinnerHeader.size() > positionSelected)
            outState.putInt(SAVED_STATE_ID_TAG, listSpinnerHeader.get(positionSelected).getId());
        super.onSaveInstanceState(outState);
    }

    private void setupHeaderSpinner(Bundle savedInstanceState) {
        if (listSpinnerHeader != null && !listSpinnerHeader.isEmpty()) {
            int itemIdRequestSelection;
            int tmpPositionSelected = 0;

            if (savedInstanceState != null)
                itemIdRequestSelection = savedInstanceState.getInt(SAVED_STATE_ID_TAG);
            else
                itemIdRequestSelection = getSpinnerDefaultPosition(listSpinnerHeader);

            if (itemIdRequestSelection != 0)
                for (int i = 0; i < listSpinnerHeader.size(); i++) {
                    if (listSpinnerHeader.get(i).getId() == itemIdRequestSelection)
                        tmpPositionSelected = i;
                }
            SPINNER_ADAPTER adapter = onGenerateHeaderAdapter(listSpinnerHeader);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);

            if (tmpPositionSelected != 0)
                positionSelected = tmpPositionSelected;
            spinner.setSelection(positionSelected);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        positionSelected = i;
        textView.setVisibility(View.GONE);
        if (listSpinnerHeader != null && listSpinnerHeader.size() > positionSelected)
            onHeaderItemChanged(listSpinnerHeader.get(positionSelected));
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        positionSelected = -1;
        textView.setVisibility(View.VISIBLE);
    }

    public abstract View onCreateChildView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    public abstract void onHeaderItemChanged(T item);

    @Nullable
    public abstract List<T> getSpinnerElemList();

    public abstract int getSpinnerDefaultPosition(List<T> list);

    public abstract SPINNER_ADAPTER onGenerateHeaderAdapter(List<T> items);

}
