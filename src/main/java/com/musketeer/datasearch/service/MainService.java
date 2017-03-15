/**   
* @Title: MainService.java 
* @Package com.musketeer.datasearch.service 
*
* @author musketeer zhongxuqi@163.com  
* @date 2015-3-15 下午12:59:33 
* @version V1.0   
*/
package com.musketeer.datasearch.service;

import com.musketeer.baselibrary.service.BaseService;
import com.musketeer.baselibrary.util.LogUtils;

/**
 * @author zhongxuqi
 *
 */
public class MainService extends BaseService {
private static MainService instance;
	
	public static MainService getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		LogUtils.d(TAG, "[MainService] On Create");
		instance=this;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
