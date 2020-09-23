package com.hardware.wifi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public final class WifiCode {
	private WifiCode() {
		throw new Error("Do not need instantiate!");
	}

	public static final int WIFI_PERMISSION_ERROR = 1;
	public static final int WIFI_LOCATION_ERROR = 2;
	public static final int WIFI_OPEN_ERROR = 3;
	/**
	 * 在线状态
	 */
	public enum ErrorStatus {
		/**
		 * 权限异常
		 */
		permission_error,
		/**
		 * 定位异常
		 */
		location_error,
		/**
		 * wifi异常
		 */
		wifi_error,
		/**
		 * 正常
		 * @return
		 */
		ok;

		@Override
		public String toString() {
			switch (this) {
				case permission_error:
					return "permission error!!!";
				case location_error:
					return "location error!!!";
				case wifi_error:
					return "wifi error!!!";
				case ok:
					return "wifi is ok!!!!!";
				default:
					return "";
			}
		}
	}
}
