package com.paulvarry.intra42.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.paulvarry.intra42.ApiService;
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.Tools.Pagination;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BasicFragmentCallSpinner<T, ADAPTER extends BaseAdapter, SPINNER>
        extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, AbsListView.OnScrollListener, AdapterView.OnItemSelectedListener {

    private final static String SAVED_STATE_ID_TAG = "saved_state_tag";

    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout linearLayoutHeader;
    private TextView textView;
    private ListView listView;
    private Spinner spinner;

    private boolean flag_loading = true;
    private ADAPTER adapter;
    private int positionSelected;

    @Nullable
    private Call<List<T>> call;
    @Nullable
    private List<T> list;
    @Nullable
    private List<SPINNER> listSpinner;
    private Callback<List<T>> callback = new Callback<List<T>>() {

        @Override
        public void onResponse(Call<List<T>> call, Response<List<T>> response) {
            List<T> listTmp = response.body();
            if (list != null && listTmp != null)
                list.addAll(listTmp);
            else
                list = listTmp;
            setView();
            flag_loading = false;
        }

        @Override
        public void onFailure(Call<List<T>> call, Throwable t) {
            if (!call.isCanceled()) {
                Context c = getContext();
                if (c != null)
                    Toast.makeText(c, "Error", Toast.LENGTH_SHORT).show();
            }
            t.printStackTrace();
            if (BasicFragmentCallSpinner.this.isAdded())
                setView();
            flag_loading = false;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment__basic_spinner, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        linearLayoutHeader = (LinearLayout) view.findViewById(R.id.linearLayoutHeader);
        listView = (ListView) view.findViewById(R.id.listView);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        textView = (TextView) view.findViewById(R.id.textView);
        spinner = (Spinner) view.findViewById(R.id.spinner);

        swipeRefreshLayout.setOnRefreshListener(this);
        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(this);
        textView.setVisibility(View.GONE);

        listSpinner = getSpinnerElemList();
        if (listSpinner == null) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(R.string.initializing);
            new Thread(new Runnable() {
                @Override
                public void run() {

                    listSpinner = getSpinnerItems(((AppClass) getActivity().getApplication()).getApiService());

                    Activity a = getActivity();
                    if (a != null)
                        a.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setSpinner();
                                onRefresh();
                            }
                        });
                }
            }).start();
        } else {
            setSpinner();
            onRefresh();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (call != null)
            call.cancel();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (listSpinner != null && positionSelected != -1 && listSpinner.size() > positionSelected)
            outState.putInt(SAVED_STATE_ID_TAG, getSpinnerElemId(listSpinner.get(positionSelected)));
        super.onSaveInstanceState(outState);

        if (call != null)
            call.cancel();
    }

    private void setSpinner() {
        if (listSpinner != null && !listSpinner.isEmpty()) {
            List<String> tmp = new ArrayList<>();
            int idTagsNeedSelected;
            int positionSelected = 0;
            Bundle savedInstanceState = getArguments();

            if (savedInstanceState != null)
                idTagsNeedSelected = savedInstanceState.getInt(SAVED_STATE_ID_TAG);
            else
                idTagsNeedSelected = getSpinnerDefaultId(listSpinner);

            for (int i = 0; i < listSpinner.size(); i++) {
                tmp.add(getSpinnerElemName(listSpinner.get(i)));
                if (getSpinnerElemId(listSpinner.get(i)) == idTagsNeedSelected)
                    positionSelected = i;
            }
            ArrayAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, tmp);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);

            if (positionSelected != 0)
                spinner.setSelection(positionSelected);
        } else {

        }
    }

    public void setView() {
        if (list == null || list.isEmpty()) {
            listView.setAdapter(null);
            textView.setVisibility(View.VISIBLE);
        } else {
            if (adapter == null) {
                adapter = generateAdapter(list);
                listView.setAdapter(adapter);
            }
            adapter.notifyDataSetChanged();
            textView.setVisibility(View.GONE);
            flag_loading = false;
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
            if (!flag_loading && Pagination.canAdd(list)) {
                flag_loading = true;
                addItems();
            }
        }
    }

    private void addItems() {
        if (listSpinner != null) {

            ApiService apiService = ((AppClass) getActivity().getApplication()).getApiService();

            this.call = getCall(apiService, listSpinner.get(positionSelected), list);
        }

        if (call != null) {
            call.enqueue(callback);
        }
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p/>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (list != null && list.size() > position)
            onItemClick(list.get(position));
    }


    @Override
    public void onRefresh() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        list = null;
        adapter = null;

        ApiService apiService = ((AppClass) getActivity().getApplication()).getApiService();

        Call<List<T>> call = null;
        if (listSpinner != null) {
            call = getCall(apiService, listSpinner.get(positionSelected), list);
        }

        if (call != null) {
            this.call = call;
            call.enqueue(callback);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        positionSelected = i;
        onRefresh();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        positionSelected = -1;
    }


    @Nullable
    abstract public Call<List<T>> getCall(ApiService apiService, SPINNER spinner, @Nullable List<T> list);

    public abstract void onItemClick(T item);

    @Nullable
    public abstract ADAPTER generateAdapter(List<T> list);

    @Nullable
    public abstract List<SPINNER> getSpinnerElemList();

    /**
     * Run on a new Thread
     *
     * @param apiService The api.
     * @return list of items for spinner
     */
    @Nullable
    public abstract List<SPINNER> getSpinnerItems(ApiService apiService);

    public abstract int getSpinnerDefaultId(List<SPINNER> list);

    public abstract int getSpinnerElemId(SPINNER spinner);

    public abstract String getSpinnerElemName(SPINNER spinner);

}
