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

package com.world.compet.activity;

import com.world.compet.R;
import com.world.compet.component.BottomTabHost;
import com.world.compet.component.BottomTabWidget;

import android.app.Activity;
import android.app.ActivityGroup;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * An activity that contains and runs multiple embedded activities or views.
 */
public class BottomTabActivity extends ActivityGroup {
	private BottomTabHost mTabHost;
	private String mDefaultTab = null;
	private int mDefaultTabIndex = -1;

	public BottomTabActivity() {
	}

	/**
	 * Sets the default tab that is the first tab highlighted.
	 * 
	 * @param tag
	 *            the name of the default tab
	 */
	public void setDefaultTab(String tag) {
		mDefaultTab = tag;
		mDefaultTabIndex = -1;
	}

	/**
	 * Sets the default tab that is the first tab highlighted.
	 * 
	 * @param index
	 *            the index of the default tab
	 */
	public void setDefaultTab(int index) {
		mDefaultTab = null;
		mDefaultTabIndex = index;
	}

	@Override
	protected void onRestoreInstanceState(Bundle state) {
		super.onRestoreInstanceState(state);
	}

	@Override
	protected void onPostCreate(Bundle icicle) {
		super.onPostCreate(icicle);

		ensureTabHost();

		if (mTabHost.getCurrentTab() == -1 && mTabHost.getChildCount() > 0) {
			mTabHost.setCurrentTab(0);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		int currentTab = mTabHost.getCurrentTab();
		if (currentTab != -1) {
			outState.putInt("currentTab", currentTab);
		}
	}

	/**
	 * Updates the screen state (current list and other views) when the content
	 * changes.
	 * 
	 * @see Activity#onContentChanged()
	 */
	@Override
	public void onContentChanged() {
		super.onContentChanged();
		mTabHost = (BottomTabHost) findViewById(R.id.mytabhost);

		if (mTabHost == null) {
			throw new RuntimeException("Your content must have a TabHost whose id attribute is "
					+ "'android.R.id.tabhost'");
		}
		mTabHost.setup(getLocalActivityManager());
	}

	private void ensureTabHost() {
		if (mTabHost == null) {
			this.setContentView(R.layout.tab_content);
		}
	}

	@Override
	protected void onChildTitleChanged(Activity childActivity, CharSequence title) {
		// Dorky implementation until we can have multiple activities running.
		if (getLocalActivityManager().getCurrentActivity() == childActivity) {
			View tabView = mTabHost.getCurrentTabView();
			if (tabView != null && tabView instanceof TextView) {
				((TextView) tabView).setText(title);
			}
		}
	}

	/**
	 * Returns the {@link BottomTabHost} the activity is using to host its tabs.
	 * 
	 * @return the {@link BottomTabHost} the activity is using to host its tabs.
	 */
	public BottomTabHost getTabHost() {
		ensureTabHost();
		return mTabHost;
	}

	/**
	 * Returns the {@link BottomTabWidget} the activity is using to draw the
	 * actual tabs.
	 * 
	 * @return the {@link BottomTabWidget} the activity is using to draw the
	 *         actual tabs.
	 */
	public BottomTabWidget getTabWidget() {
		return mTabHost.getTabWidget();
	}
}
