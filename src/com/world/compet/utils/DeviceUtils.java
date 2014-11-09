package com.world.compet.utils;

import android.text.TextUtils;

/**
 * 跟设备信息相关，需要在程序一启动就调用
 * 
 * @author huanggui
 * 
 */
public class DeviceUtils {

	public static String model;

	public static String getModel() {
		if (TextUtils.isEmpty(model)) {
			model = android.os.Build.MODEL;
		}
		return model;
	}

}
