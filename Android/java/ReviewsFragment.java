package com.example.zyz.hw9android;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ReviewsFragment extends Fragment {

    static String TAG = "ReviewsFragment";

    private static JSONObject result;
    private View parentView;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private static List googleReview;
    private static List yelpReview;
    private static List review;

    private ReviewAdapter googleAdapter;
    private TextView emptyReview;

    public ReviewsFragment() {
        // Required empty public constructor
    }

    public static ReviewsFragment newInstance(List g, List y) {


        googleReview = g;
        yelpReview = y;
        review = new ArrayList<>();
        review.addAll(googleReview);
        if(yelpReview == null){
            yelpReview = new ArrayList<>();
        }
        if(googleReview == null){
            googleReview = new ArrayList();
        }

        Log.e(TAG,"YelpReviewSIZE:" + String.valueOf(yelpReview.size()));

        ReviewsFragment fragment = new ReviewsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_reviews, container, false);
        initView();
        return parentView;
    }

    private void initView() {

        emptyReview = (TextView) parentView.findViewById(R.id.empty_review);
        
        mRecyclerView = (RecyclerView) parentView.findViewById(R.id.review_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

       // mAdapter = new ReviewAdapter(review);
        //mRecyclerView.setAdapter(mAdapter);
        googleAdapter = new ReviewAdapter(review,getActivity());
        mRecyclerView.setAdapter(googleAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        googleAdapter.setOnItemClickListener(new ReviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Review item) {
                Uri uri = Uri.parse(item.getUrl());
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.setData(uri);
                startActivity(intent);
            }
        });

        setupSort();
        setupContentChange();

    }

    private void setupContentChange() {
        Spinner spinner = (Spinner) parentView.findViewById(R.id.content_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e(TAG, "content spinner click" + i );
                if(i == 0){
                    ReviewGoogle();
                }else{
                    ReviewYelp();
                }
                googleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void ReviewGoogle() {
        review.clear();
        review.addAll(googleReview);
        EmptyCheck();
        setUpChangeSourceSort();
    }

    private void ReviewYelp() {
        review.clear();
        review.addAll(yelpReview);
        EmptyCheck();
        setUpChangeSourceSort();
    }

    private void setUpChangeSourceSort(){
        Spinner spinner = (Spinner) parentView.findViewById(R.id.sort_spinner);
        int i = spinner.getSelectedItemPosition();
        switch(i){
            case 0:
                sortDefault();
                break;
            case 1:
                sortHighestRating();
                break;
            case 2:
                sortLowestRating();
                break;
            case 3:
                sortMostRecent();
                break;
            case 4:
                sortLeastRecent();
                break;
        }
        googleAdapter.notifyDataSetChanged();
    }

    private void setupSort() {
        Spinner spinner = (Spinner) parentView.findViewById(R.id.sort_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e(TAG, "sort spinner click:" + i);
                switch(i){
                    case 0:
                        sortDefault();
                        break;
                    case 1:
                        sortHighestRating();
                        break;
                    case 2:
                        sortLowestRating();
                        break;
                    case 3:
                        sortMostRecent();
                        break;
                    case 4:
                    sortLeastRecent();
                    break;
                }
                googleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void sortDefault(){
       // Log.e(TAG,"sortDefault");
        Collections.sort(review, new IdComparator());

    }

    static class IdComparator implements Comparator{

        public int compare(Object object1, Object object2) {// 实现接口中的方法
            Review p1 = (Review) object1; // 强制转换
            Review p2 = (Review) object2;
            return new Double(p1.getId()).compareTo(new Double(p2.getId()));
        }
    }

    private void sortHighestRating(){
        Collections.sort(review,new HighestRatingComparator());

    }
    static class HighestRatingComparator implements Comparator {
        public int compare(Object object1, Object object2) {// 实现接口中的方法
            Review p1 = (Review) object1; // 强制转换
            Review p2 = (Review) object2;
            return new Double(p2.getRating()).compareTo(new Double(p1.getRating()));
        }
    }

    private void sortLowestRating(){
        Collections.sort(review,new LowestRatingComparator());

    }
    static class LowestRatingComparator implements Comparator {
        public int compare(Object object1, Object object2) {// 实现接口中的方法
            Review p1 = (Review) object1; // 强制转换
            Review p2 = (Review) object2;
            return new Double(p1.getRating()).compareTo(new Double(p2.getRating()));
        }
    }

    private void sortMostRecent(){
        Collections.sort(review, new MostRecentComparator());

    }
    static class MostRecentComparator implements Comparator {
        public int compare(Object object1, Object object2) {// 实现接口中的方法
            Review p1 = (Review) object1; // 强制转换
            Review p2 = (Review) object2;
            return p2.getTime().compareTo(p1.getTime());
        }
    }


    private void sortLeastRecent(){
        Collections.sort(review,new LeastRecentComparator());

    }
    static class LeastRecentComparator implements Comparator {
        public int compare(Object object1, Object object2) {// 实现接口中的方法
            Review p1 = (Review) object1; // 强制转换
            Review p2 = (Review) object2;
            return p1.getTime().compareTo(p2.getTime());
        }
    }

    private void EmptyCheck(){
        if(googleAdapter.getItemCount() == 0){
            mRecyclerView.setVisibility(View.GONE);
            emptyReview.setVisibility(View.VISIBLE);
        }else{
            mRecyclerView.setVisibility(View.VISIBLE);
            emptyReview.setVisibility(View.GONE);
        }
    }
}


