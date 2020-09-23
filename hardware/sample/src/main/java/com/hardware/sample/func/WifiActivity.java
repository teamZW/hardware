package com.hardware.sample.func;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.hardware.sample.MainActivity;
import com.hardware.sample.R;
import com.hardware.sample.data.CustomBean;
import com.hardware.wifi.WifiCode;
import com.hardware.wifi.WifiManager;
import com.hardware.wifi.able.WifiUpdate;
import com.hardware.wifi.bean.BindSmartBean;
import com.hardware.wifi.bean.WifiAdapter;
import java.util.ArrayList;

public class WifiActivity extends Activity{
    private TextView current_wifi,code;
    private ListView list_wifi;
    private boolean wifiOpt = false;
    private ArrayList<BindSmartBean> searchWifiList = new ArrayList<BindSmartBean>();
    private WifiAdapter mAdapter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加载MainView
        setContentView(R.layout.activity_wifi);

        current_wifi = (TextView) findViewById(R.id.current_wifi);
        code = (TextView) findViewById(R.id.code);
        list_wifi = (ListView) findViewById(R.id.list_wifi);
        mAdapter = new WifiAdapter(WifiActivity.this,searchWifiList);
        list_wifi.setAdapter(mAdapter);

        initListen();
        initValue();
    }

    private void initListen(){
        list_wifi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BindSmartBean bean = searchWifiList!=null?searchWifiList.get(position):null;
                if(bean!=null){
                    String ssid = bean.getSSID();
                    WifiManager.L().connectAp(WifiActivity.this,ssid,null);
                }
            }
        });
    }

    private void initValue(){
        String[] str = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(this, str, 1);
    }

    public class CallBackWifi implements WifiUpdate{

        @Override
        public void updateWifiBean(ArrayList<BindSmartBean> bean) {
            searchWifiList.clear();
            searchWifiList.addAll(bean);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.refresh();
                }
            });
        }

        @Override
        public void updateCurrentState(final String ssid, final String state) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    current_wifi.setText(ssid+"("+state+")");
                }
            });
        }

        @Override
        public void updateError(final WifiCode.ErrorStatus state) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    code.setText("CODE："+state.toString());
//                    if(state==WifiCode.ErrorStatus.location_error){
//                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        WifiActivity.this.startActivity(intent);
//                    }else if(state==WifiCode.ErrorStatus.wifi_error){
//
//                    }
                }
            });
        }
    }

    public void onBack(View view){
        finish();
    }

    public void onStartScan(View view){
        if(wifiOpt==false){
            wifiOpt=true;
            WifiManager.L().startScanWiFi(WifiActivity.this,new CallBackWifi());
        }
    }

    public void onStopScan(View view){
        if(wifiOpt==true){
            wifiOpt=false;
            WifiManager.L().releaseWiFi(WifiActivity.this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WifiManager.L().releaseWiFi(WifiActivity.this);
    }
}