/**   
* @Title: WebGroupEntity.java 
* @Package com.musketeer.datasearch.entity 
*
* @author musketeer zhongxuqi@163.com  
* @date 2015-4-11 下午12:01:49 
* @version V1.0   
*/
package com.musketeer.datasearch.entity;

import java.util.ArrayList;
import java.util.List;

import android.view.View;

import com.j256.ormlite.field.DatabaseField;
import com.musketeer.baselibrary.bean.BaseEntity;
import com.musketeer.datasearch.MainApplication;

public class WebGroupEntity extends BaseEntity {
	@DatabaseField(generatedId=true)
	private int id;
	@DatabaseField
	private String webs;
	@DatabaseField
	private boolean isSelected;
	@DatabaseField
	private String groupName;
	@DatabaseField
	private int groupKind;
	
	public View mSelectedView;
	
	public WebGroupEntity() {
		
	}
	
	public WebGroupEntity(List<WebEntity> list) {
		if (list!=null) {
			webs="";
			for (int i=0;i<list.size();i++) {
				if (i<list.size()-1) {
					webs=webs+list.get(i).getUrl()+",";
				} else {
					webs=webs+list.get(i).getUrl();
				}
			}
		}
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getWebs() {
		if (webs==null) {
			webs="";
		}
		return webs;
	}
	public void setWebs(String webs) {
		this.webs = webs;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public int getGroupKind() {
		return groupKind;
	}
	public void setGroupKind(int groupKind) {
		this.groupKind = groupKind;
	}
	
	public List<WebEntity> getWebEntitys() {
		List<WebEntity> list=new ArrayList<>();
		if (this.webs==null) {
			return list;
		}
		String[] urls=this.webs.split(",");
		for (int i=0;i<urls.length;i++) {
			WebEntity web=MainApplication.getInstance().getWebDao().getDataByUrl(urls[i]);
			if (web!=null) {
				list.add(web);
			}
		}
		return list;
	}
	public void setWebs(List<WebEntity> list) {
		this.webs = "";
		if (list!=null) {
			for (int i=0;i<list.size();i++) {
				if (i<list.size()-1) {
					this.webs=this.webs+list.get(i).getUrl()+",";
				} else {
					this.webs=this.webs+list.get(i).getUrl();
				}
			}
		}
	}

}
