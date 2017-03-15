package com.musketeer.datasearch.activity;

import com.musketeer.baselibrary.Activity.BaseActivity;
import com.musketeer.datasearch.R;
import com.musketeer.datasearch.view.HeaderLayoutBar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;

public class HelpActivity extends BaseActivity {
	@Bind(R.id.main_headbar)
	HeaderLayoutBar mHeadBar;
	TextView mTitle;

	@Override
	public void setContentView(Bundle savedInstanceState) {
		setContentView(R.layout.activity_help);
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		mHeadBar.addView(LayoutInflater.from(this).inflate(R.layout.include_common_headbar, null));
		mTitle=(TextView) mHeadBar.findViewById(R.id.title);
	}

	@Override
	public void initEvent() {
		// TODO Auto-generated method stub
		mHeadBar.findViewById(R.id.back).setOnClickListener(this);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		mTitle.setText(R.string.title_activity_help);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		}
	}
}
