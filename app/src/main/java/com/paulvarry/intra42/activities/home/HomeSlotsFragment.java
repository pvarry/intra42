package com.paulvarry.intra42.activities.home;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.ListAdapterSlotsGroup;
import com.paulvarry.intra42.api.model.Slots;
import com.paulvarry.intra42.bottomSheet.BottomSheetSlotsDialogFragment;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeSlotsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeSlotsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeSlotsFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private HomeActivity activity;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private TextView textViewLoading;
    private FloatingActionButton fabNew;

    private List<Slots> list;

    private OnFragmentInteractionListener mListener;

    public HomeSlotsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeSlotsFragment.
     */
    public static HomeSlotsFragment newInstance() {
        return new HomeSlotsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (HomeActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_slots, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        fabNew = view.findViewById(R.id.fabNew);
        fabNew.setOnClickListener(this);
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        listView = view.findViewById(R.id.listView);
        textViewLoading = view.findViewById(R.id.textViewLoading);

        textViewLoading.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(true);

        onRefresh();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void onClick(View v) {
        if (v == fabNew) {
            BottomSheetSlotsDialogFragment bottomSheetDialogFragment = BottomSheetSlotsDialogFragment.newInstance();
            bottomSheetDialogFragment.show(activity.getSupportFragmentManager(), bottomSheetDialogFragment.getTag());

            bottomSheetDialogFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    onRefresh();
                }
            });
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                list = Slots.getAll(activity.app.getApiService());

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        if (list == null || list.isEmpty()) {
                            listView.setVisibility(View.GONE);
                            textViewLoading.setVisibility(View.VISIBLE);
                            textViewLoading.setText(R.string.info_nothing_to_show);
                        } else {
                            listView.setVisibility(View.VISIBLE);
                            textViewLoading.setVisibility(View.GONE);
                            listView.setAdapter(new ListAdapterSlotsGroup(HomeSlotsFragment.this, list));
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
