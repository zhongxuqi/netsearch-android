package com.musketeer.datasearch.entity;

/**
 * Created by zhongxuqi on 16-5-13.
 */
public class ListUpdateEvent {

    protected EventType type;

    public ListUpdateEvent(EventType type) {
        this.type = type;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public enum EventType {
        UNION_SEARCH, MONITOR_SEARCH
    }
}
