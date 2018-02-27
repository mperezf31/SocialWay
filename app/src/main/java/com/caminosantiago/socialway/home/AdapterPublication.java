package com.caminosantiago.socialway.home;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.caminosantiago.socialway.BuildConfig;
import com.caminosantiago.socialway.MyApiEndpointInterface;
import com.caminosantiago.socialway.R;
import com.caminosantiago.socialway.Utils;
import com.caminosantiago.socialway.WS;
import com.caminosantiago.socialway.model.Publication;
import com.caminosantiago.socialway.model.query.ListComments;
import com.caminosantiago.socialway.user.UserActivity;

import java.util.List;

import me.relex.circleindicator.CircleIndicator;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by root on 17/10/2015.
 */
public class AdapterPublication extends ArrayAdapter<Publication> {

    private final Activity context;
    private static List<Publication> contenido;
    List<Integer> listFavourites;
    OnInteractionHome callback;

    static class ViewHolder {
        ImageView imageView;
        TextView name;
        TextView fechaPublicacion;
        TextView goMap;
        TextView des;
        ViewPager viewPager;
        CircleIndicator defaultIndicator;
        RelativeLayout layoutLikes;
        TextView textViewNumVotes;
        TextView textViewNumComments;
        RelativeLayout layoutComments;
        Button addFavourite;
        Button goComments;
        Button buttonShare;
    }


    public AdapterPublication(OnInteractionHome callback,Activity context, List<Publication> contenido, List<Integer> listFavourites) {
        super(context, R.layout.item_publication, contenido);
        this.context = context;
        this.contenido = contenido;
        this.listFavourites=listFavourites;
        this.callback=callback;
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = context.getLayoutInflater();
            convertView= inflater.inflate(R.layout.item_publication, null, true);
            // well set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imgAvatarPublication);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.fechaPublicacion = (TextView) convertView.findViewById(R.id.info);
            viewHolder.goMap = (TextView) convertView.findViewById(R.id.textView12);
            viewHolder.des = (TextView) convertView.findViewById(R.id.textView2);
            viewHolder.viewPager = (ViewPager) convertView.findViewById(R.id.imageView2);
            viewHolder.defaultIndicator = (CircleIndicator) convertView.findViewById(R.id.indicator);
            viewHolder.layoutLikes = (RelativeLayout) convertView.findViewById(R.id.layoutLikes);
            viewHolder.textViewNumVotes = (TextView) convertView.findViewById(R.id.textViewNumVotes);
            viewHolder.textViewNumComments = (TextView) convertView.findViewById(R.id.textViewNumComments);
            viewHolder.layoutComments = (RelativeLayout) convertView.findViewById(R.id.layoutComments);
            viewHolder.addFavourite = (Button) convertView.findViewById(R.id.button3);
            viewHolder.goComments = (Button) convertView.findViewById(R.id.button4);
            viewHolder.buttonShare = (Button) convertView.findViewById(R.id.buttonShare);

            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Publication data = getItem(position);

        // Avatar
        Glide.with(context).load(data.getUser().getImageAvatar()).fitCenter().error(R.drawable.img_default).into(viewHolder.imageView);

        // Nombre usuario
        viewHolder.name.setText(data.getUser().getName().replaceAll("\"", ""));

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, UserActivity.class);
                i.putExtra("idUser", data.getUser().getId());
                context.startActivity(i);
                context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);            }
        };

        viewHolder.name.setOnClickListener(listener);
        viewHolder.imageView.setOnClickListener(listener);
        viewHolder.fechaPublicacion.setOnClickListener(listener);

        // Fecha publicacion
        viewHolder.fechaPublicacion.setText(Utils.formatDate(data.getFecha()));


        // Ir a mapa
        if (data.getLat()==0 || data.getLon()==0)
            viewHolder.goMap.setVisibility(View.INVISIBLE);
        else{
            viewHolder.goMap.setVisibility(View.VISIBLE);
            viewHolder.goMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, MapsActivity.class);
                    i.putExtra("data", data);
                    context.startActivity(i);
                    context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
        }

        // Descripcion publicacion
        viewHolder.des.setText(data.getDescription());

        //Imagenes de la publicacion
        final PagerAdapterPublication adapterPager;
        if (data.getListImages().size()==0){
            viewHolder.viewPager.setVisibility(View.GONE);
            viewHolder.defaultIndicator.setVisibility(View.GONE);
        }else{
            viewHolder.viewPager.setVisibility(View.VISIBLE);
            viewHolder.defaultIndicator.setVisibility(View.VISIBLE);
            adapterPager = new PagerAdapterPublication(context,data.getListImages());
            viewHolder.viewPager.setAdapter(adapterPager);
            viewHolder.defaultIndicator.setViewPager(viewHolder.viewPager);
        }

        //Obciones ////////////////////////////////////////////////////////////////////////////////////////////////////////
        viewHolder.layoutLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WS.showFavourites(context,data.getId());
            }
        });

        if (data.getNumFavourites()!=0){
            viewHolder.layoutLikes.setVisibility(View.VISIBLE);
            viewHolder.textViewNumVotes.setText(data.getNumFavourites() +"");

        } else {
            viewHolder.layoutLikes.setVisibility(View.GONE);
        }


        viewHolder.layoutComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskGetComments(context, data.getId(), position);
            }
        });

        if (data.getNumComments()!=0){
            viewHolder.layoutComments.setVisibility(View.VISIBLE );
            viewHolder.textViewNumComments.setText(data.getNumComments() + "");
        }else{
            viewHolder.layoutComments.setVisibility(View.GONE );
        }


        if (Utils.isFavourite(listFavourites,data.getId()))
            viewHolder.addFavourite.setTextColor(context.getResources().getColor(R.color.blue));
        else
            viewHolder.addFavourite.setTextColor(Color.BLACK);

        viewHolder.addFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WS.setFavourite(context, viewHolder.addFavourite, listFavourites, data.getId(), data, viewHolder.layoutLikes, viewHolder.textViewNumVotes);
            }
        });

        viewHolder.goComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskGetComments(context,data.getId(),position);
            }
        });


        viewHolder.buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.publicar(context, data, viewHolder.viewPager.getCurrentItem());
            }
        });



        return convertView;
    }


    public void taskGetComments(final Activity activity,int idPublication, final int position){

            final ProgressDialog dialog = Utils.showDialog(activity,R.string.loading);
            final Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
            MyApiEndpointInterface apiService =retrofit.create(MyApiEndpointInterface.class);
            Call<ListComments> call = apiService.getComments(idPublication);
            call.enqueue(new Callback<ListComments>() {
                @Override
                public void onResponse(Response<ListComments> response, Retrofit retrofit) {
                    dialog.dismiss();
                    if (response.body().getStatus().equals("ok")) {
                        callback.goComments(response.body(),position);
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


    public interface OnInteractionHome{
        public void goComments(ListComments listComments,int position);

    }




}