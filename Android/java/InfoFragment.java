package com.example.zyz.hw9android;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.HashMap;


public class InfoFragment extends Fragment {

    public static final String TYPE = "type";
    private View parentView;
    private TextView txt_content;
    public InfoFragment() {
        // Required empty public constructor
    }


    public static InfoFragment newInstance(Bundle bundle) {
        InfoFragment fragment = new InfoFragment();

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_info, container, false);
        initView();
        return parentView;
    }

    private void initView() {

        //txt_content = (TextView) parentView.findViewById(R.id.txt_content);
        //txt_content.setText(getArguments().getString(TYPE, "Default"));
        TextView address = (TextView) parentView.findViewById(R.id.info_address);
        address.setText(getArguments().getString("address"));

        RatingBar rating = (RatingBar) parentView.findViewById(R.id.ratingBar);
        rating.setRating(getArguments().getFloat("rating"));


        TextView price = (TextView) parentView.findViewById(R.id.info_price_level);
        Integer p = getArguments().getInt("price_level");
        char[] c = new char[p];
        for(int i = 0; i < p; i++){
            c[i] = '$';
        }
        String str = new String(c);
        price.setText(str);

        TextView website = (TextView) parentView.findViewById(R.id.info_website);
        website.setText(Html.fromHtml("<u>"+getArguments().getString("website")+"</u>"));

        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(getArguments().getString("website"));
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.setData(uri);
                startActivity(intent);
            }
        });

        TextView url = (TextView) parentView.findViewById(R.id.info_url);
        url.setText(Html.fromHtml("<u>"+getArguments().getString("url")+"</u>"));
        url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(getArguments().getString("url"));
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.setData(uri);
                startActivity(intent);
            }
        });

        TextView phone = (TextView) parentView.findViewById(R.id.info_phone_number);
        phone.setText(Html.fromHtml("<u>" + getArguments().getString("phone_number")+ "</u>"));
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+getArguments().getString("phone_number")));
                startActivity(intent);
            }
        });

    }

}
