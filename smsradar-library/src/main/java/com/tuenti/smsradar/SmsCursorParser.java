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

import android.database.Cursor;


/**
 * Works as cursor parser to get sms info from a cursor obtained from sms inbox/sent content provider.
 * <p/>
 * This entity will be called from SmsObserver with a cursor created over sms inbox or sms sent content provider to
 * extract the sms information and return an Sms object with the most important info we can get from sms content
 * provider.
 * <p/>
 * This entity can't be stateless because the SmsObserver it's called more than one time when the sms
 * content provider receive a incoming or outgoing sms. SmsCursorParser keep a reference of the last sms id parsed
 * and use it to parse only the correct incoming or outgoing sms. This implementation is based on a
 * lastSmsIdProcessed var that is updated each time an sms it's parsed.
 *
 * @author Pedro Vcente Gómez Sánchez <pgomez@tuenti.com>
 * @author Manuel Peinado <mpeinado@tuenti.com>
 */
class SmsCursorParser {

	private static final String ADDRESS_COLUMN_NAME = "address";
	private static final String DATE_COLUMN_NAME = "date";
	private static final String BODY_COLUMN_NAME = "body";
	private static final String TYPE_COLUMN_NAME = "type";
	private static final String ID_COLUMN_NAME = "_id";
	private static final int SMS_MAX_AGE_MILLIS = 5000;

	private SmsStorage smsStorage;
	private TimeProvider timeProvider;

	SmsCursorParser(SmsStorage smsStorage, TimeProvider timeProvider) {
		this.smsStorage = smsStorage;
		this.timeProvider = timeProvider;
	}

	Sms parse(Cursor cursor) {

		if (!canHandleCursor(cursor) || !cursor.moveToNext()) {
			return null;
		}

		Sms smsParsed = extractSmsInfoFromCursor(cursor);

		int smsId = cursor.getInt(cursor.getColumnIndex(ID_COLUMN_NAME));
		String date = cursor.getString(cursor.getColumnIndex(DATE_COLUMN_NAME));
		Date smsDate = new Date(Long.parseLong(date));

		if (shouldParseSms(smsId, smsDate)) {
			updateLastSmsParsed(smsId);
		} else {
			smsParsed = null;
		}

		return smsParsed;
	}

	private void updateLastSmsParsed(int smsId) {
		smsStorage.updateLastSmsIntercepted(smsId);
	}

	private boolean shouldParseSms(int smsId, Date smsDate) {
		boolean isFirstSmsParsed = isFirstSmsParsed();
		boolean isOld = isOld(smsDate);
		boolean shouldParseId = shouldParseSmsId(smsId);
		return (isFirstSmsParsed && !isOld) || (!isFirstSmsParsed && shouldParseId);
	}

	private boolean isOld(Date smsDate) {
		Date now = timeProvider.getDate();
		return now.getTime() - smsDate.getTime() > SMS_MAX_AGE_MILLIS;
	}

	private boolean shouldParseSmsId(int smsId) {
		if (smsStorage.isFirstSmsIntercepted()) {
			return false;
		}
		int lastSmsIdIntercepted = smsStorage.getLastSmsIntercepted();
		return smsId > lastSmsIdIntercepted;
	}

	private boolean isFirstSmsParsed() {
		return smsStorage.isFirstSmsIntercepted();
	}

	private Sms extractSmsInfoFromCursor(Cursor cursor) {
		String address = cursor.getString(cursor.getColumnIndex(ADDRESS_COLUMN_NAME));
		String date = cursor.getString(cursor.getColumnIndex(DATE_COLUMN_NAME));
		String msg = cursor.getString(cursor.getColumnIndex(BODY_COLUMN_NAME));
		String type = cursor.getString(cursor.getColumnIndex(TYPE_COLUMN_NAME));

		return new Sms(address, date, msg, SmsType.fromValue(Integer.parseInt(type)));
	}

	private boolean canHandleCursor(Cursor cursor) {
		return cursor != null && cursor.getCount() > 0;
	}

}
