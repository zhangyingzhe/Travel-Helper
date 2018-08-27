package com.example.zyz.hw9android;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.VHolder> {
    private List photo;
    String TAG = "PhotoAdapter";


    public static class VHolder extends RecyclerView.ViewHolder {
        public ImageView photo_img;
        public VHolder(View v) {
            super(v);
            photo_img = (ImageView) v.findViewById(R.id.photo_img);
        }
    }

    public PhotoAdapter(List myDataset) {
        photo = myDataset;
    }

    @Override
    public VHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_item, parent, false);

        VHolder vh = new VHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(VHolder holder, final int position) {
         Bitmap item = (Bitmap) photo.get(position);
         int iw = item.getWidth();
         int ih = item.getHeight();
         int tw = 1000;
         int th = ih * tw / iw;
         //Log.e("Bitmap:", iw + " " + ih + " " + holder.photo_img.getWidth() + " " );
         //holder.photo_img.setImageBitmap(item);
         //holder.photo_img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        holder.photo_img.setImageBitmap(Bitmap.createScaledBitmap(item,tw,th,false));

    }

    @Override
    public int getItemCount() {
        return photo.size();
    }
}

