package com.example.dttt.idiary.model;

import android.net.Uri;

public class Diary {
    int id;
    String title;
    String contents;
    Uri image;
    String date;

    public Diary(int id, String title, String contents, Uri image, String date) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.image = image;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
