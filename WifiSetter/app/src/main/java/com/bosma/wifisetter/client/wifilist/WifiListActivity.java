package com.bosma.wifisetter.client.wifilist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bosma.wifisetter.MainActivity;
import com.bosma.wifisetter.R;
import com.bosma.wifisetter.util.ActivityUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WifiListActivity extends AppCompatActivity {

    private static final String TAG_LOG = WifiListActivity.class.getSimpleName();
    @BindView(R.id.fl_wifilist_content)
    FrameLayout flWifilistContent;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private WifiListContract.View mFragView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_list);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//显示返回按钮

        mToolbar.setTitle("WifiSetter");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mToolbar.setOnMenuItemClickListener(onMenuItemClick);

        //先检查是否已经存在该View
        WifiListFrag statisticsFragment = (WifiListFrag) getSupportFragmentManager()
                .findFragmentById(R.id.fl_wifilist_content);

        if (statisticsFragment == null) {
            statisticsFragment = WifiListFrag.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    statisticsFragment, R.id.fl_wifilist_content);
        }

        mFragView = statisticsFragment;
        new WifiListPresenter(this, statisticsFragment);


    }

    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_setwifi:
                    mFragView.wifiSeetingRequest();
                    break;

            }


            return true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 為了讓 Toolbar 的 Menu 有作用，這邊的程式不可以拿掉
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }
}
