/**   
* @Title: WebGroupDao.java 
* @Package com.musketeer.datasearch.db.dao.impl 
*
* @author musketeer zhongxuqi@163.com  
* @date 2015-4-11 下午12:14:07 
* @version V1.0   
*/
package com.musketeer.datasearch.db.dao.impl;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.musketeer.datasearch.db.BaseDaoI;
import com.musketeer.datasearch.db.dao.WebGroupDaoI;
import com.musketeer.datasearch.entity.WebGroupEntity;

public class WebGroupDao extends BaseDaoI<WebGroupEntity> implements WebGroupDaoI {
	private static WebGroupDao taskManager = null;
	
	public WebGroupDao(Dao<WebGroupEntity, Integer> taskDao) {
		super(taskDao);
	}
	
	public static WebGroupDao getInstance(Dao<WebGroupEntity, Integer> taskDao) {
		if (taskManager == null) {
			taskManager = new WebGroupDao(taskDao);
		}
		return taskManager;
	}

	@Override
	public WebGroupEntity getSelectedWebGroup() {
		// TODO Auto-generated method stub
		QueryBuilder<WebGroupEntity, Integer> queryBuilder = taskDao.queryBuilder();
		try {
			queryBuilder.where().eq("isSelected", true);
			return queryBuilder.queryForFirst();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
