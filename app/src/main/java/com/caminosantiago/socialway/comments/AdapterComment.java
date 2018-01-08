package com.caminosantiago.socialway.comments;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.caminosantiago.socialway.R;
import com.caminosantiago.socialway.Utils;
import com.caminosantiago.socialway.model.Comment;
import com.caminosantiago.socialway.user.UserActivity;

import java.util.List;

/**
 * Created by root on 17/10/2015.
 */
public class AdapterComment extends ArrayAdapter<Comment> {

    private final Activity context;
    List<Integer> listFavourites;

    public AdapterComment(Activity context, List<Comment> contenido) {
        super(context, R.layout.item_comment, contenido);
        this.context = context;

    }


    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final Comment data = getItem(position);
        View rowView= inflater.inflate(R.layout.item_comment, null, true);
        final ImageView imageView = (ImageView) rowView.findViewById(R.id.imgAvatarPublication);
        Glide.with(context).load(data.getUser().getImageAvatar()).fitCenter().error(R.drawable.img_default).into(imageView);

        TextView name = (TextView) rowView.findViewById(R.id.name);
        name.setText(data.getUser().getName().replaceAll("\"", ""));
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, UserActivity.class);
                i.putExtra("idUser", data.getUser().getId());
                context.startActivity(i);
                context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        TextView fechaPublicacion = (TextView) rowView.findViewById(R.id.info);
        fechaPublicacion.setText(Utils.formatDate(data.getFecha()));

        TextView des = (TextView) rowView.findViewById(R.id.textView2);
        des.setText(data.getTexto());


        return rowView;
    }







}