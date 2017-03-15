package com.musketeer.datasearch.entity;

/**
 * Created by zhongxuqi on 16-5-7.
 */
public class SearchFinishEvent {
    private WebEntity mWeb;

    public SearchFinishEvent(WebEntity web) {
        this.mWeb = web;
    }

    public WebEntity getmWeb() {
        return mWeb;
    }

    public void setmWeb(WebEntity mWeb) {
        this.mWeb = mWeb;
    }
}
