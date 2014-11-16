package com.world.compet.activity;

import com.world.compet.R;
import com.world.compet.component.BottomTabHost;
import com.world.compet.component.TabView;

import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import com.world.compet.component.BottomTabHost.TabSpec;
import com.world.compet.view.MainHeaderActionView;

public class MainActivity extends BottomTabActivity implements SearchView.OnQueryTextListener, BottomTabHost.OnTabChangeListener{
	
	private static final String TAG = "MainActivity";
	private static final int MAX_TAB_COUNT = 3;
	
	// 竞赛， 动态，用户
	public static enum TabType {
		COMPETITION, NEWS, USER
	}
	
	static final String TAB_1 = "tab1";
	static final String TAB_2 = "tab2";
	static final String TAB_3 = "tab3";
	
	private BottomTabHost mHost;
	private TabView[] mTabBtns = new TabView[MAX_TAB_COUNT];
	private int currIndex = 0;
	private MainHeaderActionView mainHeaderActionView;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mHost = getTabHost();
        mHost.setOnTabChangedListener(this);
        initTabs();    
    }
    
    private void initTabs() {

		TypedArray icons = getResources().obtainTypedArray(R.array.tab_icons);
		String[] titles = getResources().getStringArray(R.array.tab_titles);
		
		Resources resource = getResources();
		TabSpec tabSpec = mHost.newTabSpec(TAB_1);
		tabSpec.setIndicator(titles[0], resource.getDrawable(icons.getResourceId(0, 0)), resource.getDrawable(R.drawable.tab_item_bg_selector));
		tabSpec.setContent(new Intent(MainActivity.this, CompetitionTabActivity.class));
		mTabBtns[0] = (TabView) mHost.addTab(tabSpec,R.id.main_tab1);

		tabSpec = mHost.newTabSpec(TAB_2);
		tabSpec.setIndicator(titles[1], resource.getDrawable(icons.getResourceId(1, 0)), resource.getDrawable(R.drawable.tab_item_bg_selector));
		tabSpec.setContent(new Intent(MainActivity.this, NewsTabActivity.class));
		mTabBtns[1] = (TabView) mHost.addTab(tabSpec,R.id.main_tab2);

		tabSpec = mHost.newTabSpec(TAB_3);
		tabSpec.setIndicator(titles[2], resource.getDrawable(icons.getResourceId(2, 0)), resource.getDrawable(R.drawable.tab_item_bg_selector));
		tabSpec.setContent(new Intent(MainActivity.this, UserTabActivity.class));
		mTabBtns[2] = (TabView) mHost.addTab(tabSpec,R.id.main_tab3);
		
		icons.recycle();
	}

//    private void initSearchActionBar() {
//        // 自定义标题栏  
//    	if (getActionBar() == null) {
//			Log.i(TAG, "Has no ActionBar in this Activity.");
//			return ;
//		}
//        getActionBar().setDisplayShowHomeEnabled(false);  
//        getActionBar().setDisplayShowTitleEnabled(false);  
//        getActionBar().setDisplayShowCustomEnabled(true);  
//        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
//        View mTitleView = mInflater.inflate(R.layout.main_search_action_bar_layout,  
//                null);  
//        getActionBar().setCustomView(  
//                mTitleView,  
//                new ActionBar.LayoutParams(LayoutParams.MATCH_PARENT,  
//                        LayoutParams.WRAP_CONTENT));  
//        mSearchView = (SearchView) mTitleView.findViewById(R.id.search_view); 
//    }
    
	public boolean onQueryTextSubmit(String query) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onTabChanged(String tabId, String preTabId) {
		// TODO Auto-generated method stub
		if (mainHeaderActionView == null) {
			mainHeaderActionView = (MainHeaderActionView) findViewById(R.id.header_view);
		}
		
		mainHeaderActionView.setTitle(mHost.getCurrentTabTitle());
		if (TAB_1.equals(tabId)) {
			mainHeaderActionView.showSearchButton();
		}else if (TAB_2.equals(tabId)) {
			mainHeaderActionView.hideSearchButton();
		} else if (TAB_3.equals(tabId)) {
			mainHeaderActionView.hideSearchButton();
		}
	}

	public void onTabAction(String tabId, String nextTabId) {
		// TODO Auto-generated method stub
		
	}

//	public void onClick(View v) {
//
//		switch (v.getId()) {
//		case R.id.main_tab1:
//			setTab(TabType.COMPETITION.ordinal());
//			break;
//		case R.id.main_tab2:
//			setTab(TabType.NEWS.ordinal());
//			break;
//		case R.id.main_tab3:
//			setTab(TabType.USER.ordinal());
//			break;
//		}
//	}
//	
//	private void setTab(int index) {
//		mHost.setCurrentTab(currIndex);
//	}
}