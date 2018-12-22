package clwater.com.bleserialport.view;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import clwater.com.bleserialport.R;
import clwater.com.bleserialport.event.BleConnect;
import clwater.com.bleserialport.model.Device;
import clwater.com.bleserialport.utils.BleConnectUtils;
import clwater.com.bleserialport.view.adapter.ScanAdapter;

public class BleScanListActivity extends AppCompatActivity {
    private ScanAdapter mScanAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private List<Device> mDeviceList = new ArrayList<>();
    private Activity activity;
    private RelativeLayout relativeLayout;
    private BleBroadcastReceiver bleBroadcastReceiver;
    private Set<String> loadAddress = new HashSet<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_scan);
        EventBus.getDefault().register(this);

        activity = this;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        initView();
        initData();
        initBle();
    }

    private void initBle() {
        bleBroadcastReceiver = new BleBroadcastReceiver();
        initReceiver();
        mBluetoothAdapter.startDiscovery();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(bleBroadcastReceiver);
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(bleBroadcastReceiver, filter);
    }

    private void initData() {
        Device top1 = new Device();
        top1.text = "已配对设备";
        mDeviceList.add(top1);
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                Device temp = new Device();
                temp.bluetoothDevice = device;
                temp.type = 1;
                loadAddress.add(device.getAddress());
                mDeviceList.add(temp);
            }

            Device top2 = new Device();
            top2.text = "可用设备";
            mDeviceList.add(top2);
            mScanAdapter.setNewData(mDeviceList);
        }
    }

    private void initView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerview_list);
        mScanAdapter = new ScanAdapter(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mScanAdapter);
        relativeLayout = findViewById(R.id.progress_all);


        mScanAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                relativeLayout.setVisibility(View.VISIBLE);
                final BluetoothDevice bluetoothDevice = mDeviceList.get(position).bluetoothDevice;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        BleConnectUtils.INSTANCE.connect(bluetoothDevice, mBluetoothAdapter);
                    }
                }).start();
            }
        });


    }

    class BleBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
                    Device temp = new Device();
                    temp.bluetoothDevice = device;
                    temp.type = 1;
                    if (!loadAddress.contains(device.getAddress())) {
                        mDeviceList.add(temp);
                        loadAddress.add(device.getAddress());
                        mScanAdapter.notifyDataSetChanged();
                    }
                }
            }

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Toast.makeText(activity, "扫描结束", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void BleConnectStatus(BleConnect bleConnect){
        if (!bleConnect.success){
            switch (bleConnect.status){
                case 1:
                    Toast.makeText(activity, "获取Socket失败", Toast.LENGTH_SHORT).show();
                    relativeLayout.setVisibility(View.GONE);
                    break;
                case 2:
                    Toast.makeText(activity, "连接失败", Toast.LENGTH_SHORT).show();
                    relativeLayout.setVisibility(View.GONE);
                    break;

            }
        }else {
            this.finish();
        }
    }

}
