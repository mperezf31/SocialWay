package com.caminosantiago.socialway;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.caminosantiago.socialway.chat.notifications.RegistrationIntentService;
import com.caminosantiago.socialway.followings.FollowingFragment;
import com.caminosantiago.socialway.followings.UsersMainFragment;
import com.caminosantiago.socialway.home.MainFragment;
import com.caminosantiago.socialway.loadPublication.LoadPublicationFragment;
import com.caminosantiago.socialway.model.User;
import com.caminosantiago.socialway.user.UserFragment;


public class MainActivity extends AppCompatActivity implements MainFragment.OnFragmentInteractionListener, UserFragment.OnFragmentInteractionListener, FollowingFragment.OnFragmentInteractionListener, LoadPublicationFragment.OnFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener {

    TextView titleToolbar;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        titleToolbar = (TextView) toolbar.findViewById(R.id.textViewTitleApp);

        registerNotifications();

        if (!Utils.isLogin(this)) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close) {
                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    User userData = Utils.getUserData(MainActivity.this);

                    if (userData.getImageAvatar() != null && !userData.getImageAvatar().isEmpty()) {
                        final ImageView iconUser = (ImageView) drawerView.findViewById(R.id.imageViewUserAvatar);
                        Glide.with(MainActivity.this).load(userData.getImageAvatar()).asBitmap().fitCenter().error(R.drawable.default_avatar).into(new BitmapImageViewTarget(iconUser) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                iconUser.setImageDrawable(circularBitmapDrawable);
                            }
                        });
                    }

                    if (userData.getName() != null) {
                        ((TextView) drawerView.findViewById(R.id.textViewUserName)).setText(userData.getName());
                    }
                }
            };

            drawer.setDrawerListener(toggle);

            drawer.setDrawerListener(toggle);
            toggle.syncState();

            navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            navigationView.findViewById(R.id.headerView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navigationView.setCheckedItem(R.id.nav_profile);
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, UserFragment.newInstance("", Utils.getUserID(MainActivity.this))).commit();
                    drawer.closeDrawer(GravityCompat.START);
                }
            });

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, MainFragment.newInstance()).commit();
        }

        //Iniciamos servicio para obtener la posicion si no está ya activado
        if (!Utils.isServiceRunning(MainActivity.this, ServiceLocation.class)) {
            Intent i = new Intent(MainActivity.this, ServiceLocation.class);
            startService(i);
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        if (id == R.id.nav_home) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentManager.beginTransaction().replace(R.id.container, MainFragment.newInstance()).commit();
        } else if (id == R.id.nav_pilgrim) {
            fragmentManager.beginTransaction().replace(R.id.container, UsersMainFragment.newInstance()).commit();
        } else if (id == R.id.nav_publish) {
            fragmentManager.beginTransaction().replace(R.id.container, LoadPublicationFragment.newInstance()).commit();
        } else if (id == R.id.nav_profile) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, UserFragment.newInstance("", Utils.getUserID(MainActivity.this))).commit();
        } else if (id == R.id.nav_help) {
            fragmentManager.beginTransaction().replace(R.id.container, HelpFragment.newInstance()).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void goToHome() {
        navigationView.setCheckedItem(R.id.nav_home);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, MainFragment.newInstance()).commit();

    }

    public void registerNotifications() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getString("tokenNotifications", "").equals("")) {
            Intent intentService = new Intent(this, RegistrationIntentService.class);
            startService(intentService);
        }
    }

    @Override
    public void goToPublish() {
        navigationView.setCheckedItem(R.id.nav_publish);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, LoadPublicationFragment.newInstance()).commit();
    }

    @Override
    public void goToProfile() {
        navigationView.setCheckedItem(R.id.nav_profile);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, UserFragment.newInstance("", Utils.getUserID(MainActivity.this))).commit();
    }
}
