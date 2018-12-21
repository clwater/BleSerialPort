package clwater.com.bleserialport.view.adapter;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import clwater.com.bleserialport.R;
import clwater.com.bleserialport.model.Device;

public class ScanAdapter extends BaseQuickAdapter<BluetoothDevice, BaseViewHolder> {

    public ScanAdapter(@Nullable List<BluetoothDevice> data) {
        super(R.layout.item_scan, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BluetoothDevice item) {
        helper.setText(R.id.textview_scan_title, item.getName());
        helper.setText(R.id.textview_scan_address, item.getAddress());
    }
}
