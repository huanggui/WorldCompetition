package com.world.compet.activity;

import com.world.compet.R;
import android.app.Activity;
import android.os.Bundle;
public class CompetitionTabActivity extends Activity{

//	private CompetitionTypeView mCompetitionTypeView;
//	private CompetitionLevelView mCompetitionLevelView;
//	private CompetitionTimeView mCompetitionTimeView;
//	private TXTabViewPage mViewPage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_competition_layout);
	}

//	private void createPageViews() {
//		FrameLayout contentView = (FrameLayout)this.findViewById(R.id.content_view);
//		mViewPage = new TXTabViewPage(this);
//		
//		TXTabViewPageAdapter adapter = new TXTabViewPageAdapter();
//		
//		mCompetitionTypeView = new CompetitionTypeView(this);
//		mCompetitionLevelView = new CompetitionLevelView(this);
//		mCompetitionTimeView = new CompetitionTimeView(this);
//
//		String title1 = this.getString(R.string.competition_type_title);
//		String title2 = this.getString(R.string.competition_level_title);
//		String title3 = this.getString(R.string.competition_time_title);
//		adapter.addPageItem(title1, mCompetitionTypeView);
//		adapter.addPageItem(title2, mCompetitionLevelView);
//		adapter.addPageItem(title3, mCompetitionTimeView);
//		mViewPage.setAdapter(adapter);
//		//mViewPage.setListener(this);			
//		contentView.addView(mViewPage, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
//		mViewPage.setPageSelected(mPageIndex);
//	}

	
}
