package clwater.com.bleserialport.view.adapter;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import clwater.com.bleserialport.R;
import clwater.com.bleserialport.model.Device;

public class ScanAdapter extends BaseQuickAdapter<Device, BaseViewHolder> {

    public ScanAdapter(@Nullable List<Device> data) {
        super(R.layout.item_scan, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Device item) {
        BluetoothDevice bluetoothDevice = item.bluetoothDevice;
        helper.setText(R.id.textview_scan_title, bluetoothDevice.getName());
        helper.setText(R.id.textview_scan_address, bluetoothDevice.getAddress());
    }
}
