package com.example.administrator.jijin.bean;

/**
 * Created by Administrator on 2016/2/27.
 */
public class ExamSmallItem {
    private String formalDBURL;
    private String title;
    private int current;
    private int max;

    public ExamSmallItem() {

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

    public String getFormalDBURL() {
        return formalDBURL;
    }

    public void setFormalDBURL(String formalDBURL) {
        this.formalDBURL = formalDBURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
