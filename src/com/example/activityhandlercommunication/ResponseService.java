package com.example.activityhandlercommunication;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class ResponseService extends Service {
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				String str = (String) msg.obj;
				str = "ServerReply: " + str;
				
				Message reply = Message.obtain();
				reply.obj = str;
				reply.what = 0;
				
				try {
					msg.replyTo.send(reply);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
	};
	
	private Messenger mMessenger;
	

	@Override
	public void onCreate() {
		super.onCreate();
		
		mMessenger = new Messenger(mHandler);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mMessenger.getBinder();
	}

}
