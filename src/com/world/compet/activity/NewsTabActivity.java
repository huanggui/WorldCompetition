package com.world.compet.activity;

import com.world.compet.R;
import com.world.compet.adpter.NewsTabAdapter;
import com.world.compet.adpter.NewsTabAdapter.ItemElement;

import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;

public class NewsTabActivity extends Activity{
	
	private NewsTabAdapter mAdapter;
	private GridView gridView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news_layout);
		initView();
	}
	
	private void initView() {
		gridView = (GridView) findViewById(R.id.news_tab_gridview);
		gridView.setFocusable(false);
		mAdapter = new NewsTabAdapter(this);
		gridView.setAdapter(mAdapter);
	}
}
