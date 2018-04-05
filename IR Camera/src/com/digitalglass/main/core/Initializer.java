package com.digitalglass.main.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import org.opencv.core.Core;
import org.opencv.core.Scalar;

import com.digitalglass.main.bluetooth.BLEHelper;
import com.digitalglass.main.callibration.Callibration;
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
	private final static int ERASER_THICKNESS = 10;
	private final static int DRAW_THICKNESS = 7;
	
	private boolean isErasing = false;
	
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

		currentBoard = new Board(2, GREEN_COLOR, PASTEL_LIGHT_GREEN_COLOR, 7);
		
		//Callibration callibration = new Callibration(currentBoard.getCameras());
		//callibration.startCallibration();

		while(true) {
			if (ble.isConnected()) {
				currentBoard.updateImage();
				outputWindow.setImage(currentBoard.getResultingImage());
			}
			else {
				System.out.println("Trying to reconnect");
				ble.tryConnect();
			}
		}
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
		isErasing = (isErasing == true) ? false : true;
		
		if (isErasing == true) {
			currentBoard.updateThickness(ERASER_THICKNESS);
			currentBoard.updateCurrentColor(GREEN_COLOR);
		}
		else {
			currentBoard.updateThickness(DRAW_THICKNESS);
			currentBoard.updateCurrentColor(colorPalette.get(currentIndex));
		}
		System.out.println("TOGGLE ERASE");
	}
	
	public void toggleColor() {
		if(currentIndex == colorPalette.size()-1) {
			currentIndex = 0;
		}
		else {
			currentIndex += 1;	
		}
		Scalar colorToChange = colorPalette.get(currentIndex);
		currentBoard.updateCurrentColor(colorToChange);
		System.out.println("TOGGLE COLOR");
	}
	
	public void clearBoard() {
		currentBoard.fillFrames(GREEN_COLOR);
		System.out.println("CLEAR BOARD");
	}
}