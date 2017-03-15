/**   
* @Title: AppContant.java 
* @Package com.musketeer.datasearch.common 
*
* @author musketeer zhongxuqi@163.com  
* @date 2015-3-18 下午7:50:05 
* @version V1.0   
*/
package com.musketeer.datasearch.common;

import java.util.HashSet;
import java.util.Set;

public class AppContant {
	public static final int WEBVIEW_LOAD=0;
	public static final int BROWSER_LOAD=1;

	public static final Set<String> BanKeyWords = new HashSet<>();

	static {
		BanKeyWords.add("苍井空");
	}
}
