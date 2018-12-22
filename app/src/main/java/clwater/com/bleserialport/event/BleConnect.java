package clwater.com.bleserialport.event;

/**
 * Create by VillageHope on 2018/12/22.
 */
public class BleConnect {
    public boolean success;
    public int status;  //1 获取socket失败 2 连接失败

    public BleConnect(boolean success, int status) {
        this.success = success;
        this.status = status;
    }
}
