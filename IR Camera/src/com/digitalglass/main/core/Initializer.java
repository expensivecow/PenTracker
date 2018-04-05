package com.digitalglass.main.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import org.opencv.core.Core;
import org.opencv.core.Scalar;

import com.digitalglass.main.bluetooth.BLEHelper;
import com.digitalglass.main.gui.ImgWindow;

public class Initializer {
	private final static Scalar GREEN_COLOR = new Scalar(0, 255, 0);
	private final static Scalar SKY_BLUE_COLOR = new Scalar(235, 206, 136);
	private final static Scalar DEEP_BLUE_COLOR = new Scalar(255, 0, 0);
	
	private final static Scalar PASTEL_LIGHT_GREEN_COLOR = new Scalar(189,255,173);
	private final static Scalar PASTEL_LIGHT_BLUE_COLOR = new Scalar(209,254,255);
	private final static Scalar PASTEL_NAVY_BLUE_COLOR = new Scalar(170,180,255);
	private final static Scalar PASTEL_BRIGHT_PINK_COLOR = new Scalar(209,254,255);
	private final static Scalar PASTEL_DARK_PINK_COLOR = new Scalar(255,178,236);
	
	private List<Scalar> colorPalette;
	
	int currentIndex;
	
	public Initializer() {
		colorPalette = new ArrayList<Scalar>();
		currentIndex = 0;

		colorPalette.add(PASTEL_LIGHT_GREEN_COLOR);
		colorPalette.add(PASTEL_LIGHT_BLUE_COLOR);
		colorPalette.add(PASTEL_NAVY_BLUE_COLOR);
		colorPalette.add(PASTEL_BRIGHT_PINK_COLOR);
		colorPalette.add(PASTEL_DARK_PINK_COLOR);
	}
	
	public static ImgWindow outputWindow;
	private static Board currentBoard;
	
	public static void main(String[] args) throws InterruptedException, IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		Initializer initializer = new Initializer();
		initializer.start();
		
        BLEHelper ble = new BLEHelper(initializer);
        
        while(ble.isConnected()) {
        	
        }
        
        System.out.println("DONE!");
        /*
		Initializer initializer = new Initializer();
		initializer.start();

		currentBoard = new Board(2, GREEN_COLOR, DEEP_BLUE_COLOR, 3);
		
		Callibration callibration = new Callibration(currentBoard.getCameras());
		callibration.startCallibration();
		
		while(true) {
			currentBoard.updateImage();
			outputWindow.setImage(currentBoard.getResultingImage());
		}
		*/
	}

	private void start() {
		outputWindow = ImgWindow.newWindow("Merged Image");
	}
	
	public void stopDrawing() {
		currentBoard.updateDrawFlag(false);
		System.out.println("STOP DRAWING");
	}
	
	public void startDrawing() {
		currentBoard.updateDrawFlag(true);
		System.out.println("START DRAWING");
	}
	
	public void recallibrate() {
		System.out.println("START RECALLIBRATING");
	}
	
	public void toggleErase() {
		currentBoard.updateCurrentColor(GREEN_COLOR);
		System.out.println("TOGGLE ERASE");
	}
	
	public void toggleColor() {
		System.out.println("TOGGLE COLOR");
	}
	
	public void clearBoard() {
		currentBoard.fillFrames(GREEN_COLOR);
		System.out.println("CLEAR BOARD");
	}
}