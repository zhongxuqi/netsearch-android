package com.musketeer.datasearch.entity;

import com.musketeer.baselibrary.bean.BaseEntity;

import java.util.List;

/**
 * Created by zhongxuqi on 16-5-7.
 */
public class WebListResponse extends BaseEntity {
    protected String status;
    protected String message;
    protected List<WebEntity> Webs;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<WebEntity> getWebs() {
        return Webs;
    }

    public void setWebs(List<WebEntity> webs) {
        Webs = webs;
    }
}
