import tinyb.BluetoothNotification;

public class DrawNotification implements BluetoothNotification<byte[]> {
    public void run(byte[] tempRaw) {
        System.out.print("New Draw Value = {");
        for (byte b : tempRaw) {
            System.out.print(String.format("%02x,", b));
        }
        System.out.print("}");
    }
}