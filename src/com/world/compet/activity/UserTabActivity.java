package com.world.compet.activity;

import com.world.compet.R;
import com.world.compet.adpter.TXTabViewPageAdapter;
import com.world.compet.component.TXTabViewPage;
import com.world.compet.view.CompetitionLevelView;
import com.world.compet.view.CompetitionTimeView;
import com.world.compet.view.CompetitionTypeView;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

public class UserTabActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_layout);
	}
}
