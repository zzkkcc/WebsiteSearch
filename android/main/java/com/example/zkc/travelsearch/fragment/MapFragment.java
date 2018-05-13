package com.example.zkc.travelsearch.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.zkc.travelsearch.R;
import com.example.zkc.travelsearch.Util.LocationDecode;
import com.example.zkc.travelsearch.Util.PolylineDecoder;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;

import java.util.ArrayList;
import java.util.List;
import android.os.Handler;

import org.json.JSONArray;
import org.json.JSONObject;

public class MapFragment extends Fragment{
    private GoogleMap googleMap;
    private List<String> paths;
    private LatLng start;
    private LatLng end;
    private Spinner travelMode;
    private List<String> travelList;
    private EditText inputStart;
    private Handler handler;
    private String input;
    private String selectedTravel;


    private Runnable delayRun = new Runnable() {
        @Override
        public void run() {
            setStartPoint();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_map, null);
        Bundle bundle = getArguments();
        String location = bundle.getString("location");
        parseLoc(location);
        initView(view);
        return view;
    }

    private void setStartPoint(){
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" +
                input + "&key=AIzaSyAOunfFzF-SKgiujodINlKnepIU8Eh9TNk";
        RxVolley.get(url, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                start = LocationDecode.getSpecPos(t);
                String url = getDirectionsUrl(start, end);
                RxVolley.get(url, new HttpCallback(){
                    @Override
                    public void onSuccess(String t) {
                        parseDirJson(t);
                        googleMap.clear();
                        googleMap.addMarker(new MarkerOptions().position(end).title("end in " + end.toString()));
                        displayDirction();
                    }
                });
            }
        });
    }

    private void initView(View view){

        travelMode = (Spinner)view.findViewById(R.id.spinner_direction);

        travelList = new ArrayList<>();
        travelList.add("walking");
        travelList.add("biking");
        travelList.add("driving");
        travelList.add("transit");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, travelList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        travelMode.setAdapter(adapter);
        travelMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTravel = travelList.get(position);
                if(start != null){
                    String url = getDirectionsUrl(start, end);
                    RxVolley.get(url, new HttpCallback(){
                        @Override
                        public void onSuccess(String t) {
                            parseDirJson(t);
                            googleMap.clear();
                            googleMap.addMarker(new MarkerOptions().position(end).title("end in " + end.toString()));
                            displayDirction();
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        handler = new Handler();
        inputStart = (EditText)view.findViewById(R.id.start_location);
        inputStart.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(delayRun!=null){
                    //每次editText有变化的时候，则移除上次发出的延迟线程
                    handler.removeCallbacks(delayRun);
                }
                input = s.toString();
                //延迟1000ms，如果不再输入字符，则执行该线程的run方法
                handler.postDelayed(delayRun, 1000);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.mapSection);
        mapFragment.getMapAsync(new OnMapReadyCallback(){
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;
                //Add a marker in start and move the camera
                googleMap.addMarker(new MarkerOptions().position(end).title("end in " + end.toString()));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(end));
                googleMap.setMinZoomPreference(10f);
            }
        });
    }
    private void parseLoc(String loc){
        try{
            JSONObject obj = new JSONObject(loc);
            double lat = obj.getDouble("lat");
            double lon = obj.getDouble("lng");
            end = new LatLng(lat, lon);
            Log.v("position",String.valueOf(lat));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + ","
                + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Travelling Mode
        String mode = "mode=" + selectedTravel;

        // 如果使用途径点，需要添加此字段
        // String waypoints = "waypoints=";

        String parameters = null;
        // Building the parameters to the web service

        parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;
        // parameters = str_origin + "&" + str_dest + "&" + sensor + "&"
        // + mode+"&"+waypoints;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + parameters;
        return url;
    }

    private void parseDirJson(String json){
        try{
            JSONObject object = new JSONObject(json);
            JSONArray jsonArray = object.getJSONArray("routes").getJSONObject(0).
                    getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
            paths = new ArrayList<>();
            for(int i = 0; i < jsonArray.length(); i++){
                getPath(jsonArray.getJSONObject(i));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void getPath(JSONObject object){
        try{
            String polyline = object.getJSONObject("polyline").getString("points");
            paths.add(polyline);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void displayDirction(){
        googleMap.addMarker(new MarkerOptions().position(start).title("start in " + start.toString()));
        PolylineOptions options = new PolylineOptions();
        for(int i = 0; i < paths.size(); i++){
            options.color(Color.BLUE);
            options.width(10);
            List<LatLng> points = PolylineDecoder.decode(paths.get(i));
            for(int j = 0; j < points.size(); j++){
                options.add(points.get(j));
            }
            googleMap.addPolyline(options);
        }
    }
}
