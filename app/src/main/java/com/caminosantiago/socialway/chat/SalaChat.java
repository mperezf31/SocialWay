package com.caminosantiago.socialway.chat;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.caminosantiago.socialway.BuildConfig;
import com.caminosantiago.socialway.MyApiEndpointInterface;
import com.caminosantiago.socialway.R;
import com.caminosantiago.socialway.Utils;
import com.caminosantiago.socialway.chat.notifications.MyGcmListenerService;
import com.caminosantiago.socialway.model.User;
import com.caminosantiago.socialway.model.query.UserData;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class SalaChat extends AppCompatActivity {
	TextView titleToolbar;
	String tokenOtherUser="";
	private ListView list;
	EditText textoAEnviar;
	public static String idOtroUsuario;
	String idMyUser;
    User otherUser;
	public static boolean inFront=false;
	chatAdapter adapter;
	TextView textviewLastConexion;


	@Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.chat_sala);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        titleToolbar = (TextView) toolbar.findViewById(R.id.textViewTitleApp);
		textviewLastConexion = (TextView) toolbar.findViewById(R.id.textViewSubTitle);


		list = (ListView)findViewById(R.id.listviewSalaChat);
		textoAEnviar = (EditText)findViewById(R.id.editTextEnviar);
		idMyUser=Utils.getUserID(this);

		otherUser= (User) getIntent().getExtras().getSerializable("user");

		if (otherUser!=null){
			titleToolbar.setText(otherUser.getName());
			if(otherUser.getLastConexion()!=null && !otherUser.getLastConexion().equals("") ){
				textviewLastConexion.setVisibility(View.VISIBLE);
				textviewLastConexion.setText(Utils.formatDateChat(SalaChat.this, otherUser.getLastConexion()));
			}

			tokenOtherUser=otherUser.getToken();
			idOtroUsuario=otherUser.getId();

			mostrarConversacion();

		} else {
			executeTaskGetUserData(getIntent().getExtras().getString("idUser"));
		}

	}



	public void executeTaskGetUserData(final String idUserToChat){
		final ProgressDialog dialog = Utils.showDialog(this, R.string.loading);

		final Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
		MyApiEndpointInterface apiService =retrofit.create(MyApiEndpointInterface.class);
		Call<UserData> call = apiService.getUserDataChat(idMyUser,idUserToChat,Utils.getAllCurrentDate());
		call.enqueue(new Callback<UserData>() {
			@Override
			public void onResponse(Response<UserData> response, Retrofit retrofit) {
				if (dialog.isShowing())
					dialog.dismiss();

				if (response.body()!=null && response.body().getStatus().equals("ok")) {
					otherUser =response.body().getUserInfo();
					titleToolbar.setText(otherUser.getName());
					if(otherUser.getLastConexion()!=null && !otherUser.getLastConexion().equals("")){
						textviewLastConexion.setVisibility(View.VISIBLE);
						textviewLastConexion.setText(Utils.formatDateChat(SalaChat.this, otherUser.getLastConexion()));
					}

					tokenOtherUser=otherUser.getToken();
					idOtroUsuario=otherUser.getId();

					mostrarConversacion();

				} else {
					errorLoadDate(idUserToChat);
				}

			}

			@Override
			public void onFailure(Throwable t) {
				if (dialog.isShowing())
					dialog.dismiss();
				errorLoadDate(idUserToChat);
			}
		});


	}


	public void errorLoadDate(final String idUserToChat) {
		final RelativeLayout layoutErrorConection = (RelativeLayout)findViewById(R.id.layoutErrorConection);
		layoutErrorConection.setVisibility(View.VISIBLE);
		layoutErrorConection.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				executeTaskGetUserData(idUserToChat);
				layoutErrorConection.setVisibility(View.GONE);
			}
		});

	}


	public void mostrarConversacion(){
    	AdminSQLiteOpenHelper adm = new AdminSQLiteOpenHelper(this,"chat", null, 1);
        SQLiteDatabase bd = adm.getWritableDatabase();
		Cursor fila = bd.rawQuery("select id,user, msg,dateChat  from conversaciones where user='" + idOtroUsuario + "' or user='" + (idMyUser + idOtroUsuario) + "' ", null);

		ArrayList<ItemsPersonalizados> items = new ArrayList<ItemsPersonalizados>();

		 if(fila.getCount()!=0){    

		        if(fila.moveToFirst()){
		            do{
		            	if(fila.getString(1).equals(idOtroUsuario)){
		            		items.add(new ItemsPersonalizados(fila.getString(2),"",fila.getString(3)));
		            	}else{
		            		items.add(new ItemsPersonalizados("",fila.getString(2),fila.getString(3)));
		            	}
		 		  		Log.i("->>", "" + fila.getString(1) + "-" + fila.getString(2) + "");
		            }while(fila.moveToNext());
		        }

		}
		 
		 bd.close();

		list.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		list.setStackFromBottom(true);
		adapter = new chatAdapter(this, items);
		list.setAdapter(adapter);
		 

    }



	public boolean isOnline() {
		ConnectivityManager cm =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting();
	}


	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				this.finish();
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}

	}



	@Override
	public void onResume() {
		super.onResume();
		inFront=true;
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(MyGcmListenerService.UPDATE_CHAT));
	}

	@Override
	protected void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
		inFront=false;
	}

	// handler for received Intents for the "my-event" event
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String message = intent.getStringExtra("message");
			textviewLastConexion.setText(Utils.formatDateChat(SalaChat.this, Utils.getAllCurrentDate()));
			Log.d("receiver", "Got message: " + message);
			adapter.add(new ItemsPersonalizados(message, "",Utils.getCurrentDate()));
		}
	};




	public void enviar(View v){
		String text=textoAEnviar.getText().toString().trim();
    	if(text.length()>0){
			if (isOnline() && tokenOtherUser != null && !tokenOtherUser.equals("")){
				new TheTaskEnviartexto().execute(text);
			}else
				Toast.makeText(this, R.string.error_conection, Toast.LENGTH_LONG).show();

		}
    }




	private class TheTaskEnviartexto extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String res="error";
			try {

				Sender sender = new Sender("AIzaSyCLDsEfIe5vlFLxDRwHmGXmjB9PWD5gxt4");
				ArrayList<String> devicesList = new ArrayList<String>();
				//add you deviceI
				devicesList.add(tokenOtherUser);
				// use this line to send message with payload data
				Message message = new Message.Builder().delayWhileIdle(true).addData("msg", params[0]).addData("myIdUser", idMyUser).addData("nameUser", Utils.getUserID(SalaChat.this)).addData("idUser",otherUser.getId()).build();
				// Use this for multicast messages
				MulticastResult result = sender.send(message, devicesList, 1);

				if (result.getResults() != null) {
					AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(SalaChat.this,"chat", null, 1);
					SQLiteDatabase bd = admin.getWritableDatabase();
					ContentValues registro = new ContentValues();
					registro.put("user",(idMyUser+idOtroUsuario));
					registro.put("msg", params[0]);
					registro.put("dateChat", Utils.getCurrentDate());
					bd.insert("conversaciones", null, registro);
					bd.close();
					Log.v("Hilo enviar tecto", "-" + result + "-");
					return "ok";
				}

			} catch (Exception e) {  e.printStackTrace(); }

			return res;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.equals("ok")){
				adapter.add(new ItemsPersonalizados("", textoAEnviar.getText().toString(), Utils.getCurrentDate()));
				textoAEnviar.setText("");
			}else{
				Toast.makeText(SalaChat.this, R.string.error_conection, Toast.LENGTH_LONG).show();
			}

		}

		@Override
		protected void onPreExecute() {}

		@Override
		protected void onProgressUpdate(Void... values) {}
	}





}
