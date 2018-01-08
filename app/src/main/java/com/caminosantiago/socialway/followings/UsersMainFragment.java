package com.caminosantiago.socialway.followings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.caminosantiago.socialway.R;
import com.caminosantiago.socialway.followings.followers.FollowersFragment;

public class UsersMainFragment extends Fragment {

    private FragmentTabHost mTabHost;

    public static UsersMainFragment newInstance() {
        UsersMainFragment fragment = new UsersMainFragment();
        return fragment;
    }
    public UsersMainFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTabHost = null;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_tabs_follow,container, false);
        final FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.containerMain, AllUsersFragment.newInstance()) .commit();

        RadioGroup radioButtonGroup=(RadioGroup)rootView.findViewById(R.id.radioButtonGroup);
        radioButtonGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId==R.id.radioButtonAllUsers){
                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.beginTransaction().replace(R.id.containerMain, AllUsersFragment.newInstance()) .commit();
                }else if (checkedId==R.id.radioButton){
                   fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                   fragmentManager.beginTransaction().replace(R.id.containerMain, FollowingFragment.newInstance()) .commit();
               } else
                     fragmentManager.beginTransaction().replace(R.id.containerMain, FollowersFragment.newInstance()) .commit();

            }
        });


        return rootView;
    }
}
