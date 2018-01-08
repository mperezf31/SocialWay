package com.caminosantiago.socialway.home;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.baoyz.widget.PullRefreshLayout;
import com.caminosantiago.socialway.BuildConfig;
import com.caminosantiago.socialway.MyApiEndpointInterface;
import com.caminosantiago.socialway.R;
import com.caminosantiago.socialway.Utils;
import com.caminosantiago.socialway.comments.CommentsActivity;
import com.caminosantiago.socialway.home.slider.FixedSpeedScroller;
import com.caminosantiago.socialway.home.slider.SliderAdapter;
import com.caminosantiago.socialway.loadPublication.LoadPublicationActivity;
import com.caminosantiago.socialway.model.User;
import com.caminosantiago.socialway.model.query.ListComments;
import com.caminosantiago.socialway.model.query.ListPublications;
import com.caminosantiago.socialway.model.Publication;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class HomeFragment extends Fragment implements AdapterPublication.OnInteractionHome {

    ViewPager viewPager;
    Handler mHandler;
    Runnable mUpdateResults;
    Timer timer;
    TimerTask task;
    ListView list;
    Activity activity;
    int positionSetComment=-1;
    View view;
    static HomeFragment fragment;
    private OnFragmentInteractionListener mListener;
    public static List<Publication> listPublications=new ArrayList<>();
    List<Integer> listFavourites=new ArrayList<>();
    ProgressDialog dialog;
    PullRefreshLayout refreshLayout;
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance() {
        fragment = new HomeFragment();
        return fragment;
    }

    public HomeFragment() {}

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
            View header = inflater.inflate(R.layout.header_home, list, false);
            list.addHeaderView(header, null, false);
            viewPager = (ViewPager) header.findViewById(R.id.view_pager);

            executeTaskGetPublications();
        }

        return view;
    }


    public void executeTaskGetPublications(){
        if (!refreshLayout.isActivated())
             dialog = Utils.showDialog(activity,R.string.loading);

        final Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        MyApiEndpointInterface apiService =retrofit.create(MyApiEndpointInterface.class);
        Call<ListPublications> call = apiService.getPublications(Utils.getIdUser(activity),Utils.getAllCurrentDate(),Utils.getMyToken(activity));
        call.enqueue(new Callback<ListPublications>() {
            @Override
            public void onResponse(Response<ListPublications> response, Retrofit retrofit) {
                refreshLayout.setRefreshing(false);
                if (dialog.isShowing())
                    dialog.dismiss();

                if (response.body()!=null && response.body().getStatus().equals("ok")) {
                    initSlider(response.body().getHeader());
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

    public void initSlider(List<User> listUser){

        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(viewPager.getContext());
            mScroller.set(viewPager, scroller);
        } catch (NoSuchFieldException e) {} catch (IllegalArgumentException e) {} catch (IllegalAccessException e) { }

        ArrayList<Publication> publications = new ArrayList<Publication>();
        SliderAdapter adapter = new SliderAdapter(getFragmentManager() ,listUser);
        viewPager.setAdapter(adapter);
    }

    public void loadPublications(){
        AdapterPublication mAdapter = new AdapterPublication(fragment,activity, listPublications,listFavourites);
        list.setAdapter(mAdapter);
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity=activity;
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void goComments(ListComments listComments,int positionSetComment) {
        this.positionSetComment=positionSetComment;
        Intent i = new Intent(activity, CommentsActivity.class);
        i.putExtra("data",listComments);
        i.putExtra("numPubli",positionSetComment);
        i.putExtra("procedencia",1);
        startActivity(i);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


 /*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            listPublications.get(positionSetComment).setNumComments(data.getIntExtra("num", 0));
            loadPublications();
            list.setSelection(positionSetComment+1);
        }
    }
*/

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

    public void AnimateandSlide() {
        mHandler = new Handler();
        // Create runnable for posting
        mUpdateResults = new Runnable() {
            public void run() {
                viewPager.setCurrentItem(viewPager.getCurrentItem()+1,true);
            }
        };

        int delay = 4000; // delay for x sec.
        int period = 5000; // repeat every x sec.
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                mHandler.post(mUpdateResults);
            }
        }, delay, period);

    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
        mHandler.removeCallbacks(mUpdateResults);

    }

    @Override
    public void onResume() {
        super.onResume();
        AnimateandSlide();

        if (positionSetComment!=-1){
            loadPublications();
            list.setSelection(positionSetComment+1);
            positionSetComment=-1;
        }


    }

}
