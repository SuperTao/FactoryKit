/*
 * Copyright (c) 2013-2014, Qualcomm Technologies, Inc. All Rights Reserved.
 * Qualcomm Technologies Proprietary and Confidential.
 */
package com.qti.factory.Framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import com.qti.factory.android_serialport_api.SerialPort;
//import com.qti.factory.android_serialport_api.SerialPortFinder;
import android.content.SharedPreferences;

public class MainApp extends Application {

	private static MainApp mMainApp;
	private static Context mContext;
	private static String TAG = "MainApp";
	private int servcieCounter;

	public static class ServiceInfo {
		ServiceInfo(Intent intent, Object object) {
			intentService = intent;
			this.paraMap = (HashMap<String, String>) object;
		}

		Intent intentService;
		HashMap<String, String> paraMap;
	}

	ArrayList<ServiceInfo> intentArrayList = new ArrayList<ServiceInfo>();

	void clearAllService() {
		if (intentArrayList.size() > 0) {
			for (int i = 0; i < intentArrayList.size(); i++) {
				Intent intent = intentArrayList.get(i).intentService;
				logd("===" + intent);
				mContext.stopService(intent);
			}
			intentArrayList.clear();
		}
	}

	void addServiceList(ServiceInfo serviceInfo) {
		intentArrayList.add(serviceInfo);
	}

	public static class FunctionItem {

		String name;
		String packageName;// the key for get test name
		String auto;
		HashMap<String, String> parameter = new HashMap<String, String>();
	}

	private void init(Context context) {
		mContext = context;
	}

	public List<? extends Map<String, ?>> mItemList;

	@Override
	public void onCreate() {
		logd("");
		init(getApplicationContext());
		super.onCreate();
	}

	@Override
	public void onLowMemory() {
		logd("");
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		logd("");
		super.onTerminate();
	}

	public static MainApp getInstance() {
		if (mMainApp == null)
			mMainApp = new MainApp();
		return mMainApp;
	}

	public MainApp() {
	}

	public Context getContext() {
		return mContext;
	}

	private static void logd(Object s) {

		Thread mThread = Thread.currentThread();
		StackTraceElement[] mStackTrace = mThread.getStackTrace();
		String mMethodName = mStackTrace[3].getMethodName();

		s = "[" + mMethodName + "] " + s;
		Log.d(TAG, s + "");
	}

	public int getServiceCounter() {
		return servcieCounter;
	}

	public void voteServcieCounter() {
		this.servcieCounter++;
	}

	public void unvoteServcieCounter() {
		this.servcieCounter--;
	}

	// private GpsStatus mGpsStatus;
	// private Iterable<GpsSatellite> mSatellites;
	// List<String> satelliteList = new ArrayList<String>();
	// GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener() {
	//
	// public void onGpsStatusChanged(int arg0) {
	//
	// switch (arg0) {
	// case GpsStatus.GPS_EVENT_STARTED:
	// break;
	// case GpsStatus.GPS_EVENT_STOPPED:
	// break;
	// case GpsStatus.GPS_EVENT_FIRST_FIX:
	// pass();
	// break;
	// case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
	// logd("GPS_EVENT_SATELLITE_STATUS");
	// mGpsStatus = mLocationManager.getGpsStatus(null);
	// mSatellites = mGpsStatus.getSatellites();
	// Iterator<GpsSatellite> iterator = mSatellites.iterator();
	// int count = 0;
	// satelliteList.clear();
	// while (iterator.hasNext()) {
	// GpsSatellite gpsS = (GpsSatellite) iterator.next();
	// satelliteList.add(count++, "Prn" + gpsS.getPrn() + " Snr:" +
	// gpsS.getSnr());
	// }
	// if (count >= MIN_SAT_NUM)
	// pass();
	// break;
	// default:
	// break;
	// }
	//
	// }
	//
// };



	//	public SerialPortFinder mSerialPortFinder = new SerialPortFinder();
	private SerialPort mSerialPort = null;

	public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
		if (mSerialPort == null) {
			/* Read serial port parameters */
			SharedPreferences sp = getSharedPreferences("android_serialport_api.sample_preferences", MODE_PRIVATE);
			String path = sp.getString("DEVICE", "");
			int baudrate = Integer.decode(sp.getString("BAUDRATE", "-1"));

			/* Check parameters */
//			if ( (path.length() == 0) || (baudrate == -1)) {
//				throw new InvalidParameterException();
//			}

			/* Open the serial port */
//			mSerialPort = new SerialPort(new File(path), baudrate, 0);
			System.out.println("mSerialPort1");
			mSerialPort = new SerialPort(new File("/dev/ttyHSL1"), 115200, 0);
			System.out.println("mSerialPort2");
		}
		return mSerialPort;
	}
	
	public SerialPort getSerialPort0() throws SecurityException, IOException, InvalidParameterException {
		if (mSerialPort == null) {
			/* Read serial port parameters */
			SharedPreferences sp = getSharedPreferences("android_serialport_api.sample_preferences", MODE_PRIVATE);
			String path = sp.getString("DEVICE", "");
			int baudrate = Integer.decode(sp.getString("BAUDRATE", "-1"));

			/* Check parameters */
//			if ( (path.length() == 0) || (baudrate == -1)) {
//				throw new InvalidParameterException();
//			}

			/* Open the serial port */
//			mSerialPort = new SerialPort(new File(path), baudrate, 0);
			System.out.println("mSerialPort3");
			mSerialPort = new SerialPort(new File("/dev/ttyHSL0"), 115200, 0);
			System.out.println("mSerialPort4");
		}
		return mSerialPort;
	}

	public void closeSerialPort() {
		if (mSerialPort != null) {
			mSerialPort.close();
			mSerialPort = null;
		}
	}

}
