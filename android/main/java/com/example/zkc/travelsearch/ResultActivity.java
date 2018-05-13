package com.example.zkc.travelsearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.zkc.travelsearch.adapter.ResultAdapter;
import com.example.zkc.travelsearch.entity.ResultData;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends BaseActivity implements View.OnClickListener{
    private String res = null;
    public static ListView mListResult;
    public static List<ResultData> mList = new ArrayList<>();
    public static ResultAdapter adapter;
    public static int offset;
    //名称
    private List<String> name = new ArrayList<>();
    private List<String> vicinity = new ArrayList<>();
    //PlaceId
    private List<String> placeId = new ArrayList<>();
    private Button btn_next;
    private Button btn_prev;
    private String pageToken = null;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        //设置标题
        getSupportActionBar().setTitle("Search Result");
        initView();
    }
    //初始化界面
    private void initView(){
        mListResult = (ListView) findViewById(R.id.mListResult);
        offset = 0;
        mListResult.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ResultActivity.this, DetailActivity.class);
                intent.putExtra("name", name.get(position));
                intent.putExtra("placeId", placeId.get(position));
                intent.putExtra("vicinity",vicinity.get(position));
                intent.putExtra("position",String.valueOf(position));
                startActivity(intent);
            }
        });
        btn_next = (Button) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);
        btn_prev = (Button) findViewById(R.id.btn_prev);
        btn_prev.setOnClickListener(this);;
        try{
            Bundle bundle = getIntent().getExtras();
            if(bundle != null){
                res = bundle.getString("result");
                //Toast.makeText(ResultActivity.this, res, Toast.LENGTH_SHORT).show();
                parseJson(res);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        btn_prev.setEnabled(false);
    }
    private void parseJson(String res){
        try {
            JSONObject jsonObject = new JSONObject(res);
            JSONArray jsonResult = jsonObject.getJSONArray("results");
            if(jsonObject.has("next_page_token")){
                pageToken = jsonObject.getString("next_page_token");
            }else{
                pageToken = null;
            }
            for(int i = 0; i < jsonResult.length(); i++){
                JSONObject json = (JSONObject) jsonResult.get(i);
                ResultData data = new ResultData();
                data.setIcon(json.getString("icon"));

                String vicinityName = json.getString("vicinity");
                data.setVicinity(vicinityName);

                String dataName = json.getString("name");
                data.setName(dataName);

                String placeIdName = json.getString("place_id");
                data.setPlaceId(placeIdName);

                mList.add(data);
                vicinity.add(vicinityName);
                name.add(dataName);
                placeId.add(placeIdName);

            }
            adapter = new ResultAdapter(this, mList.subList(offset, Math.min(mList.size(), offset + 20)));
            mListResult.setAdapter(adapter);
            btn_prev.setEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_next:
                btn_prev.setEnabled(true);
                if(offset + 20 >= mList.size()){
                    String url = "http://csci571-php-hw8.us-east-2.elasticbeanstalk.com/hw8.php?keyword="
                            +"usc&location=34.0266,-118.2831&radius=10&type=default";
                    url += "&pagetoken=" + pageToken;
                    RxVolley.get(url, new HttpCallback(){
                        @Override
                        public void onSuccess(String t) {
                            offset += 20;
                            parseJson(t);
                            if(offset + 20 >= mList.size() && pageToken == null){
                                btn_next.setEnabled(false);
                            }
                        }
                    });
                }else{
                    offset += 20;
                    adapter = new ResultAdapter(this, mList.subList(offset, Math.min(mList.size(), offset + 20)));
                    mListResult.setAdapter(adapter);
                }
                if(offset + 20 >= mList.size() && pageToken == null){
                    btn_next.setEnabled(false);
                }
                break;
            case R.id.btn_prev:
                btn_next.setEnabled(true);
                offset -= 20;
                adapter = new ResultAdapter(this, mList.subList(offset, Math.min(mList.size(), offset + 20)));
                mListResult.setAdapter(adapter);
                if(offset <= 0){
                    btn_prev.setEnabled(false);
                }
                break;
        }
    }

}
