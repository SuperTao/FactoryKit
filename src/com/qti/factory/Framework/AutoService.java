/*
 * Copyright (c) 2013-2015, Qualcomm Technologies, Inc. All Rights Reserved.
 * Qualcomm Technologies Proprietary and Confidential.
 */
package com.qti.factory.Framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import com.qti.factory.Utils;
import com.qti.factory.Values;
import com.qti.factory.Framework.MainApp.ServiceInfo;
import android.content.ComponentName;

public class AutoService extends IntentService {
	String TAG = "AutoService";
	private static Context mContext = null;

	public AutoService() {
		super("AutoService");
	}

	private int taskCounter = 0;
	private long INTERVAL_TIME = 500;
	private long FINISH_TIME = 30 * 1000;
	// 定时器，30s结束，调用finish()函数.定时器开启期间，没500ms调用一次onTick()函数
	CountDownTimer taskCountDownTimer = new CountDownTimer(FINISH_TIME,
			INTERVAL_TIME) {
		@Override
		public void onTick(long arg0) {
			ArrayList<ServiceInfo> intentArrayList = MainApp.getInstance().intentArrayList;
			if (intentArrayList.size() > 0) {
				for (int i = 0; i < intentArrayList.size(); i++) {
					int delay = 0;
					// 判断是否有ServiceDelay, 有些activity中是没有的
					if (intentArrayList.get(i).paraMap
							.get(Values.KEY_SERVICE_DELAY) != null)
						// 例如：parameter="service=com.qti.factory.LightSensor.LightSensorService;ServiceDelay=3000;"
						// delay = 3000
						delay = Integer.valueOf(intentArrayList.get(i).paraMap
								.get(Values.KEY_SERVICE_DELAY));
					// 判断上面获得的时间，例如3000是否和 taskCounter * INTERVAL_TIME相等
					if (delay == taskCounter * INTERVAL_TIME) {
						Intent intent = new Intent();
						ComponentName componentName = new ComponentName(getPackageName(),"intentArrayList.get(i).intentService" );
						intent.setComponent(componentName);
						/* 从这里就可以看出, ServiceDelay表示app运行之后，对应的服务不是马上启动的，而是在延时ServiceDelay之后启动的
						 * 这个定时器按照顺序进行比对各个服务的启动时间，在启动相应的服务。
						 * 例如：parameter="service=com.qti.factory.LightSensor.LightSensorService;ServiceDelay=3000;"
						 * 在延时3000ms之后启动com.qti.LightSensorLightSensorService这个服务
						 */
						// 启动服务
						startService(intent);
						logd(taskCounter
								+ " "
								+ intentArrayList.get(i).intentService
								.getAction());
					}
				}
			}
			taskCounter++;
		}

		@Override
		public void onFinish() {
		}
	};

	private void init(Context context) {

		mContext = context;
		taskCounter = 0;

		// To save test time, enable some devices first
		Utils.enableWifi(mContext, true);
		Utils.enableBluetooth(true);
		Utils.enableGps(mContext, true);
		Utils.enableNfc(mContext, true);
		Utils.enableCharging(true);
		Utils.configScreenTimeout(mContext, 1800000); // 1 min
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		logd("RunningService=" + MainApp.getInstance().getServiceCounter());
		if (MainApp.getInstance().getServiceCounter() > 0)
			return;
		init(getApplicationContext());

		List<Map<String, ?>> list = (List<Map<String, ?>>) MainApp
				.getInstance().mItemList;
		for (int i = 0; i < list.size(); i++) {
			Map<String, ?> item = list.get(i);
			if ("true".equals(item.get("auto")))
				logd(item.get("title") + " " + item.get("result"));
			// auto = "true"
			if ("true".equals(item.get("auto")))
				// RESULT_FAIL = "failed" || "NULL"
				// 所有没有测试的activity都符合这个判断
				if (Utils.RESULT_FAIL.equals(item.get("result"))
						|| "NULL".equals(item.get("result"))) {

					logd("Add " + item.get("title") + " to service list");
					// 查找服务
					// 例如：parameter="service=com.qti.factory.LightSensor.LightSensorService;ServiceDelay=3000;"
					// service = comqti.factory.LightSensor.LightSensorService
					String service = ((HashMap<String, String>) item
							.get("parameter")).get("service");
					Intent serviceIntent = new Intent(service);
					// Values.KEY_SERVICE_INDEX = "key"
					/ "key" = i
					serviceIntent.putExtra(Values.KEY_SERVICE_INDEX, i);
					// 将serviceIntent添加到动态数组中
					MainApp.getInstance().addServiceList(
							new ServiceInfo(serviceIntent, item
									.get("parameter")));
				}
		}

		taskCountDownTimer.start();
	}

	@Override
	public void onDestroy() {
		logd("");
		// if (taskCountDownTimer != null)
		// taskCountDownTimer.cancel();
		super.onDestroy();
	}

	private void logd(Object s) {

		Thread mThread = Thread.currentThread();
		StackTraceElement[] mStackTrace = mThread.getStackTrace();
		String mMethodName = mStackTrace[3].getMethodName();

		s = "[" + mMethodName + "] " + s;
		Log.d(TAG, s + "");
	}

	private void loge(Object s) {

		Thread mThread = Thread.currentThread();
		StackTraceElement[] mStackTrace = mThread.getStackTrace();
		String mMethodName = mStackTrace[3].getMethodName();

		s = "[" + mMethodName + "] " + s;
		Log.e(TAG, s + "");
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
