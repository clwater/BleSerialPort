package clwater.com.bleserialport.view.adapter;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

import clwater.com.bleserialport.R;
import clwater.com.bleserialport.model.Device;

public class ScanAdapter extends BaseMultiItemQuickAdapter<Device, BaseViewHolder> {

    public ScanAdapter(List<Device> data) {
        super(data);
        addItemType(1, R.layout.item_scan);
        addItemType(0, R.layout.item_title);
    }

    @Override
    protected void convert(BaseViewHolder helper, Device item) {
        switch (helper.getItemViewType()) {
            case 1:
                BluetoothDevice bluetoothDevice = item.bluetoothDevice;
                helper.setText(R.id.textview_scan_title, bluetoothDevice.getName());
                helper.setText(R.id.textview_scan_address, bluetoothDevice.getAddress());
                break;
            case 0:
                helper.setText(R.id.item_title, item.text);
                break;
        }
    }

}
