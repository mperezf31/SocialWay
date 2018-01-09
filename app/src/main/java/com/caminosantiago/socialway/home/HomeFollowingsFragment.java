package com.caminosantiago.socialway.home;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.caminosantiago.socialway.BuildConfig;
import com.caminosantiago.socialway.MyApiEndpointInterface;
import com.caminosantiago.socialway.R;
import com.caminosantiago.socialway.Utils;
import com.caminosantiago.socialway.comments.CommentsActivity;
import com.caminosantiago.socialway.model.Publication;
import com.caminosantiago.socialway.model.query.ListComments;
import com.caminosantiago.socialway.model.query.ListPublications;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class HomeFollowingsFragment extends Fragment implements AdapterPublication.OnInteractionHome {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    Handler mHandler;
    Runnable mUpdateResults;
    Timer timer;
    TimerTask task;
    ListView list;
    Activity activity;
    int positionSetComment=-1;
    View view;
    static HomeFollowingsFragment fragment;
    public  static  List<Publication> listPublications=new ArrayList<>();
    List<Integer> listFavourites=new ArrayList<>();
    ProgressDialog dialog;
    PullRefreshLayout refreshLayout;

    public static HomeFollowingsFragment newInstance() {
        fragment = new HomeFollowingsFragment();
        return fragment;
    }

    public HomeFollowingsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_home, container, false);

        if (Utils.isLogin(activity)){
            controlToolbar();
            refreshLayout = (PullRefreshLayout)view.findViewById(R.id.swipeRefreshLayout);
            refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    executeTaskGetPublications();
                }
            });

            list = (ListView)view.findViewById(R.id.list);
            executeTaskGetPublications();
        }

        return view;
    }


    public void executeTaskGetPublications(){
        if (!refreshLayout.isActivated())
             dialog = Utils.showDialog(activity,R.string.loading);

        final Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        MyApiEndpointInterface apiService =retrofit.create(MyApiEndpointInterface.class);
        Call<ListPublications> call = apiService.getPublicationsFollowings(Utils.getUserID(activity));
        call.enqueue(new Callback<ListPublications>() {
            @Override
            public void onResponse(Response<ListPublications> response, Retrofit retrofit) {
                refreshLayout.setRefreshing(false);
                if (dialog.isShowing())
                    dialog.dismiss();

                if (response.body().getStatus().equals("ok")) {
                    listPublications = response.body().getListPublication();
                    listFavourites = response.body().getFavourites();
                    loadPublications();

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
                executeTaskGetPublications();
                layoutErrorConection.setVisibility(View.GONE);
            }
        });

    }


    public void controlToolbar(){
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        TextView title1 = (TextView) toolbar.findViewById(R.id.textViewTitleApp);
        title1.setText(R.string.app_name);
    }



    public void loadPublications(){
        AdapterPublication mAdapter = new AdapterPublication(fragment,activity, listPublications,listFavourites);
        list.setAdapter(mAdapter);

        final RelativeLayout layoutNoFollowingsPublications = (RelativeLayout) view.findViewById(R.id.layoutNoFollowingsPublications);
        if (listPublications.size()==0){
            layoutNoFollowingsPublications.setVisibility(View.VISIBLE);
        }else{
            layoutNoFollowingsPublications.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (positionSetComment!=-1){
            loadPublications();
            list.setSelection(positionSetComment);
            positionSetComment=-1;
        }
    }

    @Override
    public void goComments(ListComments listComments,int positionSetComment) {
        this.positionSetComment=positionSetComment;
        Intent i = new Intent(activity, CommentsActivity.class);
        i.putExtra("data",listComments);
        i.putExtra("numPubli",positionSetComment);
        i.putExtra("procedencia",2);
        startActivity(i);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity=activity;
    }
}
