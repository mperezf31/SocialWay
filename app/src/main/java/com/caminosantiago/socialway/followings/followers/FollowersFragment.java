package com.caminosantiago.socialway.followings.followers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.caminosantiago.socialway.BuildConfig;
import com.caminosantiago.socialway.MyApiEndpointInterface;
import com.caminosantiago.socialway.R;
import com.caminosantiago.socialway.Utils;
import com.caminosantiago.socialway.model.User;
import com.caminosantiago.socialway.model.query.ListFollowings;
import com.caminosantiago.socialway.user.UserActivity;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;


public class FollowersFragment extends Fragment {
    ProgressDialog dialog;
    static FollowersFragment fragment;
    Activity activity;
    ListView listView;
    View view;
    TextView titleFollow;
    AdapterFollowers mAdapter;
    PullRefreshLayout refreshLayout;
    ListFollowings listFollowings;
    public static FollowersFragment newInstance() {
        fragment = new FollowersFragment();
        return fragment;
    }

    public FollowersFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,   Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_following, container, false);
        titleFollow=(TextView)view.findViewById(R.id.titleFollow);
        titleFollow.setText(R.string.peregrinos_que_te_siguen);
        controlToolbar();
        listView=(ListView)view.findViewById(R.id.listView2);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gotoUser((User) parent.getAdapter().getItem(position));
            }
        });

        refreshLayout = (PullRefreshLayout)view.findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                executeTaskGetFollowings();
            }
        });

        executeTaskGetFollowings();
        return view;
    }



    public void controlToolbar(){
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        TextView title1 = (TextView) toolbar.findViewById(R.id.textViewTitleApp);
        title1.setText(R.string.app_name);
    }




    public void executeTaskGetFollowings(){
        if (!refreshLayout.isActivated())
            dialog = Utils.showDialog(activity, R.string.loading);

        final Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        MyApiEndpointInterface apiService =retrofit.create(MyApiEndpointInterface.class);
        Call<ListFollowings> call = apiService.getFollowers(Utils.getIdUser(activity));
        call.enqueue(new Callback<ListFollowings>() {
            @Override
            public void onResponse(Response<ListFollowings> response, Retrofit retrofit) {
                refreshLayout.setRefreshing(false);
                if (dialog.isShowing())
                    dialog.dismiss();

                if (response != null && response.body().getStatus().equals("ok")) {
                    listFollowings = response.body();
                    showFollowings();
                } else {
                    errorLoadDate();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                refreshLayout.setRefreshing(false);
                if (dialog.isShowing())
                    dialog.dismiss();
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
                executeTaskGetFollowings();
                layoutErrorConection.setVisibility(View.GONE);
            }
        });

    }

     public void showFollowings(){
        if (listFollowings.getUsers().size()!=0){
            mAdapter = new AdapterFollowers(activity, listFollowings.getUsers());
            listView.setAdapter(mAdapter);
            titleFollow.setText(getString(R.string.peregrinos_que_te_siguen)+" ("+listFollowings.getUsers().size()+")");

        }else{
            final RelativeLayout layoutNoFollowings = (RelativeLayout) view.findViewById(R.id.layoutNoFollowers);
            layoutNoFollowings.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity=activity;
    }

    public void gotoUser(User user) {
        Intent i = new Intent(activity, UserActivity.class);
        i.putExtra("idUser",user.getId());
        activity.startActivity(i);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


}

