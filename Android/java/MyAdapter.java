package com.example.zyz.hw9android;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static android.support.v4.content.ContextCompat.startActivity;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.VHolder> implements View.OnClickListener {
    private List data;
    String TAG = "MyAdapter";
    Context context;

    @Override
    public void onClick(View view) {
        if(mOnItemClickListener != null){
            mOnItemClickListener.onItemClick(view,(PlaceItem) data.get((int)view.getTag()));
        }
    }

    public  static interface OnItemClickListener {
        void onItemClick(View view , PlaceItem item);
    }

    public static interface OnFavoriteClickListener{
        void onFavoriteClick(View view,PlaceItem item);
    }

    private OnItemClickListener mOnItemClickListener = null;
    private OnFavoriteClickListener mOnFavoriteClickListener = null;


    public static class VHolder extends RecyclerView.ViewHolder  {
        public TextView name;
        public ImageView imgIcon;
        public TextView address;
        public  ImageButton imgFavo;
        public VHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.name);
            imgIcon = (ImageView) v.findViewById(R.id.img_icon);
            address = (TextView) v.findViewById(R.id.address);
            imgFavo = (ImageButton) v.findViewById(R.id.img_favo);
        }
    }

    public MyAdapter(Context context, List myDataset) {
        this.context = context;
        data = myDataset;
    }

    @Override
    public VHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.table_view, parent, false);

        VHolder vh = new VHolder(v);
        v.setOnClickListener(this);
        return vh;
    }

    @Override
    public void onBindViewHolder(final VHolder holder, final int position) {
        final PlaceItem item = (PlaceItem) data.get(position);

        SharedPreferences pref = context.getSharedPreferences(context.getResources().getString(R.string.storage),0);
        SharedPreferences.Editor editor = pref.edit();

        Log.i(TAG, item.getName());
        Log.e(TAG,"size:" + pref.getAll().size());
        holder.name.setText(item.getName());
        holder.address.setText(item.getAddress());

        holder.itemView.setTag(position);
        Picasso.get().load(item.getIcon()).into(holder.imgIcon);

        if(pref.contains(item.getPlaceId())) {
            item.setFavorite(true);
            holder.imgFavo.setImageResource(R.drawable.heart_fill_red);
        }else {
            item.setFavorite(false);
            holder.imgFavo.setImageResource(R.drawable.heart_outline_black);
        }

            holder.imgFavo.setClickable(true);
            holder.imgFavo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e(TAG,"favoclick:" + item.getPlaceId());

                    SharedPreferences pref = context.getSharedPreferences(context.getResources().getString(R.string.storage),0);
                    SharedPreferences.Editor editor = pref.edit();

                    if(pref.contains(item.getPlaceId())) {
                        item.setFavorite(false);
                        holder.imgFavo.setImageResource(R.drawable.heart_outline_black);
                        editor.remove(item.getPlaceId());
                        editor.commit();
                    }else {
                        item.setFavorite(true);
                        holder.imgFavo.setImageResource(R.drawable.heart_fill_red);
                        Log.e(TAG, "TOSTRING"+item.toString());
                        editor.putString(item.getPlaceId(),item.toString());
                        Log.e(TAG,"size:" + pref.getAll().size());
                        editor.commit();
                    }

                    if(mOnFavoriteClickListener != null){
                        mOnFavoriteClickListener.onFavoriteClick(view,item);
                    }
                }
            });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnFavoriteClickListener(OnFavoriteClickListener listener) {
        this.mOnFavoriteClickListener = listener;
    }

}
