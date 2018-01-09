package com.caminosantiago.socialway;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.caminosantiago.socialway.comments.AdapterFavourites;
import com.caminosantiago.socialway.model.LoadImage;
import com.caminosantiago.socialway.model.Publication;
import com.caminosantiago.socialway.model.ResultWS;
import com.caminosantiago.socialway.model.User;
import com.caminosantiago.socialway.model.query.ListFavourites;
import com.caminosantiago.socialway.model.query.UserData;
import com.caminosantiago.socialway.user.UserActivity;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by admin on 31/10/2015.
 */
public class WS extends Application {


    public static void setFavourite(Activity activity,Button button,List<Integer> listFavourites, int idPublication,Publication publication,RelativeLayout layoutLikes,TextView textView){
        if (Utils.isFavourite(listFavourites,idPublication))
            removeFavourite(activity,button,listFavourites,idPublication,publication,layoutLikes,textView);
        else
            addFavourite(activity,button,listFavourites,idPublication,publication,layoutLikes,textView);

    }

    public static void addFavourite(final Activity activity, final Button button,final List<Integer> listFavourites, final int idPublication, final Publication publication, final RelativeLayout layoutLikes, final TextView textView){

            final ProgressDialog dialog = new ProgressDialog(activity);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Conectando con el servidor...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
            final Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
            MyApiEndpointInterface apiService =retrofit.create(MyApiEndpointInterface.class);
            Call<ResultWS> call = apiService.addFavourite(Utils.getUserID(activity), idPublication);
            call.enqueue(new Callback<ResultWS>() {
                @Override
                public void onResponse(Response<ResultWS> response, Retrofit retrofit) {
                    dialog.dismiss();
                    if (response.body().getStatus().equals("ok")) {
                        listFavourites.add(idPublication);
                        button.setTextColor(activity.getResources().getColor(R.color.blue));
                        publication.setNumFavourites(publication.getNumFavourites() + 1);
                        layoutLikes.setVisibility(View.VISIBLE);
                        textView.setText(publication.getNumFavourites()+"");

                    } else {
                        Toast.makeText(activity,R.string.error_conection,Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    dialog.dismiss();
                    Toast.makeText(activity,R.string.error_conection,Toast.LENGTH_LONG).show();
                }
            });
    }

    public static void removeFavourite(final Activity activity, final Button button,final List<Integer> listFavourites, final int idPublication, final Publication publication, final RelativeLayout layoutLikes, final TextView textView){

        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Conectando con el servidor...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        MyApiEndpointInterface apiService =retrofit.create(MyApiEndpointInterface.class);
        Call<ResultWS> call = apiService.deleteFavourite(Utils.getUserID(activity), idPublication);
        call.enqueue(new Callback<ResultWS>() {
            @Override
            public void onResponse(Response<ResultWS> response, Retrofit retrofit) {
                dialog.dismiss();
                if (response.body().getStatus().equals("ok")) {
                    listFavourites.remove((Object) idPublication);
                    button.setTextColor(Color.BLACK);
                    publication.setNumFavourites(publication.getNumFavourites() - 1);
                    if (publication.getNumFavourites()==0)
                        layoutLikes.setVisibility(View.GONE);
                    else
                      textView.setText(publication.getNumFavourites() + "");
                } else {
                    Toast.makeText(activity,R.string.error_conection,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                dialog.dismiss();
                Toast.makeText(activity,R.string.error_conection,Toast.LENGTH_LONG).show();
            }
        });
    }





    public static void showFavourites(final Activity activity,int idPublication){

        final ProgressDialog dialog = Utils.showDialog(activity,R.string.loading);
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        MyApiEndpointInterface apiService =retrofit.create(MyApiEndpointInterface.class);
        Call<ListFavourites> call = apiService.getFavourites(idPublication);
        call.enqueue(new Callback<ListFavourites>() {
            @Override
            public void onResponse(final Response<ListFavourites> response, Retrofit retrofit) {
                dialog.dismiss();
                if (response.body().getStatus().equals("ok")) {

                    final Dialog dialog = new Dialog(activity);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                    dialog.setContentView(R.layout.layout_likes);
                    ListView listViewLikes = (ListView) dialog.findViewById(R.id.listViewLikes);
                    listViewLikes.setDividerHeight(0);
                    AdapterFavourites mAdapter = new AdapterFavourites(activity, response.body().getFavourites());
                    listViewLikes.setAdapter(mAdapter);
                    listViewLikes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            dialog.dismiss();
                            Intent i = new Intent(activity, UserActivity.class);
                            i.putExtra("idUser", response.body().getFavourites().get(position).getUser().getId());
                            activity.startActivity(i);
                            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    });

                    TextView textViewClose = (TextView) dialog.findViewById(R.id.textViewClose);
                    textViewClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();



                } else {
                    Toast.makeText(activity, R.string.error_conection, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                dialog.dismiss();
                Toast.makeText(activity, R.string.error_conection, Toast.LENGTH_LONG).show();
            }
        });
    }




    public static  void taskSendImages(final Activity activity, Publication data){
        LoadImage loadImage=new LoadImage(Utils.getUserID(activity),data.getDescription().toString(),data.getLat(),data.getLon(),data.getListImages());
        final ProgressDialog dialog = Utils.showDialog(activity,R.string.enviando_publication);
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        MyApiEndpointInterface apiService =retrofit.create(MyApiEndpointInterface.class);
        Call<ResultWS> call = apiService.sharePublication(loadImage);
        call.enqueue(new Callback<ResultWS>() {
            @Override
            public void onResponse(Response<ResultWS> response, Retrofit retrofit) {
                dialog.hide();
                if (response.body().getStatus().equals("ok")){
                    Toast.makeText(activity, R.string.publication_ok, Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(activity, R.string.error_conection, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                dialog.hide();
                Toast.makeText(activity, R.string.error_conection, Toast.LENGTH_LONG).show();
            }
        });


    }


    public static void addFollow(final Activity activity, final UserData idUserFollow,final Button button){

        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Añadiendo following..");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        MyApiEndpointInterface apiService =retrofit.create(MyApiEndpointInterface.class);
        Call<ResultWS> call = apiService.addFollow(Utils.getUserID(activity), idUserFollow.getUserInfo().getId());
        call.enqueue(new Callback<ResultWS>() {
            @Override
            public void onResponse(Response<ResultWS> response, Retrofit retrofit) {
                dialog.dismiss();
                if (response.body().getStatus().equals("ok")) {
                    button.setTextColor(activity.getResources().getColor(R.color.blue));
                    idUserFollow.setFollow(1);
                } else {
                    Toast.makeText(activity,R.string.error_conection,Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                dialog.dismiss();
                Toast.makeText(activity,R.string.error_conection,Toast.LENGTH_LONG).show();
            }
        });
    }


    public static void addFollow2(final Activity activity, final User idUserFollow,final TextView button){

        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Añadiendo following..");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        MyApiEndpointInterface apiService =retrofit.create(MyApiEndpointInterface.class);
        Call<ResultWS> call = apiService.addFollow(Utils.getUserID(activity), idUserFollow.getId());
        call.enqueue(new Callback<ResultWS>() {
            @Override
            public void onResponse(Response<ResultWS> response, Retrofit retrofit) {
                dialog.dismiss();
                if (response.body().getStatus().equals("ok")) {
                    button.setTextColor(activity.getResources().getColor(R.color.blue));
                    idUserFollow.setFollow(1);
                } else {
                    Toast.makeText(activity,R.string.error_conection,Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                dialog.dismiss();
                Toast.makeText(activity,R.string.error_conection,Toast.LENGTH_LONG).show();
            }
        });
    }



    public static void removeFollow(final Activity activity, final UserData idUserFollow,final Button button){

        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Eliminando following..");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        MyApiEndpointInterface apiService =retrofit.create(MyApiEndpointInterface.class);
        Call<ResultWS> call = apiService.removeFollow(Utils.getUserID(activity), idUserFollow.getUserInfo().getId());
        call.enqueue(new Callback<ResultWS>() {
            @Override
            public void onResponse(Response<ResultWS> response, Retrofit retrofit) {
                dialog.dismiss();
                if (response.body().getStatus().equals("ok")) {
                    button.setTextColor(Color.GRAY);
                    idUserFollow.setFollow(0);

                } else {
                    Toast.makeText(activity,R.string.error_conection,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                dialog.dismiss();
                Toast.makeText(activity,R.string.error_conection,Toast.LENGTH_LONG).show();
            }
        });
    }


    public static void removeFollow2(final Activity activity, final User idUserFollow,final TextView button){

        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Eliminando following..");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        MyApiEndpointInterface apiService =retrofit.create(MyApiEndpointInterface.class);
        Call<ResultWS> call = apiService.removeFollow(Utils.getUserID(activity), idUserFollow.getId());
        call.enqueue(new Callback<ResultWS>() {
            @Override
            public void onResponse(Response<ResultWS> response, Retrofit retrofit) {
                dialog.dismiss();
                if (response.body().getStatus().equals("ok")) {
                    button.setTextColor(Color.GRAY);
                    idUserFollow.setFollow(0);

                } else {
                    Toast.makeText(activity,R.string.error_conection,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                dialog.dismiss();
                Toast.makeText(activity,R.string.error_conection,Toast.LENGTH_LONG).show();
            }
        });
    }

}
