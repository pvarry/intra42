package com.paulvarry.intra42.ui;

import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class BasicFragmentSpinner<T extends IBaseItemSmall, SPINNER_ADAPTER extends BaseAdapter>
        extends Fragment
        implements AdapterView.OnItemSelectedListener {

    private final static String SAVED_STATE_ID_TAG = "saved_state_tag";

    private ViewGroup viewGroupHeader;
    private View viewGroupChild;
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
        viewGroupHeader = view.findViewById(R.id.viewGroupHeader);
        textView = view.findViewById(R.id.textView);
        spinner = view.findViewById(R.id.spinner);

        viewGroupChild = onCreateChildView(LayoutInflater.from(getContext()), layoutContent, savedInstanceState);
        layoutContent.addView(viewGroupChild);

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

            viewGroupHeader.setVisibility(View.VISIBLE);
            viewGroupChild.setVisibility(View.VISIBLE);
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
        } else {
            viewGroupHeader.setVisibility(View.GONE);
            viewGroupChild.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        positionSelected = i;
        textView.setVisibility(View.GONE);
        viewGroupHeader.setVisibility(View.VISIBLE);
        viewGroupChild.setVisibility(View.VISIBLE);
        if (listSpinnerHeader != null && listSpinnerHeader.size() > positionSelected)
            onHeaderItemChanged(listSpinnerHeader.get(positionSelected));
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        positionSelected = -1;
    }

    public abstract View onCreateChildView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    public abstract void onHeaderItemChanged(T item);

    @Nullable
    public abstract List<T> getSpinnerElemList();

    public abstract int getSpinnerDefaultPosition(List<T> list);

    public abstract SPINNER_ADAPTER onGenerateHeaderAdapter(List<T> items);

}
