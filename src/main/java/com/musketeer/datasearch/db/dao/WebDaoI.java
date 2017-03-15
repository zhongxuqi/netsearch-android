/**   
* @Title: WebDaoI.java 
* @Package com.musketeer.datasearch.db.dao 
*
* @author musketeer zhongxuqi@163.com  
* @date 2015-4-11 上午11:02:48 
* @version V1.0   
*/
package com.musketeer.datasearch.db.dao;

import com.musketeer.datasearch.entity.WebEntity;

public interface WebDaoI {
	
	boolean isExist(int id);
	
	WebEntity getDataByUrl(String name);

	boolean deleteAll();

}
