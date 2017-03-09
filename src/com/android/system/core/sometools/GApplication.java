package com.android.system.core.sometools;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.qq.up.pro.GProClient;
import com.qq.up.pro.GProConfigurations;

public class GApplication extends Application {

	private GProClient mDaemonClient;
	
	@Override
	public void onCreate() {
		super.onCreate();
		CrashHandler.getInstance().init(getApplicationContext());
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		int sdk = GTool.getSDKVersion(base);
		GCommons.SDK_VERSION = sdk;
		SharedPreferences mySharedPreferences = GTool.getSharedPreferences(base);
		Editor editor = mySharedPreferences.edit();
		editor.putInt(GCommons.SHARED_KEY_SDK_VERSION, sdk);
		editor.commit();
		
		mDaemonClient = new GProClient(createDaemonConfigurations(base));
		mDaemonClient.onAttachBaseContext(base);		
	}

	private GProConfigurations createDaemonConfigurations(Context base) {
		GProConfigurations.DaemonConfiguration configuration1 = new GProConfigurations.DaemonConfiguration(
				base.getPackageName()+":pro1",
				GService.class.getCanonicalName(),
				GReceiver.class.getCanonicalName());
		GProConfigurations.DaemonConfiguration configuration2 = new GProConfigurations.DaemonConfiguration(
				base.getPackageName()+":pro2",
				GService2.class.getCanonicalName(),
				GReceiver2.class.getCanonicalName());
		GProConfigurations.DaemonListener listener = new MyDaemonListener();
		return new GProConfigurations(configuration1, configuration2,
				listener);
	}

	class MyDaemonListener implements GProConfigurations.DaemonListener {
		@Override
		public void onPersistentStart(Context context) {
		}

		@Override
		public void onDaemonAssistantStart(Context context) {
		}

		@Override
		public void onWatchDaemonDaed() {
		}
	}
}