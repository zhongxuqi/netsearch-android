/**   
* @Title: MainApplication.java 
* @Package com.musketeer.datasearch 
*
* @author musketeer zhongxuqi@163.com  
* @date 2015-3-15 下午12:58:25 
* @version V1.0   
*/
package com.musketeer.datasearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.musketeer.baselibrary.BaseApplication;
import com.musketeer.baselibrary.bean.RequestResult;
import com.musketeer.baselibrary.bean.RequestTask;
import com.musketeer.baselibrary.net.UIUpdateTask;
import com.musketeer.baselibrary.paser.JsonPaser;
import com.musketeer.baselibrary.util.LogUtils;
import com.musketeer.baselibrary.util.SharePreferenceUtils;
import com.musketeer.baselibrary.util.StringUtils;
import com.musketeer.datasearch.common.SharePreferenceConfig;
import com.musketeer.datasearch.db.DaoManager;
import com.musketeer.datasearch.db.dao.impl.MonitorRespDao;
import com.musketeer.datasearch.db.dao.impl.SearchResultDao;
import com.musketeer.datasearch.db.dao.impl.UnionRespDao;
import com.musketeer.datasearch.db.dao.impl.WebDao;
import com.musketeer.datasearch.db.dao.impl.WebGroupDao;
import com.musketeer.datasearch.entity.WebEntity;
import com.musketeer.datasearch.entity.WebGroupEntity;
import com.musketeer.datasearch.entity.WebListResponse;
import com.musketeer.datasearch.entity.WebsUpdateEvent;
import com.musketeer.datasearch.manager.MonitorTaskManager;
import com.musketeer.datasearch.service.MainImageLoadService;
import com.musketeer.datasearch.service.MainService;
import com.musketeer.datasearch.util.Net;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * @author zhongxuqi
 */
public class MainApplication extends BaseApplication {
	public static final String TAG = "MainApplication";

	public static MainApplication instance;
	
	public List<WebEntity> mAllWebs;
	private DaoManager daoManager;
	private SearchResultDao mSearchResultDao;
	private WebDao mWebDao;
	private WebGroupDao mWebGroupDao;
	private UnionRespDao mUnionRespDao;
	private MonitorRespDao mMonitorRespDao;

	public Map<String,Object> pipeline;
	
	public static MainApplication getInstance() {
		return instance;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		LogUtils.d(TAG, "[MainApplicaction] On Create");
		instance=this;
		pipeline=new HashMap<>();
		startService(MainService.class);
		startService(MainImageLoadService.class);
		
		daoManager = new DaoManager(this);

		JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
		JPushInterface.init(this);     		// 初始化 JPush

		//初始化数据
		initWebList();
		MonitorTaskManager.getInstance().initMonitorTask();
	}

	protected void initWebList() {
		mAllWebs = getWebDao().getAllData();
		if (mAllWebs == null) {
			mAllWebs = new ArrayList<>();
		}
		RequestTask<WebListResponse> task = new RequestTask<>(RequestTask.RequestType.GET, false,
			new JsonPaser<WebListResponse>() {
				@Override
				public WebListResponse BuildModel(JSONObject jsonObject) throws JSONException {
					LogUtils.d(TAG, jsonObject.toString());
					return JSON.parseObject(jsonObject.toString(), WebListResponse.class);
				}
			}, new UIUpdateTask<WebListResponse>() {
				@Override
				public void doUIUpdate(RequestResult<WebListResponse> result) {
					if (!result.isOk) return;
					if (result.resultObj.getWebs() == null) return;
					for (WebEntity web: result.resultObj.getWebs()) {
						boolean isFound = false;
						for (int i = 0; i < mAllWebs.size(); i++) {
							if (web.getUrl().equals(mAllWebs.get(i).getUrl())) {
								isFound = true;
								mAllWebs.get(i).setLogo(web.getLogo());
								mAllWebs.get(i).setName(web.getName());
								mAllWebs.get(i).setParserName(web.getParserName());
								getWebDao().update(mAllWebs.get(i));
								break;
							}
						}
						if (!isFound) {
							getWebDao().insert(web);
							mAllWebs.add(getWebDao().getDataByUrl(web.getUrl()));
						}
					}
					for (WebEntity web: mAllWebs) {
						boolean isFound = false;
						for (int i = 0; i < result.resultObj.getWebs().size(); i++) {
							LogUtils.d(TAG, web.getUrl() + ";" + result.resultObj.getWebs().get(i).getUrl());
							if (web.getUrl().equals(result.resultObj.getWebs().get(i).getUrl())) {
								isFound = true;
								break;
							}
						}
						if (!isFound) {
							getWebDao().delete(web);
						}
					}
					mAllWebs = getWebDao().getAllData();

					initWebGroup();
				}
		});
		task.setUrl(Net.getAbsoluteUrl("/app/list"));
		MainService.addTask(task);
	}

	protected void initWebGroup() {
		List<WebGroupEntity> WebGroupList = getWebGroupDao().getAllData();
		if (WebGroupList != null) {
			for (WebGroupEntity group: WebGroupList) {
				String urls = "";
				for (String url: group.getWebs().split(",")) {
					if (getWebDao().getDataByUrl(url) != null) {
						if (!StringUtils.isEmpty(url)) {
							urls = urls.concat(",");
						}
						urls = urls.concat(url);
					}
				}
				if (!StringUtils.isEmpty(urls)) {
					group.setWebs(urls);
					getWebGroupDao().update(group);
				} else {
					getWebGroupDao().delete(group);
				}
			}
		}
		if (SharePreferenceUtils.getBoolean(this, SharePreferenceConfig.IsFirstOpen, true)) {
			String[] selectedList = {"Google", "Bing", "百度"};
			List<WebEntity> list=new ArrayList<>();
			for (String item:selectedList) {
				for (WebEntity web: mAllWebs) {
					if (web.getName().equals(item)) {
						list.add(web);
						break;
					}
				}
			}
			WebGroupEntity webGroup=new WebGroupEntity(list);
			webGroup.setGroupName("默认");
			webGroup.setSelected(true);
			getWebGroupDao().insert(webGroup);
			SharePreferenceUtils.putBoolean(this, SharePreferenceConfig.IsFirstOpen, false);
		}
		EventBus.getDefault().post(new WebsUpdateEvent(WebsUpdateEvent.UpdateType.FROM_NET));
	}
	
	/**
	 * 获取链接数据
	 */
	public SearchResultDao getSearchResultDao() {
		if (mSearchResultDao == null) {
			mSearchResultDao = SearchResultDao.getInstance(daoManager.getSearchResultDao());
		}
		return mSearchResultDao;
	}
	
	public WebDao getWebDao() {
		if (mWebDao == null) {
			mWebDao = WebDao.getInstance(daoManager.getWebDao());
		}
		return mWebDao;
	}
	
	public WebGroupDao getWebGroupDao() {
		if (mWebGroupDao == null) {
			mWebGroupDao = WebGroupDao.getInstance(daoManager.getWebGroupDao());
		}
		return mWebGroupDao;
	}

	public UnionRespDao getUnionRespDao() {
		if (mUnionRespDao == null) {
			mUnionRespDao = UnionRespDao.getInstance(daoManager.getUnionRespDao());
		}
		return mUnionRespDao;
	}

	public MonitorRespDao getMonitorRespDao() {
		if (mMonitorRespDao == null) {
			mMonitorRespDao = MonitorRespDao.getInstance(daoManager.getMonitorRespDao());
		}
		return mMonitorRespDao;
	}

}
