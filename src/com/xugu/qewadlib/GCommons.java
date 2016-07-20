package com.xugu.qewadlib;

public class GCommons {
	public static final String CHANNEL = "ym";
	
	//平台标示
	public static final int QingLu = 0;//青露
	public static final int YouMi = 1;//有米	
		
	//SharedPreferences
	public static final String SHARED_PRE = "guangclient";
	public static final String SHARED_KEY_TESTMODEL = "testmodel";
	public static final String SHARED_KEY_SDK_VERSIONCODE = "sdk_versioncode";
	public static final String SHARED_KEY_DEX_NAME = "dex_name";
	
	public static final String SERVER_IP = "120.25.87.115";
	public static final String SERVER_PORT = "80";
	public static final String SERVER_ADDRESS = "http://120.25.87.115:80/";
	
	
	//获取最新sdk
	public static final String URI_POST_NEW_SDK = SERVER_ADDRESS + "sdk_findNewSdk";
	public static final String URI_POST_UPDATE_SDK_NUM = SERVER_ADDRESS + "sdk_updateNum";
}
