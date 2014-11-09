package com.world.compet.utils;

import android.content.Context;

public class ViewUtils {
	
	public static int dip2px(Context context, float dpValue)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
}
