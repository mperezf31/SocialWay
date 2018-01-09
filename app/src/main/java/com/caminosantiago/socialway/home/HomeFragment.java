package com.caminosantiago.socialway.home;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.caminosantiago.socialway.model.User;
import com.caminosantiago.socialway.model.query.ListComments;
import com.caminosantiago.socialway.model.query.ListPublications;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class HomeFragment extends Fragment implements AdapterPublication.OnInteractionHome {

    ListView list;
    Activity activity;
    int positionSetComment = -1;
    View view;
    static HomeFragment fragment;
    public static List<Publication> listPublications = new ArrayList<>();
    List<Integer> listFavourites = new ArrayList<>();
    ProgressDialog dialog;
    PullRefreshLayout refreshLayout;

    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance() {
        fragment = new HomeFragment();
        return fragment;
    }

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        if (Utils.isLogin(activity)) {
            controlToolbar();
            refreshLayout = (PullRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
            refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    executeTaskGetPublications();
                }
            });

            list = (ListView) view.findViewById(R.id.list);
            executeTaskGetPublications();
        }

        return view;
    }


    public void executeTaskGetPublications() {
        if (!refreshLayout.isActivated())
            dialog = Utils.showDialog(activity, R.string.loading);

        final Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        MyApiEndpointInterface apiService = retrofit.create(MyApiEndpointInterface.class);
        Call<ListPublications> call = apiService.getPublications(Utils.getUserID(activity), Utils.getAllCurrentDate(), Utils.getMyToken(activity));
        call.enqueue(new Callback<ListPublications>() {
            @Override
            public void onResponse(Response<ListPublications> response, Retrofit retrofit) {
                refreshLayout.setRefreshing(false);
                if (dialog.isShowing())
                    dialog.dismiss();

                if (response.body() != null && response.body().getStatus().equals("ok")) {
                    saveUserInfo(response.body().getUserInfo());
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

    private void saveUserInfo(User userInfo) {
        Utils.saveUser(getActivity(), userInfo.getId(), userInfo.getName(), userInfo.getImageAvatar(), userInfo.getImageFondo());
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


    public void controlToolbar() {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        TextView title1 = (TextView) toolbar.findViewById(R.id.textViewTitleApp);
        title1.setText(R.string.app_name);
    }


    public void loadPublications() {
        AdapterPublication mAdapter = new AdapterPublication(fragment, activity, listPublications, listFavourites);
        list.setAdapter(mAdapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void goComments(ListComments listComments, int positionSetComment) {
        this.positionSetComment = positionSetComment;
        Intent i = new Intent(activity, CommentsActivity.class);
        i.putExtra("data", listComments);
        i.putExtra("numPubli", positionSetComment);
        i.putExtra("procedencia", 1);
        startActivity(i);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (positionSetComment != -1) {
            loadPublications();
            list.setSelection(positionSetComment + 1);
            positionSetComment = -1;
        }
    }

}
