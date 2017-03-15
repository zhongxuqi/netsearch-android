/**   
* @Title: DAOHelper.java 
* @Package com.musketeer.datasearch.db 
*
* @author musketeer zhongxuqi@163.com  
* @date 2015-3-28 下午7:20:38 
* @version V1.0   
*/
package com.musketeer.datasearch.db;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.musketeer.baselibrary.util.LogUtils;
import com.musketeer.datasearch.entity.MonitorResultResp;
import com.musketeer.datasearch.entity.SearchResultEntity;
import com.musketeer.datasearch.entity.UnionResultResp;
import com.musketeer.datasearch.entity.WebEntity;
import com.musketeer.datasearch.entity.WebGroupEntity;

public class DaoHelper extends OrmLiteSqliteOpenHelper {
	private static final String TAG = "DaoHelper";
	
	private static final String DATABASE_NAME = "data.db"; // 数据库名
	private static final int DATABASE_VERSION = 5;
	
	public DaoHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
		// TODO Auto-generated method stub
		try {
			LogUtils.d(TAG, "create tables");
			TableUtils.createTable(connectionSource, SearchResultEntity.class);
			TableUtils.createTable(connectionSource, WebEntity.class);
			TableUtils.createTable(connectionSource, WebGroupEntity.class);
			TableUtils.createTable(connectionSource, UnionResultResp.class);
			TableUtils.createTable(connectionSource, MonitorResultResp.class);
			LogUtils.d("DBHelper", "[DAOHelper]create all table");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtils.e("DBHelper", "[DAOHelper]create table fail "+e.getMessage());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		try {
			LogUtils.d(TAG, "drop tables");
			TableUtils.dropTable(connectionSource, SearchResultEntity.class, true);
			TableUtils.dropTable(connectionSource, WebEntity.class, true);
			TableUtils.dropTable(connectionSource, WebGroupEntity.class, true);
			TableUtils.dropTable(connectionSource, UnionResultResp.class, true);
			TableUtils.dropTable(connectionSource, MonitorResultResp.class, true);
			onCreate(database, connectionSource);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtils.e("DBHelper", "[DAOHelper]create table fail "+e.getMessage());
		}
	}

}
