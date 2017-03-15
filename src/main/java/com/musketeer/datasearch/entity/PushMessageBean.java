package com.musketeer.datasearch.entity;

/**
 * Created by zhongxuqi on 16-5-13.
 */
public class PushMessageBean {
    protected String MapKey;
    protected String Keyword;
    protected int Type;

    public PushMessageBean() {

    }

    public String getMapKey() {
        return MapKey;
    }

    public void setMapKey(String mapKey) {
        MapKey = mapKey;
    }

    public String getKeyword() {
        return Keyword;
    }

    public void setKeyword(String keyword) {
        Keyword = keyword;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }
}
