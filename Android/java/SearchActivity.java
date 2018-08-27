package com.example.zyz.hw9android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.ls.LSException;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.*;


public class SearchActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private  MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Button previousButton;
    private Button nextButton;
    private TextView emptyView;
    Toolbar toolbar;
    List resultsList;
    List resultsPageList;
    List token;
    int currentIndex;

    ProgressDialog pd;

    String TAG = "SearchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG,"oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        pd = new ProgressDialog(this);
        initData();
        initView();
    }

    private void initData() {
        resultsList = new ArrayList<>();
        token = new ArrayList<>();
        resultsPageList = new ArrayList<>();
        currentIndex = 0;
    }

    private void initView(){
        // Get the Intent that started this activity and extract the string

        setupToolbar();
        emptyView = (TextView) findViewById(R.id.empty_results);

        final Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        if(message == null) {
            Log.e(TAG, "no message");
            return;
        }else{
            //Log.e(TAG,message);
        }
        previousButton = (Button) findViewById(R.id.previous_button);
        nextButton = (Button) findViewById(R.id.next_button);

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPreviousPage();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNextPage();
            }
        });


        try{
            final JSONObject obj;
            obj = new JSONObject(message);
            Log.i(TAG,obj.get("start_point").toString());

            JSONArray results = obj.getJSONArray("results");
            ArrayList<String> myDataset = new ArrayList<>();


            if(obj.has("next_page_token")) {
                token.add(obj.getString("next_page_token"));
            }

            setUpNewPage(results);
            List tmp;
            tmp = (ArrayList<PlaceItem>) resultsPageList.get(0);
            resultsList.addAll(tmp);
            currentIndex = 0;
            previousButton.setEnabled(false);

            if(!token.isEmpty()){
                nextButton.setEnabled(true);
            }else {
                nextButton.setEnabled(false);
            }

            mRecyclerView = (RecyclerView) findViewById(R.id.resultsView);
            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mAdapter = new MyAdapter(this,resultsList);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            emptyCheck();

            mAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener(){
                @Override
                public void onItemClick(View view , PlaceItem item){
                    Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("place_item_str",item.toString());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            mAdapter.setOnFavoriteClickListener(new MyAdapter.OnFavoriteClickListener() {
                @Override
                public void onFavoriteClick(View view, PlaceItem item) {
                    Log.e(TAG,"button favo:" );
                    if(item.isFavorite()) {
                        Toast.makeText(getBaseContext(),
                                item.getName() + " was added to favorites", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getBaseContext(),
                                item.getName() + " was removed from favorites", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch(JSONException e){
            Log.e("SearchActivity", "Invalid JSON string: " + message, e);
        }
    }


    public void setupToolbar(){
        Log.e(TAG, "setup toolbar");
        toolbar = (Toolbar) findViewById(R.id.result_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setTitle("Search results");
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        //toolbar.inflateMenu(R.menu.detail);

        //toolbar.inflateMenu(R.menu.detail);
       // ActionBar ab = getSupportActionBar();
        //ab.setDisplayHomeAsUpEnabled(true);

    }

    public void setUpNewPage(JSONArray jsonArray){
        List list = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++){
            try {
                JSONObject record = jsonArray.getJSONObject(i);

                PlaceItem item = new PlaceItem();
                item.setAddress(record.getString("vicinity"));
                item.setIcon(record.getString("icon"));
                item.setName(record.getString("name"));
                item.setPlaceId(record.getString("place_id"));
                item.setFavorite(false);
                list.add(item);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        resultsPageList.add(list);
    }

    public void changeResultsPage(){

        if(currentIndex == 0){
            previousButton.setEnabled(false);
        }else {
            previousButton.setEnabled(true);
        }
        if(currentIndex >= token.size()){
            nextButton.setEnabled(false);
        }else {
            nextButton.setEnabled(true);
        }
        resultsList.clear();
        List tmp;
        tmp = (ArrayList<PlaceItem>) resultsPageList.get(currentIndex);
        resultsList.addAll(tmp);
        mAdapter.notifyDataSetChanged();
        emptyCheck();
    }

    public void getPreviousPage(){
        currentIndex = currentIndex - 1;
        if(currentIndex < 0) return;
        changeResultsPage();
    }

    public void getNextPage(){
        currentIndex = currentIndex + 1;
        if(currentIndex < resultsPageList.size()){
            changeResultsPage();
        }
        else {
            String url = getString(R.string.server_path) + "next_page?token=" + token.get(currentIndex - 1);
            Log.e(TAG, url);
            pd.setMessage("Fetching next page");
            pd.show();
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                   // Log.i(TAG,response);

                    try{
                        JSONObject obj = new JSONObject(response);
                        JSONArray results = obj.getJSONArray("results");
                        setUpNewPage(results);

                        if(obj.has("next_page_token")){
                            token.add(obj.getString("next_page_token"));
                        }
                        pd.dismiss();
                        changeResultsPage();
                    }catch (JSONException e){
                        Log.e(TAG, "next page response error");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.getStackTrace();
                    Log.e(TAG,"next page error");


                }
            });
            queue.add(stringRequest);
        }

    }

    private void emptyCheck(){
        if(mAdapter.getItemCount() == 0){
            mRecyclerView.setVisibility(View.GONE);
            previousButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }else{
            mRecyclerView.setVisibility(View.VISIBLE);
            previousButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        Log.e(TAG,"onresume");
        super.onResume();
        mAdapter.notifyDataSetChanged();
        emptyCheck();
    }

    @Override
    protected void onStart() {
        Log.e(TAG,"onstart");
        super.onStart();

    }
}
