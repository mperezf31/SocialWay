package com.caminosantiago.socialway.loadPublication;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.caminosantiago.socialway.BuildConfig;
import com.caminosantiago.socialway.MyApiEndpointInterface;
import com.caminosantiago.socialway.R;
import com.caminosantiago.socialway.Utils;
import com.caminosantiago.socialway.model.LoadImage;
import com.caminosantiago.socialway.model.ResultWS;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class LoadPublicationFragment extends Fragment {

    EditText editTextDescripcion;
    GoogleMap googleMap;
    Marker marker;
    int REQUEST_IMAGE = 2;
    int PICK_IMAGE = 1;
    File destination;
    List<String> listImages = new ArrayList<>();
    List<ImageView> listImageViews;
    LinearLayout layoutImages1;
    LinearLayout layoutImages2;
    View view;

    private OnFragmentInteractionListener mListener;

    public static LoadPublicationFragment newInstance() {
        LoadPublicationFragment fragment = new LoadPublicationFragment();
        return fragment;
    }

    public LoadPublicationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_load_publication, container, false);
        controlToolbar();
        start();

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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

    public void controlToolbar() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        TextView title1 = (TextView) toolbar.findViewById(R.id.textViewTitleApp);
        title1.setText(R.string.add_publication);
    }

    protected void start() {

        editTextDescripcion = (EditText) view.findViewById(R.id.editTextDescripcion);
        final View selectImage = view.findViewById(R.id.select_image);
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        final View addLocation = view.findViewById(R.id.layoutAddLocation);
        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLocation();
            }
        });

        final View buttonSendPost = view.findViewById(R.id.buttonSendPost);
        buttonSendPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonEnviar();
            }
        });

        googleMap = ((ScrollMap) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                googleMap.clear();
                marker = googleMap.addMarker(new MarkerOptions().position(point));
            }
        });
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return true;
            }
        });

        scrollMap();

        layoutImages1 = (LinearLayout) view.findViewById(R.id.layoutImages1);
        layoutImages1.setVisibility(View.GONE);
        layoutImages2 = (LinearLayout) view.findViewById(R.id.layoutImages2);
        layoutImages2.setVisibility(View.GONE);


        ImageView imageViewPost1 = (ImageView) view.findViewById(R.id.imageViewPost1);
        ImageView imageViewPost2 = (ImageView) view.findViewById(R.id.imageViewPost2);
        ImageView imageViewPost3 = (ImageView) view.findViewById(R.id.imageViewPost3);
        ImageView imageViewPost4 = (ImageView) view.findViewById(R.id.imageViewPost4);
        ImageView imageViewPost5 = (ImageView) view.findViewById(R.id.imageViewPost5);
        ImageView imageViewPost6 = (ImageView) view.findViewById(R.id.imageViewPost6);
        listImageViews = new ArrayList<>();
        listImageViews.add(imageViewPost1);
        listImageViews.add(imageViewPost2);
        listImageViews.add(imageViewPost3);
        listImageViews.add(imageViewPost4);
        listImageViews.add(imageViewPost5);
        listImageViews.add(imageViewPost6);

    }


    public void scrollMap() {
        final ScrollView mScrollView = (ScrollView) view.findViewById(R.id.scrollView);
        ((ScrollMap) getChildFragmentManager().findFragmentById(R.id.map)).setListener(new ScrollMap.OnTouchListener() {
            @Override
            public void onTouch() {
                mScrollView.requestDisallowInterceptTouchEvent(true);
            }
        });
    }

    public void addLocation() {
        googleMap.clear();

        try {
            String inputPattern = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
            Date lastDate = inputFormat.parse(Utils.getLastTimeLocation(getActivity()));
            Date currentDate = new Date();
            currentDate.setTime(System.currentTimeMillis() - (60 * 60 * 1000));

            if (currentDate.after(lastDate))
                Toast.makeText(getActivity(), R.string.active_gps_location, Toast.LENGTH_LONG).show();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        LatLng location = Utils.getLocation(getActivity());
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        marker = googleMap.addMarker(new MarkerOptions().position(location));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 5));

    }


    public void selectImage() {

        if (listImages.size() == 6) {
            Toast.makeText(getActivity(), R.string.not_add_more_images, Toast.LENGTH_LONG).show();
        } else {
            final Dialog dialog = new Dialog(getActivity());
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
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);

                    } else {
                        String name = Utils.dateToString(new Date(), "yyyy-MM-dd-hh:mm:ss");
                        destination = new File(Environment.getExternalStorageDirectory(), Utils.getUserID(getActivity()) + "SocialWay-" + name + ".jpg");
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));
                        startActivityForResult(intent, REQUEST_IMAGE);
                    }
                }
            });
            dialog.show();
        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }

            try {

                InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream); //compress to which format you want.

                byte[] byte_arr = stream.toByteArray();
                if (listImages.size() == 0)
                    layoutImages1.setVisibility(View.VISIBLE);
                else if (listImages.size() == 3)
                    layoutImages2.setVisibility(View.VISIBLE);

                listImages.add(Base64.encodeToString(byte_arr, Base64.DEFAULT));

                //Añadimos la imágen i ponemos el boton para poder eliminarla
                showImage(bitmap);


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
                if (listImages.size() == 0)
                    layoutImages1.setVisibility(View.VISIBLE);
                else if (listImages.size() == 3)
                    layoutImages2.setVisibility(View.VISIBLE);
                listImages.add(Base64.encodeToString(byte_arr, Base64.DEFAULT));

                //Añadimos la imágen i ponemos el boton para poder eliminarla
                showImage(bitmap);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }


    public void showImage(Bitmap bitmap) {
        ImageView imageView = listImageViews.get(listImages.size() - 1);
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageBitmap(bitmap);
    }


    public void buttonEnviar() {

        if (listImages.size() == 0) {
            Toast.makeText(getActivity(), R.string.add_one_image, Toast.LENGTH_LONG).show();
        } else {
            if (marker == null) {
                taskSendImages(new LoadImage(Utils.getUserID(getActivity()), editTextDescripcion.getText().toString().trim(), (double) 0, (double) 0, listImages));
            } else {
                taskSendImages(new LoadImage(Utils.getUserID(getActivity()), editTextDescripcion.getText().toString().trim(), marker.getPosition().latitude, marker.getPosition().longitude, listImages));
            }
        }
    }


    public void taskSendImages(LoadImage data) {

        final ProgressDialog dialog = Utils.showDialog(getActivity(), R.string.enviando_publication);

        final Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        MyApiEndpointInterface apiService = retrofit.create(MyApiEndpointInterface.class);
        Call<ResultWS> call = apiService.sendPublication(data);
        call.enqueue(new Callback<ResultWS>() {
            @Override
            public void onResponse(Response<ResultWS> response, Retrofit retrofit) {
                dialog.hide();
                if (response.body().getStatus().equals("ok")) {
                    Toast.makeText(getActivity(), R.string.publication_ok, Toast.LENGTH_LONG).show();
                    mListener.goToProfile();


                } else {
                    Toast.makeText(getActivity(), R.string.error_send_publication, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("Error", t.getMessage());

                dialog.hide();
                Toast.makeText(getActivity(), R.string.error_send_publication, Toast.LENGTH_LONG).show();
            }
        });

    }


    public interface OnFragmentInteractionListener {
        void goToProfile();
    }

}
