package com.hardware.wifi.able;

import com.hardware.wifi.WifiCode;
import com.hardware.wifi.bean.BindSmartBean;

import java.util.ArrayList;

public interface WifiUpdate {
    public void updateWifiBean(ArrayList<BindSmartBean> bean);
    public void updateCurrentState(String ssid,String state);
    public void updateError(WifiCode.ErrorStatus state);
}
