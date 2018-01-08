package com.caminosantiago.socialway.home;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.caminosantiago.socialway.R;

import java.util.List;

/**
 * Created by root on 18/10/2015.
 */
public class PagerAdapterPublication extends PagerAdapter {
    Context context;
    public List<String> GalImages ;

    public  PagerAdapterPublication(Context context,List<String> GalImages){
        this.context=context;
        this.GalImages=GalImages;
    }


    public int getCount() {
        return GalImages.size();//Integer.MAX_VALUE;
    }


    public Object instantiateItem(View collection, final int position) {

        final ImageView mwebView = new ImageView(context);
        ((ViewPager) collection).addView(mwebView, 0);
        mwebView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        Glide.with(context).load(GalImages.get(position)).fitCenter()
                .error(R.drawable.img_default)
                .into(mwebView);

        mwebView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.image_dialog);
                ImageView image = (ImageView) dialog.findViewById(R.id.image);
                Glide.with(context).load(GalImages.get(position)).fitCenter().error(R.drawable.img_default).into(image);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                dialog.show();
                dialog.getWindow().setAttributes(lp);
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        return mwebView;
    }



    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView((View) arg2);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == ((View) arg1);
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

}