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

import android.content.Context;
import android.content.Intent;

/**
 * Main library class. This class has to be used to initialize or stop the sms interceptor service.
 *
 * @author Pedro Vcente Gómez Sánchez <pgomez@tuenti.com>
 * @author Manuel Peinado <mpeinado@tuenti.com>
 */
public class SmsRadar {

	static SmsListener smsListener;


	/**
	 * Starts the service and store the listener to be notified when a new incoming or outgoing sms be processed
	 * inside the SMS content provider
	 *
	 * @param context used to start the service
	 * @param smsListener to notify when the sms content provider gets a new sms
	 */
	public static void initializeSmsRadarService(Context context, SmsListener smsListener) {
		SmsRadar.smsListener = smsListener;
		Intent intent = new Intent(context, SmsRadarService.class);
		context.startService(intent);
	}

	/**
	 * Stops the service and remove the SmsListener added when the SmsRadar was initialized
	 *
	 * @param context used to stop the service
	 */
	public static void stopSmsRadarService(Context context) {
		SmsRadar.smsListener = null;
		Intent intent = new Intent(context, SmsRadarService.class);
		context.stopService(intent);
	}
}
