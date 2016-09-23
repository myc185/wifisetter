package com.bosma.wifisetter.client.wifilist;

/**
 * Created by moyunchuan on 16/9/22.
 */
public class WifiBean implements Comparable<WifiBean>{

    private String ssid;
    private int level;

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public int compareTo(WifiBean wifiBean) {
//        int sx = this.level - wifiBean.getLevel();
//        if(sx == 0) {
//
//        }
//        if (this.level < wifiBean.getLevel()) {
//            return (this.level - wifiBean.getLevel());
//        }
//        if (this.level > wifiBean.getLevel()) {
//            return (this.level - wifiBean.getLevel());
//        }
        if(this.level != wifiBean.getLevel()) {
            return wifiBean.getLevel() - this.level;
        }


        return this.getSsid().length() - getSsid().length();
    }
}
