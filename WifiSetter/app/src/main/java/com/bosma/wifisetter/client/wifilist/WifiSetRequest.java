package com.bosma.wifisetter.client.wifilist;

import android.os.AsyncTask;
import android.util.Log;


import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 项目名称：SmartCamp
 * 类描述：Wifi设置
 * 创建人：moyc
 * 创建时间：2016/2/23 13:13
 * 修改人：moyc
 * 修改时间：2016/2/23 13:13
 * 修改备注：
 */
public class WifiSetRequest extends AsyncTask<String, Integer, Boolean> {

    private static final String TAG_LOG = WifiSetRequest.class.getSimpleName();

    private WifiSetCallBack mCallBack;

    private int respcode;

    public interface WifiSetCallBack {
        void wifiSetResponse(boolean isSucceed, String respcode);
    }

    public WifiSetRequest(WifiSetCallBack callBack) {
        this.mCallBack = callBack;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        String wifiName = params[0];
        String wifiPw = params[1];

        String path = "http://192.168.169.1/wireless_sta";
        //通过Map构造器传参
        Map<String, String> data = new HashMap<String, String>();
        data.put("SSID", wifiName);
        //为key赋值
        data.put("SecurityMode", "");
        //为key赋值
        data.put("passwd", wifiPw);
        return sendGetRequest(path, data);
    }

    /**
     * 在调用该方法时，也需构建一个Map对象
     */
    private boolean sendGetRequest(String path, Map<String, String> params) {
        HttpURLConnection conn = null;
        try {
            StringBuilder sb = new StringBuilder(path);
            sb.append('?');
            // ?method=save&title=435435435&timelength=89&
            // 把Map中的数据迭代附加到StringBuilder中
            for (Map.Entry<String, String> entry : params.entrySet()) {
                // URLEncoder.encode对字符串中文进行编码，防止乱码
                sb.append(entry.getKey())
                        .append('=')
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                        .append('&');
            }
            // 去掉最后一个字符&
            sb.deleteCharAt(sb.length() - 1);
            // 把组拼完的路径传到URL对象
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            // 设置请求方式，GET要大写
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5 * 1000);
            int status = conn.getResponseCode();
            // "200"代表请求成功
            respcode = status;
            if (conn.getResponseCode() == 200) {
                return true;
            }

            Log.i(TAG_LOG, "WifiSet response status : " + status);
        } catch (Exception ex) {
            respcode = 999;
            Log.e(TAG_LOG, ex.toString());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (mCallBack != null) {
            mCallBack.wifiSetResponse(result, respcode+ "");
        }

    }

}
