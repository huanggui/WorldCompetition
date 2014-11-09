package com.world.compet.component;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.support.v4.view.ViewPager;
import java.util.ArrayList;

import com.world.compet.R;
import com.world.compet.adpter.TXTabViewPageAdapter;
import com.world.compet.utils.ViewUtils;
/**
 * 包含TXTabBarLayout 和 一个TXViewPage控件，两个控件互相交互
 * 
 * @author huanggui
 * add on 2014-11-9
 * 
 */
public class TXTabViewPage extends RelativeLayout
		implements TXTabBarLayoutBase.ITXTabBarLayoutLinstener,ViewPager.OnPageChangeListener
{
	private final static int nTXTableBarLayoutID = 1000; // 定义顶部bar的id
	private final static int nTXTableBarBottomLineID = 1001; // 定义顶部bar下边的白线的id
	private static final int TAB_HEIGHT = 40; // tab标题高度

	private Context mContext;

	private TXTabBarLayout mTxTabBarLayout = null; // 顶部的tabbar［这个也可以放在底部］
	private TXViewPager mTxViewPager = null; // 带弹簧功能的viewpage
	private View mBottomLine = null;

	private int mLastPositionOffsetPixels = 0; // 纪录上一次的偏移，方便计算是向左还是向右滑动
	private int mPositionOffset = 0; // 向左，这个值 －1，向右这个值 ＋1
	private int mLastPosition = 0; // 最后一次停下来索引的位置

	public TXTabViewPageAdapter mTxTabViewPageAdapter = null; // 当前layout对应的数据源
	private ITXTabViewPageListener mListener;

	public TXTabViewPage(Context context)
	{
		super(context);
		mContext = context;

		buildSubViews();
	}

	public TXTabViewPage(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mContext = context;

		buildSubViews();
		// 水平方向居中
		this.setGravity(Gravity.CENTER_HORIZONTAL);
	}

	/**
	 * 这个adapter同时给tab和viewpage提供数据
	 */
	public void setAdapter(TXTabViewPageAdapter viewPageAdapter)
	{
		this.mTxTabViewPageAdapter = viewPageAdapter;

		ArrayList<String> titleList = mTxTabViewPageAdapter.getTitleList();
		mTxTabBarLayout.setItemStringList(titleList);

		mTxViewPager.setAdapter(mTxTabViewPageAdapter);
		requestLayout();
	}

	/**
	 * 返回指定tab位置的文本标签
	 * 
	 * @param position
	 * @return
	 */
	public LinearLayout getTabItemView(int position)
	{
		if (mTxTabBarLayout != null)
		{
			return mTxTabBarLayout.getTabItemView(position);
		}
		return null;
	}

	/**
	 * 设置当前选中的tab
	 * 
	 * @param position
	 */
	public void setPageSelected(int position)
	{
		if (mTxTabBarLayout != null)
		{
			mTxTabBarLayout.setTabItemSelected(position);
		}
		if (mTxViewPager != null)
		{
			mTxViewPager.setCurrentItem(position);
		}
	}

	private void buildSubViews()
	{
		// 创建title
		buildTxTabBarlayout();
		// 创建 viewpage
		buildTxViewPager();
	}

	private void buildTxTabBarlayout()
	{
		// 如果已经存在，先移出再创建
		if (mTxTabBarLayout != null && mTxTabBarLayout.getParent() == this)
		{
			this.removeView(mTxTabBarLayout);
			mTxTabBarLayout = null;
		}

		mTxTabBarLayout = new TXTabBarLayout(mContext);
		mTxTabBarLayout.setLinstener(this);
		mTxTabBarLayout.setId(nTXTableBarLayoutID);

//		mTxTabBarLayout.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.game_classbar_bg));
		mTxTabBarLayout.setBackgroundColor(Color.WHITE);
		int tabBarHeight = ViewUtils.dip2px(mContext, TAB_HEIGHT);

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, tabBarHeight);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP); // 设置成顶端
		mTxTabBarLayout.setGravity(Gravity.CENTER_HORIZONTAL);
		this.addView(mTxTabBarLayout, layoutParams);
		
		mBottomLine = new View(mContext);
		mBottomLine.setId(nTXTableBarBottomLineID);
		mBottomLine.setBackgroundColor(mContext.getResources().getColor(R.color.top_tab_bottom_line));//
		RelativeLayout.LayoutParams bottomParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 1);
		bottomParams.addRule(RelativeLayout.BELOW, nTXTableBarLayoutID);
		this.addView(mBottomLine, bottomParams);
	}

	private void buildTxViewPager()
	{
		if (mTxViewPager != null && mTxViewPager.getParent() == this)
		{
			// 先将 mTxViewPager 的子view移除
			mTxViewPager.removeAllViews();
			// 再将自己移除
			this.removeView(mTxViewPager);
			mTxViewPager = null;
		}

		mTxViewPager = new TXViewPager(mContext);
		mTxViewPager.setOnPageChangeListener(this);
		// 填充剩下的所有空间
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		layoutParams.addRule(RelativeLayout.BELOW, nTXTableBarBottomLineID);
		mTxViewPager.setGravity(Gravity.CENTER_HORIZONTAL);
		this.addView(mTxViewPager, layoutParams);
	}

	/*----各种回调接口------------*/

	public void onTxTabBarLayoutItemClick(int nTabItemIndex)
	{
		// 将viewpage切换到指定的tab
		mLastPosition = -1; // 这里将这个值设置为异常值
		mTxViewPager.setCurrentItem(nTabItemIndex);

		// 回调通知，即将跳转到 nTabItemIndex
		if (mListener != null)
		{
			mListener.onTxTabViewPageWillSelect(nTabItemIndex);
		}
	}

	public void onPageScrollStateChanged(int state)
	{
		if (mTxTabViewPageAdapter == null)
		{
			return;
		}

		if (state == ViewPager.SCROLL_STATE_SETTLING)
		{
			// mLastPosition != -1，这个条件用在直接跳转的时候
			if (mPositionOffset != 0 && mLastPosition != -1)
			{
				int nWillSelectPosition = mLastPosition + mPositionOffset;
				if (nWillSelectPosition < 0)
				{
					nWillSelectPosition = 0;
				}
				if (nWillSelectPosition >= mTxTabViewPageAdapter.getCount())
				{
					nWillSelectPosition = mTxTabViewPageAdapter.getCount() - 1;
				}

				// 发送通知消息
				if (mListener != null)
				{
					mListener.onTxTabViewPageWillSelect(nWillSelectPosition);
				}
				mPositionOffset = 0; // 执行后归0
			}
		}
	}

	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
	{
		/*
		 * positionOffsetPixels 在向左的时候会慢慢减小，在向右的时候会慢慢变大， position 表示当前的索引
		 */
		if (positionOffsetPixels > mLastPositionOffsetPixels)
		{
			mPositionOffset = 1;
		}
		else if (positionOffsetPixels < mLastPositionOffsetPixels)
		{
			mPositionOffset = -1;
		}
		else
		{
			mPositionOffset = 0;
		}
		// 更新上一次的位置
		mLastPositionOffsetPixels = positionOffsetPixels;
	}

	public void onPageSelected(int position)
	{
		// 切换mTxTabBarLayout 到指定的index
		mTxTabBarLayout.setTabItemSelected(position);
		if (mListener != null)
		{
			mListener.onTxTabViewPageSelected(position);
		}
		mLastPosition = position;
	}

	public void setListener(ITXTabViewPageListener mListener)
	{
		this.mListener = mListener;
	}

	/*---回调接口定义----------------------*/
	public interface ITXTabViewPageListener
	{
		/**
		 * 已经选择了具体的哪个tab
		 * 
		 * @param position
		 */
		public void onTxTabViewPageSelected(final int position);

		/**
		 * 当前viewpage即将跳到哪个tab，如果是当前tab弹回当前tab，不会发出通知
		 * 
		 * @param position
		 */
		public void onTxTabViewPageWillSelect(final int position);
	}

}
