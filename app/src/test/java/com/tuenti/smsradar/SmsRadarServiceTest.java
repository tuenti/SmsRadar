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


import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.shadows.ShadowPendingIntent;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;

/**
 * Test created to check the correctness of SmsRadarService
 *
 * @author Pedro Vcente Gómez Sánchez <pgomez@tuenti.com>
 * @author Manuel Peinado <mpeinado@tuenti.com>
 */
@RunWith(RobolectricTestRunner.class)
public class SmsRadarServiceTest {

	private static final Intent ANY_INTENT = new Intent();
	private static final int ANY_FLAG = 0;
	private static final int ANY_START_ID = 0;
	public static final boolean NOTIFY_FOR_DESCENDANTS = true;
	private static final Uri CONTENT_SMS_URI = Uri.parse("content://sms");
	private static final long ANY_TIME = 4L;
	private static final long ONE_SECOND = 1000;

	@Mock
	private ContentResolver mockedContentResolver;
	@Mock
	private SmsObserver mockedSmsObserver;
	@Mock
	private AlarmManager mockedAlarmManager;
	@Mock
	private TimeProvider mockedTimeProvider;

	private SmsRadarService smsRadarService;

	@Before
	public void setUp() {
		initMocks();
		createServiceWithDependencies();
	}

	@Test
	public void shouldRegisterContentResolver() {
		startSmsInterceptorService();

		verify(mockedContentResolver).registerContentObserver(eq(CONTENT_SMS_URI), eq(NOTIFY_FOR_DESCENDANTS),
				eq(mockedSmsObserver));
	}

	@Test
	public void shouldNontegisterSmsObserverTwice() {
		startSmsInterceptorService();
		startSmsInterceptorService();

		verify(mockedContentResolver).registerContentObserver(eq(CONTENT_SMS_URI),
				eq(NOTIFY_FOR_DESCENDANTS),
				eq(mockedSmsObserver));
	}

	@Test
	public void shouldUnregisterContentResolver() {
		startSmsInterceptorService();
		stopSmsInterceptorService();

		verify(mockedContentResolver).unregisterContentObserver(mockedSmsObserver);
	}

	@Test
	public void shouldRestartServiceUsingAlarmManagerWhenTaskRemoved() {
		when(mockedTimeProvider.getDate()).thenReturn(new Date(ANY_TIME));

		startSmsInterceptorService();
		smsRadarService.onTaskRemoved(ANY_INTENT);

		ArgumentCaptor<PendingIntent> pendingIntentArgumentCaptor = ArgumentCaptor.forClass(PendingIntent.class);
		verify(mockedAlarmManager).set(eq(AlarmManager.RTC_WAKEUP), eq(ANY_TIME + ONE_SECOND),
				pendingIntentArgumentCaptor.capture());
		PendingIntent capturedPendingIntent = pendingIntentArgumentCaptor.getValue();
		ShadowPendingIntent pendingIntent = Shadows.shadowOf(capturedPendingIntent);
		ShadowIntent intent = Shadows.shadowOf(pendingIntent.getSavedIntent());
		assertEquals(SmsRadarService.class, intent.getIntentClass());
	}

	private void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	private void createServiceWithDependencies() {
		smsRadarService = new SmsRadarService();
		smsRadarService.setSmsObserver(mockedSmsObserver);
		smsRadarService.setContentResolver(mockedContentResolver);
	}

	private void startSmsInterceptorService() {
		smsRadarService.setContentResolver(mockedContentResolver);
		smsRadarService.setSmsObserver(mockedSmsObserver);
		smsRadarService.setAlarmManager(mockedAlarmManager);
		smsRadarService.setTimeProvider(mockedTimeProvider);
		smsRadarService.onStartCommand(ANY_INTENT, ANY_FLAG, ANY_START_ID);
	}

	private void stopSmsInterceptorService() {
		smsRadarService.setContentResolver(mockedContentResolver);
		smsRadarService.setSmsObserver(mockedSmsObserver);
		smsRadarService.setAlarmManager(mockedAlarmManager);
		smsRadarService.setTimeProvider(mockedTimeProvider);
		smsRadarService.onDestroy();
	}
}
