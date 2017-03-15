/**   
* @Title: WebEntity.java 
* @Package com.musketeer.datasearch.entity 
*
* @author musketeer zhongxuqi@163.com  
* @date 2015-3-31 下午6:37:52 
* @version V1.0   
*/
package com.musketeer.datasearch.entity;

import android.widget.ImageView;

import com.j256.ormlite.field.DatabaseField;
import com.musketeer.baselibrary.bean.BaseEntity;

public class WebEntity extends BaseEntity {
	@DatabaseField(generatedId=true)
	private int id;
	@DatabaseField
	private String Url;
	@DatabaseField
	private String ParserName;
	@DatabaseField
	private String Name;
	@DatabaseField
	private String Logo;

	protected ImageView statusView;
	
	public WebEntity() {
		this.id = 0;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return Url;
	}

	public void setUrl(String url) {
		Url = url;
	}

	public String getParserName() {
		return ParserName;
	}

	public void setParserName(String parserName) {
		ParserName = parserName;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getLogo() {
		return Logo;
	}

	public void setLogo(String logo) {
		Logo = logo;
	}

	public ImageView getStatusView() {
		return statusView;
	}

	public void setStatusView(ImageView statusView) {
		this.statusView = statusView;
	}
}
