package com.probegin.probegin.entities;

import java.io.Serializable;

public class News implements Serializable{

    private static final long serialVersionUID = -5884244024692751958L;

    private String title;

    private String summary;

    private String actions;

    private String url;

    private String image;

    public News(String title, String summary, String actions, String url, String image) {
        this.title = title;
        this.summary = summary;
        this.actions = actions;
        this.url = url;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
