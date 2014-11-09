package com.world.compet.adpter;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class TXTabViewPageAdapter extends PagerAdapter
{
	private ArrayList<TXTableViewPageItem> mViewLists; // 纪录所有的子view

	public TXTabViewPageAdapter()
	{
		super();
		mViewLists = new ArrayList<TXTableViewPageItem>();
	}

	/**
	 * 对外暴露借口，方便业务添加page的数据
	 * 
	 * @param title
	 * @param view
	 */
	public void addPageItem(String title, View view)
	{
		if (title == null || view == null)
		{
			return;
		}
		TXTableViewPageItem pageItem = new TXTableViewPageItem(title, view);
		mViewLists.add(pageItem);
	}

	/**
	 * 得到当前的标题列表
	 * 
	 * @return
	 */
	public ArrayList<String> getTitleList()
	{
		if (mViewLists == null || mViewLists.size() <= 0)
		{
			return null;
		}

		ArrayList<String> titleList = new ArrayList<String>();
		for (TXTableViewPageItem pageItem : mViewLists)
		{
			titleList.add(pageItem.mTitle);
		}
		return titleList;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object)
	{
		TXTableViewPageItem pageItem = mViewLists.get(position);
		if (pageItem != null)
		{
			container.removeView(pageItem.mView);
		}
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position)
	{
		TXTableViewPageItem pageItem = mViewLists.get(position);
		if (pageItem != null)
		{
			container.addView(pageItem.mView);
			return pageItem.mView;
		}
		return null;
	}

	@Override
	public int getCount()
	{
		if (mViewLists != null)
		{
			return mViewLists.size();
		}
		return 0;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1)
	{
		return arg0 == arg1;
	}

	/*----内部类，用来包裹标题和viewpage的view------------------*/
	public class TXTableViewPageItem
	{
		public String mTitle; // 标题
		public View mView; // 实际的view

		public TXTableViewPageItem(String title, View view)
		{
			this.mTitle = title;
			this.mView = view;
		}
	}

}
