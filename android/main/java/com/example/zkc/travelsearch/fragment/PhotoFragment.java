package com.example.zkc.travelsearch.fragment;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;


import com.example.zkc.travelsearch.R;
import com.example.zkc.travelsearch.adapter.PhotoAdapter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class PhotoFragment extends Fragment {
    protected GeoDataClient mGeoDataClient;
    private ListView listResult;
    private List<Bitmap> bitmapList;
    private String placeId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_photo, null);
        initView(view);
        setPhotos();
        return view;
    }
    private void initView(View view){
        listResult = (ListView)view.findViewById(R.id.image_list);
        bitmapList = new ArrayList<>();
        Bundle bundle = getArguments();
        placeId = bundle.getString("placeId");
        mGeoDataClient = Places.getGeoDataClient(getContext());

    }
    private void setPhotos(){
        //String placeId = "ChIJa147K9HX3IAR-lwiGIQv9i4";
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                // Get the list of photos.
                PlacePhotoMetadataResponse photos = task.getResult();
                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                final PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                // Get photos in the list.
                if(photoMetadataBuffer.getCount() <= 0){
                    Toast.makeText(getContext(), "No Photos", Toast.LENGTH_SHORT).show();

                }else{
                    for(int i = 0; i < photoMetadataBuffer.getCount(); i++){
                        loadPhotos(photoMetadataBuffer, i, photoMetadataBuffer.getCount());
                    }
                }
                photoMetadataBuffer.release();
            }
        });
    }

    private void loadPhotos(final PlacePhotoMetadataBuffer photoMetadataBuffer, final int index,
                            final int total){
        PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(index);
        // Get the attribution text.
        CharSequence attribution = photoMetadata.getAttributions();
        //Log.v("attribution",String.valueOf(attribution));
        // Get a full-size bitmap for the photo.
        Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                PlacePhotoResponse photo = task.getResult();
                Bitmap bitmap = photo.getBitmap();
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                float scale = (float) width / height;
                int newHeight = (int)(990 / scale);
                bitmap = Bitmap.createScaledBitmap(bitmap, 990, newHeight, false);
                bitmapList.add(bitmap);
                if(bitmapList.size() == total){
                    parsePhotos();
                }
            }
        });
    }
    private void parsePhotos(){
        PhotoAdapter adapter = new PhotoAdapter(getContext(), bitmapList);
        listResult.setAdapter(adapter);
    }
}

