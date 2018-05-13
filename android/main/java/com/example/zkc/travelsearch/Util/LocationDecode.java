package com.example.zkc.travelsearch.Util;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

public class LocationDecode {
    public static LatLng getCurrentPos(){
        return new LatLng(0,0);
    }
    public static LatLng getSpecPos(String addrJson) {
        LatLng res = null;
        try {
            JSONObject obj = new JSONObject(addrJson);
            JSONObject location = obj.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
            double lat = location.getDouble("lat");
            double lon = location.getDouble("lng");
            res = new LatLng(lat, lon);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
        //Log.v("start","hello");
    }
}
