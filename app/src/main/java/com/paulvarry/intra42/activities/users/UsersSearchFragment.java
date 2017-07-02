package com.paulvarry.intra42.activities.users;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.user.UserActivity;
import com.paulvarry.intra42.adapters.GridAdapterUsers;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.model.Users;
import com.paulvarry.intra42.api.model.UsersLTE;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UsersSearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UsersSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsersSearchFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    AppClass app;
    Button buttonSearch;
    TextView textViewSearching;
    EditText editTextLogin;
    GridView gridViewUser;
    List<UsersLTE> users;

    private Call<Users> callUser;
    private Call<List<UsersLTE>> callSearch;
    private Callback<List<UsersLTE>> callbackSearch = new Callback<List<UsersLTE>>() {

        @Override
        public void onResponse(Call<List<UsersLTE>> call, retrofit2.Response<List<UsersLTE>> response) {
            users = response.body();
            if (users != null && users.size() == 1) {
                UserActivity.openIt(getContext(), users.get(0));
            } else if (users != null && users.size() > 1) {
                final GridAdapterUsers adapter = new GridAdapterUsers(getActivity(), users);
                gridViewUser.setAdapter(adapter);
            } else
                Toast.makeText(getContext(), R.string.nothing_found, Toast.LENGTH_SHORT).show();

            buttonSearch.setVisibility(View.VISIBLE);
            textViewSearching.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onFailure(Call<List<UsersLTE>> call, Throwable t) {
            if (!call.isCanceled())
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            t.printStackTrace();

            gridViewUser.setAdapter(null);
            buttonSearch.setVisibility(View.VISIBLE);
            textViewSearching.setVisibility(View.INVISIBLE);
        }
    };
    private Callback<Users> callbackUser = new Callback<Users>() {

        @Override
        public void onResponse(Call<Users> call, retrofit2.Response<Users> response) {
            Users user = response.body();
            gridViewUser.setAdapter(null);
            buttonSearch.setVisibility(View.VISIBLE);
            textViewSearching.setVisibility(View.INVISIBLE);
            if (user != null)
                UserActivity.openIt(app, user);
            else
                search();
        }

        @Override
        public void onFailure(Call<Users> call, Throwable t) {
            if (!call.isCanceled())
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            t.printStackTrace();
            search();
        }
    };
    private OnFragmentInteractionListener mListener;

    public UsersSearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UsersSearchFragment.
     */
    public static UsersSearchFragment newInstance() {
        return new UsersSearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (AppClass) getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_users_search, container, false);

        buttonSearch = rootView.findViewById(R.id.buttonSearch);
        textViewSearching = rootView.findViewById(R.id.textViewSearching);
        editTextLogin = rootView.findViewById(R.id.editTextLogin);
        gridViewUser = rootView.findViewById(R.id.gridViewUser);

        buttonSearch.setOnClickListener(this);
        gridViewUser.setOnItemClickListener(this);

        return rootView;
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

        if (callUser != null)
            callUser.cancel();

        if (callSearch != null)
            callSearch.cancel();
    }

    @Override
    public void onClick(final View v) {
        if (v == buttonSearch) {
            buttonSearch.setVisibility(View.INVISIBLE);
            textViewSearching.setVisibility(View.VISIBLE);
            gridViewUser.setAdapter(null);

            ApiService s = app.getApiService();
            callUser = s.getUser(editTextLogin.getText().toString());
            callUser.enqueue(callbackUser);
        }
    }

    private void search() {
        ApiService s = app.getApiService();
        s.getUsersSearchLogin(editTextLogin.getText().toString());
        callSearch = s.getUsersSearchLogin(editTextLogin.getText().toString());
        callSearch.enqueue(callbackSearch);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        UserActivity.openIt(getContext(), users.get(position));
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
