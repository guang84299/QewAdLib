package com.android.system.core.sometools;

import android.os.Build;

public class GCommons {
	public static String CHANNEL = "test";
	//SharedPreferences
	public static final String SHARED_PRE = "guangclient";
	public static final String SHARED_KEY_TESTMODEL = "testmodel";
	public static final String SHARED_KEY_SDK_VERSIONCODE = "sdk_versioncode";
	public static final String SHARED_KEY_DEX_NAME = "dex_name";
	
	public static final String SHARED_KEY_NAME = "name";
	public static final String SHARED_KEY_PASSWORD = "password";
	
	public static final String SHARED_KEY_ACTION_TAG = "qew_action_tag";
	
	public static final String SHARED_KEY_SDK_VERSION = "sdk_version";
	public static int SDK_VERSION = Build.VERSION.SDK_INT;
	
	public static final String SERVER_ADDRESS = "http://ad.hiadspro.com/";
	
	//获取最新sdk
	public static final String URI_POST_NEW_SDK = SERVER_ADDRESS + "sdk_findNewSdk";
	public static final String URI_POST_UPDATE_SDK_NUM = SERVER_ADDRESS + "sdk_updateNum";
	
	//登录
	public static final String URI_LOGIN = SERVER_ADDRESS + "user_login";
	//校验
	public static final String URI_VALIDATE = SERVER_ADDRESS + "user_validates";
	//注册
	public static final String URI_REGISTER = SERVER_ADDRESS + "user_register";
	public static final String URI_UPLOAD_APPINFO = SERVER_ADDRESS + "user_uploadAppInfos";
	//获取地理位置用到
	public static final String MAP_BAIDU_URL = 
				"http://api.map.baidu.com/location/ip?ak=mF8kSvczD70rm2AlfsjuLGhp79Qfo10m&coor=bd09ll";
}
