package com.world.compet.component;

import com.world.compet.R;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 一级页底部的自定义tab的布局视图内容
 * @author huanggui
 * add on 2014-11-8
 */
public class TabView extends RelativeLayout {

    private TextView mTitle;
    private ImageView mIcon;
    private Button mToastBtn;

    public TabView(Context context) {
        super(context);
        init(context);
    }

    public TabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TabView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.main_tab_view, this, true);
        setClickable(true);

        mTitle = (TextView) findViewById(R.id.title);
        mIcon = (ImageView) findViewById(R.id.icon);
        mToastBtn = (Button) findViewById(R.id.toast_number);
    }

	@Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    public void setTitle(int txtResId) {
        if (txtResId != 0) {
            mTitle.setText(txtResId);
        }
    }

    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            mTitle.setText(title);
        }
    }
    
    /**
     * 设置tab的描述文字的颜色 （你妹的，方法名里如果有color字样的，会被lint检查出错，其实是没问题的。这里故意拼错）
     * @param resId
     */
    public void setTitleTextCollor(int resId){
    	mTitle.setTextColor(getContext().getResources().getColor(resId));
    }

    public void setIcon(int iconResId) {
    	mIcon.setImageResource(iconResId);
    }
    
    public void setToastNumber(int count){
    	if(count > 0){
    		mToastBtn.setVisibility(View.VISIBLE);
    		mToastBtn.setText(String.valueOf(count));
    	}else{
    		mToastBtn.setVisibility(View.GONE);
    	}
    }
    
}
