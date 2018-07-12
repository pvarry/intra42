package com.paulvarry.intra42.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.paulvarry.intra42.R;

import java.util.List;

public abstract class BasicFragmentGrid<T, ADAPTER extends BaseAdapter>
        extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView textView;
    private GridView listView;
    private ADAPTER adapter;

    private List<T> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment__basic_grid_users, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = view.findViewById(R.id.gridView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        textView = view.findViewById(R.id.textView);

        swipeRefreshLayout.setOnRefreshListener(this);
        listView.setOnItemClickListener(this);
        textView.setVisibility(View.GONE);
        onRefresh();
    }

    public void setView() {
        if (list == null || list.isEmpty()) {
            listView.setAdapter(null);
            textView.setVisibility(View.VISIBLE);
            textView.setText(R.string.info_nothing_to_show);
        } else {
            if (adapter == null) {
                adapter = generateAdapter(list);
                listView.setAdapter(adapter);
            }
            adapter.notifyDataSetChanged();
            textView.setVisibility(View.GONE);
        }
        swipeRefreshLayout.setRefreshing(false);
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
        if (list.size() > position)
            onItemClick(list.get(position));
    }


    @Override
    public void onRefresh() {
//        swipeRefreshLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                swipeRefreshLayout.setRefreshing(true);
//            }
//        });

        list = getData();
        adapter = null;

        setView();
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * When a item on the list is clicked
     */
    public abstract List<T> getData();

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

}
