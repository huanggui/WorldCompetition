/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.world.compet.component;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TabHost;


/**
 * 
 * 首页底部tab的具体布局，是bottomTabHost底部的子View
 * @author huanggui
 * add on 2014-11-8
 */
public class BottomTabWidget extends LinearLayout implements OnFocusChangeListener{
	private static final String TAG = BottomTabWidget.class.getSimpleName();
	private OnTabSelectionChanged mSelectionChangedListener;
	private int mSelectedTab = 0;
	private boolean mStripMoved;
	private int mCurSkinId = -1;
	private Drawable mWidgetItemBg = null;

	public BottomTabWidget(Context context) {
		this(context, null);
	}

	public BottomTabWidget(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.tabWidgetStyle);
	}

	public BottomTabWidget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		initTabWidget();
//		try {
//			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable..TabWidget, defStyle, 0);
//			a.recycle();
//		} catch (Exception e) {
//			
//		}
	
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mStripMoved = true;
		super.onSizeChanged(w, h, oldw, oldh);
	}

	private void initTabWidget() {
		setOrientation(LinearLayout.HORIZONTAL);

		setFocusable(true);
		setOnFocusChangeListener(this);
	}

	@Override
	public void childDrawableStateChanged(View child) {
		if (child == getChildAt(mSelectedTab)) {
			// To make sure that the bottom strip is redrawn
			invalidate();
		}
		super.childDrawableStateChanged(child);
	}

	@Override
	public void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);

	}
	public void setCurrentTab(int index) {
		if (index < 0 || index >= getChildCount()) {
			return;
		}

		getChildAt(mSelectedTab).setSelected(false);
		mSelectedTab = index;
		getChildAt(mSelectedTab).setSelected(true);
		mStripMoved = true;
	}

	public void focusCurrentTab(int index) {
		final int oldTab = mSelectedTab;

		// set the tab
		setCurrentTab(index);

		// change the focus if applicable.
		if (oldTab != index) {
//			getWindow().getDecorView().clearFocus();
			getChildAt(index).requestFocus();
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		int count = getChildCount();

		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			child.setEnabled(enabled);
		}
	}

	@Override
	public void addView(View child) {
		
		
		if (child.getLayoutParams() == null) {
			final LinearLayout.LayoutParams lp = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
			lp.setMargins(0, 0, 0, 0);
			child.setLayoutParams(lp);
		}

		// Ensure you can navigate to the tab with the keyboard, and you can
		// touch it
//		child.setFocusable(true);
//		child.setClickable(true);

		super.addView(child);

		// TODO: detect this via geometry with a tabwidget listener rather
		// than potentially interfere with the view's listener
		child.setOnClickListener(new TabClickListener(getChildCount() - 1));
		child.setOnFocusChangeListener(this);
//		child.clearFocus();
	}

	@Override
	public void addView(View child, int index) {
		super.addView(child, index);
		child.setOnClickListener(new TabClickListener(index));
		child.setOnFocusChangeListener(this);
	}

	/**
	 * Provides a way for {@link TabHost} to be notified that the user clicked
	 * on a tab indicator.
	 */
	void setTabSelectionListener(OnTabSelectionChanged listener) {
		mSelectionChangedListener = listener;
	}

	public void onFocusChange(View v, boolean hasFocus) {
		if (getChildAt(mSelectedTab) == null) {
			return;
		}
		if (v == this && hasFocus) {
			getChildAt(mSelectedTab).requestFocus();
			return;
		}

		if (hasFocus) {
			int i = 0;
			while (i < getChildCount()) {
				if (getChildAt(i) == v) {
					setCurrentTab(i);
					mSelectionChangedListener.onTabSelectionChanged(i, false);
					break;
				}
				i++;
			}
		}
	}

	// registered with each tab indicator so we can notify tab host
	private class TabClickListener implements OnClickListener {
		private final int mTabIndex;

		private TabClickListener(int tabIndex) {
			mTabIndex = tabIndex;
		}

		public void onClick(View v) {
			mSelectionChangedListener.onTabSelectionChanged(mTabIndex, true);
		}
	}

	static interface OnTabSelectionChanged {
		void onTabSelectionChanged(int tabIndex, boolean clicked);
	}


}
