/**   
* @Title: BaseDialog.java 
* @Package com.musketeer.datasearch.view 
*
* @author musketeer zhongxuqi@163.com  
* @date 2015-3-29 上午8:58:27 
* @version V1.0   
*/
package com.musketeer.datasearch.view;

import com.musketeer.datasearch.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;

public class BaseDialog extends Dialog implements
		android.view.View.OnClickListener {
	
	private FrameLayout mMainContent;
	private TextView mButton1,mButton2,mButton3;
	
	public BaseDialog(Context context) {
		super(context,R.style.BaseDialog);
		// TODO Auto-generated constructor stub
		init();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	}
	
	public void init() {
		setContentView(R.layout.dialog_base);
		initView();
	}
	
	public void initView() {
		mMainContent=(FrameLayout) findViewById(R.id.main_content);
		mButton1=(TextView) findViewById(R.id.button1);
		mButton2=(TextView) findViewById(R.id.button2);
		mButton3=(TextView) findViewById(R.id.button3);
	}
	
	public void setDialogContentView(int layoutId) {
		View view=getLayoutInflater().inflate(layoutId, null);
		FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		view.setLayoutParams(params);
		mMainContent.removeAllViews();
		mMainContent.addView(view);
	}
	
	/**
	 * 设置button1
	 * @param text
	 * @param l
	 */
	public void setButton1(String text, View.OnClickListener l) {
		mButton1.setText(text);
		mButton1.setOnClickListener(l);
		mButton1.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 设置button2
	 * @param text
	 * @param l
	 */
	public void setButton2(String text, View.OnClickListener l) {
		mButton2.setText(text);
		mButton2.setOnClickListener(l);
		mButton2.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 设置button3
	 * @param text
	 * @param l
	 */
	public void setButton3(String text, View.OnClickListener l) {
		mButton3.setText(text);
		mButton3.setOnClickListener(l);
		mButton3.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	public TextView getButton1() {
		return mButton1;
	}

	public void setButton1(TextView mButton1) {
		this.mButton1 = mButton1;
	}

	public TextView getButton2() {
		return mButton2;
	}

	public void setButton2(TextView mButton2) {
		this.mButton2 = mButton2;
	}

	public TextView getButton3() {
		return mButton3;
	}

	public void setButton3(TextView mButton3) {
		this.mButton3 = mButton3;
	}
}
