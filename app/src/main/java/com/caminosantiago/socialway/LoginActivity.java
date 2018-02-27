package com.caminosantiago.socialway;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.caminosantiago.socialway.model.ResultWS;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.UUID;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class LoginActivity extends AppCompatActivity {

    private LoginButton loginFacebookButton;
    private CallbackManager callbackManager;
    EditText editTextNombre;
    EditText editTextEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);
        editTextNombre = (EditText) findViewById(R.id.editTextU);
        editTextEmail = (EditText) findViewById(R.id.editText);

        loginFacebookButton = (LoginButton) findViewById(R.id.login_button);
        loginFacebookButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                FacebookUserGson fuser = new Gson().fromJson(object.toString(), FacebookUserGson.class);
                                registerUser(fuser);
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name,locale");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                Snackbar.make(findViewById(android.R.id.content), R.string.error_autentication, Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE).show();
            }

            @Override
            public void onError(FacebookException e) {
                Snackbar.make(findViewById(android.R.id.content), R.string.error_autentication, Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE).show();
            }


        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    public void register(View view) {
        if (editTextNombre.getText().toString().equals("") || editTextEmail.getText().toString().equals(""))
            Snackbar.make(findViewById(android.R.id.content), R.string.introducir_nombre_email, Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE).show();
        else if (!Utils.isValidEmail(editTextEmail.getText()))
            Snackbar.make(findViewById(android.R.id.content), R.string.formato_email_incorrecto, Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE).show();
        else {
            FacebookUserGson user = new FacebookUserGson(UUID.randomUUID().toString(), editTextNombre.getText().toString(), editTextEmail.getText().toString());
            registerUser(user);
        }


    }

    public void registerUser(FacebookUserGson fuser) {
        final ProgressDialog dialog = Utils.showDialog(LoginActivity.this, R.string.loading);

        String id = "";
        if (fuser.getId() != null)
            id = fuser.getId();

        String email = "";
        if (fuser.getEmail() != null)
            email = fuser.getEmail();

        String name = "";
        if (fuser.getFirst_name() != null)
            name = fuser.getFirst_name();

        String apellido = "";
        if (fuser.getLast_name() != null)
            apellido = fuser.getLast_name();

        String locale = "";
        if (fuser.getLocale() != null)
            locale = fuser.getLocale();


        final Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        MyApiEndpointInterface apiService = retrofit.create(MyApiEndpointInterface.class);
        Call<ResultWS> call = apiService.createUser(id, email, name, apellido, locale);
        call.enqueue(new Callback<ResultWS>() {
            @Override
            public void onResponse(Response<ResultWS> response, Retrofit retrofit) {
                dialog.dismiss();

                if (response.body() != null && response.body().getStatus().equals("ok")) {
                    ResultWS res = response.body();
                    Log.e("Datos de respuesta", res.getName());

                    Utils.saveUser(LoginActivity.this, res.getId(), res.getName(), null, null);

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                } else {
                    Snackbar.make(findViewById(android.R.id.content), R.string.error_autentication, Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                dialog.dismiss();
                Snackbar.make(findViewById(android.R.id.content), R.string.error_autentication, Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE).show();

            }
        });


    }


    private static class FacebookUserGson implements Serializable {
        String id, first_name, last_name, email, locale;

        public FacebookUserGson(String id, String first_name, String email) {
            this.id = id;
            this.first_name = first_name;
            this.email = email;

        }


        public String getId() {
            return id;
        }

        public String getEmail() {
            return email;
        }

        public String getFirst_name() {
            return first_name;
        }


        public String getLast_name() {
            return last_name;
        }

        public String getLocale() {
            return locale;
        }

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }

}
