package com.example.zkc.travelsearch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.example.zkc.travelsearch.entity.InfoData;
import com.example.zkc.travelsearch.fragment.FavoriteFragment;
import com.example.zkc.travelsearch.fragment.InfoFragment;
import com.example.zkc.travelsearch.fragment.MapFragment;
import com.example.zkc.travelsearch.fragment.NoPhotoFragment;
import com.example.zkc.travelsearch.fragment.PhotoFragment;
import com.example.zkc.travelsearch.fragment.ReviewFragment;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends BaseActivity{
    //TabLayout
    private TabLayout tabLayout;
    //ViewPager
    private ViewPager viewPager;
    //Title
    private List<String> title;
    //Fragment
    private List<Fragment> fragment;
    //
    private String placeId;

    private String vicinity;

    private String formatedAddress;

    private String website;

    private InfoData data;

    private String reviewJson;

    private String reviewYelpJson;

    private String location;

    private int position;

    private ImageView heart;
    private ImageView share;

    private ProgressDialog pdDialog=null;

    private boolean hasPhoto;

    private String name;
    //步骤:
    //1. 获取PlaceId信息
    //2. 获得当前的PlaceDetail内容，转化位String格式
    //3. 创建Title，Fragments
    //4. Adapter绑定
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        runProgressBar();
        getDetail();
        setActionBar();

    }
    private void runProgressBar(){
        pdDialog = new ProgressDialog(DetailActivity.this);
        pdDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // 设置ProgressDialog 的进度条是否不明确
        pdDialog.setIndeterminate(false);
        //pdDialog.setMessage("正在下载中……");
        pdDialog.setMessage("fetching data……");
        // 让ProgressDialog显示
        pdDialog.show();

    }
    private void stopProgressBar(){
        pdDialog.cancel();
    }
    private void setActionBar(){
        ActionBar actionBar = getSupportActionBar();
        //去阴影
        actionBar.setElevation(0f);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        //设置显示自定义的View，如果不设置这个属性，自定义的View不会起作用
        actionBar.setDisplayShowCustomEnabled(true);
        View view = LayoutInflater.from(this).inflate(R.layout.actionbar_detail, null);
        heart = (ImageView) view.findViewById(R.id.bar_right);
        share = (ImageView) view.findViewById(R.id.twitter);
        actionBar.setCustomView(view);

        if(ResultActivity.mList.get(position).isFavorite()){
            heart.setImageResource(R.drawable.heart_fill_white);
        }else{
            heart.setImageResource(R.drawable.heart_outline_white);
        }
        final int pos = position;
        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean value = !ResultActivity.mList.get(pos).isFavorite();
                ResultActivity.mList.get(pos).setFavorite(value);
                if(value){
                    heart.setImageResource(R.drawable.heart_fill_white);
                    FavoriteFragment.favorList.add(ResultActivity.mList.get(pos));
                }else{
                    heart.setImageResource(R.drawable.heart_outline_white);
                    FavoriteFragment.favorList.remove(ResultActivity.mList.get(pos));
                }
                FavoriteFragment.listView.setAdapter(FavoriteFragment.favorAdapter);
                ResultActivity.mListResult.setAdapter(ResultActivity.adapter);
            }
        });
        share.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String url = buildTwitterUrl();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(url));
                startActivity(intent);

            }
        });
    }
    private String buildTwitterUrl(){
        String url = "https://twitter.com/intent/tweet?text=Check%20out%20";
        url += name +".%20Locate%20at%20" + vicinity;
        url += ".%20Website%20:%20" + website + "%20%23TravelAndEntertainmentSearch";
        return url;
    }
    private void getDetail(){
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        placeId = intent.getStringExtra("placeId");
        vicinity = intent.getStringExtra("vicinity");
        position = Integer.valueOf(intent.getStringExtra("position"));
        //设置标题
        getSupportActionBar().setTitle(name);
        data = new InfoData();
        String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid="
                + placeId + "&key=AIzaSyAKjzvV0YTKgbdE7fmIHWMb9aZAxISOdiE";
        RxVolley.get(url, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                parseJson(t);
                getYelp();
            }
        });
    }
    private void getYelp(){
        String url = "http://csci571-php-hw8.us-east-2.elasticbeanstalk.com/hw8.php?yelp&location=" + vicinity;
        RxVolley.get(url, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                parseReviewJson(t);
                initData();
                stopProgressBar();
            }
        });
    }
    private void parseReviewJson(String json){
        try{
            JSONObject object = new JSONObject(json);
            reviewYelpJson = object.getString("reviews");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void parseJson(String json){
        try {
            JSONObject object = new JSONObject(json);
            JSONObject res = object.getJSONObject("result");

            formatedAddress = res.getString("formatted_address");
            data.setAddr(formatedAddress);
            data.setPhoneNumber(res.getString("formatted_phone_number"));
            data.setPrice(res.getInt("price_level"));
            data.setRating(res.getDouble("rating"));

            website = res.getString("website");
            data.setWebsite(website);
            data.setUrl(res.getString("url"));

            location = res.getJSONObject("geometry").getString("location");
            reviewJson = res.getString("reviews");

            hasPhoto = res.has("photos");
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private void initData(){
        title = new ArrayList<>();
        title.add("Info");
        title.add("Photo");
        title.add("Map");
        title.add("Review");

        fragment = new ArrayList<>();

        Fragment info = new InfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("detail", data.toString());
        info.setArguments(bundle);
        fragment.add(info);

        if(!hasPhoto){
            fragment.add(new NoPhotoFragment());
        }else {
            Fragment photo = new PhotoFragment();
            Bundle phBundle = new Bundle();
            phBundle.putString("placeId", placeId);
            photo.setArguments(phBundle);
            fragment.add(photo);
        }

        Fragment map = new MapFragment();
        Bundle mapBundle = new Bundle();
        mapBundle.putString("location",location);
        map.setArguments(mapBundle);
        fragment.add(map);

        Fragment review = new ReviewFragment();
        Bundle revBundle = new Bundle();
        revBundle.putString("GoogleReview", reviewJson);
        revBundle.putString("YelpReview",reviewYelpJson);
        review.setArguments(revBundle);
        fragment.add(review);

        initView();
    }
    //初始化View
    private void initView(){

        tabLayout = (TabLayout) findViewById(R.id.detail_tabLayout);
        viewPager = (ViewPager) findViewById(R.id.detail_viewPager);
        //预加载
        viewPager.setOffscreenPageLimit(fragment.size());
        //设置适配器
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragment.get(position);
            }

            @Override
            public int getCount() {
                return fragment.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return title.get(position);
            }
        });
        //绑定
        tabLayout.setupWithViewPager(viewPager);
    }
}