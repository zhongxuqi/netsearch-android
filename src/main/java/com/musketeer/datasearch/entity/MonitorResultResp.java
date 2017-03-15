package com.musketeer.datasearch.entity;

import com.j256.ormlite.field.DatabaseField;
import com.musketeer.baselibrary.adapter.BaseRecyclerAdapter;
import com.musketeer.baselibrary.bean.BaseEntity;
import com.musketeer.datasearch.MainApplication;
import com.musketeer.datasearch.adapter.MonitorRespListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhongxuqi on 16-5-15.
 */
public class MonitorResultResp extends BaseEntity {
    public static final String SEPARATION = ",";

    @DatabaseField(generatedId = true)
    protected int id;
    @DatabaseField
    protected String Url;
    @DatabaseField
    protected String Keyword;
    @DatabaseField
    protected String Title;
    @DatabaseField
    protected String itemUrls;
    @DatabaseField
    protected long time;
    @DatabaseField
    protected boolean isNew;

    protected List<UnionItemBean> ResultData;
    protected BaseRecyclerAdapter.OnItemClickListener<MonitorResultResp> clickListener;
    protected BaseRecyclerAdapter.OnItemDeleteListener<MonitorResultResp> deleteListener;
    protected MonitorRespListAdapter.OnRootClickListener rootClickListener;

    public MonitorResultResp() {
        this.time = System.currentTimeMillis();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getKeyword() {
        return Keyword;
    }

    public void setKeyword(String keyword) {
        Keyword = keyword;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getItemUrls() {
        return itemUrls;
    }

    public void setItemUrls(String itemUrls) {
        this.itemUrls = itemUrls;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public BaseRecyclerAdapter.OnItemClickListener<MonitorResultResp> getClickListener() {
        return clickListener;
    }

    public void setClickListener(BaseRecyclerAdapter.OnItemClickListener<MonitorResultResp> clickListener) {
        this.clickListener = clickListener;
    }

    public BaseRecyclerAdapter.OnItemDeleteListener<MonitorResultResp> getDeleteListener() {
        return deleteListener;
    }

    public void setDeleteListener(BaseRecyclerAdapter.OnItemDeleteListener<MonitorResultResp> deleteListener) {
        this.deleteListener = deleteListener;
    }

    public MonitorRespListAdapter.OnRootClickListener getRootClickListener() {
        return rootClickListener;
    }

    public void setRootClickListener(MonitorRespListAdapter.OnRootClickListener rootClickListener) {
        this.rootClickListener = rootClickListener;
    }

    public void setResultData(List<UnionItemBean> resultData) {
        ResultData = resultData;
    }

    public List<UnionItemBean> getResultData() {
        return ResultData;
    }

    public List<SearchResultEntity> getSearchResultData() {
        List<SearchResultEntity> list = new ArrayList<>();
        if (ResultData == null && itemUrls != null) {
            for (String itemUrl: itemUrls.split(",")) {
                SearchResultEntity item = MainApplication.getInstance().getSearchResultDao().getDataByLink(itemUrl);
                if (item == null) continue;
                list.add(item);
            }
        } else if (ResultData != null) {
            for (UnionItemBean item: ResultData) {
                list.add(item.getTask().getInfo());
            }
        }
        return list;
    }
}
