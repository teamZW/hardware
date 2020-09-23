package com.hardware.wifi;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * Created by dunn on 2018/08/06
 */

public final class PermissionTool {
    /*  special permission
    group:android.permission-group.CONTACTS
    permission:android.permission.WRITE_CONTACTS
    permission:android.permission.GET_ACCOUNTS       no
    permission:android.permission.READ_CONTACTS

    group:android.permission-group.PHONE     ok
    permission:android.permission.READ_CALL_LOG
    permission:android.permission.READ_PHONE_STATE
    permission:android.permission.CALL_PHONE
    permission:android.permission.WRITE_CALL_LOG
    permission:android.permission.USE_SIP
    permission:android.permission.PROCESS_OUTGOING_CALLS
    permission:com.android.voicemail.permission.ADD_VOICEMAIL

    group:android.permission-group.CALENDAR
    permission:android.permission.READ_CALENDAR
    permission:android.permission.WRITE_CALENDAR

    group:android.permission-group.CAMERA   ok
    permission:android.permission.CAMERA

    group:android.permission-group.SENSORS
    permission:android.permission.BODY_SENSORS

    group:android.permission-group.LOCATION
    permission:android.permission.ACCESS_FINE_LOCATION   no
    permission:android.permission.ACCESS_COARSE_LOCATION   no

    group:android.permission-group.STORAGE
    permission:android.permission.READ_EXTERNAL_STORAGE     no
    permission:android.permission.WRITE_EXTERNAL_STORAGE    no

    group:android.permission-group.MICROPHONE     ok
    permission:android.permission.RECORD_AUDIO

    group:android.permission-group.SMS
    permission:android.permission.READ_SMS
    permission:android.permission.RECEIVE_WAP_PUSH
    permission:android.permission.RECEIVE_MMS
    permission:android.permission.RECEIVE_SMS
    permission:android.permission.SEND_SMS
    permission:android.permission.READ_CELL_BROADCASTS
     */
    public static String[] specialPermission={
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.USE_SIP,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.ADD_VOICEMAIL,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.CAMERA,
            Manifest.permission.BODY_SENSORS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_WAP_PUSH,
            Manifest.permission.RECEIVE_MMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.SEND_SMS
    };

    /*  public permission
      android.permission.ACCESS_LOCATION_EXTRA_COMMANDS
      android.permission.ACCESS_NETWORK_STATE
      android.permission.ACCESS_NOTIFICATION_POLICY
      android.permission.ACCESS_WIFI_STATE
      android.permission.ACCESS_WIMAX_STATE
      android.permission.BLUETOOTH
      android.permission.BLUETOOTH_ADMIN
      android.permission.BROADCAST_STICKY
      android.permission.CHANGE_NETWORK_STATE
      android.permission.CHANGE_WIFI_MULTICAST_STATE
      android.permission.CHANGE_WIFI_STATE
      android.permission.CHANGE_WIMAX_STATE
      android.permission.DISABLE_KEYGUARD
      android.permission.EXPAND_STATUS_BAR
      android.permission.FLASHLIGHT
      android.permission.GET_ACCOUNTS
      android.permission.GET_PACKAGE_SIZE
      android.permission.INTERNET
      android.permission.KILL_BACKGROUND_PROCESSES
      android.permission.MODIFY_AUDIO_SETTINGS
      android.permission.NFC
      android.permission.READ_SYNC_SETTINGS
      android.permission.READ_SYNC_STATS
      android.permission.RECEIVE_BOOT_COMPLETED
      android.permission.REORDER_TASKS
      android.permission.REQUEST_INSTALL_PACKAGES
      android.permission.SET_TIME_ZONE
      android.permission.SET_WALLPAPER
      android.permission.SET_WALLPAPER_HINTS
      android.permission.SUBSCRIBED_FEEDS_READ
      android.permission.TRANSMIT_IR
      android.permission.USE_FINGERPRINT
      android.permission.VIBRATE
      android.permission.WAKE_LOCK
      android.permission.WRITE_SYNC_SETTINGS
      com.android.alarm.permission.SET_ALARM
      com.android.launcher.permission.INSTALL_SHORTCUT
      com.android.launcher.permission.UNINSTALL_SHORTCUT
     */
    public static final int CODE_REQUEST_CAMERA_PERMISSIONS = 4578;
    public static final String CAMERA_MIX="custom_camera_mix";
    public static final String EXSTORAGE_MIX="custom_exstorage_mix";
    public static final String LOCATION_AUTO="location_auto";

    public static boolean reqCheckPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return true;
        }else{
            return false;
        }
    }

    public static boolean reqCheckTarget(Context mContext){
        if(mContext.getApplicationInfo().targetSdkVersion>=Build.VERSION_CODES.M){
            return true;
        }else{
            return false;
        }
    }
}
