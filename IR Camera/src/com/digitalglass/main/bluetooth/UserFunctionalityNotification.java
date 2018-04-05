package com.digitalglass.main.bluetooth;
import tinyb.BluetoothNotification;
import com.digitalglass.main.core.Initializer;


public class UserFunctionalityNotification implements BluetoothNotification<byte[]> {

	private final static int COLOR_TOGGLE = 0;
	private final static int ERASE_TOGGLE = 1;
	private final static int CLEAR_BOARD = 2;
	
	Initializer stateMachine;
	
	public UserFunctionalityNotification(Initializer instance) {
		stateMachine = instance;
	}
	
    public void run(byte[] tempRaw) {
    	if (tempRaw[0] == COLOR_TOGGLE) {
    		stateMachine.toggleColor();
    	}
    	else if (tempRaw[0] == ERASE_TOGGLE) {
    		stateMachine.toggleErase();
    	}
    	else if (tempRaw[0] == CLEAR_BOARD) {
    		stateMachine.clearBoard();
    	}
    }
}