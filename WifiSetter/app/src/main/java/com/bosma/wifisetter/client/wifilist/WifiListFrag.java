package com.bosma.wifisetter.client.wifilist;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.bosma.wifisetter.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class WifiListFrag extends Fragment implements WifiListContract.View {


    @BindView(android.R.id.list)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_widget)
    SwipeRefreshLayout mSwipeRefreshWidget;

    private List<WifiBean> data = new ArrayList<>();
    private WifiListContract.Presenter mWifiListPresenter;
    private RecyclerViewAdapter adapter;

    private WifiAdmin wifiAdmin;
    //当前需要设置的路由器
    private String ssid;

    public static WifiListFrag newInstance() {
        return new WifiListFrag();
    }

    public WifiListFrag() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wifi_list, container, false);
        ButterKnife.bind(this, view);

        wifiAdmin = new WifiAdmin(getContext());
        wifiAdmin.checkState(getContext());

        mSwipeRefreshWidget.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setListData();
            }
        });

        adapter = new RecyclerViewAdapter(getActivity(), data);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);
        setListData();

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d("test", "StateChanged = " + newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d("test", "onScrolled");
            }
        });

        //添加点击事件
        adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                Log.d("test", "item position = " + position);

                wifiAdmin.getConfiguration();
                ssid = data.get(position).getSsid();

//                //判断是否是纯数字（我们的ipcam以数字开头）
//                //如果是，直接连接并设置
//                if (mWifiListPresenter.checkIPCWifi(ssid)) {
//                    Toast.makeText(getContext(), "正在连接IPC AP: " + ssid, Toast.LENGTH_LONG).show();
//                    wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo(ssid, "0"+ ssid, 3));
//                    return;
//                } else {
//
//
//                }


                final SharedPreferences preferences = getActivity().getSharedPreferences("wifi_password", Context.MODE_PRIVATE);
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle(ssid);
                alert.setMessage("输入密码");
                final EditText etPassword = new EditText(getActivity());

                etPassword.setText(preferences.getString(ssid, ""));
                alert.setView(etPassword);
                //alert.setView(view1);
                alert.setPositiveButton("连接", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String pw = etPassword.getText().toString();
                        if (null == pw || pw.length() < 8) {
                            Toast.makeText(getContext(), "密码至少8位", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(ssid, pw);   //保存密码
                        editor.commit();
                        Toast.makeText(getContext(), "正在给IPCAM 设置SSID", Toast.LENGTH_SHORT).show();
                        //TODO 设置wifi给路由器
                        mWifiListPresenter.setWifiRequest(ssid, pw);

                    }
                });
                alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.create();
                alert.show();

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        IntentFilter filter = new IntentFilter(
                WifiManager.NETWORK_STATE_CHANGED_ACTION);
        //="android.net.wifi.STATE_CHANGE"  监听wifi状态的变化
        getActivity().registerReceiver(mReceiver, filter);

        return view;
    }

    private void setListData() {
        data.clear();
        data = mWifiListPresenter.getWiFiList();
        Collections.sort(data);
        adapter.setData(data);
        adapter.notifyDataSetChanged();
        mSwipeRefreshWidget.setRefreshing(false);
        adapter.notifyItemRemoved(adapter.getItemCount());
    }

    @Override
    public void setPresenter(WifiListContract.Presenter presenter) {
        this.mWifiListPresenter = presenter;

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    //监听wifi状态
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            Log.i("WifiFrag", "============wifi set mReceiver ===================");
            if (wifiInfo.isConnected()) {
                WifiManager wifiManager = (WifiManager) context
                        .getSystemService(Context.WIFI_SERVICE);
                String wifiSSID = wifiManager.getConnectionInfo()
                        .getSSID();
                Toast.makeText(context, wifiSSID + " 连接成功", Toast.LENGTH_SHORT).show();
                setListData();
            }
        }

    };

    @Override
    public void wifiSeetingRequest() {

    }
}
