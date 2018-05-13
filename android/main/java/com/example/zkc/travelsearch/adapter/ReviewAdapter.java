package com.example.zkc.travelsearch.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.zkc.travelsearch.R;
import com.example.zkc.travelsearch.entity.ReviewData;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ReviewAdapter extends BaseAdapter{
    private Context context;
    private List<ReviewData> list;
    //布局加载器
    private LayoutInflater inflater;
    private ReviewData data;

    public ReviewAdapter(Context mContext, List<ReviewData> list) {
        this.context = mContext;
        this.list = list;
        //获取系统服务
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        //第一次加载
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.layout_review_item, null);
            viewHolder.author_name = (TextView) convertView.findViewById(R.id.author);
            viewHolder.head_url = (ImageView)convertView.findViewById(R.id.head_image);
            viewHolder.rating =(RatingBar) convertView.findViewById(R.id.rating);
            viewHolder.date = (TextView)convertView.findViewById(R.id.date);
            viewHolder.content = (TextView)convertView.findViewById(R.id.content);
            //缓存设置
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //设置数据
        data = list.get(position);

        SpannableString link = new SpannableString(data.getAuthor());
        link.setSpan(new URLSpan(data.getUrl()), 0,link.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        link.setSpan(new ForegroundColorSpan(Color.parseColor("#1ec4b1")), 0, link.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        viewHolder.author_name.setText(link);
        viewHolder.author_name.setMovementMethod(LinkMovementMethod.getInstance());
        viewHolder.rating.setRating(data.getRating());
        viewHolder.date.setText(data.getDate());
        viewHolder.content.setText(data.getContent());

        //加载图片
        Picasso.get().load(data.getHeadUrl()).resize(150, 150).centerCrop().into(viewHolder.head_url);
        return convertView;
    }
    class ViewHolder{
        private ImageView head_url;
        private TextView author_name;
        private RatingBar rating;
        private TextView date;
        private TextView content;
    }
}
