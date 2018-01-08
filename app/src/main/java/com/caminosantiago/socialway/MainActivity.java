package com.caminosantiago.socialway;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caminosantiago.socialway.chat.notifications.RegistrationIntentService;
import com.caminosantiago.socialway.followings.UsersMainFragment;
import com.caminosantiago.socialway.followings.FollowingFragment;
import com.caminosantiago.socialway.home.HomeFragment;
import com.caminosantiago.socialway.home.MainFragment;
import com.caminosantiago.socialway.loadPublication.LoadPublicationActivity;
import com.caminosantiago.socialway.user.UserFragment;


public class MainActivity extends AppCompatActivity implements FollowingFragment.OnFragmentInteractionListener,NavigationDrawerFragment.NavigationDrawerCallbacks,HomeFragment.OnFragmentInteractionListener {


    private NavigationDrawerFragment mNavigationDrawerFragment;
    DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    TextView titleToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        titleToolbar = (TextView) toolbar.findViewById(R.id.textViewTitleApp);

        registerNotifications();

        if (!Utils.isLogin(this)){
            Intent intent=new Intent(this,LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else{
            drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
            final LinearLayout parentContainer=(LinearLayout)findViewById(R.id.parentContainer);

            mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
            mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

            mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,R.string.app_name, R.string.app_name) {
                public void onDrawerClosed(View view) {   supportInvalidateOptionsMenu();}
                public void onDrawerOpened(View drawerView) { supportInvalidateOptionsMenu(); }

                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
                    super.onDrawerSlide(drawerView, slideOffset);
                    parentContainer.setTranslationX(slideOffset * drawerView.getWidth());
                    drawerLayout.bringChildToFront(drawerView);
                    drawerLayout.requestLayout();
                }
            };
            drawerLayout.setDrawerListener(mDrawerToggle);
            drawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent));
        }

        //Iniciamos servicio para obtener la posicion si no está ya activado
        if (!Utils.isServiceRunning(MainActivity.this, ServiceLocation.class)){
            Intent i= new Intent(MainActivity.this, ServiceLocation.class);
            startService(i);
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (position==0){
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentManager.beginTransaction().replace(R.id.container, MainFragment.newInstance()) .commit();
        }

        else if (position==1)
            fragmentManager.beginTransaction().replace(R.id.container, UsersMainFragment.newInstance()).commit();
        else if (position==2){
            Intent i = new Intent(this, LoadPublicationActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.indicator_no_animator, R.anim.indicator_no_animator);}
        else if (position==3)
            fragmentManager.beginTransaction().replace(R.id.container, UserFragment.newInstance("", Utils.getIdUser(this))) .commit();
        else if (position == 4)
            fragmentManager.beginTransaction().replace(R.id.container, HelpFragment.newInstance()).commit();

    }

    @Override
    public void onFragmentInteraction(Uri uri) { }

    @Override
    public void goToHome() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, MainFragment.newInstance()) .commit();

    }

    public void registerNotifications(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getString("tokenNotifications","").equals("")){
            Intent intentService = new Intent(this, RegistrationIntentService.class);
            startService(intentService);
        }
    }

}
