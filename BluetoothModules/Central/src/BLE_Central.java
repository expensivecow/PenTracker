import tinyb.*;
import java.util.*;

public class BLE_Central {
	static boolean running = true;
	
	public static void main(String[] args) throws InterruptedException {
		String MAC_ADDR = "E0:2B:2D:3B:82:89";
		String DRAW_SERVICE = "180D";
		String BATTERY_LEVEL_CHARACTERISTIC = "2A38";

		BluetoothManager manager = BluetoothManager.getBluetoothManager();
		
		boolean discoveryStarted = manager.startDiscovery();
		System.out.println("The discovery started: " + (discoveryStarted ? "true" : "false"));

		BluetoothDevice penDevice = scanDevice(MAC_ADDR);

		// Stop discovery after finding penDevice
        try {
            manager.stopDiscovery();
        } catch (BluetoothException e) {
            System.err.println("Discovery could not be stopped.");
        }

        // Try to connect to the penDevice
        if (penDevice != null && penDevice.connect())
            System.out.println("penDevice has been connected");
        else {
            System.out.println("Could not connect penDevice.");
            System.exit(-1);
        }

        BluetoothGattService tempService = getService(penDevice, DRAW_SERVICE);
        
        if (tempService == null) {
            System.err.println("This device does not have the draw service we are looking for.");
            penDevice.disconnect();
            System.exit(-1);
        }
        
        BluetoothGattCharacteristic batteryCharacteristic = tempService.find(BATTERY_LEVEL_CHARACTERISTIC);
        
        batteryCharacteristic.enableValueNotifications(new DrawNotification());
        
        // Do notifications for 10 seconds
        for (int i = 0; i < 10; i++) {
            byte[] tempRaw = batteryCharacteristic.readValue();
            System.out.print("Read Battery Level = {");
            for (byte b : tempRaw) {
                System.out.print(String.format("%02x,", b));
            }
            System.out.print("}");
        	Thread.sleep(1000);	
        }
	}
	
    static BluetoothGattService getService(BluetoothDevice device, String UUID) throws InterruptedException {
        System.out.println("Services exposed by device:");
        BluetoothGattService tempService = null;
        List<BluetoothGattService> bluetoothServices = null;
        do {
            bluetoothServices = device.getServices();
            if (bluetoothServices == null)
                return null;

            for (BluetoothGattService service : bluetoothServices) {
                System.out.println("UUID: " + service.getUUID());
                if (service.getUUID().equals(UUID))
                    tempService = service;
            }
            Thread.sleep(4000);
        } while (bluetoothServices.isEmpty() && running);
        return tempService;
    }
	
	static BluetoothDevice scanDevice(String macAddr) throws InterruptedException {
		BluetoothManager manager = BluetoothManager.getBluetoothManager();
	    List<BluetoothDevice> list = manager.getDevices();
	    
		for (int i = 0; (i < 5); ++i) {
			for (BluetoothDevice penDevice : list) {
			    printDevice(penDevice);
			    
				if (penDevice.getAddress().equals(macAddr)) {
					System.out.println("Found the Device!");
					return penDevice;	
				}
			}
			
			Thread.sleep(4000);
		}
		
		return null;
	}
	
    static void printDevice(BluetoothDevice penDevice) {
        System.out.print("Address = " + penDevice.getAddress());
        System.out.print(" Name = " + penDevice.getName());
        System.out.print(" Connected = " + penDevice.getConnected());
        System.out.println();
    }
}
