package com.paulvarry.intra42.activities.users;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.paulvarry.intra42.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UsersAdvancedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UsersAdvancedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsersAdvancedFragment extends Fragment {

    Spinner spinnerCursus;
    Spinner spinnerCampus;

    UsersActivity activity;
    private OnFragmentInteractionListener mListener;

    public UsersAdvancedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UsersAdvancedFragment.
     */
    public static UsersAdvancedFragment newInstance() {
        return new UsersAdvancedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (UsersActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_users_advanced, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinnerCursus = (Spinner) view.findViewById(R.id.spinnerCursus);
        spinnerCampus = (Spinner) view.findViewById(R.id.spinnerCampus);

//        ArrayAdapter<String> adapterCursus = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, Cursus.getStrings(activity.app.allCursus));
//        adapterCursus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerCursus.setAdapter(adapterCursus);

//        ArrayAdapter<String> adapterCampus = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, Campus.getStrings(activity.app.allCampus));
//        adapterCampus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerCampus.setAdapter(adapterCampus);
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
