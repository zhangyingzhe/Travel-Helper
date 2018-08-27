package com.example.zyz.hw9android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements OnConnectionFailedListener{

    JSONObject result;
    String TAG = "DetailActivity";
    Toolbar toolbar;
    ViewPager mViewPager;
    TabLayout mTabLayout;
    List<String> titles;
    List<Fragment> fragments;
    private int[] tabIcons={
            R.drawable.info_outline,
            R.drawable.photos,
            R.drawable.maps,
            R.drawable.review
    };

    Bundle info_bundle;
    Bundle map_bundle;
    List googleReview;
    List yelpReview;
    Bundle data;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Menu menu;

    PlaceItem placeItem;
    GoogleApiClient mGoogleApiClient;
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        pref = getSharedPreferences(getResources().getString(R.string.storage),0);
        editor = pref.edit();
        pd = new ProgressDialog(this);
        Intent intent = getIntent();
        Bundle bundle  = intent.getExtras();
        String str = bundle.getString("place_item_str");
        JSONObject obj = null;
        try {
            obj= new JSONObject(str);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        placeItem = new PlaceItem(obj);
        Log.e(TAG, placeItem.getName());
        setupToolbar();
        pd.setMessage("Fetching details");
        pd.show();
        initData();
    }

    private void initData() {
        getDetail();
    }


    private void getDetail(){
        String detail_url = getResources().getString(R.string.server_path) + "place_detail?placeid=" + placeItem.getPlaceId();
        Log.i(TAG,"getDetail");
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, detail_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.i(TAG,response);
                try{
                    JSONObject obj = new JSONObject(response);
                    result = obj.getJSONObject("result");
                    getDetailCallback();
                }catch (JSONException e){
                    Log.e(TAG, "getdetail response error");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,"detail error");


            }
        });
        queue.add(stringRequest);
    }

    private void getDetailCallback() {
        
        try{
            Log.i(TAG, "formatted address:" + result.getString("formatted_address"));

            data = new Bundle();
            if(result.has("name")) {
                data.putString("name", result.getString("name"));
            }else{
                data.putString("name","");
            }

            data.putString("address",result.getString("formatted_address"));
            data.putString("website",result.getString("website"));
            data.putString("url", result.getString("url"));
            data.putString("place_id", result.getString("place_id"));

            info_bundle = new Bundle();
            info_bundle.putString("address", result.getString("formatted_address"));
            info_bundle.putString("url", result.getString("url"));
            info_bundle.putString("phone_number", result.getString("formatted_phone_number"));
            info_bundle.putString("website",result.getString("website") );
            info_bundle.putFloat("rating",(float)result.getDouble("rating"));
            if(result.has("price_level")) {
                info_bundle.putInt("price_level", result.getInt("price_level"));
            }else{
                info_bundle.putInt("price_level",0);
            }
            setupGoogleReview();
            setupYelpReview();
        }catch (JSONException e){
            Log.e(TAG,"formatted address error");
        }
        map_bundle = new Bundle();
        try {
            map_bundle.putString("title", result.getString("name"));
            JSONObject location = null;
            location = result.getJSONObject("geometry").getJSONObject("location");
            map_bundle.putDouble("lat", location.getDouble("lat"));
            map_bundle.putDouble("lng",location.getDouble("lng"));
        } catch (JSONException e) {
            Log.e(TAG,"map bundle error");
            e.printStackTrace();
        }

    }

    private void initView(){
        pd.dismiss();
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);

        fragments = new ArrayList<>();
        fragments.add(InfoFragment.newInstance(info_bundle));
        fragments.add(PhotosFragment.newInstance(data.getString("place_id")));
        fragments.add(MapFragment.newInstance(map_bundle));
        fragments.add(ReviewsFragment.newInstance(googleReview,yelpReview));

        titles = new ArrayList<>();
        titles.add("INFO");
        titles.add("PHOTOS");
        titles.add("MAP");
        titles.add("REVIEW");

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.i(TAG,"onTabSelect" + tab.getText());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

         mTabLayout.setupWithViewPager(mViewPager);
         AppSectionPagerAdapter adapter = new AppSectionPagerAdapter(getSupportFragmentManager(),fragments,titles);
         mViewPager.setAdapter(adapter);
         mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
             @Override
             public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
             }
             @Override
             public void onPageSelected(int position) {
                Log.i(TAG, "onpageselect" + position);
             }

             @Override
             public void onPageScrollStateChanged(int state) {
             }
         });
        setupTabIcons();
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setTitle(placeItem.getName());
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        toolbar.inflateMenu(R.menu.detail);
    }

    private void setupTabIcons(){
        mTabLayout.getTabAt(0).setCustomView(getTabView(0));
        mTabLayout.getTabAt(1).setCustomView(getTabView(1));
        mTabLayout.getTabAt(2).setCustomView(getTabView(2));
        mTabLayout.getTabAt(3).setCustomView(getTabView(3));
    }
    public View getTabView(int position) {
        View view =
                LayoutInflater.from(this).inflate(R.layout.item_tab, null);
        TextView txt_title = (TextView) view.findViewById(R.id.txt_title);
        txt_title.setText(titles.get(position));
        ImageView img_title = (ImageView) view.findViewById(R.id.img_title);
        img_title.setImageResource(tabIcons[position]);
        return view;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.detail, menu);
        if(pref.contains(placeItem.getPlaceId())){
            menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.heart_fill_white));
        }else{
            menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.heart_outline_white));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                // User chose the "Settings" item, show the app settings UI...
                Log.e(TAG, "share is click");
                tweetAction();
                return true;
            case R.id.action_favo:
                String str = placeItem.getPlaceId();
                if(pref.contains(str)){
                    editor.remove(str);
                    editor.commit();
                    menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.heart_outline_white));
                    Toast.makeText(this,placeItem.getName() + " was removed from favorites",Toast.LENGTH_SHORT).show();
                }else {
                    editor.putString(str,placeItem.toString());
                    editor.commit();
                    menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.heart_fill_white));
                    Toast.makeText(this,placeItem.getName() + " was added to favorites",Toast.LENGTH_SHORT).show();

                }
                Log.e(TAG,"favo is click:" + pref.getAll().size());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void tweetAction() {

        String twitter_content = "Check out " + data.getString("name") + " located at " + data.getString("address")
                + "%0a" + "Website:" + data.getString("website") + "\n";
        twitter_content += "%0a" +  "&hashtags=TravelAndEntertainmentSearch";

        String url = "https://twitter.com/intent/tweet?text="+ twitter_content;
        Uri uri = Uri.parse(url);
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setData(uri);
        startActivity(intent);
    }

    private void setupGoogleReview() {
        JSONArray reviews = null;
        try {
            reviews = result.getJSONArray("reviews");
            googleReview = new ArrayList<>();
            for(int i = 0; i < reviews.length(); i++){
                JSONObject record = reviews.getJSONObject(i);
                Review item = new Review();
                item.setId(i);
                item.setAuthor_name(record.getString("author_name"));
                item.setPhoto_url(record.getString("profile_photo_url"));
                item.setRating((float)record.getDouble("rating"));

                Long timeStamp= record.getLong("time") + 4*60*60;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = sdf.format(timeStamp * 1000);

                item.setTime(date);
                item.setText(record.getString("text"));
                item.setUrl(record.getString("author_url"));

                googleReview.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setupYelpReview() {
        String url = getString(R.string.server_path) + "yelp_review?name="
                + data.getString("name").replaceAll(" ","%20")
                + "&address=" + data.getString("address").replaceAll(" ","%20");
        Log.i(TAG,"setupYelpReview");
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG,response);

                try{
                    JSONObject obj = new JSONObject(response);
                    setupYelpReviewCallback(obj);
                }catch (JSONException e){
                    Log.e(TAG, "yelp_review response error");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getStackTrace();
                Log.e(TAG,"yelp_review error");


            }
        });
        queue.add(stringRequest);
    }

    private void setupYelpReviewCallback(JSONObject obj) {
        JSONArray reviews = null;
        try {
            reviews = obj.getJSONArray("reviews");
            yelpReview = new ArrayList<>();
            for(int i = 0; i < reviews.length(); i++){
                JSONObject record = reviews.getJSONObject(i);
                JSONObject user = record.getJSONObject("user");
                Review item = new Review();
                item.setId(i);
                item.setAuthor_name(user.getString("name"));
                item.setPhoto_url(user.getString("image_url"));
                item.setRating((float)record.getDouble("rating"));

                item.setTime(record.getString("time_created"));
                item.setText(record.getString("text"));
                item.setUrl(record.getString("url"));

                yelpReview.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        initView();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
