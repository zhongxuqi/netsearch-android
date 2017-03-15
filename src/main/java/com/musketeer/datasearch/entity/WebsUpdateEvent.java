package com.musketeer.datasearch.entity;

/**
 * Created by zhongxuqi on 16-5-7.
 */
public class WebsUpdateEvent {
    public enum UpdateType {
        FROM_NET, FROM_SET
    }

    private UpdateType type;

    public WebsUpdateEvent(UpdateType type) {
        this.type = type;
    }

    public UpdateType getType() {
        return type;
    }

    public void setType(UpdateType type) {
        this.type = type;
    }
}
