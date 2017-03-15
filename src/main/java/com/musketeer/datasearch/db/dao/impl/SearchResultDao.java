/**   
* @Title: SearchResultDao.java 
* @Package com.musketeer.datasearch.db.dao.impl 
*
* @author musketeer zhongxuqi@163.com  
* @date 2015-3-28 下午7:30:38 
* @version V1.0   
*/
package com.musketeer.datasearch.db.dao.impl;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.musketeer.baselibrary.util.LogUtils;
import com.musketeer.datasearch.MainApplication;
import com.musketeer.datasearch.db.BaseDaoI;
import com.musketeer.datasearch.db.dao.SearchResultDaoI;
import com.musketeer.datasearch.entity.SearchResultEntity;

public class SearchResultDao extends BaseDaoI<SearchResultEntity> implements SearchResultDaoI {
	private static SearchResultDao taskManager = null;
	
	public SearchResultDao(Dao<SearchResultEntity, Integer> taskDao) {
		super(taskDao);
	}
	
	public static SearchResultDao getInstance(Dao<SearchResultEntity, Integer> taskDao){
		if (taskManager == null) {
			taskManager = new SearchResultDao(taskDao);
		}
		return taskManager;
	}

	@Override
	public boolean insert(SearchResultEntity item) {
		if (getDataByLink(item.getLink()) != null) return true;
		item.setRecorded(false);
		return super.insert(item);
	}

	@Override
	public boolean delete(SearchResultEntity item) {
		if (MainApplication.getInstance().getUnionRespDao().hasItemUrl(item.getLink()) ||
				MainApplication.getInstance().getMonitorRespDao().hasItemUrl(item.getLink())) {
			item.setRecorded(false);
			update(item);
			return true;
		} else {
			return super.deleteById(item.getId());
		}
	}

	@Override
	public SearchResultEntity getDataByLink(String link) {
		// TODO Auto-generated method stub
		QueryBuilder<SearchResultEntity, Integer> queryBuilder = taskDao.queryBuilder();
		try {
			queryBuilder.where().eq("Link", link);
			return queryBuilder.queryForFirst();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean deleteDataByUrl(String link) {
		// TODO Auto-generated method stub
		DeleteBuilder<SearchResultEntity, Integer> deleteBuilder = taskDao.deleteBuilder();
		try {
			deleteBuilder.where().eq("Link", link);
			return deleteBuilder.delete() > 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public List<SearchResultEntity> getRecordDataByParentId(int parentId) {
		// TODO Auto-generated method stub
		QueryBuilder<SearchResultEntity, Integer> queryBuilder = taskDao.queryBuilder();
		try {
			queryBuilder.where().eq("parentId", parentId).and().eq("isRecorded", true);
			return queryBuilder.query();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void deleteDataByParentId(int parentId) {
		// TODO Auto-generated method stub
		List<SearchResultEntity> itemList = getRecordDataByParentId(parentId);
		if (itemList == null) return;
		for (SearchResultEntity item: itemList) {
			deleteDataByUrlIfUseless(item.getLink());
		}
	}

	@Override
	public List<SearchResultEntity> getAllFolderData() {
		// TODO Auto-generated method stub
		QueryBuilder<SearchResultEntity, Integer> queryBuilder = taskDao.queryBuilder();
		try {
			queryBuilder.where().eq("isFolder", true);
			return queryBuilder.query();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void deleteDataByUrlIfUseless(String url) {
		if (MainApplication.getInstance().getUnionRespDao().hasItemUrl(url) ||
				MainApplication.getInstance().getMonitorRespDao().hasItemUrl(url)) {
			SearchResultEntity entity = MainApplication.getInstance().getSearchResultDao().getDataByLink(url);
			entity.setRecorded(false);
			MainApplication.getInstance().getSearchResultDao().update(entity);
			return;
		}
		deleteDataByUrl(url);
	}

}
