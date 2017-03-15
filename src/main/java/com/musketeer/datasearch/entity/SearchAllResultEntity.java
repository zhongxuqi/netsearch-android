/**   
* @Title: SearchAllResultEntity.java 
* @Package com.musketeer.datasearch.entity 
*
* @author musketeer zhongxuqi@163.com  
* @date 2015-3-18 下午8:28:56 
* @version V1.0   
*/
package com.musketeer.datasearch.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchAllResultEntity {
	public Map<String,List<SearchResultEntity>> ResultListMap;
	
	public SearchAllResultEntity() {
		ResultListMap=new HashMap<String,List<SearchResultEntity>>();
	}

}
