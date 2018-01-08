package com.caminosantiago.socialway.followings.followers;

import android.app.Activity;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.caminosantiago.socialway.R;
import com.caminosantiago.socialway.WS;
import com.caminosantiago.socialway.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by root on 17/10/2015.
 */
public class AdapterFollowers extends ArrayAdapter<User> {

    private final Activity context;
    List<User> contenido;
    private ArrayList<User> arraylist;

    public AdapterFollowers( Activity context, List<User> contenido) {
        super(context, R.layout.item_following, contenido);
        this.context = context;
        this.contenido=contenido;
        this.arraylist = new ArrayList<User>();
        this.arraylist.addAll(contenido);

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

        final TextView removeFollowing = (TextView) rowView.findViewById(R.id.textView13);
        removeFollowing.setText(R.string.fa_user_plus);
        if (data.getFollow()==1)
             removeFollowing.setTextColor(context.getResources().getColor(R.color.blue));
        else
            removeFollowing.setTextColor(Color.GRAY);

        removeFollowing.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
        removeFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.getFollow()==1)
                      WS.removeFollow2(context, data, removeFollowing);
                else
                      WS.addFollow2(context, data, removeFollowing);
            }
        });

        return rowView;
    }



    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        contenido.clear();
        if (charText.length() == 0) {
            contenido.addAll(arraylist);
        }else{
            for (User wp : arraylist)
            {
                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    contenido.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }


}