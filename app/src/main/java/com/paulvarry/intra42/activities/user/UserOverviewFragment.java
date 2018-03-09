package com.paulvarry.intra42.activities.user;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.ImageViewerActivity;
import com.paulvarry.intra42.activities.LocationHistoryActivity;
import com.paulvarry.intra42.adapters.SpinnerAdapterCursusAccent;
import com.paulvarry.intra42.api.ApiService42Tools;
import com.paulvarry.intra42.api.model.Campus;
import com.paulvarry.intra42.api.model.CursusUsers;
import com.paulvarry.intra42.api.model.Tags;
import com.paulvarry.intra42.api.model.Users;
import com.paulvarry.intra42.api.tools42.Friends;
import com.paulvarry.intra42.ui.TagSpanGenerator;
import com.paulvarry.intra42.utils.DateTool;
import com.paulvarry.intra42.utils.Share;
import com.paulvarry.intra42.utils.Tag;
import com.paulvarry.intra42.utils.Tools;
import com.paulvarry.intra42.utils.UserImage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserOverviewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserOverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserOverviewFragment
        extends Fragment
        implements View.OnClickListener, AdapterView.OnItemSelectedListener, SwipeRefreshLayout.OnRefreshListener, View.OnLongClickListener {

    final static private String STATE_SELECTED_CURSUS = "selected_cursus";
    final static private String STATE_FRIEND = "isFriend";

    @Nullable
    private UserActivity activity;
    private AppClass app;
    private Users user;
    private Boolean isFriend;
    private OnFragmentInteractionListener mListener;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ViewGroup layoutPhone;
    private View separatorPhone;
    private ImageButton imageButtonSMS;
    private ViewGroup layoutMail;
    private ViewGroup layoutLocation;
    private ImageView imageViewProfile;
    private Button buttonFriend;
    private ProgressBar progressBarFriends;
    private TextView textViewName;
    private TextView textViewMobile;
    private TextView textViewMail;
    private TextView textViewPosition;
    private TextView textViewWallet;
    private TextView textViewCorrectionPoints;
    private TextView textViewPiscine;
    private Group groupPiscine;
    private Spinner spinnerCursus;
    private LinearLayout linearLayoutGrade;
    private View viewSeparatorGrade;
    private TextView textViewGrade;
    private TextView textViewLvl;
    private ProgressBar progressBarLevel;
    private TextView textViewNoCursusAvailable;
    private LinearLayout linearLayoutCursus;
    private TextView textViewCursusDate;

    private Callback<Friends> checkFriend = new Callback<Friends>() {
        @Override
        public void onResponse(Call<Friends> call, Response<Friends> response) {
            isFriend = null;
            if (Tools.apiIsSuccessfulNoThrow(response)) {
                isFriend = true;
                setButtonFriends(1);
            } else if (response.code() == 404) {
                isFriend = false;
                setButtonFriends(1);
            } else
                setButtonFriends(-1);
        }

        @Override
        public void onFailure(Call<Friends> call, Throwable t) {
            isFriend = null;
            setButtonFriends(-1);
        }
    };
    private Callback<Friends> addFriend = new Callback<Friends>() {
        @Override
        public void onResponse(Call<Friends> call, Response<Friends> response) {
            if (Tools.apiIsSuccessfulNoThrow(response) || response.code() == 404) {
                isFriend = true;
                setButtonFriends(1);
            } else {
                setButtonFriends(-1);
                if (response.code() == 102)
                    Toast.makeText(activity, R.string.friends_info_api_data_processing, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onFailure(Call<Friends> call, Throwable t) {
            isFriend = null;
            setButtonFriends(-1);
        }
    };
    private Callback<Void> removeFriend = new Callback<Void>() {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            if (Tools.apiIsSuccessfulNoThrow(response)) {
                isFriend = false;
                setButtonFriends(1);
            } else
                setButtonFriends(-1);
        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {
            isFriend = null;
            setButtonFriends(-1);
        }
    };

    public UserOverviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserOverviewFragment.
     */
    public static UserOverviewFragment newInstance() {

        return new UserOverviewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (UserActivity) getActivity();
        if (activity != null) {
            user = activity.user;
            app = (AppClass) activity.getApplication();
        }

        if (savedInstanceState != null) {
            if (spinnerCursus != null)
                spinnerCursus.setSelection(savedInstanceState.getInt(STATE_SELECTED_CURSUS), false);
            if (savedInstanceState.containsKey(STATE_FRIEND))
                isFriend = savedInstanceState.getBoolean(STATE_FRIEND);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_overview, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        layoutPhone = view.findViewById(R.id.layoutPhone);
        separatorPhone = view.findViewById(R.id.separatorPhone);
        imageButtonSMS = view.findViewById(R.id.imageButtonSMS);
        layoutMail = view.findViewById(R.id.layoutMail);
        layoutLocation = view.findViewById(R.id.layoutLocation);
        imageViewProfile = view.findViewById(R.id.imageViewProfile);
        buttonFriend = view.findViewById(R.id.buttonFriend);
        progressBarFriends = view.findViewById(R.id.progressBarFriends);

        textViewName = view.findViewById(R.id.textViewName);
        textViewMobile = view.findViewById(R.id.textViewMobile);
        textViewMail = view.findViewById(R.id.textViewMail);
        textViewPosition = view.findViewById(R.id.textViewPosition);
        textViewWallet = view.findViewById(R.id.textViewWallet);
        textViewCorrectionPoints = view.findViewById(R.id.textViewCorrectionPoints);
        textViewPiscine = view.findViewById(R.id.textViewPiscine);
        groupPiscine = view.findViewById(R.id.groupPiscine);

        linearLayoutCursus = view.findViewById(R.id.linearLayoutCursus);
        textViewNoCursusAvailable = view.findViewById(R.id.textViewNoCursusAvailable);
        spinnerCursus = view.findViewById(R.id.spinnerCursus);
        linearLayoutGrade = view.findViewById(R.id.linearLayoutGrade);
        viewSeparatorGrade = view.findViewById(R.id.viewSeparatorGrade);
        textViewGrade = view.findViewById(R.id.textViewGrade);
        textViewLvl = view.findViewById(R.id.textViewLvl);
        progressBarLevel = view.findViewById(R.id.progressBarLevel);
        textViewCursusDate = view.findViewById(R.id.textViewCursusDate);

        setView();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setView();
    }

    void setView() {

        if (activity == null || activity.user == null || isDetached() || !isAdded())
            return;
        user = activity.user;

        swipeRefreshLayout.setOnRefreshListener(this);

        if (user.equals(app.me)) {
            progressBarFriends.setVisibility(View.GONE);
            buttonFriend.setVisibility(View.GONE);
        } else if (isFriend == null) {
            setButtonFriends(0);
            ApiService42Tools api = app.getApiService42Tools();
            api.getFriend(user.id).enqueue(checkFriend);
        } else
            setButtonFriends(1);

        TagSpanGenerator span = new TagSpanGenerator.Builder(getContext())
                .setTextSize(textViewName.getTextSize())
                .build();
        span.addText(user.displayName + " - " + user.login);
        if (user.groups != null && !user.groups.isEmpty()) {
            span.addText(" ");
            for (Tags tag : user.groups) {
                span.addTag(tag.name, Tag.getUsersTagColor(tag));
            }
        }
        textViewName.setText(span.getString());


        if (user.phone == null || user.phone.isEmpty()) {
            layoutPhone.setVisibility(View.GONE);
            separatorPhone.setVisibility(View.GONE);
        } else {
            layoutPhone.setVisibility(View.VISIBLE);
            separatorPhone.setVisibility(View.VISIBLE);
            textViewMobile.setText(user.phone);
        }
        textViewMail.setText(user.email);

        StringBuilder strLocation = new StringBuilder();
        if (user.location != null) {
            strLocation.append(user.location);
        } else
            strLocation.append(getString(R.string.user_unavailable));
        if (user.campus != null && !user.campus.isEmpty()) {
            strLocation.append(" - ");
            String sep = "";
            for (Campus c : user.campus) {
                strLocation.append(sep).append(c.name);
                sep = " | ";
            }
        }
        textViewPosition.setText(strLocation);
        String wallet = String.valueOf(user.wallet) + " ₳";
        textViewWallet.setText(wallet);
        textViewCorrectionPoints.setText(String.valueOf(user.correction_point));

        String pool = "";
        if (user.pool_month != null && !user.pool_month.isEmpty())
            pool += user.pool_month.substring(0, 1).toUpperCase() + user.pool_month.substring(1) + " - ";
        if (user.pool_year != null)
            pool += user.pool_year;
        if (user.pool_year != null || user.pool_month != null) {
            groupPiscine.setVisibility(View.VISIBLE);
            textViewPiscine.setText(pool);
        } else
            groupPiscine.setVisibility(View.GONE);

        imageViewProfile.setOnClickListener(this);
        buttonFriend.setOnClickListener(this);

        layoutPhone.setOnClickListener(this);
        imageButtonSMS.setOnClickListener(this);
        layoutMail.setOnClickListener(this);
        layoutLocation.setOnClickListener(this);

        layoutPhone.setOnLongClickListener(this);
        imageButtonSMS.setOnLongClickListener(this);
        layoutMail.setOnLongClickListener(this);
        layoutLocation.setOnLongClickListener(this);

        CursusUsers selected = user.getCursusUsersToDisplay(getContext());
        if (selected != null && user.cursusUsers != null) {
            linearLayoutCursus.setVisibility(View.VISIBLE);
            textViewNoCursusAvailable.setVisibility(View.GONE);
            SpinnerAdapterCursusAccent adapterUserCursus = new SpinnerAdapterCursusAccent(getActivity(), user.cursusUsers);
            spinnerCursus.setAdapter(adapterUserCursus);
            spinnerCursus.setOnItemSelectedListener(this);

            for (int i = 0; i < user.cursusUsers.size(); i++) {
                if (user.cursusUsers.get(i).cursusId == selected.cursusId)
                    spinnerCursus.setSelection(i, false);
            }
        } else {
            linearLayoutCursus.setVisibility(View.GONE);
            textViewNoCursusAvailable.setVisibility(View.VISIBLE);
        }

        UserImage.setImage(getContext(), user, imageViewProfile);

        swipeRefreshLayout.setRefreshing(false);
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (spinnerCursus != null)
            outState.putInt(STATE_SELECTED_CURSUS, spinnerCursus.getSelectedItemPosition());
        if (isFriend != null)
            outState.putBoolean(STATE_FRIEND, isFriend);
    }

    @Override
    public void onClick(View v) {
        if (v == layoutPhone) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + user.phone));
            startActivity(intent);
        } else if (v == imageButtonSMS) {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setData(Uri.parse("sms:" + user.phone));
            startActivity(sendIntent);
        } else if (v == layoutMail) {
            Intent testIntent = new Intent(Intent.ACTION_VIEW);
            Uri data = Uri.parse("mailto:?to=" + user.email);
            testIntent.setData(data);
            startActivity(testIntent);
        } else if (v == layoutLocation) {
            LocationHistoryActivity.openItWithUser(getContext(), user);
        } else if (v == buttonFriend) {
            ApiService42Tools api = app.getApiService42Tools();

            setButtonFriends(0);
            if (isFriend == null)
                return;
            if (!isFriend) { //add
                Call<Friends> friendsCall = api.addFriend(user.id);
                friendsCall.enqueue(addFriend);
            } else {
                api.deleteFriend(user.id).enqueue(removeFriend);
            }
        } else if (v == imageViewProfile) {
            ImageViewerActivity.openIt(getContext(), user.login);
        }
    }

    /**
     * 0 -> loading;
     * <p>
     * 1 -> normal state;
     * <p>
     * -1 -> error on loading;
     *
     * @param state Current state;
     */
    void setButtonFriends(int state) {

        if (state == 0) {
            progressBarFriends.setVisibility(View.VISIBLE);
            buttonFriend.setActivated(false);
        } else {
            progressBarFriends.setVisibility(View.GONE);
            buttonFriend.setActivated(true);
        }

        if (state == 1 && isFriend != null) {
            buttonFriend.setActivated(true);
            if (!isFriend) {
                buttonFriend.setText(R.string.user_profile_add_to_friends);
                TypedValue typedValue = new TypedValue();
                Context context = getContext();
                if (context == null)
                    return;
                Resources.Theme theme = context.getTheme();
                theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
                @ColorInt int color = typedValue.data;
                buttonFriend.setTextColor(color);

            } else {
                buttonFriend.setText(R.string.user_profile_remove_from_friends);
                buttonFriend.setTextColor(getResources().getColor(R.color.colorGray));
            }
        } else if (state == -1 || isFriend == null)
            buttonFriend.setTextColor(Color.argb(200, 150, 150, 150));
    }

    /**
     * <p>Callback method to be invoked when an item in this view has been
     * selected. This callback is invoked only when the newly selected
     * position is different from the previously selected position or if
     * there was no selected item.</p>
     * <p/>
     * Impelmenters can call getItemAtPosition(position) if they need to access the
     * data associated with the selected item.
     *
     * @param parent   The AdapterView where the selection happened
     * @param view     The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id       The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (user.cursusUsers == null)
            return;
        CursusUsers userCursus = user.cursusUsers.get(position);
        if (activity != null) {
            activity.selectedCursus = userCursus;
        }

        if (userCursus.grade == null) {
            linearLayoutGrade.setVisibility(View.GONE);
            viewSeparatorGrade.setVisibility(View.GONE);
        } else {
            linearLayoutGrade.setVisibility(View.VISIBLE);
            viewSeparatorGrade.setVisibility(View.VISIBLE);
            textViewGrade.setText(userCursus.grade);
        }
        textViewLvl.setText(String.valueOf(userCursus.level));
        progressBarLevel.setProgress((int) (userCursus.level / 21.0 * 100.0));

        String dateInfo;

        if (userCursus.begin_at == null && userCursus.end_at == null)
            dateInfo = getString(R.string.user_overview_cursus_date_not_start_not_finised);
        else {
            if (userCursus.begin_at != null)
                dateInfo = DateTool.getDateLong(userCursus.begin_at);
            else
                dateInfo = getString(R.string.user_overview_cursus_date_not_start);

            dateInfo += " • ";

            if (userCursus.end_at != null)
                dateInfo += DateTool.getDateLong(userCursus.end_at);
            else
                dateInfo += getString(R.string.user_overview_cursus_date_not_finished);
        }
        textViewCursusDate.setText(dateInfo);
    }

    /**
     * Callback method to be invoked when the selection disappears from this
     * view. The selection can disappear for instance when touch is activated
     * or when the adapter becomes empty.
     *
     * @param parent The AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {
        if (activity != null)
            activity.refresh(new Runnable() {
                @Override
                public void run() {
                    setView();
                }
            });
    }

    @Override
    public boolean onLongClick(View v) {

        if (v == layoutPhone)
            dialogCopyOrShare(user.phone);
        else if (v == imageButtonSMS)
            dialogCopyOrShare(user.phone);
        else if (v == layoutMail)
            dialogCopyOrShare(user.email);
        else if (v == layoutLocation)
            dialogCopyOrShare(user.location);
        else
            return false;
        return true;
    }

    void dialogCopyOrShare(final String string) {
        final Context context = getContext();
        if (context == null)
            return;
        CharSequence action[] = new CharSequence[]{
                context.getString(R.string.copy),
                context.getString(R.string.navigation_share)};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(string);
        builder.setItems(action, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0)
                    Share.copyString(context, string);
                else
                    Share.shareString(activity, string);
            }
        });
        builder.show();
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
