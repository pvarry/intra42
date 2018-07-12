package com.paulvarry.intra42.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.utils.Pagination;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public abstract class BasicFragmentCallGrid<T, ADAPTER extends BaseAdapter> extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, AbsListView.OnScrollListener, AdapterView.OnItemLongClickListener {

    private ConstraintLayout constraintOnError;
    private ConstraintLayout constraintLayoutLoading;
    private SwipeRefreshLayout swipeRefreshLayout;

    private TextView textViewError;
    private TextView textViewStatus;

    private GridView gridView;
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
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment__basic_grid_users, container, false);
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

        constraintLayoutLoading = view.findViewById(R.id.constraintLayoutLoading);
        constraintOnError = view.findViewById(R.id.constraintOnError);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        textViewError = view.findViewById(R.id.textViewError);
        textViewStatus = view.findViewById(R.id.textViewStatus);
        gridView = view.findViewById(R.id.gridView);

        swipeRefreshLayout.setOnRefreshListener(this);
        gridView.setOnItemClickListener(this);
        gridView.setOnItemLongClickListener(this);
        gridView.setOnScrollListener(this);

        setViewHide();
        constraintLayoutLoading.setVisibility(View.VISIBLE);
        onRefresh();
    }

    public void setView() {
        if (!isDetached()) {

            setViewHide();

            if (list == null || list.isEmpty()) {
                constraintOnError.setVisibility(View.VISIBLE);
                gridView.setAdapter(null);
                String message = getEmptyMessage();

                if (message != null && !message.isEmpty())
                    textViewError.setText(message);
            } else {
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                if (adapter == null) {
                    adapter = generateAdapter(list);
                    gridView.setAdapter(adapter);
                }
                adapter.notifyDataSetChanged();
                flag_loading = false;
            }
            swipeRefreshLayout.setRefreshing(false);
        }
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

        textViewStatus.setText(R.string.info_api_request_users_1_1);

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
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        return list.size() > position && onItemLongClick(list.get(position));
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

    void setViewHide() {
        constraintLayoutLoading.setVisibility(View.GONE);
        constraintOnError.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.GONE);
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
     * When a item on the list is clicked
     *
     * @param item The item clicked
     */
    public abstract boolean onItemLongClick(T item);

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
