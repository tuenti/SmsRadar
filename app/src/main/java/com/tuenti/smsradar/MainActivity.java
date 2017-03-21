/*
 * Copyright (c) Tuenti Technologies S.L. All rights reserved.
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
package com.tuenti.smsradar;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * Sample activity created to show how to use SmsRadar library.
 *
 * @author Pedro Vicente Gómez Sánchez <pgomez@tuenti.com>
 * @author Manuel Peinado <mpeinado@tuenti.com>
 */
public class MainActivity extends Activity {

	private Button startService;
	private Button stopService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mapGui();
		hookListeners();
	}

	private void mapGui() {
		startService = (Button) findViewById(R.id.bt_start_service);
		stopService = (Button) findViewById(R.id.bt_stop_service);
	}

	private void hookListeners() {
		startService.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				initializeSmsRadarService();
			}
		});

		stopService.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				stopSmsRadarService();
			}
		});
	}

	private void initializeSmsRadarService() {
		SmsRadar.initializeSmsRadarService(this, new SmsListener() {
			@Override
			public void onSmsSent(Sms sms) {
				showSmsToast(sms);
			}

			@Override
			public void onSmsReceived(Sms sms) {
				showSmsToast(sms);
			}
		});
	}

	private void stopSmsRadarService() {
		SmsRadar.stopSmsRadarService(this);
	}

	private void showSmsToast(Sms sms) {
		Toast.makeText(this, sms.toString(), Toast.LENGTH_LONG).show();

	}

}
