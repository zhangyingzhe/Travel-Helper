package com.example.zyz.hw9android;

import android.support.annotation.NonNull;

public class Review implements Comparable<Review>{
    private Integer id;
    private String author_name;
    private String photo_url;
    public float rating;
    private String text;
    public String time;
    private String url;

    public Review(){

    }

    public void setId(Integer id){
        this.id = id;
    }

    public Integer getId(){
        return id;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }
    public String getAuthor_name() {
        return author_name;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }
    public String getPhoto_url() {
        return photo_url;
    }
    public void setRating(float rating) {
        this.rating = rating;
    }

    public float getRating() {
        return rating;
    }

    public void setText(String text) {
        this.text = text;
    }
    public String getText() {
        return text;
    }

    public void setTime(String time) {
        this.time = time;
    }
    public String getTime() {
        return time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public int compareTo(@NonNull Review review) {
        return 0;
    }
}
