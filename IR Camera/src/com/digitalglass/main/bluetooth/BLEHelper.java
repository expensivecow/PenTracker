package com.digitalglass.main.bluetooth;

import java.util.HashMap;
import java.util.List;

import com.digitalglass.main.core.Initializer;
import com.digitalglass.main.utils.Config;



import tinyb.BluetoothDevice;
import tinyb.BluetoothException;
import tinyb.BluetoothGattCharacteristic;
import tinyb.BluetoothGattService;
import tinyb.BluetoothManager;

public class BLEHelper {
	private String macAddress;
	private BluetoothManager bleManager;
	private BluetoothDevice penDevice;
	private Initializer instance;
	private boolean connected;
	private HashMap<String, BluetoothGattCharacteristic> characteristics;
	
	
	public BLEHelper(Initializer inst) throws InterruptedException {
		// Have reference to board
		instance = inst;

		bleManager = BluetoothManager.getBluetoothManager();
		tryConnect();
	}

    public BluetoothGattService getService(String UUID) throws InterruptedException {
        //System.out.println("Services exposed by device:");
        BluetoothGattService tempService = null;
        List<BluetoothGattService> bluetoothServices = null;
        do {
            bluetoothServices = penDevice.getServices();
            if (bluetoothServices == null)
                return null;

            for (BluetoothGattService service : bluetoothServices) {
                //System.out.println("UUID: " + service.getUUID());
                if (service.getUUID().equals(UUID))
                    tempService = service;
            }
            Thread.sleep(4000);
        } while (bluetoothServices.isEmpty() && penDevice.getConnected());
        return tempService;
    }
	

	private BluetoothDevice scanDevice(String macAddr) throws InterruptedException {
		BluetoothManager manager = BluetoothManager.getBluetoothManager();
	    List<BluetoothDevice> list = manager.getDevices();
	    
		for (int i = 0; (i < 5); ++i) {
			for (BluetoothDevice penDevice : list) {
			    printDevice(penDevice);
			    
				if (penDevice.getAddress().equals(macAddr)) {
					//System.out.println("Found the Device!");
					return penDevice;
				}
			}
			
			Thread.sleep(5000);
		}
		
		return null;
	}
	
    private void printDevice(BluetoothDevice device) {
        System.out.print("Address = " + device.getAddress());
        System.out.print(" Name = " + device.getName());
        System.out.print(" Connected = " + device.getConnected());
        System.out.println();
    }
    
    public void tryConnect() throws InterruptedException {
		bleManager.startDiscovery();
		
		// Store and scan for the device with a given macAddress
		macAddress = Config.getInstance().getProperty("MacAddress");
		penDevice = scanDevice(macAddress);
		
		// Stop discovery after finding penDevice
        try {
            bleManager.stopDiscovery();
        } catch (BluetoothException e) {
            System.err.println("Discovery could not be stopped.");
        }

        // Try to connect to the penDevice
        if (penDevice != null && penDevice.connect())
            System.out.println("penDevice has been connected");
        else {
            System.out.println("Could not connect penDevice.");
        }
        
		characteristics = new HashMap<String, BluetoothGattCharacteristic>();
		
		String digitalGlassService = Config.getInstance().getProperty("ServiceID");
		
		BluetoothGattService service = getService(digitalGlassService);
		
		if(service != null) {
			for(BluetoothGattCharacteristic characteristic : getService(digitalGlassService).getCharacteristics()) {
				characteristics.put(characteristic.getUUID(), characteristic);
			}	
		}
		
		// Draw Notification 
		BluetoothGattCharacteristic drawCharacteristic = characteristics.get(Config.getInstance().getProperty("Draw_Characteristic"));
		if (drawCharacteristic != null) {
			drawCharacteristic.enableValueNotifications(new DrawNotification(instance));
		}
		// Resync Notification
		BluetoothGattCharacteristic rsCharacteristic = characteristics.get(Config.getInstance().getProperty("Resync_Characteristic"));
		if (rsCharacteristic != null) {
			rsCharacteristic.enableValueNotifications(new ResyncNotification(instance));
		}
		// User Functionality Notification
		BluetoothGattCharacteristic ufCharacteristic = characteristics.get(Config.getInstance().getProperty("User_Functionality_Characteristic"));
		if (ufCharacteristic != null) {
			ufCharacteristic.enableValueNotifications(new UserFunctionalityNotification(instance));
		}
    }
    
    public boolean isConnected() {
    	return penDevice.getConnected();
    }
}
