/**   
* @Title: SearchResultEntity.java 
* @Package com.musketeer.datasearch.entity 
*
* @author musketeer zhongxuqi@163.com  
* @date 2015-3-15 下午7:25:01 
* @version V1.0   
*/
package com.musketeer.datasearch.entity;

import android.view.View;

import com.j256.ormlite.field.DatabaseField;
import com.musketeer.baselibrary.adapter.BaseRecyclerAdapter;
import com.musketeer.baselibrary.bean.BaseEntity;
import com.musketeer.datasearch.adapter.UnionListAdapter;

/**
 * @author zhongxuqi
 */
public class SearchResultEntity extends BaseEntity {
	@DatabaseField(generatedId=true)
	private int id;
	@DatabaseField
	private String Title;
	@DatabaseField
	private String Abstract;
	@DatabaseField
	private String Link;
	@DatabaseField
	private String ImageUrl;
	@DatabaseField
	private boolean isFolder;
	@DatabaseField
	private boolean isRecorded;
	@DatabaseField
	private int parentId;

	protected View StatusView = null;
	protected Object tag = null;
	protected BaseRecyclerAdapter.OnItemClickListener<SearchResultEntity> clickListener = null;
	protected UnionListAdapter.OnRecordListener recordListener = null;
	
	public SearchResultEntity() {
		parentId=0;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public String getAbstract() {
		return Abstract;
	}

	public void setAbstract(String anAbstract) {
		Abstract = anAbstract;
	}

	public String getLink() {
		return Link;
	}

	public void setLink(String link) {
		Link = link;
	}

	public String getImageUrl() {
		return ImageUrl;
	}
	public void setImageUrl(String imageUrl) {
		ImageUrl = imageUrl;
	}
	public boolean isFolder() {
		return isFolder;
	}
	public void setFolder(boolean isFolder) {
		this.isFolder = isFolder;
	}
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	public boolean isRecorded() {
		return isRecorded;
	}
	public void setRecorded(boolean isRecorded) {
		this.isRecorded = isRecorded;
	}

	public View getStatusView() {
		return StatusView;
	}

	public void setStatusView(View statusView) {
		StatusView = statusView;
	}

	public Object getTag() {
		return tag;
	}

	public void setTag(Object tag) {
		this.tag = tag;
	}

	public BaseRecyclerAdapter.OnItemClickListener<SearchResultEntity> getClickListener() {
		return clickListener;
	}

	public void setClickListener(BaseRecyclerAdapter.OnItemClickListener<SearchResultEntity> clickListener) {
		this.clickListener = clickListener;
	}

	public UnionListAdapter.OnRecordListener getRecordListener() {
		return recordListener;
	}

	public void setRecordListener(UnionListAdapter.OnRecordListener recordListener) {
		this.recordListener = recordListener;
	}
}
