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

import java.util.ArrayList;
import java.util.List;

import com.world.compet.R;

import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * 一级界面的底部tab布局，包括每个tab的内容Activity，底部的bottomtabwidget
 * @author huanggui 
 * add on 2014-11-8
 */
public class BottomTabHost extends LinearLayout implements ViewTreeObserver.OnTouchModeChangeListener {

	private BottomTabWidget mTabWidget;
	private FrameLayout mTabContent;
	private List<TabSpec> mTabSpecs = new ArrayList<TabSpec>(2);
	private String mTitle;
	private String mIndicator;
	private int mDrawId;

	protected int mCurrentTab = -1;
	protected int mPreviewTab = -1;
	private View mCurrentView = null;

	protected LocalActivityManager mLocalActivityManager = null;
	private OnTabChangeListener mOnTabChangeListener;
	private OnKeyListener mTabKeyListener;
	private View guideView;
	private PopupWindow guidePopWin;

//	public boolean mTabHasChange4Adv = false;

	public BottomTabHost(Context context) {
		super(context);
		initTabHost();
	}

	public BottomTabHost(Context context, AttributeSet attrs) {
		super(context, attrs);
		initTabHost();
	}

	private final void initTabHost() {
		setFocusableInTouchMode(true);
		setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
		mCurrentTab = -1;
		mCurrentView = null;
	}

	/**
	 * Get a new {@link TabSpec} associated with this tab host.
	 * 
	 * @param tag required tag of tab.
	 */
	public TabSpec newTabSpec(String tag) {
		return new TabSpec(tag);
	}

	public void setTabSpecTitle(String title) {
		mTitle = title;
	}

	public void setTabSpecIndicator(String indicator) {
		mIndicator = indicator;
	}

	public void setTabSpecIcon(int drawId) {
		mDrawId = drawId;
	}

//	public void setTabSpecView(int srcId) {
//
//		Resources rsrc = getResources();
//		if (rsrc == null) {
//			return;
//		}
//		try {
//			Drawable icon = rsrc.getDrawable(mDrawId);
//			setup();
//			addTab(newTabSpec(mTitle).setIndicator(mIndicator, icon, (Drawable)null).setContent(srcId));
//		} catch (Throwable e) {
//			e.printStackTrace();
//			return;
//		}
//	}

	/**
	 * <p>
	 * Call setup() before adding tabs if loading TabHost using findViewById(). <i><b>However</i></b>: You do not need to call setup() after
	 * getTabHost() in {@link android.app.TabActivity TabActivity}. Example:
	 * </p>
	 * 
	 * <pre>
	 * mTabHost = (TabHost) findViewById(R.id.tabhost);
	 * mTabHost.setup();
	 * mTabHost.addTab(TAB_TAG_1, "Hello, world!", "Tab 1");
	 */
	public void setup() {
		mTabWidget = (BottomTabWidget) findViewById(R.id.mytabs);
		if (mTabWidget == null) {
			throw new RuntimeException("Your TabHost must have a TabWidget whose id attribute is 'android.R.id.tabs'");
		}

		// KeyListener to attach to all tabs. Detects non-navigation keys
		// and relays them to the tab content.
		mTabKeyListener = new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				switch (keyCode) {
				case KeyEvent.KEYCODE_DPAD_CENTER:
				case KeyEvent.KEYCODE_DPAD_LEFT:
				case KeyEvent.KEYCODE_DPAD_RIGHT:
				case KeyEvent.KEYCODE_DPAD_UP:
				case KeyEvent.KEYCODE_DPAD_DOWN:
				case KeyEvent.KEYCODE_ENTER:
					return false;

				}
				mTabContent.requestFocus(View.FOCUS_FORWARD);
				return mTabContent.dispatchKeyEvent(event);
			}

		};

		mTabWidget.setTabSelectionListener(new BottomTabWidget.OnTabSelectionChanged() {
			public void onTabSelectionChanged(int tabIndex, boolean clicked) {
				
				if(clicked){
					//首先记录点击事件
					invokeOnTabActionListener(tabIndex);
				}
				setCurrentTab(tabIndex);
				if (clicked) {
					mTabContent.requestFocus(View.FOCUS_FORWARD);
				}
			}
		});

		mTabContent = (FrameLayout) findViewById(R.id.mytabcontent);
		if (mTabContent == null) {
			throw new RuntimeException("Your TabHost must have a FrameLayout whose id attribute is 'android.R.id.tabcontent'");
		}
	}

	/**
	 * If you are using {@link TabSpec#setContent(android.content.Intent)}, this must be called since the activityGroup is needed to launch
	 * the local activity. This is done for you if you extend {@link android.app.TabActivity}.
	 * 
	 * @param activityGroup Used to launch activities for tab content.
	 */
	public void setup(LocalActivityManager activityGroup) {
		setup();
		mLocalActivityManager = activityGroup;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		final ViewTreeObserver treeObserver = getViewTreeObserver();
		if (treeObserver != null) {
			treeObserver.addOnTouchModeChangeListener(this);
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		final ViewTreeObserver treeObserver = getViewTreeObserver();
		if (treeObserver != null) {
			treeObserver.removeOnTouchModeChangeListener(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void onTouchModeChanged(boolean isInTouchMode) {
		if (!isInTouchMode) {
			if (mCurrentView != null && (!mCurrentView.hasFocus() || mCurrentView.isFocused())) {
				mTabWidget.getChildAt(mCurrentTab).requestFocus();
			}
		}
	}

	/**
	 * Add a tab.
	 * 
	 * @param tabSpec Specifies how to create the indicator and content.
	 */
	public View addTab(TabSpec tabSpec, int viewId) {
		if (tabSpec.mIndicatorStrategy == null) {
			throw new IllegalArgumentException("you must specify a way to create the tab indicator.");
		}

		if (tabSpec.mContentStrategy == null) {
			throw new IllegalArgumentException("you must specify a way to create the tab content");
		}
		View tabIndicator = tabSpec.mIndicatorStrategy.createIndicatorView();
		tabIndicator.setOnKeyListener(mTabKeyListener);
		mTabWidget.addView(tabIndicator);
		tabIndicator.setId(viewId);
		mTabSpecs.add(tabSpec);
		if (mCurrentTab == -1) {
			setCurrentTab(0);
		}
		return tabIndicator;
	}

	public void replaceTab(TabSpec tabSpec, int index) {

		if (index > mTabSpecs.size()) {
			throw new IllegalArgumentException("index is more than tab size");
		}

		if (tabSpec.mIndicatorStrategy == null) {
			throw new IllegalArgumentException("you must specify a way to create the tab indicator.");
		}

		if (tabSpec.mContentStrategy == null) {
			throw new IllegalArgumentException("you must specify a way to create the tab content");
		}
		View tabIndicator = tabSpec.mIndicatorStrategy.createIndicatorView();
		tabIndicator.setOnKeyListener(mTabKeyListener);

		mTabWidget.addView(tabIndicator, index);
		mTabSpecs.add(index, tabSpec);
		setCurrentTab(index);

		// this.removeViewAt(index+1);

		mTabWidget.removeView(mTabWidget.getChildAt(index + 1));
		mTabSpecs.remove(index + 1);

		if (mCurrentTab == -1) {
			setCurrentTab(0);
		}
	}

	/**
	 * Removes all tabs from the tab widget associated with this tab host.
	 */
	public void clearAllTabs() {
		mTabWidget.removeAllViews();
		initTabHost();
		mTabContent.removeAllViews();
		mTabSpecs.clear();
		requestLayout();
		invalidate();
	}

	public BottomTabWidget getTabWidget() {
		return mTabWidget;
	}

	public int getCurrentTab() {
		return mCurrentTab;
	}

	public int getPreviewTab() {
		return mPreviewTab;
	}
	
	public String getTabTag(int tabId){
		if (tabId >= 0 && tabId < mTabSpecs.size()) {
			return mTabSpecs.get(tabId).getTag();
		}
		return null;
	}

	public String getCurrentTabTag() {
		if (mCurrentTab >= 0 && mCurrentTab < mTabSpecs.size()) {
			return mTabSpecs.get(mCurrentTab).getTag();
		}
		return null;
	}

	public View getCurrentTabView() {
		if (mCurrentTab >= 0 && mCurrentTab < mTabSpecs.size()) {
			return mTabWidget.getChildAt(mCurrentTab);
		}
		return null;
	}

	public View getCurrentView() {
		return mCurrentView;
	}

	public void setCurrentTabByTag(String tag) {
		int i;
		for (i = 0; i < mTabSpecs.size(); i++) {
			if (mTabSpecs.get(i).getTag().equals(tag)) {
				setCurrentTab(i);
				break;
			}
		}
	}

	/**
	 * Get the FrameLayout which holds tab content
	 */
	public FrameLayout getTabContentView() {
		return mTabContent;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		final boolean handled = super.dispatchKeyEvent(event);

		// unhandled key ups change focus to tab indicator for embedded
		// activities
		// when there is nothing that will take focus from default focus
		// searching
		if (!handled && (event.getAction() == KeyEvent.ACTION_DOWN) && (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) && mCurrentView != null
				&& (mCurrentView.hasFocus()) && (mCurrentView.findFocus()!=null && mCurrentView.findFocus().focusSearch(View.FOCUS_DOWN) == null)) {
			mTabWidget.getChildAt(mCurrentTab).requestFocus();
			playSoundEffect(SoundEffectConstants.NAVIGATION_UP);
			return true;
		}
		return handled;
	}

	/**
	 * added an if condition to determine that mCurrentView is not null
	 */
	@Override
	public void dispatchWindowFocusChanged(boolean hasFocus) {
		if (mCurrentView != null)
			mCurrentView.dispatchWindowFocusChanged(hasFocus);
	}

	
	

	public void setCurrentTab(int index, int subIndex) {
		if (index < 0 || index >= mTabSpecs.size() || index >= mTabWidget.getChildCount()) {
			return;
		}

		if (index == mCurrentTab) {
			return;
		}
		// notify old tab content
		if (mCurrentTab != -1) {
			mTabSpecs.get(mCurrentTab).mContentStrategy.tabClosed();
		}
		// int lastTabIndex = mCurrentTab;
		mPreviewTab = mCurrentTab;
		mCurrentTab = index;
//		mTabHasChange4Adv = true;
		final BottomTabHost.TabSpec spec = mTabSpecs.get(index);

		// Call the tab widget's focusCurrentTab(), instead of just selecting the tab.
		mTabWidget.focusCurrentTab(mCurrentTab);

		// tab content
		mCurrentView = spec.mContentStrategy.getContentView(subIndex);
		
		if (mCurrentView == null) {
			return;
		}

		if (mCurrentView.getParent() == null) {
			mTabContent.addView(mCurrentView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
		}

		if (!mTabWidget.hasFocus()) {
			// if the tab widget didn't take focus (likely because we're in
			// touch mode)
			// give the current tab content view a shot
			mCurrentView.requestFocus();
		}

		// mTabContent.requestFocus(View.FOCUS_FORWARD);
		invokeOnTabChangeListener();

//		if (mTabWidget != null && mTabWidget.getVisibility() == View.VISIBLE) {
//			Message msg = Message.obtain();
//			msg.arg1 = mPreviewTab;
//			msg.arg2 = index;
//			animationHandler.sendMessageDelayed(msg, 200);
//		}
	}

	public void setCurrentTab(int index) {
		setCurrentTab(index, -1);
	}


	/**
	 * Register a callback to be invoked when the selected state of any of the items in this list changes
	 * 
	 * @param l The callback that will run
	 */
	public void setOnTabChangedListener(OnTabChangeListener l) {
		mOnTabChangeListener = l;
	}

	private void invokeOnTabChangeListener() {
		if (mOnTabChangeListener != null) {
			mOnTabChangeListener.onTabChanged(getCurrentTabTag(), getTabTag(mPreviewTab));
		}
	}
	
	private void invokeOnTabActionListener(int nextTabId){
		if (mOnTabChangeListener != null) {
			mOnTabChangeListener.onTabAction(getCurrentTabTag(), getTabTag(nextTabId));
		}
	}

	/**
	 * Interface definition for a callback to be invoked when tab changed
	 */
	public interface OnTabChangeListener {
		void onTabChanged(String tabId, String preTabId);
		void onTabAction(String tabId, String nextTabId);
	}

	/**
	 * Makes the content of a tab when it is selected. Use this if your tab content needs to be created on demand, i.e. you are not showing
	 * an existing view or starting an activity.
	 */
	public interface TabContentFactory {
		/**
		 * Callback to make the tab contents
		 * 
		 * @param tag Which tab was selected.
		 * @return The view to distplay the contents of the selected tab.
		 */
		View createTabContent(String tag);
	}

	/**
	 * A tab has a tab indictor, content, and a tag that is used to keep track of it. This builder helps choose among these options. For the
	 * tab indicator, your choices are: 1) set a label 2) set a label and an icon For the tab content, your choices are: 1) the id of a
	 * {@link View} 2) a {@link TabContentFactory} that creates the {@link View} content. 3) an {@link Intent} that launches an
	 * {@link android.app.Activity}.
	 */
	public class TabSpec {
		private String mTag;
		private IndicatorStrategy mIndicatorStrategy;
		private ContentStrategy mContentStrategy;

		private TabSpec(String tag) {
			mTag = tag;
		}

		/**
		 * Specify a label as the tab indicator.
		 */
		public TabSpec setIndicator(CharSequence label) {
			return this;
		}

		/**
		 * Specify a label and icon as the tab indicator.
		 */
		public TabSpec setIndicator(CharSequence label, Drawable icon, Drawable bgDrawable) {
			mIndicatorStrategy = new LabelAndIconIndicatorStrategy(label, icon, bgDrawable);
			return this;
		}

		/**
		 * Specify the id of the view that should be used as the content of the tab.
		 */
		public TabSpec setContent(int viewId) {
			mContentStrategy = new ViewIdContentStrategy(viewId);
			return this;
		}

		/**
		 * Specify a {@link BottomTabHost.TabContentFactory} to use to create the content of the tab.
		 */
		public TabSpec setContent(TabContentFactory contentFactory) {
			mContentStrategy = new FactoryContentStrategy(mTag, contentFactory);
			return this;
		}

		/**
		 * Specify an intent to use to launch an activity as the tab content.
		 */
		public TabSpec setContent(Intent intent) {
			mContentStrategy = new IntentContentStrategy(mTag, intent);
			return this;
		}

		String getTag() {
			return mTag;
		}
	}

	/**
	 * Specifies what you do to create a tab indicator.
	 */
	private static interface IndicatorStrategy {

		/**
		 * Return the view for the indicator.
		 */
		View createIndicatorView();
	}

	/**
	 * Specifies what you do to manage the tab content.
	 */
	private static interface ContentStrategy {

		/**
		 * Return the content view. The view should may be cached locally.
		 */
		View getContentView();

		/**
		 * Perhaps do something when the tab associated with this content has been closed (i.e make it invisible, or remove it).
		 */
		void tabClosed();

		View getContentView(int subTabId);
	}


	/**
	 * How we create a tab indicator that has a label and an icon
	 */
	private class LabelAndIconIndicatorStrategy implements IndicatorStrategy {

		private final CharSequence mLabel;
		private final Drawable mIcon;
		private final Drawable mBgDrawable;
		private LabelAndIconIndicatorStrategy(CharSequence label, Drawable icon, Drawable bgDrawable) {
			mLabel = label;
			mIcon = icon;
			mBgDrawable = bgDrawable;
		}

		public View createIndicatorView() {
			View tabIndicator = new TabView(getContext());

			final TextView tv = (TextView) tabIndicator.findViewById(R.id.title);
			tv.setText(mLabel);

			final ImageView iconView = (ImageView) tabIndicator.findViewById(R.id.icon);
			iconView.setImageDrawable(mIcon);

			tabIndicator.setBackgroundDrawable(mBgDrawable);
			return tabIndicator;
		}
	}

	/**
	 * How to create the tab content via a view id.
	 */
	private class ViewIdContentStrategy implements ContentStrategy {

		private final View mView;

		private ViewIdContentStrategy(int viewId) {
			mView = mTabContent.findViewById(viewId);
			if (mView != null) {
				mView.setVisibility(View.GONE);
			} else {
				throw new RuntimeException("Could not create tab content because " + "could not find view with id " + viewId);
			}
		}

		public View getContentView(int subTabId) {
			return getContentView();
		}

		public View getContentView() {
			mView.setVisibility(View.VISIBLE);
			return mView;
		}

		public void tabClosed() {
			mView.setVisibility(View.GONE);
		}
	}

	/**
	 * How tab content is managed using {@link TabContentFactory}.
	 */
	private class FactoryContentStrategy implements ContentStrategy {
		private View mTabContent;
		private final CharSequence mTag;
		private TabContentFactory mFactory;

		public FactoryContentStrategy(CharSequence tag, TabContentFactory factory) {
			mTag = tag;
			mFactory = factory;
		}

		public View getContentView() {
			if (mTabContent == null) {
				mTabContent = mFactory.createTabContent(mTag.toString());
			}
			mTabContent.setVisibility(View.VISIBLE);
			return mTabContent;
		}

		public void tabClosed() {
			mTabContent.setVisibility(View.INVISIBLE);
		}

		public View getContentView(int subTabId) {
			return getContentView();
		}
	}

	/**
	 * How tab content is managed via an {@link Intent}: the content view is the decorview of the launched activity.
	 */
	private class IntentContentStrategy implements ContentStrategy {

		private final String mTag;
		private final Intent mIntent;

		private View mLaunchedView;

		private IntentContentStrategy(String tag, Intent intent) {
			mTag = tag;
			mIntent = intent;
		}

		public View getContentView(int subTabId) {
			mIntent.putExtra("subTab", subTabId);
			return getContentView();
		}

		public View getContentView() {
			if (mLocalActivityManager == null) {
				throw new IllegalStateException("Did you forget to call 'public void setup(LocalActivityManager activityGroup)'?");
			}
			Window w = null;
			try {
				w = mLocalActivityManager.startActivity(mTag, mIntent);
			} catch (Exception e) {
				e.printStackTrace();
			} catch (OutOfMemoryError e){
				// 根据异常上报做的兼容，非治标方法
			}
			
			final View wd = w != null ? w.getDecorView() : null;
			if (mLaunchedView != wd && mLaunchedView != null) {
				if (mLaunchedView.getParent() != null) {
					mTabContent.removeView(mLaunchedView);
				}
			}
			mLaunchedView = wd;

			// XXX Set FOCUS_AFTER_DESCENDANTS on embedded activies for now so
			// they can get
			// focus if none of their children have it. They need focus to be
			// able to
			// display menu items.
			//
			// Replace this with something better when Bug 628886 is fixed...
			//
			if (mLaunchedView != null) {
				mLaunchedView.setVisibility(View.VISIBLE);
				mLaunchedView.setFocusableInTouchMode(true);
				((ViewGroup) mLaunchedView).setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
			}
			return mLaunchedView;
		}

		public void tabClosed() {
			if (mLaunchedView != null) {
				mLaunchedView.setVisibility(View.GONE);
			}
		}
	}

}
