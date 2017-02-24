package com.android.system.core.sometools;




import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class GBootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {		
		String action = intent.getAction();
		Log.e("----------------------", "action="+action);
		if(action == null)
			return;
		GAdController.getInstance().init(context, false);
	}

}
