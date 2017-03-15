package com.musketeer.datasearch.activity;

import com.musketeer.baselibrary.Activity.BaseActivity;
import com.musketeer.baselibrary.adapter.FragmentAdapter;
import com.musketeer.datasearch.MainApplication;
import com.musketeer.datasearch.R;
import com.musketeer.datasearch.fragment.MonitorSearchFragment;
import com.musketeer.datasearch.fragment.TabSearchFragment;
import com.musketeer.datasearch.fragment.PersonalRecordFragment;
import com.musketeer.datasearch.fragment.ScannerFragment;
import com.musketeer.datasearch.entity.WebGroupEntity;
import com.musketeer.datasearch.entity.WebsUpdateEvent;
import com.musketeer.datasearch.fragment.UnionSearchFragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import cn.jpush.android.api.JPushInterface;

public class MainActivity extends BaseActivity {
	public static final String TAG = "MainActivity";

	public static final int SCANNER_CODE=1;
	
	private static MainActivity instance;
	private long exitTime=0;

	@Bind(R.id.drawer_layout)
	public DrawerLayout mDrawerLayout;
	protected FragmentAdapter mFragmentAdapter;
	protected Handler handler = new Handler();
	
	//fragments
	private PersonalRecordFragment mPersonalRecordFragment;
	private UnionSearchFragment mUnionSearchFragment;
	private TabSearchFragment mTabSearchFragment;
	private MonitorSearchFragment mMonitorSearchFragment;
	private ScannerFragment mScannerFragment;

	public static MainActivity getInstance() {
		return instance;
	}

	@Override
	public void setContentView(Bundle savedInstanceState) {
		setContentView(R.layout.activity_main);
		instance=this;
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initEvent() {
		// TODO Auto-generated method stub
		findViewById(R.id.personal_record).setOnClickListener(this);
		findViewById(R.id.union_search).setOnClickListener(this);
		findViewById(R.id.multirow_search).setOnClickListener(this);
		findViewById(R.id.monitor_search).setOnClickListener(this);
		findViewById(R.id.two_dimension_scanner).setOnClickListener(this);
		findViewById(R.id.settings).setOnClickListener(this);
		findViewById(R.id.help).setOnClickListener(this);
		EventBus.getDefault().register(this);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		
		//初始化fragment
		mPersonalRecordFragment = new PersonalRecordFragment();
		mUnionSearchFragment = new UnionSearchFragment();
        mTabSearchFragment = new TabSearchFragment();
		mMonitorSearchFragment = new MonitorSearchFragment();
        mScannerFragment=new ScannerFragment();
        
        //显示MainFragment
		mFragmentAdapter = new FragmentAdapter(getFragmentManager(), R.id.fragment_content);
		mFragmentAdapter.addFragment(mUnionSearchFragment);
		mFragmentAdapter.addFragment(mTabSearchFragment);
		mFragmentAdapter.addFragment(mMonitorSearchFragment);
		mFragmentAdapter.addFragment(mPersonalRecordFragment);
		mFragmentAdapter.addFragment(mScannerFragment);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
			case R.id.personal_record:
				mFragmentAdapter.showFragment(mPersonalRecordFragment);
				mDrawerLayout.closeDrawers();
				break;
			case R.id.union_search:
				mFragmentAdapter.showFragment(mUnionSearchFragment);
				mDrawerLayout.closeDrawers();
				break;
			case R.id.multirow_search:
				mFragmentAdapter.showFragment(mTabSearchFragment);
				mDrawerLayout.closeDrawers();
				break;
			case R.id.monitor_search:
				mFragmentAdapter.showFragment(mMonitorSearchFragment);
				mDrawerLayout.closeDrawers();
				break;
			case R.id.two_dimension_scanner:
				mFragmentAdapter.showFragment(mScannerFragment);
				mDrawerLayout.closeDrawers();
				break;
			case R.id.settings:
				startActivity(SettingActivity.class);
				mDrawerLayout.closeDrawers();
				break;
			case R.id.help:
				startActivity(HelpActivity.class);
				mDrawerLayout.closeDrawers();
				break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
	    	if (mScannerFragment.isVisible()&&
	    			mScannerFragment.mURIResult!=null&&
	    			mScannerFragment.mURIResult.canGoBack()) {
	    		mScannerFragment.mURIResult.goBack();
				return true;
	    	}
	    	
	        if((System.currentTimeMillis()-exitTime) > 2000){
	        	showCustomToast("再按一次退出程序");                            
	            exitTime=System.currentTimeMillis();   
	        } else {
	            finish();
	            System.exit(0);
	        }
	        return true;   
	    }
	    return super.onKeyDown(keyCode, event);
	}

	@Subscribe
	public void onEvent(WebsUpdateEvent event) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (MainActivity.this.isDestroyed()) return;
				if (MainApplication.getInstance().getWebGroupDao().getSelectedWebGroup()==null) {
					mTabSearchFragment.mSelectedWebGroup=new WebGroupEntity();
				} else if (mTabSearchFragment.mSelectedWebGroup.getWebs().equals(
						MainApplication.getInstance().getWebGroupDao().getSelectedWebGroup().getWebs())) {
					return;
				}
				boolean isShowed = mTabSearchFragment.isVisible();
				mFragmentAdapter.removeFragment(mTabSearchFragment);
				mTabSearchFragment.onPause();
				mTabSearchFragment.onStop();
				mTabSearchFragment.onDestroyView();
				mTabSearchFragment = new TabSearchFragment();
				mFragmentAdapter.addFragment(mTabSearchFragment);
				if (isShowed) mFragmentAdapter.showFragment(mTabSearchFragment);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case SCANNER_CODE:
			mScannerFragment.onActivityResult(requestCode, resultCode, data);
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		JPushInterface.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		JPushInterface.onPause(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
