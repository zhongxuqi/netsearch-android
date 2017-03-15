package com.musketeer.datasearch.entity;

import com.j256.ormlite.field.DatabaseField;
import com.musketeer.baselibrary.adapter.BaseRecyclerAdapter;
import com.musketeer.baselibrary.bean.BaseEntity;
import com.musketeer.datasearch.MainApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhongxuqi on 16-5-13.
 */
public class UnionResultResp extends BaseEntity {
    public static final String SEPARATION = ",";

    @DatabaseField(generatedId = true)
    protected int id;
    @DatabaseField
    protected String Keyword;
    @DatabaseField
    protected String itemUrls;
    @DatabaseField
    protected String ParserNames;
    @DatabaseField
    protected long time;
    @DatabaseField
    protected boolean isNew;

    protected List<UnionItemBean> ResultData;
    protected BaseRecyclerAdapter.OnItemClickListener<UnionResultResp> clickListener;
    protected BaseRecyclerAdapter.OnItemDeleteListener<UnionResultResp> deleteListener;

    public UnionResultResp() {
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

    public String getKeyword() {
        return Keyword;
    }

    public void setKeyword(String keyword) {
        Keyword = keyword;
    }

    public String getItemUrls() {
        return itemUrls;
    }

    public void setItemUrls(String itemUrls) {
        this.itemUrls = itemUrls;
    }

    public String getParserNames() {
        return ParserNames;
    }

    public void setParserNames(String parserNames) {
        ParserNames = parserNames;
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

    public void setResultData(List<UnionItemBean> resultData) {
        ResultData = resultData;
    }

    public List<UnionItemBean> getResultData() {
        return ResultData;
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

    public BaseRecyclerAdapter.OnItemClickListener<UnionResultResp> getClickListener() {
        return clickListener;
    }

    public void setClickListener(BaseRecyclerAdapter.OnItemClickListener<UnionResultResp> clickListener) {
        this.clickListener = clickListener;
    }

    public BaseRecyclerAdapter.OnItemDeleteListener<UnionResultResp> getDeleteListener() {
        return deleteListener;
    }

    public void setDeleteListener(BaseRecyclerAdapter.OnItemDeleteListener<UnionResultResp> deleteListener) {
        this.deleteListener = deleteListener;
    }

    public List<WebEntity> getWebEntitys() {
        List<WebEntity> list=new ArrayList<>();
        if (this.ParserNames==null) {
            return list;
        }
        String[] ParserNames=this.ParserNames.split(",");
        for (int i=0;i<ParserNames.length;i++) {
            WebEntity web= MainApplication.getInstance().getWebDao().getDataByParserName(ParserNames[i]);
            if (web!=null) {
                list.add(web);
            }
        }
        return list;
    }
}
