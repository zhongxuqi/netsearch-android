/**   
* @Title: SearchResultDaoI.java 
* @Package com.musketeer.datasearch.db.dao 
*
* @author musketeer zhongxuqi@163.com  
* @date 2015-3-28 下午7:28:34 
* @version V1.0   
*/
package com.musketeer.datasearch.db.dao;

import java.util.List;

import com.musketeer.datasearch.db.BaseDaoI;
import com.musketeer.datasearch.entity.SearchResultEntity;

public interface SearchResultDaoI{

	SearchResultEntity getDataByLink(String link);
	
	boolean deleteDataByUrl(String title);
	
	List<SearchResultEntity> getRecordDataByParentId(int parentId);
	
	void deleteDataByParentId(int parentId);
	
	List<SearchResultEntity> getAllFolderData();
}
