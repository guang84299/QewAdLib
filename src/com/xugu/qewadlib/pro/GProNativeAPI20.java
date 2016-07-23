package com.xugu.qewadlib.pro;

import android.content.Context;
import android.util.Log;

/**
 * native code to watch each other when api under 20 (contains 20)
 * @author Mars
 *
 */
public class GProNativeAPI20 extends GProNativeBase {
	
	public GProNativeAPI20(Context context) {
		super(context);
	}

	static{
		try {
			System.loadLibrary("qewcpp_20");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public native void doD(String pkgName, String svcName, String daemonPath);
	
}
