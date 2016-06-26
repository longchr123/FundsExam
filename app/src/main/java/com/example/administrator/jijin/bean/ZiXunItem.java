package com.example.administrator.jijin.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/8.
 */
public class ZiXunItem implements Serializable {
    private String title;
    private String url;

    public ZiXunItem(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
