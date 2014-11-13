package com.world.compet.view;

import com.world.compet.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

public class MainHeaderActionView extends RelativeLayout{
	
	private Context mContext;
	private TextView mTitleView;
	private ImageView mUserImageView;
	private SearchView mSearchView;
	private ImageView mSearchBtnView;

	public MainHeaderActionView(Context context) {
		super(context);
		mContext = context;
		initView();
	}
	
	public MainHeaderActionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
	}

	private void initView() {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.header_view_layout, this, true);
		mTitleView = (TextView) findViewById(R.id.title);
		mUserImageView = (ImageView) findViewById(R.id.user_image);
		mSearchView = (SearchView) findViewById(R.id.search_view);
		mSearchBtnView = (ImageView) findViewById(R.id.search_image);
		
		mSearchBtnView.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				mTitleView.setVisibility(View.GONE);
				mUserImageView.setVisibility(View.GONE);
				mSearchBtnView.setVisibility(View.GONE);
				mSearchView.setVisibility(View.VISIBLE);
			}
		});
	}
	
	public void hideSearchButton() {
		mSearchBtnView.setVisibility(View.GONE);
	}
	
	public void showSearchButton() {
		mSearchBtnView.setVisibility(View.VISIBLE);
	}
}
