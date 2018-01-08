package com.caminosantiago.socialway;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caminosantiago.socialway.model.ResultAyuda;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class HelpFragment extends Fragment {

    View view;

    public static HelpFragment newInstance() {
        HelpFragment fragment = new HelpFragment();
        return fragment;
    }

    public HelpFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_help, container, false);
        controlToolbar();
        executeTaskGetInfo();
        return view;
    }

    public void controlToolbar(){
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        TextView title1 = (TextView) toolbar.findViewById(R.id.textViewTitleApp);
        title1.setText(R.string.app_name);
    }




    public void executeTaskGetInfo(){
          final Dialog dialog = Utils.showDialog(getActivity(),R.string.loading);

        final Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        MyApiEndpointInterface apiService =retrofit.create(MyApiEndpointInterface.class);
        Call<ResultAyuda> call = apiService.getInfoApp();
        call.enqueue(new Callback<ResultAyuda>() {
            @Override
            public void onResponse(Response<ResultAyuda> response, Retrofit retrofit) {
                if (dialog.isShowing())
                    dialog.dismiss();

                if ( response.body().getStatus().equals("ok")) {
                    showData(response.body());
                } else {
                    errorLoadDate();
                }
            }

            @Override
            public void onFailure(Throwable t) {
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
                executeTaskGetInfo();
                layoutErrorConection.setVisibility(View.GONE);
            }
        });

    }


    public void showData(final ResultAyuda data){
        Button inviteApp=(Button)view.findViewById(R.id.button7);
        inviteApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Sharing SocialWay");
                i.putExtra(Intent.EXTRA_TEXT, data.getEnlace());
                startActivity(Intent.createChooser(i, "Share SocialWay"));
            }
        });

    }

}
