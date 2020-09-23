package com.hardware.wifi;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static android.content.Context.MODE_PRIVATE;

public class ApManager {
//    /*
//    private static volatile ApManager instance;
//    private Context mmContext = BaseApplication.getContext();
//
//    private ConnectAp connectAp;
//    private WifiUtils wifiUtils;
//    //------wifiBean------------
//    private WiFiBean wifiBean;
//    //------listen--------------
//
//    //------ap type-------------
//    private enum TYPE{
//        INIT,CONNECT,P2P
//    }
//    private TYPE typeAP = TYPE.INIT;
//    //-------view---------------
//    private ImageView imageView;
//    private TextView textView;
//    //-------premission--------
//    private int premissionCount = 0;
//
//
//
//
//
//
//    //-----------------------connect wifi-----------------------------------------------------------
//
//    public ApManager cmdConnectWifi(String uid, ImageView imageView, TextView textView){
//        if(uid==null) return instance;
//
//        //根据uid查找ssid
//        String ssid = checkConnectAp(uid);
//        String pwd = "";
//        wifiBean = new WiFiBean(uid,ssid,pwd);
//        typeAP= TYPE.CONNECT;
//        LogTools.debug("ap","(2)connect：cmd did="+wifiBean.getUid()+", ssid="+wifiBean.getSsid()+", pwd="+wifiBean.getPwd()+", --------typeAP="+typeAP);
//
//        //view
//        //还原上次
//        if(this.imageView!=null){
//            this.imageView.setVisibility(View.VISIBLE);
//        }
//        if(this.textView!=null){
//            this.textView.setText(mmContext.getResources().getString(R.string.ap_restart_connected));
//        }
//        this.imageView = imageView;
//        this.textView = textView;
//        if(this.imageView!=null){
//            this.imageView.setVisibility(View.GONE);
//        }
//        if(this.textView!=null){
//            this.textView.setText(mmContext.getResources().getString(R.string.ap_restart_connecting));
//        }
//
//        try {
//            if( WifiUtils.getCurrentSSIDName(mmContext).replace("\"", "").equals(wifiBean.getSsid()) ){
//                typeAP= TYPE.P2P;
//                LogTools.debug("ap","(2)connect：current ssid did="+WifiUtils.getCurrentSSIDName(mmContext).replace("\"", "")+", --------typeAP="+typeAP);
//                String pwdDevice = LocalCameraData.getCameraPwd(wifiBean.getUid());
//                ApManager.getInstance().putVoicePwdStatus(wifiBean.getUid(), DualauthenticationCom.VOICE_DEVICE_PWD_RESET_FLAG_SET);
//                LogTools.debug("ap", "(2)connect： ----------restart---------- did=" + wifiBean.getUid() + ", pwd="+pwdDevice);
//                DualauthenticationUtils.ap_pwdwrong_reConnect(mmContext,wifiBean.getUid(),pwdDevice/*,false*/);
//                stopConnectTimer();
//            }else{
//                if (cmdExecutorService != null || !cmdExecutorService.isShutdown()) {
//                    Runnable run = new Runnable() {
//                        @Override
//                        public void run() {
//                            connect(ssid,pwd);
//                        }
//                    };
//                    cmdExecutorService.schedule(run, 100, TimeUnit.MILLISECONDS);
//                }
//                startConnectTimer();
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        return instance;
//    }
//    public void stopConnectWifi(){
//        LogTools.debug("ap","(2)connect：stop connect wifi");
//        typeAP= TYPE.INIT;
//        wifiBean = null;
//        //view
//        ThreadUtils.getIns().runOnMainThread(new Runnable() {
//            @Override
//            public void run() {
//                if(imageView!=null){
//                    imageView.setVisibility(View.VISIBLE);
//                }
//                if(textView!=null){
//                    textView.setText(mmContext.getResources().getString(R.string.ap_restart_connected));
//                }
//            }
//        });
//    }
//    private Timer timerConnect =new Timer();
//    private ConnectApTimerTask mConnectApTimerTask;
//    private final int CONNECT_SUM = 60;  //60
//    private int CONNECT_COUNT =0;
//    private void startConnectTimer(){
//        if (mConnectApTimerTask != null) {
//            mConnectApTimerTask.cancel();
//            mConnectApTimerTask = null;
//        }
//        CONNECT_COUNT = 0;
//        mConnectApTimerTask = new ConnectApTimerTask();
//        timerConnect.schedule(mConnectApTimerTask,1000,1000);
//    }
//    private void stopConnectTimer(){
//        if (mConnectApTimerTask != null) {
//            mConnectApTimerTask.cancel();
//            mConnectApTimerTask = null;
//        }
//    }
//    private void releaseConnectTimer(){
//        if (mConnectApTimerTask != null) {
//            mConnectApTimerTask.cancel();
//            mConnectApTimerTask = null;
//        }
//
//        if(timerConnect!=null){
//            timerConnect.cancel();
//            timerConnect=null;
//        }
//    }
//    private class ConnectApTimerTask extends TimerTask {
//        @Override
//        public void run() {
//            CONNECT_COUNT++;
//
//            //去判断连接设备AP
//            if ( (CONNECT_COUNT % 10) == 0 && wifiBean!=null ) {
//                LogTools.debug("ap","(2)connect：timerConnect currentAP="+ WifiUtils.getCurrentSSIDName(mmContext).replace("\"", "") +
//                        ", isConnected="+ WifiUtils.isWifiConnected(mmContext)+
//                        ", typeAP="+typeAP);
//                if ( (!WifiUtils.getCurrentSSIDName(mmContext).replace("\"", "").equals(wifiBean.getSsid())) &&   //当前连接不是指定AP
//                        //WifiUtils.isWifiConnected(mmContext)==false &&   //连接不稳定
//                        typeAP== TYPE.CONNECT    //是在连接状态
//                ) {
//                    connect(wifiBean.getSsid(),wifiBean.getPwd());
//                }
//            }
//
//            if (CONNECT_COUNT > CONNECT_SUM) {
//                if (mConnectApTimerTask != null) {
//                    mConnectApTimerTask.cancel();
//                    mConnectApTimerTask = null;
//                }
//
//                //view
//                ThreadUtils.getIns().runOnMainThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if(imageView!=null){
//                            imageView.setVisibility(View.VISIBLE);
//                        }
//                        if(textView!=null){
//                            textView.setText(mmContext.getResources().getString(R.string.ap_restart_connected));
//                        }
//                    }
//                });
//
//                if(apNptify!=null){
//                    if(wifiBean!=null) {
//                        LogTools.debug("ap", "(2)connect：timerConnect timeout!!!!!!! did=" + wifiBean.getUid() + ", ssid=" + wifiBean.getSsid() + ", pwd=" + wifiBean.getPwd());
//                        apNptify.apTimeOut("xxxx");
//                    }
//                }
//            }
//        }
//    }
//    private void connect(final String ssid, final String pwd) {
//        try {
//            if (checkApConnectType(ssid)) {
//                LogTools.debug("ap","(2)connect：quickConnWifi ssid="+ssid+", pwd="+pwd+", type=NOPWD(pwd=null)");
//                wifiUtils.quickConnWifi(mmContext, ssid, null, WifiUtils.NOPWD);
//            } else {
//                String ssidType = connectAp.getCipherType(mmContext, ssid);
//                if (ssidType.equals("no")) {
//                    if (checkApConnectType(ssid)) {
//                        LogTools.debug("ap","(2)connect：quickConnWifi ssid="+ssid+", pwd="+pwd+", type=NOPWD(pwd=null)");
//                        wifiUtils.quickConnWifi(mmContext, ssid, null, WifiUtils.NOPWD);
//                    } else {
//                        LogTools.debug("ap","(2)connect：quickConnWifi ssid="+ssid+", pwd="+pwd+", type=NOPWD");
//                        wifiUtils.quickConnWifi(mmContext, ssid, pwd, WifiUtils.NOPWD);
//                    }
//                } else if (ssidType.equals("wep")) {
//                    LogTools.debug("ap","(2)connect：quickConnWifi ssid="+ssid+", pwd="+pwd+", type=WEP");
//                    wifiUtils.quickConnWifi(mmContext, ssid, pwd, WifiUtils.WEP);
//                } else if (ssidType.equals("wpa")) {
//                    LogTools.debug("ap","(2)connect：quickConnWifi ssid="+ssid+", pwd="+pwd+", type=WPA");
//                    wifiUtils.quickConnWifi(mmContext, ssid, pwd, WifiUtils.WPA);
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//
//
//
//
//
//
//    //-------------------------------common---------------------------------------------------------
//    private boolean checkUidToCurrentSsid(String uid){
//        boolean result = false;
//
//        String currentSsid = WifiUtils.getCurrentSSIDName(mmContext).replace("\"", "");
//        String uidNum = HasDigitResult(uid);
//        String ssidNum = HasDigitResult(currentSsid);
//        if(uidNum!=null && uidNum.equals(ssidNum)){
//            result=true;
//        }
//
//        return result;
//    }
//    private boolean checkApConnectType(String ssid){
//        if(ssid==null) return false;
//
//        try {
//            if (ssid.startsWith("IPC-") || ssid.startsWith("@IPC-") || ssid.startsWith("MC-") || ssid.startsWith("@MC-")) {
//                return true;
//            } else {
//                return false;
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            return false;
//        }
//    }
//    private String HasDigitResult(String content) {
//        String result="";
//        Pattern p = Pattern.compile(".*\\d+.*");
//        Matcher m = p.matcher(content);
//        if (m.matches()) {
//            result=getNumbers(content);
//        }
//        return result;
//    }
//    private String getNumbers(String content) {
//        Pattern pattern = Pattern.compile("\\d+");
//        Matcher matcher = pattern.matcher(content);
//        while (matcher.find()) {
//            return matcher.group(0);
//        }
//        return "";
//    }
//    private String getSystemVer(String did) {
//        String ver = "0";
//        try {
//            SharedPreferences sp = mmContext.getSharedPreferences(ContentCommon.STR_CAMERA_SYSTEMFIRM, mmContext.MODE_PRIVATE);
//            ver = sp.getString(did, "0");
//        } catch (Exception e) {
//        }
//        return ver;
//    }
//    */
}
