package com.paulvarry.intra42.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.BaseRecyclerAdapter;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.utils.Pagination;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

// usage of Deprecated but is work in progress
@Deprecated
public abstract class BasicFragmentCallRecycler<T, ADAPTER extends RecyclerView.Adapter>
        extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    protected FloatingActionButton fabBasicFragmentCall;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView textView;
    private RecyclerView recyclerView;
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
            swipeRefreshLayout.setRefreshing(false);
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
        return inflater.inflate(R.layout.fragment__basic_recycler, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        textView = view.findViewById(R.id.textView);
        fabBasicFragmentCall = view.findViewById(R.id.fabBasicFragmentCall);

        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        listView.setOnItemClickListener(this);
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//
//                if (!recyclerView.canScrollVertically(1) && !flag_loading && Pagination.canAdd(list)) {
//                    flag_loading = true;
//                    addItems();
//                }
//            }
//        });
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

    private void addItems() {

        if (isDetached())
            return;

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        Activity a = getActivity();
        if (a == null)
            return;
        ApiService apiService = ((AppClass) a.getApplication()).getApiService();

        Call<List<T>> call = getCall(apiService, list);

        if (call != null) {
            this.call = call;
            call.enqueue(callback);
        }
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

    public void setView() {
        if (!isAdded())
            return;
        if (list == null || list.isEmpty()) {
            recyclerView.setAdapter(null);
            textView.setVisibility(View.VISIBLE);
            String message = getEmptyMessage();
            if (message != null && !message.isEmpty())
                textView.setText(message);
            textView.setText(getString(R.string.info_nothing_to_show));
        } else {
            if (adapter == null) {
                adapter = generateAdapter(list);

                if (adapter instanceof BaseRecyclerAdapter)
                    ((BaseRecyclerAdapter) adapter).setInfiniteScrollListener(new BaseRecyclerAdapter.InfiniteScrollListener() {
                        @Override
                        public boolean requestMoreItem() {
                            if (!flag_loading && Pagination.canAdd(list)) {
                                flag_loading = true;
                                addItems();
                                return true;
                            }
                            return false;
                        }
                    });

                recyclerView.setAdapter(adapter);
            }
            adapter.notifyDataSetChanged();
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
