package com.example.zyz.hw9android;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.VHolder> implements View.OnClickListener{
    private  List reviews;
    String TAG = "ReviewAdapter";
    Context context;


    public void onClick(View view) {
        if(mOnItemClickListener != null){
            mOnItemClickListener.onItemClick(view,(Review) reviews.get((int)view.getTag()));
        }
    }

    public  static interface OnItemClickListener {
        void onItemClick(View view , Review item);
    }

    private OnItemClickListener mOnItemClickListener = null;

    public static class VHolder extends RecyclerView.ViewHolder {
        public TextView author_name;
        public ImageView author_photo;
        public TextView time;
        public TextView text;
        public RatingBar rating;
        public VHolder(View v) {
            super(v);
            author_name = (TextView) v.findViewById(R.id.author_name);
            author_photo = (ImageView) v.findViewById(R.id.author_photo);
            time = (TextView) v.findViewById(R.id.time);
            text = (TextView) v.findViewById(R.id.text);
            rating = (RatingBar) v.findViewById(R.id.ratingBar);
        }
    }

    public ReviewAdapter(List myDataset, Context context) {
        reviews = myDataset;
        this.context = context;
    }

    @Override
    public VHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item, parent, false);

        VHolder vh = new VHolder(v);
        v.setOnClickListener(this);

        return vh;
    }

    @Override
    public void onBindViewHolder(VHolder holder, final int position) {
            Review item = (Review) reviews.get(position);
            holder.author_name.setText(item.getAuthor_name());
            holder.rating.setRating(item.getRating());
            holder.time.setText(item.getTime());
            holder.text.setText(item.getText());
            holder.itemView.setTag(position);


        Picasso.get().load(item.getPhoto_url()).into(holder.author_photo);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}

