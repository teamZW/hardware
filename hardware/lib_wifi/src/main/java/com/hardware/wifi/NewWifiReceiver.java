package com.hardware.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;

import com.hardware.wifi.able.WifiNotifyCallBack;

/**
 * Wifi广播监听（控制整个项目）
 */
public class NewWifiReceiver extends BroadcastReceiver {
	/** wifi消息发送 */
	private Handler wifiNewsSend;
	/** 静态Handler(用于发布全局) */
	public WifiNotifyCallBack wifiCallBack = null;
	// ============== 常量 =================
	/** startscan() 扫描附近wifi结束触发 */
	public static final int WIFI_SCAN_FINISH = 101;
	/** 已连接的wifi强度发生变化 */
	public static final int WIFI_RSSI_CHANGED = 102;
	/** wifi认证错误(密码错误等) */
	public static final int WIFI_ERROR_AUTHENTICATING = 103;
	/** 连接错误（其他错误） */
	public static final int WIFI_ERROR_UNKNOWN = 104;
	/** wifi已打开 */
	public static final int WIFI_STATE_ENABLED = 105;
	/** wifi正在打开 */
	public static final int WIFI_STATE_ENABLING = 106;
	/** wifi已关闭 */
	public static final int WIFI_STATE_DISABLED = 107;
	/** wifi正在关闭 */
	public static final int WIFI_STATE_DISABLING = 108;
	/** wifi状态未知 */
	public static final int WIFI_STATE_UNKNOWN = 109;
	/** wifi连接成功 */
	public static final int CONNECTED = 110;
	/** wifi连接中 */
	public static final int CONNECTING = 111;
	/** wifi连接失败,断开 */
	public static final int DISCONNECTED = 112;
	/** wifi暂停、延迟 */
	public static final int SUSPENDED = 113;
	/** wifi未知 */
	public static final int UNKNOWN = 114;
	// ====================================================
	/** wifi列表刷新 */
	public static final int REF_WIFI_LIST = 1000;
	/** 连接wifi超时 */
	public static final int CONN_WIFI_OUTTIME = 1001;

	/*
	供全局监听使用
	 */
	public NewWifiReceiver() {
		super();
	}

	/*
	供AP配置使用
	 */
	public NewWifiReceiver(Handler wifiNewsSend) {
		super();
		this.wifiNewsSend = wifiNewsSend;
	}

	/**
	 * 设置全局WifiNotifyCallBack
	 */
	public void setWifiCallBack(WifiNotifyCallBack wifiCallBack){
		this.wifiCallBack = wifiCallBack;
	}
	
	/**
	 * 设置全局回调为null
	 */
	public void releaseWifiCallBack(){
		this.wifiCallBack = null;
	}

	@Override
	public void onReceive(Context mContext, Intent intent) {
		try {
			/*刷新列表*/
			if(wifiNewsSend!=null)wifiNewsSend.sendEmptyMessage(REF_WIFI_LIST);
			//sendGlobalMsg(REF_WIFI_LIST);

			/*获取触发的广播*/
			String iAction = intent.getAction();

			/** 当调用WifiManager的startscan() 方法，扫描结束后，系统会发出改Action广播 */
			if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(iAction)) {
				if(wifiNewsSend!=null)wifiNewsSend.sendEmptyMessage(WIFI_SCAN_FINISH);
				//sendGlobalMsg(WIFI_SCAN_FINISH);
			}
			
			/** 当前连接的Wifi强度发生变化触发 */
			if (WifiManager.RSSI_CHANGED_ACTION.equals(iAction)) {
				if(wifiNewsSend!=null)wifiNewsSend.sendEmptyMessage(WIFI_RSSI_CHANGED);
				//sendGlobalMsg(WIFI_RSSI_CHANGED);
			}

			/** 发送WIFI连接的过程信息，如果出错ERROR信息才会收到。连接WIFI时触发，触发多次。 */
			if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(iAction)) {
				/** 出现错误状态,则获取错误状态 */
				int wifiErrorCode = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 0);
				/** 获取错误状态 */
				switch(wifiErrorCode){
					case WifiManager.ERROR_AUTHENTICATING: // 认证错误，如密码错误等
						if(wifiNewsSend!=null)wifiNewsSend.sendEmptyMessage(WIFI_ERROR_AUTHENTICATING);
						if(wifiCallBack != null){
							Message wifiMsg = new Message();
							wifiMsg.what = WIFI_ERROR_AUTHENTICATING;
							sendGlobalMsg(wifiMsg);
						}
						break;
					default: // 连接错误（其他错误） -- 暂时不加上去，因为其他状态未知
						// wifiNewsSend.sendEmptyMessage(WIFI_ERROR_UNKNOWN);
						break;
				}
			}
			
			/** 监听wifi的打开与关闭等状态，与wifi的连接无关 */
			if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(iAction)) {
				/** 获取wifi状态 */
				int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
				switch (wifiState) {
				case WifiManager.WIFI_STATE_ENABLED: // 已打开
					if(wifiNewsSend!=null)wifiNewsSend.sendEmptyMessage(WIFI_STATE_ENABLED);
					if(wifiCallBack != null){
						Message wifiMsg = new Message();
						wifiMsg.what = WifiManager.WIFI_STATE_ENABLED;
						sendGlobalMsg(wifiMsg);
					}
					break;
				case WifiManager.WIFI_STATE_ENABLING: // 正在打开
					if(wifiNewsSend!=null)wifiNewsSend.sendEmptyMessage(WIFI_STATE_ENABLING);
					if(wifiCallBack != null){
						Message wifiMsg = new Message();
						wifiMsg.what = WifiManager.WIFI_STATE_ENABLING;
						sendGlobalMsg(wifiMsg);
					}
					break;
				case WifiManager.WIFI_STATE_DISABLED: // 已关闭
					if(wifiNewsSend!=null)wifiNewsSend.sendEmptyMessage(WIFI_STATE_DISABLED);
					if(wifiCallBack != null){
						Message wifiMsg = new Message();
						wifiMsg.what = WifiManager.WIFI_STATE_DISABLED;
						sendGlobalMsg(wifiMsg);
					}
					break;
				case WifiManager.WIFI_STATE_DISABLING: // 正在关闭
					if(wifiNewsSend!=null)wifiNewsSend.sendEmptyMessage(WIFI_STATE_DISABLING);
					if(wifiCallBack != null){
						Message wifiMsg = new Message();
						wifiMsg.what = WifiManager.WIFI_STATE_DISABLING;
						sendGlobalMsg(wifiMsg);
					}
					break;
				case WifiManager.WIFI_STATE_UNKNOWN: // 未知
					if(wifiNewsSend!=null)wifiNewsSend.sendEmptyMessage(WIFI_STATE_UNKNOWN);
					if(wifiCallBack != null){
						Message wifiMsg = new Message();
						wifiMsg.what = WifiManager.WIFI_STATE_UNKNOWN;
						sendGlobalMsg(wifiMsg);
					}
					break;
				}
			}
			
			/** wifi在连接过程的状态返回 */
			if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(iAction)) {
				/** 获取连接的信息(序列化) */
				Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if (null != parcelableExtra) {
					/** 获取连接信息 急对象 */
					NetworkInfo nwInfo = (NetworkInfo) parcelableExtra;
					/** 获取连接的状态 */
					State wifiState = nwInfo.getState();
					/** 当前连接的ssid */
					String cSSID = WifiUtils.getSSID(mContext);
					// 通知的消息
					Message msg = new Message();
					// wifi ssid
					msg.obj = cSSID;
					// 判断连接状态
					switch(wifiState){
						case CONNECTED: // 连接成功
							msg.what = CONNECTED;
							break;
						case CONNECTING: // 连接中
							msg.what = CONNECTING;
							break;
						case DISCONNECTED: // 连接失败,断开
							msg.what = DISCONNECTED;
							break;
						case SUSPENDED: // 暂停、延迟
							msg.what = SUSPENDED;
							break;
						case UNKNOWN:  // 未知
							msg.what = UNKNOWN;
							break;
					}
					// 发送消息
					if(wifiNewsSend!=null)wifiNewsSend.sendMessage(msg);
					if(wifiCallBack != null){
						Message wifiMsg = new Message();
						wifiMsg.obj = cSSID;
						wifiMsg.what = msg.what;
						sendGlobalMsg(wifiMsg);
					}
				}
			}

			/** 判断是否WIFI打开了，变化触发一次 */
			if(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION.equals(iAction)){
				boolean isWifiState = intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED,false);
				if(isWifiState){
					// 表示wifi已打开
					//wifiNewsSend.sendEmptyMessage(WIFI_STATE_ENABLED);
				} else {
					// 断开连接设备  Wifi此时状态为disabling
					// wifiNewsSend.sendEmptyMessage(WIFI_STATE_DISABLING);
				}
			}
			
			/** 当网络连接状态改变时通知应用程序 */
			if (ConnectivityManager.CONNECTIVITY_ACTION.equals(iAction)) {
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 发送全局消息
	 * @param msg 消息
	 */
	public void sendGlobalMsg(Message msg){
		if(wifiCallBack != null){
			wifiCallBack.notifyWifiStateChange(0, msg);
		}
	}

	/*
	 * 模拟处理what ，可以直接copy
	 * @param what
	 */
	protected void checkWhat(int what){
		switch(what){
			/** startscan() 扫描附近wifi结束触发 */
			case NewWifiReceiver.WIFI_SCAN_FINISH:
				break;
			/** 已连接的wifi强度发生变化 */
			case NewWifiReceiver.WIFI_RSSI_CHANGED:
				break;
			/** wifi认证错误(密码错误等) */
			case NewWifiReceiver.WIFI_ERROR_AUTHENTICATING:
				break;
			/** 连接错误（其他错误） */
			case NewWifiReceiver.WIFI_ERROR_UNKNOWN:
				break;
			/** wifi已打开 */
			case NewWifiReceiver.WIFI_STATE_ENABLED:
				break;
			/** wifi正在打开 */
			case NewWifiReceiver.WIFI_STATE_ENABLING:
				break;
			/** wifi已关闭 */
			case NewWifiReceiver.WIFI_STATE_DISABLED:
				break;
			/** wifi正在关闭 */
			case NewWifiReceiver.WIFI_STATE_DISABLING:
				break;
			/** wifi状态未知 */
			case NewWifiReceiver.WIFI_STATE_UNKNOWN:
				break;
			/** wifi连接成功 */
			case NewWifiReceiver.CONNECTED:
				break;
			/** wifi连接中 */
			case NewWifiReceiver.CONNECTING:
				break;
			/** wifi连接失败,断开 */
			case NewWifiReceiver.DISCONNECTED:
				break;
			/** wifi暂停、延迟 */
			case NewWifiReceiver.SUSPENDED:
				break;
			/** wifi未知 */
			case NewWifiReceiver.UNKNOWN:
				break;
		}
	}
}
