/**   
* @Title: TabSearchFragment.java
* @Package com.musketeer.datasearch.activity.fragment 
*
* @author musketeer zhongxuqi@163.com  
* @date 2015-3-27 下午1:59:42 
* @version V1.0   
*/
package com.musketeer.datasearch.fragment;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.EditText;

import com.flyco.tablayout.SlidingTabLayout;
import com.musketeer.baselibrary.Activity.BaseActivity;
import com.musketeer.baselibrary.Activity.BaseFragment;
import com.musketeer.datasearch.MainApplication;
import com.musketeer.datasearch.R;
import com.musketeer.datasearch.activity.MainActivity;
import com.musketeer.datasearch.adapter.SearchResultPageAdapter;
import com.musketeer.datasearch.common.AppContant;
import com.musketeer.datasearch.entity.SearchFinishEvent;
import com.musketeer.datasearch.entity.WebEntity;
import com.musketeer.datasearch.entity.WebGroupEntity;
import com.musketeer.datasearch.view.HeaderLayoutBar;
import com.musketeer.datasearch.view.LoadingDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;

public class TabSearchFragment extends BaseFragment {
	public static final String TAG = "TabSearchFragment";

	private LoadingDialog mLoadingDialog;

	@Bind(R.id.main_headbar)
	HeaderLayoutBar mHeadBar;
	@Bind(R.id.search_result_page)
	ViewPager mSearchResultPage;
	@Bind(R.id.tab_title)
	SlidingTabLayout mTabLayout;

	private SearchResultPageAdapter mViewPagerAdapter;
	private List<WebEntity> mWebsList;
	private List<SearchResultFragment> mSearchResultList;
	
	//head bar layout
	private EditText mSearchText;
	
	public WebGroupEntity mSelectedWebGroup;
	
	private String OldKeyword="";
	//搜索结果
	private boolean isAllInSearching=false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mLoadingDialog = new LoadingDialog(getActivity());
	}

	@Override
	public void setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		BaseView=inflater.inflate(R.layout.fragment_main, null);
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		mHeadBar.addView(LayoutInflater.from(getActivity()).inflate(R.layout.include_mainpage_headbar, null));
		mSearchText=(EditText) mHeadBar.findViewById(R.id.search_keyword);
		mSearchText.setHint(R.string.search_bar_input_hint);
		mSearchResultPage=(ViewPager) BaseView.findViewById(R.id.search_result_page);
	}

	@Override
	public void initEvent() {
		// TODO Auto-generated method stub
		mHeadBar.findViewById(R.id.left_image_button).setOnClickListener(this);
		mHeadBar.findViewById(R.id.search_button).setOnClickListener(this);

		mSearchText.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode==KeyEvent.KEYCODE_ENTER
						&&mSearchText.getText().toString().trim().length()>0&&
						!mSearchText.getText().toString().trim().equals(OldKeyword)) {
					String keyword = mSearchText.getText().toString().trim().replace(" ", "+");
					OldKeyword=mSearchText.getText().toString().trim();
					isAllInSearching=true;
					if (!AppContant.BanKeyWords.contains(keyword)) {
						showLoadingDialog(R.string.loading);
					} else {
						showCustomToast(getString(R.string.ban_word_hint));
					}
					for (SearchResultFragment fragment:mSearchResultList) {
						try {
							fragment.search(keyword);
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
					return true;
				}
				return false;
			}
			
		});

		EventBus.getDefault().register(this);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		mSearchResultList=new ArrayList<>();
		
		mSelectedWebGroup=MainApplication.getInstance().getWebGroupDao()
				.getSelectedWebGroup();
		if (mSelectedWebGroup==null) {
			mSelectedWebGroup=new WebGroupEntity();
		}
		initWebGroup();
	}
	
	public void initWebGroup() {
		if (mSelectedWebGroup!=null) {
			mWebsList=mSelectedWebGroup.getWebEntitys();
		} else {
			mWebsList=new ArrayList<>();
		}
		mSearchResultList.clear();
		for (WebEntity item:mWebsList) {
			SearchResultFragment fragment=new SearchResultFragment();
			fragment.mWeb=item;
			mSearchResultList.add(fragment);
		}
		mViewPagerAdapter=new SearchResultPageAdapter(
				((BaseActivity)getActivity()).getSupportFragmentManager(), mSearchResultList);
		mSearchResultPage.setAdapter(mViewPagerAdapter);
		mTabLayout.setViewPager(mSearchResultPage);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_image_button:
			MainActivity.getInstance().mDrawerLayout.openDrawer(Gravity.LEFT);
			break;
		case R.id.search_button:
			if (mSearchText.getText().toString().trim().length()>0&&
					!mSearchText.getText().toString().trim().equals(OldKeyword)) {
				String keyword = mSearchText.getText().toString().trim().replace(" ", "+");
				OldKeyword=mSearchText.getText().toString().trim();
				isAllInSearching=true;
				if (!AppContant.BanKeyWords.contains(keyword)) {
					showLoadingDialog(R.string.loading);
				} else {
					showCustomToast(getString(R.string.ban_word_hint));
				}
				for (SearchResultFragment fragment:mSearchResultList) {
					try {
						fragment.search(keyword);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
			break;
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		FragmentTransaction fragmentTransaction = ((BaseActivity)getActivity()).getSupportFragmentManager().beginTransaction();
		for (Fragment fragment: mSearchResultList) {
			fragmentTransaction.remove(fragment);
		}
		fragmentTransaction.commitAllowingStateLoss();
		EventBus.getDefault().unregister(this);
	}

	@Subscribe
	public void onEvent(SearchFinishEvent event) {
		dismissLoadingDialog();
		if (!isAllInSearching) {
			return;
		}
		for (int i = 0; i < mSearchResultList.size(); i++) {
			if (mSearchResultList.get(i).mWeb == event.getmWeb()) {
				mSearchResultPage.setCurrentItem(i, true);
				break;
			}
		}
		isAllInSearching=false;
	}

	/**
	 * 设置LoadingDialog并显示
	 * @param resId
	 */
	protected void showLoadingDialog(int resId) {
		mLoadingDialog.setCancelable(true);
		mLoadingDialog.setMessage(resId);
		mLoadingDialog.show();
	}

	/**
	 * 关闭LoadingDialog
	 */
	protected void dismissLoadingDialog() {
		mLoadingDialog.dismiss();
	}

}
