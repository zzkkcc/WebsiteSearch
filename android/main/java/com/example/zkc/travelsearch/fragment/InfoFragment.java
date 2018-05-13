package com.example.zkc.travelsearch.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.zkc.travelsearch.R;

import org.json.JSONObject;

public class InfoFragment extends Fragment{
    private TextView address;
    private TextView phoneNumber;
    private TextView price;
    private RatingBar rating;
    private TextView website;
    private TextView google_page;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_info, null);
        initView(view);
        Bundle bundle = getArguments();
        String info = bundle.getString("detail");
        parseString(info);
        return view;
    }

    private void initView(View view){
        address = (TextView)view.findViewById(R.id.address);
        phoneNumber = (TextView)view.findViewById(R.id.phone_number);
        price = (TextView)view.findViewById(R.id.price_level);
        rating = (RatingBar) view.findViewById(R.id.rating);
        website = (TextView)view.findViewById(R.id.website);
        google_page = (TextView)view.findViewById(R.id.google_page);
    }

    private void parseString(String str){
        try{
            JSONObject jsonObject = new JSONObject(str);
            String addrData = jsonObject.getString("addr");
            String phoneNumberData = jsonObject.getString("phone_number");
            int priceData = jsonObject.getInt("price");
            double ratingData = jsonObject.getDouble("rating");
            String websiteData = jsonObject.getString("website");
            String url = jsonObject.getString("url");

            address.setText(addrData);
            phoneNumber.setText(phoneNumberData);
            char[] priceDisplay = new char[priceData];
            for(int i = 0; i < priceData; i++){
                priceDisplay[i] = '$';
            }
            price.setText(new String(priceDisplay));
            rating.setRating((float) ratingData);
            website.setText(websiteData);
            google_page.setText(url);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
