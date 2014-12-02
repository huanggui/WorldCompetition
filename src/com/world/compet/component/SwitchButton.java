package com.world.compet.component;

import com.world.compet.R;
import com.world.compet.utils.HandlerUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SwitchButton extends RelativeLayout implements OnClickListener{
	
	/**
	 * 开关动画默认持续时长(ms)
	 */
	public static final int SWITCH_ANIMATION_DURATION = 150;

	private ImageView switch_on_Bkg;//开关开启时的背景
	private ImageView switch_off_Bkg;//关闭时的背景
//	private ImageView switch_off_anim;//关闭打开时的动画对象
	private ImageView switch_default_Bkg;//不可点击时的背景
	private ImageView switch_left_Bkg;//圆饼开关，左右各一个
	private ImageView switch_right_Bkg;
	private TextView left_text;//开关左边文案
	private TextView right_text;//开关右边文案
	
	private Animation mTransOpenAnimation;//圆饼开关打开时平移动画(从左向右平移)
	private Animation mTransCloseAnimation;//圆饼开关关闭时平移动画(从右向左平移)
	private Animation mScaleOpenAnimation;//开关打开时的缩小的动画(从有到无)
	private Animation mScaleCloseAnimation;//开关关闭时的放大的动画(从无到有)
	
	private int mWidth;//开关的实际宽度，单位：像素
	private int mCycle;//开关的圆饼宽度，单位：像素  这2个量是为了按钮的自适应平移距离
	private Context mContext;
	private LayoutInflater mInflater;
	private View mView;
	private int mAnimationDuration;
	
	//关-->开，此时右边圆饼可见，左边不可见
	private AnimationListenerAdapter mTransOpenAnimationListener = new AnimationListenerAdapter() {
		
		@Override
		public void onAnimationEnd(Animation arg0) {
			setOffToOnView();
//			switch_left_Bkg.setVisibility(View.INVISIBLE);
//			switch_right_Bkg.setVisibility(View.VISIBLE);
//			switch_off_Bkg.setVisibility(View.INVISIBLE);
//			left_text.setVisibility(View.VISIBLE);
//			right_text.setVisibility(View.INVISIBLE);
//			switch_on_Bkg.setVisibility(View.VISIBLE);
		}
	};
	
	//开-->关，此时右边圆饼不可见，左边可见
	private AnimationListenerAdapter mTransCloseAnimationListener = new AnimationListenerAdapter() {
		
		@Override
		public void onAnimationEnd(Animation arg0) {
			setOnToOffView();
//			switch_left_Bkg.setVisibility(View.VISIBLE);
//			switch_right_Bkg.setVisibility(View.INVISIBLE);
//			switch_off_Bkg.setVisibility(View.VISIBLE);
//			left_text.setVisibility(View.INVISIBLE);
//			right_text.setVisibility(View.VISIBLE);
//			switch_on_Bkg.setVisibility(View.INVISIBLE);
		}
	};
	
	//当前开关状态，true为开启，false为关闭
	private boolean isSwitchOn = false;
	
	//开关监听器
	private OnSwitchButtonClickListener onSwitchButtonClickListener;
	//是否设置了开关监听器
	private boolean isSwitchListenerOn = false;
	
	public SwitchButton(Context context) {
		super(context);
		mContext = context;
		mView = this;
		mAnimationDuration = SWITCH_ANIMATION_DURATION;
		init();
	}
	
	
	public SwitchButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mView = this;
		mAnimationDuration = SWITCH_ANIMATION_DURATION;
		init();
	}
	
	public void setAnimationDuration(int duration) {
		mAnimationDuration = duration;
	}
	
	public int getAnimationDuration() {
		return mAnimationDuration;
	}
	
	private void init() {
		
		setOnClickListener(this);
		
		//初始化界面
		mInflater = LayoutInflater.from(mContext);
		mInflater.inflate(R.layout.setting_switch_view_layout, this);
		//LayoutInflater.from(getContext()).inflate(R.layout.setting_switch_view_layout, this, true);
		switch_default_Bkg = (ImageView) findViewById(R.id.img_default_bg);
		switch_on_Bkg = (ImageView) findViewById(R.id.img_open_bg);
		switch_off_Bkg = (ImageView) findViewById(R.id.img_close_bg);
//		switch_off_anim = (ImageView) findViewById(R.id.img_close_anim);
		switch_left_Bkg = (ImageView) findViewById(R.id.img_switch_left);
		switch_right_Bkg = (ImageView) findViewById(R.id.img_switch_right);
		left_text = (TextView) findViewById(R.id.text_left);
		right_text = (TextView) findViewById(R.id.text_right);
	}
	
	private void initAnimation() {
		
		mWidth = getWidth();
		if (switch_left_Bkg.getVisibility() == View.VISIBLE) {
			mCycle = switch_left_Bkg.getWidth();
		}else {
			mCycle = switch_right_Bkg.getWidth();
		}
		
		mScaleOpenAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.setting_switch_scale_out);
		mScaleCloseAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.setting_switch_scale_in);
		mScaleOpenAnimation.setDuration(mAnimationDuration);
		mScaleCloseAnimation.setDuration(mAnimationDuration);
		mTransOpenAnimation = new TranslateAnimation(0, mWidth-mCycle, 0, 0);
		mTransOpenAnimation.setDuration(mAnimationDuration);
		mTransOpenAnimation.setInterpolator(new AccelerateInterpolator());
		mTransOpenAnimation.setAnimationListener(mTransOpenAnimationListener);
		mTransCloseAnimation = new TranslateAnimation(0, -(mWidth-mCycle), 0, 0);
		mTransCloseAnimation.setDuration(mAnimationDuration);
		mTransCloseAnimation.setInterpolator(new AccelerateInterpolator());
		mTransCloseAnimation.setAnimationListener(mTransCloseAnimationListener);
	}
	
	//初始化开关时只做初始化，不做动画效果
	public void setSwitchState(boolean switchState) {
		isSwitchOn = switchState;
		initSwitchButton();
	}
	
	public void setTitlesOfSwitch(String leftTitle, String rightTitle) {
		left_text.setText(leftTitle);
		right_text.setText(rightTitle);
	}
	
	public boolean getSwitchState() {
		return isSwitchOn;
	}
	
	//响应开关点击切换，同时做动画
	public void updateSwitchStateWithAnim(boolean switchState) {
		isSwitchOn = switchState;
		startSwitchAnimation();
	}
	
	private void initSwitchButton () {
		if (isSwitchOn) {//开关初始为打开状态
			setOffToOnView();
		}else {
			setOnToOffView();
		}
	}
	
	private void setOffToOnView() {
		switch_off_Bkg.clearAnimation();
		switch_left_Bkg.setVisibility(View.INVISIBLE);
		switch_right_Bkg.setVisibility(View.VISIBLE);
		switch_off_Bkg.setVisibility(View.INVISIBLE);
//		switch_off_anim.setVisibility(View.INVISIBLE);
		left_text.setVisibility(View.VISIBLE);
		right_text.setVisibility(View.INVISIBLE);
//		switch_on_Bkg.setVisibility(View.VISIBLE);
	}
	
	private void setOnToOffView() {
		switch_off_Bkg.clearAnimation();
		switch_left_Bkg.setVisibility(View.VISIBLE);
		switch_right_Bkg.setVisibility(View.INVISIBLE);
		switch_off_Bkg.setVisibility(View.VISIBLE);
//		switch_off_anim.setVisibility(View.INVISIBLE);
		left_text.setVisibility(View.INVISIBLE);
		right_text.setVisibility(View.VISIBLE);
//		switch_on_Bkg.setVisibility(View.INVISIBLE);
	}
	
	private void startSwitchAnimation() {
		
		initAnimation();//在这里初始化动画，是因为动画中要获取开关的宽度参数，需要在view显示后才行
		
		if (isSwitchOn) { //开关：关-->开
			switch_left_Bkg.startAnimation(mTransOpenAnimation);
			switch_off_Bkg.setVisibility(View.VISIBLE);
//			switch_off_anim.setVisibility(View.VISIBLE);
			switch_off_Bkg.startAnimation(mScaleOpenAnimation);
			right_text.setVisibility(View.INVISIBLE);
		}else { //开关：开-->关
			switch_right_Bkg.startAnimation(mTransCloseAnimation);
			switch_off_Bkg.setVisibility(View.VISIBLE);
			switch_off_Bkg.startAnimation(mScaleCloseAnimation);
			left_text.setVisibility(View.INVISIBLE);
		}
	}
	
	public void setOnSwitchListener(OnSwitchButtonClickListener listener) {
		onSwitchButtonClickListener = listener;
		isSwitchListenerOn = true;
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (enabled) {
			switch_default_Bkg.setVisibility(View.GONE);
			switch_off_Bkg.setVisibility(View.VISIBLE);
			switch_on_Bkg.setVisibility(View.VISIBLE);
		}else {
			switch_default_Bkg.setVisibility(View.VISIBLE);
			switch_off_Bkg.setVisibility(View.INVISIBLE);
			switch_on_Bkg.setVisibility(View.INVISIBLE);
//			switch_off_anim.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 这个类用来减少实现动画监听时不需要实现的方法
	 * 
	 */
	private static class AnimationListenerAdapter implements AnimationListener {

		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
			
		}

		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
			
		}

		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub
			
		}
	}

	
	public interface OnSwitchButtonClickListener {
		abstract void onSwitchButtonClick(View view,boolean isSwitchOn);
	}


	public void onClick(View v) {
		// TODO Auto-generated method stub
		updateSwitchStateWithAnim(!getSwitchState());//响应开关点击切换并做动画
		
		//如果设置了监听器，则调用此方法进行开关相应的业务逻辑处理，为了不影响动画效果，业务处理延迟300ms执行
		HandlerUtils.getMainHandler().postDelayed(new Runnable(){
			public void run() {
				if(isSwitchListenerOn) {
					onSwitchButtonClickListener.onSwitchButtonClick(mView,isSwitchOn);
				}
			}
				
		},mAnimationDuration);
		
	}
}
