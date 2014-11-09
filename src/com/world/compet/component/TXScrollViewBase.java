package com.world.compet.component;

import com.world.compet.R;
import com.world.compet.utils.DeviceUtils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * 完成一个可以拖过界的容器view <br>
 * 1.这个view本身继承于linearLayout。<br>
 * 2.这个类是个虚基类
 * 
 * @author huanggui
 * add on 2014-11-9
 * 
 */

public abstract class TXScrollViewBase<T extends View> extends LinearLayout
{
	public static final int SMOOTH_SCROLL_DURATION_MS = 200; // 滚动动画的持续时间，短的
	public static final float FRICTION = 2.0f; // 滑动2个像素，view只移动一个像素

	protected boolean mIsBeingDragged = false; // 是否已经开始拖动
	protected ScrollState mCurScrollState = ScrollState.ScrollState_Initial; // 当前的滑动状态

	protected ScrollMode mScrollMode = ScrollMode.BOTH; // 当前view支持的滚动方式
	protected ScrollDirection mScrollDirection = ScrollDirection.SCROLL_DIRECTION_VERTICAL;

	protected PointF mInitialMotionPointF = new PointF(0, 0); // 初始按下的位置
	protected PointF mLastMotionPointF = new PointF(0, 0); // 最后的一个位置

	private int mTouchSlop = 0; // 判断是否为一个真实拖动
	private Interpolator mScrollAnimationInterpolator = new DecelerateInterpolator(); // 滚动动画的变形器
	private SmoothScrollRunnable mCurrentSmoothScrollRunnable = null; // 动画驱动线程

	protected T mScrollContentView; // 实际的内容滚动View
	protected FrameLayout mContentViewWrapper;

	private View mTipsView = null; // 用来显示提示信息的view，加载中。。。，无数据。

	public TXScrollViewBase(Context context, ScrollDirection scrollDirection, ScrollMode mode)
	{
		super(context);

		this.mScrollDirection = scrollDirection;
		this.mScrollMode = mode;

		initView(context);
	}

	public TXScrollViewBase(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		// 从 attrs 中读取 mode
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TXScrollView);
		if (typedArray != null)
		{
			// 得到滚动方向是水平还是垂直
			int scrollDirection = typedArray.getInt(R.styleable.TXScrollView_ScrollDirection, 0);
			this.mScrollDirection = ScrollDirection.mapIntToValue(scrollDirection);

			// 得到下拉支持的方向，[默认都不支持]
			int scrollMode = typedArray.getInt(R.styleable.TXScrollView_ScrollMode, 3);
			this.mScrollMode = ScrollMode.mapIntToValue(scrollMode);
			
			typedArray.recycle();
		}

		initView(context);
	}

	protected void initView(Context context)
	{
		if (mScrollDirection == ScrollDirection.SCROLL_DIRECTION_HORIZONTAL)
		{
			setOrientation(LinearLayout.HORIZONTAL);
		}
		else
		{
			setOrientation(LinearLayout.VERTICAL);
		}
		setGravity(Gravity.CENTER);

		// 获取滑动的最小阈值
		ViewConfiguration configuration = ViewConfiguration.get(context);
		mTouchSlop = configuration.getScaledTouchSlop();

		// 获取实际的内容现实View，放入本地
		mScrollContentView = createScrollContentView(context);
		addScrollContentView(context, mScrollContentView);
	}

	/**
	 * 设置显示的view，如果 tipsView == null,则是移除之前的view
	 * 
	 * @param tipsView
	 */
	public void setTipsView(View tipsView)
	{
		if (mContentViewWrapper == null)
		{
			return;
		}

		if (mTipsView != null)
		{
			mContentViewWrapper.removeView(mTipsView);
		}

		// 添加新view
		mTipsView = tipsView;
		if (mTipsView != null)
		{
			// 添加到中心居中
			mContentViewWrapper.addView(mTipsView, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
			// 设置到前端显示
			mTipsView.bringToFront();
		}
	}

	/*----view group 相关函数重载------------------------------------------------------------------------*/

	@Override
	public final boolean onInterceptTouchEvent(MotionEvent event)
	{
		final int action = event.getAction();

		// 如果是抬起或者取消，释放对事件拦截的控制
		if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP)
		{
			mIsBeingDragged = false;
			return false;
		}

		// 如果正在拖动，又不是按下事件，则拦截所有事件
		if (mIsBeingDragged == true && action != MotionEvent.ACTION_DOWN)
		{
			return true;
		}

		switch (action)
		{
		case MotionEvent.ACTION_MOVE:
		{
			// 判断是否能拖动的关键逻辑在这里
			if (isReadyForScroll() == true)
			{
				final float x = event.getX();
				final float y = event.getY();

				final float diff, absDiff;
				final float oppositeDiff; // 另一个方向上的偏移

				if (mScrollDirection == ScrollDirection.SCROLL_DIRECTION_HORIZONTAL)
				{
					diff = x - mLastMotionPointF.x;
					oppositeDiff = y - mLastMotionPointF.y;
				}
				else
				{
					diff = y - mLastMotionPointF.y;
					oppositeDiff = x - mLastMotionPointF.x;
				}
				absDiff = Math.abs(diff);

				// 开始判断是否为一个真实的拖动
				if (absDiff > mTouchSlop && absDiff > Math.abs(oppositeDiff))
				{
					// 顶部处理[diff > 0 表示往下拉]
					if (diff >= 1f && isReadyForScrollStart())
					{
						mLastMotionPointF.x = x;
						mLastMotionPointF.y = y;
						mIsBeingDragged = true;
						mCurScrollState = ScrollState.ScrollState_FromStart;
					}

					if (diff <= -1f && isReadyForScrollEnd())
					{
						mLastMotionPointF.x = x;
						mLastMotionPointF.y = y;
						mIsBeingDragged = true;
						mCurScrollState = ScrollState.ScrollState_FromEnd;
					}
				}
			}
		}
			break;
		case MotionEvent.ACTION_DOWN:
		{
			if (isReadyForScroll() == true)
			{
				mLastMotionPointF.x = mInitialMotionPointF.x = event.getX();
				mLastMotionPointF.y = mInitialMotionPointF.y = event.getY();
				mIsBeingDragged = false;
			}
		}
			break;
		default:
			break;
		}

		return mIsBeingDragged;
	}

	/**
	 * 真实的viewgroup的移动是由这个重载函数来负责处理的
	 */
	public final boolean onTouchEvent(MotionEvent event)
	{
		// 按下的时候按在了屏幕边缘
		if (event.getAction() == MotionEvent.ACTION_DOWN && event.getEdgeFlags() != 0)
		{
			return false;
		}

		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
		{
			if (isReadyForScroll() == true)
			{
				mLastMotionPointF.x = mInitialMotionPointF.x = event.getX();
				mLastMotionPointF.y = mInitialMotionPointF.y = event.getY();
				return true;
			}
		}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
		{
			return onTouchEventCancelAndUp();
		}
		case MotionEvent.ACTION_MOVE:
		{
			if (mIsBeingDragged == true)
			{
				mLastMotionPointF.x = event.getX();
				mLastMotionPointF.y = event.getY();

				// 执行拖动动作核心是 scrollTo
				if (mScrollMode != ScrollMode.NOSCROLL) {
					scrollMoveEvent();
				}
				return true;
			}
		}
			break;

		default:
			break;
		}

		return false;
	}

	// 刷新动作的子类需要覆盖此函数
	protected boolean onTouchEventCancelAndUp()
	{
		if (mIsBeingDragged == true)
		{
			mIsBeingDragged = false;
			// 弹回到初始的位置
			smoothScrollTo(0);
			return true;
		}
		return false;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);

		onScrollViewSizeChange(w, h);

		// 请求重新layout
		post(new Runnable()
		{
			public void run()
			{
				requestLayout();
			}
		});
	}

	// 当view的size变化的时候，子类可以重写该方法来做些其他的工作
	protected void onScrollViewSizeChange(int w, int h)
	{
		refreshScrollContentViewSize(w, h);
	}

	/*-----更新UI状态使得可以拖动--------------------------------------------------------------------------*/

	// 获取最大的滚动偏移量
	protected int getMaximumScrollOffset()
	{
		if (mScrollDirection == ScrollDirection.SCROLL_DIRECTION_HORIZONTAL)
		{
			return Math.round(getWidth() / FRICTION);
		}
		else
		{
			return Math.round(getHeight() / FRICTION);
		}
	}

	/*-----addView 相关----------------------------------------------------*/

	/**
	 * 重载系统addview方法，保证只有 scrollContentView 是一个 ViewGroup的时候，才会被添加到 contentView上
	 */
	public void addView(View child, int index, ViewGroup.LayoutParams params)
	{
		if (child != null && mScrollContentView != null && (mScrollContentView instanceof ViewGroup))
		{
			ViewGroup viewGroup = (ViewGroup) mScrollContentView;
			viewGroup.addView(child, index, params);
		}
	}

	// 对这个类的覆盖需要特别小心, 后续需要处理
	protected void addViewInternal(View child, int index, ViewGroup.LayoutParams params)
	{
		super.addView(child, index, params);
	}

	// 当sizechange的时候，需要更新下主要内容
	protected final void refreshScrollContentViewSize(int width, int height)
	{
		if (mContentViewWrapper == null)
		{
			return;
		}

		LinearLayout.LayoutParams lParams = (LayoutParams) mContentViewWrapper.getLayoutParams();
		if (mScrollDirection == ScrollDirection.SCROLL_DIRECTION_HORIZONTAL)
		{
			if (lParams.width != width)
			{
				lParams.width = width;
				mContentViewWrapper.requestLayout();
			}
		}
		else
		{
			if (lParams.height != height)
			{
				lParams.height = height;
				mContentViewWrapper.requestLayout();
			}
		}
	}

	// 将实际滚动内容添加到当前layout里面
	private void addScrollContentView(Context context, T scrollContentView)
	{
		if (scrollContentView == null)
		{
			return;
		}

//		// 做一层包裹,方便在sizechange的时候做些处理
//		mContentViewWrapper = new FrameLayout(context);
//		mContentViewWrapper.addView(scrollContentView, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);

		// 再将包裹结构插入到当前实际的layout
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		super.addView(scrollContentView, -1, params);
	}

	/*--------------------------------------*/

	protected boolean isReadyForScroll()
	{
		if (isReadyForScrollStart() == true)
		{
			return true;
		}
		if (isReadyForScrollEnd() == true)
		{
			return true;
		}
		return false;
	}

	// 真正的滚动操作是在这里执行的,返回 newScrollValue，方便子类处理
	protected int scrollMoveEvent()
	{
		int newScrollValue = 0;
		// 初始和开始的鼠标位置
		final float initialMotionValue;
		final float lastMotionValue;

		if (mScrollDirection == ScrollDirection.SCROLL_DIRECTION_HORIZONTAL)
		{
			initialMotionValue = mInitialMotionPointF.x;
			lastMotionValue = mLastMotionPointF.x;
		}
		else
		{
			initialMotionValue = mInitialMotionPointF.y;
			lastMotionValue = mLastMotionPointF.y;
		}

		if (mCurScrollState == ScrollState.ScrollState_FromStart)
		{
			newScrollValue = Math.round(Math.min(initialMotionValue - lastMotionValue, 0) / FRICTION);
		}
		else
		{
			newScrollValue = Math.round(Math.max(initialMotionValue - lastMotionValue, 0) / FRICTION);
		}

		contentViewScrollTo(newScrollValue);

		return newScrollValue;
	}

	/**
	 * 将内容滚动到指定的位置
	 * 
	 * @param value
	 */
	protected void contentViewScrollTo(int value)
	{
		final int maximunScrollVaule = getMaximumScrollOffset();
		value = Math.min(maximunScrollVaule, Math.max(-maximunScrollVaule, value));

		if (mScrollDirection == ScrollDirection.SCROLL_DIRECTION_HORIZONTAL)
		{
			scrollTo(value, 0);
		}
		else
		{
			scrollTo(0, value);
		}
	}

	public final void smoothScrollTo(int newScrollValue)
	{
		smoothScrollTo(newScrollValue, getSmoothScrollDuration(), 0, null);
	}

	public final void smoothScrollTo(int newScrollValue, ISmoothScrollRunnableListener listener)
	{
		smoothScrollTo(newScrollValue, getSmoothScrollDuration(), 0, listener);
	}

	/**
	 * 执行动画
	 * 
	 * @param newScrollValue
	 *            新目标地址
	 * @param duration
	 *            距离
	 * @param delayMillis
	 *            延迟多少完成
	 * @param listener
	 *            完成的通知对象
	 */
	public final void smoothScrollTo(int newScrollValue, long duration, long delayMillis, ISmoothScrollRunnableListener listener)
	{
		// 如果动画对象不为空，停止动画
		if (mCurrentSmoothScrollRunnable != null)
		{
			mCurrentSmoothScrollRunnable.stop();
		}

		final int oldScrollValue;
		if (mScrollDirection == ScrollDirection.SCROLL_DIRECTION_HORIZONTAL)
		{
			oldScrollValue = getScrollX();
		}
		else
		{
			oldScrollValue = getScrollY();
		}

		if (oldScrollValue != newScrollValue)
		{
			mCurrentSmoothScrollRunnable = new SmoothScrollRunnable(oldScrollValue, newScrollValue, duration, listener);
			if (delayMillis > 0)
			{
				postDelayed(mCurrentSmoothScrollRunnable, delayMillis);
			}
			else
			{
				post(mCurrentSmoothScrollRunnable);
			}
		}
	}

	/*----虚函数接口定义，需要子类来实现----------------------------*/

	// 返回是否已经准备好开始位置的滚动
	protected abstract boolean isReadyForScrollStart();

	// 返回是否已经准备好结束位置的滚动
	protected abstract boolean isReadyForScrollEnd();

	// 创建一个真实的放在内部滚动的view
	protected abstract T createScrollContentView(Context context);

	/*-----定义滚动方向的枚举-------------------------------------*/

	/**
	 * 定义支持的工作模式
	 * 
	 * @author jaren
	 * 
	 */
	public static enum ScrollMode
	{
		/**
		 * 头部下拉
		 */
		PULL_FROM_START,

		/**
		 * 底部上拉
		 */
		PULL_FROM_END,

		/**
		 * 都支持
		 */
		BOTH,

		/**
		 * 只支持弹簧，不支持刷新
		 */
		NONE
		/**连弹簧都不支持,加这个属性后，下面其实用位操作更合理，先不改了*/
		,NOSCROLL;

		static ScrollMode mapIntToValue(int modeIntValue)
		{
			switch (modeIntValue)
			{
			case 0:
				return PULL_FROM_START;
			case 1:
				return PULL_FROM_END;
			case 2:
				return BOTH;
			case 3:
				return NONE;
			case 4:
				return NOSCROLL;
			default:
				break;
			}
			return BOTH;
		}
	}

	/**
	 * 内部枚举定义当前类支持的滚动方向
	 * 
	 * @author jaren
	 * 
	 */
	public static enum ScrollDirection
	{
		/**
		 * 垂直滚动
		 */
		SCROLL_DIRECTION_VERTICAL,

		/**
		 * 水平滚动
		 */
		SCROLL_DIRECTION_HORIZONTAL;

		static ScrollDirection mapIntToValue(int modeIntValue)
		{
			switch (modeIntValue)
			{
			case 0:
				return SCROLL_DIRECTION_VERTICAL;
			case 1:
				return SCROLL_DIRECTION_HORIZONTAL;
			case 2:

			default:
				break;
			}
			return SCROLL_DIRECTION_VERTICAL;
		}
	}

	public static enum ScrollState
	{
		/**
		 * 在鼠标按下和抬起的时候，会进入这个状态
		 */
		ScrollState_Initial,

		/**
		 * 从开始位置滑动
		 */
		ScrollState_FromStart,

		/**
		 * 从结束的位置开始滑动
		 */
		ScrollState_FromEnd,
	}

	/*-----动画相关的部分----－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－----*/

	/**
	 * 滚动动画驱动工作线程
	 * 
	 * @author jaren
	 * 
	 */
	public class SmoothScrollRunnable implements Runnable
	{

		private final Interpolator mInterpolator; // 动画插值器
		private final int mScrollToY; // 结束位置
		private final int mScrollFromY; // 开始位置
		private final long mDuration; // 持续时间

		private boolean mContinueRunning = true;
		private long mStartTime = -1;
		private int mCurrentY = -1;

		private ISmoothScrollRunnableListener mScrollRunnableListener;

		public SmoothScrollRunnable(int fromY, int toY, long duration, ISmoothScrollRunnableListener listener)
		{
			mScrollFromY = fromY;
			mScrollToY = toY;
			mDuration = duration;

			mScrollRunnableListener = listener;
			mInterpolator = mScrollAnimationInterpolator;
		}

		public void run()
		{
			if (mStartTime == -1)
			{
				mStartTime = System.currentTimeMillis();
			}
			else
			{
				long normalizedTime = (1000 * (System.currentTimeMillis() - mStartTime)) / mDuration;

				normalizedTime = Math.max(Math.min(normalizedTime, 1000), 0);
				final int deltaY = Math.round((mScrollFromY - mScrollToY) * mInterpolator.getInterpolation(normalizedTime / 1000f));
				mCurrentY = mScrollFromY - deltaY;

				contentViewScrollTo(mCurrentY);
			}

			if (mContinueRunning == true && mScrollToY != mCurrentY)
			{
				// 为一个机型适配的crash做适配，很恶心，增加一个机型适配
				String model = DeviceUtils.getModel();
				if(TextUtils.isEmpty(model) || (!model.contains("OZZO138T") && !model.contains("W9800B"))){
					ViewCompat.postOnAnimation(TXScrollViewBase.this, this);
				}
			}
			else
			{
				if (mScrollRunnableListener != null)
				{
					mScrollRunnableListener.onSmoothScrollFinished();
				}
			}
		}

		public void stop()
		{
			mContinueRunning = false;
			removeCallbacks(this);
		}
	}

	/*----回调接口定义---------*/
	public static interface ISmoothScrollRunnableListener
	{
		void onSmoothScrollFinished();
	}

	public void recycleData()
	{

	}
	
	protected int getSmoothScrollDuration(){
		return SMOOTH_SCROLL_DURATION_MS;
	}
}
