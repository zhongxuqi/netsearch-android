package com.musketeer.datasearch.entity;

import com.musketeer.baselibrary.bean.BaseEntity;

import java.util.ArrayList;
import java.util.List;

public class SearchResultAndPageEntity extends BaseEntity {
	protected String Status;
	protected String Message;
	protected List<SearchResultEntity> ResultData;
	protected String NextPage;
	protected boolean isNewList=false;
	
	public SearchResultAndPageEntity() {
		ResultData=new ArrayList<SearchResultEntity>();
	}

	public List<SearchResultEntity> getResultData() {
		return ResultData;
	}

	public void setResultData(List<SearchResultEntity> resultData) {
		ResultData = resultData;
	}

	public String getNextPage() {
		return NextPage;
	}

	public void setNextPage(String nextPage) {
		NextPage = nextPage;
	}

	public boolean isNewList() {
		return isNewList;
	}

	public void setNewList(boolean newList) {
		isNewList = newList;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}
}
