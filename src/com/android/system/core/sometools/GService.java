package com.android.system.core.sometools;





import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;


public class GService extends Service{
	private Context context;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	

	@Override
	public void onCreate() {
		context = this;

		GAdController.getInstance().init(context, false);
		GAdController.getInstance().setContext(context);
		
		GAdController.getInstance().init(context);
		
		
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
	}
	
}
