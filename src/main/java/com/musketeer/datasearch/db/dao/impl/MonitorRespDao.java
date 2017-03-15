package com.musketeer.datasearch.db.dao.impl;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.musketeer.datasearch.MainApplication;
import com.musketeer.datasearch.db.BaseDaoI;
import com.musketeer.datasearch.entity.MonitorResultResp;
import com.musketeer.datasearch.entity.SearchResultEntity;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by zhongxuqi on 16-5-15.
 */
public class MonitorRespDao extends BaseDaoI<MonitorResultResp> {
    private static MonitorRespDao taskManager = null;

    public MonitorRespDao(Dao<MonitorResultResp, Integer> taskDao) {
        super(taskDao);
    }

    public static MonitorRespDao getInstance(Dao<MonitorResultResp, Integer> taskDao){
        if (taskManager == null) {
            taskManager = new MonitorRespDao(taskDao);
        }
        return taskManager;
    }

    @Override
    public boolean insert(MonitorResultResp monitorResultResp) {
        for (SearchResultEntity item: monitorResultResp.getSearchResultData()) {
            MainApplication.getInstance().getSearchResultDao().insert(item);
        }
        String itemUrls = "";
        for (int i = 0; i < monitorResultResp.getSearchResultData().size(); i++) {
            if (i > 0) {
                itemUrls += ",";
            }
            itemUrls += monitorResultResp.getSearchResultData().get(i).getLink();
        }
        monitorResultResp.setItemUrls(itemUrls);
        return super.insert(monitorResultResp);
    }

    public boolean updateByNewResp(MonitorResultResp monitorResultResp) {
        MonitorResultResp localResp = MainApplication.getInstance().getMonitorRespDao()
                .getDataByUrlAndKeyWord(monitorResultResp.getUrl(), monitorResultResp.getKeyword());
        if (localResp != null) {
            delete(localResp);
        }
        monitorResultResp.setNew(true);
        return insert(monitorResultResp);
    }

    @Override
    public boolean deleteById(int id) {
        boolean result = super.deleteById(id);
        MonitorResultResp monitorItem = getDataById(id);
        if (monitorItem == null) return true;
        for (String url: monitorItem.getItemUrls().split(MonitorResultResp.SEPARATION)) {
            SearchResultEntity resultItem = MainApplication.getInstance().getSearchResultDao().getDataByLink(url);
            if (resultItem == null || resultItem.isRecorded()) continue;
            result &= MainApplication.getInstance().getSearchResultDao().delete(resultItem);
        }
        return result;
    }

    public boolean hasItemUrl(String url) {
        QueryBuilder<MonitorResultResp, Integer> queryBuilder = taskDao.queryBuilder();
        try {
            return queryBuilder.where().like("itemUrls", "%" + url + "%").countOf() > 0;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public List<MonitorResultResp> getAllDataOrderByTime() {
        QueryBuilder<MonitorResultResp, Integer> queryBuilder = taskDao.queryBuilder();
        try {
            return queryBuilder.orderBy("time", false).query();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public MonitorResultResp getDataByTime(long time) {
        QueryBuilder<MonitorResultResp, Integer> queryBuilder = taskDao.queryBuilder();
        try {
            return queryBuilder.where().eq("time", time).queryForFirst();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public MonitorResultResp getDataByUrlAndKeyWord(String targetUrl, String keyword) {
        QueryBuilder<MonitorResultResp, Integer> queryBuilder = taskDao.queryBuilder();
        try {
            return queryBuilder.where().eq("Url", targetUrl).and().eq("Keyword", keyword).queryForFirst();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public boolean hasUrlAndKeyWord(String targetUrl, String keyword) {
        QueryBuilder<MonitorResultResp, Integer> queryBuilder = taskDao.queryBuilder();
        try {
            return queryBuilder.where().eq("Url", targetUrl).and().eq("Keyword", keyword).countOf() > 0;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
}
