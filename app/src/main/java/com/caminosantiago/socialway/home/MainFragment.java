package com.caminosantiago.socialway.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import com.caminosantiago.socialway.R;
import com.caminosantiago.socialway.loadPublication.LoadPublicationActivity;

public class MainFragment extends Fragment {

    private FragmentTabHost mTabHost;
    Activity activity;

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }
    public MainFragment() {
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity=activity;
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

        View rootView = inflater.inflate(R.layout.fragment_tabs,container, false);
      /*
        mTabHost = (FragmentTabHost)rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);
        mTabHost.addTab(mTabHost.newTabSpec("fragmentb").setIndicator("All"), HomeFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("fragmentc").setIndicator("Followings"),FollowingFragment.class, null);
*/

        final FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.containerMain, HomeFragment.newInstance()) .commit();

        RadioGroup radioButtonGroup=(RadioGroup)rootView.findViewById(R.id.radioButtonGroup);
        radioButtonGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
               if (checkedId==R.id.radioButton){
                   fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                   fragmentManager.beginTransaction().replace(R.id.containerMain, HomeFragment.newInstance()) .commit();
               } else if (checkedId==R.id.radioButton2){
                     fragmentManager.beginTransaction().replace(R.id.containerMain, TuCaminoFragment.newInstance()) .commit();

               }else if (checkedId==R.id.radioButton3){
                   fragmentManager.beginTransaction().replace(R.id.containerMain, HomeFollowingsFragment.newInstance()) .commit();

               }
            }
        });

        FloatingActionButton addPublication=(FloatingActionButton)rootView.findViewById(R.id.addPublication);
        addPublication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, LoadPublicationActivity.class);
                startActivity(i);
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        return rootView;
    }
}
