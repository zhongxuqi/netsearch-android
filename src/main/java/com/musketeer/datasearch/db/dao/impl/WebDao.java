/**   
* @Title: WebDao.java 
* @Package com.musketeer.datasearch.db.dao.impl 
*
* @author musketeer zhongxuqi@163.com  
* @date 2015-4-11 上午11:04:13 
* @version V1.0   
*/
package com.musketeer.datasearch.db.dao.impl;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.musketeer.datasearch.db.BaseDaoI;
import com.musketeer.datasearch.db.dao.WebDaoI;
import com.musketeer.datasearch.entity.WebEntity;

public class WebDao extends BaseDaoI<WebEntity> implements WebDaoI {
	private static WebDao taskManager = null;
	
	public WebDao(Dao<WebEntity, Integer> taskDao) {
		super(taskDao);
	}
	
	public static WebDao getInstance(Dao<WebEntity, Integer> taskDao){
		if (taskManager == null) {
			taskManager = new WebDao(taskDao);
		}
		return taskManager;
	}

	@Override
	public WebEntity getDataById(int id) {
		// TODO Auto-generated method stub
		QueryBuilder<WebEntity, Integer> queryBuilder = taskDao.queryBuilder();
		try {
			queryBuilder.where().eq("id", id);
			return queryBuilder.queryForFirst();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public WebEntity getDataByUrl(String url) {
		// TODO Auto-generated method stub
		QueryBuilder<WebEntity, Integer> queryBuilder = taskDao.queryBuilder();
		try {
			queryBuilder.where().eq("Url", url);
			return queryBuilder.queryForFirst();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public WebEntity getDataByParserName(String ParserName) {
		// TODO Auto-generated method stub
		QueryBuilder<WebEntity, Integer> queryBuilder = taskDao.queryBuilder();
		try {
			queryBuilder.where().eq("ParserName", ParserName);
			return queryBuilder.queryForFirst();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
