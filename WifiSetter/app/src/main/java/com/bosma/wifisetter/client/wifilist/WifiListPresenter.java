package com.bosma.wifisetter.client.wifilist;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 业务处理类
 *
 * Created by moyunchuan on 16/9/21.
 */
public class WifiListPresenter implements WifiListContract.Presenter {


    private Context mContext;

    @NonNull
    private final WifiListContract.View mWifiListView;

    public WifiListPresenter(Context context , @NonNull WifiListContract.View mWifiListView) {
        this.mWifiListView = Preconditions.checkNotNull(mWifiListView);
        mWifiListView.setPresenter(this);
        this.mContext = context;

    }

    @Override
    public void start() {

    }


    @Override
    public List<WifiBean> getWiFiList() {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        String currentSSID = null;
        if(info.getSSID() != null) {
            currentSSID = info.getSSID().replace("\"","");
        }


        wifiManager.startScan(); //必须先启动扫描，之后才能检测到变化！！
        List<ScanResult> scanResults=wifiManager.getScanResults();//搜索到的设备列表
        List<WifiBean> wifiInfo = new ArrayList<>();
        for (ScanResult scanResult : scanResults) {
            if(scanResult.SSID.equals(currentSSID)) {
                final SharedPreferences preferences = mContext.getSharedPreferences("wifi_password", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("current_ssid", currentSSID); //当前连接的wifi,即要设置到ipcam的wifi
                editor.commit();
                continue;
            }
            WifiBean wifiBean = new WifiBean();
            wifiBean.setSsid(scanResult.SSID);
            wifiBean.setLevel(wifiManager.calculateSignalLevel(scanResult.level, 1001));
            wifiInfo.add(wifiBean);
        }

        return wifiInfo;
    }

    @Override
    public boolean checkIPCWifi(String ssid) {

        try {
            Integer.parseInt(ssid);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    @Override
    public void setWifiRequest(String wifiName,  String passwd) {

        new WifiSetRequest(new WifiSetRequest.WifiSetCallBack() {
            @Override
            public void wifiSetResponse(boolean isSucceed, String respcode) {
                if(isSucceed) {
                    Toast.makeText(mContext, "设置成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "设置结束： " + respcode, Toast.LENGTH_SHORT).show();

                }

            }
        }).execute(wifiName,passwd);
    }
}
