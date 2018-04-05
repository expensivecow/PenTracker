package com.digitalglass.main.bluetooth;

import com.digitalglass.main.core.Initializer;

import tinyb.BluetoothNotification;

public class DrawNotification implements BluetoothNotification<byte[]> {
	private final static int DRAW_OFF = 0;
	private final static int DRAW_ON = 1;
	
	Initializer stateMachine;
	
	public DrawNotification(Initializer instance) {
		stateMachine = instance;
	}
	
    public void run(byte[] tempRaw) {
    	if (tempRaw[0] == DRAW_OFF) {
    		stateMachine.stopDrawing();
    	}
    	else if (tempRaw[0] == DRAW_ON) {
    		stateMachine.startDrawing();
    	}
    }
}
