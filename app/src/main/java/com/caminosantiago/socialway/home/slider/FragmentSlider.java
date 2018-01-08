package com.caminosantiago.socialway.home.slider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.caminosantiago.socialway.R;
import com.caminosantiago.socialway.model.User;
import com.caminosantiago.socialway.user.UserActivity;

/**
 * Created by root on 18/10/2015.
 */
public class FragmentSlider extends Fragment {

Activity activity;

    public static FragmentSlider newInstance(User user) {

        FragmentSlider f = new FragmentSlider();
        Bundle b = new Bundle();
        b.putSerializable("user", user);
        f.setArguments(b);
        return f;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity=activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_slider, container, false);
        final User data= (User) getArguments().getSerializable("user");

        TextView nombre = (TextView) v.findViewById(R.id.textView);
        nombre.setText(data.getName());

        TextView estado = (TextView) v.findViewById(R.id.textViewEstadoUser);
        estado.setText(data.getEstado());

        ImageView ImageViewHeader = (ImageView)v.findViewById(R.id.ImageViewHeader);
        Glide.with(activity).load(data.getImageFondo()).fitCenter().error(R.drawable.img_default).into(ImageViewHeader);

        final ImageView iconUser = (ImageView)v.findViewById(R.id.iconUser);
        Glide.with(activity).load(data.getImageAvatar()).fitCenter().error(R.drawable.img_default).into(iconUser);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, UserActivity.class);
                i.putExtra("idUser", data.getId());
                activity.startActivity(i);
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        return v;
    }
}