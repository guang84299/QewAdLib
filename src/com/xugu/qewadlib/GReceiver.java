package com.xugu.qewadlib;




import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {		
		String action = intent.getAction();
		if("com.xugu.showspotad".equals(action))
		{
			GTool.callSpot(GAdController.getInstance().getContext().getClassLoader(),
					GAdController.getInstance().getContext());
		}
		
	}

}
