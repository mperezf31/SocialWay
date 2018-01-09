package com.caminosantiago.socialway.comments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.caminosantiago.socialway.BuildConfig;
import com.caminosantiago.socialway.MyApiEndpointInterface;
import com.caminosantiago.socialway.R;
import com.caminosantiago.socialway.Utils;
import com.caminosantiago.socialway.home.HomeFollowingsFragment;
import com.caminosantiago.socialway.home.HomeFragment;
import com.caminosantiago.socialway.home.TuCaminoFragment;
import com.caminosantiago.socialway.model.Comment;
import com.caminosantiago.socialway.model.query.ListComments;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class CommentsActivity extends AppCompatActivity {

    ListComments listComments;
    EditText editTextComment;
    int numComments=0;
    int numPubli;
    int procedencia;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView title1 = (TextView) toolbar.findViewById(R.id.textViewTitleApp);
        title1.setText("Comentarios");
        listComments= (ListComments) getIntent().getExtras().getSerializable("data");
        numPubli= getIntent().getExtras().getInt("numPubli");
        procedencia= getIntent().getExtras().getInt("procedencia");
        editTextComment= (EditText) findViewById(R.id.editText2);
        numComments=listComments.getComments().size();
        showComments(listComments.getComments());


    }


    public void showComments(List<Comment> listComments){
        ListView list = (ListView)findViewById(R.id.listView);
        TextView textoNoCommments = (TextView) findViewById(R.id.textView11);
        if (listComments.size()==0)
            textoNoCommments.setVisibility(View.VISIBLE);
        else
            textoNoCommments.setVisibility(View.GONE);

        AdapterComment mAdapter = new AdapterComment(this, listComments);
        list.setAdapter(mAdapter);
    }





    public void sendComment(View view){
        View viewKey = this.getCurrentFocus();
        if (viewKey != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(viewKey.getWindowToken(), 0);
        }

        if (editTextComment.getText().toString().equals(""))
            Snackbar.make(findViewById(android.R.id.content), R.string.must_add_comment, Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE).show();
        else
             taskSendComment(editTextComment.getText().toString());



    }


    public void taskSendComment(String msg){

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Enviando comentario...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        MyApiEndpointInterface apiService =retrofit.create(MyApiEndpointInterface.class);
        Call<ListComments> call = apiService.addComments(Utils.getUserID(CommentsActivity.this), listComments.getIdPublication(), msg);
        call.enqueue(new Callback<ListComments>() {
            @Override
            public void onResponse(Response<ListComments> response, Retrofit retrofit) {
                dialog.dismiss();
                if (response.body().getStatus().equals("ok")) {
                    editTextComment.setText("");
                    numComments=response.body().getComments().size();
                    showComments(response.body().getComments());
                    if (procedencia==1)
                        HomeFragment.listPublications.get(numPubli).setNumComments(numComments);
                    else if(procedencia==2)
                        HomeFollowingsFragment.listPublications.get(numPubli).setNumComments(numComments);
                    else if(procedencia==3)
                        TuCaminoFragment.listPublications.get(numPubli).setNumComments(numComments);


                } else {
                    Snackbar.make(findViewById(android.R.id.content), R.string.error_conection, Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                dialog.dismiss();
                Snackbar.make(findViewById(android.R.id.content), R.string.error_conection, Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE).show();
            }
        });

    }







    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("num", numComments);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
