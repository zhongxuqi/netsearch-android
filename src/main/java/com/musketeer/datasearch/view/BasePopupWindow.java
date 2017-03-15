package com.musketeer.datasearch.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.PopupWindow;

public class BasePopupWindow extends PopupWindow{

	public BasePopupWindow() {
		super();
		// TODO Auto-generated constructor stub
		init();
	}

	public BasePopupWindow(Context context, AttributeSet attrs,
						   int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		// TODO Auto-generated constructor stub
		init();
	}

	public BasePopupWindow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	public BasePopupWindow(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	public BasePopupWindow(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public BasePopupWindow(int width, int height) {
		super(width, height);
		// TODO Auto-generated constructor stub
		init();
	}

	public BasePopupWindow(View contentView, int width, int height,
						   boolean focusable) {
		super(contentView, width, height, focusable);
		// TODO Auto-generated constructor stub
		init();
	}

	public BasePopupWindow(View contentView, int width, int height) {
		super(contentView, width, height);
		// TODO Auto-generated constructor stub
		init();
	}

	public BasePopupWindow(View contentView) {
		super(contentView);
		// TODO Auto-generated constructor stub
		init();
	}
	
	void init() {
		setFocusable(true);
		setOutsideTouchable(true);
		setBackgroundDrawable(new ColorDrawable(0));
	}

}
