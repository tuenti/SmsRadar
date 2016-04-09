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
import org.robolectric.RobolectricTestRunner;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import android.database.Cursor;

/**
 * Test created to check the correctness of SmsCursorParser.
 *
 * @author Pedro Vicente Gómez Sánchez <pgomez@tuenti.com>
 * @author Manuel Peinado <mpeinado@tuenti.com>
 */
@RunWith(RobolectricTestRunner.class)
public class SmsCursorParserTest {

	private static final String ANY_ADDRESS = "address";
	private static final String OLD_DATE = "1";
	private static final String NEW_DATE = "123456789";
	private static final String ANY_BODY = "body";
	private static final String TYPE_RECEIVED = "1";
	private static final String ADDRESS_COLUMN_NAME = "address";
	private static final String DATE_COLUMN_NAME = "date";
	private static final String BODY_COLUMN_NAME = "body";
	private static final String TYPE_COLUMN_NAME = "type";
	private static final String ID_COLUMN_NAME = "_id";
	private static final int DEFAULT_ID = -1;
	private static final int SMS_ID_ONE = 1;
	private static final int SMS_ID_ZERO = 0;

	private SmsCursorParser smsCursorParser;

	private SmsStorage mockedSmsStorage;
	private TimeProvider mockedTimeProvider;

	@Before
	public void setUp() {
		initTimeProvider();
		initSmsStorage();
		initSmsCursorParser();
	}

	@Test
	public void shouldNotParseAnySmsWithANullCursor() {
		assertNull(smsCursorParser.parse(null));
	}

	@Test
	public void shouldParseFirstSmsIfTheFirstTime() {
		Cursor mockedCursor = mockCursorToReturn(ANY_ADDRESS, NEW_DATE, ANY_BODY, TYPE_RECEIVED, SMS_ID_ONE);
		mockTimeProviderToReturn(NEW_DATE);
		mockSmsCursorParserToReturnLastSmsIdParsed();

		Sms sms = smsCursorParser.parse(mockedCursor);

		Sms expectedSms = createSms(ANY_ADDRESS, NEW_DATE, ANY_BODY, SmsType.RECEIVED);
		assertEquals(expectedSms, sms);
	}

	@Test
	public void shouldNotParseFirstSmsIfIsOld() {
		Cursor mockedCursor = mockCursorToReturn(ANY_ADDRESS, OLD_DATE, ANY_BODY, TYPE_RECEIVED, SMS_ID_ONE);
		mockTimeProviderToReturn(NEW_DATE);
		mockSmsCursorParserToReturnLastSmsIdParsed();

		Sms sms = smsCursorParser.parse(mockedCursor);

		assertNull(sms);
	}

	@Test
	public void shouldParseTheNewSms() {
		Cursor mockedCursor = mockCursorToReturn(ANY_ADDRESS, NEW_DATE, ANY_BODY, TYPE_RECEIVED, SMS_ID_ONE);
		mockTimeProviderToReturn(NEW_DATE);
		mockSmsCursorParserToReturnLastSmsIdParsed(0);

		Sms sms = smsCursorParser.parse(mockedCursor);

		Sms expectedSms = createSms(ANY_ADDRESS, NEW_DATE, ANY_BODY, SmsType.RECEIVED);
		assertEquals(expectedSms, sms);
	}

	@Test
	public void shouldNotParseTheOldSms() {
		Cursor mockedCursor = mockCursorToReturn(ANY_ADDRESS, NEW_DATE, ANY_BODY, TYPE_RECEIVED, SMS_ID_ZERO);
		mockTimeProviderToReturn(NEW_DATE);
		mockSmsCursorParserToReturnLastSmsIdParsed(1);

		Sms sms = smsCursorParser.parse(mockedCursor);

		assertNull(sms);
	}

	private void initTimeProvider() {
		mockedTimeProvider = mock(TimeProvider.class);
	}

	private void initSmsStorage() {
		mockedSmsStorage = mock(SmsStorage.class);
	}

	private void initSmsCursorParser() {
		smsCursorParser = new SmsCursorParser(mockedSmsStorage, mockedTimeProvider);
	}

	private Sms createSms(String address, String date, String body, SmsType type) {
		return new Sms(address, date, body, type);
	}

	private void mockSmsCursorParserToReturnLastSmsIdParsed() {
		mockSmsCursorParserToReturnLastSmsIdParsed(DEFAULT_ID);
	}

	private void mockSmsCursorParserToReturnLastSmsIdParsed(int id) {
		if (id == DEFAULT_ID) {
			when(mockedSmsStorage.isFirstSmsIntercepted()).thenReturn(true);
		} else {
			when(mockedSmsStorage.isFirstSmsIntercepted()).thenReturn(false);
			when(mockedSmsStorage.getLastSmsIntercepted()).thenReturn(id);
		}

	}

	private Cursor mockCursorToReturn(String address, String date, String body, String type, int id) {
		Cursor mockedCursor = mock(Cursor.class);
		when(mockedCursor.getCount()).thenReturn(1);
		when(mockedCursor.moveToNext()).thenReturn(true);
		when(mockedCursor.getColumnIndex(ADDRESS_COLUMN_NAME)).thenReturn(0);
		when(mockedCursor.getString(0)).thenReturn(address);

		when(mockedCursor.getColumnIndex(DATE_COLUMN_NAME)).thenReturn(1);
		when(mockedCursor.getString(1)).thenReturn(date);

		when(mockedCursor.getColumnIndex(BODY_COLUMN_NAME)).thenReturn(2);
		when(mockedCursor.getString(2)).thenReturn(body);

		when(mockedCursor.getColumnIndex(TYPE_COLUMN_NAME)).thenReturn(3);
		when(mockedCursor.getString(3)).thenReturn(type);

		when(mockedCursor.getColumnIndex(ID_COLUMN_NAME)).thenReturn(4);
		when(mockedCursor.getInt(4)).thenReturn(id);

		return mockedCursor;
	}

	private void mockTimeProviderToReturn(String date) {
		Date newDate = new Date(Long.parseLong(date));
		when(mockedTimeProvider.getDate()).thenReturn(newDate);

	}
}
