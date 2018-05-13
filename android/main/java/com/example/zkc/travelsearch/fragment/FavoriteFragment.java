package com.example.zkc.travelsearch.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.zkc.travelsearch.DetailActivity;
import com.example.zkc.travelsearch.R;
import com.example.zkc.travelsearch.adapter.ResultAdapter;
import com.example.zkc.travelsearch.entity.ResultData;

import java.util.ArrayList;
import java.util.List;


public class FavoriteFragment extends Fragment{
    public static ListView listView;
    public static List<ResultData> favorList;
    public static ResultAdapter favorAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_favorite, null);
        initView(view);
        return view;
    }
    private void initView(View view){
        listView = (ListView) view.findViewById(R.id.favoriteList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("name", favorList.get(position).getName());
                intent.putExtra("placeId", favorList.get(position).getPlaceId());
                intent.putExtra("vicinity",favorList.get(position).getVicinity());
                startActivity(intent);
            }
        });
        favorList = new ArrayList<>();
        favorAdapter = new ResultAdapter(getContext(),favorList);
        listView.setAdapter(favorAdapter);
    }
}