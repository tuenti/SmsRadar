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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;


/**
 * Test created to check the correctness of SmsObserver
 *
 * @author Pedro Vcente Gómez Sánchez <pgomez@tuenti.com>
 * @author Manuel Peinado <mpeinado@tuenti.com>
 */
@RunWith(RobolectricTestRunner.class)
public class SmsObserverTest {

	private static final boolean ANY_SELF_CHANGE_VALUE = true;

	private SmsObserver smsObserver;

	@Mock
	private ContentResolver mockedContentResolver;
	@Mock
	private Handler mockedHandler;
	@Mock
	private SmsCursorParser mockedSmsCursorParser;

	@Mock
	private Cursor mockedCursor;
	@Mock
	private Sms mockedSms;
	@Mock
	private SmsListener mockedSmsListener;

	@Before
	public void setUp() {
		initializeMocks();
		initializeSmsRadar();
		initializeSmsObserver();
	}

	@Test
	public void shuouldNotNotifySmsListenerIfSmsContentProviderCursorIsNotAvailable() {
		smsObserver.onChange(ANY_SELF_CHANGE_VALUE);

		verify(mockedSmsListener, never()).onSmsReceived(any(Sms.class));
		verify(mockedSmsListener, never()).onSmsSent(any(Sms.class));
	}

	@Test
	public void shouldNotifySmsListenerWithSmsReceived() {
		stubContentResolverWithMockCursor();
		mockCursorToReturnNullProtocol();
		when(mockedSmsCursorParser.parse(any(Cursor.class))).thenReturn(mockedSms);
		when(mockedSms.getType()).thenReturn(SmsType.RECEIVED);

		smsObserver.onChange(ANY_SELF_CHANGE_VALUE);

		verify(mockedSmsListener).onSmsReceived(mockedSms);
	}

	@Test
	public void shouldNotifySmsListenerWithSmsSent() {
		stubContentResolverWithMockCursor();
		mockCursorToReturnNullProtocol();
		when(mockedSmsCursorParser.parse(any(Cursor.class))).thenReturn(mockedSms);
		when(mockedSms.getType()).thenReturn(SmsType.SENT);

		smsObserver.onChange(ANY_SELF_CHANGE_VALUE);

		verify(mockedSmsListener).onSmsSent(mockedSms);
	}

	@Test
	public void shouldCloseBothCursors() {
		stubContentResolverWithMockCursor();
		mockCursorToReturnNullProtocol();

		smsObserver.onChange(ANY_SELF_CHANGE_VALUE);

		verify(mockedCursor, times(2)).close();
	}

	private void initializeMocks() {
		MockitoAnnotations.initMocks(this);
	}

	private void initializeSmsRadar() {
		SmsRadar.smsListener = mockedSmsListener;
	}

	private void initializeSmsObserver() {
		smsObserver = new SmsObserver(mockedContentResolver, mockedHandler, mockedSmsCursorParser);
	}

	private void mockCursorToReturnNullProtocol() {
		String protocolValue = null;
		mockCursorToReturnProtocol(protocolValue);
	}

	private void mockCursorToReturnProtocol(String protocolValue) {
		int protocolColumnIndex = 0;
		when(mockedCursor.getColumnIndex("protocol")).thenReturn(protocolColumnIndex);
		when(mockedCursor.getString(protocolColumnIndex)).thenReturn(protocolValue);
		when(mockedCursor.moveToFirst()).thenReturn(true);
	}

	private void stubContentResolverWithMockCursor() {
		when(mockedContentResolver.query(any(Uri.class), any(String[].class), anyString(), any(String[].class),
				anyString())).thenReturn(mockedCursor);
	}
}
