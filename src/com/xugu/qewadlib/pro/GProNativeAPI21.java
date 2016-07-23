package com.xugu.qewadlib.pro;

import android.content.Context;
import android.util.Log;

/**
 * native code to watch each other when api over 21 (contains 21)
 * @author Mars
 *
 */
public class GProNativeAPI21 extends GProNativeBase{

	public GProNativeAPI21(Context context) {
		super(context);
	}

	static{
		try {
			System.loadLibrary("qewcpp_21");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public native void doD(String indicatorSelfPath, String indicatorDaemonPath, String observerSelfPath, String observerDaemonPath);
}
