/**   
* @Title: DAOManager.java 
* @Package com.musketeer.datasearch.db 
*
* @author musketeer zhongxuqi@163.com  
* @date 2015-3-28 下午7:25:16 
* @version V1.0   
*/
package com.musketeer.datasearch.db;

import java.sql.SQLException;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.musketeer.baselibrary.util.LogUtils;
import com.musketeer.datasearch.entity.MonitorResultResp;
import com.musketeer.datasearch.entity.SearchResultEntity;
import com.musketeer.datasearch.entity.UnionResultResp;
import com.musketeer.datasearch.entity.WebEntity;
import com.musketeer.datasearch.entity.WebGroupEntity;

public class DaoManager {
	private static final String TAG = DaoManager.class.getSimpleName();
	
	private OrmLiteSqliteOpenHelper helper;
	
	public DaoManager(Context context){
		helper = OpenHelperManager.getHelper(context, DaoHelper.class);
	}
	
	public Dao<SearchResultEntity, Integer> getSearchResultDao(){
		Dao<SearchResultEntity, Integer> dao = null;
		try {
			dao = helper.getDao(SearchResultEntity.class);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtils.e(TAG, "[DaoManager]create msg dao exception " + e.getMessage());
		}
		return dao;
	}
	
	public Dao<WebEntity, Integer> getWebDao(){
		Dao<WebEntity, Integer> dao = null;
		try {
			dao = helper.getDao(WebEntity.class);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtils.e(TAG, "[DaoManager]create msg dao exception "+e.getMessage());
		}
		return dao;
	}
	
	public Dao<WebGroupEntity, Integer> getWebGroupDao(){
		Dao<WebGroupEntity, Integer> dao = null;
		try {
			dao = helper.getDao(WebGroupEntity.class);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtils.e(TAG, "[DaoManager]create msg dao exception "+e.getMessage());
		}
		return dao;
	}

	public Dao<UnionResultResp, Integer> getUnionRespDao(){
		Dao<UnionResultResp, Integer> dao = null;
		try {
			dao = helper.getDao(UnionResultResp.class);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtils.e(TAG, "[DaoManager]create msg dao exception "+e.getMessage());
		}
		return dao;
	}

	public Dao<MonitorResultResp, Integer> getMonitorRespDao(){
		Dao<MonitorResultResp, Integer> dao = null;
		try {
			dao = helper.getDao(MonitorResultResp.class);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtils.e(TAG, "[DaoManager]create msg dao exception "+e.getMessage());
		}
		return dao;
	}

}
