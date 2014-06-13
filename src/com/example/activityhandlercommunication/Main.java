package com.example.activityhandlercommunication;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity {

	private Messenger mServiceMessenger;
	private Messenger mActivityMessenger;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String str = (String) msg.obj;
			mResponseText.setText(str);
		}
	};

	private TextView mResponseText;

	private EditText mInput;

	private Button mSendButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mActivityMessenger = new Messenger(mHandler);

		setContentView(R.layout.main);

		mResponseText = (TextView) findViewById(R.id.response);

		mInput = (EditText) findViewById(R.id.input);

		mSendButton = (Button) findViewById(R.id.btn_send);
		mSendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mServiceMessenger == null) {
					return;
				}

				String text = mInput.getText().toString();
				if (TextUtils.isEmpty(text)) {
					return;
				}
				mInput.setText("");
				Message send = Message.obtain();
				send.replyTo = mActivityMessenger;
				send.what = 0;
				send.obj = text;

				try {
					mServiceMessenger.send(send);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});

		Intent intent = new Intent(this, ResponseService.class);
		bindService(intent, mConn, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			unbindService(mConn);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ServiceConnection mConn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName c) {

		}

		@Override
		public void onServiceConnected(ComponentName c, IBinder binder) {
			Toast.makeText(Main.this, "Service Binded", Toast.LENGTH_LONG).show();
			mServiceMessenger = new Messenger(binder);
		}
	};

}
