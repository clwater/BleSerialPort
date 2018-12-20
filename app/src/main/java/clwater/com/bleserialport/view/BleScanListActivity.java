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

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import clwater.com.bleserialport.R;
import clwater.com.bleserialport.model.Device;
import clwater.com.bleserialport.utils.BleConnectUtils;
import clwater.com.bleserialport.view.adapter.ScanAdapter;

public class BleScanListActivity extends AppCompatActivity{
    private ScanAdapter mScanAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private List<Device> mDevice = new ArrayList<>();
    private List<BluetoothDevice> mDeviceList = new ArrayList<>();
    private Activity activity;
    private  RelativeLayout relativeLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_scan);

        activity = this;

        initView();
        initData();
        initReceiver();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        mBluetoothAdapter.startDiscovery();
    }

    private void initData() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                Device temp = new Device();
                temp.name = device.getName();
                temp.mac = device.getAddress();
                mDevice.add(temp);
                mDeviceList.add(device);
            }
            mScanAdapter.setNewData(mDevice);
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

                BluetoothDevice bluetoothDevice = mDeviceList.get(position);
                BleConnectUtils.INSTANCE.connect(activity, relativeLayout, bluetoothDevice, mBluetoothAdapter);
            }
        });


    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDeviceList.add(device);
                Device tempDevice = new Device();
                tempDevice.name = device.getName();
                tempDevice.mac = device.getAddress();
                mDevice.add(tempDevice);
                mScanAdapter.addData(mDevice);
            }
        }
    };

}
