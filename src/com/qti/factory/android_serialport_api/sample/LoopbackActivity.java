/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package com.qti.factory.android_serialport_api.sample;

import java.io.IOException;

import com.qti.factory.R;
import com.qti.factory.Utils;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LoopbackActivity extends SerialPortActivity {

	byte mValueToSend;
	boolean mByteReceivedBack;
	Object mByteReceivedBackSemaphore = new Object();
	Integer mIncoming = new Integer(0);
	Integer mOutgoing = new Integer(0);
	Integer mLost = new Integer(0);
	Integer mCorrupted = new Integer(0);

	SendingThread mSendingThread;
	TextView mTextViewOutgoing;
	TextView mTextViewIncoming;
	TextView mTextViewLost;
	TextView mTextViewCorrupted;
	private static String resultString = Utils.RESULT_FAIL;
	public boolean sendFlag = false;

	private class SendingThread extends Thread {
		@Override
		public void run() {
			while (!isInterrupted()&&sendFlag==false) {
				synchronized (mByteReceivedBackSemaphore) {
					mByteReceivedBack = false;
					try {
						if (mOutputStream != null) {
							mOutputStream.write(mValueToSend);
						} else {
							return;
						}
					} catch (IOException e) {
						e.printStackTrace();
						return;
					}
					mOutgoing++;
					// Wait for 100ms before sending next byte, or as soon as
					// the sent byte has been read back.
					try {
						mByteReceivedBackSemaphore.wait(100);
						if (mByteReceivedBack == true) {
							// Byte has been received
							mIncoming++;
						} else {
							// Timeout
							mLost++;
						}
						runOnUiThread(new Runnable() {
							public void run() {
								mTextViewOutgoing.setText(mOutgoing.toString());
								mTextViewLost.setText(mLost.toString());
								mTextViewIncoming.setText(mIncoming.toString());
								mTextViewCorrupted.setText(mCorrupted.toString());
							}
						});
					} catch (InterruptedException e) {
					}
				}
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loopback);
		mTextViewOutgoing = (TextView) findViewById(R.id.TextViewOutgoingValue);
		mTextViewIncoming = (TextView) findViewById(R.id.TextViewIncomingValue);
		mTextViewLost = (TextView) findViewById(R.id.textViewLostValue);
		mTextViewCorrupted = (TextView) findViewById(R.id.textViewCorruptedValue);
		if (mSerialPort != null) {
			mSendingThread = new SendingThread();
			mSendingThread.start();
		}
		
		Button pass = (Button) findViewById(R.id.pass);
		pass.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				pass();
			}
		});

		Button fail = (Button) findViewById(R.id.fail);
		fail.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				fail(null);
			}
		});
	}

	@Override
	protected void onDataReceived(byte[] buffer, int size) {

		synchronized (mByteReceivedBackSemaphore) {
			int i;
			for (i = 0; i < size; i++) {
				if ((buffer[i] == mValueToSend) && (mByteReceivedBack == false)) {
					// This byte was expected
					// Wake-up the sending thread
					mByteReceivedBack = true;
					mByteReceivedBackSemaphore.notify();
				} else {
					// The byte was not expected
					mCorrupted++;
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		if (mSendingThread != null)
			mSendingThread.interrupt();
		super.onDestroy();
	}
	
	
	void fail(Object msg) {
		setResult(RESULT_CANCELED);
		resultString = Utils.RESULT_FAIL;
		sendFlag = true;
		finish();
	}

	void pass() {
		setResult(RESULT_OK);
		resultString = Utils.RESULT_PASS;
		sendFlag = true;
		finish();
	}
	
}
