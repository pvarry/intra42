package com.paulvarry.intra42.activities.intro;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.utils.AppSettings;
import com.paulvarry.intra42.utils.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link IntroCalendarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link IntroCalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IntroCalendarFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    private Context context;

    private Switch switchEnableCalendar;
    private Button buttonAskPermission;

    public IntroCalendarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment IntroCelendarFragment.
     */
    public static IntroCalendarFragment newInstance() {
        return new IntroCalendarFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_intro_celendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonAskPermission = view.findViewById(R.id.buttonAskPermission);
        switchEnableCalendar = view.findViewById(R.id.switchEnableCalendar);

        buttonAskPermission.setOnClickListener(this);
        switchEnableCalendar.setOnCheckedChangeListener(this);

        permissionFinished();
    }

    void permissionFinished() {
        if (switchEnableCalendar == null || buttonAskPermission == null)
            return;
        switchEnableCalendar.setVisibility(View.GONE);
        buttonAskPermission.setVisibility(View.GONE);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            switchEnableCalendar.setVisibility(View.VISIBLE);
            switchEnableCalendar.setChecked(AppSettings.Notifications.getCalendarSyncEnable(context));
        } else {
            buttonAskPermission.setVisibility(View.VISIBLE);
        }
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked)
            Calendar.setEnableCalendarWithAutoSelect(context, true);
        else
            AppSettings.Notifications.setCalendarSyncEnable(context, false);
    }

    @Override
    public void onClick(View v) {
        if (v == buttonAskPermission)
            mListener.askCalendarPermission();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);

        void askCalendarPermission();
    }
}
