package com.caminosantiago.socialway.comments;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.caminosantiago.socialway.R;
import com.caminosantiago.socialway.model.Comment;

import java.util.List;

/**
 * Created by root on 17/10/2015.
 */
public class AdapterFavourites extends ArrayAdapter<Comment> {

    private final Activity context;
    List<Integer> listFavourites;

    public AdapterFavourites(Activity context, List<Comment> contenido) {
        super(context, R.layout.item_favourite, contenido);
        this.context = context;

    }


    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final Comment data = getItem(position);
        View rowView= inflater.inflate(R.layout.item_favourite, null, true);
        final ImageView imageView = (ImageView) rowView.findViewById(R.id.imgAvatarPublication);
        Glide.with(context).load(data.getUser().getImageAvatar()).fitCenter().error(R.drawable.img_default).into(imageView);


        TextView name = (TextView) rowView.findViewById(R.id.name);
        name.setText(data.getUser().getName().replaceAll("\"", ""));
        TextView fechaPublicacion = (TextView) rowView.findViewById(R.id.info);
        fechaPublicacion.setText(data.getUser().getEstado());



        return rowView;
    }







}