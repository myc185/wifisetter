package com.bosma.wifisetter.client.wifilist;


import android.net.wifi.WifiInfo;

import com.bosma.wifisetter.common.BasePresenter;
import com.bosma.wifisetter.common.BaseView;

import java.util.List;

/**
 * Created by moyunchuan on 16/9/21.
 */
public interface WifiListContract {



    interface View extends BaseView<Presenter> {

            void wifiSeetingRequest();
    }


    interface Presenter extends BasePresenter {

        /***
         * 获取wifi列表
         * @return
         */
        List<WifiBean> getWiFiList();

        /****
         * 判断是否是ipc 发出的wifi
         * @return
         */
        boolean checkIPCWifi(String ssid);

        void setWifiRequest(String wifiName,  String passwd);


    }
}
