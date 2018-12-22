package clwater.com.bleserialport.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.UUID;

import clwater.com.bleserialport.event.BleConnect;

public enum  BleConnectUtils {
    INSTANCE;
    public void connect(BluetoothDevice device, BluetoothAdapter mBluetoothAdapter){
        BluetoothSocket socket = null;
        try {
            // 蓝牙串口服务对应的UUID。如使用的是其它蓝牙服务，需更改下面的字符串
            UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            socket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (Exception e) {
            EventBus.getDefault().post(new BleConnect(false, 1));
            return;
        }
        mBluetoothAdapter.cancelDiscovery();
        try {
            socket.connect();
            BluetoothUtils.setBluetoothSocket(socket);
            EventBus.getDefault().post(new BleConnect(true, 0));
        } catch (IOException connectException) {
            EventBus.getDefault().post(new BleConnect(false, 2));
            try {
                socket.close();
            } catch (IOException closeException) {
            }
        }
    }

}
