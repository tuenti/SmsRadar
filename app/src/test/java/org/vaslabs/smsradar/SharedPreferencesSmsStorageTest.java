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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import android.content.Context;
import android.content.SharedPreferences;


/**
 * Test created to check the correctness of SharedPreferencesSmsStorage.
 *
 * @author Pedro Vicente Gómez Sánchez <pgomez@tuenti.com>
 * @author Manuel Peinado <mpeinado@tuenti.com>
 */
@RunWith(RobolectricTestRunner.class)
public class SharedPreferencesSmsStorageTest {

	private static final String SHARED_PREFERENCES_PATH = "path";
	private static final int DEFAULT_VALUE = -1;
	private static final int ANY_SMS_ID = 1;

	private SharedPreferencesSmsStorage smsStorage;
	private SharedPreferences sharedPreferences;

	@Before
	public void setUp() {
		initializeSmsStorage();
	}

	@After
	public void tearDown() {
		sharedPreferences.edit().clear().commit();
	}

	@Test
	public void shouldReturnDefaultValueIfHadNotBeenEditedPreviously() {
		assertEquals(DEFAULT_VALUE, smsStorage.getLastSmsIntercepted());
	}

	@Test
	public void shouldUpdateLastSmsInterceptedId() {
		smsStorage.updateLastSmsIntercepted(ANY_SMS_ID);

		assertEquals(ANY_SMS_ID, smsStorage.getLastSmsIntercepted());
	}

	@Test
	public void shouldReturnTrueIfIsTheFirstSmsIntercepted() {
		assertTrue(smsStorage.isFirstSmsIntercepted());
	}

	private void initializeSmsStorage() {
		sharedPreferences = Robolectric.application.getSharedPreferences(SHARED_PREFERENCES_PATH,
				Context.MODE_PRIVATE);
		smsStorage = new SharedPreferencesSmsStorage(sharedPreferences);
	}

}
