package com.example.zkc.travelsearch.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zkc.travelsearch.R;
import com.example.zkc.travelsearch.entity.ResultData;
import com.example.zkc.travelsearch.fragment.FavoriteFragment;
import com.squareup.picasso.Picasso;

import java.util.List;


public class ResultAdapter extends BaseAdapter{
    private Context mContext;
    private List<ResultData> mList;
    //布局加载器
    private LayoutInflater inflater;

    public ResultAdapter(Context mContext, List<ResultData> mList){
        this.mContext = mContext;
        this.mList = mList;
        //获取系统服务
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder viewHolder = null;
        //第一次加载
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.layout_result_item, null);
            viewHolder.res_icon = (ImageView) convertView.findViewById(R.id.res_icon);
            viewHolder.res_name = (TextView) convertView.findViewById(R.id.res_name);
            viewHolder.res_vicinity =(TextView) convertView.findViewById(R.id.res_vicinity);
            viewHolder.res_favor = (ImageView) convertView.findViewById(R.id.heart);
            //缓存设置
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //设置数据
        ResultData data = mList.get(position);

        viewHolder.res_name.setText(data.getName());
        viewHolder.res_vicinity.setText(data.getVicinity());

        //加载图片
        Picasso.get().load(data.getIcon()).resize(150, 150).centerCrop().into(viewHolder.res_icon);

        if(data.isFavorite()){
            viewHolder.res_favor.setImageResource(R.drawable.heart_fill_red);
        }else{
            viewHolder.res_favor.setImageResource(R.drawable.heart_outline_black);
        }

        viewHolder.res_favor.setOnClickListener(new heartClick(position, data));
        return convertView;
    }

    class heartClick implements View.OnClickListener{
        private int position;
        private ResultData data;
        public heartClick(int position, ResultData data){
            this.position = position;
            this.data = data;
        }

        @Override
        public void onClick(final View v) {
            ImageView heart = (ImageView) v;
            if(v.getId() == heart.getId()){
                boolean value = !data.isFavorite();
                data.setFavorite(value);
                if(value){
                    heart.setImageResource(R.drawable.heart_fill_red);
                    FavoriteFragment.favorList.add(data);
                }else{
                    heart.setImageResource(R.drawable.heart_outline_black);
                    FavoriteFragment.favorList.remove(data);
                }
                FavoriteFragment.listView.setAdapter(FavoriteFragment.favorAdapter);
                notifyDataSetChanged();
            }
        }
    }

    class ViewHolder{
        private ImageView res_icon;
        private TextView res_name;
        private TextView res_vicinity;
        private ImageView res_favor;

    }
}
