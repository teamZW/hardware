package com.hardware.wifi.able;

import android.os.Message;

/**
 * Wifi通知回调
 */
public interface WifiNotifyCallBack {

	/**
	 * 通知Wifi状态改变
	 * @param what 消息类型
	 * @param msg 消息
	 */
	public void notifyWifiStateChange(int what, Message msg);
}
