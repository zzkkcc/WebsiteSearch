package com.example.zkc.travelsearch.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.zkc.travelsearch.R;

import java.util.List;

public class PhotoAdapter extends BaseAdapter {
    private Context mContext;
    private List<Bitmap> list;
    //布局加载器
    private LayoutInflater inflater;
    private ImageView imageView;

    public PhotoAdapter(Context mContext, List<Bitmap> list) {
        this.mContext = mContext;
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
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.layout_photo_item, null);
            viewHolder.image = (ImageView)convertView.findViewById(R.id.image);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        //设置数据
        Bitmap bitmap = list.get(position);
        viewHolder.image.setImageBitmap(bitmap);
        return convertView;
    }

    class ViewHolder{
        private ImageView image;
    }
}
