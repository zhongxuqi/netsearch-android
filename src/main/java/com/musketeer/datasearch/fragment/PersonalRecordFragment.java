/**   
* @Title: PersonalRecordFragment.java 
* @Package com.musketeer.datasearch.activity.fragment 
*
* @author musketeer zhongxuqi@163.com  
* @date 2015-3-28 下午3:14:01 
* @version V1.0   
*/
package com.musketeer.datasearch.fragment;

import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.musketeer.baselibrary.Activity.BaseFragment;
import com.musketeer.baselibrary.util.LogUtils;
import com.musketeer.baselibrary.util.ScreenUtils;
import com.musketeer.baselibrary.util.SharePreferenceUtils;
import com.musketeer.datasearch.MainApplication;
import com.musketeer.datasearch.R;
import com.musketeer.datasearch.activity.MainActivity;
import com.musketeer.datasearch.activity.ResultDetailActivity;
import com.musketeer.datasearch.adapter.DirectionaryListAdapter;
import com.musketeer.datasearch.common.AppContant;
import com.musketeer.datasearch.common.SharePreferenceConfig;
import com.musketeer.datasearch.entity.SearchResultEntity;
import com.musketeer.datasearch.other.SearchResultComparator;
import com.musketeer.datasearch.view.BaseDialog;
import com.musketeer.datasearch.view.BasePopupWindow;
import com.musketeer.datasearch.view.HeaderLayoutBar;

import butterknife.Bind;

public class PersonalRecordFragment extends BaseFragment
		implements OnItemClickListener,OnItemLongClickListener {

	@Bind(R.id.main_headbar)
	HeaderLayoutBar mHeadBar;
	TextView mTitle;

	protected SearchResultEntity subDirEntity;
	
	private BasePopupWindow mPopupWindow;
	
	//目录信息
	private int parentId=0;
	@Bind(R.id.directionary_list)
	ListView mDirectionaryList;
	private DirectionaryListAdapter mAdapter;
	
	private AlertDialog mDirListDialog;
	private AlertDialog mDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		BaseView=inflater.inflate(R.layout.fragment_personal_record, null);
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		mHeadBar.addView(LayoutInflater.from(getActivity()).inflate(R.layout.include_personal_record_headbar, null));
		mTitle = (TextView) mHeadBar.findViewById(R.id.title);
		initPopupWindow();
	}

	@Override
	public void initEvent() {
		// TODO Auto-generated method stub
		mHeadBar.findViewById(R.id.left_image_button).setOnClickListener(this);
		mHeadBar.findViewById(R.id.right_image_button).setOnClickListener(this);
		mDirectionaryList.setOnItemClickListener(this);
		mDirectionaryList.setOnItemLongClickListener(this);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		mTitle.setText(R.string.title_personal_record);
		
		initDirList();
	}
	
	private void initDirList() {
		List<SearchResultEntity> list=MainApplication.getInstance()
				.getSearchResultDao().getRecordDataByParentId(0);
		if (list!=null) {
			Collections.sort(list, new SearchResultComparator());
		}
		mAdapter=new DirectionaryListAdapter(getActivity(), list);
		mDirectionaryList.setAdapter(mAdapter);
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initDirList();
		
//		List<SearchResultEntity> list=MainApplication.getInstance()
//				.getSearchResultDao().getAllData();
//		if (list!=null) {
//			for (SearchResultEntity entity:list) {
//				LogUtils.d("zxq", "id: " + entity.getId() + ";parentId: " + entity.getParentId() + ";title: " + entity.getTitle());
//			}
//		}

		if (subDirEntity != null) showSubDir(subDirEntity);
	}

	public void initPopupWindow() {
		View view=LayoutInflater.from(getActivity()).inflate(R.layout.include_personal_record_menu, null);
		mPopupWindow=new BasePopupWindow(view, mScreenWidth/2, LayoutParams.WRAP_CONTENT, false);
		view.findViewById(R.id.create_new_folder).setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_image_button:
			MainActivity.getInstance().mDrawerLayout.openDrawer(Gravity.LEFT);
			break;
		case R.id.right_image_button:
			mPopupWindow.showAsDropDown(mHeadBar, (int)(mScreenWidth*4/7-ScreenUtils.dpToPx(MainApplication.getInstance(), 20)),
					-(int) ScreenUtils.dpToPx(getActivity(), 10));
			break;
		case R.id.create_new_folder:
			if (mDialog!=null) {
				mDialog.dismiss();
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			View view = LayoutInflater.from(getActivity()).inflate(R.layout.include_dialog_common_edittext, null);
			builder.setView(view);
			final EditText editText = (EditText) view.findViewById(R.id.edittext);
			editText.setHint("请输入文件夹名");
			builder.setNegativeButton(getResources().getString(R.string.cancel),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mDialog.dismiss();
						}
					});
			builder.setPositiveButton(getResources().getString(R.string.affirm),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (editText.getText().toString().trim().length()==0) {
								showCustomToast("文件夹名不能为空");
								return;
							}
							SearchResultEntity entity=new SearchResultEntity();
							entity.setTitle(editText.getText().toString().trim());
							entity.setParentId(parentId);
							entity.setFolder(true);
							entity.setRecorded(true);
							MainApplication.getInstance().getSearchResultDao().insert(entity);
							initDirList();
							mDialog.dismiss();
						}
					});
			mDialog = builder.create();
			mDialog.show();
			mPopupWindow.dismiss();
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		if (!mAdapter.getItem(position).isFolder()) {
			showSearchResult(mAdapter.getItem(position));
		} else {
			showSubDir(mAdapter.getItem(position));
		}
	}

	public void showSubDir(SearchResultEntity resultEntity) {
		subDirEntity = resultEntity;
		if (mDirListDialog!=null) {
			mDirListDialog.dismiss();
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.include_dialog_directionary_list, null);
		builder.setView(rootView);
		ListView listView=(ListView) rootView.findViewById(R.id.dir_list);

		//加载数据
		List<SearchResultEntity> list=MainApplication.getInstance()
				.getSearchResultDao().getRecordDataByParentId(resultEntity.getId());
		if (list!=null) {
			Collections.sort(list, new SearchResultComparator());
		}
		DirectionaryListAdapter adapter=new DirectionaryListAdapter(getActivity(), list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				// TODO Auto-generated method stub
				DirectionaryListAdapter adapter = (DirectionaryListAdapter) parent.getAdapter();
				if (!adapter.getItem(position).isFolder()) {
					showSearchResult(adapter.getItem(position));
				}
			}

		});
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent,
										   View view, int position, long id) {
				// TODO Auto-generated method stub
				final DirectionaryListAdapter adapter = (DirectionaryListAdapter) parent.getAdapter();
				final SearchResultEntity entity=adapter.getItem(position);
				if (mDialog != null) {
					mDialog.dismiss();
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setMessage("是否删除该收藏记录?");
				builder.setNegativeButton(getResources().getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								mDialog.dismiss();
							}
						});
				entity.setTag(adapter);
				builder.setPositiveButton(getResources().getString(R.string.affirm),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								MainApplication.getInstance().getSearchResultDao().delete(entity);
								MainApplication.getInstance().getSearchResultDao().deleteDataByParentId(entity.getId());
								adapter.refreshList(MainApplication.getInstance().getSearchResultDao().getRecordDataByParentId(subDirEntity.getId()));
								mDialog.dismiss();
							}
						});
				mDialog = builder.create();
				mDialog.show();
				return true;
			}

		});
		mDirListDialog = builder.create();
		mDirListDialog.show();
		mDirListDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				subDirEntity = null;
			}
		});
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		// TODO Auto-generated method stub
		final SearchResultEntity entity=mAdapter.getItem(position);
		if (mDialog != null) {
			mDialog.dismiss();
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("是否删除该收藏记录?");
		builder.setNegativeButton(getResources().getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mDialog.dismiss();
					}
				});
		builder.setPositiveButton(getResources().getString(R.string.affirm),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						MainApplication.getInstance().getSearchResultDao().deleteDataByUrlIfUseless(entity.getLink());
						MainApplication.getInstance().getSearchResultDao().deleteDataByParentId(entity.getId());
						initDirList();
						mDialog.dismiss();
					}
				});
		mDialog = builder.create();
		mDialog.show();
		return true;
	}
	
	/**
	 * 展示搜索结果
	 */
	private void showSearchResult(SearchResultEntity resultEntity) {
		switch (SharePreferenceUtils.getInt(getActivity(), SharePreferenceConfig.LOAD_WAY, AppContant.WEBVIEW_LOAD)) {
		case AppContant.WEBVIEW_LOAD:
			Bundle bundle=new Bundle();
			bundle.putString("entity", "search_result");
			MainApplication.getInstance().pipeline.put("search_result", resultEntity);
			startActivity(ResultDetailActivity.class, bundle);
			break;
		case AppContant.BROWSER_LOAD:
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(resultEntity.getLink()));
			startActivity(intent);
			break;
		}
	}
}
