/**   
* @Title: SearchResultPageAdapter.java 
* @Package com.musketeer.datasearch.adapter 
*
* @author musketeer zhongxuqi@163.com  
* @date 2015-3-15 下午3:34:01 
* @version V1.0   
*/
package com.musketeer.datasearch.adapter;

import java.util.ArrayList;
import java.util.List;

import com.musketeer.datasearch.fragment.SearchResultFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;

/**
 * @author zhongxuqi
 */
public class SearchResultPageAdapter extends FragmentPagerAdapter{
	private List<SearchResultFragment> mSearchResultList;

	public SearchResultPageAdapter(FragmentManager fm, List<SearchResultFragment> list) {
		super(fm);
		// TODO Auto-generated constructor stub
		if (list!=null) {
			mSearchResultList=list;
		} else {
			mSearchResultList=new ArrayList<>();
		}
	}

	@Override
	public SearchResultFragment getItem(int postion) {
		// TODO Auto-generated method stub
		return mSearchResultList.get(postion);
	}
	
	@Override
    public CharSequence getPageTitle(int position) {
        return getItem(position).mWeb.getName();
    }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mSearchResultList.size();
	}
	
	public void refreshList(List<SearchResultFragment> list) {
		mSearchResultList.clear();
		mSearchResultList.addAll(list);
		notifyDataSetChanged();
	}
	
	@Override  
    public int getItemPosition(Object object) {  
        // TODO Auto-generated method stub  
        return PagerAdapter.POSITION_NONE;
    }

}
