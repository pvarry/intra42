package com.paulvarry.intra42.tab.home;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.paulvarry.intra42.Adapter.ListAdapterEvents;
import com.paulvarry.intra42.ApiService;
import com.paulvarry.intra42.BottomSheet.BottomSheetEventDialogFragment;
import com.paulvarry.intra42.Tools.ApiParams;
import com.paulvarry.intra42.Tools.DateTool;
import com.paulvarry.intra42.Tools.Pagination;
import com.paulvarry.intra42.api.Events;
import com.paulvarry.intra42.ui.BasicFragmentCall;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeEventsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeEventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeEventsFragment extends BasicFragmentCall<Events, ListAdapterEvents> {

    private OnFragmentInteractionListener mListener;

    public HomeEventsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeEventsFragment.
     */
    public static HomeEventsFragment newInstance() {
        return new HomeEventsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    @Nullable
    @Override
    public Call<List<Events>> getCall(ApiService apiService, @Nullable List<Events> list) {
        int cursus = ApiParams.getCursus(getContext());
        int campus = ApiParams.getCampus(getContext());

        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, 2);
        String date = DateTool.getNowUTC() + "," + DateTool.getUTC(c.getTime());

        if (cursus != -1 && cursus != 0 && campus != -1 && campus != 0)
            return apiService.getEvent(campus, cursus, date, Pagination.getPage(list));
        else if (cursus != -1 && cursus != 0)
            return apiService.getEventCursus(cursus, date, Pagination.getPage(list));
        else if (campus != -1 && campus != 0)
            return apiService.getEventCampus(campus, date, Pagination.getPage(list));
        else
            return apiService.getEvent(date, Pagination.getPage(list));
    }

    @Override
    public void onItemClick(Events item) {
        BottomSheetEventDialogFragment bottomSheetDialogFragment = BottomSheetEventDialogFragment.newInstance(item);
        bottomSheetDialogFragment.show(getActivity().getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
    }

    @Override
    public ListAdapterEvents generateAdapter(List<Events> list) {
        return new ListAdapterEvents(getContext(), list);
    }

    @Override
    public String getEmptyMessage() {
        return null;
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
