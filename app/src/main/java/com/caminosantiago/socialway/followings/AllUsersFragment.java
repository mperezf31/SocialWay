package com.caminosantiago.socialway.followings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.baoyz.widget.PullRefreshLayout;
import com.caminosantiago.socialway.BuildConfig;
import com.caminosantiago.socialway.MyApiEndpointInterface;
import com.caminosantiago.socialway.R;
import com.caminosantiago.socialway.Utils;
import com.caminosantiago.socialway.followings.followers.AdapterFollowers;
import com.caminosantiago.socialway.model.User;
import com.caminosantiago.socialway.model.query.ListFollowings;
import com.caminosantiago.socialway.user.UserActivity;

import java.util.Locale;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;


public class AllUsersFragment extends Fragment {
    static AllUsersFragment fragment;
    Activity activity;
    ListView listView;
    View view;
    AdapterFollowers mAdapter;
    PullRefreshLayout refreshLayout;
    ListFollowings listFollowings;
    EditText editsearch;
    private ProgressBar pbMain;

    public static AllUsersFragment newInstance() {
        fragment = new AllUsersFragment();
        return fragment;
    }

    public AllUsersFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_all_users, container, false);
        pbMain = (ProgressBar) getActivity().findViewById(R.id.pbMain);
        listView = (ListView) view.findViewById(R.id.listView2);
        refreshLayout = (PullRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                executeTaskGetAllUsers();
            }
        });
        editsearch = (EditText) view.findViewById(R.id.search);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gotoUser((User) parent.getAdapter().getItem(position));
            }
        });

        executeTaskGetAllUsers();
        return view;
    }


    public void executeTaskGetAllUsers() {
        if (!refreshLayout.isActivated()) {
            pbMain.setVisibility(View.VISIBLE);
        }

        final Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).addConverterFactory(
                GsonConverterFactory.create()).build();
        MyApiEndpointInterface apiService = retrofit.create(MyApiEndpointInterface.class);
        Call<ListFollowings> call = apiService.getAllUsers(Utils.getUserID(activity));
        call.enqueue(new Callback<ListFollowings>() {
            @Override
            public void onResponse(Response<ListFollowings> response, Retrofit retrofit) {
                refreshLayout.setRefreshing(false);
                if (isAdded()) {
                    pbMain.setVisibility(View.GONE);
                }

                if (response != null && response.body().getStatus().equals("ok")) {
                    listFollowings = response.body();
                    showUsers();
                } else {
                    errorLoadDate();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                refreshLayout.setRefreshing(false);
                if (isAdded()) {
                    pbMain.setVisibility(View.GONE);
                }
                errorLoadDate();
            }
        });


    }


    public void errorLoadDate() {
        final RelativeLayout layoutErrorConection = (RelativeLayout) view.findViewById(R.id.layoutErrorConection);
        layoutErrorConection.setVisibility(View.VISIBLE);
        layoutErrorConection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeTaskGetAllUsers();
                layoutErrorConection.setVisibility(View.GONE);
            }
        });

    }

    public void showUsers() {
        if (listFollowings.getUsers().size() != 0) {
            mAdapter = new AdapterFollowers(activity, listFollowings.getUsers());
            listView.setAdapter(mAdapter);
            editsearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable arg0) {
                    String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
                    mAdapter.filter(text);
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                }

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                }
            });

        } else {
            final RelativeLayout layoutNoFollowings = (RelativeLayout) view.findViewById(R.id.layoutNoFollowers);
            layoutNoFollowings.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }


    public void gotoUser(User user) {
        Intent i = new Intent(activity, UserActivity.class);
        i.putExtra("idUser", user.getId());
        activity.startActivity(i);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


}

