package com.musketeer.datasearch.db.dao.impl;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.musketeer.datasearch.MainApplication;
import com.musketeer.datasearch.db.BaseDaoI;
import com.musketeer.datasearch.entity.SearchResultEntity;
import com.musketeer.datasearch.entity.UnionResultResp;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by zhongxuqi on 16-5-13.
 */
public class UnionRespDao extends BaseDaoI<UnionResultResp> {
    private static UnionRespDao taskManager = null;

    public UnionRespDao(Dao<UnionResultResp, Integer> taskDao) {
        super(taskDao);
    }

    public static UnionRespDao getInstance(Dao<UnionResultResp, Integer> taskDao){
        if (taskManager == null) {
            taskManager = new UnionRespDao(taskDao);
        }
        return taskManager;
    }

    @Override
    public boolean insert(UnionResultResp unionResultResp) {
        for (SearchResultEntity item: unionResultResp.getSearchResultData()) {
            MainApplication.getInstance().getSearchResultDao().insert(item);
        }
        String itemUrls = "";
        for (int i = 0; i < unionResultResp.getSearchResultData().size(); i++) {
            if (i > 0) {
                itemUrls += ",";
            }
            itemUrls += unionResultResp.getSearchResultData().get(i).getLink();
        }
        unionResultResp.setItemUrls(itemUrls);
        unionResultResp.setNew(true);
        return super.insert(unionResultResp);
    }

    @Override
    public boolean deleteById(int id) {
        boolean result = super.deleteById(id);
        UnionResultResp unionItem = getDataById(id);
        if (unionItem == null) return true;
        for (String url: unionItem.getItemUrls().split(UnionResultResp.SEPARATION)) {
            SearchResultEntity resultItem = MainApplication.getInstance().getSearchResultDao().getDataByLink(url);
            if (resultItem == null || resultItem.isRecorded()) continue;
            result &= MainApplication.getInstance().getSearchResultDao().delete(resultItem);
        }
        return result;
    }

    public boolean hasItemUrl(String url) {
        QueryBuilder<UnionResultResp, Integer> queryBuilder = taskDao.queryBuilder();
        try {
            return queryBuilder.where().like("itemUrls", "%" + url + "%").countOf() > 0;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public List<UnionResultResp> getAllDataOrderByTime() {
        QueryBuilder<UnionResultResp, Integer> queryBuilder = taskDao.queryBuilder();
        try {
            return queryBuilder.orderBy("time", false).query();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public UnionResultResp getDataByTime(long time) {
        QueryBuilder<UnionResultResp, Integer> queryBuilder = taskDao.queryBuilder();
        try {
            return queryBuilder.where().eq("time", time).queryForFirst();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
