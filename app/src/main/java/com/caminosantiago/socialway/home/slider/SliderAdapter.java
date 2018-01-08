package com.caminosantiago.socialway.home.slider;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.caminosantiago.socialway.model.User;

import java.util.List;


/**
 * Created by redegal010 on 8/07/15.
 */
public class SliderAdapter extends FragmentPagerAdapter {

    List<User> listUser;
    private int pos = 0;


    public SliderAdapter(FragmentManager fm, List<User> listUser) {
        super(fm);
        this.listUser=listUser;
    }


    public int getCount() {
        return Integer.MAX_VALUE;
    }


    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView((View) arg2);
    }

    @Override
    public Fragment getItem(int position) {
        if (pos >= listUser.size() - 1)
            pos = 0;
        else
            ++pos;

        FragmentSlider fragmentSlider=  FragmentSlider.newInstance(listUser.get(pos));

        return fragmentSlider;
    }


}







