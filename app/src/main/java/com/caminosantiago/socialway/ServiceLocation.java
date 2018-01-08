package com.caminosantiago.socialway;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by redegal010 on 19/10/15.
 */


public class ServiceLocation extends  Service {
    LocationManager lm;
    boolean gps_enabled=false;
    boolean network_enabled=false;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("Location", "start");

        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            getLocation();

                        } catch (Exception e) { }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 10000);


       // return Service.START_STICKY; Se repite aunque la app se cierre
         return Service.START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Location","destroy");

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public boolean getLocation()
    {
        if(lm==null)
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //exceptions will be thrown if provider is not permitted.
        try{gps_enabled=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}
        try{network_enabled=lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}

        //don't start listeners if no provider is enabled
        if(!gps_enabled && !network_enabled)
            return false;

        if(gps_enabled)
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);

        if(network_enabled)
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);


        getLastLocation();


        return true;
    }


    public void getLastLocation() {
        lm.removeUpdates(locationListenerGps);
        lm.removeUpdates(locationListenerNetwork);

        Location net_loc=null, gps_loc=null;

        if(gps_enabled)
            gps_loc=lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(network_enabled)
            net_loc=lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        //if there are both values use the latest one
        if(gps_loc!=null && net_loc!=null){
            if(gps_loc.getTime()>net_loc.getTime())
                setLocationResult(gps_loc);
            else
                setLocationResult(net_loc);
            return;
        }

        if(gps_loc!=null){
            setLocationResult(gps_loc);
            return;
        }
        if(net_loc!=null){
            setLocationResult(net_loc);
            return;
        }
        setLocationResult(null);
    }


    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            setLocationResult(location);
            lm.removeUpdates(this);
            lm.removeUpdates(locationListenerNetwork);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };


    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            setLocationResult(location);
            lm.removeUpdates(this);
            lm.removeUpdates(locationListenerGps);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };


    public void setLocationResult(Location location){
        if (location!=null){
            Log.i("Location",location.toString());

            String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            SharedPreferences prefs = getSharedPreferences("dataUser", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("updadePosition",currentDate );
            editor.putFloat("latitude", (float)location.getLatitude());
            editor.putFloat("longitude", (float) location.getLongitude());
            editor.commit();
        }
    }


}
