package com.paulvarry.intra42.activities.home;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.ListAdapterSlotsGroup;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.model.Slots;
import com.paulvarry.intra42.bottomSheet.BottomSheetSlotsDialogFragment;
import com.paulvarry.intra42.ui.BasicFragmentCall;
import com.paulvarry.intra42.utils.Pagination;

import java.util.List;

import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeSlotsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeSlotsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeSlotsFragment extends BasicFragmentCall<Slots, ListAdapterSlotsGroup> implements View.OnClickListener {

    HomeActivity activity;
    FloatingActionButton fabNew;

    Thread thread;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_slots, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        fabNew = view.findViewById(R.id.fabNew);
        fabNew.setOnClickListener(this);
        super.onViewCreated(view, savedInstanceState);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
        thread.interrupt();
    }


    @Nullable
    @Override
    public Call<List<Slots>> getCall(ApiService apiService, @Nullable List<Slots> list) {
        return apiService.getSlotsMe(Pagination.getPage(list));
    }

    @Override
    public void onItemClick(Slots item) {

    }

    @Override
    public ListAdapterSlotsGroup generateAdapter(List<Slots> list) {
        return new ListAdapterSlotsGroup(this, list);
    }

    @Override
    public String getEmptyMessage() {
        return null;
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
