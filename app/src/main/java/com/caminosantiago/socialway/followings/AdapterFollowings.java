package com.caminosantiago.socialway.followings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.caminosantiago.socialway.BuildConfig;
import com.caminosantiago.socialway.MyApiEndpointInterface;
import com.caminosantiago.socialway.R;
import com.caminosantiago.socialway.Utils;
import com.caminosantiago.socialway.model.ResultWS;
import com.caminosantiago.socialway.model.User;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by root on 17/10/2015.
 */
public class AdapterFollowings extends ArrayAdapter<User> {

    private final Activity context;
    List<Integer> listFavourites;
    static OnInteractionFollowings callback;

    public AdapterFollowings(OnInteractionFollowings callback,Activity context, List<User> contenido) {
        super(context, R.layout.item_following, contenido);
        this.context = context;
        this.callback=callback;

    }


    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final User data = getItem(position);
        View rowView= inflater.inflate(R.layout.item_following, null, true);
        final ImageView imageView = (ImageView) rowView.findViewById(R.id.imgAvatarPublication);
        Glide.with(context).load(data.getImageAvatar()).fitCenter().error(R.drawable.img_default).into(imageView);

        TextView name = (TextView) rowView.findViewById(R.id.name);
        name.setText(data.getName().replaceAll("\"", ""));
        TextView fechaPublicacion = (TextView) rowView.findViewById(R.id.info);
        fechaPublicacion.setText(data.getEstado());

        TextView removeFollowing = (TextView) rowView.findViewById(R.id.textView13);
        removeFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFollow(context,data);
            }
        });


        return rowView;
    }


    public static void removeFollow(final Activity activity, final User data){

        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Eliminando following..");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        MyApiEndpointInterface apiService =retrofit.create(MyApiEndpointInterface.class);
        Call<ResultWS> call = apiService.removeFollow(Utils.getUserID(activity), data.getId());
        call.enqueue(new Callback<ResultWS>() {
            @Override
            public void onResponse(Response<ResultWS> response, Retrofit retrofit) {
                dialog.dismiss();
                if (response.body().getStatus().equals("ok")) {
                    callback.deleteItem(data);

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



    public interface OnInteractionFollowings{
        public void deleteItem(User user);
    }

}