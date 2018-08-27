package com.example.zyz.hw9android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MapFragment extends Fragment implements OnMapReadyCallback {
    private final String TAG = "MapFragment";
    MapView mapView;
    GoogleMap map;
    private AutoCompleteTextView fromView;
    Spinner spinner;
    Marker marker;
    Polyline polyline;
    Marker startMarker;
    Marker endMarker;

    public static final String placeid = "ChIJ7aVxnOTHwoARxKIntFtakKo";
    private View parentView;

    public MapFragment() {
        // Required empty public constructor
    }


    public static MapFragment newInstance(Bundle bundle) {
        MapFragment fragment = new MapFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_map, container, false);
            mapView = (MapView) parentView.findViewById(R.id.mapView);
            mapView.onCreate(savedInstanceState);

            mapView.getMapAsync(this);
        initView();
        return parentView;
    }

    void initView() {

        spinner = (Spinner) parentView.findViewById(R.id.travel_spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                searchDirection(view);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        fromView = (AutoCompleteTextView) parentView.findViewById(R.id.fromTextVIew);
        CustomAutoCompleteAdapter adapter = new CustomAutoCompleteAdapter(getActivity());
        fromView.setAdapter(adapter);

        fromView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                searchDirection(view);
            }
        });

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng sydney = new LatLng(getArguments().getDouble("lat"),getArguments().getDouble("lng"));
        //LatLng sydney = new LatLng(37.35, -122.0);


        map.getUiSettings().setMyLocationButtonEnabled(false);
        //map.getUiSettings().setZoomControlsEnabled(true);
        //map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));
        marker = map.addMarker(new MarkerOptions()
               .title(getArguments().getString("title"))
                //.snippet("The most populous city in Australia.")
               .position(sydney));
        marker.showInfoWindow();
        //drawRoute();
    }

    private void searchDirection(View view) {
        String[] travelMode = {"driving","bicycling","transit","walking"};
        String from = fromView.getText().toString();
        int travel = spinner.getSelectedItemPosition();
        Log.e(TAG,from + travel);
        if(from == null || from.isEmpty()) {
            Log.e(TAG, "no origin");
            return;}
        String url = getActivity().getResources().getString(R.string.server_path)+ "direction?mode=" + travelMode[travel]+
                "&destination=" + getArguments().getDouble("lat") + ","
                + getArguments().getDouble("lng") +  "&origin=" + from.replaceAll(" ","%20");
        Log.e(TAG, url);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.i(TAG,response);
                drawRoute(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,"searchdirection error");
            }
        });
        queue.add(stringRequest);

        //drawRoute();
    }

    private void drawRoute(String response) {
        try {
            Log.e(TAG,"draw route");

            PolylineOptions polylineOptions = new PolylineOptions();
            JSONObject obj = new JSONObject(response);
            JSONArray routes = obj.getJSONArray("routes");
            JSONObject route = routes.optJSONObject(0);
            JSONArray legs = route.getJSONArray("legs");
            JSONObject leg = legs.getJSONObject(0);
            JSONObject start_location = leg.getJSONObject("start_location");

            LatLng start_marker = new LatLng(start_location.getDouble("lat"),start_location.getDouble("lng"));

            JSONObject end_location = leg.getJSONObject("end_location");
            LatLng end_marker = new LatLng(end_location.getDouble("lat"),end_location.getDouble("lng"));
            JSONArray steps= leg.getJSONArray("steps");

            double minlat = Math.min(end_location.getDouble("lat"),start_location.getDouble("lat"));
            double maxlat = Math.max(end_location.getDouble("lat"),start_location.getDouble("lat"));
            double minlng = Math.min(end_location.getDouble("lng"),start_location.getDouble("lng"));
            double maxlng = Math.max(end_location.getDouble("lng"),start_location.getDouble("lng"));


            for(int i = 0; i < steps.length();i++){
                JSONObject record = steps.getJSONObject(i);
                JSONObject point = record.getJSONObject("start_location");
                //Log.e(TAG,String.valueOf(point));
                Double lat = point.getDouble("lat");
                Double lng = point.getDouble("lng");
                minlat = Math.min(minlat,lat);
                maxlat = Math.max(maxlat,lat);
                minlng = Math.min(minlng,lng);
                maxlng = Math.max(maxlng,lng);
                polylineOptions.add(new LatLng(lat,lng));

            }
            if(marker != null){
                marker.remove();
            }
            if(polyline != null) {
                polyline.remove();
            }
            if(startMarker != null) {
                startMarker.remove();
            }
            if(endMarker != null) {
                endMarker.remove();
            }

            LatLngBounds latLngBounds = new LatLngBounds(new LatLng(minlat,minlng),new LatLng(maxlat,maxlng));
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,120));

            polylineOptions.add(new LatLng(end_location.getDouble("lat"), end_location.getDouble("lng")));
            polyline = map.addPolyline(polylineOptions);
            polyline.setColor(Color.BLUE);
            String[] strs = fromView.getText().toString().split(",");
            startMarker = map.addMarker(new MarkerOptions()
                    .title(fromView.getText().toString().split(",")[0])
                    .position(start_marker));
            startMarker.showInfoWindow();
            endMarker = map.addMarker(new MarkerOptions()
                    .position(end_marker));
        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
