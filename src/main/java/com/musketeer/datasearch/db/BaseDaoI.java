/**   
* @Title: BaseDaoI.java 
* @Package com.musketeer.datasearch.db 
*
* @author musketeer zhongxuqi@163.com  
* @date 2015-3-28 下午7:26:42 
* @version V1.0   
*/
package com.musketeer.datasearch.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.musketeer.baselibrary.bean.BaseEntity;

import java.sql.SQLException;
import java.util.List;

public class BaseDaoI<T extends BaseEntity> {
	protected Dao<T, Integer> taskDao = null;

	public BaseDaoI(Dao<T, Integer> taskDao) {
		this.taskDao = taskDao;
	}

	/**
	 * 添加一条数据
	 * <p>Title: insert
	 * <p>Description: 
	 * @param t
	 * @return
	 */
	public boolean insert(T t) {
		try {
			return taskDao.create(t) == 1;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 添加或更新一条数据
	 * <p>Title: insert
	 * <p>Description:
	 * @param t
	 * @return
	 */
	public boolean insertOrUpdate(T t) {
		if (isExist(t.getId())) {
			update(t);
			return true;
		}else{
			return insert(t);
		}
	}
	
	/**
	 * 添加多条数据
	 * <p>Title: insert
	 * <p>Description: 
	 * @param list
	 * @return
	 */
	public boolean insert(List<T> list) {
		if (list == null) {
			return false;
		}
		for (int i = 0; i < list.size(); i++) {
			insert(list.get(i));
		}
		return true;
	}
	
	/**
	 * 删除数据
	 * <p>Title: delete
	 * <p>Description: 
	 * @param t
	 * @return
	 */
	public boolean delete(T t) {
		return deleteById(t.getId());
	}
	
	/**
	 * 通过id删除数据
	 * <p>Title: deleteById
	 * <p>Description: 
	 * @param id
	 * @return
	 */
	public boolean deleteById(int id) {
		DeleteBuilder<T, Integer> deleteBuilder = taskDao.deleteBuilder();
		try {
			deleteBuilder.where().eq("id", id);
			return deleteBuilder.delete() > 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 更新数据
	 * <p>Title: update
	 * <p>Description: 
	 * @param t
	 */
	public void update(T t) {
		try {
			taskDao.update(t);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 通过id更新数据
	 * <p>Title: updateById
	 * <p>Description: 
	 * @param t
	 * @param id
	 */
	public void updateById(T t, int id) {
		t.setId(id);
		update(t);
	}
	
	/**
	 * 通过id获取数据
	 * <p>Title: getDataById
	 * <p>Description: 
	 * @param id
	 * @return
	 */
	public T getDataById(int id) {
		QueryBuilder<T, Integer> queryBuilder = taskDao.queryBuilder();
		try {
			queryBuilder.where().eq("id", id);
			return queryBuilder.queryForFirst();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取所有数据
	 * <p>Title: getAllData
	 * <p>Description: 
	 * @return
	 */
	public List<T> getAllData() {
		QueryBuilder<T, Integer> queryBuilder = taskDao.queryBuilder();
		try {
			return queryBuilder.query();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public boolean isExist(int id) {
		// TODO Auto-generated method stub
		QueryBuilder<T, Integer> queryBuilder = taskDao.queryBuilder();
		try {
			queryBuilder.where().eq("id", id);
			return queryBuilder.countOf() > 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public boolean deleteAll() {
		DeleteBuilder<T, Integer> deleteBuilder = taskDao.deleteBuilder();
		try {
			return deleteBuilder.delete() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

}
