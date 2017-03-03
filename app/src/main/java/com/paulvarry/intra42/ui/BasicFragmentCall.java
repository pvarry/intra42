package com.paulvarry.intra42.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.Tools.Pagination;
import com.paulvarry.intra42.api.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BasicFragmentCall<T, ADAPTER extends BaseAdapter>
        extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    protected FloatingActionButton fabBasicFragmentCall;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView textView;
    private ListView listView;
    private boolean flag_loading = false;
    private ADAPTER adapter;

    @Nullable
    private Call<List<T>> call;
    private List<T> list;
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
            if (!call.isCanceled())
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            t.printStackTrace();
            setView();
            flag_loading = false;
            swipeRefreshLayout.setRefreshing(false);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment__basic, container, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (call != null)
            call.cancel();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = (ListView) view.findViewById(R.id.listView);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        textView = (TextView) view.findViewById(R.id.textView);
        fabBasicFragmentCall = (FloatingActionButton) view.findViewById(R.id.fabBasicFragmentCall);

        swipeRefreshLayout.setOnRefreshListener(this);
        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(this);
        textView.setVisibility(View.GONE);
        fabBasicFragmentCall.setVisibility(View.GONE);
        onRefresh();
    }

    public void setView() {
        if (!isAdded())
            return;
        if (list == null || list.isEmpty()) {
            listView.setAdapter(null);
            textView.setVisibility(View.VISIBLE);
            String message = getEmptyMessage();
            if (message != null && !message.isEmpty())
                textView.setText(message);
            else if (isAdded())
                textView.setText(getString(R.string.nothing_to_show));
            else
                textView.setText("Nothing to show");
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

        ApiService apiService = ((AppClass) getActivity().getApplication()).getApiService();

        Call<List<T>> call = getCall(apiService, list);

        if (call != null) {
            this.call = call;
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
        flag_loading = true;
        if (call != null)
            call.cancel();
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        list = null;
        adapter = null;

        addItems();
    }

    /**
     * Return a Call of retrofit2 can be enqueue()
     *
     * @param apiService Service
     * @param list       The current item on list (for pagination)
     * @return The Call
     */
    @Nullable
    public abstract Call<List<T>> getCall(ApiService apiService, @Nullable List<T> list);

    /**
     * When a item on the list is clicked
     *
     * @param item The item clicked
     */
    public abstract void onItemClick(T item);

    /**
     * Generate a new adapter for the list, called on create fragment and after refresh.
     *
     * @param list The list for the ListViewAdapter
     * @return A Adapter.
     */
    public abstract ADAPTER generateAdapter(List<T> list);

    /**
     * Get the message displayed when list view is empty
     *
     * @return The Message.
     */
    public abstract String getEmptyMessage();

}
