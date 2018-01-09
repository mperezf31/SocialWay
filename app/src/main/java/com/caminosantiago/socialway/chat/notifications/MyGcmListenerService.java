package com.caminosantiago.socialway.chat.notifications;

/**
 * Created by admin on 21/11/2015.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.caminosantiago.socialway.MainActivity;
import com.caminosantiago.socialway.R;
import com.caminosantiago.socialway.Utils;
import com.caminosantiago.socialway.chat.AdminSQLiteOpenHelper;
import com.caminosantiago.socialway.chat.SalaChat;
import com.google.android.gms.gcm.GcmListenerService;

import java.util.Date;

public class MyGcmListenerService extends GcmListenerService {

    public static String UPDATE_CHAT="update_chat";
    private static final String TAG = "MyGcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String type= data.getString("type",null);

        if (type!=null && type.equals("comment")){
            //Notificacion de comentario en tu publicación
            String idPublication = data.getString("idPublication");
            String msg = data.getString("msg");
            String title = data.getString("title");
            sendNotificationComment(idPublication,msg,title);

        }else{
            //mensaje de char
            String msg = data.getString("msg");
            String idUser = data.getString("idUser");
            String myIdUser = data.getString("myIdUser");
            String nameUser = data.getString("nameUser");
            saveChat(myIdUser, msg);


            if (SalaChat.inFront && SalaChat.idOtroUsuario.equals(myIdUser)){
                sendMessage(msg);
            }else
                sendNotification(msg,myIdUser,nameUser);
        }
    }

    // Send an Intent with an action named "my-event".
    private void sendMessage(String msg) {
        Intent intent = new Intent(UPDATE_CHAT);
        intent.putExtra("message", msg);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    public void saveChat(String idUser,String msg){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"chat", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        ContentValues registro = new ContentValues();
        registro.put("user",idUser);
        registro.put("dateChat", Utils.getCurrentDate());
        registro.put("msg",msg);
        bd.insert("conversaciones", null, registro);
        bd.close();
    }

    private void sendNotificationComment(String idPublication,String msg,String title) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("idPublication",idPublication);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(getNotificationIcon())
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle(title)
                .setContentText(msg)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(getIDNotification(), notificationBuilder.build());
    }

    private void sendNotification(String message,String idUser,String nameUser) {
        Intent intent = new Intent(this, SalaChat.class);
        intent.putExtra("idUser",idUser);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(getNotificationIcon())
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle(nameUser)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(getIDNotification(), notificationBuilder.build());
    }


    public int getIDNotification(){
        long time = new Date().getTime();
        String tmpStr = String.valueOf(time);
        String last4Str = tmpStr.substring(tmpStr.length() - 5);
        int notificationId = Integer.valueOf(last4Str);
        return notificationId;
    }

    private int getNotificationIcon() {
        boolean whiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return whiteIcon ? R.drawable.icon_notification : R.mipmap.ic_launcher;
    }
}
