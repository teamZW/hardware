package com.hardware.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import java.util.List;

public class ConnectAp {
	private WifiManager wifi_service;
	private static final String TAG = "ConnectAp";

	public ConnectAp(Context context) {
		wifi_service = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	}

	public WifiManager getWifiManager() {
		return wifi_service;
	}

	public boolean ConnectionEllE() {
//		LogTools.e("info", "ConnectionEllE");
		WifiInfo wifiInfo = wifi_service.getConnectionInfo();
		if (isAPFindEllE() != null&&!wifiInfo.getSSID().contains("EllE")) {
			connect(isAPFindEllE(), "12345678");
			return true;
		}
		return false;
	}

	public void ConnectionHot(String ssid, String password, WifiCipherType type) {
		Thread thread = new Thread(new ConnectRunnable(ssid, password, type));
		thread.start();
	}

	/**
	 * 查找ap ELLE
	 * 
	 * @return
	 */
	public String isAPFindEllE() {
//		LogTools.e("info", "isAPFindEllE");
		WifiInfo wifiInfo = wifi_service.getConnectionInfo();// 当前wifi连接信息
		List<ScanResult> scanResults = wifi_service.getScanResults();// 搜索到的设备列表
		String ssid = null;
		int level = 0;
//		LogTools.e("info",scanResults.size()+""+wifiInfo.getSSID());
		for (ScanResult scanResult : scanResults) {
//			LogTools.e("info",scanResult.SSID);
			if (scanResult.SSID.contains("EllE.")) {
				if (ssid != null && level != 0) {
					if (scanResult.level > level) {
						ssid = scanResult.SSID;
						level = scanResult.level;
					}
				} else {
					ssid = scanResult.SSID;
					level = scanResult.level;
				}
//				LogTools.e("info", ssid);
			}
		}
		return ssid;
	}

	class ConnectRunnable implements Runnable {

		private String ssid;
		private WifiCipherType type;
		private String password;

		public ConnectRunnable(String ssid, String password) {
			this.ssid = ssid;
			this.password = password;
			this.type = WifiCipherType.WIFICIPHER_WPA;
		}

		public ConnectRunnable(String ssid, String password, WifiCipherType type) {
			this.ssid = ssid;
			this.password = password;
			this.type = type;
		}

		@Override
		public void run() {
			// 打开wifi
			openWifi();
			// 开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
			// 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
			while (wifi_service.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
				try {
					// 为了避免程序一直while循环，让它睡个100毫秒检测……
					Thread.sleep(100);
				} catch (InterruptedException ie) {
				}
			}

			WifiConfiguration wifiConfig = createWifiInfo(ssid, password, type);
			//
			if (wifiConfig == null) {
//				LogTools.d(TAG, "wifiConfig is null!");
				return;
			}

			WifiConfiguration tempConfig = isExsits(ssid);

			if (tempConfig != null) {
				wifi_service.removeNetwork(tempConfig.networkId);
			}

			int netID = wifi_service.addNetwork(wifiConfig);
			boolean enabled = wifi_service.enableNetwork(netID, true);
//			LogTools.d(TAG, "enableNetwork status enable=" + enabled);
			boolean connected = wifi_service.reconnect();
//			LogTools.d(TAG, "enableNetwork connected=" + connected);
		}
	}

	// 打开WIFI
	public void openWifi() {
		if (!wifi_service.isWifiEnabled()) {
			wifi_service.setWifiEnabled(true);
		}
	}

	private WifiConfiguration isExsits(String SSID) {
		List<WifiConfiguration> existingConfigs = wifi_service.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existingConfigs) {
			if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
				return existingConfig;
			}
		}
		return null;
	}

	private WifiConfiguration createWifiInfo(String SSID, String Password, WifiCipherType Type) {
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";
		// nopass
		if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
			config.wepKeys[0] = "";
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		// wep
		if (Type == WifiCipherType.WIFICIPHER_WEP) {
			if (!TextUtils.isEmpty(Password)) {
				if (isHexWepKey(Password)) {
					config.wepKeys[0] = Password;
				} else {
					config.wepKeys[0] = "\"" + Password + "\"";
				}
			}
			config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		// wpa
		if (Type == WifiCipherType.WIFICIPHER_WPA) {
			config.preSharedKey = "\"" + Password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			// 此处需要修改否则不能自动重联
			// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.status = WifiConfiguration.Status.ENABLED;
		}
		return config;
	}

	public void connect(String ssid, String password) {
		Thread thread = new Thread(new ConnectRunnable(ssid, password));
		thread.start();
	}

	public enum WifiCipherType {
		WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
	}

	private static boolean isHexWepKey(String wepKey) {
		final int len = wepKey.length();

		// WEP-40, WEP-104, and some vendors using 256-bit WEP (WEP-232?)
		if (len != 10 && len != 26 && len != 58) {
			return false;
		}

		return isHex(wepKey);
	}

	private static boolean isHex(String key) {
		for (int i = key.length() - 1; i >= 0; i--) {
			final char c = key.charAt(i);
			if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a' && c <= 'f')) {
				return false;
			}
		}

		return true;
	}

	public String getCipherType(Context context, String ssid) {
		List<ScanResult> list = wifi_service.getScanResults();
		for (ScanResult scResult : list) {
//			LogTools.d("getCipherType",scResult.SSID + ",,,," + ssid);
			if (!TextUtils.isEmpty(scResult.SSID) && scResult.SSID.equals(ssid)) {
				String capabilities = scResult.capabilities;
//				LogTools.i("hefeng", "capabilities=" + capabilities);
				if (!TextUtils.isEmpty(capabilities)) {

					if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
						return "wpa";
					} else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
						return "wep";
					} else {
						return "no";
					}
				}
			}
		}
		return "wpa";
	}

}
