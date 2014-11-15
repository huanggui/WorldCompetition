package com.world.compet.activity;

import java.util.ArrayList;

import com.world.compet.R;
import com.world.compet.view.ExpandTabView;
import com.world.compet.view.ViewLeft;
import com.world.compet.view.ViewMiddle;
import com.world.compet.view.ViewRight;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class CompetitionTabActivity extends Activity{
	private static final String TAG = "CompetitionTabActivity";
    private ExpandTabView mExpandTabView;
    private ArrayList<View> mViewArray = new ArrayList<View>();
    private ViewLeft mTypeView;
    private ViewMiddle mLevelView;
    private ViewRight mTimeView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_competition_layout);
		
		initView();
		initValue();
		
	}

	private void initView(){
		mExpandTabView = (ExpandTabView) findViewById(R.id.expandtab_view);
		mTypeView = new ViewLeft(this);
		mLevelView = new ViewMiddle(this);
		mTimeView = new ViewRight(this);
	}

	private void initValue() {
		
		mViewArray.add(mTypeView);
		mViewArray.add(mLevelView);
		mViewArray.add(mTimeView);
		ArrayList<String> mTextArray = new ArrayList<String>();
		mTextArray.add("璺濈");
		mTextArray.add("鍖哄煙");
		mTextArray.add("璺濈");
		mExpandTabView.setValue(mTextArray, mViewArray);
		mExpandTabView.setTitle(mTypeView.getShowText(), 0);
		mExpandTabView.setTitle(mLevelView.getShowText(), 1);
		mExpandTabView.setTitle(mTimeView.getShowText(), 2);
		
	}

	private void initListener() {
		
		mTypeView.setOnSelectListener(new ViewLeft.OnSelectListener() {

			public void getValue(String distance, String showText) {
				onRefresh(mTypeView, showText);
			}
		});
		
		mLevelView.setOnSelectListener(new ViewMiddle.OnSelectListener() {
			
			public void getValue(String showText) {
				
				onRefresh(mLevelView,showText);
				
			}
		});
		
		mTimeView.setOnSelectListener(new ViewRight.OnSelectListener() {

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
		Toast.makeText(CompetitionTabActivity.this, showText, Toast.LENGTH_SHORT).show();

	}
	
	private int getPositon(View tView) {
		for (int i = 0; i < mViewArray.size(); i++) {
			if (mViewArray.get(i) == tView) {
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
