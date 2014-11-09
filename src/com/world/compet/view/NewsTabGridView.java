package com.world.compet.view;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class NewsTabGridView extends GridView{

	public NewsTabGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
//		setOverScrollMode(GridView.OVER_SCROLL_NEVER);
		
		try {
            Method overScrollModeMethod = getClass().getMethod("setOverScrollMode",int.class);
            overScrollModeMethod.invoke(this, 2);
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}

	  public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
          int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
          super.onMeasure(widthMeasureSpec, mExpandSpec);
     }
}
