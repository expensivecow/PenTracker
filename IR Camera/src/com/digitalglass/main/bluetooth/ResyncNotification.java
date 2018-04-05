package com.digitalglass.main.bluetooth;

import tinyb.BluetoothNotification;

import com.digitalglass.main.core.Initializer;

public class ResyncNotification implements BluetoothNotification<byte[]> {
	private final static int RESYNC_REQUEST = 1;
	
	Initializer stateMachine;
	
	public ResyncNotification(Initializer instance) {
		stateMachine = instance;
	}
	
    public void run(byte[] tempRaw) {
    	if (tempRaw[0] == RESYNC_REQUEST) {
    		stateMachine.recallibrate();
    	}
    }
}
