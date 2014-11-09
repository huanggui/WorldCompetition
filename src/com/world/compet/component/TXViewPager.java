package com.world.compet.component;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;

/**
 * 对系统的ViewPager包裹，支持左右方向的弹簧
 * TabPage不需要左右弹框效果了，所以这里注释掉了弹框相关代码
 * 
 * @author huanggui
 * add on 2014-11-9
 */
public class TXViewPager extends TXScrollViewBase<ViewPager>
{

	public TXViewPager(Context context)
	{
		super(context, ScrollDirection.SCROLL_DIRECTION_HORIZONTAL, ScrollMode.NONE);
	}

	public TXViewPager(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	/**
	 * 设置数据源
	 * 
	 * @param adapter
	 */
	public void setAdapter(PagerAdapter adapter)
	{
		if (adapter != null)
		{
			mScrollContentView.setAdapter(adapter);
		}
	}

	/**
	 * 设置viewpage的监听
	 * 
	 * @param listener
	 */
	public void setOnPageChangeListener(OnPageChangeListener listener)
	{
		mScrollContentView.setOnPageChangeListener(listener);
	}

	/**
	 * 设置当前项
	 * 
	 * @param item
	 */
	public void setCurrentItem(int item)
	{
		mScrollContentView.setCurrentItem(item);
	}

	/**
	 * 得到当前项的索引
	 * 
	 * @return
	 */
	public int getCurrentItem()
	{
		return mScrollContentView.getCurrentItem();
	}

	/**
	 * 移除所有的view，方便再添加
	 */
	public void removeAllViews()
	{
		mScrollContentView.removeAllViews();
	}

	@Override
	protected boolean isReadyForScrollStart()
	{
	    // 5.0新版规范，tab不需要左右弹框效果了，这里注释掉
//		PagerAdapter adapter = mScrollContentView.getAdapter();
//		if (adapter != null)
//		{
//			return (mScrollContentView.getCurrentItem() == 0);
//		}
		return false;
	}

	@Override
	protected boolean isReadyForScrollEnd()
	{
	    // 5.0新版规范，tab不需要左右弹框效果了，这里注释掉
//		PagerAdapter adapter = mScrollContentView.getAdapter();
//		if (adapter != null)
//		{
//			return (mScrollContentView.getCurrentItem() == (adapter.getCount() - 1));
//		}
		return false;
	}

	@Override
	protected ViewPager createScrollContentView(Context context)
	{
		ViewPager viewPager = new ViewPager(context);

		return viewPager;
	}

}
