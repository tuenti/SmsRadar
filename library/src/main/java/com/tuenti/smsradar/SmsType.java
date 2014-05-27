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

/**
 * Represents the SmsType.
 * <p/>
 * RECEIVED SmsType is the equivalent to MT in a telco terminology.
 * SENT SmsType is the equivalent to MO in a telco terminology.
 * <p/>
 * Review GSM short message service to get more information: http://en.wikipedia.org/wiki/Short_Message_Service
 *
 * @author Pedro Vcente Gómez Sánchez <pgomez@tuenti.com>
 * @author Manuel Peinado <mpeinado@tuenti.com>
 */
public enum SmsType {

	UNKNOWN(-1),
	RECEIVED(1),
	SENT(2),;

	private final int value;

	private SmsType(int value) {
		this.value = value;
	}

	/**
	 * Create a new SmsType using the sms type value represented with integers in the Sms content provider.
	 *
	 * @param value used to translate into SmsType
	 * @return new SmsType associated to the value passed as parameter
	 */
	public static SmsType fromValue(int value) {
		for (SmsType smsType : values()) {
			if (smsType.value == value) {
				return smsType;
			}
		}
		throw new IllegalArgumentException("Invalid sms type: " + value);
	}

}
