package com.world.compet.activity;

import com.world.compet.R;
import com.world.compet.component.BottomTabHost;
import com.world.compet.component.TabView;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;

public class MainActivity extends BottomTabActivity {
	
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
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mHost = getTabHost();
        initTabs();
    }
    
    private void initTabs() {

//		TypedArray icons = getResources().obtainTypedArray(R.array.tab_icons);
		String[] titles = getResources().getStringArray(R.array.tab_titles);
		
		Resources resource = getResources();
		com.world.compet.component.BottomTabHost.TabSpec tabSpec = mHost.newTabSpec("tab1");
//		tabSpec.setIndicator(titles[0], resource.getDrawable(icons.getResourceId(0, 0)), resource.getDrawable(R.drawable.tab_item_bg_selector));
		tabSpec.setIndicator(titles[0]);
		tabSpec.setContent(new Intent(MainActivity.this, CompetitonActivity.class));
//		mTabBtns[0] = (TabView) mHost.addTab(tabSpec,R.id.main_tab1);

		tabSpec = mHost.newTabSpec("tab2");
//		tabSpec.setIndicator(titles[1], resource.getDrawable(icons.getResourceId(1, 0)), resource.getDrawable(R.drawable.tab_item_bg_selector));
		tabSpec.setIndicator(titles[1]);
		tabSpec.setContent(new Intent(MainActivity.this, NewsActivity.class));
//		mTabBtns[1] = (TabView) mHost.addTab(tabSpec,R.id.main_tab2);

		tabSpec = mHost.newTabSpec("tab3");
//		tabSpec.setIndicator(titles[2], resource.getDrawable(icons.getResourceId(2, 0)), resource.getDrawable(R.drawable.tab_item_bg_selector));
		tabSpec.setIndicator(titles[2]);
		tabSpec.setContent(new Intent(MainActivity.this, UserActivity.class));
//		mTabBtns[2] = (TabView) mHost.addTab(tabSpec,R.id.main_tab3);
		
//		icons.recycle();
	}
}