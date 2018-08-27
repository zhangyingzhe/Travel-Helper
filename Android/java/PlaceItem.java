package com.example.zyz.hw9android;

import org.json.JSONException;
import org.json.JSONObject;

public class PlaceItem {
    String name;
    String address;
    String icon;
    boolean favorite;
    String placeId;

    public PlaceItem(){

    }

    public PlaceItem(String name,String address, String icon, String placeId, boolean favorite){
        this.name = name;
        this.address = address;
        this.icon = icon;
        this.placeId = placeId;
        this.favorite = favorite;

    }

    public PlaceItem(JSONObject obj){
        try {
            this.name = obj.getString("name");
            this.placeId = obj.getString("place_id");
            this.icon = obj.getString("icon");
            this.address = obj.getString("address");
            this.favorite = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getAddress() {
        return address;
    }


    public String getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    @Override
    public String toString() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("name",name);
            obj.put("address",address);
            obj.put("place_id",placeId);
            obj.put("icon",icon);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }
}

