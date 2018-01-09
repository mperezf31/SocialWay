package com.caminosantiago.socialway.home;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.caminosantiago.socialway.BuildConfig;
import com.caminosantiago.socialway.MyApiEndpointInterface;
import com.caminosantiago.socialway.R;
import com.caminosantiago.socialway.Utils;
import com.caminosantiago.socialway.comments.CommentsActivity;
import com.caminosantiago.socialway.model.Publication;
import com.caminosantiago.socialway.model.ResultWS;
import com.caminosantiago.socialway.model.query.ListComments;
import com.caminosantiago.socialway.model.query.ListPublications;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class TuCaminoFragment extends Fragment implements AdapterPublication.OnInteractionHome {

    ListView list;
    Activity activity;
    int positionSetComment=-1;
    RelativeLayout layoutSelectWay;
    View view;
    TextView   myWayTitle;
    static TuCaminoFragment fragment;
    public  static  List<Publication> listPublications=new ArrayList<>();
    List<Integer> listFavourites=new ArrayList<>();
    ProgressDialog dialog;
    PullRefreshLayout refreshLayout;
    RelativeLayout layoutNoPublicationsWay;

    public static TuCaminoFragment newInstance() {
        fragment = new TuCaminoFragment();
        return fragment;
    }

    public TuCaminoFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_way, container, false);

        if (Utils.isLogin(activity)){
            controlToolbar();
            refreshLayout = (PullRefreshLayout)view.findViewById(R.id.swipeRefreshLayout);
            refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    executeTaskGetPublications();
                }
            });


            myWayTitle = (TextView)view.findViewById(R.id.textView18);
            layoutNoPublicationsWay = (RelativeLayout) view.findViewById(R.id.layoutNoPublicationsWay);

            RelativeLayout   layoutSetWay = (RelativeLayout)view.findViewById(R.id.layoutSetWay);
            layoutSetWay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layoutNoPublicationsWay.setVisibility(View.GONE);
                    showSelectorWay(view);
                }
            });

            list = (ListView)view.findViewById(R.id.list);
            if (!Utils.getUserCamino(activity).equals(""))
                 executeTaskGetPublications();
            else
                showSelectorWay(view);
        }

        return view;
    }


    public void showSelectorWay(View view){
        layoutSelectWay = (RelativeLayout) view.findViewById(R.id.layoutSelectWay);
        layoutSelectWay.setVisibility(View.VISIBLE);

        final Spinner spinner1 = (Spinner)view.findViewById(R.id.spinner);
        List<String> list = new ArrayList<String>();
        list.add("Camino a Finisterre");
        list.add("Camino Aragonés");
        list.add("Camino de Le Puy");
        list.add("Camino de Levante");
        list.add("Camino de Madrid");
        list.add("Camino del Norte");
        list.add("Camino del Salvador");
        list.add("Camino Francés");
        list.add("Camino Inglés");
        list.add("Camino Mozárabe");
        list.add("Camino Portugués");
        list.add("Camino Primitivo");
        list.add("Camino Sanabrés");
        list.add("Camino Vía de la Plata");
        list.add("Camino Vasco Interior");
        list.add("Ruta de la Lana");

        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item,list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(dataAdapter);
        String compareValue=Utils.getUserCamino(activity);
        if (!compareValue.equals(null) && !compareValue.equals("")) {
            int spinnerPosition = dataAdapter.getPosition(compareValue);
            spinner1.setSelection(spinnerPosition);
        }

        Button sendMyWay = (Button)view.findViewById(R.id.button8);
        sendMyWay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskSetTexts((String)spinner1.getSelectedItem());
            }
        });


    }






    public void taskSetTexts(final String camino){

        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(activity.getString(R.string.select_way_loaging));
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        MyApiEndpointInterface apiService =retrofit.create(MyApiEndpointInterface.class);
        Call<ResultWS> call = apiService.setWay(Utils.getUserID(activity), camino);
        call.enqueue(new Callback<ResultWS>() {
            @Override
            public void onResponse(Response<ResultWS> response, Retrofit retrofit) {
                dialog.dismiss();
                if (response.body()!=null && response.body().getStatus().equals("ok")) {
                    Utils.saveUserCamino(activity,camino);
                    layoutSelectWay.setVisibility(View.GONE);
                    executeTaskGetPublications();
                } else {
                    Snackbar.make(view, R.string.error_conection, Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                dialog.dismiss();
                Snackbar.make(view, R.string.error_conection, Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE).show();
            }
        });


    }

    public void executeTaskGetPublications(){
        if (!refreshLayout.isActivated())
             dialog = Utils.showDialog(activity,R.string.loading);

        final Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        MyApiEndpointInterface apiService =retrofit.create(MyApiEndpointInterface.class);
        Call<ListPublications> call = apiService.getPublicationsWay(Utils.getUserID(activity),Utils.getUserCamino(activity));
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

        myWayTitle.setText(Utils.getUserCamino(activity));
        if (listPublications.size()==0){
            layoutNoPublicationsWay.setVisibility(View.VISIBLE);
        }else{
            layoutNoPublicationsWay.setVisibility(View.GONE);
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
        i.putExtra("procedencia",3);
        startActivity(i);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity=activity;
    }
}
