package com.musketeer.datasearch.entity;

/**
 * Created by zhongxuqi on 16-5-13.
 */
public class TaskBean {
    private String Url;
    private String[] Keywords;
    private SearchResultEntity Info;

    public TaskBean() {

    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String[] getKeywords() {
        return Keywords;
    }

    public void setKeywords(String[] keywords) {
        Keywords = keywords;
    }

    public SearchResultEntity getInfo() {
        return Info;
    }

    public void setInfo(SearchResultEntity info) {
        Info = info;
    }
}
