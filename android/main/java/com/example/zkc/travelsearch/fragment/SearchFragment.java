package com.example.zkc.travelsearch.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.zkc.travelsearch.NoResultActivity;
import com.example.zkc.travelsearch.R;
import com.example.zkc.travelsearch.ResultActivity;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;

import org.json.JSONObject;

public class SearchFragment extends Fragment implements View.OnClickListener{
    private Button btn_search;
    private Button btn_clear;

    private EditText keywordText;
    private Spinner category;
    private EditText distanceText;
    private RadioButton radioCurrent;
    private EditText otherText;

    private String type;
    private boolean curLocation = true;
    private String lat;
    private String lon;

    private ProgressDialog pdDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_search, null);
        keywordText = (EditText)view.findViewById(R.id.keyword_text);
        category = (Spinner) view.findViewById(R.id.spinner);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        distanceText = (EditText)view.findViewById(R.id.distance_text);
        radioCurrent = (RadioButton) view.findViewById(R.id.radioCurrent);
        radioCurrent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                curLocation = isChecked;
            }
        });
        otherText = (EditText)view.findViewById(R.id.other_text);
        findView(view);
        return view;
    }
    private void stopProgressBar(){
        pdDialog.cancel();
    }
    private void runProgressBar(){
        pdDialog = new ProgressDialog(getContext());
        pdDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // 设置ProgressDialog 的进度条是否不明确
        pdDialog.setIndeterminate(false);
        //pdDialog.setMessage("正在下载中……");
        pdDialog.setMessage("fecthing data……");
        // 让ProgressDialog显示
        pdDialog.show();

    }
    private void findView(View view){
        btn_search = (Button) view.findViewById(R.id.btn_search);
        btn_search.setOnClickListener(this);
        btn_clear = (Button) view.findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(this);
    }

    private void validConent(){

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                //1.获取API内容,搜索结果内容
                clear();
                final String keyword = keywordText.getText().toString().trim();
                type = type.replace(" ","");
                String distance;
                try {
                    distance = distanceText.getText().toString().trim();
                }catch (Exception e){
                    distance = "10";
                }
                if(distance.length() == 0){
                    distance = "10";
                }
                if(keyword == null || keyword.length() == 0){
                    Toast.makeText(getContext(), "Keyword cannot be Empty", Toast.LENGTH_SHORT).show();
                    break;
                }
                String otherPlace = otherText.getText().toString().trim();
                if(!curLocation && otherPlace.length() == 0){
                    Toast.makeText(getContext(), "situation cannot be Empty", Toast.LENGTH_SHORT).show();
                    break;
                }
                if(curLocation){
                    final String finalDistance = distance;
                    RxVolley.get("http://www.ip-api.com/json", new HttpCallback() {
                        @Override
                        public void onSuccess(String t) {
                            parseCurLoc(t);
                            runProgressBar();
                            String url = buildUrl(keyword, lat, lon, finalDistance, type);
                            startSearch(url);
                        }
                    });
                }else{
                    final String finalDistance1 = distance;
                    String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + otherPlace
                            + "&key=AIzaSyAOunfFzF-SKgiujodINlKnepIU8Eh9TNk";
                    RxVolley.get(url, new HttpCallback() {
                        @Override
                        public void onSuccess(String t) {
                            parseSpecLoc(t);
                            runProgressBar();
                            String uurrll = buildUrl(keyword, lat, lon, finalDistance1, type);
                            Log.v("uurrrlll",uurrll);
                            startSearch(uurrll);
                        }
                    });
                }
                //3.拼接url
                //4.解析JSON
                //5.ListView适配器
                //6.实体类(item布局)
                //7.设置数据／显示效果
                break;
            case R.id.btn_clear:
                clear();
                clearNum();
                break;
        }
    }
    private String buildUrl(String keyword, String lat, String lon, String radius, String type){
        String url = "http://csci571-php-hw8.us-east-2.elasticbeanstalk.com/hw8.php?keyword="
                +keyword + "&location=" + lat + "," + lon + "&radius=" + radius + "&type=" + type;
        return url;
    }
    private void startSearch(String url){
        RxVolley.get(url, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                stopProgressBar();
                String status = null;
                try {
                    JSONObject obj = new JSONObject(t);
                    status = obj.getString("status");
                }catch (Exception e){
                    startActivity(new Intent(getActivity(), NoResultActivity.class));
                }
                if(status.equals("ZERO_RESULTS")){
                    startActivity(new Intent(getActivity(), NoResultActivity.class));
                }
                Intent intent = new Intent(getActivity(), ResultActivity.class);
                intent.putExtra("result", t);
                startActivity(intent);
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                Log.i("Error url with:" + errorNo,strMsg);
            }
        });
    }
    private void parseSpecLoc(String json){
        try{
            JSONObject obj = new JSONObject(json);
            JSONObject loc = obj.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
            lat = loc.getString("lat");
            lon = loc.getString("lng");
        }catch (Exception e){
            Toast.makeText(getContext(), "Error in getting spec location", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    private void parseCurLoc(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            lat = obj.getString("lat");
            lon = obj.getString("lon");
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error in getting current location", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    private void clear(){
        ResultActivity.offset = 0;
        ResultActivity.mList.clear();
    }
    private void clearNum(){
        keywordText.clearComposingText();
        otherText.clearComposingText();
        distanceText.clearComposingText();
    }
}
