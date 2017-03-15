package com.musketeer.datasearch.entity;

import com.musketeer.baselibrary.bean.BaseEntity;

/**
 * Created by zhongxuqi on 16-5-13.
 */
public class BaseResp extends BaseEntity {
    protected String status;
    protected String message;

    public BaseResp() {

    }

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
}
