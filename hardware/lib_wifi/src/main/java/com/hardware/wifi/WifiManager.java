package com.hardware.wifi;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.hardware.wifi.able.WifiNotifyCallBack;
import com.hardware.wifi.able.WifiUpdate;
import com.hardware.wifi.bean.BindSmartBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET;
import static android.net.NetworkCapabilities.TRANSPORT_CELLULAR;

/**
 * Created by dunn on 2018/08/06
 */

public class WifiManager {
    private Activity activity;
    private volatile static WifiManager instance;
    private StringBuffer permissionBuffer = new StringBuffer();
    private WifiUtils mWifiUtils;
    private ConnectAp connectAp;
    private NewWifiReceiver wifiReceiver;
    private WifiNotify MSG;
    private WifiUpdate wifiUpdate;
    private ExecutorService dealExecutorService;  //用于处理任务  数量多，耗时少的线程任务
    private ScheduledExecutorService cmdExecutorService; //用于定时线程任务

    private WifiManager() {

    }
    public static WifiManager L() {
        if (instance == null) {
            synchronized (WifiManager.class) {
                if (instance == null) {
                    instance = new WifiManager();
                }
            }
        }
        return instance;
    }
    /**
     * 扫描WIFI
     * @param mContext
     * @param listen
     */
    public void startScanWiFi(Activity mContext, WifiUpdate listen){
        try {
            activity = mContext;
            wifiUpdate = listen;
            //check
            mWifiUtils = new WifiUtils(mContext);
            int state = checkWifi(mContext);
            if(state==-1){
                if(wifiUpdate!=null){
                    wifiUpdate.updateError(WifiCode.ErrorStatus.location_error);
                }
                return;
            }else if(state==-2){
                if(wifiUpdate!=null){
                    wifiUpdate.updateError(WifiCode.ErrorStatus.wifi_error);
                }
                return;
            }
            int permission = checkPermissionMixLocation(mContext,PermissionTool.LOCATION_AUTO);
            if(permission==1){
                if(wifiUpdate!=null){
                    wifiUpdate.updateError(WifiCode.ErrorStatus.permission_error);
                }
                return;
            }
            if(wifiUpdate!=null){
                wifiUpdate.updateError(WifiCode.ErrorStatus.ok);
            }

            if (wifiReceiver == null) {
                wifiReceiver = new NewWifiReceiver();
            }
            if (MSG == null) {
                MSG = new WifiNotify();
            }
            wifiReceiver.setWifiCallBack(MSG);

            if (dealExecutorService == null || dealExecutorService.isShutdown()) {
                dealExecutorService = Executors.newCachedThreadPool();
            }
            if (cmdExecutorService == null || cmdExecutorService.isShutdown()) {
                cmdExecutorService = Executors.newScheduledThreadPool(2);
            }

            startAction(mContext);

            connectAp = new ConnectAp(mContext);

            startSearchTimer();
        }catch (Exception e){
            e.printStackTrace();
            if(wifiUpdate!=null){
                wifiUpdate.updateError(WifiCode.ErrorStatus.permission_error);
            }
        }
    }
    /**
     * 结束
     */
    public void releaseWiFi(Activity mContext){
        try {
            stopAction(mContext);
            if (wifiReceiver != null) wifiReceiver.releaseWifiCallBack();
            MSG = null;
            wifiReceiver = null;
            wifiUpdate = null;
            if (dealExecutorService != null && !dealExecutorService.isShutdown()) {
                dealExecutorService.shutdown();
            }
            if (cmdExecutorService != null && !cmdExecutorService.isShutdown()) {
                cmdExecutorService.shutdown();
            }
            releaseSearchTimer();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void connectAp(final Context mContext,final String ssid, final String pwd) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                String ssidType = connectAp.getCipherType(mContext, ssid);
                Log.v("wifi[","search ssid="+ssid+", pwd="+pwd+", ssidType="+ssidType);
                if(ssid.startsWith("IPC-") || ssid.startsWith("MC-")){
                    mWifiUtils.quickConnWifi(mContext, ssid, null, WifiUtils.NOPWD);
                }else{
                    if (ssidType.equals("no")) {
                        if(ssid.startsWith("IPC-") || ssid.startsWith("MC-")){
                            mWifiUtils.quickConnWifi(mContext, ssid, null, WifiUtils.NOPWD);
                        } else {
                            mWifiUtils.quickConnWifi(mContext, ssid, pwd, WifiUtils.NOPWD);
                        }
                    } else if (ssidType.equals("wep")) {
                        mWifiUtils.quickConnWifi(mContext, ssid, pwd, WifiUtils.WEP);
                    } else if (ssidType.equals("wpa")) {
                        mWifiUtils.quickConnWifi(mContext, ssid, pwd, WifiUtils.WPA);
                    }
                }
            }
        }.start();
    }

    //-----------------------wifi notify------------------------------------------------------------
    private class WifiNotify implements WifiNotifyCallBack {
        @Override
        public void notifyWifiStateChange(int type, Message msg) {
            if (dealExecutorService != null && !dealExecutorService.isShutdown()){
                dealExecutorService.execute( new dualWiFiMsg(msg) );
            }
        }
    }
    private class dualWiFiMsg implements Runnable {
        private Message msg;

        public dualWiFiMsg(Message m_msg) {
            msg = m_msg;
        }

        @Override
        public void run() {
            try{
                switch(msg.what) {
                    /** wifi认证错误(密码错误等) */
                    case NewWifiReceiver.WIFI_ERROR_AUTHENTICATING:
                        if(wifiUpdate!=null){
                            wifiUpdate.updateCurrentState("","wifi error authenticating");
                        }
                        break;
                    case android.net.wifi.WifiManager.WIFI_STATE_ENABLED:
//                        if(wifiUpdate!=null){
//                            wifiUpdate.updateCurrentState("","wifi state enabled");
//                        }
                        break;
                    case android.net.wifi.WifiManager.WIFI_STATE_ENABLING: // 正在打开
                        if(wifiUpdate!=null){
                            wifiUpdate.updateCurrentState("","wifi state enabling");
                        }
                        break;
                    case android.net.wifi.WifiManager.WIFI_STATE_DISABLED: // 已关闭
                        if(wifiUpdate!=null){
                            wifiUpdate.updateCurrentState("","wifi state disabled");
                        }
                        break;
                    case android.net.wifi.WifiManager.WIFI_STATE_DISABLING: // 正在关闭
                        if(wifiUpdate!=null){
                            wifiUpdate.updateCurrentState("","wifi state disabling");
                        }
                        break;
                    case android.net.wifi.WifiManager.WIFI_STATE_UNKNOWN:
                        if(wifiUpdate!=null){
                            wifiUpdate.updateCurrentState("","wifi state unknown");
                        }
                        break;
                    case NewWifiReceiver.CONNECTING:
                        if(wifiUpdate!=null){
                            wifiUpdate.updateCurrentState(((String) msg.obj),"wifi --->connecting");
                        }
                        break;
                    case NewWifiReceiver.DISCONNECTED:
                        if(wifiUpdate!=null){
                            wifiUpdate.updateCurrentState(((String) msg.obj),"wifi --->disconnected");
                        }
                        break;
                    case NewWifiReceiver.SUSPENDED:
                        if(wifiUpdate!=null){
                            wifiUpdate.updateCurrentState(((String) msg.obj),"wifi --->suspended");
                        }
                        break;
                    case NewWifiReceiver.UNKNOWN:
                        if(wifiUpdate!=null){
                            wifiUpdate.updateCurrentState(((String) msg.obj),"wifi --->unknown");
                        }
                        break;
                    /** wifi连接成功 */
                    case NewWifiReceiver.CONNECTED:
                        if(wifiUpdate!=null){
                            wifiUpdate.updateCurrentState(((String) msg.obj),"wifi --->connected");
                        }
                        break;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    //-----------------------search wifi------------------------------------------------------------
    private ArrayList<BindSmartBean> searchWifiList = new ArrayList<BindSmartBean>();
    private Timer timerSearch =new Timer();
    private SearchApTimerTask mSearchApTimerTask;
    private final int SEARCH_SUM = 60;  //60
    private int SEARCH_COUNT =0;
    private class SearchApTimerTask extends TimerTask {
        @Override
        public void run() {
            SEARCH_COUNT++;

            if (SEARCH_COUNT > SEARCH_SUM) {
                SEARCH_COUNT=0;
            }

            serachWifiList();
        }
    }
    private void startSearchTimer(){
        if (mSearchApTimerTask != null) {
            mSearchApTimerTask.cancel();
            mSearchApTimerTask = null;
        }
        SEARCH_COUNT = 0;
        mSearchApTimerTask = new SearchApTimerTask();
        timerSearch.schedule(mSearchApTimerTask,100,3000);
    }
    private void releaseSearchTimer(){
        if (mSearchApTimerTask != null) {
            mSearchApTimerTask.cancel();
            mSearchApTimerTask = null;
        }

        if(timerSearch!=null){
            timerSearch.cancel();
            timerSearch=null;
        }
    }
    private void serachWifiList(){
        try{
            WifiAdmin wifiAdmin = new WifiAdmin(activity);
            wifiAdmin.scanWifiList();
            List<ScanResult> tmp = wifiAdmin.getWifiList();
            int size = tmp.size();
            Log.v("wifi[","search size="+size);
            clearApMap();
            for (int i = 0; i < size; i++) {
                //if(checkAp(tmp.get(i).SSID)){
                    putApMap(tmp.get(i).SSID);
                //}
            }
            if(wifiUpdate!=null){
                wifiUpdate.updateWifiBean(searchWifiList);
            }
        }catch (SecurityException e){
            e.printStackTrace();
            if(wifiUpdate!=null){
                wifiUpdate.updateError(WifiCode.ErrorStatus.permission_error);
            }
            clearApMap();
        }catch (Exception e){
            e.printStackTrace();
            clearApMap();
        }
    }
    private boolean checkAp(String ssid){
        try {
            if (    ssid.startsWith("IPC") ||
                    ssid.startsWith("@IPC") ||
                    ssid.startsWith("MC") ||
                    ssid.startsWith("@MC") ||
                    ssid.startsWith("DoorBell") ||
                    ssid.startsWith("@DoorBell")) {
                return true;
            } else {
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    private synchronized void clearApMap(){
        if(searchWifiList!=null) searchWifiList.clear();
    }
    private synchronized void putApMap(String ssid){
        if(ssid==null) return;
        if(searchWifiList==null)searchWifiList = new ArrayList<BindSmartBean>();

        if(searchWifiList!=null){
            BindSmartBean bindSmartBean = new BindSmartBean();
            bindSmartBean.setSSID(ssid);
            searchWifiList.add(bindSmartBean);
        }
    }

    //-----------------------other------------------------------------------------------------------
    private void startAction(Context mContext){
        try {
            // 注册监听广播
            IntentFilter filter = new IntentFilter();
            /** 当调用WifiManager的startscan() 方法，扫描结束后，系统会发出改Action广播 */
            filter.addAction(android.net.wifi.WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            /** 当前连接的Wifi强度发生变化触发 */
            filter.addAction(android.net.wifi.WifiManager.RSSI_CHANGED_ACTION);
            /** wifi在连接过程的状态返回 */
            filter.addAction(android.net.wifi.WifiManager.NETWORK_STATE_CHANGED_ACTION);
            /** 监听wifi的打开与关闭等状态，与wifi的连接无关 */
            filter.addAction(android.net.wifi.WifiManager.WIFI_STATE_CHANGED_ACTION);
            /** 发送WIFI连接的过程信息，如果出错ERROR信息才会收到。连接WIFI时触发，触发多次。 */
            filter.addAction(android.net.wifi.WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
            /** 判断是否WIFI打开了，变化触发一次 */
            filter.addAction(android.net.wifi.WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
            /** 当网络连接状态改变时通知应用程序 */
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            // 注册广播
            if(wifiReceiver!=null){
                mContext.registerReceiver(wifiReceiver, filter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void stopAction(Context mContext){
        try{
            if(wifiReceiver!=null){
                mContext.unregisterReceiver(wifiReceiver);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     return:
     -1：不满足条件，不检查权限
     0：权限已经申请，不需要再申请
     1：需要申请权限
     */
    private int checkPermissionMixLocation(Activity activity,String permisionPara){
        int check=-1;

        if(permisionPara==null || permisionPara.isEmpty()) return check;
        if(!PermissionTool.reqCheckPermission()) return check;
        if(!PermissionTool.reqCheckTarget(activity)) return check;

        if(permisionPara.equals(PermissionTool.LOCATION_AUTO)){
            for(String permission:PermissionTool.specialPermission){
                if(permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)
                        || permission.equals(Manifest.permission.ACCESS_COARSE_LOCATION)){
                    check = 0;
                    break;
                }
            }
            if(check==-1)return check;
            if( isAppliedPermission(activity,Manifest.permission.ACCESS_FINE_LOCATION)
                    && isAppliedPermission(activity,Manifest.permission.ACCESS_COARSE_LOCATION) ){
                check = 0;
            }else{
                check = 1;
            }
        }

        return check;
    }
    /**
     * @param activity
     * @return
     * -1：不满足条件，不检查权限
     * 0：权限已经申请，不需要再申请
     * 1：需要申请权限
     */
    private int checkPermission(Activity activity,String permisionPara){
        int check=-1;

        if(permisionPara==null || permisionPara.isEmpty()) return check;
        if(!PermissionTool.reqCheckPermission()) return check;
        if(!PermissionTool.reqCheckTarget(activity)) return check;
        for(String permission:PermissionTool.specialPermission){
            if(permission.equals(permisionPara)){
                check = 0;
                break;
            }
        }
        if(check==-1)return check;

        if(isAppliedPermission(activity,permisionPara)){
            check = 0;
        }else{
            check = 1;
        }

        return check;
    }
    /**
     * @param context
     * @return
     * -1:定位未打开
     * -2:wifi开关未打开
     * 0:正常
     */
    private int checkWifi(Context context){
        int result = 0;

        //wifi lan check
        android.net.wifi.WifiManager wifiMan = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMan.getConnectionInfo();
        String currentSSID = wifiInfo.getSSID().toString().replace("\"","");
        //currentSSID = "<unknown ssid>";
        Log.v("wifi[","currentSSID="+currentSSID);
        if( currentSSID!=null &&
                !currentSSID.isEmpty() &&
                !currentSSID.equals("<unknown ssid>") ){
            if( isLocationEnabled(context)==false ){
//				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//				context.startActivity(intent);
                result = -1;
            }
        }else {
            if (isLocationEnabled(context) && mWifiUtils.isOpenWifi()) {
                result = 0;
            } else {
                if (isLocationEnabled(context) == false) {
//					Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//					mContext.startActivity(intent);
                    result = -1;
                } else if (mWifiUtils.isOpenWifi() == false) {
//					String mobile = SmartSharedPreferenceDefine.getMobileTypen(mContext);
//					if ("MEIZU".equals(mobile)) {
//						Intent intent = new Intent("android.settings.WIFI_SETTINGS");
//						startActivityForResult(intent, 1);
//					} else {
//						Intent intent2 = new Intent();
//						intent2.setAction("android.net.wifi.PICK_WIFI_NETWORK");
//						startActivityForResult(intent2, 1);
//					}
                    result = -2;
                } else {
                    result = 0;
                }
            }
        }

        return result;
    }
    /**
     * 查看权限是否已申请
     */
    private String checkPermissions(Activity activity, String... permissions) {
        permissionBuffer.delete(0,permissionBuffer.length());
        for (String permission : permissions) {
            permissionBuffer.append(permission);
            permissionBuffer.append(" is applied? \n     ");
            permissionBuffer.append(isAppliedPermission(activity,permission));
            permissionBuffer.append("\n\n");
        }
        return permissionBuffer.toString();
    }
    /**
     * 查看权限是否已申请
     */
    private boolean isAppliedPermission(Activity activity, String permission) {
        return activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }
    private boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
				/*
				1:GPS-TRUE  NETWORK-FALSE
				2;GPS-FALSE   NETWORK-TRUE
				3:GPS-TRUE    NETWORK-TRUE
				 */
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }
    /**
     * 4G或者wifi网卡切换
     * @param mContext
     * @param choiceWifi
     */
    private void changeNetworkType(final Context mContext, final boolean choiceWifi) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (Build.VERSION.SDK_INT >= 21) {
                        //if (is4GAvailable(mContext)) {
                        //LogTools.debug("camera_config", "changeNetwork goto set NET");
                        final ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkRequest.Builder builder = new NetworkRequest.Builder();

                        if(choiceWifi){
                            builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
                        }else{
                            builder.addCapability(NET_CAPABILITY_INTERNET);
                            //强制使用蜂窝数据网络-移动数据
                            builder.addTransportType(TRANSPORT_CELLULAR);
                        }

                        NetworkRequest request = builder.build();
                        ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback() {
                            @Override
                            public void onAvailable(Network network) {
                                //start net update
                                super.onAvailable(network);
                                //end
                                //LogTools.debug("camera_config", "changeNetwork network=" + network);
                                if (Build.VERSION.SDK_INT >= 23) {
                                    connectivityManager.bindProcessToNetwork(network);
                                } else {
                                    connectivityManager.setProcessDefaultNetwork(network);
                                }
                            }
                        };
                        connectivityManager.requestNetwork(request, callback);
                    }
                    //}
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private boolean netAp(Context mContext) {
        try {
            if (WifiUtils.getCurrentSSIDName(mContext).replace("\"", "").startsWith("IPC") ||
                    WifiUtils.getCurrentSSIDName(mContext).replace("\"", "").startsWith("@IPC") ||
                    WifiUtils.getCurrentSSIDName(mContext).replace("\"", "").startsWith("MC") ||
                    WifiUtils.getCurrentSSIDName(mContext).replace("\"", "").startsWith("@MC") ||
                    WifiUtils.getCurrentSSIDName(mContext).replace("\"", "").startsWith("DoorBell") ||
                    WifiUtils.getCurrentSSIDName(mContext).replace("\"", "").startsWith("@DoorBell")) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
