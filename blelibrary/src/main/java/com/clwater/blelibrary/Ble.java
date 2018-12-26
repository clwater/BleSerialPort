package com.clwater.blelibrary;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Create by clwater on 2018/12/26.
 */
public enum Ble {
    INSTANCE;
    private Activity context;
    BluetoothAdapter mBluetoothAdapter;

    public void init(Activity context){
        this.context = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public boolean check(boolean showToas){
        if (mBluetoothAdapter == null) {
            if (showToas){
                Toast.makeText(context, "不支持", Toast.LENGTH_SHORT).show();
            }
            return false;
        }else {
            return true;
        }
    }

    private boolean check(){
        return check(false);
    }

    public void open(int RESULT_CODE_BLE){
        boolean enabled = mBluetoothAdapter.isEnabled();
        if (!enabled) {
            context.startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), RESULT_CODE_BLE);
        }

    }

}
