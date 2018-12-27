package com.clwater.blelibrary;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

/**
 * Create by clwater on 2018/12/26.
 */
public enum Ble {
    INSTANCE;
    private Activity context;
    private BluetoothAdapter mBluetoothAdapter;
    public final int RESULT_CODE_BLE = 7001;
    public final int RESULT_CODE_BLE_PERMISSION = 7002;

    public void init(Activity context){
        this.context = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * 当前设备是否支持蓝牙
     * @param showToas
     * @return
     */
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

    /**
     * 调用系统打开蓝牙
     */
    public void open(){
        open(RESULT_CODE_BLE);
    }
    public void open(int requestCode){
        boolean enabled = mBluetoothAdapter.isEnabled();
        if (!enabled) {
            context.startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), requestCode);
        }
    }

/**
 * 添加如下方法，增加对蓝牙打开状况的回调
 */
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == Ble.INSTANCE.RESULT_CODE_BLE) {
//            if (resultCode == RESULT_OK) {
//                Toast.makeText(this, "打开", Toast.LENGTH_SHORT).show();
//            } else if (resultCode == RESULT_CANCELED) {
//                Toast.makeText(this, "取消", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    public void checkPermission(BLEPermission blePermission){
        checkPermission(blePermission, RESULT_CODE_BLE_PERMISSION);
    }

    /**
     * 申请搜索蓝牙相关权限
     * @param blePermission
     * @param requestCode
     */
    public void checkPermission(final BLEPermission blePermission, final int requestCode){
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(context);
        normalDialog.setTitle("申请定位权限");
        normalDialog.setMessage("禁止定位权限可能导致6.0以上系统无法搜索到新的蓝牙设备\n是否重新申请权限?");
        normalDialog.setPositiveButton("是",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        blePermission.onPosition();
                        ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, requestCode);
                    }
                });
        normalDialog.setNegativeButton("否",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        blePermission.onNegative();
//                        startActivity(new Intent(MainActivity.this, BleScanListActivity.class));
                    }
                });
        normalDialog.show();
    }

  /**
   * 添加如下方法，增加对蓝牙权限状况的回调
   */
    //    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case Ble.INSTANCE.RESULT_CODE_BLE_PERMISSION: {
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                  成功
//                } else {
//                  失败
//                }
//                return;
//            }
//        }
//    }


    public interface BLEPermission{
        void onPosition();
        void onNegative();
    }
}
