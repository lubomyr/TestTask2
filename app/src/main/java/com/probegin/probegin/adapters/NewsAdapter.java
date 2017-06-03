package com.probegin.probegin.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.probegin.probegin.R;
import com.probegin.probegin.entities.News;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private int mResources;
    private Context mContext;
    private List<News> itemList;
    private OnItemClickListener  mOnItemClickListener;

    public NewsAdapter(Context context, int resources) {
        this.mContext = context;
        this.mResources = resources;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(mResources, parent, false);

        return new ViewHolder(itemView, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        News item = itemList.get(position);
        holder.setItem(item);
        Glide.with(mContext).load(item.getImage()).into(holder.imageView);
        holder.titleView.setText(item.getTitle());
        holder.buttonView.setText(item.getActions());
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    public void setList(List<News> list){
        this.itemList = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(News item);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageView;
        private TextView titleView;
        private Button buttonView;
        private NewsAdapter.OnItemClickListener mOnItemClickListener;
        private News item;

        public ViewHolder(View itemView, NewsAdapter.OnItemClickListener onItemClickListener) {
            super(itemView);
            mOnItemClickListener = onItemClickListener;
            itemView.findViewById(R.id.item).setOnClickListener(ViewHolder.this);
            imageView  = (ImageView) itemView.findViewById(R.id.image);
            titleView = (TextView) itemView.findViewById(R.id.title);
            buttonView = (Button) itemView.findViewById(R.id.button);
        }

        public void setItem(News item){
            this.item = item;
        }

        @Override
        public void onClick(View v) {
            if(mOnItemClickListener != null)
                mOnItemClickListener.onItemClick(item);
        }
    }
}
