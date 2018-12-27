package com.clwater.baseble;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.clwater.blelibrary.Ble;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Ble.INSTANCE.init(this);
        Ble.INSTANCE.open();
        Ble.INSTANCE.checkPermission(new Ble.BLEPermission() {
            @Override
            public void onPosition() {

            }

            @Override
            public void onNegative() {

            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Ble.INSTANCE.RESULT_CODE_BLE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "打开", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "取消", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == Ble.INSTANCE.RESULT_CODE_BLE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(MainActivity.this, BleScanListActivity.class));

            } else {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
//                    showDialog();
            }
        }
    }

}
