/**   
* @Title: SearchResultComparator.java 
* @Package com.musketeer.datasearch.other 
*
* @author musketeer zhongxuqi@163.com  
* @date 2015-3-29 下午12:05:04 
* @version V1.0   
*/
package com.musketeer.datasearch.other;

import java.util.Comparator;

import com.musketeer.datasearch.entity.SearchResultEntity;

public class SearchResultComparator implements Comparator<SearchResultEntity> {

	@Override
	public int compare(SearchResultEntity lhs, SearchResultEntity rhs) {
		// TODO Auto-generated method stub
		if (lhs.isFolder()&&rhs.isFolder()) {
			return lhs.getTitle().compareTo(rhs.getTitle());
		} else if (lhs.isFolder()) {
			return -1;
		} else if (rhs.isFolder()) {
			return 1;
		} else {
			return lhs.getTitle().compareTo(rhs.getTitle());
		}
	}

}
