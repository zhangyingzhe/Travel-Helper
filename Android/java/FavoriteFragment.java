package com.example.zyz.hw9android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class FavoriteFragment extends Fragment {

    String TAG = "FavoriteFragment";
    private View view;
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private TextView emptyView;

    List favoriteList;

    public FavoriteFragment() {
        // Required empty public constructor
    }


    public static FavoriteFragment newInstance() {
        FavoriteFragment fragment = new FavoriteFragment();
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
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_favorite, container, false);

        initData();
        initView();
        return view;
    }

    private void initData() {
        favoriteList = new ArrayList<>();
        pref = getActivity().getSharedPreferences(getActivity().getResources().getString(R.string.storage),0);
        editor = pref.edit();
        Map map = pref.getAll();
        Log.e(TAG, "FAVOSIZE" + map.size());
        Iterator entries = map.entrySet().iterator();
        while (entries.hasNext()){
            Map.Entry entry = (Map.Entry) entries.next();
            String str = (String) entry.getValue();
            try {
                JSONObject obj = new JSONObject(str);
                Log.e(TAG, obj.getString("icon"));
                PlaceItem item = new PlaceItem(obj);
                favoriteList.add(item);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void initView() {

        emptyView = (TextView) view.findViewById(R.id.no_favorites);
        recyclerView = (RecyclerView) view.findViewById(R.id.favorite_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MyAdapter(getActivity(),favoriteList);

        adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view , PlaceItem item){
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("place_item_str",item.toString());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        adapter.setOnFavoriteClickListener(new MyAdapter.OnFavoriteClickListener() {
            @Override
            public void onFavoriteClick(View view, PlaceItem item) {
                Toast.makeText(getActivity(),item.getName() + " was removed from favorites",Toast.LENGTH_SHORT).show();
                changeData();
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        emptyCheck();
    }

    public void changeData() {
        favoriteList.clear();
        Map map = pref.getAll();
        Log.e(TAG, "pageselect" + map.size());
        Iterator entries = map.entrySet().iterator();
        while (entries.hasNext()){
            Map.Entry entry = (Map.Entry) entries.next();
            String str = (String) entry.getValue();
            try {
                JSONObject obj = new JSONObject(str);
                Log.e(TAG, obj.getString("icon"));
                PlaceItem item = new PlaceItem(obj);
                favoriteList.add(item);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        adapter.notifyDataSetChanged();
        emptyCheck();
    }

    private void emptyCheck(){
        if(adapter.getItemCount() == 0){
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }else{
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

}
