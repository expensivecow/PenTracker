import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.bytedeco.javacpp.indexer.UByteRawIndexer;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

public class IRCameraInterface {
	// Camera Mats
	public static Mat cameraMat;
	public static Mat cameraMat2;
	
	// Vertically Flipped Mats
	public static Mat flipMat;
	public static Mat flipMat2;
	
	// Transformed Mats
	public static Mat warpedMat;
	public static Mat warpedMat2;

	public static Mat beforeMat;
	public static Mat combinedMat;
	
	public static int WIDTH;
	public static int HEIGHT;
	public static ImgWindow cameraWindow = ImgWindow.newWindow();
	public static ImgWindow camera2Window = ImgWindow.newWindow();

	public static WarpedFrameTransformer warpedTransformer;
	public static WarpedFrameTransformer warpedTransformer2;
	
	public IRCameraInterface(int width, int height) {
		WIDTH = width;
		HEIGHT = height;
	}

	
	public static void main(String[] args) throws InterruptedException, IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
        List<Point> callibList = new ArrayList<Point>();
        callibList.add(new Point(40.0, 30.0));
        callibList.add(new Point(508.0, 54.0));
        callibList.add(new Point(509.0, 688.0));
        callibList.add(new Point(34.0, 703.0));

        List<Point> callibList2 = new ArrayList<Point>();
        callibList2.add(new Point(40.0, 30.0));
        callibList2.add(new Point(508.0, 54.0));
        callibList2.add(new Point(509.0, 688.0));
        callibList2.add(new Point(34.0, 703.0));

        assert(callibList.size() == 4);
        assert(callibList2.size() == 4);
        
		IRCamera camera = new IRCamera("/dev/ttyACM1", 19200, callibList);
		IRCamera camera2 = new IRCamera("/dev/ttyACM0", 19200, callibList2);

		// Camera interface 1
		IRCameraInterface cameraInterface = new IRCameraInterface(767, 1023);  
        
		cameraInterface.start(Color.BLACK);
		
		while(true) {
			String camerastring = cleanUpString(camera.getCurrentFrame());
			String camera2string = cleanUpString(camera2.getCurrentFrame());

			String[] cameraValues = camerastring.split(",");
			String[] camera2Values = camera2string.split(",");
			
			if(Integer.parseInt(cameraValues[0]) != 1023 && Integer.parseInt(cameraValues[1]) != 1023) {
		        Point point = new Point(Double.parseDouble(cameraValues[1]), Double.parseDouble(cameraValues[0]));
		        Core.circle(cameraMat, point, 3, new Scalar(0, 0, 255), -1);
		        Core.flip(cameraMat, flipMat, 0);
		        
		        warpedMat = warpedTransformer.getProjection();
				System.out.println("Camera 1 " + camerastring);
			}
			
			if(Integer.parseInt(camera2Values[0]) != 1023 && Integer.parseInt(camera2Values[1]) != 1023) {
				Point point = new Point(Double.parseDouble(camera2Values[1]), Double.parseDouble(camera2Values[0]));
		        Core.circle(cameraMat2, point, 3, new Scalar(0, 255, 255), -1);
		        Core.flip(cameraMat2, flipMat2, 0);
		        
		        warpedMat2 = warpedTransformer2.getProjection();
				System.out.println("Camera 2 " + camera2string);
			}
			
			List<Mat> src = Arrays.asList(warpedMat2, warpedMat);
			List<Mat> src2 = Arrays.asList(flipMat2, flipMat);

			Core.hconcat(src, combinedMat);
			Core.hconcat(src2, beforeMat);
			
			cameraWindow.setImage(combinedMat);
			camera2Window.setImage(beforeMat);
			/*
			cameraWindow.setImage(warpedMat);
			camera2Window.setImage(warpedMat2);
			*/
		}
	}

	private static String cleanUpString(String toClean) {
		String result = toClean.replace("[", "");
		result = result.replace("]", "");
		result = result.replace(" ", "");
		
		return result; 
	}

	private void start(Color color){
	    cameraMat = new Mat(HEIGHT, WIDTH, CvType.CV_8UC3);
	    cameraMat2 = new Mat(HEIGHT, WIDTH, CvType.CV_8UC3);
	    
	    flipMat = new Mat(HEIGHT, WIDTH, CvType.CV_8UC3);
	    flipMat2 = new Mat(HEIGHT, WIDTH, CvType.CV_8UC3);
	    
	    warpedMat = new Mat(HEIGHT, WIDTH, CvType.CV_8UC3);
	    warpedMat2 = new Mat(HEIGHT, WIDTH, CvType.CV_8UC3);

	    beforeMat = new Mat(HEIGHT, WIDTH*2, CvType.CV_8UC3);
	    combinedMat = new Mat(HEIGHT, WIDTH*2, CvType.CV_8UC3);
	    
        List<Point> corners = new ArrayList<Point>();
        corners.add(new Point(125.0, 266.0));
		corners.add(new Point(705.0, 264.0));
		corners.add(new Point(698.0, 937.0));
		corners.add(new Point(125.0, 886.0));
        
        List<Point> corners2 = new ArrayList<Point>();
		corners2.add(new Point(136.0, 321.0));
		corners2.add(new Point(684.0, 330.0));
		corners2.add(new Point(687.0, 943.0));
		corners2.add(new Point(135.0, 967.0));
		
        warpedTransformer = new WarpedFrameTransformer(flipMat, corners);
        warpedTransformer2 = new WarpedFrameTransformer(flipMat2, corners2);
	}
}