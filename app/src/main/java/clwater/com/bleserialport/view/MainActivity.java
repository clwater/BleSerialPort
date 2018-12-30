package clwater.com.bleserialport.view;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import clwater.com.bleserialport.R;
import clwater.com.bleserialport.event.BleConnect;
import clwater.com.bleserialport.utils.BleConnectUtils;
import clwater.com.bleserialport.utils.BluetoothUtils;
import clwater.com.bleserialport.utils.ConnectedThread;
import clwater.com.bleserialport.view.adapter.SimpleAdapter;

public class MainActivity extends AppCompatActivity {

    private final int RESULT_CODE_BLE = 10001;
    private final int RESULT_CODE_SCAN = 10002;
    private ConnectedThread mConnectedThread;
    private BluetoothAdapter mBluetoothAdapter;

    public static boolean isConnect = false;


    //已连接蓝牙设备，则接收数据，并显示到接收区文本框
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ConnectedThread.MESSAGE_READ:
                    byte[] buffer = (byte[]) msg.obj;
                    int length = msg.arg1;
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < length; i++) {
                        char c = (char) buffer[i];
                        sb.append(c);
                    }
                    break;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);

        initBleStatus();
        initView();
    }

    private void initView() {

        TextView textView = findViewById(R.id.text_into_scan);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBleScan();
            }
        });

        findViewById(R.id.text_into_disconnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnect){
                    BleConnectUtils.INSTANCE.close();
                    isConnect = false;
                }else{
                    Toast.makeText(MainActivity.this, "未链接设备", Toast.LENGTH_SHORT).show();
                }
            }
        });


        findViewById(R.id.ctrl_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendText("31");
            }
        });

        findViewById(R.id.ctrl_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendText("34");
            }
        });

        findViewById(R.id.ctrl_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendText("37");
            }
        });

        findViewById(R.id.ctrl_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendText("39");
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (BluetoothUtils.getBluetoothSocket() == null || mConnectedThread != null) {
            return;
        }


        mConnectedThread = new ConnectedThread(BluetoothUtils.getBluetoothSocket(), handler);
        mConnectedThread.start();
    }

    private void sendText(String sendStr) {
        if (isConnect || getConnectBt()) {
            send(sendStr);
        } else {
            Toast.makeText(this, "未连接设备", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean getConnectBt() {
        int a2dp = mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP);
        int headset = mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET);
        int health = mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEALTH);
        int flag = -1;
        if (a2dp == BluetoothProfile.STATE_CONNECTED) {
            flag = a2dp;
        } else if (headset == BluetoothProfile.STATE_CONNECTED) {
            flag = headset;
        } else if (health == BluetoothProfile.STATE_CONNECTED) {
            flag = health;
        }
        if (flag != -1) {
            isConnect = true;
            return true;
        } else {
            return false;
        }

    }

    private void send(String sendStr) {
        char[] chars = sendStr.toCharArray();
        byte[] bytes = new byte[chars.length];
        for (int i = 0; i < chars.length; i++) {
            bytes[i] = (byte) chars[i];
        }
        mConnectedThread.write(bytes);
    }

    private void checkBleScan() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, RESULT_CODE_SCAN);
        } else {
            startActivity(new Intent(MainActivity.this, BleScanListActivity.class));
        }
    }

    private void initBleStatus() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "不支持", Toast.LENGTH_SHORT).show();
        }

        boolean enabled = mBluetoothAdapter.isEnabled();
        if (!enabled) {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), RESULT_CODE_BLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void BleConnectStatus(BleConnect bleConnect) {
        if (bleConnect.success) {
            isConnect = true;
            Toast.makeText(this, "连接成功", Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_CODE_BLE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "打开", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "取消", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RESULT_CODE_SCAN: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, BleScanListActivity.class));

                } else {
                    Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
                    showDialog();
                }
                return;
            }
        }
    }

    public void showDialog() {
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(MainActivity.this);
        normalDialog.setTitle("申请定位权限");
        normalDialog.setMessage("禁止定位权限可能导致6.0以上系统无法搜索到新的蓝牙设备\n是否重新申请权限?");
        normalDialog.setPositiveButton("是",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, RESULT_CODE_SCAN);
                    }
                });
        normalDialog.setNegativeButton("否",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(MainActivity.this, BleScanListActivity.class));
                    }
                });
        normalDialog.show();
    }
}
