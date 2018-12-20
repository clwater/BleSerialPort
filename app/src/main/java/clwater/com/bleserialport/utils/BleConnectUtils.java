package clwater.com.bleserialport.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public enum  BleConnectUtils {
    INSTANCE;
    public void connect(Activity activity, RelativeLayout relativeLayout, BluetoothDevice device, BluetoothAdapter mBluetoothAdapter){
        BluetoothSocket socket = null;
        try {
            // 蓝牙串口服务对应的UUID。如使用的是其它蓝牙服务，需更改下面的字符串
            UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            socket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (Exception e) {
            Toast.makeText(activity, "获取Socket失败", Toast.LENGTH_SHORT).show();
            relativeLayout.setVisibility(View.GONE);
            return;
        }
        mBluetoothAdapter.cancelDiscovery();
        try {
            socket.connect();
            Toast.makeText(activity, "连接成功", Toast.LENGTH_SHORT).show();
            BluetoothUtils.setBluetoothSocket(socket);

            activity.finish();
        } catch (IOException connectException) {
            Toast.makeText(activity, "连接失败", Toast.LENGTH_SHORT).show();
            relativeLayout.setVisibility(View.GONE);
            try {
                socket.close();
            } catch (IOException closeException) {
            }
        }
    }

}
