package com.caminosantiago.socialway.user;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.caminosantiago.socialway.BuildConfig;
import com.caminosantiago.socialway.MyApiEndpointInterface;
import com.caminosantiago.socialway.R;
import com.caminosantiago.socialway.Utils;
import com.caminosantiago.socialway.WS;
import com.caminosantiago.socialway.chat.SalaChat;
import com.caminosantiago.socialway.comments.CommentsActivity;
import com.caminosantiago.socialway.model.ResultWS;
import com.caminosantiago.socialway.model.User;
import com.caminosantiago.socialway.model.query.ListComments;
import com.caminosantiago.socialway.model.query.UserData;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A placeholder fragment containing a simple view.
 */
public class UserFragment extends Fragment implements AdapterUser.OnInteractionHome {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String typeUser;
    private String idUser;
    Activity activity;
    ListView listView;
    View view;
    View header;
    int REQUEST_IMAGE = 2;
    int PICK_IMAGE = 1;
    File destination;
    int imageSet = 0;
    ImageView ImageViewHeader;
    ImageView iconUser;
    TextView nombreUser;
    TextView textViewEstadoUser;
    UserData userData;
    static UserFragment fragment;
    int positionSetComment = 0;
    private OnFragmentInteractionListener mListener;

    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String param1, String param2) {
        fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public UserFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            typeUser = getArguments().getString(ARG_PARAM1);
            idUser = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user, container, false);
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        TextView title1 = (TextView) toolbar.findViewById(R.id.textViewTitleApp);
        pullRefreshLayout(view);
        listView = (ListView) view.findViewById(R.id.list);
        if (idUser.equals(Utils.getUserID(activity))) {
            header = inflater.inflate(R.layout.header_my_user, listView, false);
            title1.setText(R.string.my_count);
        } else {
            header = inflater.inflate(R.layout.header_user, listView, false);
            title1.setText(R.string.app_name);
        }

        listView.addHeaderView(header, null, false);

        executeTaskGetUserData();

        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void executeTaskGetUserData() {
        final ProgressDialog dialog = Utils.showDialog(activity, R.string.loading);
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        MyApiEndpointInterface apiService = retrofit.create(MyApiEndpointInterface.class);
        Call<UserData> call = apiService.getUserData(Utils.getMyToken(activity), Utils.getUserID(activity), idUser);
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Response<UserData> response, Retrofit retrofit) {
                dialog.dismiss();
                userData = response.body();
                if (userData.getStatus().equals("ok")) {
                    if (idUser.equals(Utils.getUserID(activity)))
                        controlMyHeader(userData.getUserInfo());
                    else
                        controlHeader(userData.getUserInfo());

                    loadPublicationsUser();
                } else {
                    errorLoadDate();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                dialog.dismiss();
                errorLoadDate();
            }
        });


    }


    public void errorLoadDate() {
        final RelativeLayout layoutErrorConection = (RelativeLayout) view.findViewById(R.id.layoutErrorConection);
        layoutErrorConection.setVisibility(View.VISIBLE);
        layoutErrorConection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeTaskGetUserData();
                layoutErrorConection.setVisibility(View.GONE);
            }
        });

    }


    public void loadPublicationsUser() {

        AdapterUser mAdapter = new AdapterUser(fragment, activity, userData.getListPublication(), userData.getFavourites());
        listView.setAdapter(mAdapter);

        final LinearLayout layoutNoPublicationsMyUser = (LinearLayout) view.findViewById(R.id.layoutNoPublicationsMyUser);
        final LinearLayout layoutNoPublications = (LinearLayout) view.findViewById(R.id.layoutNoPublications);


        if (userData.getListPublication().size() == 0) {
            if (idUser.equals(Utils.getUserID(activity))) {
                layoutNoPublicationsMyUser.setVisibility(View.VISIBLE);
                layoutNoPublicationsMyUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.goToPublish();
                    }
                });
            } else {
                layoutNoPublications.setVisibility(View.VISIBLE);
            }


        } else {
            layoutNoPublications.setVisibility(View.GONE);
            layoutNoPublicationsMyUser.setVisibility(View.GONE);
        }

    }

    public void pullRefreshLayout(View view) {
        final PullRefreshLayout refreshLayout = (PullRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });
    }


    public void controlHeader(User data) {
        ImageViewHeader = (ImageView) header.findViewById(R.id.ImageViewHeader);
        Glide.with(activity).load(data.getImageFondo()).fitCenter().error(R.drawable.img_default).into(ImageViewHeader);

        iconUser = (ImageView) header.findViewById(R.id.iconUser);
        Glide.with(activity).load(data.getImageAvatar()).fitCenter().error(R.drawable.img_default).into(iconUser);

        nombreUser = (TextView) header.findViewById(R.id.textView);
        nombreUser.setText(data.getName());
        textViewEstadoUser = (TextView) header.findViewById(R.id.textViewEstadoUser);
        textViewEstadoUser.setText(data.getEstado());


        final Button buttonFollow = (Button) header.findViewById(R.id.button3);
        if (userData.getFollow() == 1)
            buttonFollow.setTextColor(getResources().getColor(R.color.blue));
        else
            buttonFollow.setTextColor(Color.BLACK);


        buttonFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userData.getFollow() == 1)
                    WS.removeFollow(activity, userData, buttonFollow);
                else
                    WS.addFollow(activity, userData, buttonFollow);

            }
        });

        Button buttonChat = (Button) header.findViewById(R.id.button4);
        buttonChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, SalaChat.class);
                i.putExtra("user", userData.getUserInfo());
                startActivity(i);
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    public void controlMyHeader(User data) {
        ImageViewHeader = (ImageView) header.findViewById(R.id.ImageViewHeader);
        Glide.with(activity).load(data.getImageFondo()).fitCenter().error(R.drawable.img_default).into(ImageViewHeader);
        ImageViewHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageSet = 1;
                selectImage();
            }
        });


        iconUser = (ImageView) header.findViewById(R.id.iconUser);
        Glide.with(activity).load(data.getImageAvatar()).asBitmap().fitCenter().error(R.drawable.default_avatar).into(new BitmapImageViewTarget(iconUser) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(activity.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                iconUser.setImageDrawable(circularBitmapDrawable);
            }
        });

        iconUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageSet = 2;
                selectImage();
            }
        });


        TextView setTexts = (TextView) header.findViewById(R.id.view);
        setTexts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTexts();
            }
        });

        nombreUser = (TextView) header.findViewById(R.id.textView);
        nombreUser.setText(data.getName());
        textViewEstadoUser = (TextView) header.findViewById(R.id.textViewEstadoUser);
        textViewEstadoUser.setText(data.getEstado());

    }


    public void selectImage() {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(true);

        final RadioButton rdGalery = (RadioButton) dialog.findViewById(R.id.radioButton1);
        RadioButton rd2 = (RadioButton) dialog.findViewById(R.id.radioButton2);
        Button button = (Button) dialog.findViewById(R.id.buttonOK);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (rdGalery.isChecked()) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, activity.getString(R.string.select_image)), PICK_IMAGE);

                } else {
                    String name = Utils.dateToString(new Date(), "yyyy-MM-dd-hh:mm:ss");
                    destination = new File(Environment.getExternalStorageDirectory(), Utils.getUserID(activity) + "SocialWay-" + name + ".jpg");
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));
                    startActivityForResult(intent, REQUEST_IMAGE);
                }
            }
        });
        dialog.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }

            try {

                InputStream inputStream = activity.getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream); //compress to which format you want.
                byte[] byte_arr = stream.toByteArray();

                if (imageSet == 1)
                    taskSetFondo(Base64.encodeToString(byte_arr, Base64.DEFAULT));
                else if (imageSet == 2)
                    taskSetAvatar(Base64.encodeToString(byte_arr, Base64.DEFAULT));


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } else if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK) {
            try {
                FileInputStream in = new FileInputStream(destination);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 10;
                Bitmap bitmap = BitmapFactory.decodeStream(in, null, options);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream); //compress to which format you want.

                byte[] byte_arr = stream.toByteArray();

                if (imageSet == 1)
                    taskSetFondo(Base64.encodeToString(byte_arr, Base64.DEFAULT));
                else if (imageSet == 2)
                    taskSetAvatar(Base64.encodeToString(byte_arr, Base64.DEFAULT));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } else if (resultCode == Activity.RESULT_OK && requestCode == 15) {
            userData.getListPublication().get(positionSetComment).setNumComments(data.getIntExtra("num", 0));
            loadPublicationsUser();
            listView.setSelection(positionSetComment + 1);
        }
    }


    public void taskSetAvatar(String data) {
        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Modificando Imagen...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        MyApiEndpointInterface apiService = retrofit.create(MyApiEndpointInterface.class);
        Call<ResultWS> call = apiService.setAvatar(Utils.getUserID(activity), data);
        call.enqueue(new Callback<ResultWS>() {
            @Override
            public void onResponse(Response<ResultWS> response, Retrofit retrofit) {
                dialog.dismiss();
                if (response.body().getStatus().equals("ok")) {
                    Utils.updateUserAvatar(activity, response.body().getData());
                    Glide.with(activity).load(response.body().getData()).asBitmap().fitCenter().error(R.drawable.default_avatar).into(new BitmapImageViewTarget(iconUser) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(activity.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            iconUser.setImageDrawable(circularBitmapDrawable);
                        }
                    });
                } else {
                    Snackbar.make(view, R.string.error_conection, Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                dialog.dismiss();
                Snackbar.make(view, R.string.error_conection, Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE).show();

            }
        });

    }

    public void taskSetFondo(String data) {

        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Modificando Imagen...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        MyApiEndpointInterface apiService = retrofit.create(MyApiEndpointInterface.class);
        Call<ResultWS> call = apiService.setImagenFondo(Utils.getUserID(activity), data);
        call.enqueue(new Callback<ResultWS>() {
            @Override
            public void onResponse(Response<ResultWS> response, Retrofit retrofit) {
                dialog.dismiss();
                if (response.body().getStatus().equals("ok")) {
                    Glide.with(activity).load(response.body().getData()).fitCenter().error(R.drawable.img_default).into(ImageViewHeader);
                } else {
                    Snackbar.make(view, R.string.error_conection, Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                dialog.dismiss();
                Snackbar.make(view, R.string.error_conection, Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE).show();

            }
        });


    }


    public void setTexts() {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_texts);
        dialog.setCancelable(true);

        final TextView nombre = (TextView) dialog.findViewById(R.id.editText4);
        nombre.setText(nombreUser.getText().toString());
        final TextView estado = (TextView) dialog.findViewById(R.id.editText3);
        estado.setText(textViewEstadoUser.getText().toString());

        Button button = (Button) dialog.findViewById(R.id.buttonOK);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (nombre.getText().toString().equals(""))
                    Toast.makeText(activity, R.string.necesario_un_nombre, Toast.LENGTH_LONG).show();
                else
                    taskSetTexts(nombre.getText().toString(), estado.getText().toString());
            }
        });
        dialog.show();
    }


    public void taskSetTexts(final String nombre, final String estado) {

        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Modificando datos...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        MyApiEndpointInterface apiService = retrofit.create(MyApiEndpointInterface.class);
        Call<ResultWS> call = apiService.setTezts(Utils.getUserID(activity), nombre, estado);
        call.enqueue(new Callback<ResultWS>() {
            @Override
            public void onResponse(Response<ResultWS> response, Retrofit retrofit) {
                dialog.dismiss();
                if (response.body().getStatus().equals("ok")) {
                    nombreUser.setText(nombre);
                    textViewEstadoUser.setText(estado);
                    Utils.updateUserName(activity, nombre);
                } else {
                    Snackbar.make(view, R.string.error_conection, Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                dialog.dismiss();
                Snackbar.make(view, R.string.error_conection, Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE).show();
            }
        });


    }

    @Override
    public void goComments(ListComments listComments, int position) {
        this.positionSetComment = position;
        Intent i = new Intent(activity, CommentsActivity.class);
        i.putExtra("data", listComments);
        startActivityForResult(i, 15);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    public interface OnFragmentInteractionListener {
        void goToPublish();
    }


}
