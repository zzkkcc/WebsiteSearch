package com.example.zkc.travelsearch.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.zkc.travelsearch.R;
import com.example.zkc.travelsearch.adapter.ReviewAdapter;
import com.example.zkc.travelsearch.entity.ReviewData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ReviewFragment extends Fragment{

    private Spinner reviewType;
    private Spinner reviewOrder;

    private List<String> orderList;

    private ListView reviewListview;

    ReviewHolder google;
    ReviewHolder yelp;

    int cur;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_review, null);
        init(view);
        Bundle bundle = getArguments();
        String google = bundle.getString("GoogleReview");
        parseGoogleReview(google);
        String yelp = bundle.getString("YelpReview");
        parseYelpReivew(yelp);
        //Toast.makeText(getContext(), review, Toast.LENGTH_SHORT).show();
        return view;
    }

    private void init(View view){
        google = new ReviewHolder();
        yelp = new ReviewHolder();
        cur = 0;

        reviewType = (Spinner) view.findViewById(R.id.review_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.review_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reviewType.setAdapter(adapter);
        reviewType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    reviewListview.setAdapter(google.getAdapter());
                    cur = 0;
                }else{
                    reviewListview.setAdapter(yelp.getAdapter());
                    cur = 1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                reviewListview.setAdapter(google.getAdapter());
            }
        });

        orderList = new ArrayList<>();
        orderList.add("Default order");
        orderList.add("Highest rating");
        orderList.add("Loweast rating");
        orderList.add("Most recent");
        orderList.add("Least recent");
        reviewOrder = (Spinner) view.findViewById(R.id.review_order);
        ArrayAdapter<String> orderAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, orderList);
        orderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reviewOrder.setAdapter(orderAdapter);
        reviewOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ReviewHolder now = cur == 0 ? google: yelp;

                switch (position){
                    case 0:
                        Collections.sort(now.getList(), new Comparator<ReviewData>(){
                            @Override
                            public int compare(ReviewData one, ReviewData two){
                                if(one.getPosition() == two.getPosition()){
                                    return 0;
                                }
                                return one.getPosition() < two.getPosition() ? -1: 1;
                            }
                        });
                        reviewListview.setAdapter(now.getAdapter());
                        break;
                    case 1:
                        Collections.sort(now.getList(), new Comparator<ReviewData>(){
                            @Override
                            public int compare(ReviewData one, ReviewData two){
                                if(one.getRating() == two.getRating()){
                                    return 0;
                                }
                                return one.getRating() > two.getRating() ? -1: 1;
                            }
                        });
                        reviewListview.setAdapter(now.getAdapter());
                        break;
                    case 2:
                        Collections.sort(now.getList(), new Comparator<ReviewData>(){
                            @Override
                            public int compare(ReviewData one, ReviewData two){
                                if(one.getRating() == two.getRating()){
                                    return 0;
                                }
                                return one.getRating() < two.getRating() ? -1: 1;
                            }
                        });
                        reviewListview.setAdapter(now.getAdapter());
                        break;
                    case 3:
                        Collections.sort(now.getList(), new Comparator<ReviewData>(){
                            @Override
                            public int compare(ReviewData one, ReviewData two){
                                Date fir = convertDate(one.getDate());
                                Date sec = convertDate(two.getDate());
                                if(fir.equals(sec)){
                                    return 0;
                                }
                                return fir.after(sec) ? -1: 1;
                            }
                        });
                        reviewListview.setAdapter(now.getAdapter());
                        break;
                    case 4:
                        Collections.sort(now.getList(), new Comparator<ReviewData>(){
                            @Override
                            public int compare(ReviewData one, ReviewData two){
                                Date fir = convertDate(one.getDate());
                                Date sec = convertDate(two.getDate());
                                if(fir.equals(sec)){
                                    return 0;
                                }
                                return fir.before(sec) ? -1: 1;
                            }
                        });
                        reviewListview.setAdapter(now.getAdapter());
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        reviewListview = (ListView)view.findViewById(R.id.review_list);

    }
    private void parseYelpReivew(String json){
        try{
            JSONArray array = new JSONArray(json);
            for(int i = 0; i < array.length(); i++){
                ReviewData data = new ReviewData();
                data.setPosition(i);
                JSONObject obj = (JSONObject) array.get(i);
                data.setAuthor(obj.getJSONObject("user").getString("name"));
                data.setHeadUrl(obj.getJSONObject("user").getString("image_url"));
                data.setDate(obj.getString("time_created"));
                data.setRating(obj.getDouble("rating"));
                data.setContent(obj.getString("text"));
                data.setUrl(obj.getString("url"));
                yelp.getList().add(data);
            }
            yelp.setAdapter(getContext());
            //yelpListview.setAdapter(adapter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void parseGoogleReview(String json){
        try{
            JSONArray array = new JSONArray(json);
            for(int i = 0; i < array.length(); i++){
                JSONObject obj = (JSONObject) array.get(i);
                ReviewData data = new ReviewData();
                data.setPosition(i);
                data.setAuthor(obj.getString("author_name"));
                data.setHeadUrl(obj.getString("profile_photo_url"));
                data.setDate(obj.getLong("time"));
                data.setRating(obj.getDouble("rating"));
                data.setContent(obj.getString("text"));
                data.setUrl(obj.getString("author_url"));
                google.getList().add(data);
            }
            google.setAdapter(getContext());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private Date convertDate(String time){
        Date date = null;
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try{
            date = fmt.parse(time);
        }catch(Exception e){
            e.printStackTrace();
        }

        return date;
    }
    class ReviewHolder{
        private List<ReviewData> list;
        private ReviewAdapter adapter;

        public ReviewHolder(){
            list = new ArrayList<>();
        }
        public void setAdapter(Context context){
            adapter = new ReviewAdapter(context, list);
        }

        public ReviewAdapter getAdapter() {
            return adapter;
        }

        public List<ReviewData> getList() {
            return list;
        }
    }
}
