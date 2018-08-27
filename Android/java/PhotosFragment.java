package com.example.zyz.hw9android;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.List;


public class PhotosFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener{
    private final String TAG = "PhotosFragment";

    private View parentView;
    private GoogleApiClient mGoogleApiClient;
    private ImageView mImageView;
    List photosBitmap;
    private RecyclerView photoRecyclerView;
    private RecyclerView.LayoutManager photoLayoutManager;
    private RecyclerView.Adapter adapter;
    private TextView noPhotosView;



    public PhotosFragment() {
        // Required empty public constructor
    }

    public static PhotosFragment newInstance(String placeid) {
        PhotosFragment fragment = new PhotosFragment();
        Bundle args = new Bundle();
        args.putString("place_id",placeid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentView =  inflater.inflate(R.layout.fragment_photos, container, false);
        initView();
        return parentView;
    }

    private void initView() {

        photoRecyclerView = (RecyclerView) parentView.findViewById(R.id.photo_view);
        photoRecyclerView.setHasFixedSize(true);
        photoLayoutManager = new LinearLayoutManager(getActivity());
        photoRecyclerView.setLayoutManager(photoLayoutManager);
        photosBitmap = new ArrayList<>();
        adapter = new PhotoAdapter(photosBitmap);
        photoRecyclerView.setAdapter(adapter);

        noPhotosView = (TextView) parentView.findViewById(R.id.no_photos);
        noPhotosView.setVisibility(View.GONE);
        Log.i(TAG,"initVIEw");
            try{
                mGoogleApiClient = new GoogleApiClient
                    .Builder(getActivity())
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .enableAutoManage(getActivity(), this)
                    .build();
                Log.e(TAG, "mgoogle ok");
            }catch (Exception e){
               Log.e(TAG,"mgoogle error");
                e.printStackTrace();
            }
        mImageView = (ImageView) parentView.findViewById(R.id.photo_img);
        placePhotosAsync();
    }

    private ResultCallback<PlacePhotoResult> mDisplayPhotoResultCallback
            = new ResultCallback<PlacePhotoResult>() {
        @Override
        public void onResult(PlacePhotoResult placePhotoResult) {
            if (!placePhotoResult.getStatus().isSuccess()) {
                Log.e(TAG,"no placephoto success" );
                return;
            }
            Log.i(TAG,"mdisplay");
            Bitmap b = placePhotoResult.getBitmap();
            photosBitmap.add(b);
            adapter.notifyDataSetChanged();
        }
    };

    private void placePhotosAsync() {
        Log.e(TAG, "placePhotoASYNC");
        Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, getArguments().getString("place_id"))
                .setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {

                    @Override
                    public void onResult(PlacePhotoMetadataResult photos) {
                        if(photos == null){
                            Log.e(TAG, "empty photo");

                        }
                        if (!photos.getStatus().isSuccess()) {
                            Log.e(TAG, "no photo success:" + photos.getStatus().isSuccess());
                            return;
                        }

                        PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                        if (photoMetadataBuffer.getCount() > 0) {
                            // Display the first bitmap in an ImageView in the size of the view
                            Log.i(TAG, "buffer ok");
                            for(int i = 0; i < photoMetadataBuffer.getCount();i++) {
                                photoMetadataBuffer.get(i)
                                        .getPhoto(mGoogleApiClient)
                                        .setResultCallback(mDisplayPhotoResultCallback);
                            }
                        }else{
                            noPhotosView.setVisibility(View.VISIBLE);
                            photoRecyclerView.setVisibility(View.GONE);
                        }
                        photoMetadataBuffer.release();
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "on connectionfailed google client");
    }

}
