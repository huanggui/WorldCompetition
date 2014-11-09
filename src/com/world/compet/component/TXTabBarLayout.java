package com.world.compet.component;

import java.util.ArrayList;

import com.world.compet.R;
import com.world.compet.utils.ViewUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 实现顶部的pageview，从而到达分页的效果。
 * @author huanggui
 *  add on 2014-11-9
 */
public class TXTabBarLayout extends TXTabBarLayoutBase
{
	public final static int TABITEM_TEXT_ID = 100; // 文本的id
	public final static int TABITEM_TIPS_TEXT_ID = 101; // 数字文本
	
	public static final int ITEM_TEXT_FONT_SIZE = 16;

	public TXTabBarLayout(Context context)
	{
		super(context);

	}

	public TXTabBarLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);

	}

	/**
	 * 设置当前控件的数据源
	 * 
	 * @param mItemStringList
	 */
	public void setItemStringList(ArrayList<String> mItemStringList)
	{
		if (mItemStringList == null || mItemStringList.size() <= 0)
		{
			return;
		}

		// 将字符串转换成一个个的TextView
		ArrayList<View> viewList = new ArrayList<View>();
		for (String string : mItemStringList)
		{
			LinearLayout contentView = new LinearLayout(mContext);
			contentView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.navigation_bar_right_selector));
			contentView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			contentView.setGravity(Gravity.CENTER);

			TextView itemView = new TextView(mContext);
			itemView.setText(string);
			itemView.setTextSize(ITEM_TEXT_FONT_SIZE);
			itemView.setGravity(Gravity.CENTER);
			itemView.setId(TABITEM_TEXT_ID);

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			contentView.addView(itemView, layoutParams);

			viewList.add(contentView);
		}
		this.setTabItemList(viewList);
	}

	@Override
	public void setTabItemSelected(int nIndex) {
		super.setTabItemSelected(nIndex);
		TextView textView;
		for(int i=0; i<mTabItemViews.size(); i++){
			textView = (TextView)mTabItemViews.get(i).findViewById(TABITEM_TEXT_ID);
			if(nIndex==i){
				textView.setTextColor(mContext.getResources().getColor(R.color.second_tab_selected_color));
			}else{
				textView.setTextColor(mContext.getResources().getColor(R.color.second_tab_unselected_color));
			}
		}
	}

	/**
	 * 得到指定位置的文本标签
	 * 
	 * @param position
	 * @return
	 */
	public LinearLayout getTabItemView(int position)
	{
		if (mTabItemViews != null && position < mTabItemViews.size() && position >= 0)
		{
			return (LinearLayout) mTabItemViews.get(position);
		}
		return null;
	}

	protected void buildCursorView()
	{
		if (mCursorView != null && mCursorView.getParent() == this)
		{
			this.removeView(mCursorView);
			mCursorView = null;
		}

		// 创建游标，游标的宽度需要特殊计算,高度使用图片的高度［这里的图片需要一个.9文件］
		mCursorView = new ImageView(mContext);
//		mCursorView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.game_classbar_selectline));
		mCursorView.setBackgroundColor(mContext.getResources().getColor(R.color.second_cursor_index));
		int nCursorViewHeight = ViewUtils.dip2px(mContext, 2);

		int nViewWidth = this.getWidth(); // 根据父窗口的宽度来计算

		mTabItemWidth = nViewWidth * 1.0f / mTabItemViews.size();
		mCurrImageWidth = mTabItemWidth; // 滑块的宽度为当前块的 80%宽

		RelativeLayout.LayoutParams lParams = new RelativeLayout.LayoutParams((int) mCurrImageWidth, nCursorViewHeight);
		lParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		this.addView(mCursorView, lParams);

		// 根据当前index项，设置初始位置
		setImagePosWithIndex(mCurItemIndex);
	}
}
