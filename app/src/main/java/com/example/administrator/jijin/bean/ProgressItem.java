package com.example.administrator.jijin.bean;

/**
 * Created by Administrator on 2016/6/16.
 */
public class ProgressItem {

    private String title;
    private int current;
    private int max;

    public ProgressItem() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
