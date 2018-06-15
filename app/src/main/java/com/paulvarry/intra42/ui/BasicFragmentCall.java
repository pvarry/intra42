package com.paulvarry.intra42.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.utils.Tools;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BasicFragmentCall<T, ADAPTER extends BaseAdapter>
        extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    protected FloatingActionButton fabBasicFragmentCall;
    protected List<T> list;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView textView;
    private ListView listView;
    private boolean flag_loading = false;
    private Integer maxPage = null;
    private int lastPage = 0;
    private ADAPTER adapter;
    @Nullable
    private Call<List<T>> call;
    private Callback<List<T>> callback = new Callback<List<T>>() {

        @Override
        public void onResponse(Call<List<T>> call, Response<List<T>> response) {
            List<T> listTmp = response.body();

            flag_loading = false;
            swipeRefreshLayout.setRefreshing(false);

            if (Tools.apiIsSuccessfulNoThrow(response)) {
                maxPage = (int) Math.ceil(Double.parseDouble(response.headers().get("X-Total")) / Double.parseDouble(response.headers().get("X-Per-Page")));
                lastPage = Integer.parseInt(response.headers().get("X-Page"));
                if (list != null && listTmp != null)
                    list.addAll(listTmp);
                else
                    list = listTmp;
                setView();
            }
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment__basic, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = view.findViewById(R.id.listView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        textView = view.findViewById(R.id.textView);
        fabBasicFragmentCall = view.findViewById(R.id.fabBasicFragmentCall);

        swipeRefreshLayout.setOnRefreshListener(this);
        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(this);
        textView.setVisibility(View.GONE);
        onRefresh();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (call != null)
            call.cancel();
    }

    @Override
    public void onRefresh() {
        flag_loading = true;
        if (call != null)
            call.cancel();
        swipeRefreshLayout.setRefreshing(true);

        maxPage = 0; // set max page to avoid max useless call
        lastPage = 0;
        list = null;
        adapter = null;

        addItems();
    }

    private void addItems() {

        if (isDetached())
            return;

        Activity a = getActivity();
        if (a == null)
            return;
        ApiService apiService = ((AppClass) a.getApplication()).getApiService();

        Call<List<T>> call = getCall(apiService, lastPage + 1);

        if (call != null) {
            this.call = call;
            call.enqueue(callback);
        } else {
            setView();
            flag_loading = false;
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * Return a Call of retrofit2 can be enqueue()
     *
     * @param apiService Service
     * @param page       The current page
     * @return The Call
     */
    @Nullable
    public abstract Call<List<T>> getCall(ApiService apiService, int page);

    public void setView() {
        if (!isAdded())
            return;
        if (list != null && adapter == null) {
            adapter = generateAdapter(list);
            listView.setAdapter(adapter);
        }

        if (adapter == null) {
            listView.setAdapter(null);
            listView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            String message = getEmptyMessage();
            if (message != null && !message.isEmpty())
                textView.setText(message);
            else if (isAdded())
                textView.setText(getString(R.string.info_nothing_to_show));
        } else {
            adapter.notifyDataSetChanged();
            listView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
            flag_loading = false;
        }

        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Get the message displayed when list view is empty
     *
     * @return The Message.
     */
    public abstract String getEmptyMessage();

    /**
     * Generate a new adapter for the list, called on create fragment and after refresh.
     *
     * @param list The list for the ListViewAdapter
     * @return A Adapter.
     */
    public abstract ADAPTER generateAdapter(List<T> list);

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
            if (!flag_loading && (maxPage == null || lastPage < maxPage)) {
                flag_loading = true;
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                    }
                });
                addItems();
            }
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

    /**
     * When a item on the list is clicked
     *
     * @param item The item clicked
     */
    public abstract void onItemClick(T item);

}
