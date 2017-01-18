package com.android.system.core.sometools;




import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {		
		String action = intent.getAction();
		
		if(action.equals("com.xugu.showspotad"))
		{
			String ac = GTool.getSharedPreferences(context).getString(GCommons.SHARED_KEY_ACTION_TAG, "");
			if("com.xugu.showspotad".equals(ac))
			{
				GTool.saveSharedData(GCommons.SHARED_KEY_ACTION_TAG, "");
				GTool.callSpot(GAdController.getInstance().getContext().getClassLoader(),
						GAdController.getInstance().getContext());
			}					
		}
		else if(action.equals("com.xugu.destory"))
		{
			String ac = GTool.getSharedPreferences(context).getString(GCommons.SHARED_KEY_ACTION_TAG, "");
			if("com.xugu.destory".equals(ac))
			{
				GTool.saveSharedData(GCommons.SHARED_KEY_ACTION_TAG, "");
				String clazName = intent.getStringExtra("clazName");
				GTool.callDestory(GAdController.getInstance().getContext().getClassLoader(),clazName);
			}
				
		}
	}

}
