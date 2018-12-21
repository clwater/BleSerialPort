package clwater.com.bleserialport.model;

import android.bluetooth.BluetoothDevice;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class Device implements MultiItemEntity {
    public BluetoothDevice bluetoothDevice;
    public int type;
    public String text;

    @Override
    public int getItemType() {
        return type;
    }
}
