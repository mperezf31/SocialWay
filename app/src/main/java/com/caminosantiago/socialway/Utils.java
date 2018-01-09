package com.caminosantiago.socialway;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.caminosantiago.socialway.chat.notifications.RegistrationIntentService;
import com.caminosantiago.socialway.model.User;
import com.google.android.gms.maps.model.LatLng;
import com.caminosantiago.socialway.model.Publication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by root on 17/10/2015.
 */
public class Utils {


    public static String getAllCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public static String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm dd/MM");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public static String dateToString(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    public static String getMyToken(Activity activity) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return sharedPreferences.getString("tokenNotifications", "");
    }

    //Funcion para guardar el usuario
    public static void saveUserCamino(Context context, String camino) {
        SharedPreferences prefs = context.getSharedPreferences("dataUser", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("camino", camino);
        editor.commit();

    }

    public static String getUserCamino(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("dataUser", Context.MODE_PRIVATE);
        return prefs.getString("camino", "");
    }


    //Funcion para guardar el usuario
    public static void saveUser(Context context, String id, String user, String avatar, String background) {
        SharedPreferences prefs = context.getSharedPreferences("dataUser", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("login", true);
        editor.putString("id", id);
        editor.putString("user", user);
        editor.putString("avatar", avatar);
        editor.putString("background", background);
        editor.commit();

    }

    //Funcion para guardar el usuario
    public static void updateUserName(Context context, String user) {
        SharedPreferences prefs = context.getSharedPreferences("dataUser", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user", user);
        editor.commit();

    }

    public static User getUserData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("dataUser", Context.MODE_PRIVATE);
        return new User(prefs.getString("id", ""), prefs.getString("user", ""), prefs.getString("avatar", ""), prefs.getString("background", ""));
    }

    public static String getUserID(Context context) {
        return getUserData(context).getId();
    }

    //Función para saber si un usuario está logueado
    public static boolean isLogin(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("dataUser", Context.MODE_PRIVATE);

        if (prefs.getBoolean("login", false)) {
            return true;
        } else {
            return false;
        }

    }


    public static ProgressDialog showDialog(Activity activity, int msg) {
        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(activity.getResources().getString(msg));
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        try {
            dialog.show();

        } catch (Exception e) {
        }

        return dialog;
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }


    public static boolean isFavourite(List<Integer> list, int idPublication) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == idPublication)
                return true;
        }

        return false;
    }


    public static void publicar(final Activity activity, final Publication publication, final int numImage) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_share);
        dialog.setCancelable(true);

        final RadioButton facebook = (RadioButton) dialog.findViewById(R.id.radioButton1);
        Button button = (Button) dialog.findViewById(R.id.buttonOK);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (facebook.isChecked()) {
                    Glide.with(activity)
                            .load(publication.getListImages().get(numImage))
                            .asBitmap()
                            .into(new SimpleTarget<Bitmap>(100, 100) {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                    shareimage(activity, publication, resource);
                                }
                            });
                } else {
                    WS.taskSendImages(activity, publication);
                }
            }
        });
        dialog.show();

    }


    public static void shareimage(Activity activity, Publication publication, Bitmap bitmap) {

        OutputStream output;

        // Find the SD Card path
        File filepath = Environment.getExternalStorageDirectory();

        // Create a new folder AndroidBegin in SD Card
        File dir = new File(filepath.getAbsolutePath() + "/SocialWay/");
        dir.mkdirs();
        // Create a name for the saved image
        File file = new File(dir, "publication.png");

        try {
            // Share Intent
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/jpeg");
            output = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.flush();
            output.close();
            Uri uri = Uri.fromFile(file);
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.app_name) + " - Camino de Santiago");
            activity.startActivity(Intent.createChooser(share, activity.getString(R.string.share_publication)));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public static String formatDate(String time) {
        Log.w("MAP", time);
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "HH:mm dd-MM-yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            Log.e("MAP", "Error parsing date: " + e.getMessage());
            e.printStackTrace();
        }
        return str;
    }


    public static boolean isServiceRunning(Activity activity, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static LatLng getLocation(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences("dataUser", Context.MODE_PRIVATE);
        return new LatLng(prefs.getFloat("latitude", 42.52f), prefs.getFloat("longitude", -08.32f));
    }


    public static String getLastTimeLocation(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences("dataUser", Context.MODE_PRIVATE);
        return prefs.getString("updadePosition", "2014-11-06 11:52:52");
    }


    public static String formatDateChat(Context context, String time) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");

        Date date = null;
        String res = "";

        try {
            date = inputFormat.parse(time);

            Long diff = Calendar.getInstance().getTime().getTime() - date.getTime();//as given
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);

            if (minutes < 5)
                res = context.getString(R.string.online);
            else if (minutes < 60)
                res = context.getString(R.string.ult_vez_hace) + " " + minutes + " m";
            else if (minutes < 1440)
                res = context.getString(R.string.ult_vez_hace) + " " + (int) (minutes / 60) + " h";
            else
                res = context.getString(R.string.ult_vez_desde) + " " + time;


        } catch (ParseException e) {
            e.printStackTrace();
        }

        return res;

    }

}
