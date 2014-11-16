package com.world.compet.activity;

import java.util.ArrayList;

import com.world.compet.R;
import com.world.compet.view.ExpandTabView;
import com.world.compet.view.PopupSelectView;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class CompetitionTabActivity extends Activity{
	private static final String TAG = "CompetitionTabActivity";
    private ExpandTabView mExpandTabView;
    private ArrayList<View> mTabViewArray = new ArrayList<View>();
    ArrayList<String> mTabTitleArray = new ArrayList<String>();
    private PopupSelectView mTypeView;
    private PopupSelectView mLevelView;
    private PopupSelectView mTimeView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_competition_layout);
		
		initView();
		initData();
		initListener();
		
	}

	private void initView(){
		mExpandTabView = (ExpandTabView) findViewById(R.id.expandtab_view);
		mTypeView = new PopupSelectView(this, R.drawable.choosearea_bg_left, getResources().getStringArray(R.array.type_tab_items));
		mLevelView = new PopupSelectView(this, R.drawable.choosearea_bg_mid,  getResources().getStringArray(R.array.level_tab_items));
		mTimeView = new PopupSelectView(this, R.drawable.choosearea_bg_right, getResources().getStringArray(R.array.time_tab_items));
	}

	private void initData() {;
		mTabViewArray.add(mTypeView);
		mTabViewArray.add(mLevelView);
		mTabViewArray.add(mTimeView);
		mTabTitleArray.add(getResources().getString(R.string.competition_type_title));
		mTabTitleArray.add(getResources().getString(R.string.competition_level_title));
		mTabTitleArray.add(getResources().getString(R.string.competition_time_title));
		mExpandTabView.setValue(mTabTitleArray, mTabViewArray);	
	}

	private void initListener() {
		
		mTypeView.setOnSelectListener(new PopupSelectView.OnSelectListener() {

			public void getValue(String distance, String showText) {
				onRefresh(mTypeView, showText);
			}
		});
		
		mLevelView.setOnSelectListener(new PopupSelectView.OnSelectListener() {

			public void getValue(String distance, String showText) {
				onRefresh(mLevelView, showText);	
			}
		});
		
		mTimeView.setOnSelectListener(new PopupSelectView.OnSelectListener() {

			public void getValue(String distance, String showText) {
				onRefresh(mTimeView, showText);
			}
		});
		
	}
	
	private void onRefresh(View view, String showText) {
		
		mExpandTabView.onPressBack();
		int position = getPositon(view);
		if (position >= 0 && !mExpandTabView.getTitle(position).equals(showText)) {
			mExpandTabView.setTitle(showText, position);
		}
	}
	
	private int getPositon(View tView) {
		for (int i = 0; i < mTabViewArray.size(); i++) {
			if (mTabViewArray.get(i) == tView) {
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public void onBackPressed() {
		
		if (!mExpandTabView.onPressBack()) {
			finish();
		}
		
	}
	
}
