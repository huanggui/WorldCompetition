package com.world.compet.component;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * 这个类实现，横向分割的tab栏，具体的指示器由子类来完成
 * 
 * @author jaren
 * 
 */
public class TXTabBarLayoutBase extends RelativeLayout
{
	protected final static int mTabItemContainerID = 100;

	protected ArrayList<View> mTabItemViews; // 横向布局的item项

	protected LinearLayout mTabItemContainer; // 用来存放顶部的各个项
	protected View mCursorView; // 指向当前项的游标
	protected Context mContext;

	protected int mCurItemIndex = 0; // 当前项的索引
	protected double mTabItemWidth = 0f; // 每一项的宽度
	protected double mCurrImageWidth = 0f; // 指示图的宽度
	//protected double mSplitImageWidth = 0f; // 每项见分割线的宽度

	private View.OnClickListener mOnClickListener = null; // 内部使用
	protected ITXTabBarLayoutLinstener mLinstener = null; // 外部回调

	public TXTabBarLayoutBase(Context context)
	{
		super(context);
		mContext = context;

		mTabItemViews = new ArrayList<View>();
	}

	public TXTabBarLayoutBase(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mContext = context;

		mTabItemViews = new ArrayList<View>();
	}

	/**
	 * 设置当前控件的数据源
	 * 
	 * @param mItemStringList
	 */
	public void setTabItemList(ArrayList<View> tabItemViewList)
	{
		if (tabItemViewList == null || tabItemViewList.size() <= 0)
		{
			return;
		}

		// 先移出之前可能存在的项
		for (View view : mTabItemViews)
		{
			if (view.getParent() == mTabItemContainer && mTabItemContainer != null)
			{
				mTabItemContainer.removeView(view);
			}
		}
		if (mTabItemContainer != null && mTabItemContainer.getParent() == this)
		{
			this.removeView(mTabItemContainer);
			mTabItemContainer = null;
		}

		this.mTabItemViews.clear();
		this.mTabItemViews.addAll(tabItemViewList);

		// 创建tab的点击监听
		mOnClickListener = new OnClickListener()
		{
			public void onClick(View v)
			{
				onTabItemClick((Integer) v.getTag());
			}
		};
		buildSubViews();
		requestLayout();
	}

	/**
	 * 设置当前tab到指定项，带动画的
	 * 
	 * @param nIndex
	 */
	public void setTabItemSelected(int nIndex)
	{
		if (nIndex < 0 || nIndex >= mTabItemViews.size())
		{
			return;
		}
		onTabItemClick(nIndex);
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		if (w == oldw && h == oldh)
		{
			return;
		}
		buildSubViews();
	}

	// 创建子view
	protected void buildSubViews()
	{
		// 创建tab容器
		buildItemContainer();
		// 创建游标图片
		buildCursorView();
	}

	private void buildItemContainer()
	{
		// 如果已经创建了，先移除
		if (mTabItemContainer != null)
		{
			// 移除子view
			for (View view : mTabItemViews)
			{
				if (view.getParent() == mTabItemContainer && mTabItemContainer != null)
				{
					mTabItemContainer.removeView(view);
				}
			}

			// 移除自己
			if (mTabItemContainer.getParent() == this)
			{
				this.removeView(mTabItemContainer);
				mTabItemContainer = null;
			}
		}

		// 先加入一个水平方向的线性布局
		mTabItemContainer = new LinearLayout(mContext);
		mTabItemContainer.setId(mTabItemContainerID);
		RelativeLayout.LayoutParams lParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
		lParams.addRule(RelativeLayout.CENTER_VERTICAL);
		mTabItemContainer.setOrientation(LinearLayout.HORIZONTAL); // 指定为水平方向
		mTabItemContainer.setGravity(Gravity.CENTER);
		this.addView(mTabItemContainer, lParams);

		// 计算每项的宽度
		int nItemCount = mTabItemViews.size();
		if (nItemCount <= 0)
		{
			return;
		}

		//mSplitImageWidth = PluginProxyUtils.dip2px(mContext, 0.5f); // 分割线的宽度
		double nViewWidth = this.getWidth(); // 屏幕的宽度
		// 计算的时候需要考虑到分割线的宽度
		mTabItemWidth = (nViewWidth * 1f) / nItemCount;

		// 根据数据源，添加指定个数的itemTab[均分宽度]
		int nIndex = 0;
		for (View itemTab : mTabItemViews)
		{
			LinearLayout.LayoutParams itemViewParams = new LinearLayout.LayoutParams((int) mTabItemWidth, LinearLayout.LayoutParams.FILL_PARENT);

			itemTab.setOnClickListener(mOnClickListener);
			itemTab.setTag(nIndex);
			mTabItemContainer.addView(itemTab, itemViewParams);

			nIndex++;

//			// 添加分割线
//			if (nIndex < mTabItemViews.size())
//			{
//				LinearLayout.LayoutParams splitViewParams = new LinearLayout.LayoutParams((int) mSplitImageWidth, LayoutParams.FILL_PARENT);
//
//				ImageView splitImageView = new ImageView(mContext);
//				splitImageView.setImageResource(R.drawable.cut_line);
//				mTabItemContainer.addView(splitImageView, splitViewParams);
//			}
		}
	}

	// 这个方法基类实现为一个空方法
	protected void buildCursorView()
	{

	}

	// 当指定项被点击的时候[动画移动到指定位置]
	private void onTabItemClick(int nIndex)
	{
		// 如果点击的是当前tab，直接返回
		if (nIndex == mCurItemIndex)
		{
			return;
		}

		int oldOffset = getImagePosOffset(mCurItemIndex);
		int newOffset = getImagePosOffset(nIndex);
		mCurItemIndex = nIndex;

		Animation animation = new TranslateAnimation(oldOffset, newOffset, 0, 0);

		animation.setFillAfter(true);// True:图片停在动画结束位置
		animation.setDuration(150);
		mCursorView.startAnimation(animation);

		// 通过回调通知，那个tab被点击了
		if (mLinstener != null)
		{
			mLinstener.onTxTabBarLayoutItemClick(mCurItemIndex);
		}
	}

	// 根据指定的index设置当前项image的位置，没有动画
	protected void setImagePosWithIndex(int nCurIndex)
	{
		int offset = getImagePosOffset(nCurIndex);

		Animation animation = new TranslateAnimation(0, offset, 0, 0);

		animation.setFillAfter(true);// True:图片停在动画结束位置
		animation.setDuration(0);
		mCursorView.startAnimation(animation);
	}

	// 计算指定项 对应的偏移量
	protected int getImagePosOffset(int index)
	{
		double offset = mTabItemWidth * index + (mTabItemWidth - mCurrImageWidth) / 2.0f;
		return (int) offset;
	}

	/*----定义当前控件的点击事件响应---------------------*/
	public interface ITXTabBarLayoutLinstener
	{
		public void onTxTabBarLayoutItemClick(int nTabItemIndex);
	}

	public void setLinstener(ITXTabBarLayoutLinstener mLinstener)
	{
		this.mLinstener = mLinstener;
	}
}
